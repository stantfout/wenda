package com.usth.wenda.controller;

import com.usth.wenda.async.EventModel;
import com.usth.wenda.async.EventProducer;
import com.usth.wenda.async.EventType;
import com.usth.wenda.model.Comment;
import com.usth.wenda.model.EntityType;
import com.usth.wenda.model.HostHolder;
import com.usth.wenda.service.CommentService;
import com.usth.wenda.service.QuestionService;
import com.usth.wenda.util.WendaUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@Controller
public class CommentController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    HostHolder hostHolder;

    @Autowired
    CommentService commentService;

    @Autowired
    QuestionService questionService;

    @Autowired
    EventProducer eventProducer;

    /**
     * 增加评论
     * @param questionId 评论ID
     * @param content 内容
     * @return
     */
    @RequestMapping(value = "/addComment",method = {RequestMethod.POST})
    public String addComment(@RequestParam() int questionId,
                             @RequestParam() String content) {
        try {
            Comment comment = new Comment();
            comment.setContent(content);
            if(hostHolder.getUser() != null) {
                comment.setUserId(hostHolder.getUser().getId());
            } else {
                comment.setUserId(WendaUtil.ANONYMOUS_USERID);
            }
            comment.setCreatedDate(new Date());
            comment.setEntityType(EntityType.ENTITY_QUESTION);
            comment.setEntityId(questionId);
            commentService.addComment(comment);

            // 通过评论ID得到问题的总评论数
            int count = commentService.getCommentCount(comment.getEntityId(),comment.getEntityType());
            questionService.updateCommentCount(comment.getEntityId(),count);

            eventProducer.fireEvent(new EventModel(EventType.COMMENT).setActorId(comment.getUserId()).
                    setEntityId(questionId));

        } catch (Exception e) {
            LOGGER.error("增加评论失败" + e.getMessage());
        }
        return "redirect:/question/" + questionId;
     }
}
