package com.usth.wenda.async.handler;

import com.usth.wenda.async.EventHandler;
import com.usth.wenda.async.EventModel;
import com.usth.wenda.async.EventType;
import com.usth.wenda.model.Message;
import com.usth.wenda.model.User;
import com.usth.wenda.service.MessageService;
import com.usth.wenda.service.UserService;
import com.usth.wenda.util.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class LikeHandler implements EventHandler {
    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Override
    public void doHandler(EventModel model) {
        Message message = new Message();
        User user = userService.findById(model.getActorId());
        message.setFromId(WendaUtil.SYSTEM_USERID);
        message.setToId(model.getEntityOwnerId());
        message.setCreatedDate(new Date());
        message.setContent("用户" + user.getName() + "赞了你对问题<<" + model.getExts("question") + ">>的评论: " + model.getExts("comment"));
        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LIKE);
    }
}
