package com.fanzibang.community.mq;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.fanzibang.community.pojo.Event;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SystemMessageProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendMessage(Event event) {
        rabbitTemplate.convertAndSend(event.getExchange(), event.getRoutingKey(), JSONObject.toJSONString(event));
    }

}
