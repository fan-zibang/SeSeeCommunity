package com.fanzibang.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fanzibang.community.mapper.UserMapper;
import com.fanzibang.community.pojo.User;
import com.fanzibang.community.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
