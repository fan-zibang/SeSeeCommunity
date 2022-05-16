package com.fanzibang.community.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;

@Data
public class UserParam {

    @Email
    @NotEmpty
    private String email;

    @Max(125)
    @NotEmpty
    private String password;

}
