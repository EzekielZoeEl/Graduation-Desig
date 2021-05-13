package cn.com.zonesion.powercontrol.fragment;

import java.util.Timer;
import java.util.TimerTask;

import cn.com.zonesion.powercontrol.DbOperation.CommonUtils;
import cn.com.zonesion.powercontrol.DbOperation.LoginActivity;
import cn.com.zonesion.powercontrol.MakeAnAppoint;
import cn.com.zonesion.powercontrol.R;
import cn.com.zonesion.powercontrol.view.DialChartIlluminationView;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
/**
 * HomePageFragment是用来展示首页页面的Fragment
 */

public class HomePageFragment extends BaseFragment implements
		OnCheckedChangeListener {
	private TextView textIlluminationState;
	private DialChartIlluminationView dialChartIlluminationView;
	private TextView textLightState;
	private ImageView imageLightState;
	private Button openOrCloseLight;
	private TextView textPowerState;
	private ImageView imagePowerState;
	private Button openOrClosePower;
	private SeekBar seekBarThreshold;
	private TextView textAmount;
	private RadioButton securityModule;
	private RadioButton manualModule;
	private TextView securityModuleTip;
	private TextView manualModuleTip;

	private int startTime = 0;
	private int gapTime; //预约的时长
	private Chronometer recordChronometer;

	private Button test,test2;

	/**
	 * 灯光控制器,光照强度的MAC地址
	 */
	private String sensorAMac;
	private String sensorBMac;
	/**
	 * SharedPreferences实例，用于存取数据
	 */
	private SharedPreferences preferences, sharedPreferences,sharedPreferencesmac;
	/**
	 * 用于存储数据
	 */
	private SharedPreferences.Editor editor,editor1,editormac;
	/**
	 * 用户设置的亮度，当小于这个亮度的时候，打开实验室灯光
	 */
	private int lightLimit;
	/**
	 * 当前的环境亮度
	 */
	private float currentLight;
	/**
	 * 当前是否处于自动模式的标识 true:处于自动模式 false:处于手动模式
	 */
	private boolean isSecurityMode = true;
	private int numResult1;
	
	private Handler handler;
	 private boolean state;

	/**
	 * 初始化用户的ID和KEY以及使用的服务器地址
	 */
	private void initSetting() {
		securityModule.setOnCheckedChangeListener(this);
		manualModule.setOnCheckedChangeListener(this);
		securityModule.setChecked(true);
	}

	@Override
	public View initView() {
		View view = View.inflate(mContext, R.layout.home_page_layout, null);
		textIlluminationState = (TextView) view
				.findViewById(R.id.text_illumination_state);
		dialChartIlluminationView = (DialChartIlluminationView) view
				.findViewById(R.id.illumination_circle_view);
		textLightState = (TextView) view.findViewById(R.id.text_light_state);
		imageLightState = (ImageView) view.findViewById(R.id.image_light_state);
		openOrCloseLight = (Button) view.findViewById(R.id.open_or_close_light);
		textPowerState = (TextView) view
				.findViewById(R.id.text_power_state);
		imagePowerState = (ImageView) view
				.findViewById(R.id.image_power_state);
		openOrClosePower = (Button) view
				.findViewById(R.id.open_or_close_power);
		seekBarThreshold = (SeekBar) view.findViewById(R.id.seek_bar_threshold);
		textAmount = (TextView) view.findViewById(R.id.text_amount);
		securityModule = (RadioButton) view.findViewById(R.id.security_module);
		manualModule = (RadioButton) view.findViewById(R.id.manual_module);
		securityModuleTip = (TextView) view
				.findViewById(R.id.security_module_tip);
		manualModuleTip = (TextView) view.findViewById(R.id.manual_module_tip);

		recordChronometer = (Chronometer)view.findViewById(R.id.time);

//		test = (Button) view.findViewById(R.id.test);
//		test2 = (Button) view.findViewById(R.id.test2);


		return view;
	}

	@Override
	public void initData() {
		super.initData();
		preferences = getActivity().getSharedPreferences("user_info",
				Context.MODE_PRIVATE);
		sharedPreferencesmac = getActivity().getSharedPreferences("account",
				Context.MODE_PRIVATE);
		editor = preferences.edit();
		editormac = sharedPreferencesmac.edit();
		handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0) {
                   
                	sensorAMac = sharedPreferencesmac.getString("sensor_a", null);
                	sensorBMac = sharedPreferencesmac.getString("sensor_b", null);
                    state = preferences.getBoolean("state", false);
                    if (sensorAMac == "00:00:00:00:00:00:00:00" && sensorBMac == "00:00:00:00:00:00:00:00" && !state) {
                    	Toast.makeText(mContext, "请向管理员申请设备！", Toast.LENGTH_SHORT).show();
                    }else{
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
        }, 0,30000);
		initSetting();
		// 设置SeekBar的监听事件，监听当SeekBar的进度改变的时候的响应事件
		seekBarThreshold
				.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
					@Override
					public void onProgressChanged(SeekBar seekBar, int i,
							boolean fromUser) {
						lightLimit = i;// SeekBar的进度即为湿度的上限值
						textAmount.setText(String.valueOf(i));
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
					}

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						lightTooLow();
						preferences
								.edit()
								.putInt("lightLimit",
										lightLimit).apply();
					}
				});
		seekBarThreshold.setProgress(preferences.getInt(
				"lightLimit", 0));
		openOrCloseLight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (sensorBMac != null) {
					if (openOrCloseLight.getText().equals("开启")) {
						new Thread(new Runnable() {
							@Override
							public void run() {
								sendMessageTo(sensorBMac, "{OD1=48,D1=?}");
							}
						}).start();

					}
					if (openOrCloseLight.getText().equals("关闭")) {
						new Thread(new Runnable() {
							@Override
							public void run() {
								sendMessageTo(sensorBMac, "{CD1=48,D1=?}");
							}
						}).start();

					}
				} else {
					Toast.makeText(mContext, "请等待MAC地址上线", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
		openOrClosePower.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (sensorAMac != null) {
					if (openOrClosePower.getText().equals("开启")) {
						new Thread(new Runnable() {
							@Override
							public void run() {
								sendMessageTo(sensorAMac, "{OD1=64,D1=?}");
							}
						}).start();
					}
					if (openOrClosePower.getText().equals("关闭")) {
						new Thread(new Runnable() {
							@Override
							public void run() {
								sendMessageTo(sensorAMac, "{CD1=64,D1=?}");
							}
						}).start();

					}
				} else {
					Toast.makeText(mContext, "请等待MAC地址上线", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});


		sharedPreferences = getActivity().getSharedPreferences("appointment", Context.MODE_PRIVATE);
		editor1 = sharedPreferences.edit();
		gapTime = sharedPreferences.getInt("gap_time", 5);

		final String test1 = Integer.toString(gapTime);

//		test.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				CommonUtils.showShortMsg(mContext, sensorAMac);
//			}
//		});

//		test2.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				CommonUtils.showShortMsg(mContext, test1);
//				recordChronometer.stop();
//			}
//		});


		/**
		 * 计时器监听，当电源开关长时间达到预约时间后询问是否继续使用
		 * 若选择关闭电源则自动关闭电源并将开启按钮设置为不可点击
		 * 若选择继续使用则跳转到预约界面重新预约时长
		 */
		recordChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
			@Override
			public void onChronometerTick(Chronometer chronometer) {
				if((SystemClock.elapsedRealtime() - recordChronometer.getBase()) >= (startTime + gapTime)  * 1000)
				{
					recordChronometer.stop();
					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.Theme_AppCompat_Light_Dialog_Alert);
					builder.setTitle("预约到期提醒")
							.setMessage("您预约的时间已到期，是否继续使用？")
							.setPositiveButton("继续使用", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									Intent intent = new Intent(getActivity(), MakeAnAppoint.class);
									startActivity(intent);
//									getActivity().finish();
								}
							})
							.setNegativeButton("关闭电源", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									openOrClosePower.setEnabled(false);
									if (sensorAMac != null) {
										if (openOrClosePower.getText().equals("关闭")) {
											new Thread(new Runnable() {
												@Override
												public void run() {
													sendMessageTo(sensorAMac, "{CD1=64,D1=?}");
												}
											}).start();
										}
									}
								}
							})
							.create().show();
				}
			}
		});
	}

	/**
	 * 当当前亮度低于设定值的时候，打开实验室灯光
	 */
	private void lightTooLow() {
		if (sensorBMac != null) {
			if (isSecurityMode == true) {
				if (lightLimit >= currentLight) 
					sendMessageTo(sensorBMac, "{OD1=48,D1=?}");
				else 
					sendMessageTo(sensorBMac, "{CD1=48,D1=?}");
			} else {
				Toast.makeText(mContext, "手动模式", Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(mContext, "请等待MAC地址上线", Toast.LENGTH_SHORT).show();
		}
	}


	@Override
	public void onDestroy() {
		// lcApplication.unregisterOnWSNDataListener(this);
		super.onDestroy();
	}

	/**
	 * 当服务器信息到达时回调的方法
	 * 
	 * @param mac
	 *            mac地址
	 * @param tag
	 *            数据的key值
	 * @param val
	 *            数据的value值
	 */

	@SuppressWarnings("deprecation")
	@Override
	public void onZXBee(String mac, String tag, String val) {
		if (tag.equalsIgnoreCase("A2") && mac.equalsIgnoreCase(sensorAMac)) {
			textIlluminationState.setText("在线");
			if (isAdded()) {
				textIlluminationState.setTextColor(getResources().getColor(
						R.color.line_text_color));
			}
			currentLight = Float.parseFloat(val);
			dialChartIlluminationView.setCurrentStatus(currentLight);
			dialChartIlluminationView.invalidate();
		}
		
		if (tag.equalsIgnoreCase("D1") && mac.equalsIgnoreCase(sensorAMac)) {
			textPowerState.setText("在线");
			if (isAdded()) {
				textPowerState.setTextColor(getResources().getColor(
						R.color.line_text_color));
			}
			
			int numResult = Integer.parseInt(val);
			if ((numResult & 0X40) == 0x40) {
				openOrClosePower.setText("关闭");
					recordChronometer.setBase(SystemClock.elapsedRealtime());
					recordChronometer.start();
				if (isAdded()) {
					imagePowerState.setImageDrawable(getResources().getDrawable(
							R.drawable.power_on));
					openOrClosePower.setBackground(getResources().getDrawable(
							R.drawable.close));
				}
			} else {
				openOrClosePower.setText("开启");
				recordChronometer.stop();
				startTime = 0;
				if (isAdded()) {
					imagePowerState.setImageDrawable(getResources().getDrawable(
							R.drawable.power_off));
					openOrClosePower.setBackground(getResources().getDrawable(
							R.drawable.open));
				}
			}
		}
		
		if (tag.equalsIgnoreCase("D1") && mac.equalsIgnoreCase(sensorBMac)) {
			textLightState.setText("在线");
			if (isAdded()) {
				textLightState.setTextColor(getResources().getColor(
						R.color.line_text_color));
			}
			
			int numResult = Integer.parseInt(val);
			if ((numResult & 0X10) == 0x10) {
				openOrCloseLight.setText("关闭");
				if (isAdded()) {
					imageLightState.setImageDrawable(getResources().getDrawable(
							R.drawable.open_lamp));
					openOrCloseLight.setBackground(getResources().getDrawable(
							R.drawable.close));
				}
				
			} else {
				openOrCloseLight.setText("开启");
				if (isAdded()) {
					imageLightState.setImageDrawable(getResources().getDrawable(
							R.drawable.close_lamp));
					openOrCloseLight.setBackground(getResources().getDrawable(
							R.drawable.open));
				}
				
			}
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		switch (buttonView.getId()) {
		case R.id.security_module:
			if (isChecked) {
				isSecurityMode = true;
				openOrCloseLight.setEnabled(false);
				//openOrClosePower.setEnabled(false);
				securityModuleTip.setVisibility(View.VISIBLE);
				manualModuleTip.setVisibility(View.GONE);
			}
			break;
		case R.id.manual_module:
			if (isChecked) {
				isSecurityMode = false;
				openOrCloseLight.setEnabled(true);
				openOrClosePower.setEnabled(true);
				securityModuleTip.setVisibility(View.GONE);
				manualModuleTip.setVisibility(View.VISIBLE);
			}
			break;
		}
	}
}
