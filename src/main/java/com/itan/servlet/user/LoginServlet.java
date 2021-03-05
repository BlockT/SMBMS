package com.itan.servlet.user;

import com.itan.pojo.User;
import com.itan.service.user.UserService;
import com.itan.service.user.UserServiceImpl;
import com.itan.util.Constants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginServlet extends HttpServlet {

    //控制层的功能实现需要调用业务层方法，可以设为私有属性，也可以用的时候直接new
    private UserService userService;

    public LoginServlet(){
        userService=new UserServiceImpl();
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user=null;
        String userCode=req.getParameter("userCode");
        String userPassword=req.getParameter("userPassword");
        user=userService.login(userCode,userPassword);

        if (user != null) {
            //查到用户信息不为空，证明查有此人，将其信息存入session中，让整个站点都能够知道这个用户已登录
            req.getSession().setAttribute(Constants.USER_SESSION,user);
            //跳转到首页
            resp.sendRedirect("jsp/frame.jsp");
        }
        else {
            //查无此人，重定向返回登陆界面并且提示错误信息
            req.setAttribute("error","用户名或者密码错误");
            req.getRequestDispatcher("login.jsp").forward(req,resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
