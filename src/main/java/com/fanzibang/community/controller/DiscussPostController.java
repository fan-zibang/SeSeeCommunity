package com.fanzibang.community.controller;

import com.fanzibang.community.dto.DiscussPostParam;
import com.fanzibang.community.service.DiscussPostService;
import com.fanzibang.community.vo.DiscussPostDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;

@Validated
@RestController
@RequestMapping("/discussPost")
public class DiscussPostController {

    @Autowired
    private DiscussPostService discussPostService;

    @GetMapping
    public List<DiscussPostDetailVo> getDiscussPostList(@Valid @NotNull Long userId, Integer current, Integer size,
                                                 @Valid @Min(value = 1, message = "模式：1-热度；2-最新")
                                                            @Max(value = 2, message = "模式：1-热度；2-最新") Integer mode) {
        return discussPostService.getDiscussPostList(userId, current, size, mode);
    }

    @GetMapping("/count")
    public Long getDiscussPostCount(@Valid @NotNull Long userId) {
        return discussPostService.getDiscussPostCount(userId);
    }

    @GetMapping("/{id}")
    public DiscussPostDetailVo getDiscussPostDetail(@PathVariable("id") Long id) {
        return discussPostService.getDiscussPostDetail(id);
    }

    @PostMapping
    public int publishDiscussPost(@RequestBody @Valid DiscussPostParam discussPostParam) {
        return discussPostService.publishDiscussPost(discussPostParam);
    }

    @PutMapping("/essence/{postId}")
    public Integer setEssence(@PathVariable("postId") Long postId) {
        return discussPostService.setEssence(postId);
    }

    @DeleteMapping("/{id}")
    public int deleteDiscussPost(@PathVariable("id") Long id) {
        return discussPostService.deleteDiscussPost(id);
    }

}
