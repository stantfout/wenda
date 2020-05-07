package com.usth.wenda.controller;

import com.usth.wenda.model.EntityType;
import com.usth.wenda.model.Feed;
import com.usth.wenda.model.HostHolder;
import com.usth.wenda.service.FeedService;
import com.usth.wenda.service.FollowService;
import com.usth.wenda.util.JedisAdapter;
import com.usth.wenda.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

@Controller
public class FeedController {
    @Autowired
    FeedService feedService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    FollowService followService;

    @Autowired
    JedisAdapter jedisAdapter;

    @RequestMapping(value = "/pullfeeds",method = {RequestMethod.GET,RequestMethod.POST})
    public String getPullFeeds(Model model) {
        int localUserId = hostHolder.getUser() == null ? 0 : hostHolder.getUser().getId();
        List<Integer> followees = new ArrayList<>();
        if(localUserId != 0) {
            followees = followService.getFollowees(localUserId, EntityType.ENTITY_USER,Integer.MAX_VALUE);
        }
        if (followees.size() != 0) {
            List<Feed> feeds = feedService.getUserFeeds(Integer.MAX_VALUE,followees,10);
            model.addAttribute("feeds",feeds);
        }
        return "feeds";
    }

    @RequestMapping(value = "/pushfeeds",method = {RequestMethod.GET,RequestMethod.POST})
    public String getPushFeeds(Model model) {
        int localUserId = hostHolder.getUser() == null ? 0 : hostHolder.getUser().getId();
        List<String> feedIds = jedisAdapter.lrange(RedisKeyUtil.getTimelineKey(localUserId),0,10);
        List<Feed> feeds = new ArrayList<>();
        for (String feedId : feedIds) {
            Feed feed = feedService.findById(Integer.parseInt(feedId));
            if(feed != null) {
                feeds.add(feed);
            }
        }
        model.addAttribute("feeds",feeds);
        return "feeds";
    }
}
