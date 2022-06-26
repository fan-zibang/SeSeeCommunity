package com.fanzibang.community.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fanzibang.community.constant.ReturnCode;
import com.fanzibang.community.dto.UserInfoParam;
import com.fanzibang.community.exception.Asserts;
import com.fanzibang.community.mapper.UserMapper;
import com.fanzibang.community.pojo.User;
import com.fanzibang.community.service.LoginService;
import com.fanzibang.community.service.UserService;
import com.fanzibang.community.utils.UserHolder;
import com.fanzibang.community.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private LoginService loginService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserHolder userHolder;

    @Override
    public Integer updateUserInfo(UserInfoParam userInfoParam) {
        User user = userHolder.getUser();
        BeanUtil.copyProperties(userInfoParam,user);
        int i = userMapper.updateById(user);
        if (i <= 0) {
            Asserts.fail(ReturnCode.RC216);
        }
        // 更新本地用户信息
        userHolder.refreshUser();
        return i;
    }

    @Override
    public Integer updateUserPassword(String oldPwd, String newPwd) {
        User user = userHolder.getUser();
        // 判断旧密码是否正确
        if (!bCryptPasswordEncoder.matches(oldPwd, user.getPassword())) {
            Asserts.fail(ReturnCode.RC212);
        }
        newPwd = bCryptPasswordEncoder.encode(newPwd);
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(User::getPassword, newPwd).eq(User::getId, user.getId());
        int i = userMapper.update(null, updateWrapper);
        if (i <= 0) {
            Asserts.fail(ReturnCode.RC213);
        }
        // 密码更改成功后退出登录
        loginService.logout();
        return i;
    }

    @Override
    public UserVo getCurrentUser() {
        User user = userHolder.getUser();
        UserVo userVo = new UserVo();
        BeanUtil.copyProperties(user, userVo);
        String createTime = DateUtil.date(user.getCreateTime()).toString("yyyy-MM-dd HH:mm");
        userVo.setCreateTime(createTime);
        String lastLogin = DateUtil.date(user.getLastLogin()).toString("yyyy-MM-dd HH:mm");
        userVo.setLastLogin(lastLogin);
        return userVo;
    }
}
