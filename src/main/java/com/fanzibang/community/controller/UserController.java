package com.fanzibang.community.controller;

import com.fanzibang.community.dto.UserInfoParam;
import com.fanzibang.community.service.UserService;
import com.fanzibang.community.vo.UserDetailVo;
import com.fanzibang.community.vo.UserVo;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;


@RestController
@RequestMapping("/user")
@Validated
public class UserController {

    @Resource(name = "userService")
    private UserService userService;

    @GetMapping
    public UserVo getCurrentUserDetails() {
        return userService.getCurrentUserDetails();
    }

    @GetMapping("/{userId}")
    public UserDetailVo getUserDetails(@PathVariable("userId") Long userId) {
        return userService.getUserDetails(userId);
    }

    @PutMapping
    public Integer updateUserInfo(@RequestBody @Valid UserInfoParam userInfoParam) {
        return userService.updateUserInfo(userInfoParam);
    }

    @PatchMapping
    public Integer updateUserPassword(@Valid @NotEmpty @Length(max = 125) String oldPwd,
                                      @Valid @NotEmpty @Length(max = 125) String newPwd) {
        return userService.updateUserPassword(oldPwd, newPwd);
    }

}
