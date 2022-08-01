package com.fanzibang.community.pojo;

import lombok.Data;

@Data
public class Resource {

    private Long id;
    private String name;
    private String url;
    private String description;
    private Long createTime;

}
