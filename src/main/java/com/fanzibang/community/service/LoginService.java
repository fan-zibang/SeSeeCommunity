package com.fanzibang.community.service;

import com.fanzibang.community.dto.UserParam;

public interface LoginService {

    String login(UserParam userParam);

    void logout();

    Long register(UserParam userParam);

    void activation(Long userId, String code);

}
