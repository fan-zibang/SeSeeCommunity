package com.fanzibang.community.dto;

import lombok.Data;

@Data
public class PrivateLetterParam {

    private Long fromId;
    private Long toId;
    private String content;

}
