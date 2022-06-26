package com.fanzibang.community.controller;

import com.fanzibang.community.dto.UserParam;
import com.fanzibang.community.service.LoginService;
import com.fanzibang.community.utils.CommonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/register")
    public Long register(@RequestBody @Valid UserParam userParam) {
        return loginService.register(userParam);
    }

    @GetMapping("/activation/{userId}/{code}")
    public CommonResult activation(@PathVariable("userId") Long userId,
                             @PathVariable("code") String code) {
        loginService.activation(userId, code);
        return CommonResult.success(null);
    }

    @PostMapping("/login")
    public String login(@RequestBody @Valid UserParam userParam) {
        return loginService.login(userParam);
    }

    @GetMapping("/logout")
    public CommonResult logout() {
        loginService.logout();
        return CommonResult.success(null);
    }

}
