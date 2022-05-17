package com.fanzibang.community.mq;

import com.alibaba.fastjson.JSONObject;
import com.fanzibang.community.pojo.Event;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendMessage(Event event) {
        rabbitTemplate.convertAndSend(event.getExchange(), event.getRoutingKey(), JSONObject.toJSONString(event));
    }

}
