package com.fanzibang.community.service;

import com.fanzibang.community.dto.ResourceParam;
import com.fanzibang.community.pojo.Resource;

import java.util.List;
import java.util.Map;

public interface ResourceService {
    List<Resource> getResourceList();

    int addResource(ResourceParam resourceParam);

    int editResource(Long resourceId, ResourceParam resourceParam);

    int deleteResource(Long resourceId);

    Map<String, Object> getResourcePageList(Integer current, Integer size);

    Resource getResourceById(Long resourceId);
}
