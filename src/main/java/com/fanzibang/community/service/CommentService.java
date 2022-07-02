package com.fanzibang.community.service;

import com.fanzibang.community.dto.CommentParam;
import com.fanzibang.community.pojo.Comment;

import java.util.List;
import java.util.Map;

public interface CommentService {
    List<Map<String, Object>> getCommentList(Long postId, Integer current, Integer size);

    Integer addComment(Long postId, CommentParam commentParam);

    Comment getCommentById(Long id);

}
