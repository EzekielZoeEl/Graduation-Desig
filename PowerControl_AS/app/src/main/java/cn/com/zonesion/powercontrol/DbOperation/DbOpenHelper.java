package cn.com.zonesion.powercontrol.DbOperation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class DbOpenHelper {
    private static String CLS = "com.mysql.jdbc.Driver";
    private static String URL = "jdbc:mysql://192.168.188.1:3306/userdata"; //不能使用localhost，必须使用具体ip地址
    private static String USER = "root";
    private static String PWD = "995221";

    public static Connection conn; //连接对象
    public static Statement stmt; //命令集
    public static PreparedStatement pStmt; //预编译命令集
    public static ResultSet rs; //结果集

    //获取连接的方法
    public static void getConnection(){
        try{
            Class.forName(CLS);
            conn = DriverManager.getConnection(URL, USER, PWD);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    //关闭数据库
    public static void closeAll(){
        try{
            if(rs != null)
            {
                rs.close();
                rs = null;
            }

            if(stmt != null)
            {
                stmt.close();
                stmt = null;
            }

            if(pStmt != null)
            {
                pStmt.close();
                pStmt = null;
            }

            if(conn != null)
            {
                conn.close();
                conn = null;
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
