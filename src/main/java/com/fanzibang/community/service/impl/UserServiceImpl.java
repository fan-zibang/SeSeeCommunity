package com.fanzibang.community.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
import com.fanzibang.community.service.RedisService;
import com.fanzibang.community.service.UserService;
import com.fanzibang.community.utils.JwtTokenUtil;
import com.fanzibang.community.utils.MailClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

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

    @Override
    public String register(UserParam userParam) {
        String email = userParam.getEmail();
        String password = userParam.getPassword();

        // 账号已注册已激活
        User user = getOne(new LambdaQueryWrapper<>(User.class).eq(User::getEmail, email));
        if (!ObjectUtil.isNull(user) && user.getStatus() == 1) {
            Asserts.fail(ReturnCode.RC208);
        }
        // 账号已注册未激活
        if (!ObjectUtil.isNull(user) && user.getStatus() == 0) {
            sendEmailCodeToUser(user.getId(), email);
            return user.getId().toString();
        }

        // 账号未注册
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPassword(bCryptPasswordEncoder.encode(password));
        newUser.setCreateTime(System.currentTimeMillis());

        boolean isSave = save(newUser);
        if (!isSave) {
            Asserts.fail("账号注册失败");
        }
        // 给注册用户发送激活邮件
        Long newUserId = newUser.getId();
        sendEmailCodeToUser(newUserId, email);
        return newUserId.toString();
    }

    public void sendEmailCodeToUser(Long userId, String email) {
        String code = RandomUtil.randomString(6);
        redisService.set(RedisKey.REGISTER_CODE_KEY + userId, code, 5);
        String content = "<p>激活码有限期为5分钟，请尽快使用！</p><p>激活码：" +
                "<span style='font-weight:bold;font-size: 22px;'>" + code + "</span>" + "</p>";
        mailClient.sendMail(email, "激活 Community 账号", content);
    }

    @Override
    public String activation(Integer userId, String code) {
        // 是否重复激活
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(User::getId, User::getStatus).eq(User::getId, userId);
        User user = userMapper.selectOne(queryWrapper);
        if (ObjectUtil.isNull(user)) {
            Asserts.fail("激活失败");
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
        user.setStatus(1);
        userMapper.updateById(user);
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
        return "激活成功";
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
        Integer status = loginUser.getUser().getStatus();
        if (status == 0 || status == null) {
            Asserts.fail(ReturnCode.RC203);
        }
        Long userId = loginUser.getUser().getId();
        String token = jwtTokenUtil.generateToken(userId);
        redisService.set(RedisKey.LOGIN_USER_KEY + userId, loginUser);
        return token;
    }

    @Override
    public Long logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        Long userId = loginUser.getUser().getId();
        Boolean isLogout = redisService.del(RedisKey.LOGIN_USER_KEY + userId);
        if (!isLogout) {
            Asserts.fail("退出失败");
        }
        return userId;
    }
}
