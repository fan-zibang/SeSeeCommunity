package com.fanzibang.community.service.impl;

import com.fanzibang.community.mapper.TopicMapper;
import com.fanzibang.community.pojo.Topic;
import com.fanzibang.community.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TopicServiceImpl implements TopicService {

    @Autowired
    private TopicMapper topicMapper;

    @Override
    public Topic getTopicById(Integer plateId) {
        return topicMapper.selectById(plateId);
    }

}
