package com.fanzibang.community.service;

import com.fanzibang.community.dto.DiscussPostParam;
import com.fanzibang.community.pojo.DiscussPost;

public interface DiscussPostService {
    int publishDiscussPost(DiscussPostParam discussPostParam);

    DiscussPost getDiscussPostById(Long typeId);

    int deleteDiscussPost(Long id);
}
