package com.fanzibang.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fanzibang.community.pojo.RoleResourceRelation;
import com.fanzibang.community.vo.ResourceVo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleResourceRelationMapper extends BaseMapper<RoleResourceRelation> {
    List<ResourceVo> getRoleResource(Integer roleId);

    int allotResource(List<RoleResourceRelation> roleResourceRelations);
}
