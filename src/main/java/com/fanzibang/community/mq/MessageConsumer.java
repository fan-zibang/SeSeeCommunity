package com.fanzibang.community.mq;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.fanzibang.community.pojo.Event;
import com.fanzibang.community.pojo.Message;
import com.fanzibang.community.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageConsumer {

    private static Logger logger = LoggerFactory.getLogger(MessageConsumer.class);

    @Autowired
    private MessageService messageService;

    /**
     * 消费评论、关注、点赞事件
     */
    @RabbitListener(queues = "comment.follow.like.queue")
    public void handleASystemMessage(Event event) {

    }

    /**
     * 消费管理员消息、发帖事件
     */
    @RabbitListener(queues = "admin.publish.queue")
    public void handleBSystemMessage(String msg) {
        Event event = JSONObject.parseObject(msg, Event.class);

        if (ObjectUtil.isEmpty(event)) {
            logger.error("消息内容为空");
            return;
        }
        // 发送系统通知
        Message message = new Message();
        message.setType(event.getType());
        message.setFromId(event.getFromId());
        message.setToId(event.getToId());
        message.setContent(JSONUtil.toJsonStr(event.getData()));
        message.setStatus(0);
        message.setCreateTime(System.currentTimeMillis());
        messageService.save(message);
    }

    /**
     * 消费删帖事件
     */
    @RabbitListener(queues = "delete.queue")
    public void handleCSystemMessage(Event event) {

    }


}