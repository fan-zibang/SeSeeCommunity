package com.fanzibang.community;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fanzibang.community.constant.MessageConstant;
import com.fanzibang.community.constant.RabbitMqEnum;
import com.fanzibang.community.constant.RedisKey;
import com.fanzibang.community.mapper.PrivateLetterMapper;
import com.fanzibang.community.mq.MessageProducer;
import com.fanzibang.community.pojo.DiscussPost;
import com.fanzibang.community.pojo.Event;
import com.fanzibang.community.pojo.PrivateLetter;
import com.fanzibang.community.service.DiscussPostService;
import com.fanzibang.community.service.RedisService;
import com.fanzibang.community.utils.JwtTokenUtil;
import com.fanzibang.community.vo.DiscussPostDetailVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
class CommunityApplicationTests {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisService redisService;

    @Autowired
    private PrivateLetterMapper privateLetterMapper;

    @Autowired
    private MessageProducer messageProducer;

    @Test
    void contextLoads() {

    }

    @Test
    void test01() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        System.out.println(bCryptPasswordEncoder.encode("123"));
    }

    @Test
    void test02() {
        String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOjEsImNyZWF0ZWQiOjE2NTI4MDMwMDY3NTUsImV4cCI6MTY1Mjg4OTQwNn0.ue8eZUP7NINXCTLJDYqXUOib3EyccsE5a-rS27fDwxs8ZjQ-gQ9QdI8-q2AlgUSWMxMde4NasTDkLQ39sGlHFw";
        JwtTokenUtil jwtTokenUtil = new JwtTokenUtil();
        System.out.println(jwtTokenUtil.validateToken(token, "1"));
    }

    @Test
    void test03() {
        DiscussPost post = discussPostService.getDiscussPostById(1L);
        DiscussPostDetailVo discussPostDetailVo = new DiscussPostDetailVo();
        BeanUtil.copyProperties(post,discussPostDetailVo);
        System.out.println(discussPostDetailVo);
    }

    @Test
    void test04() {
        DateTime date = DateUtil.date(1653308439974L);
        System.out.println(date.toString("yyyy-MM-dd HH:mm"));
    }

    @Test
    void test05() {
        Integer entityType = 1;
        Long entityId = 1L;
        Long userId = 1L;
        Long entityUserId = 5L;
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
        System.out.println("execute:" + execute);
    }

    @Test
    void test06() {
        // Double score = redisTemplate.opsForZSet().score("user:follower:1", 5);
        Double score = redisService.zScore(RedisKey.USER_FOLLOWER_KEY + 1, 5);
        System.out.println(score);
    }

    @Test
    void test07() {
        Snowflake snowflake = IdUtil.createSnowflake(1,1);
        long id = snowflake.nextId();
        System.out.println(id);
    }

    @Test
    void test08() {
        LambdaQueryWrapper<PrivateLetter> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PrivateLetter::getConversationId, "1_13")
                .orderByAsc(PrivateLetter::getCreateTime);
        List<PrivateLetter> privateLetterList = privateLetterMapper.selectList(queryWrapper);
        System.out.println(privateLetterList);
        List<Long> collect = privateLetterList.stream().filter(letter -> letter.getStatus() == 0 && letter.getToId() == 1)
                .map(PrivateLetter::getId).collect(Collectors.toList());
        System.out.println(collect);

    }

    @Test
    void test09() {
        System.out.println(DateUtil.between(DateUtil.date(1658315763611L), DateUtil.date(System.currentTimeMillis()), DateUnit.DAY));
    }
}
