package com.fanzibang.community.mq;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.fanzibang.community.pojo.Event;
import com.fanzibang.community.pojo.SystemMessage;
import com.fanzibang.community.service.SystemMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SystemMessageConsumer {

    private static Logger logger = LoggerFactory.getLogger(SystemMessageConsumer.class);

    @Autowired
    private SystemMessageService systemMessageService;

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
        SystemMessage systemMessage = new SystemMessage();
        systemMessage.setType(event.getType());
        systemMessage.setFromId(event.getFromId());
        systemMessage.setToId(event.getToId());
        systemMessage.setContent(JSONUtil.toJsonStr(event.getData()));
        systemMessage.setStatus(0);
        systemMessage.setCreateTime(System.currentTimeMillis());
        systemMessageService.save(systemMessage);
    }

    /**
     * 消费删帖事件
     */
    @RabbitListener(queues = "delete.queue")
    public void handleCSystemMessage(Event event) {

    }


}
