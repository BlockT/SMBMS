package com.itan.dao.role;

import com.itan.dao.BaseDao;
import com.itan.pojo.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoleDaoImpl implements RoleDao{
    @Override
    public List<Role> getRoleList(Connection connection) throws SQLException {
        List<Role> roleList = new ArrayList<>();
        if (connection!=null){
            PreparedStatement pstm=null;
            ResultSet rs=null;
            Object[] params={};
            StringBuffer sql=new StringBuffer();
            sql.append("select * from smbms_role");
            rs = BaseDao.execute(connection, pstm, rs, sql.toString(), params);
            while (rs.next()) {
                Role _role = new Role();
                _role.setId(rs.getInt("id"));
                _role.setRoleCode(rs.getString("roleCode"));
                _role.setRoleName(rs.getString("roleName"));
                roleList.add(_role);
            }
            BaseDao.closeResource(null,pstm,rs);
        }
        return roleList;
    }
}
