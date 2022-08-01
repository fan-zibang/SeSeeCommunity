package com.fanzibang.community.service;

import com.fanzibang.community.pojo.Topic;

import java.util.List;
import java.util.Map;

public interface TopicService {
    Topic getTopicById(Integer topicId);

    Map<String, Object> getTopicPageList(Integer current, Integer size);

    int addTopic(Topic topic);

    int editTopic(Integer topicId, Topic topic);

    int deleteTopic(Integer topicId);

    List<Topic> getTopicList();

}
