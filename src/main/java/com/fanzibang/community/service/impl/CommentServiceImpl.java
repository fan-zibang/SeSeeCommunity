package com.fanzibang.community.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fanzibang.community.constant.*;
import com.fanzibang.community.dto.CommentParam;
import com.fanzibang.community.exception.Asserts;
import com.fanzibang.community.mapper.CommentMapper;
import com.fanzibang.community.mq.MessageProducer;
import com.fanzibang.community.pojo.Comment;
import com.fanzibang.community.pojo.Event;
import com.fanzibang.community.pojo.Message;
import com.fanzibang.community.pojo.User;
import com.fanzibang.community.service.CommentService;
import com.fanzibang.community.service.LikeService;
import com.fanzibang.community.service.RedisService;
import com.fanzibang.community.service.UserService;
import com.fanzibang.community.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private MessageProducer messageProducer;

    @Autowired
    private RedisService redisService;

    @Override
    public List<Map<String, Object>> getCommentList(Long postId, Integer current, Integer size) {
        Page<Comment> page = new Page<>(current, size);
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getEntityType, PostConstant.ENTITY_TYPE_POST)
                .eq(Comment::getEntityId, postId)
                .orderByAsc(Comment::getCreateTime);
        IPage<Comment> commentIPage = commentMapper.selectPage(page, queryWrapper);
        List<Comment> commentList = commentIPage.getRecords();

        // 封装评论及其相关信息
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if (commentList != null) {
            for (Comment comment : commentList) {
                HashMap<String, Object> commentVo = new HashMap<>();
                commentVo.put("user", userService.getById(comment.getUserId()).getNickname());
                commentVo.put("content", comment.getContent());
                commentVo.put("likeCount", likeService.getLikeCount(PostConstant.ENTITY_TYPE_COMMENT, comment.getId()));
                // 该用户是否对该帖子点赞
                User user = UserHolder.getUser();
                if (ObjectUtil.isEmpty(user)) {
                    commentVo.put("isLike", false);
                }else {
                    commentVo.put("isLike", likeService.isLike(PostConstant.ENTITY_TYPE_COMMENT, comment.getId(), user.getId()));
                }
                // 封装每个评论对应的回复及其相关信息
                List<Map<String, Object>> replyCommentVoList = new ArrayList<>();
                queryWrapper.eq(Comment::getEntityType, PostConstant.ENTITY_TYPE_COMMENT)
                        .eq(Comment::getEntityId, comment.getId())
                        .orderByAsc(Comment::getCreateTime);
                List<Comment> replyCommentList = commentMapper.selectList(queryWrapper);
                for (Comment replyComment : replyCommentList) {
                    HashMap<String, Object> replyCommentVo = new HashMap<>();
                    replyCommentVo.put("user", userService.getById(replyComment.getUserId()).getNickname()); // 发布该回复的作者
                    replyCommentVo.put("content", replyComment.getContent());
                    replyCommentVo.put("target", userService.getById(replyComment.getTargetId()).getNickname()); // 该回复的目标作者
                    if (ObjectUtil.isEmpty(user)) {
                        replyCommentVo.put("isLike", false);
                    }else {
                        commentVo.put("isLike", likeService.isLike(PostConstant.ENTITY_TYPE_COMMENT, replyComment.getId(), user.getId()));
                    }
                    replyCommentVo.put("likeCount", likeService.getLikeCount(PostConstant.ENTITY_TYPE_COMMENT, replyComment.getId()));
                    replyCommentVoList.add(replyCommentVo);
                }
                commentVoList.add(commentVo);
            }
        }

        return commentVoList;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    @Override
    public Integer addComment(CommentParam commentParam) {
        Comment comment = new Comment();
        BeanUtil.copyProperties(commentParam,comment);
        User user = UserHolder.getUser();
        if (ObjectUtil.isEmpty(user)) {
            Asserts.fail(ReturnCode.RC205);
        }
        comment.setUserId(user.getId());
        comment.setCreateTime(System.currentTimeMillis());
        int i = commentMapper.insert(comment);
        if (i <= 0) {
            Asserts.fail("评论失败");
        }
        // 触发评论事件（系统通知）
        Event event = new Event()
                .setExchange(RabbitMqEnum.A_SYSTEM_MESSAGE_BROKER.getExchange())
                .setRoutingKey(RabbitMqEnum.A_SYSTEM_MESSAGE_BROKER.getRoutingKey())
                .setType(MessageConstant.TOPIC_COMMENT)
                .setFromId(user.getId());
        if (commentParam.getEntityType() == PostConstant.ENTITY_TYPE_POST) {
            event.setToId(commentParam.getPostUserId())
            .setData("postId", commentParam.getPostId());
            // 放入Redis，后期重新更新分数
            redisService.sAdd(RedisKey.POST_SCORE_KEY, commentParam.getPostId());
        }
        if (commentParam.getEntityType() == PostConstant.ENTITY_TYPE_COMMENT) {
            event.setToId(commentParam.getTargetId())
            .setData("commentId", commentParam.getEntityId());
        }
        messageProducer.sendMessage(event);
        return i;
    }
}
