package com.fanzibang.community.pojo;

import lombok.Data;

@Data
public class Comment {

    private Long id;
    // 发布评论的作者
    private Long userId;
    // 父级评论id
    private Long parentId;
    // 评论目标的类型（帖子、评论）
    private Integer entityType;
    // 评论目标的 id
    private Long entityId;
    // 指明对哪个用户进行评论(用户 id)
    private Long targetId;
    // 内容
    private String content;
    // 发布时间
    private Long createTime;

}
