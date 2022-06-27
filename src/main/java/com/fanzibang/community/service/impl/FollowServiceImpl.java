package com.fanzibang.community.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.fanzibang.community.constant.EntityTypeConstant;
import com.fanzibang.community.constant.RedisKey;
import com.fanzibang.community.constant.ReturnCode;
import com.fanzibang.community.exception.ApiException;
import com.fanzibang.community.service.FollowService;
import com.fanzibang.community.service.RedisService;
import com.fanzibang.community.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FollowServiceImpl implements FollowService {

    @Autowired
    private RedisService redisService;

    @Autowired
    private UserHolder userHolder;

    @Override
    public void follow(Integer entityType, Long entityId) {
        Long userId = userHolder.getUser().getId();
        Object execute = redisService.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                Boolean follow = isFollow(entityType, entityId);
                operations.multi();
                if (entityType == EntityTypeConstant.ENTITY_TYPE_USER) {
                    // 如果将isFollow放到execute里执行，score一直为null
                    if (!follow) {
                        operations.opsForZSet().add(RedisKey.USER_FOLLOWER_KEY + userId, entityId, System.currentTimeMillis());
                        operations.opsForZSet().add(RedisKey.USER_FANS_KEY + entityId, userId, System.currentTimeMillis());
                    } else {
                        operations.opsForZSet().remove(RedisKey.USER_FOLLOWER_KEY + userId, entityId);
                        operations.opsForZSet().remove(RedisKey.USER_FANS_KEY + entityId, userId);
                    }
                }
                if (entityType == EntityTypeConstant.ENTITY_TYPE_TOPIC) {
                    if (!follow) {
                        operations.opsForZSet().add(RedisKey.USER_TOPIC_KEY + userId, entityId, System.currentTimeMillis());
                    } else {
                        operations.opsForZSet().remove(RedisKey.USER_TOPIC_KEY + userId, entityId);
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
}
