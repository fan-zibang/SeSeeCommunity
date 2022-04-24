package com.fanzibang.community.exception;

import com.fanzibang.community.common.ReturnCode;

public class Asserts {

    public static void fail(String message) {
        throw new ApiException(message);
    }

    public static void fail(ReturnCode returnCode) {
        throw new ApiException(returnCode);
    }

}
