package cn.com.zonesion.powercontrol.fragment;

import java.util.ArrayList;
import java.util.List;

import cn.com.zonesion.powercontrol.R;
import cn.com.zonesion.powercontrol.view.PagerSlidingTabStrip;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class RunHomePageFragment extends Fragment{
	private ViewPager pager;
    /**
     * IDKeyFragment用来设置和显示ID、KEY和服务器地址的页面
     */
    private HomePageFragment homePageFragment;
    
    /** 
     * PagerSlidingTabStrip的实例 
     */  
    private PagerSlidingTabStrip tabs;  
  
    /** 
     * 获取当前屏幕的密度 
     */  
    private DisplayMetrics dm;  

    private void initFourChildFragment(){
    	dm = getResources().getDisplayMetrics();
		pager.setAdapter(new MyPagerAdapter(getChildFragmentManager()));
		tabs.setViewPager(pager);
		 // 设置Tab是自动填充满屏幕的  
        tabs.setShouldExpand(true);  
        // 设置Tab的分割线是透明的  
        tabs.setDividerColor(Color.TRANSPARENT);  
        // 设置Tab底部线的高度  
        tabs.setUnderlineHeight((int) TypedValue.applyDimension(  
                TypedValue.COMPLEX_UNIT_DIP, 1, dm));  
        // 设置Tab Indicator的高度  
        tabs.setIndicatorHeight((int) TypedValue.applyDimension(  
                TypedValue.COMPLEX_UNIT_DIP, 4, dm));  
        // 设置Tab标题文字的大小  
        tabs.setTextSize((int) TypedValue.applyDimension(  
                TypedValue.COMPLEX_UNIT_SP, 16, dm));  
        // 设置Tab Indicator的颜色  
        tabs.setIndicatorColor(Color.parseColor("#67AE37"));  
        // 设置选中Tab文字的颜色 (这是我自定义的一个方法)  
        tabs.setSelectedTextColor(Color.parseColor("#67AE37"));  
        // 取消点击Tab时的背景色  
        tabs.setTabBackground(0);  
    }
    
    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater,
    		@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    	View view = inflater.inflate(R.layout.run_home_page_item, container, false);
    	pager = (ViewPager) view.findViewById(R.id.run_viewpager);
        tabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
         return view;
    }

    @Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initFourChildFragment();
	}

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
    public class MyPagerAdapter extends FragmentPagerAdapter {  
  	  
        public MyPagerAdapter(FragmentManager fm) {  
            super(fm);  
        }  
  
        private final String[] titles = { "运营首页"};  
  
        @Override  
        public CharSequence getPageTitle(int position) {  
            return titles[position];  
        }  
  
        @Override  
        public int getCount() {  
            return titles.length;  
        }  
  
        @Override  
        public Fragment getItem(int position) {  
            switch (position) {  
            case 0:  
                if (homePageFragment == null) {  
                	homePageFragment = new HomePageFragment();  
                }  
                return homePageFragment;  
          
            default:  
                return null;  
            }  
        }  
    }  
}
