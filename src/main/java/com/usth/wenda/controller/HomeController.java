package com.usth.wenda.controller;

import com.usth.wenda.model.*;
import com.usth.wenda.service.CommentService;
import com.usth.wenda.service.FollowService;
import com.usth.wenda.service.QuestionService;
import com.usth.wenda.service.UserService;
import org.apache.catalina.Host;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {
    private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    QuestionService questionService;

    @Autowired
    UserService userService;

    @Autowired
    FollowService followService;

    @Autowired
    CommentService commentService;

    @Autowired
    HostHolder hostHolder;

    /**
     * 得到问题列表
     * @param userId 用户ID，ID=0为所有用户
     * @param offset 列表偏移(从第offset条评论开始)
     * @param limit 列表长度
     * @return
     */
    private List<ViewObject> getQuestions(int userId, int offset, int limit) {
        List<Question> questionList = questionService.findLatestQuestion(userId, offset, limit);
        List<ViewObject> vos = new ArrayList<>();
        for (Question question : questionList) {
            ViewObject vo = new ViewObject();
            vo.set("question",question);
            vo.set("followCount",followService.getFollowerCount(EntityType.ENTITY_QUESTION,question.getId()));
            vo.set("user",userService.findById(question.getUserId()));
            vos.add(vo);
        }
        return vos;
    }

    /**
     * 所有用户问题列表
     * @param model
     * @return
     */
    @RequestMapping(path = {"/","/index"})
    public String index(Model model) {
        model.addAttribute("vos",getQuestions(0,0,10));
        return "index";
    }

    /**
     * 单个用户的问题列表
     * @param model
     * @param userId
     * @return
     */
    @RequestMapping(path = {"/user/{userId}"})
    public String userIndex(Model model,
                            @PathVariable("userId") int userId) {
        model.addAttribute("vos",getQuestions(userId,0,10));

        User user = userService.findById(userId);
        ViewObject vo = new ViewObject();
        vo.set("user",user);
        vo.set("commentCount",commentService.getUserCommentCount(userId));
        vo.set("followerCount",followService.getFollowerCount(EntityType.ENTITY_USER,userId));
        vo.set("followeeCount",followService.getFolloweeCount(userId,EntityType.ENTITY_USER));
        if(hostHolder.getUser() != null) {
            vo.set("followed",followService.isFollower(hostHolder.getUser().getId(),EntityType.ENTITY_USER,userId));
        } else {
            vo.set("followed",false);
        }
        model.addAttribute("profileUser",vo);
        return "profile";
    }
}
