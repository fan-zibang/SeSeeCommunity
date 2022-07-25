package com.fanzibang.community.vo;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DiscussPostDetailVo {

    // 帖子id
    private Long id;
    // 作者id
    private Long authorId;
    // 作者
    private String author;
    // 帖子标题
    private String title;
    // 帖子内容
    private String content;
    // 是否已经点赞
    private Boolean isLike;
    // 点赞数量
    private Long likeCount;
    // 评论数量
    private Long commentCount;
    // 所属板块
    private String plate;
    // 发布时间
    private String createTime;

}
