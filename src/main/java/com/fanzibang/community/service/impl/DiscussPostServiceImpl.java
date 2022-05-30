package com.fanzibang.community.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DiscussPostServiceImpl implements DiscussPostService {

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


    @Override
    public DiscussPostDetailVo getDiscussPostDetail(Long id) {
        DiscussPostDetailVo postDetailVo = new DiscussPostDetailVo();
        DiscussPost post = getDiscussPostById(id);
        BeanUtil.copyProperties(post, postDetailVo);
        // 作者名称
        String nickname = userService.getById(post.getUserId()).getNickname();
        postDetailVo.setAuthor(nickname);
        // 该用户是否对该帖子点赞
        User user = UserHolder.getUser();
        if (ObjectUtil.isEmpty(user)) {
            postDetailVo.setIsLike(false);
        }else {
            Boolean isLike = likeService.isLike(PostConstant.ENTITY_TYPE_POST, id, user.getId());
            postDetailVo.setIsLike(isLike);
        }
        // 点赞数量
        Long likeCount = likeService.getLikeCount(PostConstant.ENTITY_TYPE_POST,post.getId());
        postDetailVo.setLikeCount(likeCount);
        String plate = plateService.getPlateById(post.getPlateId()).getName();
        postDetailVo.setPlate(plate);
        String createTime = DateUtil.date(post.getCreateTime()).toString("yyyy-MM-dd HH:mm");
        postDetailVo.setCreateTime(createTime);
        return postDetailVo;
    }

    @Override
    public int publishDiscussPost(DiscussPostParam discussPostParam) {
        Long userId = UserHolder.getUser().getId();
        if (ObjectUtil.isEmpty(userId)) {
            Asserts.fail(ReturnCode.RC205);
        }
        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(userId);
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
            Asserts.fail("发布帖子失败");
        }
        // 触发发帖事件，通过消息队列将其存入 Elasticsearch 服务器
        Event event = new Event()
                .setExchange(RabbitMqEnum.B_SYSTEM_MESSAGE_BROKER.getExchange())
                .setRoutingKey(RabbitMqEnum.B_SYSTEM_MESSAGE_BROKER.getRoutingKey())
                .setFromId(MessageConstant.SYSTEM_USER_ID)
                .setToId(userId)
                .setData("postId",discussPost.getId());
        messageProducer.sendMessage(event);

        // 将帖子存入redis，方便后期使用定时任务计算热度分数
        redisService.sAdd(RedisKey.POST_SCORE_KEY, discussPost.getId());
        return i;
    }

    @Override
    public Integer deleteDiscussPost(Long id) {
        Long userId = UserHolder.getUser().getId();
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

}
