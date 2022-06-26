package com.fanzibang.community.controller;

import com.fanzibang.community.dto.CommentParam;
import com.fanzibang.community.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("/{postId}")
    public List<Map<String, Object>> getPostComment(@Valid @PathVariable("postId") Long postId,
                                                     Integer current, Integer size) {
        return commentService.getCommentList(postId, current, size);
    }

    @PostMapping("/{postId}")
    public Integer addComment(@Valid @NotNull @PathVariable("postId") Long postId,
                              @Valid @RequestBody CommentParam commentParam) {
        return commentService.addComment(postId, commentParam);
    }
 }
