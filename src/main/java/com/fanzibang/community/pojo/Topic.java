package com.fanzibang.community.pojo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class Topic {

    private Integer id;
    @NotEmpty
    private String name;
    @NotEmpty
    private String summary;
    private Long createTime;

}
