package com.fanzibang.community.constant;

public class RedisKey {
    // 登录用户key
    public static String LOGIN_USER_KEY = "login:";
    // 注册激活码key
    public static String REGISTER_CODE_KEY = "register:";
    // 点赞记录key
    public static String LIKE_KEY = "like:";
    // 用户获得总点赞数量（帖子点赞 + 评论点赞）
    public static String USER_LIKE_KEY = "user:like:";
    // 帖子热度分数key
    public static String POST_SCORE_KEY = "post:score";
    // 用户的粉丝
    public static String USER_FANS_KEY = "user:fans:";
    // 用户关注的人
    public static String USER_FOLLOWER_KEY = "user:follower:";
    // 用户关注的话题
    public static String USER_TOPIC_KEY = "user:topic:";
}
