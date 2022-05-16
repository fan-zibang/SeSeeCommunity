package com.fanzibang.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fanzibang.community.constant.ReturnCode;
import com.fanzibang.community.constant.Role;
import com.fanzibang.community.dto.LoginUser;
import com.fanzibang.community.exception.Asserts;
import com.fanzibang.community.mapper.UserMapper;
import com.fanzibang.community.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(User::getEmail, email);
        User user = userMapper.selectOne(queryWrapper);
        if (Objects.isNull(user)) {
             Asserts.fail(ReturnCode.RC202);
        }
        List<String> list = new ArrayList<>();
        String role;
        switch (user.getRole()) {
            case 0:
                role = Role.AUTHORITY_ADMIN; break;
            case 1:
                role = Role.AUTHORITY_MODERATOR; break;
            default:
                role = Role.AUTHORITY_USER; break;
        }
        list.add(role);
        //封装成UserDetails对象返回
        return new LoginUser(user, list);
    }
}
