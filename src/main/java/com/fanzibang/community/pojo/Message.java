package com.fanzibang.community.pojo;

import lombok.Data;

@Data
public class Message {

    private Long id;
    private Long fromId;
    private Long toId;
    private Integer type; // 通知类型 0-系统 1-评论 2-关注 3-点赞
    private String content;
    private Integer status; // 通知状态：0-未读 1-已读
    private Long createTime;

}
