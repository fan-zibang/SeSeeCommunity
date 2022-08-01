package com.fanzibang.community.service;

import com.fanzibang.community.vo.ResourceVo;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RoleResourceRelationService {
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    int allotResource(Integer roleId, List<Long> resourceIds);

    List<ResourceVo> getRoleResource(Integer roleId);
}
