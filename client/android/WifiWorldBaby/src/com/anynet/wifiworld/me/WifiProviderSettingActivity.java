package com.anynet.wifiworld.me;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

import com.anynet.wifiworld.MainActivity;
import com.anynet.wifiworld.R;
import com.anynet.wifiworld.app.BaseActivity;
import com.anynet.wifiworld.data.WifiProfile;
import com.anynet.wifiworld.util.LoginHelper;
import com.desarrollodroide.libraryfragmenttransactionextended.FragmentTransactionExtended;

public class WifiProviderSettingActivity extends BaseActivity {

	//IPC
	private Intent mIntent = null;
	
	private void bingdingTitleUI() {
		mTitlebar.ivHeaderLeft.setVisibility(View.VISIBLE);
		mTitlebar.llFinish.setVisibility(View.VISIBLE);
		mTitlebar.tvHeaderRight.setVisibility(View.INVISIBLE);
		mTitlebar.tvTitle.setText(getString(R.string.wifi_provider));
		mTitlebar.ivMySetting.setVisibility(View.GONE);
		mTitlebar.ivHeaderLeft.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mIntent = getIntent();
		setContentView(R.layout.wifi_provider_setting);
		super.onCreate(savedInstanceState);
		bingdingTitleUI();
		
		//取消wifi
		this.findViewById(R.id.slv_change_provider_info).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				LoginHelper.getInstance(getApplicationContext()).mWifiProfile.deleteRemote(getApplicationContext());
				mIntent.setClass(getApplicationContext(), MainActivity.class);
				startActivity(mIntent);
			}
		});
		this.findViewById(R.id.slv_cancle_provider_info).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				LoginHelper.getInstance(getApplicationContext()).mWifiProfile.deleteRemote(getApplicationContext());
				mIntent.setClass(getApplicationContext(), MainActivity.class);
				startActivity(mIntent);
			}
		});
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
