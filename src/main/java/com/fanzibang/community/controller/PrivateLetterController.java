package com.fanzibang.community.controller;

import com.fanzibang.community.service.PrivateLetterService;
import com.fanzibang.community.utils.UserHolder;
import com.fanzibang.community.vo.PrivateLetterListVo;
import com.fanzibang.community.vo.PrivateLetterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/private_letter")
public class PrivateLetterController {

    @Autowired
    private PrivateLetterService privateLetterService;

    @Autowired
    private UserHolder userHolder;

    @PostMapping
    public Integer sendPrivateLetter(Long toId, String content) {
        return privateLetterService.sendPrivateLetter(toId, content);
    }

    @GetMapping("/list")
    public List<PrivateLetterListVo> getPrivateLetterList(Integer current, Integer size) {
        List<PrivateLetterListVo> privateLetterListVoList =  privateLetterService.getPrivateLetterList(current, size);
        return privateLetterListVoList;
    }

    @GetMapping("/unRead")
    public Long getLetterUnReadCount () {
        Long userId = userHolder.getUser().getId();
        return privateLetterService.getLetterUnReadCount(userId, null);
    }

    @GetMapping("/{conversationId}")
    public List<PrivateLetterVo> getPrivateLetter(@PathVariable("conversationId") String conversationId) {
        return privateLetterService.getPrivateLetter(conversationId);
    }
}
