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

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.app.BaseActivity;
import com.anynet.wifiworld.data.WifiProfile;
import com.anynet.wifiworld.util.LoginHelper;
import com.desarrollodroide.libraryfragmenttransactionextended.FragmentTransactionExtended;

public class WifiProviderDetailActivity extends BaseActivity {

	//IPC
	private Intent mIntent = null;
	private android.app.Fragment mFirstFragment = null;
    private float start_x = 0;
    private float end_x = 0;
	
	private void bingdingTitleUI() {
		mTitlebar.ivHeaderLeft.setVisibility(View.VISIBLE);
		mTitlebar.llFinish.setVisibility(View.VISIBLE);
		//mTitlebar.llHeaderMy.setVisibility(View.INVISIBLE);
		mTitlebar.tvHeaderRight.setVisibility(View.INVISIBLE);
		mTitlebar.tvTitle.setText(getString(R.string.wifi_provider));
		mTitlebar.ivHeaderLeft.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		mTitlebar.ivMySetting.setVisibility(View.VISIBLE);
		mTitlebar.ivMySetting.setOnClickListener(new OnClickListener() {
			
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
		setContentView(R.layout.wifi_provider_detail);
		super.onCreate(savedInstanceState);
		bingdingTitleUI();
		
		//Add first fragment
        mFirstFragment = new WifiOnlineSlidingFragment();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.fragment_place, mFirstFragment);
        fragmentTransaction.commit();
        
        this.findViewById(R.id.fragment_place).setOnTouchListener(new OnTouchListener() {

        	@Override
            public boolean onTouch(View v, MotionEvent event) {
        		if (event.getAction() == MotionEvent.ACTION_DOWN) {
        			start_x = event.getRawX();
        		}
        		else if (event.getAction() == MotionEvent.ACTION_UP) {
        			end_x = event.getRawX();
        			if (Math.abs(start_x - end_x) > 100) {
        				if (getFragmentManager().getBackStackEntryCount()==0) {
        		            Fragment secondFragment = new WifiReportSlidingFragment();
        		            FragmentManager fm = getFragmentManager();
        		            FragmentTransaction fragmentTransaction = fm.beginTransaction();
        		            FragmentTransactionExtended fragmentTransactionExtended = new FragmentTransactionExtended(
        		            	getApplicationContext(), fragmentTransaction, mFirstFragment, secondFragment, R.id.fragment_place);
        		            fragmentTransactionExtended.addTransition(FragmentTransactionExtended.SLIDE_HORIZONTAL);
        		            fragmentTransactionExtended.commit();
        		        }else{
        		            getFragmentManager().popBackStack();
        		        }
        			}
                }  
        	    return true;
            }
        	
        });
        
			
		/*(if (cordView == null) {
			Config.init(this);
			Whitelist mWhitelist = new Whitelist();
			cordView = (CordovaWebView) findViewById(R.id.cwv_provider_detail_view);
			cordView.init(this, new CordovaWebViewClient((CordovaInterface) this, cordView), new CordovaChromeClient(this, cordView),
		            Config.getPluginEntries(), Config.getWhitelist(), Config.getExternalWhitelist(), Config.getPreferences());
		}
		cordView.loadUrl("file:///android_asset/www/index.html");
		cordView.removeAllViews();*/
		
		//当前在线用户展示图
		/*displayUserOnlineMap(savedInstanceState);
		
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
        chartLayout.addView(mLineChart, layoutParams);*/
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
	
	// ---------------------------------------------------------------------------------------------
	/*private void displayUserOnlineMap(Bundle savedInstanceState) {
		mapView = (MapView) this.findViewById(R.id.user_online_map);
		mapView.onCreate(savedInstanceState);
		aMap = mapView.getMap();
		MyLocationStyle myLocationStyle = new MyLocationStyle();
		myLocationStyle.strokeColor(Color.BLACK);
		myLocationStyle.strokeWidth(5);
		aMap.setMyLocationStyle(myLocationStyle);
		//获取到注册wifi的地理位置
		WifiProfile wifi = (WifiProfile) mIntent.getSerializableExtra(WifiProfile.class.getName());
		LatLng point = new LatLng(wifi.Geometry.getLatitude(), wifi.Geometry.getLongitude());
		CameraUpdate update = CameraUpdateFactory.newLatLngZoom(point, (float) 20);
		aMap.moveCamera(update);
		aMap.addMarker(
			new MarkerOptions().position(point).title(wifi.Alias)
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_geo))
				.draggable(true)).showInfoWindow();
		updateUserOnline();
	}
	
	//查询正在使用wifi的用户
	private void updateUserOnline() {
		
	}*/
}
