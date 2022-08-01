package com.fanzibang.community.mq;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.fanzibang.community.constant.MessageConstant;
import com.fanzibang.community.pojo.DiscussPost;
import com.fanzibang.community.pojo.Event;
import com.fanzibang.community.pojo.Message;
import com.fanzibang.community.service.DiscussPostService;
import com.fanzibang.community.service.EsDiscussPostService;
import com.fanzibang.community.service.MessageService;
import com.fanzibang.community.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class MessageConsumer {

    private static Logger logger = LoggerFactory.getLogger(MessageConsumer.class);

    @Autowired
    private MessageService messageService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private EsDiscussPostService esDiscussPostService;

    /**
     * 消费管理员消息、评论、关注、点赞事件
     */
    @RabbitListener(queues = "admin.comment.follow.like.queue")
    public void handleASystemMessage(String msg) {
        Event event = JSONObject.parseObject(msg, Event.class);

        if (ObjectUtil.isEmpty(event)) {
            logger.error("消息内容为空");
            return;
        }

        // 是否是群发消息
        if (event.getToId() == MessageConstant.ALL_USER) {
            List<Long> ids = userService.getAllUserIds();
            for (Long id : ids) {
                event.setToId(id);
                Message message = copy(event);
                messageService.addMessage(message);
            }
        } else {
            Message message = copy(event);
            // 发送系统通知
            messageService.addMessage(message);
        }

    }

    /**
     * 消费发帖事件
     */
    @RabbitListener(queues = "publish.queue")
    public void handleBSystemMessage(String msg) {
        Event event = JSONObject.parseObject(msg, Event.class);

        if (ObjectUtil.isEmpty(event)) {
            logger.error("消息内容为空");
            return;
        }

        if (ObjectUtil.isEmpty(event.getData().get("postId"))) {
            logger.error("postId为空，es存取帖子失败");
            return;
        }
        Long postId = Long.valueOf(event.getData().get("postId").toString());
        DiscussPost discussPost = discussPostService.getDiscussPostById(postId);
        // 存入 Elasticsearch 服务器
        DiscussPost post = esDiscussPostService.save(discussPost);
        if (ObjectUtil.isNull(post)) {
            logger.error("帖子存入es服务器失败");
        }
    }

    /**
     * 消费删帖事件
     */
    @RabbitListener(queues = "delete.queue")
    public void handleCSystemMessage(String msg) {
        Event event = JSONObject.parseObject(msg, Event.class);

        if (ObjectUtil.isEmpty(event)) {
            logger.error("消息内容为空");
            return;
        }
        Long postId = Long.valueOf(event.getData().get("postId").toString());
        if (ObjectUtil.isEmpty(postId)) {
            logger.error("postId为空，es删除帖子失败");
            return;
        }
        // 更新 Elasticsearch 服务器
        esDiscussPostService.deleteById(postId);

    }

    private Message copy(Event event) {
        Message message = new Message();
        message.setType(event.getType());
        message.setFromId(event.getFromId());
        message.setToId(event.getToId());
        if (ObjectUtil.isNotEmpty(event.getData())) {
            message.setContent(JSONUtil.toJsonStr(event.getData()));
        }
        message.setStatus(0);
        message.setCreateTime(System.currentTimeMillis());
        return message;
    }

}
