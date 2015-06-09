package com.anynet.wifiworld.util;

import android.os.Handler;
import android.os.Message;

public abstract class GlobalHandler extends Handler {
	public static int UPDATE_WIFI_LIST = 1;

	@Override
	public void handleMessage(Message msg) {
		int value = msg.what;
		if (value == UPDATE_WIFI_LIST) {
			onWifiListRefreshed();
		}
		super.handleMessage(msg);
	}

	public abstract void onWifiListRefreshed();
}
