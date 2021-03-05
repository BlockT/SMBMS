package com.itan.service.role;

import com.itan.dao.BaseDao;
import com.itan.dao.role.RoleDao;
import com.itan.dao.role.RoleDaoImpl;
import com.itan.pojo.Role;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoleServiceImpl implements RoleService{

    //引入Dao
    private RoleDao roleDao;
    public RoleServiceImpl() {
        roleDao=new RoleDaoImpl();
    }

    @Override
    public List<Role> getRoleList() {
        List<Role> roleList = new ArrayList<>();
        Connection connection = null;
        try {
            connection = BaseDao.getConnection();
            roleList = roleDao.getRoleList(connection);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            BaseDao.closeResource(connection,null,null);
        }
        return roleList;
    }
    /*@Test
    public void Test(){
        List<Role> roleList = new RoleServiceImpl().getRoleList();
        for (Role role:roleList
             ) {

        }
        for (Role role : roleList) {
            System.out.println(role.getRoleName());
        }

    }*/
}
