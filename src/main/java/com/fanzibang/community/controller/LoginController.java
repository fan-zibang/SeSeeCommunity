package com.fanzibang.community.controller;

import com.fanzibang.community.dto.UserParam;
import com.fanzibang.community.service.LoginService;
import com.fanzibang.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;


@RestController
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/register")
    public String register(@RequestBody @Valid UserParam userParam) {
        return loginService.register(userParam);
    }

    @GetMapping("/activation/{userId}/{code}")
    public String activation(@Valid @PathVariable("userId") @Min(1) Long userId,
                             @NotEmpty @PathVariable("code") String code) {
        return loginService.activation(userId, code);
    }

    @PostMapping("/login")
    public String login(@RequestBody @Valid UserParam userParam) {
        return loginService.login(userParam);
    }

    @GetMapping("/logout")
    public Long logout() {
        return loginService.logout();
    }

}
