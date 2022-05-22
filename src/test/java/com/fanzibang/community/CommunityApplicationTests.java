package com.fanzibang.community;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.fanzibang.community.utils.JwtTokenUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class CommunityApplicationTests {

    @Test
    void contextLoads() {

    }

    @Test
    void test01() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        System.out.println(bCryptPasswordEncoder.encode("123"));
    }

    @Test
    void test02() {
        String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOjEsImNyZWF0ZWQiOjE2NTI4MDMwMDY3NTUsImV4cCI6MTY1Mjg4OTQwNn0.ue8eZUP7NINXCTLJDYqXUOib3EyccsE5a-rS27fDwxs8ZjQ-gQ9QdI8-q2AlgUSWMxMde4NasTDkLQ39sGlHFw";
        JwtTokenUtil jwtTokenUtil = new JwtTokenUtil();
        System.out.println(jwtTokenUtil.validateToken(token, "1"));
    }



}
