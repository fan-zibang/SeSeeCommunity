package com.fanzibang.community.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.fanzibang.community.constant.*;
import com.fanzibang.community.exception.Asserts;
import com.fanzibang.community.mq.MessageProducer;
import com.fanzibang.community.pojo.Event;
import com.fanzibang.community.service.LikeService;
import com.fanzibang.community.service.RedisService;
import com.fanzibang.community.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class LikeServiceImpl implements LikeService {

    @Autowired
    private RedisService redisService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private MessageProducer messageProducer;

    @Override
    public Boolean isLike(Integer entityType, Long entityId, Long userId) {
        // 1-帖子 2-评论
        String suffixKey = entityType == 1 ? "post:" : "comment:";
        return redisService.sIsMember(RedisKey.LIKE_KEY + suffixKey + entityId, userId);
    }

    @Override
    public Long getLikeCount(Integer entityType, Long entityId) {
        // 1-帖子 2-评论
        String suffixKey = entityType == 1 ? "post:" : "comment:";
        return redisService.sSize(RedisKey.LIKE_KEY + suffixKey + entityId);
    }

    @Override
    public String like(Integer entityType, Long entityId, Long entityUserId) {
        // 用户未登录不能点赞
        if (ObjectUtil.isEmpty(UserHolder.getUser())) {
            Asserts.fail(ReturnCode.RC205);
        }
        Long userId = UserHolder.getUser().getId();
        Object execute = redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                // 1-帖子 2-评论
                String suffixKey = entityType == 1 ? "post:" : "comment:";
                boolean isLike = redisService.sIsMember(RedisKey.LIKE_KEY + suffixKey + entityId, userId);
                // 开启事务
                operations.multi();
                if (isLike) {
                    // 如果已经点赞，取消点赞
                    redisService.sRemove(RedisKey.LIKE_KEY + suffixKey + entityId, userId);
                    redisService.decr(RedisKey.USER_LIKE_KEY + entityUserId, 1L);
                } else {
                    redisService.sAdd(RedisKey.LIKE_KEY + suffixKey + entityId, userId);
                    redisService.incr(RedisKey.USER_LIKE_KEY + entityUserId, 1L);
                    // 触发点赞事件
                    Event event = new Event()
                            .setExchange(RabbitMqEnum.A_SYSTEM_MESSAGE_BROKER.getExchange())
                            .setRoutingKey(RabbitMqEnum.A_SYSTEM_MESSAGE_BROKER.getRoutingKey())
                            .setType(MessageConstant.TOPIC_LIKE)
                            .setFromId(userId)
                            .setToId(entityUserId)
                            .setData("entityType", entityType)
                            .setData("entityId", entityId);
                    messageProducer.sendMessage(event);
                }
                return operations.exec();
            }
        });
        if (ObjectUtil.isEmpty(execute)) {
            Asserts.fail("点赞失败");
        }
        if (entityType == PostConstant.ENTITY_TYPE_POST) {
            // 将帖子存入redis，方便后期使用定时任务计算热度分数
            redisService.sAdd(RedisKey.POST_SCORE_KEY, entityId);
        }
        return "点赞成功";
    }


}
