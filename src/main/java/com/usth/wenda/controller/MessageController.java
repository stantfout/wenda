package com.usth.wenda.controller;

import com.usth.wenda.model.HostHolder;
import com.usth.wenda.model.Message;
import com.usth.wenda.model.User;
import com.usth.wenda.model.ViewObject;
import com.usth.wenda.service.MessageService;
import com.usth.wenda.service.UserService;
import com.usth.wenda.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class MessageController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    HostHolder hostHolder;

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    /**
     * 私信列表
     * @param model
     * @return
     */
    @RequestMapping(value = "/msg/list",method = {RequestMethod.GET})
    public String getConversationList(Model model) {
        if(hostHolder.getUser() == null) {
            return "redirect:/relogin";
        }
        int localUserId = hostHolder.getUser().getId();
        List<Message> messageList = messageService.findConversationList(localUserId,0,10);
        List<ViewObject> conversations = new ArrayList<>();
        for (Message message : messageList) {
            ViewObject vo = new ViewObject();
            vo.set("message",message);
            int targetId = (message.getFromId() == localUserId ? message.getToId() : message.getFromId());
            vo.set("user",userService.findById(targetId));
            vo.set("unread",messageService.findConversationUnreadCount(localUserId,message.getConversationId()));
            conversations.add(vo);
        }
        model.addAttribute("conversations",conversations);
        return "letter";
    }

    /**
     * 私信详情页面
     * @param model
     * @param conversationId
     * @return
     */
    @RequestMapping(value = "/msg/detail",method = {RequestMethod.GET})
    public String getConversationDetail(Model model, @RequestParam("conversationId") String conversationId) {
        try {
            List<Message> messageList = messageService.findConversationDetail(conversationId,0,10);
            List<ViewObject> messages = new ArrayList<>();
            for (Message message : messageList) {
                ViewObject vo = new ViewObject();
                vo.set("message",message);
                vo.set("user",userService.findById(message.getFromId()));
                messages.add(vo);
            }
            messageService.updateConversationHasRead(hostHolder.getUser().getId(),conversationId);
            model.addAttribute("messages",messages);
        } catch (Exception e) {
            LOGGER.error("获取详细失败" + e.getMessage());
        }
        return "letterDetail";
    }

    /**
     * 增加私信
     * @param toName
     * @param content
     * @return
     */
    @RequestMapping(value = "/msg/addMessage",method = {RequestMethod.POST})
    @ResponseBody
    public String addMessage(@RequestParam("toName") String toName,
                             @RequestParam("content") String content) {
        try {
            if(hostHolder.getUser() == null) {
                return WendaUtil.getJSONString(999,"未登入");
            }

            User user = userService.findByName(toName);
            if(user == null) {
                return WendaUtil.getJSONString(1,"用户不存在");
            }

            Message message = new Message();
            message.setCreatedDate(new Date());
            message.setFromId(hostHolder.getUser().getId());
            message.setToId(user.getId());
            message.setContent(content);
            messageService.addMessage(message);
            return WendaUtil.getJSONString(0);
        } catch (Exception e) {
            LOGGER.error("发送消息失败" + e.getMessage());
            return WendaUtil.getJSONString(1,"发送失败");
        }
    }
}
