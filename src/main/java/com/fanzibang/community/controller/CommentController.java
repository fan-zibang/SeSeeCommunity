package com.fanzibang.community.controller;

import com.fanzibang.community.dto.CommentParam;
import com.fanzibang.community.pojo.Comment;
import com.fanzibang.community.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("/{postId}")
    public List<Map<String, Object>> getPostComment(@PathVariable("postId") Long postId, Integer current, Integer size) {
        return commentService.getCommentList(postId, current, size);
    }

    @PostMapping
    public Integer addComment(@RequestBody CommentParam commentParam) {
        return commentService.addComment(commentParam);
    }
 }
