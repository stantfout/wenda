package com.usth.wenda.service;

import com.usth.wenda.dao.MessageDao;
import com.usth.wenda.model.Message;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    MessageDao messageDao;

    @Autowired
    SensitiveService sensitiveService;

    public int addMessage(Message message) {
        message.setContent(sensitiveService.filter(message.getContent()));
        return messageDao.addMessage(message) > 0 ? message.getId() : 0;
    }

    public List<Message> findConversationDetail(String conversationId,int offset, int limit) {
        return messageDao.findConversationDetail(conversationId,offset,limit);
    }

    public List<Message> findConversationList(int userId,int offset,int limit) {
        return messageDao.findConversationList(userId,offset,limit);
    }

    public int findConversationUnreadCount(int userId,String conversationId) {
        return messageDao.findConversationUnreadCount(userId,conversationId);
    }

    public void updateConversationHasRead(int userId, String conversationId) {
        messageDao.updateConersationHasRead(userId,conversationId);
    }
}
