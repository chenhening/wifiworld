package com.anynet.wifiworld.wifi;

import java.util.ArrayList;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

public class WifiBRService {
	private final static String TAG = WifiBRService.class.getName();
	private static Intent mIntent = null;
	private static boolean mScannable = false;
	private static Context mContext;
	private static WifiListHelper mWifiListHelper;
	private static WifiBRService mInstance;
	
	public static WifiBRService getInstance(Context context, WifiListHelper wifiListHelper) {
		if (null == mInstance) {
			mInstance = new WifiBRService(context, wifiListHelper);
		}
		return mInstance;
	}
	
	private WifiBRService(Context context, WifiListHelper wifiListHelper) {
		mContext = context;
		mWifiListHelper = wifiListHelper;
	}
	
	public void schedule(final Context ctx) {
		mIntent = new Intent(ctx, WifiMonitorService.class);
		ctx.startService(mIntent);
	}
	
	public void bindWifiService(final Context ctx, ServiceConnection conn) {
		mIntent = new Intent(ctx, WifiMonitorService.class);
		ctx.bindService(mIntent, conn, Context.BIND_AUTO_CREATE);
	}
	
	public void stopWifiService(final Context ctx) {
		if (mIntent != null) {
			ctx.stopService(mIntent);
		}
		mIntent = null;
	}

	public void setWifiScannable(boolean flag) {
		mScannable = flag;
	}
	
	public static class WifiMonitorService extends Service {
		private IntentFilter mIntentFilter;
		private OnWifiStatusListener onWifiStatusListener;
		private String statusStr;
		
		private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
		        if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
			        	Log.i(TAG, "network state changed action");
			        	Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
			        	if (null != parcelableExtra) {
			        		NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
			        		State state = networkInfo.getState();
			        		boolean isConnected = state==State.CONNECTED;
			        		boolean isDisconnected = state==State.DISCONNECTED;
			        		if(isConnected){
			        			statusStr = "已连接" + WifiAdmin.convertToNonQuotedString(WifiAdmin.getInstance(context).getWifiNameConnection());
			        			if (onWifiStatusListener != null) {
					            	onWifiStatusListener.onNetWorkChanged(true, statusStr);
			        			}
			        			Toast.makeText(context, statusStr, Toast.LENGTH_SHORT).show();
			        		} else if(isDisconnected) {
			        			statusStr = "已断开连接";
			        			if (onWifiStatusListener != null) {
					            	onWifiStatusListener.onNetWorkChanged(false, statusStr);
			        			}
			        			Toast.makeText(context, statusStr, Toast.LENGTH_SHORT).show();
							}
			        		}
		        }
		        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
					Log.i(TAG, "wifi state changed action");
					int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
					switch (wifiState) {
					case WifiManager.WIFI_STATE_ENABLED:
						if (onWifiStatusListener != null) {
							onWifiStatusListener.onWifiStatChanged(true);
						}
						break;
					case WifiManager.WIFI_STATE_DISABLED:
					case WifiManager.WIFI_STATE_DISABLING:
					case WifiManager.WIFI_STATE_ENABLING:
					case WifiManager.WIFI_STATE_UNKNOWN:
						if (onWifiStatusListener != null) {
							onWifiStatusListener.onWifiStatChanged(false);
						}
						break;
					default:
						break;
					}
				}
		        if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action) && mScannable == true) {
					Log.i(TAG, "wifi can resualts avaliable");
					mWifiListHelper.fillWifiList();
					mScannable = false;
				}
			}
			
		};
 
		@Override
		public IBinder onBind(Intent intent) {
			// TODO Auto-generated method stub
			return new WifiStatusBinder();
		}

		@Override
		public void onCreate() {
			super.onCreate();
			Log.i(TAG, "Create broadcast service");
			mIntentFilter = new IntentFilter();
			mIntentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
			mIntentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
			mIntentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
			registerReceiver(mBroadcastReceiver, mIntentFilter);
		}

		@Override
		public void onDestroy() {
			super.onDestroy();
			unregisterReceiver(mBroadcastReceiver);
		}
		
		public class WifiStatusBinder extends Binder {
			public WifiMonitorService getService() {
				return WifiMonitorService.this;
			}
		}
		
		public void setOnWifiStatusListener(OnWifiStatusListener onWifiStatusListener) {
			this.onWifiStatusListener = onWifiStatusListener;
		}
		
	}
	
	public interface OnWifiStatusListener {
		void onNetWorkChanged(boolean isSuccess, String str);
		void onWifiStatChanged(boolean isEnabled);
	}
}
