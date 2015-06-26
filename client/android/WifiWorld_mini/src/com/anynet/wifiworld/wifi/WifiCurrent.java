package com.anynet.wifiworld.wifi;

import android.content.Context;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.util.NetHelper;

public class WifiCurrent {
	private final static String TAG = WifiCurrent.class.getSimpleName();
	
	private static WifiCurrent mWifiCurrent = null;
	private Context mContext;
	private WifiListItem mWifiListItem;
	
	public static WifiCurrent getInstance(Context context) {
		if (mWifiCurrent == null) {
			mWifiCurrent = new WifiCurrent(context);
		}
		return mWifiCurrent;
	}
	
	private WifiCurrent(Context context) {
		mContext = context;
		mWifiListItem = null;
	}
	
	public void setWifiListItem(WifiListItem wifiListItem) {
		mWifiListItem = wifiListItem;
	}
	
	public WifiListItem getWifiListItem() {
		return mWifiListItem;
	}
	
	public boolean isConnected() {
		return NetHelper.isConnected(mContext);
	}
	
	public String getWifiName() {
		return WifiAdmin.convertToNonQuotedString(NetHelper.getCurrentSsid(mContext));
	}
	
	//业务逻辑都放在非UI类里面实现，这里相当于MVP的P层
	public int getDefaultLogoID() {
		//根据wifi信号强度显示不同的信号图
		int signalStrength = mWifiListItem.getWifiStrength();
		if (signalStrength >= 80) {
			return R.drawable.ic_wifi_connected_3;
		} else if (signalStrength >= 60) {
			return R.drawable.ic_wifi_connected_2;
		} else {
			return R.drawable.ic_wifi_connected_1;
		}
	}
}
