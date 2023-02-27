package com.fanzibang.community.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.fanzibang.community.constant.*;
import com.fanzibang.community.exception.ApiException;
import com.fanzibang.community.mq.MessageConsumer;
import com.fanzibang.community.mq.MessageProducer;
import com.fanzibang.community.pojo.Event;
import com.fanzibang.community.pojo.User;
import com.fanzibang.community.service.FollowService;
import com.fanzibang.community.service.RedisService;
import com.fanzibang.community.service.UserService;
import com.fanzibang.community.utils.UserHolder;
import com.fanzibang.community.vo.UserFollowVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FollowServiceImpl implements FollowService {

    @Autowired
    private RedisService redisService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserHolder userHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageProducer messageProducer;


    @Override
    public void follow(Integer entityType, Long entityId) {
        User user = userHolder.getUser();
        Optional.ofNullable(user).orElseThrow(() -> new ApiException(ReturnCode.RC205));
        boolean follow = isFollow(entityType, entityId);
        boolean limit = redisService.sIsMember(RedisKey.FOLLOW_LIMIT_KEY + entityId, user.getId());
        Object execute = redisService.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                if (entityType == EntityTypeConstant.ENTITY_TYPE_USER) {
                    if (!follow) {
                        operations.opsForZSet().add(RedisKey.USER_FOLLOWER_KEY + user.getId(), entityId, System.currentTimeMillis());
                        operations.opsForZSet().add(RedisKey.USER_FANS_KEY + entityId, user.getId(), System.currentTimeMillis());
                        // 一天内不会发送重复关注不会发送关注通知
                        if (!limit) {
                            redisService.sAdd(RedisKey.FOLLOW_LIMIT_KEY + entityId, user.getId());
                            redisService.expire(RedisKey.FOLLOW_LIMIT_KEY + entityId, 1440);
                        }
                    } else {
                        operations.opsForZSet().remove(RedisKey.USER_FOLLOWER_KEY + user.getId(), entityId);
                        operations.opsForZSet().remove(RedisKey.USER_FANS_KEY + entityId, user.getId());
                    }
                }
                if (entityType == EntityTypeConstant.ENTITY_TYPE_TOPIC) {
                    if (!follow) {
                        operations.opsForZSet().add(RedisKey.USER_TOPIC_KEY + user.getId(), entityId, System.currentTimeMillis());
                    } else {
                        operations.opsForZSet().remove(RedisKey.USER_TOPIC_KEY + user.getId(), entityId);
                    }
                }
                return operations.exec();
            }
        });
        Optional.ofNullable(execute).orElseThrow(() -> new ApiException(ReturnCode.RC451));
        if (entityType == EntityTypeConstant.ENTITY_TYPE_USER) {
            if (!follow && !limit) {
                Event event = new Event().setExchange(RabbitMqEnum.A_SYSTEM_MESSAGE_BROKER.getExchange())
                        .setRoutingKey(RabbitMqEnum.A_SYSTEM_MESSAGE_BROKER.getRoutingKey())
                        .setType(MessageConstant.TOPIC_FOLLOW)
                        .setFromId(user.getId())
                        .setToId(entityId);
                messageProducer.sendMessage(event);
            }
        }
    }

    @Override
    public Boolean isFollow(Integer entityType, Long entityId) {
        long userId = userHolder.getUser().getId();
        Double score = null;
        if (entityType == EntityTypeConstant.ENTITY_TYPE_USER) {
            score = redisService.zScore(RedisKey.USER_FOLLOWER_KEY + userId, entityId);
        }
        if (entityType == EntityTypeConstant.ENTITY_TYPE_TOPIC) {
            score = redisService.zScore(RedisKey.USER_TOPIC_KEY + userId, entityId);
        }
        return ObjectUtil.isNotNull(score);
    }

    @Override
    public Long getFollowerCount(Long uid) {
        return getUserFollowCount(EntityTypeConstant.ENTITY_TYPE_USER, uid);
    }

    @Override
    public Long getTopicCount(Long uid) {
        return getUserFollowCount(EntityTypeConstant.ENTITY_TYPE_TOPIC, uid);
    }

    @Override
    public Long getFansCount(Long uid) {
        return redisService.zCard(RedisKey.USER_FANS_KEY + uid);
    }

    public Long getUserFollowCount(Integer entityType, Long userId) {
        Long count = null;
        if (entityType == EntityTypeConstant.ENTITY_TYPE_USER) {
            count = redisService.zCard(RedisKey.USER_FOLLOWER_KEY + userId);
        }
        if (entityType == EntityTypeConstant.ENTITY_TYPE_TOPIC) {
            count = redisService.zCard(RedisKey.USER_TOPIC_KEY + userId);
        }
        return count;
    }

    @Override
    public List<UserFollowVo> getFollowerList(Long uid) {
        long end = getUserFollowCount(EntityTypeConstant.ENTITY_TYPE_USER, uid);
        Set<Integer> targetUserIds = redisTemplate.opsForZSet().reverseRange(RedisKey.USER_FOLLOWER_KEY + uid, 0, end);
        return copyToList(uid, targetUserIds);
    }

    @Override
    public List<UserFollowVo> getFansList(Long uid) {
        long end = getFansCount(uid);
        Set<Integer> targetUserIds = redisTemplate.opsForZSet().reverseRange(RedisKey.USER_FANS_KEY + uid, 0, end);
        return copyToList(uid, targetUserIds);
    }

    private List<UserFollowVo> copyToList(Long uid, Set<Integer> targetUserIds) {
        if (ObjectUtil.isNull(targetUserIds)) {
            return null;
        }
        List<UserFollowVo> userFollowVoList = new ArrayList<>();
        for (Integer targetUserId : targetUserIds) {
            UserFollowVo userFollowVo = new UserFollowVo();
            User targetUser = userService.getById(targetUserId);
            if (ObjectUtil.isNotNull(targetUser)) {
                BeanUtil.copyProperties(targetUser, userFollowVo);
            }
            User currentUser = userHolder.getUser();
            if (ObjectUtil.isNotNull(currentUser)) {
                Boolean follow = isFollow(EntityTypeConstant.ENTITY_TYPE_USER, targetUserId.longValue());
                userFollowVo.setFollow(follow);
            } else {
                userFollowVo.setFollow(false);
            }
            Double score = redisService.zScore(RedisKey.USER_FANS_KEY + uid, targetUserId);
            if (ObjectUtil.isNotNull(score)) {
                userFollowVo.setFollowTime(DateUtil.date(score.longValue()).toString("yyyy-MM-dd HH:mm"));
            }
            userFollowVoList.add(userFollowVo);
        }
        return userFollowVoList;
    }


}
