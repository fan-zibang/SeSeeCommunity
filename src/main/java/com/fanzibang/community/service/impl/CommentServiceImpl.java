package com.fanzibang.community.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fanzibang.community.constant.*;
import com.fanzibang.community.dto.CommentParam;
import com.fanzibang.community.exception.Asserts;
import com.fanzibang.community.mapper.CommentMapper;
import com.fanzibang.community.mapper.DiscussPostMapper;
import com.fanzibang.community.mq.MessageProducer;
import com.fanzibang.community.pojo.*;
import com.fanzibang.community.service.*;
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
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private MessageProducer messageProducer;

    @Autowired
    private RedisService redisService;

    @Autowired
    private UserHolder userHolder;

    @Autowired
    private DiscussPostService discussPostService;

    @Override
    public List<Map<String, Object>> getCommentList(Long postId, Integer current, Integer size) {
        Page<Comment> page = new Page<>(1, 10,false);
        if (!ObjectUtil.isEmpty(current) && !ObjectUtil.isEmpty(size)) {
            page = new Page<>(current, size,false);
        }
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getEntityType, PostConstant.ENTITY_TYPE_POST)
                .eq(Comment::getEntityId, postId)
                .orderByAsc(Comment::getCreateTime);
        List<Comment> commentList = commentMapper.selectPage(page, queryWrapper).getRecords();

        // 封装评论及其相关信息
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if (!ObjectUtil.isNull(commentList)) {
            for (Comment comment : commentList) {
                Map<String, Object> commentVo = new HashMap<>();
                commentVo.put("id", comment.getId());
                User commentUser = userService.getById(comment.getUserId());
                String nickname = !ObjectUtil.isEmpty(commentUser) ? commentUser.getNickname() : null;
                commentVo.put("nickName", nickname);

                commentVo.put("content", comment.getContent());
                commentVo.put("likeCount", likeService.getLikeCount(PostConstant.ENTITY_TYPE_COMMENT, comment.getId()));
                // 该用户是否对该评论点赞
                User user = userHolder.getUser();
                if (ObjectUtil.isEmpty(user)) {
                    commentVo.put("isLike", false);
                }else {
                    Boolean like = likeService.isLike(PostConstant.ENTITY_TYPE_COMMENT, comment.getId(), user.getId());
                    commentVo.put("isLike", like);
                }
                // 封装每个评论对应的回复及其相关信息
                List<Map<String, Object>> replyCommentVoList = new ArrayList<>();
                LambdaQueryWrapper<Comment> replyQueryWrapper = new LambdaQueryWrapper<>();
                replyQueryWrapper.eq(Comment::getEntityType, PostConstant.ENTITY_TYPE_COMMENT)
                        .eq(Comment::getParentId, comment.getId())
                        .orderByAsc(Comment::getCreateTime);
                List<Comment> replyCommentList = commentMapper.selectList(replyQueryWrapper);
                for (Comment replyComment : replyCommentList) {
                    Map<String, Object> replyCommentVo = new HashMap<>();
                    replyCommentVo.put("id", replyComment.getId());

                    User replyCommentUser = userService.getById(replyComment.getUserId());
                    String replyNickName = !ObjectUtil.isEmpty(replyCommentUser) ? replyCommentUser.getNickname() : null;
                    replyCommentVo.put("replyNickName", replyNickName);

                    replyCommentVo.put("content", replyComment.getContent());

                    User targetUser = userService.getById(replyComment.getTargetId());
                    String targetNickName = !ObjectUtil.isEmpty(targetUser) ? targetUser.getNickname() : null;
                    replyCommentVo.put("targetNickName", targetNickName);

                    if (ObjectUtil.isEmpty(user)) {
                        replyCommentVo.put("isLike", false);
                    } else {
                        Boolean like = likeService.isLike(PostConstant.ENTITY_TYPE_COMMENT, replyComment.getId(), user.getId());
                        replyCommentVo.put("isLike", like);
                    }
                    replyCommentVo.put("likeCount", likeService.getLikeCount(PostConstant.ENTITY_TYPE_COMMENT, replyComment.getId()));
                    replyCommentVoList.add(replyCommentVo);
                }
                commentVo.put("replyCommentList",replyCommentVoList);
                commentVoList.add(commentVo);
            }
        }
        return commentVoList;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    @Override
    public Integer addComment(Long postId, CommentParam commentParam) {
        User user = userHolder.getUser();
        if (ObjectUtil.isEmpty(user)) {
            Asserts.fail(ReturnCode.RC205);
        }
        Comment comment = new Comment();
        BeanUtil.copyProperties(commentParam,comment);
        comment.setUserId(user.getId());
        comment.setCreateTime(System.currentTimeMillis());
        Comment targetComment = null;
        if (commentParam.getEntityType() == PostConstant.ENTITY_TYPE_COMMENT) {
            targetComment = commentMapper.selectById(commentParam.getEntityId());
            comment.setTargetId(targetComment.getUserId());
        }
        int i = commentMapper.insert(comment);
        if (i <= 0) {
            Asserts.fail(ReturnCode.RC401);
        }
        LambdaUpdateWrapper<DiscussPost> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.setSql("comment_count = comment_count + 1").eq(DiscussPost::getId,postId);
        discussPostMapper.update(null,updateWrapper);

        // 触发评论事件（系统通知）
        Event event = new Event()
                .setExchange(RabbitMqEnum.A_SYSTEM_MESSAGE_BROKER.getExchange())
                .setRoutingKey(RabbitMqEnum.A_SYSTEM_MESSAGE_BROKER.getRoutingKey())
                .setType(MessageConstant.TOPIC_COMMENT)
                .setFromId(user.getId())
                .setData("entityType", commentParam.getEntityType())
                .setData("entityId", commentParam.getEntityId());
        if (commentParam.getEntityType() == PostConstant.ENTITY_TYPE_POST) {
            DiscussPost discussPost = discussPostService.getDiscussPostById(commentParam.getEntityId());
            event.setToId(discussPost.getUserId());
            // 放入Redis，后期重新更新分数
            redisService.sAdd(RedisKey.POST_SCORE_KEY, commentParam.getEntityId());
        }
        if (commentParam.getEntityType() == PostConstant.ENTITY_TYPE_COMMENT) {
            event.setToId(targetComment.getUserId());
        }
        messageProducer.sendMessage(event);
        return i;
    }
}
