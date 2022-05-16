package com.fanzibang.community.config;

import com.fanzibang.community.constant.RabbitMqEnum;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 消息队列配置
 */
@Configuration
public class RabbitMqConfig {

    // 系统通知队列所绑定的topic交换机
    @Bean
    public TopicExchange systemMessageTopicEx() {
        return (TopicExchange) ExchangeBuilder.topicExchange(RabbitMqEnum.A_SYSTEM_MESSAGE_BROKER.getExchange())
                .durable(true)
                .build();
    }

    // 系统通知-评论、关注、点赞队列
    @Bean
    public Queue aSystemMessageQueue() {
        return new Queue(RabbitMqEnum.A_SYSTEM_MESSAGE_BROKER.getQueueName());
    }

    // 系统通知-管理员消息、发帖队列
    @Bean
    public Queue bSystemMessageQueue() {
        return new Queue(RabbitMqEnum.B_SYSTEM_MESSAGE_BROKER.getQueueName());
    }

    // 系统通知-删帖队列
    @Bean
    public Queue cSystemMessageQueue() {
        return new Queue(RabbitMqEnum.C_SYSTEM_MESSAGE_BROKER.getQueueName());
    }

    // 系统通知-评论、关注、点赞队列绑定topic交换机
    @Bean
    public Binding aSysMesQueBindingSysMesTopicEx(TopicExchange systemMessageTopicEx, Queue aSystemMessageQueue) {
        return BindingBuilder
                .bind(aSystemMessageQueue)
                .to(systemMessageTopicEx)
                .with(RabbitMqEnum.A_SYSTEM_MESSAGE_BROKER.getRoutingKey());
    }

    // 系统通知-管理员消息、发帖队列绑定topic交换机
    @Bean
    public Binding bSysMesQueBindingSysMesTopicEx(TopicExchange systemMessageTopicEx, Queue bSystemMessageQueue) {
        return BindingBuilder
                .bind(bSystemMessageQueue)
                .to(systemMessageTopicEx)
                .with(RabbitMqEnum.B_SYSTEM_MESSAGE_BROKER.getRoutingKey());
    }

    // 系统通知-删帖队列绑定topic交换机
    @Bean
    public Binding cSysMesQueBindingSysMesTopicEx(TopicExchange systemMessageTopicEx, Queue cSystemMessageQueue) {
        return BindingBuilder
                .bind(cSystemMessageQueue)
                .to(systemMessageTopicEx)
                .with(RabbitMqEnum.C_SYSTEM_MESSAGE_BROKER.getRoutingKey());
    }


}
