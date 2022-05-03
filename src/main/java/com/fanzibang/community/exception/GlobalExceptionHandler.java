package com.fanzibang.community.exception;

import com.fanzibang.community.utils.CommonResult;
import com.fanzibang.community.constant.ReturnCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 全局默认异常处理器
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonResult<String> exceptionHandler(Exception e) {
        log.error("全局异常信息：{}",e.getMessage(), e);
        return CommonResult.fail(ReturnCode.RC500);
    }

    @ExceptionHandler(ApiException.class)
    public CommonResult<String> apiExceptionHandler(ApiException e) {
        if (e.getReturnCode() != null) {
            return CommonResult.fail(e.getReturnCode());
        }
        return CommonResult.fail(e.getMessage());
    }

}
