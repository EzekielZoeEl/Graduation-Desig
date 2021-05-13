package cn.com.zonesion.powercontrol.DbOperation;


import java.util.ArrayList;
import java.util.List;

/**
 * 用户数据库操作类
 * 实现用户的CRUD操作
 */
public class UserDao extends DbOpenHelper {




    /**
     * 添加用户信息 Create
     * @param item 要添加的用户
     * @return int 影响的行数
     */
    public int addUser(Userinfo item){
        int iRow = 0;
        try{
            getConnection(); //取得连接信息
            String sql = "insert into account(uname, upass) values(?, ?)";
            pStmt = conn.prepareStatement(sql);
            pStmt.setString(1, item.getUname());
            pStmt.setString(2, item.getUpass());
            iRow = pStmt.executeUpdate();
        }catch(Exception ex){
            ex.printStackTrace();
        }finally {
            closeAll();
        }
        return iRow;
    }


    /**
     * 按用户名和密码查询用户信息 Read
     * @param uname 用户名
     * @param upass 密码
     * @return Userinfo实例
     */
    public Userinfo getUserByUnameAndUpass(String uname, String upass){
        Userinfo item = null;
        try{
            getConnection(); //取得连接信息
            String sql = "select * from account where uname=? and upass=?";
            pStmt = conn.prepareStatement(sql);
            pStmt.setString(1, uname);
            pStmt.setString(2, upass);
            rs = pStmt.executeQuery();
            if(rs.next()){
                item = new Userinfo();
                item.setId(rs.getInt("id"));
                item.setUname(uname);
                item.setUpass(upass);
                item.setCreatDt(rs.getString("createDt"));
                item.setSensora(rs.getString("sensorA"));
                item.setSensorb(rs.getString("sensorB"));
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }finally {
            closeAll();
        }
        return item;
    }


    /**
     * 修改用户信息 Update
     * @param item 要修改的用户
     * @return int 影响的行数
     */
    public int editUser(Userinfo item){
        int iRow = 0;
        try{
            getConnection(); //取得连接信息
            String sql = "update account set upass=? where uname=?";
            pStmt = conn.prepareStatement(sql);
            pStmt.setString(2, item.getUname());
            pStmt.setString(1, item.getUpass());
            iRow = pStmt.executeUpdate();
        }catch(Exception ex){
            ex.printStackTrace();
        }finally {
            closeAll();
        }
        return iRow;
    }

    /**
     * 根据id删除用户信息
     * @param id 要删除的用户id
     * @return int 影响的行数
     */
    public int delUser(int id){
        int iRow = 0;
        try{
            getConnection(); //取得连接信息
            String sql = "delete from account where id=?";
            pStmt = conn.prepareStatement(sql);
            pStmt.setInt(1,id);
            iRow = pStmt.executeUpdate();
        }catch(Exception ex){
            ex.printStackTrace();
        }finally {
            closeAll();
        }
        return iRow;
    }
}
