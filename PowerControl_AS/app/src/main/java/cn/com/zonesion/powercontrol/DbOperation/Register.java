package cn.com.zonesion.powercontrol.DbOperation;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import cn.com.zonesion.powercontrol.MakeAnAppoint;
import cn.com.zonesion.powercontrol.R;

public class Register extends AppCompatActivity implements View.OnClickListener{

    private String realCode;
    private UserDao userDao;
    private Button mBtRegisteractivityRegister;
    private RelativeLayout mRlRegisteractivityTop;
    private ImageView mIvRegisteractivityBack;
    private LinearLayout mLlRegisteractivityBody;
    private EditText mEtRegisteractivityUsername;
    private EditText mEtRegisteractivityPassword1;
    private EditText mEtRegisteractivityPassword2;
    private EditText mEtRegisteractivityPhonecodes;
    private ImageView mIvRegisteractivityShowcode;
    private RelativeLayout mRlRegisteractivityBottom;
    private Handler mainHandler; //主线程



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();

        //将验证码用图片的形式显示出来
        mIvRegisteractivityShowcode.setImageBitmap(Code.getInstance().createBitmap());
        realCode = Code.getInstance().getCode().toLowerCase();
    }

    private void initView(){
        mBtRegisteractivityRegister = findViewById(R.id.bt_registeractivity_register); //注册按钮
        mRlRegisteractivityTop = findViewById(R.id.rl_registeractivity_top);
        mIvRegisteractivityBack = findViewById(R.id.iv_registeractivity_back); //返回主界面
        mLlRegisteractivityBody = findViewById(R.id.ll_registeractivity_body);
        mEtRegisteractivityUsername = findViewById(R.id.et_registeractivity_username); //输入用户名
        mEtRegisteractivityPassword1 = findViewById(R.id.et_registeractivity_password1); //输入密码
        mEtRegisteractivityPassword2 = findViewById(R.id.et_registeractivity_password2); //确认密码
        mEtRegisteractivityPhonecodes = findViewById(R.id.et_registeractivity_phoneCodes); //验证码
        mIvRegisteractivityShowcode = findViewById(R.id.iv_registeractivity_showCode); //刷新验证码
        mRlRegisteractivityBottom = findViewById(R.id.rl_registeractivity_bottom);

        userDao = new UserDao();

        mainHandler = new Handler(getMainLooper()); //获取主线程

        /**
         * 注册页面能点击的就三个地方
         * top处返回箭头、刷新验证码图片、注册按钮
         */
        mIvRegisteractivityBack.setOnClickListener(this); //返回箭头
        mIvRegisteractivityShowcode.setOnClickListener(this); //注册按钮
        mBtRegisteractivityRegister.setOnClickListener(this); //刷新验证码
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_registeractivity_back: //返回登录页面
                Intent intent1 = new Intent(this, LoginActivity.class);
                startActivity(intent1);
                finish();
                break;
            case R.id.iv_registeractivity_showCode:    //改变随机验证码的生成
                mIvRegisteractivityShowcode.setImageBitmap(Code.getInstance().createBitmap());
                realCode = Code.getInstance().getCode().toLowerCase();
                break;
            case R.id.bt_registeractivity_register:    //注册按钮
                //获取用户输入的用户名、密码、验证码
                final String username = mEtRegisteractivityUsername.getText().toString().trim();
                final String password = mEtRegisteractivityPassword1.getText().toString().trim();
                String password2 = mEtRegisteractivityPassword2.getText().toString().trim();
                String phoneCode = mEtRegisteractivityPhonecodes.getText().toString().toLowerCase();
                //注册验证
                if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(phoneCode) ) {
                    if (phoneCode.equals(realCode)) {
                        if(password.equals(password2)){
                            final Userinfo item = new Userinfo();
                            item.setUname(username);
                            item.setUpass(password);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    final int iRow = userDao.addUser(item);
                                    mainHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(Register.this, R.style.Theme_AppCompat_Light_Dialog_Alert);
                                            builder
                                                    .setTitle("账号注册")
                                                    .setMessage("注册成功")
                                                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            Intent intent = new Intent(Register.this, LoginActivity.class);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    })
                                                    .create().show();

                                        }
                                    });
                                }
                            }).start();
                        } else {
                            CommonUtils.showDlgMsg(Register.this, "两次密码不匹配，请检查");
                        }
                    } else {
                        CommonUtils.showDlgMsg(Register.this, "验证码错误,注册失败");
                    }
                }else {
                    CommonUtils.showDlgMsg(Register.this, "未完善信息，注册失败");
                }
                break;
        }
    }
}
