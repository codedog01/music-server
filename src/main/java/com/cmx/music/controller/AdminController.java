package com.cmx.music.controller;

import com.alibaba.fastjson.JSONObject;
import com.cmx.music.service.impl.AdminServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
public class AdminController {
    @Autowired
    private AdminServiceImpl adminService;

//    判断是否登录成功
    @ResponseBody
    @PostMapping(value = "/admin/login/status")
    public Object loginStatus(String username,String password, HttpSession session){
        JSONObject jsonObject = new JSONObject();
        boolean res = adminService.veritypasswd(username, password);
        if (res) {
            jsonObject.put("code", 1);
            jsonObject.put("msg", "登录成功");
            session.setAttribute("name", username);
            return jsonObject;
        } else {
            jsonObject.put("code", 0);
            jsonObject.put("msg", "用户名或密码错误");
            return jsonObject;
        }

    }
}
