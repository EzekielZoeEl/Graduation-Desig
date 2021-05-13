package cn.com.zonesion.powercontrol.DbOperation;

import java.io.Serializable;

/**
 * 用户信息实体类
 */
public class Userinfo implements Serializable {

    private int id; //用户的id
    private String uname; //用户名
    private String upass; //用户密码
    private String createDt; //创建时间
    private String sensora; //A传感器MAC地址
    private String sensorb; //B传感器MAC地址

    public Userinfo() {
    }

    public Userinfo(int id, String uname, String upass, String creatDt, String sensora, String sensorb) {
        this.id = id;
        this.uname = uname;
        this.upass = upass;
        this.createDt = creatDt;
        this.sensora = sensora;
        this.sensorb = sensorb;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getUpass() {
        return upass;
    }

    public void setUpass(String upass) {
        this.upass = upass;
    }

    public String getCreatDt() {
        return createDt;
    }

    public void setCreatDt(String creatDt) {
        this.createDt = creatDt;
    }

    public String getSensora() { return sensora; }

    public void setSensora(String sensora) { this.sensora = sensora; }

    public String getSensorb() { return sensorb; }

    public void setSensorb(String sensorb) {
        this.sensorb = sensorb;
    }

}
