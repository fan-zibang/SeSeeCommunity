package com.fanzibang.community.controller;

import com.fanzibang.community.dto.ResourceParam;
import com.fanzibang.community.pojo.Resource;
import com.fanzibang.community.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/resource")
public class ResourceController {

    @Autowired
    private ResourceService resourceService;

    @GetMapping("/pageList")
    public Map<String, Object> getResourcePageList(Integer current, Integer size) {
        return resourceService.getResourcePageList(current, size);
    }

    @GetMapping("/list")
    public List<Resource> getResourceList() {
        return resourceService.getResourceList();
    }

    @GetMapping("/{resourceId}")
    public Resource getResourceById(@PathVariable("resourceId") Long resourceId) {
        return resourceService.getResourceById(resourceId);
    }

    @PostMapping
    public int addResource(@RequestBody @Valid ResourceParam resourceParam) {
        return resourceService.addResource(resourceParam);
    }

    @PostMapping("/{resourceId}")
    public int editResource(@PathVariable("resourceId") Long resourceId, @RequestBody @Valid ResourceParam resourceParam) {
        return resourceService.editResource(resourceId, resourceParam);
    }

    @DeleteMapping("/delete/{resourceId}")
    public int deleteResource(@PathVariable("resourceId") Long resourceId) {
        return resourceService.deleteResource(resourceId);
    }

}
