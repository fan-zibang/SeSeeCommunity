package com.fanzibang.community.service.impl;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fanzibang.community.dto.UserInfoParam;
import com.fanzibang.community.exception.Asserts;
import com.fanzibang.community.mapper.UserMapper;
import com.fanzibang.community.pojo.User;
import com.fanzibang.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public int updateUserInfo(Long id, UserInfoParam userInfoParam) {
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getId, id);
        User user = new User();
        BeanUtil.copyProperties(userInfoParam,user);
        int i = userMapper.update(user, updateWrapper);
        if (i <= 0) {
            Asserts.fail("用户信息更新失败");
        }
        return i;
    }
}
