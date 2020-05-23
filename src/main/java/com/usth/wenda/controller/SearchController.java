package com.usth.wenda.controller;

import com.usth.wenda.model.EntityType;
import com.usth.wenda.model.Question;
import com.usth.wenda.model.ViewObject;
import com.usth.wenda.service.FollowService;
import com.usth.wenda.service.QuestionService;
import com.usth.wenda.service.SearchService;
import com.usth.wenda.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class SearchController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    SearchService searchService;

    @Autowired
    FollowService followService;

    @Autowired
    UserService userService;

    @Autowired
    QuestionService questionService;

    @RequestMapping(path = {"/search"}, method = {RequestMethod.GET})
    public String search(Model model,@RequestParam("q") String keyword,
                         @RequestParam(value = "offset", defaultValue = "0") int offset,
                         @RequestParam(value = "count", defaultValue = "10") int count) {
        if (keyword.length() == 0) {
            return "forward:/";
        }
        try {
            List<Question> questionList = searchService.searchQuestions(keyword,offset,count,"<em>","</em>");
            List<ViewObject> vos = new ArrayList<>();
            for (Question question : questionList) {
                Question q = questionService.findById(question.getId());
                ViewObject vo = new ViewObject();
                if (question.getContent() != null) {
                    q.setContent(question.getContent());
                }
                if (question.getTitle() != null) {
                    q.setTitle(question.getTitle());
                }
                vo.set("question",q);
                vo.set("followCount",followService.getFollowerCount(EntityType.ENTITY_QUESTION,q.getId()));
                vo.set("user",userService.findById(q.getUserId()));
                vos.add(vo);
            }
            model.addAttribute("vos",vos);
            model.addAttribute("keyword",keyword);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return "result";
    }
}
