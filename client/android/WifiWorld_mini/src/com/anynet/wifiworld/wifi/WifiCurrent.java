package com.anynet.wifiworld.wifi;

import android.R.string;
import android.content.Context;

import com.anynet.wifiworld.util.NetHelper;

public class WifiCurrent {
	private final static String TAG = WifiCurrent.class.getSimpleName();
	
	private static WifiCurrent mWifiCurrent = null;
	private Context mContext;
	
	public static WifiCurrent getInstance(Context context) {
		if (mWifiCurrent == null) {
			mWifiCurrent = new WifiCurrent(context);
		}
		return mWifiCurrent;
	}
	
	private WifiCurrent(Context context) {
		mContext = context;
	}
	
	public boolean isConnected() {
		return NetHelper.isWifiConnected(mContext);
	}
	
	public boolean isConnecting() {
		return NetHelper.isWifiConnecting(mContext);
	}
	
	public String getWifiName() {
		return WifiAdmin.convertToNonQuotedString(NetHelper.getCurrentSsid(mContext));
	}
}
