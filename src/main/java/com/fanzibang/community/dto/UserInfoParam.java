package com.fanzibang.community.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
public class UserInfoParam {

    @NotEmpty
    @Length(max = 50)
    private String nickname;
    @Min(value = 0, message = "性别：0-男；1-女")
    @Max(value = 1, message = "性别：0-男；1-女")
    private Byte sex;
    private String location;
    private String avatar;

}
