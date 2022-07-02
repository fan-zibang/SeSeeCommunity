package com.fanzibang.community.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.fanzibang.community.constant.*;
import com.fanzibang.community.exception.ApiException;
import com.fanzibang.community.mapper.CommentMapper;
import com.fanzibang.community.mapper.DiscussPostMapper;
import com.fanzibang.community.mq.MessageProducer;
import com.fanzibang.community.pojo.Comment;
import com.fanzibang.community.pojo.DiscussPost;
import com.fanzibang.community.pojo.Event;
import com.fanzibang.community.service.CommentService;
import com.fanzibang.community.service.DiscussPostService;
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
    private DiscussPostService discussPostService;

    @Autowired
    private CommentService commentService;

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
    public void like(Long postId, Integer entityType, Long entityId) {
        Long userId = userHolder.getUser().getId();
        String suffixKey = null;
        Long entityUserId = null;
        if (entityType == EntityTypeConstant.ENTITY_TYPE_POST) {
            suffixKey = "post:";
            DiscussPost discussPost = discussPostService.getDiscussPostById(entityId);
            Optional.ofNullable(discussPost).orElseThrow(() -> new ApiException(ReturnCode.RC301));
            entityUserId = discussPost.getUserId();
        }
        if (entityType == EntityTypeConstant.ENTITY_TYPE_COMMENT) {
            suffixKey = "comment:";
            Comment comment = commentService.getCommentById(entityId);
            Optional.ofNullable(comment).orElseThrow(() -> new ApiException(ReturnCode.RC402));
            entityUserId = comment.getUserId();
        }
        // 1-帖子 2-评论
        boolean isLike = redisService.sIsMember(RedisKey.LIKE_KEY + suffixKey + entityId, userId);
        boolean isLimit = redisService.sIsMember(RedisKey.LIKE_LIMIT_KEY + suffixKey + entityId, userId);
        String finalSuffixKey = suffixKey;
        Long finalEntityUserId = entityUserId;
        Object execute = redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                // 开启事务
                operations.multi();
                if (isLike) {
                    // 如果已经点赞，取消点赞
                    redisService.sRemove(RedisKey.LIKE_KEY + finalSuffixKey + entityId, userId);
                    redisService.decr(RedisKey.USER_LIKE_KEY + finalEntityUserId, 1L);
                } else {
                    redisService.sAdd(RedisKey.LIKE_KEY + finalSuffixKey + entityId, userId);
                    redisService.incr(RedisKey.USER_LIKE_KEY + finalEntityUserId, 1L);
                    if (!isLimit) {
                        // 一天内不会发送重复点赞不会发送点赞通知
                        redisService.sAdd(RedisKey.LIKE_LIMIT_KEY + finalSuffixKey + entityId, userId);
                        redisService.expire(RedisKey.LIKE_LIMIT_KEY + finalSuffixKey + entityId, 1440);
                    }
                }
                return operations.exec();
            }
        });
        Optional.ofNullable(execute).orElseThrow(() -> new ApiException(ReturnCode.RC450));
        if (!isLike && !isLimit) {
            // 触发点赞通知事件
            Event event = new Event()
                    .setExchange(RabbitMqEnum.A_SYSTEM_MESSAGE_BROKER.getExchange())
                    .setRoutingKey(RabbitMqEnum.A_SYSTEM_MESSAGE_BROKER.getRoutingKey())
                    .setType(MessageConstant.TOPIC_LIKE)
                    .setFromId(userId)
                    .setToId(entityUserId)
                    .setData("entityType", entityType)
                    .setData("postId", postId);
            if (entityType == EntityTypeConstant.ENTITY_TYPE_COMMENT) {
                event.setData("commentId", entityId);
            }
            messageProducer.sendMessage(event);
        }

        if (entityType == EntityTypeConstant.ENTITY_TYPE_POST) {
            // 将帖子存入redis，方便后期使用定时任务计算热度分数
            redisService.sAdd(RedisKey.POST_SCORE_KEY, entityId);
        }
    }


}
