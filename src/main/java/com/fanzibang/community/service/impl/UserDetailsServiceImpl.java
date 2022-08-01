package com.fanzibang.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fanzibang.community.constant.ReturnCode;
import com.fanzibang.community.dto.LoginUser;
import com.fanzibang.community.exception.Asserts;
import com.fanzibang.community.mapper.ResourceMapper;
import com.fanzibang.community.mapper.UserMapper;
import com.fanzibang.community.pojo.Resource;
import com.fanzibang.community.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ResourceMapper resourceMapper;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(User::getEmail, email);
        User user = userMapper.selectOne(queryWrapper);
        if (Objects.isNull(user)) {
             Asserts.fail(ReturnCode.RC202);
        }
        List<Resource> resourceList = resourceMapper.getResourceList(user.getId());
        //封装成UserDetails对象返回
        return new LoginUser(user, resourceList);
    }
}
