package com.itan.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

//操作数据库的基础公共类
public class BaseDao {
    private  static String driver;
    private  static String url;
    private  static String username;
    private  static String password;

    //静态代码块，类加载的时候就初始化了
    static {
        init();
    }

    public static void init(){
        //通过类加载器获取到对应的资源
        InputStream is = BaseDao.class.getClassLoader().getResourceAsStream("db.properties");
        Properties properties = new Properties();
        try {
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        driver=properties.getProperty("driver");
        url=properties.getProperty("url");
        username=properties.getProperty("username");
        password=properties.getProperty("password");
    }

    //获取数据库的连接
    public static Connection getConnection(){
        Connection connection=null;
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    //编写查询公共方法
    public static ResultSet execute(Connection connection,PreparedStatement preparedStatement,ResultSet resultSet,String sql,Object[] params) throws SQLException {
        preparedStatement = connection.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            preparedStatement.setObject(i+1,params[i]);
        }
        resultSet = preparedStatement.executeQuery();
        return resultSet;
    }

    //编写增删改公共方法
    public static int execute(Connection connection,PreparedStatement preparedStatement,String sql,Object[] params) throws SQLException {
        preparedStatement=connection.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            preparedStatement.setObject(i+1,params[i]);
        }
        int updateRows = preparedStatement.executeUpdate();
        return updateRows;
    }

    //释放连接资源
    public static boolean closeResource(Connection connection,PreparedStatement preparedStatement,ResultSet resultSet){
        boolean flag=true;
        if (resultSet != null) {
            try {
                resultSet.close();
                //万一关闭不成功，设置为null，让GC回收它
                resultSet=null;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                //如果关闭失败，标志位设置为false
                flag=false;
            }
        }
        if (preparedStatement != null) {
            try {
                preparedStatement.close();
                //万一关闭不成功，设置为null，让GC回收它
                preparedStatement=null;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                //如果关闭失败，标志位设置为false
                flag=false;
            }
        }
        if (connection != null) {
            try {
                connection.close();
                //万一关闭不成功，设置为null，让GC回收它
                connection=null;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                //如果关闭失败，标志位设置为false
                flag=false;
            }
        }
        return flag;
    }
}
