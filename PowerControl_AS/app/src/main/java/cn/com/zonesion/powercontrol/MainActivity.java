package cn.com.zonesion.powercontrol;

import java.util.ArrayList;

import cn.com.zonesion.powercontrol.application.LCApplication;
import cn.com.zonesion.powercontrol.fragment.MoreInformationFragment;
import cn.com.zonesion.powercontrol.fragment.RunHomePageFragment;

import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioGroup;

public class MainActivity extends MyBaseFragmentActivity {
	private RadioGroup rgBottomTag;
	private int position = 1;
	/**
	 * 装多个Fragment的实例集合
	 */
	private ArrayList<Fragment> fragments;
	/**
	 * 缓存的Fragemnt或者上次显示的Fragment
	 */
	private Fragment tempFragemnt;
	/**
	 * LCApplication实例
	 */
	private LCApplication mTApplication;

	/**
	 * 当MainActivity初始化的时候调用该方法。将ButterKnife和MainActivity绑定，在该方法里做一些视图控件的初始化工作
	 * 
	 * @param savedInstanceState
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 去除title
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 去掉Activity上面的状态栏
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		if (Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		rgBottomTag = (RadioGroup) findViewById(R.id.rg_bottom_tag);
		/**
		 * 初始化Fragment
		 */
		initFragment();
		// 设置RadioGroup的监听
		initListener();
		mTApplication = (LCApplication) getApplication();
		mTApplication.reconnect();
	}

	private void initFragment() {
		fragments = new ArrayList<>();
		fragments.add(new RunHomePageFragment());
		fragments.add(new MoreInformationFragment());
	}

	private void initListener() {
		rgBottomTag
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						switch (checkedId) {
						case R.id.btn_home_page:// 控制界面
							position = 0;
							break;
						case R.id.btn_more:// 设置界面
							position = 1;
							break;
						default:
							position = 1;
							break;

						}
						// 根据位置取不同的Fragment
						Fragment baseFragment = getFragment(position);
						switchFragment(tempFragemnt, baseFragment);
					}
				});
		rgBottomTag.check(R.id.btn_home_page);
	}

	private Fragment getFragment(int position) {
		if (fragments != null && fragments.size() > 0) {
			Fragment baseFragment = fragments.get(position);
			return baseFragment;
		}
		return null;
	}

	private void switchFragment(Fragment fromFragment, Fragment nextFragment) {
		if (tempFragemnt != nextFragment) {
			tempFragemnt = nextFragment;
			if (nextFragment != null) {
				FragmentTransaction transaction = getSupportFragmentManager()
						.beginTransaction();
				// 判断nextFragment是否添加
				if (!nextFragment.isAdded()) {
					// 隐藏当前Fragment
					if (fromFragment != null) {
						transaction.hide(fromFragment);
					}
					// 添加Fragment
					transaction.add(R.id.load_fragment, nextFragment).commit();
				} else {
					// 隐藏当前Fragment
					if (fromFragment != null) {
						transaction.hide(fromFragment);
					}
					transaction.show(nextFragment).commit();
				}
			}
		}
	}

	
	@Override
	protected void onDestroy() {
		mTApplication.disconnect();
		;
		super.onDestroy();
		System.exit(0);
		;
	}

}
