package com.fanzibang.community.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fanzibang.community.constant.ReturnCode;
import com.fanzibang.community.exception.Asserts;
import com.fanzibang.community.mapper.UserRoleRelationMapper;
import com.fanzibang.community.pojo.UserRoleRelation;
import com.fanzibang.community.service.UserRoleRelationService;
import com.fanzibang.community.vo.RoleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserRoleRelationServiceImpl implements UserRoleRelationService {

    @Autowired
    private UserRoleRelationMapper userRoleRelationMapper;

    @Override
    public List<RoleVo> getUserRole(Long userId) {
        return userRoleRelationMapper.getUserRole(userId);
    }

    @Override
    public int allotRole(Long userId, List<Integer> roleIds) {
        int i = 0;
        // 先删除之前的关系
        LambdaQueryWrapper<UserRoleRelation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserRoleRelation::getUserId, userId);
        userRoleRelationMapper.delete(queryWrapper);
        // 建立新的关系
        if (CollectionUtil.isNotEmpty(roleIds)) {
            List<UserRoleRelation> userRoleRelationList = new ArrayList<>();
            for (Integer roleId : roleIds) {
                UserRoleRelation userRoleRelation = new UserRoleRelation();
                userRoleRelation.setUserId(userId);
                userRoleRelation.setRoleId(roleId);
                userRoleRelationList.add(userRoleRelation);
            }
            i = userRoleRelationMapper.allotRole(userRoleRelationList);
        }
        if (i <= 0) {
            Asserts.fail(ReturnCode.RC656);
        }
        return i;
    }
}
