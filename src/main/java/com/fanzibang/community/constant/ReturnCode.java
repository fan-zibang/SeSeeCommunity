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
    /** 邮箱或密码不能为空 **/
    RC206(206,"邮箱或密码不能为空"),
    /** 邮箱已经激活，请勿重复激活 **/
    RC207(207,"邮箱已经激活，请勿重复激活"),
    /** 邮箱已经注册，请使用其他邮箱注册 **/
    RC208(208,"邮箱已经注册，请使用其他邮箱注册"),
    /** 激活码已经过期 **/
    RC209(209,"激活码已经过期"),
    /** 激活码错误，请重试 **/
    RC210(210,"激活码错误，请重试"),
    /** 服务异常 **/
    RC500(500,"系统异常，请稍后重试");

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
