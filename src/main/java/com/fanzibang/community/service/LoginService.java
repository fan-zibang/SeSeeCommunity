package com.fanzibang.community.service;

import com.fanzibang.community.dto.UserParam;

public interface LoginService {

    String login(UserParam userParam);

    Long logout();

    String register(UserParam userParam);

    String activation(Long userId, String code);

}
