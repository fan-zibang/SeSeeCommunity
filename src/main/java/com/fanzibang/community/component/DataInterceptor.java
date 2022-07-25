package com.fanzibang.community.component;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.fanzibang.community.pojo.User;
import com.fanzibang.community.service.DataService;
import com.fanzibang.community.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DataInterceptor implements HandlerInterceptor {

    @Autowired
    private UserHolder userHolder;

    @Autowired
    private DataService dataService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 统计UV
        String ip = request.getRemoteHost();
        if (StrUtil.isNotEmpty(ip)) {
            dataService.setUV(ip);
        }

        // 统计DAU
        User user = userHolder.getUser();
        if (ObjectUtil.isNotNull(user)) {
            dataService.setDAU(user.getId());
        }
        return true;
    }

}
