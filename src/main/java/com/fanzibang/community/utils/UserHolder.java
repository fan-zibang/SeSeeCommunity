package com.fanzibang.community.utils;

import com.fanzibang.community.constant.RedisKey;
import com.fanzibang.community.dto.LoginUser;
import com.fanzibang.community.pojo.User;
import com.fanzibang.community.service.RedisService;
import com.fanzibang.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserHolder {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisService redisService;

    public User getUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal.equals("anonymousUser")) {
            return null;
        }
        LoginUser loginUser = (LoginUser) principal;
        return loginUser.getUser();
    }

    public void refreshUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LoginUser loginUser = (LoginUser) principal;
        // 去数据库重新查询，更新用户信息
        Long userId = loginUser.getUser().getId();
        User user = userService.getById(userId);
        loginUser.setUser(user);
        redisService.set(RedisKey.LOGIN_USER_KEY + userId, loginUser, 1440);
    }

}
