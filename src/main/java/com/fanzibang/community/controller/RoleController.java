package com.fanzibang.community.controller;

import com.fanzibang.community.pojo.Role;
import com.fanzibang.community.service.RoleResourceRelationService;
import com.fanzibang.community.service.RoleService;
import com.fanzibang.community.vo.ResourceVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private RoleResourceRelationService roleResourceRelationService;

    @GetMapping
    public Map<String, Object> getRolePageList(Integer current, Integer size) {
        return roleService.getRolePageList(current, size);
    }

    @GetMapping("/{roleId}")
    public Role getRoleById(@PathVariable Integer roleId) {
        return roleService.getRoleById(roleId);
    }

    @PostMapping
    public int addRole(@RequestBody @Valid Role role) {
        return roleService.addRole(role);
    }

    @PostMapping("/{roleId}")
    public int editRole(@PathVariable("roleId") Integer roleId, @RequestBody @Valid Role role) {
        return roleService.editRole(roleId, role);
    }

    @DeleteMapping("/delete/{roleId}")
    public int deleteRole(@PathVariable("roleId") Integer roleId) {
        return roleService.deleteRole(roleId);
    }

    @PutMapping("/{roleId}")
    public int updateRoleStatus(@PathVariable("roleId") Integer roleId, Integer status) {
        return roleService.updateRoleStatus(roleId, status);
    }

    @GetMapping("/resource")
    public List<ResourceVo> getRoleResource(Integer roleId) {
        return roleResourceRelationService.getRoleResource(roleId);
    }

    @PostMapping("/allocation/resource")
    public int allotResource(@RequestParam("roleId") Integer roleId, @RequestParam("resourceIds") List<Long> resourceIds) {
        return roleResourceRelationService.allotResource(roleId, resourceIds);
    }

}
