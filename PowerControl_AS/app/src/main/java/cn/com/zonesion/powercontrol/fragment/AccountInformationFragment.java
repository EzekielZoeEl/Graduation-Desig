package cn.com.zonesion.powercontrol.fragment;

import cn.com.zonesion.powercontrol.DbOperation.*;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import cn.com.zonesion.powercontrol.MainActivity;
import cn.com.zonesion.powercontrol.MakeAnAppoint;
import cn.com.zonesion.powercontrol.R;


import static android.support.v4.os.LocaleListCompat.create;

public class AccountInformationFragment extends BaseFragment implements View.OnClickListener{
    private Button btn_modify, btn_del, btn_quit; //修改密码按钮，注销账号按钮和退出登录按钮
    private TextView tv_createDt, tv_uname; //用户名和注册时间
    private String createDt, uname;

    SharedPreferences sharedPreferences,preferences;
    private SharedPreferences.Editor editor,editor1;

    private Handler mainHandler; //主线程

    private UserDao userDao; //用户数据库操作类

    private EditText sensorAedit;
    private EditText sensorBedit;

    private Handler handler;
    private boolean state;

    private String sensorAMac;
    private String sensorBMac;

    private Button openOrClosePower;


    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.account_information_layout, null);
        btn_del = view.findViewById(R.id.btn_del);
        btn_modify = view.findViewById(R.id.btn_modify);
        btn_quit = view.findViewById(R.id.btn_quit);

        tv_createDt = view.findViewById(R.id.tv_createDt);
        tv_uname = view.findViewById(R.id.tv_uname);

        sensorAedit = view.findViewById(R.id.sensor_a_edit);
        sensorBedit = view.findViewById(R.id.sensor_b_edit);

        openOrClosePower = view.findViewById(R.id.open_or_close_power);

        userDao = new UserDao();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        sharedPreferences = getActivity().getSharedPreferences("account", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        uname = sharedPreferences.getString("user_name", null);
//        uname = sharedPreferences.getString("sensor_a", null);
        tv_uname.setText(uname);


        createDt = sharedPreferences.getString("createDt", null);
        tv_createDt.setText(createDt);
    }

    @Override
    public void onZXBee(String mac, String tag, String val) {

    }

    //修改密码方法
    private void doModify() {
        //跳转到修改密码界面
        Intent intent = new Intent(getActivity(), Modify.class);
        startActivity(intent);

    }


    //注销账号办法
    private void doDel() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.Theme_AppCompat_Light_Dialog_Alert);
                 builder
                .setTitle("注销确认")
                .setMessage("您确认要注销此账号吗？")
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doDelUser();
                    }
                })
                .setNegativeButton("取消", null)
                         .create().show();
    }

    private void doDelUser(){
        final int id = sharedPreferences.getInt("id", 0);
        final int iRow = userDao.delUser(id);
        doQuit();
    }

    private void askIfQuit(){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.Theme_AppCompat_Light_Dialog_Alert);
            builder.setTitle("电源检查")
                    .setMessage("退出登陆前请检查电源是否关闭！若确认已关闭，请点击确认退出")
                    .setPositiveButton("确认退出", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            doQuit();
                        }
                    })
                    .setNegativeButton("再检查一下", null)
                    .create().show();
    }

    //退出登录办法
    private void doQuit() {
        //跳转到登录界面
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();

    }



    @Override
    public void initData() {
            // TODO Auto-generated method stub
            super.initData();
            btn_quit.setOnClickListener(this);
            btn_modify.setOnClickListener(this);
            btn_del.setOnClickListener(this);

        preferences = getActivity().getSharedPreferences("user_info",
                Context.MODE_PRIVATE);
        editor = preferences.edit();
            handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (msg.what == 0) {

                        sensorAMac = preferences.getString("mac_a", null);
                        sensorBMac = preferences.getString("mac_b", null);
                        state = preferences.getBoolean("state", false);
                        if (sensorAMac == null && sensorBMac == null && !state) {
                            Toast.makeText(mContext, "请绑定设备", Toast.LENGTH_SHORT).show();
                        } else {
                            sendMessageTo(sensorAMac, "{A2=?}");
                            sendMessageTo(sensorBMac, "{D1=?}");
                        }
                    }
                }
            };
            new Timer().schedule(new TimerTask() {

                @Override
                public void run() {
                    Message message = new Message();
                    message.what = 0;
                    handler.sendMessage(message);
                }
            }, 0, 30000);
        }


    @Override
    public void onClick(View view){
            switch (view.getId()) {
                case R.id.btn_del:
                    doDel();
                    break;

                case R.id.btn_quit:
                    askIfQuit();
                    break;

                case R.id.btn_modify:
                    doModify();
                    break;
            }
        }
}

