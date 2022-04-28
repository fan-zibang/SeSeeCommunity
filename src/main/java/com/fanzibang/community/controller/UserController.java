package com.fanzibang.community.controller;

import com.fanzibang.community.common.ReturnCode;
import com.fanzibang.community.exception.Asserts;
import com.fanzibang.community.pojo.User;
import com.fanzibang.community.service.RedisService;
import com.fanzibang.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisService redisService;

    @GetMapping
    public List<User> getUserList() {
        return userService.list();
    }

    @GetMapping("/hello")
    public String getStr(){
        return "hello,javadaily";
    }

    @GetMapping("/wrong")
    public int error(){
        int i = 9/0;
        return i;
    }

    @GetMapping("error1")
    public void empty(){
        throw new RuntimeException("自定义异常");
    }

    @GetMapping("error2")
    public void empty2(){
        Asserts.fail("密码错误");
    }

    @GetMapping("error3")
    public void empty3(){
        Asserts.fail(ReturnCode.RC204);
    }

    @GetMapping("/phone/{phone}")
    public String phone(@PathVariable("phone") Long phone) {
        String redisKey = "phone:" + phone;
        redisService.set(redisKey, 3);
        return redisKey;
    }
}
