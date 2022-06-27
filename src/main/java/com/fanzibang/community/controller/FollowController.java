package com.fanzibang.community.controller;

import com.fanzibang.community.service.FollowService;
import com.fanzibang.community.utils.CommonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Validated
@RestController
@RequestMapping("/follow")
public class FollowController {

    @Autowired
    private FollowService followService;

    @PostMapping
    public CommonResult follow(@Valid @Min(value = 1, message = "关注实体：1-用户；2-话题") Integer entityType, Long entityId) {
        followService.follow(entityType, entityId);
        return CommonResult.success(null);
    }

}
