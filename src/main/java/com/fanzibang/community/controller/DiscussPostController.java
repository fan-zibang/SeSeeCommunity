package com.fanzibang.community.controller;

import com.fanzibang.community.dto.DiscussPostParam;
import com.fanzibang.community.service.DiscussPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/discussPost")
public class DiscussPostController {

    @Autowired
    private DiscussPostService discussPostService;

    @PostMapping
    public int publishDiscussPost(@RequestBody DiscussPostParam discussPostParam) {
        return discussPostService.publishDiscussPost(discussPostParam);
    }

    @DeleteMapping("{id}")
    public int deleteDiscussPost(@PathVariable("id") Long id) {
        return discussPostService.deleteDiscussPost(id);
    }
}
