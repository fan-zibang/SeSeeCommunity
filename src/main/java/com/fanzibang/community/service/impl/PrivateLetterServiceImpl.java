package com.fanzibang.community.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fanzibang.community.constant.ReturnCode;
import com.fanzibang.community.exception.Asserts;
import com.fanzibang.community.mapper.PrivateLetterMapper;
import com.fanzibang.community.pojo.PrivateLetter;
import com.fanzibang.community.pojo.User;
import com.fanzibang.community.service.PrivateLetterService;
import com.fanzibang.community.service.UserService;
import com.fanzibang.community.utils.UserHolder;
import com.fanzibang.community.vo.PrivateLetterListVo;
import com.fanzibang.community.vo.PrivateLetterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PrivateLetterServiceImpl implements PrivateLetterService {

    @Autowired
    private UserHolder userHolder;

    @Autowired
    private PrivateLetterMapper privateLetterMapper;

    @Autowired
    private UserService userService;

    /**
     * 发送一条私信
     * @param toId
     * @param content
     * @return
     */
    @Override
    public Integer sendPrivateLetter(Long toId, String content) {
        PrivateLetter privateLetter = new PrivateLetter();
        long fromId = userHolder.getUser().getId();
        privateLetter.setFromId(fromId);
        privateLetter.setToId(toId);
        if (fromId < toId) {
            privateLetter.setConversationId(fromId + "_" + toId);
        } else {
            privateLetter.setConversationId(toId + "_" + fromId);
        }
        privateLetter.setContent(content);
        privateLetter.setStatus(0); // 未读
        privateLetter.setCreateTime(System.currentTimeMillis());
        int i = privateLetterMapper.insert(privateLetter);
        if (i <= 0) {
            Asserts.fail(ReturnCode.RC601);
        }
        return i;
    }

    /**
     * 获取私信列表（内容为最新的一条）
     * @param current
     * @param size
     * @return
     */
    @Override
    public List<PrivateLetterListVo> getPrivateLetterList(Integer current, Integer size) {
        current = Optional.ofNullable(current).orElse(1);
        size = Optional.ofNullable(size).orElse(40);
        long currentUserId = userHolder.getUser().getId();
        List<PrivateLetter> privateLetterList =
                privateLetterMapper.getPrivateLetterList(current-1, size, currentUserId);
        List<PrivateLetterListVo> privateLetterVoList = new ArrayList<>();
        for (PrivateLetter privateLetter : privateLetterList) {
            PrivateLetterListVo privateLetterVo = new PrivateLetterListVo();
            privateLetterVo.setId(privateLetter.getId());
            Long targetUserId = null;
            if (privateLetter.getFromId() == currentUserId) {
                targetUserId = privateLetter.getToId();
            } else {
                targetUserId = privateLetter.getFromId();
            }
            User user = userService.getById(targetUserId);
            if (ObjectUtil.isNotNull(user)) {
                privateLetterVo.setToId(targetUserId);
                privateLetterVo.setNickname(user.getNickname());
                privateLetterVo.setAvatar(user.getAvatar());
            }
            privateLetterVo.setConversationId(privateLetter.getConversationId());
            privateLetterVo.setContent(privateLetter.getContent());
            privateLetterVo.setUnReadCount(getLetterUnReadCount(currentUserId, privateLetter.getConversationId()));
            privateLetterVoList.add(privateLetterVo);
        }
        return privateLetterVoList;
    }

    /**
     * 获取私信未读数量
     * @param userId
     * @param conversationId 如果为null，获取用户未读私信总数量，否则获取对应会话的未读数量
     * @return
     */
    public Long getLetterUnReadCount(Long userId, String conversationId) {
        LambdaQueryWrapper<PrivateLetter> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PrivateLetter::getToId, userId)
                .eq(PrivateLetter::getStatus, 0);
        if (StrUtil.isNotEmpty(conversationId)) {
            queryWrapper.eq(PrivateLetter::getConversationId, conversationId);
        }
        return privateLetterMapper.selectCount(queryWrapper);
    }

    /**
     * 获取私信详情
     * @param conversationId
     * @return
     */
    @Override
    public List<PrivateLetterVo> getPrivateLetter(String conversationId) {
        LambdaQueryWrapper<PrivateLetter> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PrivateLetter::getConversationId, conversationId)
                .orderByAsc(PrivateLetter::getCreateTime);
        List<PrivateLetter> privateLetterList = privateLetterMapper.selectList(queryWrapper);
        List<PrivateLetterVo> privateLetterVoList = new ArrayList<>();
        for (PrivateLetter privateLetter : privateLetterList) {
            PrivateLetterVo privateLetterVo = new PrivateLetterVo();
            BeanUtil.copyProperties(privateLetter, privateLetterVo);
            privateLetterVoList.add(privateLetterVo);
        }
        // 未读的私信变为已读
        long userId = userHolder.getUser().getId();
        List<Long> unReadLetterList = privateLetterList.stream().filter(letter -> letter.getStatus() == 0 && letter.getToId() == userId)
                .map(PrivateLetter::getId)
                .collect(Collectors.toList());
        if (!unReadLetterList.isEmpty()) {
            privateLetterMapper.updateStatus(unReadLetterList, 1);
        }
        return privateLetterVoList;
    }
}
