package com.fanzibang.community.service;

import com.fanzibang.community.dto.DiscussPostParam;
import com.fanzibang.community.pojo.DiscussPost;
import com.fanzibang.community.vo.DiscussPostDetailVo;

import java.util.List;

public interface DiscussPostService {
    int publishDiscussPost(DiscussPostParam discussPostParam);

    DiscussPost getDiscussPostById(Long postId);

    Integer deleteDiscussPost(Long id);

    DiscussPostDetailVo getDiscussPostDetail(Long id);

    List<DiscussPostDetailVo> getDiscussPostList(Long userId, Integer current, Integer size, Integer mode);

    Long getDiscussPostCount(Long userId);

    void refreshPostScore();

    Integer setEssence(Long postId, Integer mode);

    Integer setBlock(Long postId, Integer mode);
}
