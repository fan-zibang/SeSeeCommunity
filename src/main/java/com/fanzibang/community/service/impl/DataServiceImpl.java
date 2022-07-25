package com.fanzibang.community.service.impl;

import cn.hutool.core.date.DateUtil;
import com.fanzibang.community.constant.RedisKey;
import com.fanzibang.community.service.DataService;
import com.fanzibang.community.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class DataServiceImpl implements DataService {

    @Autowired
    private RedisService redisService;

    @Override
    public Long setUV(String ip) {
        String date = DateUtil.date(new Date()).toString("yyyy-MM-dd");
        return redisService.pfAdd(RedisKey.DATA_UV_KEY + date, ip);
    }

    @Override
    public Boolean setDAU(Long id) {
        String date = DateUtil.date(new Date()).toString("yyyy-MM-dd");
        return redisService.setBit(RedisKey.DATA_DAU_KEY + date, id, true);
    }

    @Override
    public Long getUV(Date startTime, Date endTime) {
        long count = 0L;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startTime);
        while (!calendar.getTime().after(endTime)) {
            String date = DateUtil.date(calendar.getTime()).toString("yyyy-MM-dd");
            Long n = redisService.pfCount(RedisKey.DATA_UV_KEY + date);
            count += n;
            calendar.add(Calendar.DATE, 1);
        }

        return count;
    }

    @Override
    public Long getDAU(Date startTime, Date endTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startTime);
        List<String> dateList = new ArrayList<>();
        while (!calendar.getTime().after(endTime)) {
            String date = DateUtil.date(calendar.getTime()).toString("yyyy-MM-dd");
            dateList.add(RedisKey.DATA_DAU_KEY + date);
            calendar.add(Calendar.DATE, 1);
        }
        return (Long) redisService.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                long count = 0L;
                for (String key : dateList) {
                    Long n = connection.bitCount(key.getBytes());
                    count += n;
                }
                return count;
            }
        });
    }
}
