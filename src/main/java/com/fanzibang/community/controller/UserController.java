package com.fanzibang.community.controller;

import com.fanzibang.community.dto.UserInfoParam;
import com.fanzibang.community.service.UserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.Min;


@RestController
@RequestMapping("/user")
@Validated
public class UserController {

    @Resource(name = "userService")
    private UserService userService;

    @PutMapping("/{id}")
    public int updateUserInfo(@Valid @PathVariable("id") @Min(1) Long id, @RequestBody UserInfoParam userInfoParam) {
        return userService.updateUserInfo(id, userInfoParam);
    }

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

}
