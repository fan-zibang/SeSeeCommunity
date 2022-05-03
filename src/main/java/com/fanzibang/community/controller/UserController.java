package com.fanzibang.community.controller;

import com.fanzibang.community.dto.UserLoginParam;
import com.fanzibang.community.pojo.User;
import com.fanzibang.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public String login(@RequestBody UserLoginParam userLoginParam) {
        return userService.login(userLoginParam);
    }

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }


}
