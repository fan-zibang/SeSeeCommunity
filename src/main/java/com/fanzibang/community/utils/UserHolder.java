package com.fanzibang.community.utils;


import com.fanzibang.community.dto.LoginUser;
import com.fanzibang.community.pojo.User;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserHolder {

    public static User getUser() {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return loginUser.getUser();
    }

}
