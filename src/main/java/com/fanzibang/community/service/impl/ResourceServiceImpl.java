package com.fanzibang.community.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fanzibang.community.constant.ReturnCode;
import com.fanzibang.community.dto.ResourceParam;
import com.fanzibang.community.exception.Asserts;
import com.fanzibang.community.mapper.ResourceMapper;
import com.fanzibang.community.pojo.Resource;
import com.fanzibang.community.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ResourceServiceImpl implements ResourceService {

    @Autowired
    private ResourceMapper resourceMapper;

    @Override
    public List<Resource> getResourceList() {
        return resourceMapper.selectList(null);
    }

    @Override
    public Map<String, Object> getResourcePageList(Integer current, Integer size) {
        current = Optional.ofNullable(current).orElse(1);
        size = Optional.ofNullable(size).orElse(20);
        Page<Resource> page = new Page<>(current, size);
        Page<Resource> resourcePage = resourceMapper.selectPage(page, null);
        Map<String, Object> map = new HashMap<>();
        map.put("resourceList", resourcePage.getRecords());
        map.put("total", resourcePage.getTotal());
        return map;
    }

    @Override
    public Resource getResourceById(Long resourceId) {
        return resourceMapper.selectById(resourceId);
    }

    @Override
    public int addResource(ResourceParam resourceParam) {
        Resource resource = new Resource();
        BeanUtil.copyProperties(resourceParam, resource);
        resource.setCreateTime(System.currentTimeMillis());
        int i = resourceMapper.insert(resource);
        if (i <= 0) {
            Asserts.fail(ReturnCode.RC650);
        }
        return i;
    }

    @Override
    public int editResource(Long resourceId, ResourceParam resourceParam) {
        Resource resource = new Resource();
        BeanUtil.copyProperties(resourceParam, resource);
        resource.setId(resourceId);
        int i = resourceMapper.updateById(resource);
        if (i <= 0) {
            Asserts.fail(ReturnCode.RC651);
        }
        return i;
    }

    @Override
    public int deleteResource(Long resourceId) {
        int i = resourceMapper.deleteById(resourceId);
        if (i <= 0) {
            Asserts.fail(ReturnCode.RC652);
        }
        return i;
    }
}
