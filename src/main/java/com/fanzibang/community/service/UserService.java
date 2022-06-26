package com.fanzibang.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fanzibang.community.dto.UserInfoParam;
import com.fanzibang.community.pojo.User;
import com.fanzibang.community.vo.UserVo;

public interface UserService extends IService<User> {

    Integer updateUserInfo(UserInfoParam userInfoParam);

    Integer updateUserPassword(String oldPwd, String newPwd);

    UserVo getCurrentUser();

}
