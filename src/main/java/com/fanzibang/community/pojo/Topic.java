package com.fanzibang.community.pojo;

import lombok.Data;

@Data
public class Topic {

    private Integer id;
    private String name;
    private String summary;
    private Long createTime;

}
