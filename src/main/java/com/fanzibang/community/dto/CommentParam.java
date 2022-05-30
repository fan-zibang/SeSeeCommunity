package com.fanzibang.community.dto;

import lombok.Data;

@Data
public class CommentParam {

    // 文章id
    private Long postId;
    // 文章作者id
    private Long postUserId;
    // 评论目标的类型（帖子、评论）
    private Integer entityType;
    // 评论目标的 id
    private Long entityId;
    // 指明对哪个用户进行评论(用户 id)
    private Long targetId;
    // 内容
    private String content;

}
