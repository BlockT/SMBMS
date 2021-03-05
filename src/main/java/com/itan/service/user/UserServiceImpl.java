package com.itan.service.user;

import com.itan.dao.BaseDao;
import com.itan.dao.user.UserDao;
import com.itan.dao.user.UserDaoImpl;
import com.itan.pojo.User;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class UserServiceImpl implements UserService {
    //因为业务层一定要调用Dao层操作数据，所以在无参构造中调用到daoimpl
    //以后这些事会交给容器做
    private UserDao userDao;

    public UserServiceImpl() {
        userDao=new UserDaoImpl();
    }

    @Override
    public User login(String userCode, String userPassword) {
        Connection connection = null;
        User user = null;
        try {
            connection = BaseDao.getConnection();
            //通过业务层调用对应的具体数据库操作
            user = userDao.getLoginUser(connection, userCode);
            if (user != null) {
                if (!user.getUserPassword().equals(userPassword)){
                    user=null;
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            BaseDao.closeResource(connection, null, null);
        }
        return user;
    }

    @Override
    public boolean updatePwd(int id, String password) {
        Connection connection=BaseDao.getConnection();
        int executeResult=0;
        boolean flag=false;
        try {
            executeResult = userDao.updatePwd(connection, id, password);
            if (executeResult>0){
                flag=true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            BaseDao.closeResource(connection,null,null);
        }
        return flag;
    }

    @Override
    public int getUserCount(String userName, int userRole) {
        Connection connection=null;
        int userCount=0;
        try {
            connection=BaseDao.getConnection();
            userCount = userDao.getUserCount(connection, userName, userRole);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            BaseDao.closeResource(connection,null,null);
        }
        return userCount;
    }

    @Override
    public List<User> getUserList(String userName, int userRole, int currentPageNo, int pageSize) {
        Connection connection=null;
        List<User> userList=new ArrayList<>();
        try {
            connection=BaseDao.getConnection();
            userList = userDao.getUserList(connection, userName, userRole, currentPageNo, pageSize);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            BaseDao.closeResource(connection,null,null);
        }
        return userList;
    }

    @Override
    public User userCodeExist(String userCode) {
        Connection connection=null;
        User user=null;

        try {
            connection = BaseDao.getConnection();
            //此处getLoginUser就是根据usercode获取用户对象，起名方式有问题
            user=userDao.getLoginUser(connection,userCode);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            BaseDao.closeResource(connection,null,null);
        }
        return user;
    }

    @Override
    public User getUserById(int id) {
        Connection connection=null;
        User user=null;
        try {
            connection=BaseDao.getConnection();
            user = userDao.getUserById(connection, id);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            BaseDao.closeResource(connection,null,null);
        }
        return user;
    }


    /*@Test
    public void test(){
        User admin = login("admin", "1234567");
        if (admin!=null){
            System.out.println(admin.getUserCode());
            System.out.println(admin.getUserPassword());
        }
        else{
            System.out.println("null");
        }
    }*/
}
