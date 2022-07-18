package com.fanzibang.community.vo;

import lombok.Data;

@Data
public class PrivateLetterListVo {

    private Long id;
    private Long toId;
    private String avatar;
    private String nickname;
    private String content;
    private String conversationId;
    private Long unReadCount;

}
