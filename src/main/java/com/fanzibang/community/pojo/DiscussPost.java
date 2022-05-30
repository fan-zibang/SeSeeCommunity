package com.fanzibang.community.pojo;

import lombok.Data;

@Data
public class DiscussPost {

    private Long id;
    private Long userId;
    private String title;
    private String content;
    private Byte type;
    private Byte status;
    private Long commentCount;
    private Double score;
    private Integer plateId;
    private Long createTime;

}
