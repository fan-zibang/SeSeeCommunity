package com.fanzibang.community.service;

public interface LikeService {
    Boolean isLike(Integer entityType,Long entityId, Long userId);

    Long getLikeCount(Integer entityType, Long entityId);

    void like(Integer entityType, Long entityId);
}