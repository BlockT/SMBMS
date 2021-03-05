package com.itan.servlet.user;

import com.alibaba.fastjson.JSONArray;
import com.itan.dao.BaseDao;
import com.itan.pojo.Role;
import com.itan.pojo.User;
import com.itan.service.role.RoleService;
import com.itan.service.role.RoleServiceImpl;
import com.itan.service.user.UserService;
import com.itan.service.user.UserServiceImpl;
import com.itan.util.Constants;
import com.itan.util.PageSupport;
import com.mysql.jdbc.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //前端jsp界面form表单使用了隐藏域input，命名为method，具有不同的value，为了实现userServlet的复用
        String method=req.getParameter("method");
        if (method != null && "savepwd".equals(method)) {
            updatePwd(req,resp);
        }if(method != null && "pwdmodify".equals(method)) {
            pwdModify(req,resp);
        }if (method != null && "query".equals(method)) {
            query(req,resp);
        }if (method != null && "getrolelist".equals(method)) {
            getRoleList(req,resp);
        }if (method != null && "ucexist".equals(method)) {
            userCodeExist(req,resp);
        }if (method != null && "modify".equals(method)) {
            userCodeExist(req,resp);
        }if (method != null && "view".equals(method)) {
            getUserById(req,resp,"userview.jsp");
        }
    }

    //修改当前用户密码
    public void updatePwd(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //从session里面拿id，现在session在本地做，以后放在缓存服务器中
        //直接强转可能在test时导致代码效率下降，可以在确认没错之后再进行强转
        Object o = req.getSession().getAttribute(Constants.USER_SESSION);
        String newpassword=req.getParameter("newpassword");
        boolean flag=false;
        //使用工具类判断 当前是否有用户登录，并且传递的新密码不为空
        if (o!=null && !StringUtils.isNullOrEmpty(newpassword)){
            UserService userService=new UserServiceImpl();
            int id=((User)o).getId();
            flag=userService.updatePwd(id,newpassword);
            if (flag){
                req.setAttribute("message","修改密码成功，请退出，使用新密码登录");
                //密码修改成功，应该移除当前用户session
                req.getSession().removeAttribute(Constants.USER_SESSION);
            }else {
                req.setAttribute("message","密码修改失败，请重试");
            }
        }else{
            req.setAttribute("message","新密码为空，请重新设置");
        }
        //转发到该页面，重新提交请求才能被过滤器过滤
        req.getRequestDispatcher("pwdmodify.jsp").forward(req,resp);
    }

    //旧密码验证
    public void pwdModify(HttpServletRequest req, HttpServletResponse resp){
        String oldpassword=req.getParameter("oldpassword");
        Object o = req.getSession().getAttribute(Constants.USER_SESSION);
        //用HashMap存储方法返回值的键值对
        Map<String, String> resultMap = new HashMap<String,String>();
        if (o==null){
            resultMap.put("result","sessionerror");
        }else if (StringUtils.isNullOrEmpty(oldpassword)){
            resultMap.put("result","error");
        }else {
            String userpassword=((User)o).getUserPassword();
            if (userpassword.equals(oldpassword)){
                resultMap.put("result","true");
            }else {
                resultMap.put("result","false");
            }
        }

        //Ajax的方法调用需要通过PrinterWriter流向外返回
        try {
            resp.setContentType("application/json");
            PrintWriter writer = resp.getWriter();
            //JSONArray 阿里巴巴的工具类，里面的方法来看就是实现对象类型转换的，比如文本-对象，对象-json
            //也可以不用工具类，自己实现字符串拼接，比较麻烦
            writer.write(JSONArray.toJSONString(resultMap));
            //刷新writer，防止内存溢出
            writer.flush();
            //关闭writer
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //查询用户列表,重点难点
    public void query(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        /* 从前端获取数据
        这里是获取到前端页面表单中的查询人物名字、用户角色以及hidden input组件当前页面下标值默认初始值为1，
        然后使用rollpage.jsp页面的下一页等按键对此值进行设定，document代表当前页面，只要嵌套在一起的jsp都算
        是一个页面，form[0]代表当前页面第一个表单，pag_nav是rollpage.js的一个函数，用来设置pageindex值，
        设置完之后，提交表单到user.do进行处理，这边就能拿到pageindex的值了 */
        String queryUserName = req.getParameter("queryname");
        String tmp = req.getParameter("queryUserRole");
        String pageIndex = req.getParameter("pageIndex");
        int queryUserRole = 0;   // 默认为0的话查询全部用户

        //需要调用service层方法来获取用户列表,用户总数量等数据
        UserService userService = new UserServiceImpl();
        RoleService roleService = new RoleServiceImpl();
        List<User> userList = null;
        List<Role> roleList = null;

        //设置页面页数以及请求查询的参数，第一次访问一定是第一页，页面大小固定，在静态常数类中定义为常量比较容易修改
        int currentPageNo = 1;
        int pageSize = Constants.PAGE_SIZE;

        //Dao层会对参数是否为空进行判断，然后进行对应处理
        if (queryUserName==null){
            queryUserName="";
        }
        if (tmp!=null && !"".equals(tmp)){
            queryUserRole=Integer.parseInt(tmp);
        }
        if (pageIndex!=null){
            currentPageNo=Integer.parseInt(pageIndex);
        }

        int totalCount = userService.getUserCount(queryUserName,queryUserRole);
        //设置页码支持参数
        //！！！
        //此处注意必须要先设置pagesize，否则在获取总页数的时候，pagesize值还是0
        //会抛异常，就算对异常进行了处理，结果还是不对
        PageSupport pageSupport = new PageSupport();
        pageSupport.setPageSize(pageSize);
        pageSupport.setTotalCount(totalCount);
        pageSupport.setCurrentPageNo(currentPageNo);
        int totalPageCount = pageSupport.getTotalPageCount();

        //判断页码首页尾页，但是此操作在前端js中已经设置过了
        if (currentPageNo<1){
            currentPageNo=1;
        }
        if (currentPageNo>totalPageCount){
            currentPageNo=totalPageCount;
        }

        //获取到用户列表和角色列表
        userList=userService.getUserList(queryUserName,queryUserRole,currentPageNo,pageSize);
        roleList=roleService.getRoleList();

        //这时前端需要的参数已准备齐全，设置到前端即可，此处我是按照前端的顺序来设置的
        //这俩虽然已经取到值，但是搜索界面不应该消除他，还应该显示着搜索的内容，人性化设计,所以给传回去
        //此处传回去的是${}里面的参数，代表是后端传回来的，上面取的时候取得是组件的name，两者可能只是名字相同
        req.setAttribute("queryUserName",queryUserName);
        req.setAttribute("queryUserRole",queryUserRole);
        req.setAttribute("roleList",roleList);
        req.setAttribute("userList",userList);
        req.setAttribute("totalPageCount",totalPageCount);
        req.setAttribute("totalCount",totalCount);
        req.setAttribute("currentPageNo",currentPageNo);

        req.getRequestDispatcher("userlist.jsp").forward(req,resp);
    }

    //获取用户列表到useradd界面
    public void getRoleList(HttpServletRequest req, HttpServletResponse resp){
        RoleService roleService = new RoleServiceImpl();
        List<Role> roleList = roleService.getRoleList();

        //将数据用json格式返回前端，ajax请求的特殊返回数据格式
        try {
            resp.setContentType("application/json");
            PrintWriter writer = resp.getWriter();
            writer.write(JSONArray.toJSONString(roleList));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //判断用户编码是否存在
    public void userCodeExist(HttpServletRequest req, HttpServletResponse resp){
        String userCode = req.getParameter("userCode");
        Map<String, String> resultMap = new HashMap<>();
        UserService userService = new UserServiceImpl();

        if (StringUtils.isNullOrEmpty(userCode)){
            resultMap.put("userCode","iskong");
        }else{
            User user = userService.userCodeExist(userCode);
            if (user != null) {
                resultMap.put("userCode","exist");
            }else{
                resultMap.put("userCode","notexist");
            }
        }

        try {
            resp.setContentType("application/json");
            PrintWriter writer = resp.getWriter();
            writer.write(JSONArray.toJSONString(resultMap));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //查看用户详细信息
    public void getUserById(HttpServletRequest req, HttpServletResponse resp, String url) throws ServletException, IOException {
        String uid = req.getParameter("uid");
        //注意此处要加取反符，因为是uid不为空时进行操作的
        if (!StringUtils.isNullOrEmpty(uid)){
            //此处也可以用string传值但是用int更规范
            int id = Integer.parseInt(uid);
            UserService userService = new UserServiceImpl();
            User user = userService.getUserById(id);
            req.setAttribute("user",user);
            req.getRequestDispatcher(url).forward(req,resp);
        }

    }

    //修改用户信息
//    public void

}
