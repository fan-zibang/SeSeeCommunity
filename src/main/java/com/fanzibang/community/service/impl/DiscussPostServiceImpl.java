package com.fanzibang.community.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fanzibang.community.constant.MessageConstant;
import com.fanzibang.community.constant.RabbitMqEnum;
import com.fanzibang.community.constant.ReturnCode;
import com.fanzibang.community.dto.DiscussPostParam;
import com.fanzibang.community.exception.Asserts;
import com.fanzibang.community.mapper.DiscussPostMapper;
import com.fanzibang.community.mq.MessageProducer;
import com.fanzibang.community.pojo.DiscussPost;
import com.fanzibang.community.pojo.Event;
import com.fanzibang.community.service.DiscussPostService;
import com.fanzibang.community.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DiscussPostServiceImpl implements DiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private MessageProducer messageProducer;

    @Override
    public int publishDiscussPost(DiscussPostParam discussPostParam) {
        Long userId = UserHolder.getUser().getId();
        if (ObjectUtil.isEmpty(userId)) {
            Asserts.fail(ReturnCode.RC205);
        }
        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(userId);
        discussPost.setTitle(discussPostParam.getTitle());
        discussPost.setContent(discussPostParam.getContent());
        discussPost.setType((byte) 0);
        discussPost.setStatus((byte) 0);
        discussPost.setLikeCount(0L);
        discussPost.setCommentCount(0L);
        discussPost.setScore(0D);
        discussPost.setCreateTime(System.currentTimeMillis());
        int i = discussPostMapper.insert(discussPost);
        if (i <= 0) {
            Asserts.fail("发布帖子失败");
        }
        // 触发发帖事件，通过消息队列将其存入 Elasticsearch 服务器
        Event event = new Event()
                .setExchange(RabbitMqEnum.B_SYSTEM_MESSAGE_BROKER.getExchange())
                .setRoutingKey(RabbitMqEnum.B_SYSTEM_MESSAGE_BROKER.getRoutingKey())
                .setFromId(MessageConstant.SYSTEM_USER_ID)
                .setToId(userId)
                .setData("postId",discussPost.getId());
        messageProducer.sendMessage(event);
        return i;
    }

    @Override
    public int deleteDiscussPost(Long id) {
        Long userId = UserHolder.getUser().getId();
        int i = discussPostMapper.deleteById(id);
        if (i <= 0) {
            Asserts.fail("删除帖子失败");
        }
        // 触发删帖事件，通过消息队列更新 Elasticsearch 服务器
        Event event = new Event()
                .setExchange(RabbitMqEnum.C_SYSTEM_MESSAGE_BROKER.getExchange())
                .setRoutingKey(RabbitMqEnum.C_SYSTEM_MESSAGE_BROKER.getRoutingKey())
                .setFromId(MessageConstant.SYSTEM_USER_ID)
                .setToId(userId)
                .setData("postId", id);
        messageProducer.sendMessage(event);
        return i;
    }

    @Override
    public DiscussPost getDiscussPostById(Long typeId) {
        LambdaQueryWrapper<DiscussPost> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DiscussPost::getId, typeId);
        return discussPostMapper.selectOne(queryWrapper);
    }

}
