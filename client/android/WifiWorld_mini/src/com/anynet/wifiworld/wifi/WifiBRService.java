/*
 * Copyright 2015 Anynet Corporation All Rights Reserved.
 *
 * The source code contained or described herein and all documents related to
 * the source code ("Material") are owned by Anynet Corporation or its suppliers
 * or licensors. Title to the Material remains with Anynet Corporation or its
 * suppliers and licensors. The Material contains trade secrets and proprietary
 * and confidential information of Anynet or its suppliers and licensors. The
 * Material is protected by worldwide copyright and trade secret laws and
 * treaty provisions.
 * No part of the Material may be used, copied, reproduced, modified, published
 * , uploaded, posted, transmitted, distributed, or disclosed in any way
 * without Anynet's prior express written permission.
 *
 * No license under any patent, copyright, trade secret or other intellectual
 * property right is granted to or conferred upon you by disclosure or delivery
 * of the Materials, either expressly, by implication, inducement, estoppel or
 * otherwise. Any license under such intellectual property rights must be
 * express and approved by Anynet in writing.
 *
 * @brief ANLog is the custom log for wifiworld project.
 * @date 2015-06-04
 * @author Jason.Chen
 *
 */

package com.anynet.wifiworld.wifi;

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
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

public class WifiBRService {
	private final static String TAG = WifiBRService.class.getName();
	private static Intent mIntent = null;
	private static OnWifiStatusListener mWifiStatusListener;
	
	private static boolean mScannable = false;
	private static boolean mSupplicantState = false;
	
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
	
	public static void setOnWifiStatusListener(OnWifiStatusListener onWifiStatusListener) {
		WifiBRService.mWifiStatusListener = onWifiStatusListener;
	}
	
	public static void openWifiSupplicant() {
		mSupplicantState = true;
	}
	
	public static boolean getWifiSupplicantState() {
		return mSupplicantState;
	}
	
	public static void setWifiScannable(boolean flag) {
		mScannable = flag;
	}
	
	public static class WifiMonitorService extends Service {
		private IntentFilter mIntentFilter;
		private String statusStr;
		
		private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
			    if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
			        	Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
			        	if (null != parcelableExtra) {
			        		NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
			        		State state = networkInfo.getState();
			        		boolean isConnected = state==State.CONNECTED;
			        		boolean isDisconnected = state==State.DISCONNECTED;
			        		boolean isConnecting = state==State.CONNECTING;
			        		boolean isDisconnecting = state==State.DISCONNECTING;
			        		if(isConnected) {
			        			statusStr = "已连接";
			        			if (mWifiStatusListener != null) {
			        				mWifiStatusListener.onWifiConnected(statusStr);
			        				mSupplicantState = false;
			        			}
			        		} else if(isDisconnected) {
			        			statusStr = "断开连接";
			        			if (mWifiStatusListener != null) {
			        				mWifiStatusListener.onWifiDisconnected(statusStr);
			        			}
			        		} else if (isConnecting) {
			        			statusStr = "连接中";
			        			if (mWifiStatusListener != null) {
			        				mWifiStatusListener.onWifiConnecting(statusStr);
			        				mSupplicantState = true;
			        			}
						} else if (isDisconnecting) {
							statusStr = "断开中";
							if (mWifiStatusListener != null) {
			        				mWifiStatusListener.onWifiDisconnecting(statusStr);
			        			}
						}
			        		Log.d(TAG, "BR network state changed: " + state);
			        	}
		        } else if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action) && mSupplicantState) {
		        		boolean isDisconnected = false;
		            WifiInfo info = WifiAdmin.getInstance(context).getWifiInfo();
		            SupplicantState state = info.getSupplicantState();
		            if (state == SupplicantState.ASSOCIATED) {
		                statusStr = "连接中...";
		            }
		            //为了兼容4.0以下的设备，不要写成state == SupplicantState.AUTHENTICATING
		            else if(state.toString().equals("AUTHENTICATING")) {
		                statusStr = "验证中";
		            } else if (state == SupplicantState.ASSOCIATING) {
		                statusStr = "连接中";
		            } else if (state == SupplicantState.COMPLETED) {
		                //只是验证密码正确，并不代表连接成功
		                statusStr = "获取IP";
		            } else if (state == SupplicantState.DISCONNECTED || state == SupplicantState.INACTIVE) {
		                statusStr = "已断开";
		                mSupplicantState = false;
		                isDisconnected = true;
		            } else if (state == SupplicantState.DORMANT) {
		                statusStr = "暂停中";
		            } else if (state == SupplicantState.FOUR_WAY_HANDSHAKE) {
		                statusStr = "四次握手";
		            } else if (state == SupplicantState.GROUP_HANDSHAKE) {
		                statusStr = "组握手";
		            } else if (state == SupplicantState.INVALID) {
		                statusStr = "无效";
		            } else if (state == SupplicantState.SCANNING) {
		                statusStr = "正在扫描";
		            } else if (state == SupplicantState.UNINITIALIZED) {
		                statusStr = "未初始化";
		            } else {
		                statusStr = "莫名其妙";
		            }
		            Log.d(TAG, "BR Supplicant state changed: " + state);

		            final int errorCode = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, -1);
		            if (errorCode == WifiManager.ERROR_AUTHENTICATING) {
			            	Toast.makeText(context, "密码输入错误", Toast.LENGTH_SHORT).show();
			            	if (mWifiStatusListener != null) {
								mWifiStatusListener.onWrongPassword();
								mSupplicantState = false;
			            	}
		            } else if (mWifiStatusListener != null) {
		            		mWifiStatusListener.onSupplicantChanged(statusStr, isDisconnected);
		            }
		        } else if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action) && mScannable) {
		        		Log.d(TAG, "BR scannable state avaliable");
					if (mWifiStatusListener != null) {
						mWifiStatusListener.onScannableAvaliable();
						mScannable = false;
					}
				} else if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
					int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
					switch (wifiState) {
					case WifiManager.WIFI_STATE_ENABLED:
						if (mWifiStatusListener != null) {
							mWifiStatusListener.onWifiStatChanged(true);
						}
						break;
					case WifiManager.WIFI_STATE_DISABLED:
					case WifiManager.WIFI_STATE_DISABLING:
					case WifiManager.WIFI_STATE_ENABLING:
					case WifiManager.WIFI_STATE_UNKNOWN:
						if (mWifiStatusListener != null) {
							mWifiStatusListener.onWifiStatChanged(false);
						}
						break;
					default:
						break;
					}
					Log.d(TAG, "BR wifi state changed: " + wifiState);
				}
			}
			
		};
 
		@Override
		public IBinder onBind(Intent intent) {
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
	}
	
	public interface OnWifiStatusListener {
		void onWifiConnected(String str);
		void onWifiDisconnected(String str);
		void onWifiConnecting(String str);
		void onWifiDisconnecting(String str);
		void onWifiStatChanged(boolean isEnabled);
		void onScannableAvaliable();
		void onSupplicantChanged(String statusStr, boolean isDisconnected);
		//void onSupplicantDisconnected(String statusStr);
		void onWrongPassword();
	}
}
