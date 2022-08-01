package com.fanzibang.community.controller;

import com.fanzibang.community.pojo.Role;
import com.fanzibang.community.pojo.Topic;
import com.fanzibang.community.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/topic")
public class TopicController {

    @Autowired
    private TopicService topicService;

    @GetMapping("/list")
    public List<Topic> getTopicList() {
        return topicService.getTopicList();
    }

    @GetMapping("/pageList")
    public Map<String, Object> getTopicPageList(Integer current, Integer size) {
        return topicService.getTopicPageList(current, size);
    }

    @GetMapping("/{topicId}")
    public Topic getTopicById(@PathVariable Integer topicId) {
        return topicService.getTopicById(topicId);
    }

    @PostMapping
    public int addTopic(@RequestBody @Valid Topic topic) {
        return topicService.addTopic(topic);
    }

    @PostMapping("/{topicId}")
    public int editTopic(@PathVariable("topicId") Integer topicId, @RequestBody @Valid Topic topic) {
        return topicService.editTopic(topicId, topic);
    }

    @DeleteMapping("/delete/{topicId}")
    public int deleteTopic(@PathVariable("topicId") Integer topicId) {
        return topicService.deleteTopic(topicId);
    }

}
