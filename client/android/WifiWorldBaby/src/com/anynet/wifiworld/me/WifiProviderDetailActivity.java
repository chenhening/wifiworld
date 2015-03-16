package com.anynet.wifiworld.me;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

import cn.bmob.v3.datatype.BmobGeoPoint;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.app.BaseActivity;
import com.anynet.wifiworld.data.WifiProfile;

public class WifiProviderDetailActivity extends BaseActivity {

	private WifiProfile mWifiProfile = new WifiProfile();
	private Bitmap mLogo;
	private BmobGeoPoint mBmobGeoPoint;
	private WifiProviderLineChartView mLineChart;
	
	private void bingdingTitleUI() {
		mTitlebar.ivHeaderLeft.setVisibility(View.VISIBLE);
		mTitlebar.llFinish.setVisibility(View.VISIBLE);
		mTitlebar.llHeaderMy.setVisibility(View.INVISIBLE);
		mTitlebar.tvHeaderRight.setVisibility(View.INVISIBLE);
		mTitlebar.tvTitle.setText(getString(R.string.wifi_provider));
		mTitlebar.ivHeaderLeft.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		String mac = getIntent().getStringExtra("mac");
		setContentView(R.layout.wifi_provider_detail);
		super.onCreate(savedInstanceState);
		bingdingTitleUI();
		
		RelativeLayout chartLayout = (RelativeLayout)
			this.findViewById(R.id.rl_wifi_provider_line_chart);
		//图表显示范围在占屏幕大小的90%的区域内	   
		int scrWidth = chartLayout.getLayoutParams().width; 	
		int scrHeight = chartLayout.getLayoutParams().height; 			   		
		RelativeLayout.LayoutParams layoutParams = 
			new RelativeLayout.LayoutParams(scrWidth, scrHeight);	
		//居中显示
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		mLineChart = new WifiProviderLineChartView(this);
        chartLayout.addView(mLineChart, layoutParams);
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
