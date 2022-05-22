package com.fanzibang.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fanzibang.community.dto.UserInfoParam;
import com.fanzibang.community.dto.UserParam;
import com.fanzibang.community.pojo.User;

public interface UserService extends IService<User> {

    int updateUserInfo(Long id, UserInfoParam userInfoParam);

}
