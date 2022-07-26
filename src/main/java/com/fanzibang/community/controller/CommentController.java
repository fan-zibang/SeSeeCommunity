package com.fanzibang.community.controller;

import com.fanzibang.community.dto.CommentParam;
import com.fanzibang.community.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("/{postId}")
    public List<Map<String, Object>> getCommentList(@PathVariable("postId") Long postId,
                                                     Integer current, Integer size) {
        return commentService.getCommentList(postId, current, size);
    }

    @GetMapping("/count/{postId}")
    public Map<String, Object> getCommentCount(@PathVariable("postId") Long postId) {
        return commentService.getCommentCount(postId);
    }

    @PostMapping("/{postId}")
    public Integer addComment(@Valid @NotNull @PathVariable("postId") Long postId,
                              @Valid @RequestBody CommentParam commentParam) {
        return commentService.addComment(postId, commentParam);
    }

 }
