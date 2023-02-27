package com.fanzibang.community.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fanzibang.community.constant.*;
import com.fanzibang.community.dto.DiscussPostParam;
import com.fanzibang.community.exception.ApiException;
import com.fanzibang.community.exception.Asserts;
import com.fanzibang.community.mapper.DiscussPostMapper;
import com.fanzibang.community.mq.MessageProducer;
import com.fanzibang.community.pojo.DiscussPost;
import com.fanzibang.community.pojo.Event;
import com.fanzibang.community.pojo.Topic;
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
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    private TopicService topicService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private FollowService followService;

    @Autowired
    private EsDiscussPostService esDiscussPostService;

    @Autowired
    private UserHolder userHolder;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${caffeine.discussPost.max-size}")
    private int maxSize;

    @Value("${caffeine.discussPost.expire}")
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
                .expireAfterWrite(expireSeconds, TimeUnit.MINUTES)
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

    /**
     * @param userId
     * @param current
     * @param size
     * @param mode 1-热度 2-最新 userId = 0 查询所有用户
     * @return
     */
    @Override
    public List<DiscussPostDetailVo> getDiscussPostList(Long userId, Integer current, Integer size, Integer mode) {
        current = Optional.ofNullable(current).orElse(1);
        size = Optional.ofNullable(size).orElse(20);
        if (userId == 0 && mode == 1) {
            return postListCache.get(current + ":" + size);
        }
        Page<DiscussPost> page = new Page<>(current, size, false); // 默认 current-1，size-20
        LambdaQueryWrapper<DiscussPost> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DiscussPost::getStatus, 0)
                .orderByDesc(DiscussPost::getCreateTime);
        if (userId != 0) {
            queryWrapper.eq(DiscussPost::getUserId, userId);
        }
        List<DiscussPost> discussPostList = discussPostMapper.selectPage(page, queryWrapper).getRecords();
        return copyList(discussPostList);
    }

    @Override
    public List<DiscussPostDetailVo> getDiscussPostBlockList(Long userId, Integer current, Integer size) {
        current = Optional.ofNullable(current).orElse(1);
        size = Optional.ofNullable(size).orElse(20);
        Page<DiscussPost> page = new Page<>(current, size, false); // 默认 current-1，size-20
        LambdaQueryWrapper<DiscussPost> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DiscussPost::getStatus, 1)
                .orderByDesc(DiscussPost::getCreateTime);
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

    /**
     * 获取帖子详情
     * @param id
     * @return
     */
    @Override
    public DiscussPostDetailVo getDiscussPostDetail(Long id) {
        DiscussPost post = getDiscussPostById(id);
        if (ObjectUtil.isNull(post)) {
            Asserts.fail(ReturnCode.RC301);
        }
        DiscussPostDetailVo discussPostDetailVo = copy(post,2);
        // 该用户是否对该帖子点赞
        User user = userHolder.getUser();
        if (ObjectUtil.isNull(user)) {
            discussPostDetailVo.setIsLike(false);
        }else {
            Boolean isLike = likeService.isLike(EntityTypeConstant.ENTITY_TYPE_POST, id, user.getId());
            discussPostDetailVo.setIsLike(isLike);
        }
        return discussPostDetailVo;
    }

    @Override
    public int publishDiscussPost(DiscussPostParam discussPostParam) {
        User user = userHolder.getUser();
        if (ObjectUtil.isNull(user)) {
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
        discussPost.setTopicId(discussPostParam.getTopicId());
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
        DiscussPost discussPost = discussPostMapper.selectById(id);
        Optional.ofNullable(discussPost).orElseThrow(() -> new ApiException(ReturnCode.RC301));
        User user = userHolder.getUser();
        Optional.ofNullable(user).orElseThrow(() -> new ApiException(ReturnCode.RC205));
        if (discussPost.getUserId() != user.getId()) {
            Asserts.fail(ReturnCode.RC305);
        }
        int i = discussPostMapper.deleteById(id);
        if (i <= 0) {
            Asserts.fail(ReturnCode.RC305);
        }
        // 触发删帖事件，通过消息队列更新 Elasticsearch 服务器
        Event event = new Event()
                .setExchange(RabbitMqEnum.C_SYSTEM_MESSAGE_BROKER.getExchange())
                .setRoutingKey(RabbitMqEnum.C_SYSTEM_MESSAGE_BROKER.getRoutingKey())
                .setFromId(MessageConstant.SYSTEM_USER_ID)
                .setToId(user.getId())
                .setData("postId", id);
        messageProducer.sendMessage(event);
        return i;
    }

    @Override
    public DiscussPost getDiscussPostById(Long postId) {
        return discussPostMapper.selectById(postId);
    }

    @Override
    public void refreshPostScore() {
        BoundSetOperations operations = redisTemplate.boundSetOps(RedisKey.POST_SCORE_KEY);
        if (operations.size() == 0) {
            logger.info("任务取消，没有需要刷新的任务");
        }
        while(operations.size() > 0) {
            Long postId = Long.valueOf(operations.pop().toString());
            DiscussPost discussPost = this.getDiscussPostById(postId);
            if (ObjectUtil.isNull(discussPost)) {
                logger.error("该帖子不存在：{}", postId);
                return ;
            }
            boolean isWonderful = discussPost.getType() == 1;
            long commentCount = discussPost.getCommentCount();
            long likeCount = likeService.getLikeCount(EntityTypeConstant.ENTITY_TYPE_POST, postId);
            long userLikeCount = likeService.getUserLikeCount(discussPost.getUserId());
            long fansCount = followService.getFansCount(discussPost.getUserId());
            /**
             *  score = H + I / (T + 1) ^ G
             *  帖子的热度（分数）= 内容的质量 + 初始质量 / (文章发布以来的时长 + 1) ^ 衰减参数
             *  内容的质量：一个帖子的是否是精华帖、评论数、点赞数加权求和的数值
             *  初始质量：与作者的影响力有关，作者的影响力和他的粉丝量和获得总点赞量有关
             *  文章发布以来的时长：文章的热度应该随着时间推移慢慢降低
             *  衰减参数：决定热度随时间降低的快慢
             */
            double h = (isWonderful ? 60 : 0) + commentCount * 25 + likeCount * 15;
            double i = fansCount * 60 + userLikeCount * 40;
            long t = DateUtil.between(DateUtil.date(discussPost.getCreateTime()), DateUtil.date(System.currentTimeMillis()), DateUnit.DAY);
            double score = (h + i) / Math.pow((t + 1), 1.2);
            logger.info("帖子：{}，分数：{}",discussPost.getId(), score);
            // 更新数据库帖子分数
            LambdaUpdateWrapper<DiscussPost> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(DiscussPost::getScore, score).eq(DiscussPost::getId, discussPost.getId());
            discussPostMapper.update(null,updateWrapper);
            // 更新es服务器数据
            discussPost.setScore(score);
            esDiscussPostService.save(discussPost);
        }

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
        if (ObjectUtil.isNotNull(user)) {
            discussPostDetailVo.setAuthorId(user.getId());
            discussPostDetailVo.setAuthor(user.getNickname());
        }
        boolean essence = discussPost.getType() == 1 ? true : false;
        discussPostDetailVo.setIsEssence(essence);
        long likeCount = likeService.getLikeCount(EntityTypeConstant.ENTITY_TYPE_POST, discussPost.getId());
        discussPostDetailVo.setLikeCount(likeCount);
        Topic topic = topicService.getTopicById(discussPost.getTopicId());
        if (ObjectUtil.isNotNull(topic)) {
            discussPostDetailVo.setTopic(topic.getName());
        }
        String createTime = DateUtil.date(discussPost.getCreateTime()).toString("yyyy-MM-dd HH:mm");
        discussPostDetailVo.setCreateTime(createTime);
        return discussPostDetailVo;
    }

    /**
     * 加精帖子
     * @param postId
     * @param mode 操作：0-取消加精 1-加精
     * @return
     */
    @Override
    public Integer setEssence(Long postId, Integer mode) {
        int i = 0;
        if (mode == 0) {
            i = updateDiscussPostType(StatusConstant.POST_TYPE_NORMAL, postId);
        }
        if (mode == 1) {
            i = updateDiscussPostType(StatusConstant.POST_TYPE_ESSENCE, postId);
        }
        if (i <= 0) {
            Asserts.fail(ReturnCode.RC303);
        }
        return i;
    }

    /**
     * 拉黑帖子
     * @param postId
     * @param mode 操作：0-取消拉黑 1-拉黑
     * @return
     */
    @Override
    public Integer setBlock(Long postId, Integer mode) {
        int i = 0;
        if (mode == 0) {
            i = updateDiscussPostStatus(StatusConstant.POST_STATUS_NORMAL, postId);
        }
        if (mode == 1) {
            i = updateDiscussPostStatus(StatusConstant.POST_STATUS_BLOCK, postId);
        }
        if (i <= 0) {
            Asserts.fail(ReturnCode.RC304);
        }
        return i;
    }

    private Integer updateDiscussPostStatus(Integer status, Long postId) {
        LambdaUpdateWrapper<DiscussPost> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(DiscussPost::getStatus, status).eq(DiscussPost::getId, postId);
        return discussPostMapper.update(null, updateWrapper);
    }

    private Integer updateDiscussPostType(Integer type, Long postId) {
        LambdaUpdateWrapper<DiscussPost> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(DiscussPost::getType, type).eq(DiscussPost::getId, postId);
        return discussPostMapper.update(null, updateWrapper);
    }
}
