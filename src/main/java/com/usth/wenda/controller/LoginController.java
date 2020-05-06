package com.usth.wenda.controller;

import com.usth.wenda.async.EventModel;
import com.usth.wenda.async.EventProducer;
import com.usth.wenda.async.EventType;
import com.usth.wenda.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
public class LoginController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    UserService userService;

    @Autowired
    EventProducer eventProducer;

    /**
     * 注册功能
     * @param model
     * @param username
     * @param password
     * @param rememberme 是否选择记住我
     * @param next 下一个跳转页面
     * @param response
     * @return
     */
    @RequestMapping(path = {"/reg/"},method = {RequestMethod.POST})
    public String reg(Model model,
                      @RequestParam("username") String username,
                      @RequestParam("password") String password,
                      @RequestParam(value = "rememberme",defaultValue = "false") boolean rememberme,
                      @RequestParam("next") String next,
                      HttpServletResponse response) {
        try {
            Map<String, Object> map = userService.register(username, password);
            if(map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
                cookie.setPath("/");
                if(rememberme) {
                    cookie.setMaxAge(3600*24*5);
                }
                response.addCookie(cookie);
                /*
                eventProducer.fireEvent(new EventModel(EventType.LOGIN)
                        .setExts("username", username).setExts("email", "zjuyxy@qq.com")
                        .setActorId((int)map.get("userId")));
                */
                if(StringUtils.isNotBlank(next)) {
                    return "redirect:" + next;
                }
                return "redirect:/";
            } else  {
                model.addAttribute("msg",map.get("msg"));
                return "login";
            }
        } catch (Exception e) {
            LOGGER.error("注册异常"+e.getMessage());
            model.addAttribute("msg","服务器异常");
            return "login";
        }
    }

    /**
     * 登陆
     * @param model
     * @param username
     * @param password
     * @param rememberme 是否选择记住我
     * @param next 下一个跳转页面
     * @param response
     * @return
     */
    @RequestMapping(path = {"/login/"},method = {RequestMethod.POST})
    public String login(Model model,
                        @RequestParam("username") String username,
                        @RequestParam("password") String password,
                        @RequestParam(value = "rememberme",defaultValue = "false") boolean rememberme,
                        @RequestParam("next") String next,
                        HttpServletResponse response) {
        try {
            Map<String, Object> map = userService.login(username, password);
            if(map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
                cookie.setPath("/");
                if(rememberme) {
                    cookie.setMaxAge(3600*24*5);
                }
                response.addCookie(cookie);
                /*
                eventProducer.fireEvent(new EventModel(EventType.LOGIN)
                        .setExts("username",username)
                        .setExts("email","986093257@qq.com")
                        .setActorId((int)map.get("userId")));
                 */
                if(StringUtils.isNotBlank(next)) {
                    return "redirect:" + next;
                }
                return "redirect:/";
            } else  {
                model.addAttribute("msg",map.get("msg"));
                return "login";
            }
        } catch (Exception e) {
            LOGGER.error("登陆异常"+e.getMessage());
            model.addAttribute("msg","服务器异常");
            return "login";
        }
    }

    /**
     * 注册登录页面
     * @return
     */
    @RequestMapping(path = {"/reglogin"},method = {RequestMethod.GET})
    public String regloginPage(Model model,
                               @RequestParam(value = "next",required = false) String next) {
        model.addAttribute("next",next);
        return "login";
    }

    /**
     * 退出
     * @param ticket
     * @return
     */
    @RequestMapping(path = {"/logout"},method = {RequestMethod.GET,RequestMethod.POST})
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        return "redirect:/";
    }
}
