package com.fanzibang.community.service;

import org.springframework.security.access.ConfigAttribute;

import java.util.Map;

public interface DynamicAuthenticationService {

    Map<String, ConfigAttribute> loadDataSource();

}
