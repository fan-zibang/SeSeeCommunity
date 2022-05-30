package com.fanzibang.community.service;

import com.fanzibang.community.dto.DiscussPostParam;
import com.fanzibang.community.pojo.DiscussPost;
import com.fanzibang.community.vo.DiscussPostDetailVo;

public interface DiscussPostService {
    int publishDiscussPost(DiscussPostParam discussPostParam);

    DiscussPost getDiscussPostById(Long typeId);

    Integer deleteDiscussPost(Long id);

    DiscussPostDetailVo getDiscussPostDetail(Long id);
}
