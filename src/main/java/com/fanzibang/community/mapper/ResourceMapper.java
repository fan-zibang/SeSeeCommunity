package com.fanzibang.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fanzibang.community.pojo.Resource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceMapper extends BaseMapper<Resource> {
    List<Resource> getResourceList(Long userId);
}
