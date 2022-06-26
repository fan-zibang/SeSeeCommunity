package com.fanzibang.community.exception;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.fanzibang.community.utils.CommonResult;
import com.fanzibang.community.constant.ReturnCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 404异常处理
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoHandlerFoundException.class)
    public CommonResult handleNoHandlerFoundException(NoHandlerFoundException e) {
        logger.info("页面不存在：{}", e.getMessage());
        return CommonResult.fail(ReturnCode.RC404);
    }

    /**
     * 处理自定义的业务异常
     */
    @ExceptionHandler(ApiException.class)
    public CommonResult handleApiException(ApiException e) {
        if (e.getReturnCode() != null) {
            return CommonResult.fail(e.getReturnCode());
        }
        return CommonResult.fail(e.getMessage());
    }

    /**
     * 处理登录异常
     */
    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public CommonResult handleLoginExceptions(InternalAuthenticationServiceException e) {
        return CommonResult.fail(ReturnCode.RC202);
    }
    @ExceptionHandler(BadCredentialsException.class)
    public CommonResult handleLoginExceptions(BadCredentialsException e) {
        return CommonResult.fail(ReturnCode.RC202);
    }

    /**
     * 解决 AccessDeniedException 被 handleException 消费掉,导致 AccessDeniedHandler 不会被触发的问题
     */
    @ExceptionHandler(AccessDeniedException.class)
    public void handleAccessDeniedException(AccessDeniedException e) throws AccessDeniedException {
        throw e;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public CommonResult handleValidationException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        String message = null;
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            if (fieldError != null) {
                message = fieldError.getField()+fieldError.getDefaultMessage();
            }
        }
        return CommonResult.validateFail(message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public CommonResult handleConstraintViolationException(ConstraintViolationException e) {
        logger.info("参数校验错误：{}", e.getMessage());
        return CommonResult.validateFail(StrUtil.subAfter(e.getMessage(),".", true));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public CommonResult handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        logger.info("上传文件错误：{}", e.getMessage(), e);
        return CommonResult.fail(ReturnCode.RC1005);
    }

    /**
     * 处理未知异常
     */
    @ExceptionHandler(Exception.class)
    public CommonResult handleException(Exception e) {
        logger.error("未知异常：{}", e.getMessage(), e);
        return CommonResult.fail(ReturnCode.RC500);
    }

}
