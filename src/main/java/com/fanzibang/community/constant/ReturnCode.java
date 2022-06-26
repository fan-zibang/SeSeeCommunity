package com.fanzibang.community.constant;

import lombok.Getter;

/**
 * 201 - 299 用户业务错误代码
 * 301 - 399 帖子业务错误代码
 * 401 - 449 评论业务错误代码
 * 450 - 499 点赞业务错误代码
 *
 * 1001 - 1010 文件业务错误代码
 */
public enum ReturnCode {
    /** 操作成功 **/
    RC100(100,"操作成功"),
    /** 操作失败 **/
    RC999(999,"操作失败"),
    /** 账号或密码错误 **/
    RC202(202,"账号或密码错误"),
    /** 账号未激活或账号已注销 **/
    RC203(203,"账号未激活或账号已注销"),
    /** access_denied **/
    RC204(204,"无访问权限,请联系管理员授予权限"),
    /** 用户未登录或token已过期 **/
    RC205(205,"用户未登录或token已过期"),
    /** 邮箱已经激活，请勿重复激活 **/
    RC207(207,"邮箱已经激活，请勿重复激活"),
    /** 邮箱已经注册，请使用其他邮箱注册 **/
    RC208(208,"邮箱已经注册，请使用其他邮箱注册"),
    /** 激活码已经过期 **/
    RC209(209,"激活码已经过期"),
    /** 激活码错误，请重试 **/
    RC210(210,"激活码错误，请重试"),
    /** 该用户不存在，请注册 **/
    RC211(211,"当前用户不存在，请您注册"),
    /** 旧密码错误，请您重试 **/
    RC212(212,"旧密码错误，请您重试"),
    /** 密码更改失败，请您重试 **/
    RC213(213,"密码更改失败，请您重试"),
    /** 用户退出失败 **/
    RC214(214,"用户退出失败"),
    /** 账号激活失败 **/
    RC215(215,"账号激活失败"),
    /** 用户信息更新失败 **/
    RC216(216,"用户信息更新失败"),
    /** 该帖子不存在 **/
    RC301(301,"该帖子不存在"),
    /** 发布帖子失败 **/
    RC302(302,"发布帖子失败"),
    /** 评论失败 **/
    RC401(401,"评论失败"),
    /** 该评论不存在 **/
    RC402(401,"该评论不存在"),
    /** 404 **/
    RC404(404,"访问路径不存在"),
    /** 点赞失败 **/
    RC450(450,"点赞失败"),
    /** 服务异常 **/
    RC500(500,"系统异常，请稍后重试"),
    /** 文件上传失败 **/
    RC1001(1001,"文件上传失败"),
    /** 文件删除失败 **/
    RC1002(1002,"文件删除失败"),
    /** 上传文件为空 **/
    RC1003(1003,"上传文件为空"),
    /** 请上传图片类型，图片支持类型为PNG和JPEG **/
    RC1004(1004,"请上传图片类型，图片支持类型为PNG和JPEG"),
    /** 图片太大了，图片大小限制为3MB **/
    RC1005(1005,"图片太大了，图片大小限制为3MB");

    // 自定义状态码
    @Getter
    private final int code;
    // 自定义描述
    @Getter
    private final String message;

    ReturnCode(int code, String message){
        this.code = code;
        this.message = message;
    }
}
