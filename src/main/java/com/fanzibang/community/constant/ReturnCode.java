package com.fanzibang.community.constant;

public enum ReturnCode {
    /** 操作成功 **/
    RC100(100,"操作成功"),
    /** 操作失败 **/
    RC999(999,"操作失败"),
    /** 账号未激活 **/
    RC203(203,"请激活账号再登录"),
    /** access_denied **/
    RC204(204,"无访问权限,请联系管理员授予权限"),
    /** 未登录或token过期 **/
    RC205(205,"未登录或token过期"),
    /** 服务异常 **/
    RC500(500,"系统异常，请稍后重试"),

    INVALID_TOKEN(2001,"访问令牌不合法"),
    ACCESS_DENIED(2003,"没有权限访问该资源"),
    CLIENT_AUTHENTICATION_FAILED(1001,"客户端认证失败");

    /**自定义状态码**/
    private final int code;
    /**自定义描述**/
    private final String message;

    ReturnCode(int code, String message){
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
