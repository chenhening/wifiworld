package com.anynet.wifiworld.provider;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.anynet.wifiworld.BaseActivity;
import com.anynet.wifiworld.R;
import com.anynet.wifiworld.util.LoginHelper;

public class WifiProviderDetailsActivity extends BaseActivity {

	//IPC
	private Intent mIntent = null;
	private ViewPager viewPager;//页卡内容
	private TextView textView1,textView2,textView3,textView4;
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
		mTitlebar.ivHeaderRight.setBackgroundResource(R.drawable.selector_wifi_plus);
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
		Fragment f1 = (Fragment)new WifiReportOnlineFragment();
		Fragment f2 = (Fragment)new WifiReportCountFragment();
		Fragment f3 = (Fragment)new WifiReportGeoFragment();
		Fragment f4 = (Fragment)new WifiReportTimerFragment();
		fragments.add(f1);
		fragments.add(f2);
		fragments.add(f3);
		fragments.add(f4);
		
		InitTextView();
		InitViewPager();
	}

	private void InitViewPager() {
		viewPager = (ViewPager) findViewById(R.id.vp_wifiinfo);
		viewPager.setAdapter(new MyViewPagerAdapter(getSupportFragmentManager()));
		viewPager.setCurrentItem(0);
		viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	private void InitTextView() {
		textView1 = (TextView) findViewById(R.id.tv_realtime);
		textView2 = (TextView) findViewById(R.id.tv_statistical);
		textView3 = (TextView) findViewById(R.id.tv_analyze);
		textView4 = (TextView) findViewById(R.id.tv_analyze);

		textView1.setOnClickListener(new MyOnClickListener(0));
		textView2.setOnClickListener(new MyOnClickListener(1));
		textView3.setOnClickListener(new MyOnClickListener(2));
		textView4.setOnClickListener(new MyOnClickListener(3));
	}

	private class MyOnClickListener implements OnClickListener{
        private int index=0;
        public MyOnClickListener(int i){
        	index=i;
        }
		public void onClick(View v) {
			viewPager.setCurrentItem(index);			
		}
		
	}
	
	public class MyViewPagerAdapter extends FragmentPagerAdapter {
		
		public MyViewPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
        public Fragment getItem(int arg0) {
	        return fragments.get(arg0);
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
