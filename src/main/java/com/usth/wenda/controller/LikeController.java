package com.usth.wenda.controller;

import com.usth.wenda.async.EventModel;
import com.usth.wenda.async.EventProducer;
import com.usth.wenda.async.EventType;
import com.usth.wenda.model.Comment;
import com.usth.wenda.model.EntityType;
import com.usth.wenda.model.HostHolder;
import com.usth.wenda.model.Question;
import com.usth.wenda.service.CommentService;
import com.usth.wenda.service.LikeService;
import com.usth.wenda.service.QuestionService;
import com.usth.wenda.util.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LikeController {

    @Autowired
    QuestionService questionService;

    @Autowired
    LikeService likeService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    CommentService commentService;

    @Autowired
    EventProducer eventProducer;


    @RequestMapping(path = "/like",method = RequestMethod.POST)
    @ResponseBody
    public String like(@RequestParam("commentId") int commentId) {
        if(hostHolder.getUser() == null) {
            return WendaUtil.getJSONString(999);
        }
        Comment comment = commentService.findCommentById(commentId);
        Question question = questionService.findById(comment.getEntityId());
        EventModel model = new EventModel(EventType.LIKE).
                setActorId(hostHolder.getUser().getId()).
                setEntityId(commentId).
                setEntityType(EntityType.ENTITY_COMMENT).
                setExt("question", String.valueOf(question.getTitle())).
                setExt("comment",comment.getContent()).
                setExt("questionId",String.valueOf(comment.getEntityId())).
                setEntityOwnerId(comment.getUserId());
        eventProducer.fireEvent(model);

        long likeCount = likeService.like(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT,commentId);
        return WendaUtil.getJSONString(0,String.valueOf(likeCount));
    }

    @RequestMapping(path = "/dislike",method = RequestMethod.POST)
    @ResponseBody
    public String dislike(@RequestParam("commentId") int commentId) {
        if(hostHolder.getUser() == null) {
            return WendaUtil.getJSONString(999);
        }
        long likeCount = likeService.disLike(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT,commentId);
        return WendaUtil.getJSONString(0,String.valueOf(likeCount));
    }
}
