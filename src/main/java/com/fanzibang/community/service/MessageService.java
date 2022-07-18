package com.fanzibang.community.service;

import com.fanzibang.community.pojo.Message;

import java.util.List;
import java.util.Map;

public interface MessageService{
    List<Map<String, Object>> getLikeMessageList(Integer current, Integer size);

    List<Map<String, Object>> getSystemMessageList(Integer current, Integer size);

    List<Map<String, Object>> getCommentMessageList(Integer current, Integer size);

    List<Map<String, Object>> getFollowMessageList(Integer current, Integer size);

    Integer addMessage(Message message);

    void publishSystemMessage(String content);
}
