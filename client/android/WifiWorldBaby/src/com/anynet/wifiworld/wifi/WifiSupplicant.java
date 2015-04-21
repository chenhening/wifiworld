package com.anynet.wifiworld.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class WifiSupplicant {
	private final static String TAG = WifiSupplicant.class.getSimpleName();

	private Context mContext;
	private SupplicantCallback mCallback = null;
	private static boolean mSupplicantFlag = false;

	private static WifiSupplicant wifiBR = null;

	public static WifiSupplicant getInstance(Context context,
			SupplicantCallback callback) {
		if (wifiBR == null) {
			wifiBR = new WifiSupplicant(context, callback);
		}
		return wifiBR;
	}

	private WifiSupplicant(Context context, SupplicantCallback callback) {
		mCallback = callback;
		mContext = context;
	}
	
	public static void setWifiSupplicant(boolean flag) {
		mSupplicantFlag = flag;
	}

	public BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.i(TAG, "supplicant state changed broadcast:" + mSupplicantFlag);
			String statusStr;
			if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action) && mSupplicantFlag == true) {
				Log.i(TAG, "supplicant state changed action");
				WifiInfo info = WifiAdmin.getInstance(context).getWifiConnecting();
				SupplicantState state = info.getSupplicantState();
				if (state == SupplicantState.ASSOCIATED) {
					statusStr = "正在连接...";
				}
				// 为了兼容4.0以下的设备，不要写成state == SupplicantState.AUTHENTICATING
				else if (state.toString().equals("AUTHENTICATING")) {
					statusStr = "正在验证";
				} else if (state == SupplicantState.ASSOCIATING) {
					statusStr = "正在连接...";
				} else if (state == SupplicantState.COMPLETED) {
					// 只是验证密码正确，并不代表连接成功
					statusStr = "正在获取IP地址";
				} else if (state == SupplicantState.DISCONNECTED) {
					statusStr = "已断开";
				} else if (state == SupplicantState.DORMANT) {
					statusStr = "暂停活动";
				} else if (state == SupplicantState.FOUR_WAY_HANDSHAKE) {
					statusStr = "正在连接...";
				} else if (state == SupplicantState.GROUP_HANDSHAKE) {
					statusStr = "正在连接...";
				} else if (state == SupplicantState.INACTIVE) {
					statusStr = "已断开";
				} else if (state == SupplicantState.INVALID) {
					statusStr = "无效";
				} else if (state == SupplicantState.SCANNING) {
					statusStr = "正在扫描...";
				} else if (state == SupplicantState.UNINITIALIZED) {
					statusStr = "未初始化";
				} else {
					statusStr = "unkown";
				}
				final int errorCode = intent.getIntExtra(
						WifiManager.EXTRA_SUPPLICANT_ERROR, -1);
				if (errorCode == WifiManager.ERROR_AUTHENTICATING) {
					Toast.makeText(context, "密码输入错误", Toast.LENGTH_SHORT)	.show();
				}
				if (mCallback != null) {
					mCallback.onSupplicantChanged(statusStr);
				}
			}
		}
	};

	public interface SupplicantCallback {
		void onSupplicantChanged(String str);
	}
}
