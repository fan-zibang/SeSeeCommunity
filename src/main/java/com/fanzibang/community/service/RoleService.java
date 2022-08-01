package com.fanzibang.community.service;

import com.fanzibang.community.pojo.Role;
import java.util.Map;

public interface RoleService {

    int addRole(Role role);

    int editRole(Integer roleId, Role role);

    int deleteRole(Integer roleId);

    Map<String, Object> getRolePageList(Integer current, Integer size);

    int updateRoleStatus(Integer roleId, Integer status);

    Role getRoleById(Integer roleId);
}
