package com.fanzibang.community.vo;

import lombok.Data;

@Data
public class UserVo {

    private Long id;
    private String nickname;
    private Byte sex;
    private String location;
    private String avatar;
    private String createTime;
    private String lastLogin;

}
