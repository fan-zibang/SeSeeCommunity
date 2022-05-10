package com.fanzibang.community.controller;

import com.fanzibang.community.dto.UserParam;
import com.fanzibang.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;


@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public String register(@RequestBody UserParam userParam) {
        return userService.register(userParam);
    }

    @GetMapping("/activation/{userId}/{code}")
    public String activation(@PathVariable("userId") Integer userId, @PathVariable("code") String code) {
        return userService.activation(userId, code);
    }

    @PostMapping("/login")
    public String login(@RequestBody UserParam userParam) {
        return userService.login(userParam);
    }

    @GetMapping("/logout")
    public String logout() {
        return userService.logout();
    }

    @PreAuthorize("hasAnyAuthority({'admin'})")
    @GetMapping("/hello")
    public List<String> hello() {
        List<String> userList = Arrays.asList("zhangsan","lisi");
        return userList;
    }

}
