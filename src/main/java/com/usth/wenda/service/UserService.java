package com.usth.wenda.service;

import com.usth.wenda.dao.LoginTicketDao;
import com.usth.wenda.dao.UserDao;
import com.usth.wenda.model.LoginTicket;
import com.usth.wenda.model.User;
import com.usth.wenda.util.WendaUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private LoginTicketDao loginTicketDao;

    public User findById(int id) {
        User users = userDao.findById(id);
        return users;
    }

    public User findByName(String username) {
        return userDao.findByName(username);
    }

    public Map<String,Object> register(String username, String password) {
        Map<String,Object> map = new HashMap<String, Object>();

        if (StringUtils.isBlank(username)) {
            map.put("msg","用户名不能为空");
            return map;
        }

        if(StringUtils.isBlank(password)) {
            map.put("msg","密码不能为空");
            return map;
        }

        if(password.length() >= 18 || password.length() <= 4) {
            map.put("msg","密码长度不安全");
            return map;
        }

        User user = userDao.findByName(username);

        if(user != null) {
            map.put("msg","用户已经被注册");
            return map;
        }

        user = new User();
        user.setName(username);
        user.setSalt(UUID.randomUUID().toString().substring(0,5));
        String head = String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000));
        user.setHeadUrl(head);
        user.setPassword(WendaUtil.MD5(password + user.getSalt()));
        userDao.addUser(user);

        String ticket = addLoginTicket(user.getId());
        map.put("ticket",ticket);
        return map;
    }

    public Map<String, Object> login(String username, String password) {
        Map<String,Object> map = new HashMap<String, Object>();

        if (StringUtils.isBlank(username)) {
            map.put("msg","用户名不能为空");
            return map;
        }

        if(StringUtils.isBlank(password)) {
            map.put("msg","密码不能为空");
            return map;
        }

        User user = userDao.findByName(username);

        if(user == null) {
            map.put("msg","用户名不存在");
            return map;
        }

        if(!user.getPassword().equals(WendaUtil.MD5(password+user.getSalt()))) {
            map.put("msg","密码不正确");
            return map;
        }

        String ticket = addLoginTicket(user.getId());
        map.put("ticket",ticket);
        return map;
    }

    private String addLoginTicket(int userId) {
        LoginTicket ticket = new LoginTicket();
        ticket.setUserId(userId);
        Date date = new Date();
        date.setTime(date.getTime() + 3600 * 24 * 1000);
        ticket.setExpired(date);
        ticket.setStatus(0);
        ticket.setTicket(UUID.randomUUID().toString().replaceAll("-",""));
        loginTicketDao.addTicket(ticket);
        return ticket.getTicket();
    }

    public void logout(String ticket) {
        loginTicketDao.updateStatus(ticket,1);
    }
}
