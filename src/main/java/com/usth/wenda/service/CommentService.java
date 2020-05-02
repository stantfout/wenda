package com.usth.wenda.service;

import com.usth.wenda.dao.CommentDao;
import com.usth.wenda.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class CommentService {
    @Autowired
    CommentDao commentDao;

    @Autowired
    SensitiveService sensitiveService;

    public List<Comment> findCommentByEntity(int entityId, int entityType) {
        return commentDao.findCommentByEntity(entityId,entityType);
    }

    public int addComment(Comment comment) {
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        //评论敏感词过滤
        comment.setContent(sensitiveService.filter(comment.getContent()));
        return commentDao.addComment(comment) > 0 ? comment.getId() : 0;
    }

    public int getCommentCount(int entityId, int entityType) {
        return commentDao.getCommentCount(entityId,entityType);
    }

    public int getUserCommentCount(int userId) {
        return commentDao.getUserCommentCount(userId);
    }

    public boolean deleteComment(int commentId) {
        return commentDao.updateStatus(1,commentId) > 0;
    }

    public Comment findCommentById(int id) {
        return commentDao.findCommentById(id);
    }
}
