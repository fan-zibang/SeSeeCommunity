package com.fanzibang.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fanzibang.community.dto.UserParam;
import com.fanzibang.community.pojo.User;

public interface UserService extends IService<User> {
    String login(UserParam userParam);

    Long logout();

    String register(UserParam userParam);

    String activation(Integer userId, String code);
}
