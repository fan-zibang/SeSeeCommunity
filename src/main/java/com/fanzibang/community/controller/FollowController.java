package com.fanzibang.community.controller;

import com.fanzibang.community.service.FollowService;
import com.fanzibang.community.utils.CommonResult;
import com.fanzibang.community.vo.UserFollowVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

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

    @GetMapping("/follower/{uid}")
    public List<UserFollowVo> getFollowerList(@PathVariable("uid") Long uid) {
        return followService.getFollowerList(uid);
    }

    @GetMapping("/fans/{uid}")
    public List<UserFollowVo> getFansList(@PathVariable("uid") Long uid) {
        return followService.getFansList(uid);
    }

    @GetMapping("/follower/count/{uid}")
    public Long getFollowerCount(@PathVariable("uid") Long uid) {
        return followService.getFollowerCount(uid);
    }

    @GetMapping("/topic/count/{uid}")
    public Long getTopicCount(@PathVariable("uid") Long uid) {
        return followService.getTopicCount(uid);
    }

    @GetMapping("/fans/count/{uid}")
    public Long getFansCount(@PathVariable("uid") Long uid) {
        return followService.getFansCount(uid);
    }
}
