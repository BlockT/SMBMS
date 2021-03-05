package com.itan.dao.user;

import com.itan.dao.BaseDao;
import com.itan.pojo.User;
import com.mysql.jdbc.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDaoImpl implements UserDao{
    @Override
    //获取登录用户信息
    public User getLoginUser(Connection connection, String userCode) throws SQLException {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        User user = null;

        if (connection != null) {
            String sql="select * from smbms_user where userCode=?";
            Object[] params={userCode};

            rs = BaseDao.execute(connection, pstm, rs, sql, params);
            //若有多条结果，处理每一条用while循环，只有一条结果就用if
            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setUserCode(rs.getString("userCode"));
                user.setUserName(rs.getString("userName"));
                user.setUserPassword(rs.getString("userPassword"));
                user.setGender(rs.getInt("gender"));
                user.setBirthday(rs.getDate("birthday"));
                user.setPhone(rs.getString("phone"));
                user.setAddress(rs.getString("address"));
                user.setUserRole(rs.getInt("userRole"));
                user.setCreatedBy(rs.getInt("createdBy"));
                user.setCreationDate(rs.getDate("creationDate"));
                user.setModifyBy(rs.getInt("modifyBy"));
                user.setModifyDate(rs.getDate("modifyDate"));
            }
            //connection可能存在业务，dao层先不关闭
            BaseDao.closeResource(null,pstm,rs);
        }
        return user;
    }

    @Override
    //修改当前用户密码
    public int updatePwd(Connection connection, int id, String password) throws SQLException {
        int executeResult=0;
        if (connection != null) {
            PreparedStatement pstm = null;
            Object[] params={password,id};
            String sql="update smbms_user set userPassword = ? where id = ?";
            executeResult = BaseDao.execute(connection, pstm, sql, params);
            BaseDao.closeResource(null,pstm,null);
        }
        return executeResult;
    }

    @Override
    //获取用户总数，但是有查询条件（即用户名和角色）的时候，对应的总数和页数也会变化，所以查询语句需要同时包括两个条件
    public int getUserCount(Connection connection,String userName,int userRole) throws SQLException {
        //Integer类型可以设置为null，但是这里是查询数量，不会为null
        //Integer会自动拆箱，拆箱的时候会报告空指针异常
        int userCount=0;
        PreparedStatement pstm=null;
        ResultSet rs=null;
        if (connection!=null){
            StringBuffer sql=new StringBuffer();
            sql.append("select COUNT(1) as count from smbms_user u,smbms_role r where u.userRole=r.id");
            //这种操作没有任何意义，无法实现这里的功能需求
            //Object[] params={userName,userRole};
            //应该使用一个ArrayList去承接参数
            ArrayList<Object> list = new ArrayList<Object>();

            if (!StringUtils.isNullOrEmpty(userName)){
                sql.append(" and u.userName like ?");
                list.add("%"+userName+"%");
            }
            if (userRole>0){
                sql.append(" and r.id=?");
                list.add(userRole);
            }
            //将list转换为数组
            Object[] params=list.toArray();
            rs = BaseDao.execute(connection, pstm, rs, sql.toString(), params);
            //rs是list链表形式，查询结果是count存储在里面
            if (rs.next()){
                userCount = rs.getInt("count");
            }
            BaseDao.closeResource(null,pstm,rs);
        }
        return userCount;
    }

    @Override
    public List<User> getUserList(Connection connection, String userName, int userRole, int currentPageNo, int pageSize) throws SQLException {
        //存放查询到的用户的链表
        List<User> userList=new ArrayList<User>();
        if (connection!=null){
            PreparedStatement pstm=null;
            ResultSet rs=null;
            //存放参数的链表，后续转为数组传入执行sql的语句
            ArrayList<Object> list=new ArrayList<Object>();
            StringBuffer sql=new StringBuffer();
            sql.append("select u.*,r.roleName as userRoleName from smbms_user u,smbms_role r where u.userRole=r.id");
            //当前请求的页面第一个用户的序号
            int currentPageFirstUserNo=0;
            if (!StringUtils.isNullOrEmpty(userName)){
                sql.append(" and u.userName like ?");
                list.add("%"+userName+"%");
            }
            if (userRole>0){
                sql.append(" and r.id=?");
                list.add(userRole);
            }
            sql.append(" order by creationDate DESC limit ?,?");
            currentPageFirstUserNo=(currentPageNo-1)*pageSize;
            list.add(currentPageFirstUserNo);
            list.add(pageSize);

            //数据准备完毕，开始准备执行sql
            Object[] params = list.toArray();
            rs = BaseDao.execute(connection, pstm, rs, sql.toString(), params);
            while(rs.next()){
                User _user=new User();
                _user.setId(rs.getInt("id"));
                _user.setUserCode(rs.getString("userCode"));
                _user.setUserName(rs.getString("userName"));
                _user.setGender(rs.getInt("gender"));
                _user.setBirthday(rs.getDate("birthday"));
                _user.setPhone(rs.getString("phone"));
                _user.setUserRole(rs.getInt("userRole"));
                _user.setUserRoleName(rs.getString("userRoleName"));
                userList.add(_user);
            }
            BaseDao.closeResource(null,pstm,rs);
        }
        return userList;
    }

    @Override
    public User getUserById(Connection connection, int id) throws SQLException {
        User user = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;

        if (connection!=null){
            Object[] params={id};
            String sql = "select u.*, r.roleName as userRoleName from smbms_user u, smbms_role r where u.id=? and u.userRole=r.id";
            rs=BaseDao.execute(connection,pstm,rs,sql,params);
            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setUserCode(rs.getString("userCode"));
                user.setUserName(rs.getString("userName"));
                user.setUserPassword(rs.getString("userPassword"));
                user.setGender(rs.getInt("gender"));
                user.setBirthday(rs.getDate("birthday"));
                user.setPhone(rs.getString("phone"));
                user.setAddress(rs.getString("address"));
                user.setUserRole(rs.getInt("userRole"));
                user.setCreatedBy(rs.getInt("createdBy"));
                user.setCreationDate(rs.getDate("creationDate"));
                user.setModifyBy(rs.getInt("modifyBy"));
                user.setModifyDate(rs.getDate("modifyDate"));
                user.setUserRoleName(rs.getString("userRoleName"));
            }
        }
        BaseDao.closeResource(null,pstm,rs);
        return user;
    }


}
