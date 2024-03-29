package com.fanzibang.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fanzibang.community.dto.UserInfoParam;
import com.fanzibang.community.pojo.User;
import com.fanzibang.community.vo.RoleVo;
import com.fanzibang.community.vo.UserDetailVo;
import com.fanzibang.community.vo.UserVo;

import java.util.List;
import java.util.Map;

public interface UserService extends IService<User> {

    Integer updateUserInfo(UserInfoParam userInfoParam);

    Integer updateUserPassword(String oldPwd, String newPwd);

    UserVo getCurrentUserDetails();

    List<Long> getAllUserIds();

    UserDetailVo getUserDetails(Long userId);

    List<RoleVo> getUserRole(Long userId);

    Map<String, Object> getUserPageList(Integer current, Integer size);

}
