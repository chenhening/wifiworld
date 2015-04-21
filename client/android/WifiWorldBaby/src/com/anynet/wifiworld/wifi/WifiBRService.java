package com.anynet.wifiworld.wifi;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

public class WifiBRService {
	private final static String TAG = WifiBRService.class.getName();
	private static Intent mIntent = null;
	private static boolean mScannable = false;
	private static boolean mSupplicant = false;
	
	public static void schedule(final Context ctx) {
		mIntent = new Intent(ctx, WifiMonitorService.class);
		ctx.startService(mIntent);
	}
	
	public static void bindWifiService(final Context ctx, ServiceConnection conn) {
		mIntent = new Intent(ctx, WifiMonitorService.class);
		ctx.bindService(mIntent, conn, Context.BIND_AUTO_CREATE);
	}
	
	public static void stopWifiService(final Context ctx) {
		if (mIntent != null) {
			ctx.stopService(mIntent);
		}
		mIntent = null;
	}

	public static void setWifiScannable(boolean flag) {
		mScannable = flag;
	}
	
	public static void setWifiSupplicant(boolean flag) {
		mSupplicant = flag;
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
			        			//Toast.makeText(context, statusStr, Toast.LENGTH_SHORT).show();
			        		} else if(isDisconnected) {
			        			statusStr = "已断开连接";
			        			if (onWifiStatusListener != null) {
					            	onWifiStatusListener.onNetWorkChanged(false, statusStr);
			        			}
			        			//Toast.makeText(context, statusStr, Toast.LENGTH_SHORT).show();
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
					if (onWifiStatusListener != null) {
						onWifiStatusListener.onScannableChanged();
					}
					mScannable = false;
				}
				if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action) && mSupplicant == true) {
		        		Log.i(TAG, "supplicant state changed action");
		            WifiInfo info = WifiAdmin.getInstance(context).getWifiConnecting();
		            SupplicantState state = info.getSupplicantState();
		            if (state == SupplicantState.ASSOCIATED){
		                statusStr = "正在连接...";
		            }
		            //为了兼容4.0以下的设备，不要写成state == SupplicantState.AUTHENTICATING
		            else if(state.toString().equals("AUTHENTICATING")){
		                statusStr = "正在验证";
		            }
		            else if (state == SupplicantState.ASSOCIATING){
		                statusStr = "正在连接...";
		            } else if (state == SupplicantState.COMPLETED){
		                //只是验证密码正确，并不代表连接成功
		                statusStr = "正在获取IP地址";
		            } else if (state == SupplicantState.DISCONNECTED){
		                statusStr = "已断开";
		                if (onWifiStatusListener != null) {
		            			onWifiStatusListener.onSupplicantDisconnected(statusStr);
		            			mSupplicant = false;
		                }
		            } else if (state == SupplicantState.DORMANT){
		                statusStr = "暂停活动";
		            } else if (state == SupplicantState.FOUR_WAY_HANDSHAKE){
		                statusStr = "正在连接...";
		            } else if (state == SupplicantState.GROUP_HANDSHAKE){
		                statusStr = "正在连接...";
		            } else if (state == SupplicantState.INACTIVE){
		                statusStr = "已断开";
		            } else if (state == SupplicantState.INVALID){
		                statusStr = "无效";
		            } else if (state == SupplicantState.SCANNING){
		                statusStr = "正在扫描...";
		            } else if (state == SupplicantState.UNINITIALIZED){
		                statusStr = "未初始化";
		            }else{
		                statusStr = "unkown";
		            }
		            final int errorCode = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, -1);
		            if (errorCode == WifiManager.ERROR_AUTHENTICATING) {
		            		Toast.makeText(context, "密码输入错误", Toast.LENGTH_SHORT).show();
		            }
		            if (onWifiStatusListener != null) {
		            		onWifiStatusListener.onSupplicantChanged(statusStr);
		            }
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
			mIntentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
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
		void onNetWorkChanged(boolean isConnected, String str);
		void onWifiStatChanged(boolean isEnabled);
		void onScannableChanged();
		void onSupplicantChanged(String statusStr);
		void onSupplicantDisconnected(String statusStr);
	}
}
