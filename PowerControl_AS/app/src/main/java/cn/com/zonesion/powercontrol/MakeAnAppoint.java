package cn.com.zonesion.powercontrol;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import cn.com.zonesion.powercontrol.DbOperation.CommonUtils;
import cn.com.zonesion.powercontrol.DbOperation.LoginActivity;
import cn.com.zonesion.powercontrol.DbOperation.UserDao;

public class MakeAnAppoint extends AppCompatActivity implements View.OnClickListener{

    private int gapTime;
    private EditText appointmentTime;
    private Button btn_saveTime;
    SharedPreferences sharedPreferences,sharedPreferencesmac;
    SharedPreferences.Editor editor,editormac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_an_appoint);

        initView();
    }

    private void initView() {
        appointmentTime = findViewById(R.id.et_appointment);
        btn_saveTime = findViewById(R.id.btn_appointment);

        btn_saveTime.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){

            case R.id.btn_appointment: //登录按钮
                saveTime(); //登录方法
                break;

        }
    }

    private void saveTime() {
        sharedPreferences = getSharedPreferences("appointment", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        sharedPreferencesmac = getSharedPreferences("account", Context.MODE_PRIVATE);
        editormac = sharedPreferencesmac.edit();
        String sensora, sensorb;
        sensora = sharedPreferencesmac.getString("sensor_a", "00:00:00:00:00:00:00:00");
        sensorb = sharedPreferencesmac.getString("sensor_b", "00:00:00:00:00:00:00:00");

//        CommonUtils.showShortMsg(MakeAnAppoint.this, sensora);

//        if(sensora == "00:00:00:00:00:00:00:00" && sensorb == "00:00:00:00:00:00:00:00" )
        if(sensora.length() > 12)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog_Alert);
            builder.setTitle("时间预约")
                    .setMessage("您的账号中没有设备信息！请向管理员申请设备！")
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                          //进入主界面
                            Intent intent = new Intent(MakeAnAppoint.this, LoginActivity.class);
                            startActivity(intent);
                            MakeAnAppoint.this.finish();
                        }
                    })
                    .create().show();
        }else
        {
            if (appointmentTime.length() != 0) {
                gapTime = Integer.parseInt(appointmentTime.getText().toString());
                sharedPreferences = getSharedPreferences("appointment", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("gap_time", gapTime);
                editor.commit();


                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog_Alert);
                builder.setTitle("时间预约")
                        .setMessage("预约成功！")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //进入主界面
                                Intent intent = new Intent(MakeAnAppoint.this, MainActivity.class);
                                startActivity(intent);
                                MakeAnAppoint.this.finish();
                            }
                        })
                        .create().show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog_Alert);
                builder.setTitle("时间预约")
                        .setMessage("请输入时长")
                        .setPositiveButton("确认", null)
                        .create().show();
            }
        }

    }

}