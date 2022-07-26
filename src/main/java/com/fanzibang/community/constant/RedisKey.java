package com.fanzibang.community.constant;

public class RedisKey {
    public static final String KEY_SPLIT = ":" ;
    // 登录用户key
    public static final String LOGIN_USER_KEY = "login:";
    // 注册激活码key
    public static final String REGISTER_CODE_KEY = "register:";
    // 点赞记录key
    public static final String LIKE_KEY = "like:";
    // 点赞限制key（防止用户重复点击点赞，重复发送点赞通知）
    public static final String LIKE_LIMIT_KEY = "like:limit:";
    // 用户获得总点赞数量（帖子点赞 + 评论点赞）
    public static final String USER_LIKE_KEY = "user:like:";
    // 帖子热度分数key
    public static final String POST_SCORE_KEY = "post:score";
    // 用户的粉丝
    public static final String USER_FANS_KEY = "user:fans:";
    // 用户关注的人
    public static final String USER_FOLLOWER_KEY = "user:follower:";
    // 关注限制key（防止用户重复点击关注，重复发送关注通知）
    public static final String FOLLOW_LIMIT_KEY = "follow:limit:";
    // 用户关注的话题
    public static final String USER_TOPIC_KEY = "user:topic:";
    // 统计UV
    public static final String DATA_UV_KEY = "data:uv:";
    // 统计DAU
    public static final String DATA_DAU_KEY = "data:dau:";
    // 统计热词
    public static final String DATA_HOTWORD_KEY = "data:hotword";
}
