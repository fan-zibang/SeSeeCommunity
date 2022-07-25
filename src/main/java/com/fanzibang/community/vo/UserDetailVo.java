package com.fanzibang.community.vo;

import lombok.Data;

@Data
public class UserDetailVo {

    private String nickname;
    private Byte sex;
    private String location;
    private String avatar;
    private Long fansCount;
    private Long userLikeCount;
    private Long followerCount;
    private String createTime;

}
