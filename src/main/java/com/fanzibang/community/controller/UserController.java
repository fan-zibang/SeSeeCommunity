package com.fanzibang.community.controller;

import com.fanzibang.community.dto.UserInfoParam;
import com.fanzibang.community.service.UserRoleRelationService;
import com.fanzibang.community.service.UserService;
import com.fanzibang.community.vo.RoleVo;
import com.fanzibang.community.vo.UserDetailVo;
import com.fanzibang.community.vo.UserVo;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;


@RestController
@RequestMapping("/user")
@Validated
public class UserController {

    @Resource(name = "userService")
    private UserService userService;

    @Autowired
    private UserRoleRelationService userRoleRelationService;

    @GetMapping
    public UserVo getCurrentUserDetails() {
        return userService.getCurrentUserDetails();
    }

    @GetMapping("/{userId}")
    public UserDetailVo getUserDetails(@PathVariable("userId") Long userId) {
        return userService.getUserDetails(userId);
    }

    @GetMapping("/role")
    public List<RoleVo> getUserRole(@RequestParam("userId") Long userId) {
        return userService.getUserRole(userId);
    }

    @PostMapping("/role")
    public int allotRole (@RequestParam("userId") Long userId, @RequestParam("roleIds") List<Integer> roleIds) {
        return userRoleRelationService.allotRole(userId, roleIds);
    }

    @PutMapping
    public int updateUserInfo(@RequestBody @Valid UserInfoParam userInfoParam) {
        return userService.updateUserInfo(userInfoParam);
    }

    @PatchMapping
    public int updateUserPassword(@Valid @NotEmpty @Length(max = 125) String oldPwd,
                                      @Valid @NotEmpty @Length(max = 125) String newPwd) {
        return userService.updateUserPassword(oldPwd, newPwd);
    }


}
