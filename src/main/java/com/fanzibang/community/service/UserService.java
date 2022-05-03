package com.fanzibang.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fanzibang.community.dto.UserLoginParam;
import com.fanzibang.community.pojo.User;

public interface UserService extends IService<User> {
    String login(UserLoginParam userLoginParam);

    String logout();
}
