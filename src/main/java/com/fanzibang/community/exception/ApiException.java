package com.fanzibang.community.exception;

import com.fanzibang.community.common.ReturnCode;
import lombok.Getter;
import lombok.Setter;

public class ApiException extends RuntimeException{

    @Setter
    @Getter
    protected ReturnCode returnCode;

    @Setter
    @Getter
    protected String message;

    public ApiException(String message) {
        this.message = message;
    }

    public ApiException(ReturnCode returnCode) {
        this.returnCode = returnCode;
    }


}
