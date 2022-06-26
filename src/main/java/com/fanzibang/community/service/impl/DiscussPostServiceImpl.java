package com.fanzibang.community.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fanzibang.community.constant.*;
import com.fanzibang.community.dto.DiscussPostParam;
import com.fanzibang.community.exception.Asserts;
import com.fanzibang.community.mapper.DiscussPostMapper;
import com.fanzibang.community.mq.MessageProducer;
import com.fanzibang.community.pojo.DiscussPost;
import com.fanzibang.community.pojo.Event;
import com.fanzibang.community.pojo.Plate;
import com.fanzibang.community.pojo.User;
import com.fanzibang.community.service.*;
import com.fanzibang.community.utils.UserHolder;
import com.fanzibang.community.vo.DiscussPostDetailVo;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class DiscussPostServiceImpl implements DiscussPostService {

    private static final Logger logger = LoggerFactory.getLogger(DiscussPostServiceImpl.class);

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private MessageProducer messageProducer;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private PlateService plateService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private UserHolder userHolder;

    @Value("${caffeine.discussPost.max-size}")
    private int maxSize;

    @Value("${caffeine.discussPost.expire-seconds}")
    private int expireSeconds;

    // 热帖列表的本地缓存
    private LoadingCache<String, List<DiscussPostDetailVo>> postListCache;

    // 热帖列表总数缓存
    private LoadingCache<Long, Long> postTotalCache;

    /**
     * 初始化本地热帖缓存
     */
    @PostConstruct
    public void init() {
        postListCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<String, List<DiscussPostDetailVo>>() {
                    @Override
                    public @Nullable List<DiscussPostDetailVo> load(@NonNull String key) throws Exception {
                        if (StrUtil.isEmpty(key)) {
                            throw new IllegalArgumentException("本地缓存参数为空");
                        }
                        String[] params = key.split(":");
                        if (ObjectUtil.isNull(params) || params.length != 2) {
                            throw new IllegalArgumentException("本地缓存参数错误");
                        }
                        logger.debug("本地热帖缓存初始化");
                        int current = Integer.valueOf(params[0]);
                        int size = Integer.valueOf(params[1]);
                        Page<DiscussPost> page = new Page<>(current, size, false);
                        LambdaQueryWrapper<DiscussPost> queryWrapper = new LambdaQueryWrapper<>();
                        queryWrapper.orderByDesc(DiscussPost::getScore, DiscussPost::getCreateTime);
                        List<DiscussPost> discussPostList = discussPostMapper.selectPage(page, queryWrapper).getRecords();
                        return copyList(discussPostList);
                    }
                });

        postTotalCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<Long, Long>() {
                    @Override
                    public @Nullable Long load(@NonNull Long userId) throws Exception {
                        return discussPostMapper.selectCount(null);
                    }
                });
    }

    @Override
    public List<DiscussPostDetailVo> getDiscussPostList(Long userId, Integer current, Integer size, Integer mode) {
        // mode 1-热度 2-最新 userId=0查询所有用户
        if (userId == 0 && mode == 1) {
             return postListCache.get(current + ":" + size);
        }
        Page<DiscussPost> page = new Page<>(1, 10, false); // 默认 current-1，size-10
        if (!ObjectUtil.isEmpty(current) && !ObjectUtil.isEmpty(size)) {
            page.setCurrent(current).setSize(size).setSearchCount(false);
        }
        LambdaQueryWrapper<DiscussPost> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(DiscussPost::getCreateTime);
        if (userId != 0) {
            queryWrapper.eq(DiscussPost::getUserId, userId);
        }
        List<DiscussPost> discussPostList = discussPostMapper.selectPage(page, queryWrapper).getRecords();
        return copyList(discussPostList);
    }

    /**
     * 查询帖子总数
     * @param userId 当传入的 userId = 0 时计算所有用户的帖子总数
     *               当传入的 userId ！= 0 时计算该指定用户的帖子总数
     * @return
     */
    @Override
    public Long getDiscussPostCount(Long userId) {
        // 只对查询所有用户的的帖子总数缓存
        if (userId == 0) {
            return postTotalCache.get(userId);
        }
        LambdaQueryWrapper<DiscussPost> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DiscussPost::getUserId, userId);
        return discussPostMapper.selectCount(queryWrapper);
    }

    @Override
    public DiscussPostDetailVo getDiscussPostDetail(Long id) {
        DiscussPost post = getDiscussPostById(id);
        if (ObjectUtil.isEmpty(post)) {
            Asserts.fail(ReturnCode.RC301);
        }
        DiscussPostDetailVo discussPostDetailVo = copy(post,2);
        // 该用户是否对该帖子点赞
        User user = userHolder.getUser();
        if (ObjectUtil.isEmpty(user)) {
            discussPostDetailVo.setIsLike(false);
        }else {
            Boolean isLike = likeService.isLike(PostConstant.ENTITY_TYPE_POST, id, user.getId());
            discussPostDetailVo.setIsLike(isLike);
        }
        return discussPostDetailVo;
    }

    @Override
    public int publishDiscussPost(DiscussPostParam discussPostParam) {
        User user = userHolder.getUser();
        if (ObjectUtil.isEmpty(user)) {
            Asserts.fail(ReturnCode.RC205);
        }
        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(user.getId());
        discussPost.setTitle(discussPostParam.getTitle());
        discussPost.setContent(discussPostParam.getContent());
        discussPost.setType((byte) 0);
        discussPost.setStatus((byte) 0);
        discussPost.setCommentCount(0L);
        discussPost.setScore(0D);
        discussPost.setPlateId(discussPostParam.getPlateId());
        discussPost.setCreateTime(System.currentTimeMillis());
        int i = discussPostMapper.insert(discussPost);
        if (i <= 0) {
            Asserts.fail(ReturnCode.RC302);
        }
        // 触发发帖事件，通过消息队列将其存入 Elasticsearch 服务器
        Event event = new Event()
                .setExchange(RabbitMqEnum.B_SYSTEM_MESSAGE_BROKER.getExchange())
                .setRoutingKey(RabbitMqEnum.B_SYSTEM_MESSAGE_BROKER.getRoutingKey())
                .setFromId(MessageConstant.SYSTEM_USER_ID)
                .setToId(user.getId())
                .setData("postId",discussPost.getId());
        messageProducer.sendMessage(event);

        // 将帖子存入redis，方便后期使用定时任务计算热度分数
        redisService.sAdd(RedisKey.POST_SCORE_KEY, discussPost.getId());
        return i;
    }

    @Override
    public Integer deleteDiscussPost(Long id) {
        Long userId = userHolder.getUser().getId();
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
    public DiscussPost getDiscussPostById(Long postId) {
        LambdaQueryWrapper<DiscussPost> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DiscussPost::getId, postId);
        return discussPostMapper.selectOne(queryWrapper);
    }

    private List<DiscussPostDetailVo> copyList(List<DiscussPost> discussPostList) {
        ArrayList<DiscussPostDetailVo> discussPostDetailVoList = new ArrayList<>();
        for (DiscussPost discussPost : discussPostList) {
            DiscussPostDetailVo discussPostDetailVo = copy(discussPost,1);
            discussPostDetailVoList.add(discussPostDetailVo);
        }
        return discussPostDetailVoList;
    }

    /**
     *
     * @param discussPost
     * @param mode 1-封装首页帖子列表，2-封装帖子详情页
     * @return
     */
    private DiscussPostDetailVo copy(DiscussPost discussPost, Integer mode) {
        DiscussPostDetailVo discussPostDetailVo = new DiscussPostDetailVo();
        switch (mode) {
            case 1: BeanUtil.copyProperties(discussPost, discussPostDetailVo, "content"); break;
            case 2: BeanUtil.copyProperties(discussPost, discussPostDetailVo); break;
        }
        User user = userService.getById(discussPost.getUserId());
        if (!ObjectUtil.isEmpty(user)) {
            discussPostDetailVo.setAuthor(user.getNickname());
        }
        Long likeCount = likeService.getLikeCount(PostConstant.ENTITY_TYPE_POST, discussPost.getId());
        discussPostDetailVo.setLikeCount(likeCount);
        Plate plate = plateService.getPlateById(discussPost.getPlateId());
        if (!ObjectUtil.isEmpty(plate)) {
            discussPostDetailVo.setPlate(plate.getName());
        }
        String createTime = DateUtil.date(discussPost.getCreateTime()).toString("yyyy-MM-dd HH:mm");
        discussPostDetailVo.setCreateTime(createTime);
        return discussPostDetailVo;
    }

}
