package com.fanzibang.community.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fanzibang.community.constant.EntityTypeConstant;
import com.fanzibang.community.constant.MessageConstant;
import com.fanzibang.community.constant.RabbitMqEnum;
import com.fanzibang.community.mapper.MessageMapper;
import com.fanzibang.community.mq.MessageProducer;
import com.fanzibang.community.pojo.*;
import com.fanzibang.community.service.*;
import com.fanzibang.community.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private UserHolder userHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private FollowService followService;

    @Autowired
    private MessageProducer messageProducer;

    @Override
    public Integer addMessage(Message message) {
        return messageMapper.insert(message);
    }

    /**
     * 显示系统通知
     * @param current
     * @param size
     * @return
     */
    @Override
    public List<Map<String, Object>> getSystemMessageList(Integer current, Integer size) {
        List<Message> systemMessageList = getMessageList(MessageConstant.TOPIC_SYSTEM, current, size);
        List<Map<String,Object>> systemMessageVoList = new ArrayList<>();
        for (Message message : systemMessageList) {
            Map<String, Object> messageVo = new HashMap<>();
            JSONObject jsonObject = JSONUtil.parseObj(message.getContent());
            messageVo.put("content", jsonObject.get("content"));
            messageVo.put("status", message.getStatus());
            messageVo.put("create_time", message.getCreateTime());
            systemMessageVoList.add(messageVo);
        }
        return systemMessageVoList;
    }

    /**
     * 显示评论通知
     * @param current
     * @param size
     * @return
     */
    @Override
    public List<Map<String, Object>> getCommentMessageList(Integer current, Integer size) {
        List<Message> commentMessageList = getMessageList(MessageConstant.TOPIC_COMMENT, current, size);
        List<Map<String, Object>> commentMessageVoList = new ArrayList<>();
        for (Message message : commentMessageList) {
            Map<String, Object> messageVo = packageMessage(message);
            commentMessageVoList.add(messageVo);
        }
        return commentMessageVoList;
    }

    /**
     * 显示关注通知
     * @param current
     * @param size
     * @return
     */
    @Override
    public List<Map<String, Object>> getFollowMessageList(Integer current, Integer size) {
        List<Message> followMessageList = getMessageList(MessageConstant.TOPIC_FOLLOW, current, size);
        List<Map<String, Object>> followMessageVoList = new ArrayList<>();
        for (Message message : followMessageList) {
            Map<String, Object> messageVo = new HashMap<>();
            User user = userService.getById(message.getFromId());
            if (ObjectUtil.isNotNull(user)) {
                messageVo.put("from_id", user.getId());
                messageVo.put("nickname", user.getNickname());
            } else {
                messageVo.put("from_id", message.getFromId());
                messageVo.put("nickname", null);
            }
            messageVo.put("is_follow", followService.isFollow(EntityTypeConstant.ENTITY_TYPE_USER, message.getFromId()));
            messageVo.put("status", message.getStatus());
            messageVo.put("create_time", message.getCreateTime());
            followMessageVoList.add(messageVo);
        }
        return followMessageVoList;
    }

    /**
     * 显示点赞通知
     * @param current
     * @param size
     * @return
     */
    @Override
    public List<Map<String, Object>> getLikeMessageList(Integer current, Integer size) {
        List<Message> likeMessageList = getMessageList(MessageConstant.TOPIC_LIKE, current, size);
        List<Map<String, Object>> likeMessageVoList = new ArrayList<>();
        for (Message message : likeMessageList) {
            Map<String, Object> messageVo = packageMessage(message);
            likeMessageVoList.add(messageVo);
        }
        return likeMessageVoList;
    }

    private List<Message> getMessageList(Integer entityType, Integer current, Integer size) {
        current = Optional.ofNullable(current).orElse(1);
        size = Optional.ofNullable(size).orElse(20);
        Page<Message> page = new Page<>(current,size,false);
        User user = userHolder.getUser();
        LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Message::getType, entityType).eq(Message::getToId, user.getId());
        Page<Message> messagePage = messageMapper.selectPage(page, queryWrapper);
        List<Message> messageList = messagePage.getRecords();
        return messageList;
    }

    private Map<String, Object> packageMessage(Message message) {
        Map<String, Object> messageVo = new HashMap<>();
        User user = userService.getById(message.getFromId());
        if (ObjectUtil.isNotNull(user)) {
            messageVo.put("from_id", user.getId());
            messageVo.put("nickname", user.getNickname());
        } else {
            messageVo.put("from_id", message.getFromId());
            messageVo.put("nickname", null);
        }
        JSONObject jsonObject = JSONUtil.parseObj(message.getContent());
        Integer entityType = (Integer) jsonObject.get("entityType");
        messageVo.put("entity_type", entityType);
        long postId = Long.valueOf(jsonObject.get("postId").toString());
        DiscussPost discussPost = discussPostService.getDiscussPostById(postId);
        String title = ObjectUtil.isNotNull(discussPost) ? discussPost.getTitle() : null;
        messageVo.put("post_id", postId);
        messageVo.put("post_title", title);
        if (ObjectUtil.isNotNull(jsonObject.get("commentId"))) {
            long commentId = Long.valueOf(jsonObject.get("commentId").toString());
            Comment comment = commentService.getCommentById(commentId);
            String commentContent = ObjectUtil.isNotNull(comment) ? comment.getContent() : null;
            messageVo.put("comment_content", commentContent);
        }
        messageVo.put("create_time", message.getCreateTime());
        return messageVo;
    }

    @Override
    public void publishSystemMessage(String content) {
        Event event = new Event().setExchange(RabbitMqEnum.A_SYSTEM_MESSAGE_BROKER.getExchange())
                .setRoutingKey(RabbitMqEnum.A_SYSTEM_MESSAGE_BROKER.getRoutingKey())
                .setType(MessageConstant.TOPIC_SYSTEM)
                .setFromId(MessageConstant.SYSTEM_USER_ID)
                .setToId(MessageConstant.ALL_USER)
                .setData("content", content);
        messageProducer.sendMessage(event);
    }
}
