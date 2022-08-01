package com.fanzibang.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fanzibang.community.pojo.UserRoleRelation;
import com.fanzibang.community.vo.RoleVo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRelationMapper extends BaseMapper<UserRoleRelation> {

    List<RoleVo> getUserRole(Long userId);

    int allotRole(List<UserRoleRelation> userRoleRelationList);
}
