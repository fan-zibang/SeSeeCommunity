package com.fanzibang.community.pojo;

import lombok.Data;

@Data
public class User {

    private Long id;
    private String email;
    private String password;
    private String nickname;
    private Byte sex;
    private String location;
    private Integer role;
    private Integer status;
    private String avatar;
    private Long createTime;

}
