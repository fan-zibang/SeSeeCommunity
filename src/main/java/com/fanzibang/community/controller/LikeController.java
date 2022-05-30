package com.fanzibang.community.controller;

import com.fanzibang.community.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("like")
public class LikeController {

    @Autowired
    private LikeService likeService;

    @PostMapping
    public String like(Integer entityType, Long entityId, Long entityUserId) {
        return likeService.like(entityType, entityId, entityUserId);
    }
}
