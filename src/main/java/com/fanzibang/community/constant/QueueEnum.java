package com.fanzibang.community.constant;

import lombok.Getter;

/**
 * 消息队列枚举配置
 */
@Getter
public enum QueueEnum {

    /**
     * 系统消息通知服务进程
     */
    SYSTEM_MESSAGE_BROKER("system.message.direct", "system.message.queue", "system.message"),
    /**
     * 系统消息通知死信服务进程
     */
    SYSTEM_MESSAGE_DEAD_BROKER("system.message.dead.direct", "system.message.dead.queue", "system.message.dead");

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
    private String routeKey;

    QueueEnum(String exchange, String queueName, String routeKey) {
        this.exchange = exchange;
        this.queueName = queueName;
        this.routeKey = routeKey;
    }
}
