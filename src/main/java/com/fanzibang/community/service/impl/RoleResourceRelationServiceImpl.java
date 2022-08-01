package com.fanzibang.community.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fanzibang.community.constant.ReturnCode;
import com.fanzibang.community.exception.Asserts;
import com.fanzibang.community.mapper.RoleResourceRelationMapper;
import com.fanzibang.community.pojo.RoleResourceRelation;
import com.fanzibang.community.service.RoleResourceRelationService;
import com.fanzibang.community.vo.ResourceVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoleResourceRelationServiceImpl implements RoleResourceRelationService {

    @Autowired
    private RoleResourceRelationMapper roleResourceRelationMapper;

    @Override
    public List<ResourceVo> getRoleResource(Integer roleId) {
        return roleResourceRelationMapper.getRoleResource(roleId);
    }

    @Override
    public int allotResource(Integer roleId, List<Long> resourceIds) {
        int i = 0;
        // 先删除原先的关系
        LambdaQueryWrapper<RoleResourceRelation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RoleResourceRelation::getRoleId, roleId);
        roleResourceRelationMapper.delete(queryWrapper);
        // 建立新的关系
        if (CollectionUtil.isNotEmpty(resourceIds)) {
            List<RoleResourceRelation> roleResourceRelations = new ArrayList<>();
            for (Long resourceId : resourceIds) {
                RoleResourceRelation roleResourceRelation = new RoleResourceRelation();
                roleResourceRelation.setRoleId(roleId);
                roleResourceRelation.setResourceId(resourceId);
                roleResourceRelations.add(roleResourceRelation);
            }
            i = roleResourceRelationMapper.allotResource(roleResourceRelations);
            if (i <= 0) {
                Asserts.fail(ReturnCode.RC657);
            }
        }
        return i;
    }
}
