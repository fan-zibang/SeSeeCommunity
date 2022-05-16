package com.fanzibang.community.utils;

import com.fanzibang.community.constant.ReturnCode;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class CommonResult<T> {
    /** 结果状态 ,具体状态码参见ReturnCode.java */
    private int status; // 由后端统一定义各种返回结果的状态码
    private String message; // 本次接口调用的结果描述
    private T data; // 本次返回的数据
    private long timestamp ; // 接口调用时间

    public CommonResult (){
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> CommonResult<T> success(T data) {
        CommonResult<T> resultData = new CommonResult<>();
        resultData.setStatus(ReturnCode.RC100.getCode());
        resultData.setMessage(ReturnCode.RC100.getMessage());
        resultData.setData(data);
        return resultData;
    }

    public static <T> CommonResult<T> fail(String message) {
        CommonResult<T> resultData = new CommonResult<>();
        resultData.setStatus(ReturnCode.RC999.getCode());
        resultData.setMessage(message);
        return resultData;
    }

    public static <T> CommonResult<T> fail(ReturnCode returnCode) {
        CommonResult<T> resultData = new CommonResult<>();
        resultData.setStatus(returnCode.getCode());
        resultData.setMessage(returnCode.getMessage());
        return resultData;
    }

    public static <T> CommonResult<T> validateFail(String message) {
        CommonResult<T> resultData = new CommonResult<>();
        resultData.setStatus(HttpStatus.BAD_REQUEST.value());
        resultData.setMessage(message);
        return resultData;
    }

}
