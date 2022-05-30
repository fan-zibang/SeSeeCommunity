package com.fanzibang.community.controller;

import com.fanzibang.community.dto.DiscussPostParam;
import com.fanzibang.community.service.DiscussPostService;
import com.fanzibang.community.vo.DiscussPostDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Validated
@RestController
@RequestMapping("/discussPost")
public class DiscussPostController {

    @Autowired
    private DiscussPostService discussPostService;

    @GetMapping("/{id}")
    public DiscussPostDetailVo getDiscussPostDetail(@Valid @PathVariable("id") @Min(1) Long id) {
        return discussPostService.getDiscussPostDetail(id);
    }

    @PostMapping
    public int publishDiscussPost(@RequestBody @Valid DiscussPostParam discussPostParam) {
        return discussPostService.publishDiscussPost(discussPostParam);
    }

    @DeleteMapping("/{id}")
    public int deleteDiscussPost(@Valid @PathVariable("id") @Min(1) Long id) {
        return discussPostService.deleteDiscussPost(id);
    }

}
