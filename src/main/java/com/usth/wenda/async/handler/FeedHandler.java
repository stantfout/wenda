package com.usth.wenda.async.handler;

import com.alibaba.fastjson.JSONObject;
import com.usth.wenda.async.EventHandler;
import com.usth.wenda.async.EventModel;
import com.usth.wenda.async.EventType;
import com.usth.wenda.model.EntityType;
import com.usth.wenda.model.Feed;
import com.usth.wenda.model.Question;
import com.usth.wenda.model.User;
import com.usth.wenda.service.FeedService;
import com.usth.wenda.service.FollowService;
import com.usth.wenda.service.QuestionService;
import com.usth.wenda.service.UserService;
import com.usth.wenda.util.JedisAdapter;
import com.usth.wenda.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jws.WebParam;
import java.awt.*;
import java.util.*;
import java.util.List;

@Component
public class FeedHandler implements EventHandler {

    @Autowired
    FeedService feedService;

    @Autowired
    QuestionService questionService;

    @Autowired
    UserService userService;

    @Autowired
    FollowService followService;

    @Autowired
    JedisAdapter jedisAdapter;

    private String buildFeedData(EventModel model) {
        Map<String,String> map = new HashMap<>();
        User actor = userService.findById(model.getActorId());
        if(actor == null) {
            return null;
        }
        map.put("userId",String.valueOf(actor.getId()));
        map.put("userHead",actor.getHeadUrl());
        map.put("userName",actor.getName());

        if(model.getType() == EventType.COMMENT) {
            Question question = questionService.findById(model.getEntityId());
            if(question == null) {
                return null;
            }
            map.put("questionId",String.valueOf(question.getId()));
            map.put("questionTitle",question.getTitle());
            return JSONObject.toJSONString(map);
        } else if (model.getType() == EventType.LIKE ) {
            map.put("comment",model.getExt("comment"));
            map.put("questionId",model.getExt("questionId"));
            map.put("questionTitle",model.getExt("question"));
            return JSONObject.toJSONString(map);
        } else if (model.getType() == EventType.ADD_QUESTION || (model.getType() == EventType.FOLLOW && model.getEntityType() == EntityType.ENTITY_QUESTION)) {
            map.put("questionId",model.getExt("questionId"));
            map.put("questionTitle",model.getExt("question"));
            return JSONObject.toJSONString(map);
        }
        return null;
    }

    @Override
    public void doHandler(EventModel model) {
        Feed feed = new Feed();
        feed.setCreatedDate(new Date());
        feed.setUserId(model.getActorId());
        feed.setType(model.getType().getValue());
        feed.setData(buildFeedData(model));
        if(feed.getData() == null) {
            return;
        }
        feedService.addFeed(feed);

        //给事件的粉丝推
        List<Integer> followers = followService.getFollowers(EntityType.ENTITY_USER, model.getActorId(), Integer.MAX_VALUE);
        followers.add(0);
        for (Integer follower : followers) {
            String timelineKey = RedisKeyUtil.getTimelineKey(follower);
            jedisAdapter.lpush(timelineKey,String.valueOf(feed.getId()));
        }
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LIKE,EventType.ADD_QUESTION,EventType.COMMENT,EventType.FOLLOW);
    }
}
