package com.fanzibang.community.controller;

import com.fanzibang.community.service.LikeService;
import com.fanzibang.community.utils.CommonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Validated
@RestController
@RequestMapping("/like")
public class LikeController {

    @Autowired
    private LikeService likeService;

    @PostMapping
    public CommonResult like(@Valid @NotNull @RequestParam("post_id") Long postId,
                             @Valid @Min(value = 1, message = "1-帖子；2-评论") @Max(value = 2, message = "1-帖子；2-评论") Integer entityType,
                             @Valid @NotNull Long entityId) {
        likeService.like(postId, entityType, entityId);
        return CommonResult.success(null);
    }

}
