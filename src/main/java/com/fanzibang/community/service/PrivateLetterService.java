package com.fanzibang.community.service;

import com.fanzibang.community.vo.PrivateLetterListVo;
import com.fanzibang.community.vo.PrivateLetterVo;

import java.util.List;

public interface PrivateLetterService {
    Integer sendPrivateLetter(Long toId, String content);

    List<PrivateLetterListVo> getPrivateLetterList(Integer current, Integer size);

    Long getLetterUnReadCount(Long userId, String conversationId);

    List<PrivateLetterVo> getPrivateLetter(String conversationId);
}
