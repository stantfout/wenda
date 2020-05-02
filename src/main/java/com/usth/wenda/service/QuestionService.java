package com.usth.wenda.service;

import com.usth.wenda.dao.QuestionDao;
import com.usth.wenda.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private SensitiveService sensitiveService;

    public int addQusetion(Question question) {
        question.setContent(HtmlUtils.htmlEscape(question.getContent()));
        question.setTitle(HtmlUtils.htmlEscape(question.getTitle()));
        //敏感词过滤
        question.setTitle(sensitiveService.filter(question.getTitle()));
        question.setContent(sensitiveService.filter(question.getContent()));
        return questionDao.addQuestion(question) > 0 ? question.getId() : 0;
    }

    public List<Question> findLatestQuestion(int userId, int offset, int limit) {
        List<Question> questionList = questionDao.findLatestQuestion(userId, offset, limit);
        return questionList;
    }

    public Question findById(int id) {
        return questionDao.findById(id);
    }

    public int updateCommentCount(int id,int count) {
        return questionDao.updateCommentCount(id,count);
    }

}
