package com.fanzibang.community.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;

@Data
public class CommentParam {

    // 父级评论id
    private Long parentId;
    @NotNull
    @Min(value = 1, message = "实体类型：1-帖子；2-评论")
    @Max(value = 2, message = "实体类型：1-帖子；2-评论")
    private Integer entityType;
    // 评论目标的 id
    @NotNull
    private Long entityId;
    // 指明对哪个用户进行评论(用户 id)
    // private Long targetId;
    // 内容
    @NotEmpty
    @Length(max = 500)
    private String content;

}
