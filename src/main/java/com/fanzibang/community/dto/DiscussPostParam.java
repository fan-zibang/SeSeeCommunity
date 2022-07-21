package com.fanzibang.community.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class DiscussPostParam {

    @NotEmpty
    @Length(max = 50)
    private String title;
    @NotEmpty
    private String content;
    @NotNull
    private Integer topicId;

}
