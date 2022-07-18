package com.fanzibang.community.pojo;

import lombok.Data;

@Data
public class PrivateLetter {

    private Long id;
    private Long fromId;
    private Long toId;
    private String conversationId;
    private String content;
    private Integer status;
    private Long createTime;

}
