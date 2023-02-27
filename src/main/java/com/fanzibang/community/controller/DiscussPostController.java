package com.fanzibang.community.controller;

import com.fanzibang.community.dto.DiscussPostParam;
import com.fanzibang.community.service.DiscussPostService;
import com.fanzibang.community.vo.DiscussPostDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequestMapping("/discussPost")
public class DiscussPostController {

    @Autowired
    private DiscussPostService discussPostService;

    @GetMapping("/list")
    public List<DiscussPostDetailVo> getDiscussPostList(Long userId, Integer current, Integer size, Integer mode) {
        return discussPostService.getDiscussPostList(userId, current, size, mode);
    }

    @GetMapping("/list/block")
    public List<DiscussPostDetailVo> getDiscussPostBlockList(Long userId, Integer current, Integer size) {
        return discussPostService.getDiscussPostBlockList(userId, current, size);
    }

    @GetMapping("/count")
    public Long getDiscussPostCount(Long userId) {
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
    public Integer setEssence(@PathVariable("postId") Long postId, Integer mode) {
        return discussPostService.setEssence(postId, mode);
    }

    @PutMapping("/block/{postId}")
    public Integer setBlock(@PathVariable("postId") Long postId, Integer mode) {
        return discussPostService.setBlock(postId, mode);
    }

    @DeleteMapping("/delete/{id}")
    public int deleteDiscussPost(@PathVariable("id") Long id) {
        return discussPostService.deleteDiscussPost(id);
    }

}
