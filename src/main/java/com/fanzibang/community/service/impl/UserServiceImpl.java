package com.fanzibang.community.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fanzibang.community.constant.RedisKey;
import com.fanzibang.community.constant.ReturnCode;
import com.fanzibang.community.dto.LoginUser;
import com.fanzibang.community.dto.UserParam;
import com.fanzibang.community.exception.Asserts;
import com.fanzibang.community.mapper.UserMapper;
import com.fanzibang.community.pojo.User;
import com.fanzibang.community.service.RedisService;
import com.fanzibang.community.service.UserService;
import com.fanzibang.community.utils.JwtTokenUtil;
import com.fanzibang.community.utils.MailClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Override
    public String register(UserParam userParam) {
        String email = userParam.getEmail();
        String password = userParam.getPassword();
        if (StrUtil.isEmpty(email) || StrUtil.isEmpty(password)) {
            Asserts.fail(ReturnCode.RC206);
        }
        // 查询邮箱是否已注册
        User user = getOne(new LambdaQueryWrapper<>(User.class).eq(User::getEmail, email));
        if (!ObjectUtil.isNull(user)) {
            Asserts.fail(ReturnCode.RC208);
        }

        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPassword(bCryptPasswordEncoder.encode(password));
        newUser.setCreateTime(System.currentTimeMillis());

        boolean isSave = save(newUser);
        if (!isSave) {
            Asserts.fail("邮箱注册失败");
        }
        // 给注册用户发送激活邮件
        String code = RandomUtil.randomString(6);
        Long userId = newUser.getId();
        redisService.set(RedisKey.REGISTER_CODE_KEY + userId, code, 5);
        String content = "<p>激活码有限期为5分钟，请尽快使用！</p><p>激活码：" +
                "<span style='font-weight:bold;font-size: 22px;'>" + code + "</span>" + "</p>";
        mailClient.sendMail(newUser.getEmail(), "激活 Community 账号", content);
        return newUser.getId().toString();
    }

    @Override
    public String activation(Integer userId, String code) {
        // 是否重复激活
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(User::getId, User::getStatus).eq(User::getId, userId);
        User user = userMapper.selectOne(queryWrapper);

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
        return "激活成功";
    }

    @Override
    public String login(UserParam userParam) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userParam.getEmail(), userParam.getPassword());
        // 认证
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        if (ObjectUtil.isNull(authenticate)) {
            Asserts.fail(ReturnCode.RC205);
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
    public String logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        Long userId = loginUser.getUser().getId();
        Boolean isLogout = redisService.del(RedisKey.LOGIN_USER_KEY + userId);
        if (!isLogout) {
            Asserts.fail("退出失败");
        }
        return "退出成功";
    }
}
