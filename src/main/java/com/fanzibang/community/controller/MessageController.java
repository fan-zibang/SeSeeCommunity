package com.fanzibang.community.controller;

import com.fanzibang.community.service.MessageService;
import com.fanzibang.community.utils.CommonResult;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping("/system")
    public List<Map<String, Object>> getSystemMessageList(Integer current, Integer size) {
        return messageService.getSystemMessageList(current, size);
    }

    @GetMapping("/comment")
    public List<Map<String, Object>> getCommentMessageList(Integer current, Integer size) {
        return messageService.getCommentMessageList(current, size);
    }

    @GetMapping("/follow")
    public List<Map<String, Object>> getFollowMessageList(Integer current, Integer size) {
        return messageService.getFollowMessageList(current, size);
    }

    @GetMapping("/like")
    public List<Map<String, Object>> getLikeMessageList(Integer current, Integer size) {
        return messageService.getLikeMessageList(current, size);
    }

    @PostMapping("/system")
    public CommonResult publishSystemMessage(@Valid @NotEmpty @Length(min = 1, max = 500) String content) {
        messageService.publishSystemMessage(content);
        return CommonResult.success(null);
    }

}
