package com.fanzibang.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fanzibang.community.constant.ReturnCode;
import com.fanzibang.community.exception.Asserts;
import com.fanzibang.community.mapper.RoleMapper;
import com.fanzibang.community.pojo.Role;
import com.fanzibang.community.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public Map<String, Object> getRolePageList(Integer current, Integer size) {
        current = Optional.ofNullable(current).orElse(1);
        size = Optional.ofNullable(size).orElse(20);
        Page<Role> page = new Page<>(current, size);
        Page<Role> rolePage = roleMapper.selectPage(page, null);
        Map<String, Object> map = new HashMap<>();
        map.put("roleList", rolePage.getRecords());
        map.put("total", rolePage.getTotal());
        return map;
    }

    @Override
    public Role getRoleById(Integer roleId) {
        return roleMapper.selectById(roleId);
    }

    @Override
    public int addRole(Role role) {
        role.setCreateTime(System.currentTimeMillis());
        role.setStatus((byte) 1);
        int i = roleMapper.insert(role);
        if (i <= 0) {
            Asserts.fail(ReturnCode.RC653);
        }
        return i;
    }

    @Override
    public int editRole(Integer roleId, Role role) {
        role.setId(roleId);
        int i = roleMapper.updateById(role);
        if (i <= 0) {
            Asserts.fail(ReturnCode.RC654);
        }
        return i;
    }

    @Override
    public int deleteRole(Integer roleId) {
        int i = roleMapper.deleteById(roleId);
        if (i <= 0) {
            Asserts.fail(ReturnCode.RC655);
        }
        return i;
    }

    @Override
    public int updateRoleStatus(Integer roleId, Integer status) {
        if (status < 0 || status >= 2) {
            Asserts.fail(ReturnCode.RC654);
        }
        LambdaUpdateWrapper<Role> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Role::getStatus, status).eq(Role::getId, roleId);
        int i = roleMapper.update(null, updateWrapper);
        if (i <= 0) {
            Asserts.fail(ReturnCode.RC654);
        }
        return i;
    }

}
