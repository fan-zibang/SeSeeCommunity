package com.fanzibang.community.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@Data
public class DiscussPostParam {

    @NotEmpty
    private String title;
    @NotEmpty
    private String content;
    @Min(1)
    private Integer plateId;

}
