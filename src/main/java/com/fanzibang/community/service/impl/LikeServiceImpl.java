package com.fanzibang.community.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.fanzibang.community.constant.*;
import com.fanzibang.community.exception.ApiException;
import com.fanzibang.community.exception.Asserts;
import com.fanzibang.community.mapper.CommentMapper;
import com.fanzibang.community.mapper.DiscussPostMapper;
import com.fanzibang.community.mq.MessageProducer;
import com.fanzibang.community.pojo.Comment;
import com.fanzibang.community.pojo.DiscussPost;
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

import java.util.Optional;

@Service
public class LikeServiceImpl implements LikeService {

    @Autowired
    private UserHolder userHolder;

    @Autowired
    private RedisService redisService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private MessageProducer messageProducer;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private CommentMapper commentMapper;

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
    public void like(Integer entityType, Long entityId) {
        /**
         * 是否有必要在点赞前判断该实体是否存在和对应的实体作者是否对应实体
         * 防止别人恶意点赞不存在的帖子和评论
         * 但是多了查询的耗时，是否有更好的解决办法
         */
        Long userId = userHolder.getUser().getId();
        Object execute = redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String suffixKey = null;
                Long entityUserId = null;
                if (entityType == PostConstant.ENTITY_TYPE_POST) {
                    suffixKey = "post:";
                    DiscussPost discussPost = discussPostMapper.selectById(entityId);
                    Optional.ofNullable(discussPost).orElseThrow(() -> new ApiException(ReturnCode.RC301));
                    entityUserId = discussPost.getUserId();
                }
                if (entityType == PostConstant.ENTITY_TYPE_COMMENT) {
                    suffixKey = "comment:";
                    Comment comment = commentMapper.selectById(entityId);
                    Optional.ofNullable(comment).orElseThrow(() -> new ApiException(ReturnCode.RC402));
                    entityUserId = comment.getUserId();
                }
                // 1-帖子 2-评论
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
        Optional.ofNullable(execute).orElseThrow(() -> new ApiException(ReturnCode.RC450));
        if (entityType == PostConstant.ENTITY_TYPE_POST) {
            // 将帖子存入redis，方便后期使用定时任务计算热度分数
            redisService.sAdd(RedisKey.POST_SCORE_KEY, entityId);
        }
    }


}
