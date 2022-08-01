package com.fanzibang.community.pojo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class Role {

    private Integer id;
    @NotEmpty
    private String name;
    private String description;
    private Long createTime;
    private Byte status;

}
