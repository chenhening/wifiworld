package com.anynet.wifiworld.me;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.cordova.Config;
import org.apache.cordova.CordovaChromeClient;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewClient;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import cn.bmob.v3.datatype.BmobGeoPoint;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.anynet.wifiworld.R;
import com.anynet.wifiworld.app.BaseActivity;
import com.anynet.wifiworld.data.WifiProfile;

public class WifiProviderDetailActivity extends BaseActivity implements CordovaInterface {

	//IPC
	private Intent mIntent = null;
	
	private WifiProfile mWifiProfile = new WifiProfile();
	private Bitmap mLogo;
	private BmobGeoPoint mBmobGeoPoint;
	private WifiProviderLineChartView mLineChart;
	
	private CordovaWebView cordView = null;
	private final ExecutorService threadPool = Executors.newCachedThreadPool();
	
	private MapView mapView = null;
	private AMap aMap = null;
	
	private void bingdingTitleUI() {
		mTitlebar.ivHeaderLeft.setVisibility(View.VISIBLE);
		mTitlebar.llFinish.setVisibility(View.VISIBLE);
		mTitlebar.llHeaderMy.setVisibility(View.INVISIBLE);
		mTitlebar.tvHeaderRight.setVisibility(View.INVISIBLE);
		mTitlebar.tvTitle.setText(getString(R.string.wifi_provider));
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
		setContentView(R.layout.wifi_provider_detail);
		super.onCreate(savedInstanceState);
		bingdingTitleUI();
					
		Config.init(this);
        
		cordView = (CordovaWebView) findViewById(R.id.cwv_provider_detail_view);
		cordView.init(this, new CordovaWebViewClient((CordovaInterface) this, cordView), new CordovaChromeClient(this, cordView),
                Config.getPluginEntries(), Config.getWhitelist(), Config.getExternalWhitelist(), Config.getPreferences());
        cordView.loadUrl("file:///android_asset/www/index.html");
		
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

	@Override
    public Activity getActivity() {
	    return this;
    }

	@Override
    public ExecutorService getThreadPool() {
	    return threadPool;
    }

	@Override
    public Object onMessage(String arg0, Object arg1) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public void setActivityResultCallback(CordovaPlugin arg0) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void startActivityForResult(CordovaPlugin arg0, Intent arg1, int arg2) {
	    // TODO Auto-generated method stub
	    
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
