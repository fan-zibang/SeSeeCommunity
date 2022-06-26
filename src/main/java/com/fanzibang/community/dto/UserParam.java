package com.fanzibang.community.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
public class UserParam {

    @Email
    @NotEmpty
    private String email;

    @Length(max = 125)
    @NotEmpty
    private String password;

}
