package com.fanzibang.community.service;

import com.fanzibang.community.vo.UserFollowVo;

import java.util.List;

public interface FollowService {

    void follow(Integer entityType, Long entityId);

    List<UserFollowVo> getFollowerList(Long uid);

    List<UserFollowVo> getFansList(Long uid);

    Long getFollowerCount(Long uid);

    Long getTopicCount(Long uid);

    Long getFansCount(Long uid);
}
