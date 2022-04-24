package com.fanzibang.community.common;

import lombok.Data;

@Data
public class CommonResult<T> {
    /** 结果状态 ,具体状态码参见ResultData.java*/
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
        resultData.setMessage(message);
        return resultData;
    }

    public static <T> CommonResult<T> fail(ReturnCode returnCode) {
        CommonResult<T> resultData = new CommonResult<>();
        resultData.setStatus(returnCode.getCode());
        resultData.setMessage(returnCode.getMessage());
        return resultData;
    }

}
