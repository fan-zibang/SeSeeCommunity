package com.fanzibang.community.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fanzibang.community.constant.ReturnCode;
import com.fanzibang.community.dto.UserInfoParam;
import com.fanzibang.community.exception.Asserts;
import com.fanzibang.community.mapper.UserMapper;
import com.fanzibang.community.pojo.User;
import com.fanzibang.community.service.*;
import com.fanzibang.community.utils.UserHolder;
import com.fanzibang.community.vo.RoleVo;
import com.fanzibang.community.vo.UserDetailVo;
import com.fanzibang.community.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private LoginService loginService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    @Autowired
    private UserRoleRelationService userRoleRelationService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserHolder userHolder;

    @Override
    public Integer updateUserInfo(UserInfoParam userInfoParam) {
        User user = userHolder.getUser();
        if (!user.getNickname().equals(userInfoParam.getNickname())) {
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getNickname, userInfoParam.getNickname());
            List<User> userList = userMapper.selectList(queryWrapper);
            if (userList.size() > 0) {
                Asserts.fail(ReturnCode.RC217);
            }
        }
        BeanUtil.copyProperties(userInfoParam,user);
        int i = userMapper.updateById(user);
        if (i <= 0) {
            Asserts.fail(ReturnCode.RC216);
        }
        // 更新本地用户信息
        userHolder.refreshUser();
        return i;
    }

    @Override
    public Integer updateUserPassword(String oldPwd, String newPwd) {
        User user = userHolder.getUser();
        // 判断旧密码是否正确
        if (!bCryptPasswordEncoder.matches(oldPwd, user.getPassword())) {
            Asserts.fail(ReturnCode.RC212);
        }
        newPwd = bCryptPasswordEncoder.encode(newPwd);
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(User::getPassword, newPwd).eq(User::getId, user.getId());
        int i = userMapper.update(null, updateWrapper);
        if (i <= 0) {
            Asserts.fail(ReturnCode.RC213);
        }
        // 密码更改成功后退出登录
        loginService.logout();
        return i;
    }

    @Override
    public UserVo getCurrentUserDetails() {
        User user = userHolder.getUser();
        UserVo userVo = new UserVo();
        BeanUtil.copyProperties(user, userVo);
        String createTime = DateUtil.date(user.getCreateTime()).toString("yyyy-MM-dd HH:mm");
        userVo.setCreateTime(createTime);
        String lastLogin = DateUtil.date(user.getLastLogin()).toString("yyyy-MM-dd HH:mm");
        userVo.setLastLogin(lastLogin);
        return userVo;
    }

    @Override
    public UserDetailVo getUserDetails(Long userId) {
        User user = userMapper.selectById(userId);
        if (ObjectUtil.isNull(user)) {
            Asserts.fail(ReturnCode.RC211);
        }
        UserDetailVo userDetailVo = new UserDetailVo();
        BeanUtil.copyProperties(user, userDetailVo);
        userDetailVo.setUserLikeCount(likeService.getUserLikeCount(userId));
        userDetailVo.setFansCount(followService.getFansCount(userId));
        userDetailVo.setFollowerCount(followService.getFollowerCount(userId));
        String createTime = DateUtil.date(user.getCreateTime()).toString("yyyy-MM-dd");
        userDetailVo.setCreateTime(createTime);
        return userDetailVo;
    }

    @Override
    public List<Long> getAllUserIds() {
        return userMapper.getAllUserIds();
    }

    /**
     * 获取用户的角色
     * @param userId
     * @return
     */
    @Override
    public List<RoleVo> getUserRole(Long userId) {
        return userRoleRelationService.getUserRole(userId);
    }

}
