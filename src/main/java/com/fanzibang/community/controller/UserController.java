package com.fanzibang.community.controller;

import com.fanzibang.community.dto.UserLoginParam;
import com.fanzibang.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @GetMapping("/logout")
    public String logout() {
        return userService.logout();
    }

    @GetMapping("/hello")
    @PreAuthorize("hasAnyAuthority({'user'})")
    public String hello() {
        return "hello";
    }


}
