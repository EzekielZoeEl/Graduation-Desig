package cn.com.zonesion.powercontrol.DbOperation;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
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

import cn.com.zonesion.powercontrol.MainActivity;
import cn.com.zonesion.powercontrol.R;
import cn.com.zonesion.powercontrol.fragment.MoreInformationFragment;

public class Modify extends AppCompatActivity implements View.OnClickListener{

    private String realCode;
    private UserDao userDao;
    private Button mBtactivityModify;
    private RelativeLayout mRlactivityTop;
    private ImageView mIvactivityBack;
    private LinearLayout mLlactivityBody;
    private EditText mEtactivityUsername;
    private EditText mEtactivityPassword1;
    private EditText mEtactivityPassword2;
    private EditText mEtactivityPhonecodes;
    private ImageView mIvactivityShowcode;
    private RelativeLayout mRlactivityBottom;
    private Handler mainHandler; //主线程

    SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);

        initView();

        //将验证码用图片的形式显示出来
        mIvactivityShowcode.setImageBitmap(Code.getInstance().createBitmap());
        realCode = Code.getInstance().getCode().toLowerCase();
    }

    private void initView(){
        mBtactivityModify = findViewById(R.id.bt_modify); //注册按钮
        mRlactivityTop = findViewById(R.id.rl_top);
        mIvactivityBack = findViewById(R.id.iv_back); //返回主界面
        mLlactivityBody = findViewById(R.id.ll_body);
        mEtactivityPassword1 = findViewById(R.id.et_password1); //输入密码
        mEtactivityPassword2 = findViewById(R.id.et_password2); //确认密码
        mEtactivityPhonecodes = findViewById(R.id.et_phoneCodes); //验证码
        mIvactivityShowcode = findViewById(R.id.iv_showCode); //刷新验证码
        mRlactivityBottom = findViewById(R.id.rl_bottom);

        userDao = new UserDao();

        mainHandler = new Handler(getMainLooper()); //获取主线程

        /**
         * 注册页面能点击的就三个地方
         * top处返回箭头、刷新验证码图片、注册按钮
         */
        mIvactivityBack.setOnClickListener(this); //返回箭头
        mIvactivityShowcode.setOnClickListener(this); //刷新验证码
        mBtactivityModify.setOnClickListener(this); //修改按钮
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back: //返回
                Intent intent1 = new Intent(Modify.this, MainActivity.class);
                startActivity(intent1);
                finish();
                break;
            case R.id.iv_showCode:    //改变随机验证码的生成
                mIvactivityShowcode.setImageBitmap(Code.getInstance().createBitmap());
                realCode = Code.getInstance().getCode().toLowerCase();
                break;
            case R.id.bt_modify:    //修改按钮
                //获取用户输入的密码、验证码
                final String password = mEtactivityPassword1.getText().toString().trim();
                String password2 = mEtactivityPassword2.getText().toString().trim();
                String phoneCode = mEtactivityPhonecodes.getText().toString().toLowerCase();
                sharedPreferences = getSharedPreferences("account", Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                final String uname = sharedPreferences.getString("user_name", null);
                //修改验证
                if (!TextUtils.isEmpty(password) && !TextUtils.isEmpty(phoneCode) ) {
                    if (phoneCode.equals(realCode)) {
                        if(password.equals(password2)){
                            final Userinfo item = new Userinfo();
                            item.setUpass(password);
                            item.setUname(uname);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    final int iRow = userDao.editUser(item);
                                    mainHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            CommonUtils.showDlgMsg(Modify.this, "修改成功");
                                            Intent intent = new Intent(Modify.this, LoginActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                                }
                            }).start();
                        } else {
                            CommonUtils.showDlgMsg(Modify.this, "两次密码不匹配，请检查");
                        }
                    } else {
                        CommonUtils.showDlgMsg(Modify.this, "验证码错误,注册失败");
                    }
                }else {
                    CommonUtils.showDlgMsg(Modify.this, "未完善信息，注册失败");
                }
                break;
        }
    }
}