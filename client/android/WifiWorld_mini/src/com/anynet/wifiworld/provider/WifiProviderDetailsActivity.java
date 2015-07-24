package com.anynet.wifiworld.provider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.anynet.wifiworld.BaseActivity;
import com.anynet.wifiworld.R;
import com.anynet.wifiworld.data.WifiProfile;
import com.anynet.wifiworld.util.LoginHelper;

import f.in;

public class WifiProviderDetailsActivity extends BaseActivity {

	//IPC
	private Intent mIntent = null;
	private ViewPager viewPager;//页卡内容
	private TextView tvOnline, tvStatistic, tvAnalyzePosition, tvAnalyzeTime;
	private List<Fragment> fragments = new ArrayList<Fragment>();
	
	private void bingdingTitleUI() {
		mTitlebar.tvTitle.setText("我提供的Wi-Fi");
		
		mTitlebar.ivHeaderLeft.setVisibility(View.VISIBLE);
		mTitlebar.ivHeaderLeft.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		mTitlebar.ivHeaderRight.setVisibility(View.VISIBLE);
		mTitlebar.ivHeaderRight.setImageResource(R.drawable.selector_wifi_plus);
		mTitlebar.ivHeaderRight.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mIntent.setClass(getApplicationContext(), WifiProviderSettingActivity.class);
				startActivity(mIntent);
			}
		});
	}
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mIntent = getIntent();
		setContentView(R.layout.activity_provider_details);
		super.onCreate(savedInstanceState);
		bingdingTitleUI();
		
		//Add fragment
		Fragment f1 = (Fragment)new WifiProviderOnline();
		Fragment f2 = (Fragment)new WifiProviderOnline();
		Fragment f3 = (Fragment)new WifiProviderOnline();
		Fragment f4 = (Fragment)new WifiProviderOnline();
		fragments.add(f1);
		fragments.add(f2);
		fragments.add(f3);
		fragments.add(f4);
		
		InitTextView();
		InitViewPager();
		
		initWifiInfoProvided();
	}

	private void InitViewPager() {
		viewPager = (ViewPager) findViewById(R.id.vp_wifiinfo);
		viewPager.setAdapter(new MyViewPagerAdapter(getSupportFragmentManager()));
		viewPager.setCurrentItem(0);
		setTitleTextColor(0);
		viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	@SuppressLint("ResourceAsColor") private void InitTextView() {
		tvOnline = (TextView) findViewById(R.id.tv_online);
		tvStatistic = (TextView) findViewById(R.id.tv_statistic);
		tvAnalyzePosition = (TextView) findViewById(R.id.tv_analyze_position);
		tvAnalyzeTime = (TextView) findViewById(R.id.tv_analyze_time);

		tvOnline.setOnClickListener(new MyOnClickListener(0));
		tvStatistic.setOnClickListener(new MyOnClickListener(1));
		tvAnalyzePosition.setOnClickListener(new MyOnClickListener(2));
		tvAnalyzeTime.setOnClickListener(new MyOnClickListener(3));
	}
	
	private void initWifiInfoProvided() {
		WifiProfile wifiProfile = LoginHelper.getInstance(getApplicationContext()).getWifiProfile();
		if (wifiProfile != null) {
			TextView wifi_name = (TextView)findViewById(R.id.tv_detail_wifi_name);
			ImageView wifi_logo = (ImageView)findViewById(R.id.iv_detail_wifi_logo);
			TextView wifi_master = (TextView)findViewById(R.id.tv_detail_wifi_master);
			TextView wifi_banner = (TextView)findViewById(R.id.tv_detail_wifi_banner);
			
			if (wifiProfile.Alias != null) {
				wifi_name.setText(wifiProfile.Alias);
			}
			if (wifiProfile.getLogo() != null) {
				wifi_logo.setImageBitmap(wifiProfile.getLogo());
			}
			if (wifiProfile.Sponser != null) {
				wifi_master.setText(wifiProfile.Sponser);
			}
			if (wifiProfile.Banner != null) {
				wifi_banner.setText(wifiProfile.Banner);
			}
		}
	}
	
	private class MyOnClickListener implements OnClickListener{
        private int index=0;
        
        public MyOnClickListener(int i){
        	index=i;
        }
        
		@Override
		public void onClick(View v) {
			viewPager.setCurrentItem(index);
			setTitleTextColor(index);
		}
	}
	
	private void setTitleTextColor(int idx) {
		Resources resource = (Resources) getBaseContext().getResources();
		ColorStateList cslSelected = (ColorStateList) resource.getColorStateList(R.color.app_color_style);
		ColorStateList cslUnselected = (ColorStateList) resource.getColorStateList(R.color.font_color_gray_2_dark);
		switch (idx) {
		case 0:
			tvOnline.setTextColor(cslSelected);
			tvStatistic.setTextColor(cslUnselected);
			tvAnalyzePosition.setTextColor(cslUnselected);
			tvAnalyzeTime.setTextColor(cslUnselected);
			break;
		case 1:
			tvOnline.setTextColor(cslUnselected);
			tvStatistic.setTextColor(cslSelected);
			tvAnalyzePosition.setTextColor(cslUnselected);
			tvAnalyzeTime.setTextColor(cslUnselected);
			break;
		case 2:
			tvOnline.setTextColor(cslUnselected);
			tvStatistic.setTextColor(cslUnselected);
			tvAnalyzePosition.setTextColor(cslSelected);
			tvAnalyzeTime.setTextColor(cslUnselected);
			break;
		case 3:
			tvOnline.setTextColor(cslUnselected);
			tvStatistic.setTextColor(cslUnselected);
			tvAnalyzePosition.setTextColor(cslUnselected);
			tvAnalyzeTime.setTextColor(cslSelected);
			break;

		default:
			break;
		}
	}
	
	public class MyViewPagerAdapter extends FragmentPagerAdapter {
		
		public MyViewPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
        public Fragment getItem(int position) {
	        return fragments.get(position);
        }

		@Override
        public int getCount() {
	        return fragments.size();
        }
	}

    public class MyOnPageChangeListener implements OnPageChangeListener{

		public void onPageScrollStateChanged(int arg0) {		
		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {	
		}

		public void onPageSelected(int arg0) {

		}
    	
    }

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}
}
