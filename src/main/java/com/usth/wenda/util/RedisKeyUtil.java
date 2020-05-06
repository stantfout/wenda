package com.usth.wenda.util;

public class RedisKeyUtil {
    private static String SPLIT = ":";
    private static String BIZ_LIKE = "LIKE";
    private static String BIZ_DISLIKE = "DISLIKE";
    private static String BIZ_EVENTQUEUE = "EVENTQUEUE";
    private static String BIZ_TIMELINE = "TIMELINE";
    //获取粉丝
    private static String BIZ_FOLLOWER = "FOLLOWER";
    //关注对象
    private static String BIZ_FOLLOWEE = "FOLLOWEE";

    public static String getLikeKey(int entityType, int entityId) {
        return BIZ_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    public static String getDisLikeKey(int entityType, int entityId) {
        return BIZ_DISLIKE + SPLIT + entityType + SPLIT + entityId;
    }

    public static String getEventQueueKey() {
        return BIZ_EVENTQUEUE;
    }

    public static String getFollowerKey(int entityType, int entityId) {
        return BIZ_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }

    public static String getFolloweeKey(int entityType, int userId) {
        return BIZ_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    public static String getTimelineKey(int userId) {
        return BIZ_TIMELINE + SPLIT + userId;
    }

}
