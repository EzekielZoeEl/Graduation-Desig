package cn.com.zonesion.powercontrol.DbOperation;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import cn.com.zonesion.powercontrol.MainActivity;
import cn.com.zonesion.powercontrol.MakeAnAppoint;
import cn.com.zonesion.powercontrol.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btn_login; //登录按钮
    private EditText et_uname, et_upass; //用户名，密码
    private TextView register;

    private SharedPreferences sharedPreferences,sharedPreferencesB;

    private UserDao userDao; //用户数据库操作类

    private Handler mainHandler; //主线程

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
    }


    private void initView(){

        et_uname = findViewById(R.id.et_uname);
        et_upass = findViewById(R.id.et_upass);

        btn_login = findViewById(R.id.btn_login);
        register = findViewById(R.id.register);

        userDao = new UserDao();

        mainHandler = new Handler(getMainLooper()); //获取主线程

        btn_login.setOnClickListener(this);
        register.setOnClickListener(this);
    }

    //重写点击函数
    @Override
    public void onClick(View v){
        switch (v.getId()){

            case R.id.btn_login: //登录按钮
                doLogin(); //登录方法
                break;

            case R.id.register:
                doRegister(); //注册页面
                break;
        }
    }



    private void doLogin(){
        final String uname = et_uname.getText().toString().trim();
        final String upass = et_upass.getText().toString().trim();

        if(TextUtils.isEmpty(uname)){
            CommonUtils.showShortMsg(this, "请输入用户名！");
            et_uname.requestFocus();
        }else if(TextUtils.isEmpty(upass)){
            CommonUtils.showShortMsg(this,"请输入用户密码！");
            et_upass.requestFocus();
        }else {
            sharedPreferences = getSharedPreferences("account", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("user_name", uname);
            editor.commit();
            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    final Userinfo item = userDao.getUserByUnameAndUpass(uname, upass);

                                    mainHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(item == null){
                                                CommonUtils.showDlgMsg(LoginActivity.this, "用户名或密码错误");
                                            }else{
                                                sharedPreferences = getSharedPreferences("account", MODE_PRIVATE);
                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                editor.putString("createDt", item.getCreatDt());
                                                editor.putInt("id", item.getId());
                                                editor.putString("sensor_a", item.getSensora());
                                                editor.putString("sensor_b", item.getSensorb());
                                                editor.commit();
                                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this, R.style.Theme_AppCompat_Light_Dialog_Alert);
                                                builder
                                                        .setMessage("登录成功")
                                                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                //进入主界面
                                                                Intent intent = new Intent(LoginActivity.this, MakeAnAppoint.class);
//                                                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                                                                startActivity(intent);
                                                            }
                                                        })
                                                        .create().show();
                                            }
                        }
                    });
                }
            }).start();
        }
    }

    private void doRegister(){
        Intent intent = new Intent(LoginActivity.this, Register.class);
        startActivity(intent);
    }
}
