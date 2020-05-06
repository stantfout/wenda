package com.usth.wenda.controller;

import com.usth.wenda.model.*;
import com.usth.wenda.service.*;
import com.usth.wenda.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;

@Controller
public class QuestionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    UserService userService;

    @Autowired
    QuestionService questionService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    CommentService commentService;

    @Autowired
    LikeService likeService;

    @Autowired
    FollowService followService;

    /**
     * 增加问题
     * @param title
     * @param content
     * @return
     */
    @RequestMapping(value = "/question/add",method = {RequestMethod.POST})
    @ResponseBody
    public String addQuestion(@RequestParam("title") String title,
                              @RequestParam("content") String content){
        try {
            Question question = new Question();
            question.setContent(content);
            question.setTitle(title);
            question.setCreatedDate(new Date());
            question.setCommentCount(0);
            if(hostHolder.getUser() == null) {
                question.setUserId(WendaUtil.ANONYMOUS_USERID);
            } else {
                question.setUserId(hostHolder.getUser().getId());
            }
            if(questionService.addQusetion(question) > 0) {
                return WendaUtil.getJSONString(0);
            }
        } catch (Exception e) {
            LOGGER.error("增加题目失败" + e.getMessage());
        }
        return WendaUtil.getJSONString(1,"失败");
    }

    /**
     * 问题及评论详细页面
     * @param model
     * @param qid 问题ID
     * @return
     */
    @RequestMapping("/question/{qid}")
    public String questionDetail(Model model,@PathVariable int qid) {
        Question question = questionService.findById(qid);
        model.addAttribute("question",question);
        //model.addAttribute("user",userService.findById(question.getUserId()));
        
        List<Comment> commentList = commentService.findCommentByEntity(qid,EntityType.ENTITY_QUESTION);
        List<ViewObject> comments = new ArrayList<>();
        for (Comment comment : commentList) {
            ViewObject vo = new ViewObject();
            vo.set("comment",comment);
            if(hostHolder.getUser() == null) {
                vo.set("liked",0);
            } else {
                vo.set("liked",likeService.getLikeStatus(hostHolder.getUser().getId(),EntityType.ENTITY_COMMENT,comment.getId()));
            }
            vo.set("likeCount",likeService.getLikeCount(EntityType.ENTITY_COMMENT,comment.getId()));
            vo.set("user",userService.findById(comment.getUserId()));
            comments.add(vo);
        }
        model.addAttribute("comments",comments);

        List<ViewObject> followUsers = new ArrayList<>();
        //获取关注用户信息
        List<Integer> users = followService.getFollowers(EntityType.ENTITY_QUESTION,qid,20);
        for (Integer userId : users) {
            ViewObject vo = new ViewObject();
            User u = userService.findById(userId);
            if(u != null) {
                vo.set("name",u.getName());
                vo.set("headUrl",u.getHeadUrl());
                vo.set("id",u.getId());
                followUsers.add(vo);
            }
        }
        model.addAttribute("followUsers",followUsers);
        if(hostHolder.getUser() != null) {
            model.addAttribute("followed",followService.isFollower(hostHolder.getUser().getId(),EntityType.ENTITY_QUESTION,qid));
        } else {
            model.addAttribute("followed",false);
        }
        return "detail";
    }
}
