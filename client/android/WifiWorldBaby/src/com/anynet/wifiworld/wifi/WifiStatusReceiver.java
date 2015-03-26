package com.anynet.wifiworld.wifi;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

public class WifiStatusReceiver {
	private final static String TAG = WifiStatusReceiver.class.getName();
	
	public static void schedule(final Context ctx) {
		ctx.startService(new Intent(ctx, WifiMonitorService.class));
	}

	public static class WifiMonitorService extends Service {
		private IntentFilter mIntentFilter;
		
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
		               if(isConnected){
		                     Toast.makeText(context, "Wifi已经连接", Toast.LENGTH_LONG).show();
		               }
		           }    
		        } 
		        if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)) {
		        	Log.i(TAG, "supplicant state changed action");
		            WifiInfo info = WifiAdmin.getInstance(context).getWifiConnection();
		            SupplicantState state = info.getSupplicantState();
		            String str = "";
		            if (state == SupplicantState.ASSOCIATED){
		                str = "正在连接...";
		            } 
		            //为了兼容4.0以下的设备，不要写成state == SupplicantState.AUTHENTICATING
		            else if(state.toString().equals("AUTHENTICATING")){
		                str = "正在验证";
		            }
		            else if (state == SupplicantState.ASSOCIATING){
		                str = "正在连接...";
		            } else if (state == SupplicantState.COMPLETED){
		                //只是验证密码正确，并不代表连接成功
		                str = "正在获取ip地址"; 
		            } else if (state == SupplicantState.DISCONNECTED){
		                str = "已断开"; 
		            } else if (state == SupplicantState.DORMANT){
		                str = "暂停活动";
		            } else if (state == SupplicantState.FOUR_WAY_HANDSHAKE){
		                str = "正在连接..."; 
		            } else if (state == SupplicantState.GROUP_HANDSHAKE){
		                str = "正在连接...";
		            } else if (state == SupplicantState.INACTIVE){
		                str = "已断开";
		            } else if (state == SupplicantState.INVALID){
		                str = "无效";
		            } else if (state == SupplicantState.SCANNING){
		                str = "正在扫描...";
		            } else if (state == SupplicantState.UNINITIALIZED){
		                str = "未初始化";
		            }else{
		                str = "unkown";
		            }
		            final int errorCode = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, -1);
		            if (errorCode == WifiManager.ERROR_AUTHENTICATING) {
		                //密码错误
		            }
		            Toast.makeText(context, str, Toast.LENGTH_LONG).show();
		        }
		        
			}
			
		};
 
		@Override
		public IBinder onBind(Intent intent) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void onCreate() {
			super.onCreate();
			Log.i(TAG, "Create broadcast service");
			mIntentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
			mIntentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
			registerReceiver(mBroadcastReceiver, mIntentFilter);
		}

		@Override
		public void onDestroy() {
			super.onDestroy();
			unregisterReceiver(mBroadcastReceiver);
		}
		
	}
	
}
