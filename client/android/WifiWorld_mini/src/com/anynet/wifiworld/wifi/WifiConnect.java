package com.anynet.wifiworld.wifi;

import com.anynet.wifiworld.MainActivity;
import com.anynet.wifiworld.R;

import android.content.Context;
import android.widget.TextView;

public class WifiConnect {
	private final static String TAG = WifiConnect.class.getSimpleName();
	
	private MainActivity mContext;
	private WifiAdmin mwiWifiAdmin;
	private WifiCurrent mWifiCurrent;
	
	private TextView mWifiName;
	
	public WifiConnect(Context context) {
		mContext = (MainActivity)context;
		getViewHolder();
		mWifiCurrent = WifiCurrent.getInstance(context);
	}
	
	public void setWifiConnectedContent() {
		if (mWifiCurrent.isConnected()) {
			mWifiName.setText(mWifiCurrent.getWifiName());
		} else if (mWifiCurrent.isConnecting()) {
			//active WiFi status supervise
		} else {
			mWifiName.setText("未连接WiFi");
		}
	}
	
	public void setWifiListContent() {
		
	}
	
	private void getViewHolder() {
		mWifiName = (TextView)mContext.findViewById(R.id.tv_wifi_connected_name);
	}
}
