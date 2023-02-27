package com.fanzibang.community.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fanzibang.community.constant.MessageConstant;
import com.fanzibang.community.constant.RabbitMqEnum;
import com.fanzibang.community.constant.RedisKey;
import com.fanzibang.community.constant.ReturnCode;
import com.fanzibang.community.dto.LoginUser;
import com.fanzibang.community.dto.UserParam;
import com.fanzibang.community.exception.Asserts;
import com.fanzibang.community.mapper.UserMapper;
import com.fanzibang.community.mq.MessageProducer;
import com.fanzibang.community.pojo.Event;
import com.fanzibang.community.pojo.User;
import com.fanzibang.community.service.LoginService;
import com.fanzibang.community.service.RedisService;
import com.fanzibang.community.utils.JwtTokenUtil;
import com.fanzibang.community.utils.MailClient;
import com.fanzibang.community.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisService redisService;

    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MessageProducer messageProducer;

    @Autowired
    private UserHolder userHolder;

    @Override
    public Long register(UserParam userParam) {
        String email = userParam.getEmail();
        String password = userParam.getPassword();

        // 账号已注册已激活
        User user = userMapper.selectOne(new LambdaQueryWrapper<>(User.class).eq(User::getEmail, email));
        if (ObjectUtil.isNotNull(user) && user.getStatus() == 1) {
            Asserts.fail(ReturnCode.RC208);
        }
        // 账号已注册未激活
        if (ObjectUtil.isNotNull(user) && user.getStatus() == 0) {
            sendEmailCodeToUser(user.getId(), email);
            return user.getId();
        }

        // 账号未注册
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPassword(bCryptPasswordEncoder.encode(password));
        newUser.setNickname(RandomUtil.randomString(10));
        newUser.setStatus((byte) 0);
        newUser.setDelFlag((byte) 0);
        newUser.setCreateTime(System.currentTimeMillis());

        int i = userMapper.insert(newUser);
        if (i <= 0) {
            Asserts.fail("账号注册失败");
        }
        // 给注册用户发送激活邮件
        long newUserId = newUser.getId();
        sendEmailCodeToUser(newUserId, email);
        return newUserId;
    }

    public void sendEmailCodeToUser(Long userId, String email) {
        String code = RandomUtil.randomString(6);
        redisService.set(RedisKey.REGISTER_CODE_KEY + userId, code, 5);
        String content = "<p>激活码有限期为5分钟，请尽快使用！</p><p>激活码：" +
                "<span style='font-weight:bold;font-size: 22px;'>" + code + "</span>" + "</p>";
        mailClient.sendMail(email, "激活 Community 账号", content);
    }

    @Override
    public void activation(Long userId, String code) {
        // 是否重复激活
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(User::getId, User::getStatus).eq(User::getId, userId);
        User user = userMapper.selectOne(queryWrapper);
        if (ObjectUtil.isNull(user)) {
            Asserts.fail(ReturnCode.RC211);
        }
        if (user.getStatus() == 1) {
            Asserts.fail(ReturnCode.RC207);
        }
        String activation_code = (String) redisService.get(RedisKey.REGISTER_CODE_KEY + userId);
        if (StrUtil.isEmpty(activation_code)) {
            Asserts.fail(ReturnCode.RC209);
        }
        if (!code.equals(activation_code)) {
            Asserts.fail(ReturnCode.RC210);
        }
        user.setStatus((byte) 1);
        int i = userMapper.updateById(user);
        if (i <= 0) {
           Asserts.fail(ReturnCode.RC215);
        }
        redisService.del(RedisKey.REGISTER_CODE_KEY + userId);
        // 发送系统通知
        Event event = new Event()
                .setExchange(RabbitMqEnum.B_SYSTEM_MESSAGE_BROKER.getExchange())
                .setRoutingKey(RabbitMqEnum.B_SYSTEM_MESSAGE_BROKER.getRoutingKey())
                .setType(MessageConstant.TOPIC_SYSTEM)
                .setFromId(MessageConstant.SYSTEM_USER_ID)
                .setToId(user.getId())
                .setData("content","欢迎您加入 Community 社区！");
        messageProducer.sendMessage(event);
    }

    @Override
    public String login(UserParam userParam) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userParam.getEmail(), userParam.getPassword());
        // 认证
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        if (ObjectUtil.isNull(authenticate)) {
            Asserts.fail(ReturnCode.RC202);
        }
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        if (!loginUser.isEnabled()) {
            Asserts.fail(ReturnCode.RC203);
        }
        long userId = loginUser.getUser().getId();
        String token = jwtTokenUtil.generateToken(userId);
        redisService.set(RedisKey.LOGIN_USER_KEY + userId, loginUser, 1440);
        return token;
    }

    @Override
    public void logout() {
        User user = userHolder.getUser();
        if (ObjectUtil.isNull(user)) {
            Asserts.fail(ReturnCode.RC205);
        }
        Boolean isLogout = redisService.del(RedisKey.LOGIN_USER_KEY + user.getId());
        if (!isLogout) {
            Asserts.fail(ReturnCode.RC214);
        }
        SecurityContextHolder.clearContext();
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(User::getLastLogin,System.currentTimeMillis()).eq(User::getId,user.getId());
        userMapper.update(null,updateWrapper);
    }

}
