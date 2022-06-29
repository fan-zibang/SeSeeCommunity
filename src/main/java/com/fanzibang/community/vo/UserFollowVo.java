package com.fanzibang.community.vo;

import lombok.Data;

@Data
public class UserFollowVo {

    private Long id;
    private String nickname;
    private String avatar;
    private Boolean follow;
    private String followTime;

}
