package com.itan.dao.user;

import com.itan.pojo.Role;
import com.itan.pojo.User;
import com.itan.util.Constants;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface UserDao {
    //获取登录用户信息
    public User getLoginUser(Connection connection, String UserCode) throws SQLException;

    //修改当前用户密码
    public int updatePwd(Connection connection, int id, String password) throws SQLException;

    //获取用户总数量。然后查询框可以指定用户角色，查询结果在显示的时候对应的页数，总数都会变。
    //是同时满足两个条件的，所以查询语句需要包含两个条件
    public int getUserCount(Connection connection,String userName,int userRole) throws SQLException;

    //获取用户列表
    public List<User> getUserList(Connection connection,String userName,int userRole,int currentPageNo,int pageSize) throws SQLException;

    //根据用户id获取用户信息
    public User getUserById(Connection connection, int id) throws SQLException;
}
