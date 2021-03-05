package com.itan.filter;

import com.itan.pojo.User;
import com.itan.util.Constants;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SysFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        //从session中获取用户，如果退出登录移除session，就获取不到用户
        User user = (User) request.getSession().getAttribute(Constants.USER_SESSION);
        if (user!=null){
            //如果已登录，就放行
            filterChain.doFilter(req,resp);
        }else{
            //如果未登录或者已注销，就跳转错误提示界面
            response.sendRedirect(request.getContextPath()+"/error.jsp");
        }
    }

    @Override
    public void destroy() {

    }
}
