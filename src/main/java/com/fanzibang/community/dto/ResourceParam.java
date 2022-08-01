package com.fanzibang.community.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class ResourceParam {

    @NotEmpty
    private String name;
    @NotEmpty
    private String url;
    private String description;

}
