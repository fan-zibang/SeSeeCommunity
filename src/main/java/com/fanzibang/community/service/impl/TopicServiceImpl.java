package com.fanzibang.community.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fanzibang.community.constant.ReturnCode;
import com.fanzibang.community.exception.Asserts;
import com.fanzibang.community.mapper.TopicMapper;
import com.fanzibang.community.pojo.Role;
import com.fanzibang.community.pojo.Topic;
import com.fanzibang.community.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TopicServiceImpl implements TopicService {

    @Autowired
    private TopicMapper topicMapper;

    @Override
    public Topic getTopicById(Integer plateId) {
        return topicMapper.selectById(plateId);
    }

    @Override
    public List<Topic> getTopicList() {
        return topicMapper.selectList(null);
    }

    @Override
    public Map<String, Object> getTopicPageList(Integer current, Integer size) {
        current = Optional.ofNullable(current).orElse(1);
        size = Optional.ofNullable(size).orElse(20);
        Page<Topic> page = new Page<>(current, size);
        Page<Topic> topicPage = topicMapper.selectPage(page, null);
        Map<String, Object> map = new HashMap<>();
        map.put("topicList", topicPage.getRecords());
        map.put("total", topicPage.getTotal());
        return map;
    }

    @Override
    public int addTopic(Topic topic) {
        topic.setCreateTime(System.currentTimeMillis());
        int i = topicMapper.insert(topic);
        if (i <= 0) {
            Asserts.fail(ReturnCode.RC751);
        }
        return i;
    }

    @Override
    public int editTopic(Integer topicId, Topic topic) {
        topic.setId(topicId);
        int i = topicMapper.updateById(topic);
        if (i <= 0) {
            Asserts.fail(ReturnCode.RC752);
        }
        return i;
    }

    @Override
    public int deleteTopic(Integer topicId) {
        int i = topicMapper.deleteById(topicId);
        if (i <= 0) {
            Asserts.fail(ReturnCode.RC753);
        }
        return i;
    }
}
