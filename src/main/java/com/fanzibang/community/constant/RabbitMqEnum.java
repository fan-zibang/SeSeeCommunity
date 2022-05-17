package com.fanzibang.community.constant;

import lombok.Getter;

/**
 * 消息队列枚举配置
 */
@Getter
public enum RabbitMqEnum {

    /**
     * 系统通知-评论、关注、点赞服务进程
     */
    A_SYSTEM_MESSAGE_BROKER("system.message.topic","comment.follow.like.queue","comment.follow.like"),

    /**
     * 系统通知-管理员消息、发帖服务进程
     */
    B_SYSTEM_MESSAGE_BROKER("system.message.topic","admin.publish.queue","admin.publish"),

    /**
     * 系统通知-删帖服务进程
     */
    C_SYSTEM_MESSAGE_BROKER("system.message.topic","delete.queue","delete");


    /**
     * 交换机名称
     */
    private String exchange;
    /**
     * 队列名称
     */
    private String queueName;
    /**
     * 路由绑定键
     */
    private String routingKey;

    RabbitMqEnum(String exchange, String queueName, String routingKey) {
        this.exchange = exchange;
        this.queueName = queueName;
        this.routingKey = routingKey;
    }

}
