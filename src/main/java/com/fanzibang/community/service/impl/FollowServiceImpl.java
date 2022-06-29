package com.fanzibang.community.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.fanzibang.community.constant.EntityTypeConstant;
import com.fanzibang.community.constant.RedisKey;
import com.fanzibang.community.constant.ReturnCode;
import com.fanzibang.community.exception.ApiException;
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


    @Override
    public void follow(Integer entityType, Long entityId) {
        User user = userHolder.getUser();
        Optional.ofNullable(user).orElseThrow(() -> new ApiException(ReturnCode.RC205));
        Object execute = redisService.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                Boolean follow = isFollow(entityType, entityId);
                operations.multi();
                if (entityType == EntityTypeConstant.ENTITY_TYPE_USER) {
                    // 如果将isFollow放到execute里执行，score一直为null
                    if (!follow) {
                        operations.opsForZSet().add(RedisKey.USER_FOLLOWER_KEY + user.getId(), entityId, System.currentTimeMillis());
                        operations.opsForZSet().add(RedisKey.USER_FANS_KEY + entityId, user.getId(), System.currentTimeMillis());
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
    }

    public Boolean isFollow(Integer entityType, Long entityId) {
        Long userId = userHolder.getUser().getId();
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
        Long end = getUserFollowCount(EntityTypeConstant.ENTITY_TYPE_USER, uid);
        Set<Integer> targetUserIds = redisTemplate.opsForZSet().reverseRange(RedisKey.USER_FOLLOWER_KEY + uid, 0, end);
        return copyToList(uid, targetUserIds);
    }

    @Override
    public List<UserFollowVo> getFansList(Long uid) {
        Long end = getFansCount(uid);
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
            if (!ObjectUtil.isNull(targetUser)) {
                BeanUtil.copyProperties(targetUser, userFollowVo);
            }
            User currentUser = userHolder.getUser();
            if (!ObjectUtil.isNull(currentUser)) {
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
