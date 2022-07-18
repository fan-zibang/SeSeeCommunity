package com.fanzibang.community.vo;

import lombok.Data;

@Data
public class PrivateLetterVo {

    private Long id;
    private Long fromId;
    private Long toId;
    private String content;
    private Long createTime;

}
