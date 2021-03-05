package com.itan.service.user;

import com.itan.pojo.User;

import java.sql.Connection;
import java.util.List;

public interface UserService {
    //用户登录
    public User login(String userCode, String userPassword);

    //修改当前用户密码
    public boolean updatePwd(int id, String password);

    //获取用户总数量
    public int getUserCount(String userName, int userRole);

    //获取用户列表
    public List<User> getUserList(String userName, int userRole, int currentPageNo, int pageSize);

    //根据添加用户时输入的usercode查询数据库以验证该用户是否存在
    public User userCodeExist(String userCode);

    //根据用户id获取用户详细信息
    public User getUserById(int id);
}
