package com.fanzibang.community.component;

import cn.hutool.core.util.ObjectUtil;
import com.fanzibang.community.constant.RedisKey;
import com.fanzibang.community.dto.LoginUser;
import com.fanzibang.community.service.RedisService;
import com.fanzibang.community.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT登录授权过滤器
 */
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private RedisService redisService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(this.tokenHeader);
        if (header != null && header.startsWith(this.tokenHead)) {
            String token = header.substring(this.tokenHead.length());
            String userId = jwtTokenUtil.getUserIdFromToken(token);
            LoginUser loginUser = (LoginUser) redisService.get(RedisKey.LOGIN_USER_KEY + userId);
            if (!ObjectUtil.isNull(loginUser) && jwtTokenUtil.validateToken(token, userId)) {
                // 存入SecurityContextHolder
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(loginUser,null,loginUser.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        filterChain.doFilter(request,response);
    }

}
