package com.fanzibang.community.service;

import com.fanzibang.community.vo.RoleVo;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserRoleRelationService {
    List<RoleVo> getUserRole(Long userId);

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    int allotRole(Long userId, List<Integer> roleIds);
}
