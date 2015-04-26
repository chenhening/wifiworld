package com.anynet.wifiworld.wifi;

import org.apache.cordova.LOG;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import cn.bmob.v3.listener.UpdateListener;

import com.anynet.wifiworld.MainActivity;
import com.anynet.wifiworld.data.DataCallback;
import com.anynet.wifiworld.data.WifiDynamic;
import com.anynet.wifiworld.data.WifiProfile;

public class WifiHandleDB {
	private final static String TAG = WifiHandleDB.class.getSimpleName();
	
	private Context mContext;
	private Handler mHandler;
	private WifiProfile mWifiProfile;
	private static WifiHandleDB wifiHandleDB = null;
	
	public static WifiHandleDB getInstance(Context context) {
		if (wifiHandleDB == null) {
			wifiHandleDB = new WifiHandleDB(context);
		}
		return wifiHandleDB;
	}
	
	private WifiHandleDB(Context context) {
		mContext = context;
		mWifiProfile = new WifiProfile();
	}
	
	public static WifiHandleDB getInstance(Context context, Handler handler) {
		if (wifiHandleDB == null) {
			wifiHandleDB = new WifiHandleDB(context, handler);
		}
		return wifiHandleDB;
	}
	
	private WifiHandleDB(Context context, Handler handler) {
		mContext = context;
		mWifiProfile = new WifiProfile();
		mHandler = handler;
	}
	
	public void updateWifiProfile(WifiInfoScanned infoScanned) {
		mWifiProfile.Ssid = infoScanned.getWifiName();
		mWifiProfile.MacAddr = infoScanned.getWifiMAC();
		mWifiProfile.Alias = "逗比";
		mWifiProfile.Password = infoScanned.getWifiPwd();
		mWifiProfile.Banner = null;
		mWifiProfile.Type = WifiProfile.WifiType.WIFI_SUPPLY_BY_UNKNOWN;
		mWifiProfile.Sponser = "无名氏";
		mWifiProfile.Geometry = infoScanned.getGeometry();
		mWifiProfile.Income = 0.0f;
		mWifiProfile.StoreRemote(mContext, new DataCallback<WifiProfile>() {

			@Override
            public void onSuccess(WifiProfile object) {
				LOG.d(TAG, "添加数据成功，返回objectId为："+ object.getObjectId());
            }

			@Override
            public void onFailed( String msg) {
				LOG.d(TAG, "添加数据失败：" + msg);
            }
		});
	}
	
	public void updateWifiUnregistered(final WifiInfoScanned infoScanned) {
		WifiProfile wifiProfile = new WifiProfile();
		final String macAddress = infoScanned.getWifiMAC();
		wifiProfile.QueryByMacAddress(mContext, macAddress, new DataCallback<WifiProfile>() {
			
			@Override
			public void onSuccess(final WifiProfile object) {
				Log.i(TAG, "Success to query wifi profile from server:" + macAddress);
				final WifiProfile wifi = new WifiProfile();
				wifi.Ssid = infoScanned.getWifiName();
				wifi.MacAddr = infoScanned.getWifiMAC();
				wifi.Alias = null;
				wifi.Password = infoScanned.getWifiPwd();
				wifi.Banner = null;
				wifi.Type = WifiProfile.WifiType.WIFI_SUPPLY_BY_HOME;
				wifi.Sponser = null;
				wifi.Geometry = infoScanned.getGeometry();
				wifi.Income = 0.0f;
				wifi.setObjectId(object.getObjectId());
				wifi.update(mContext, new UpdateListener() {

					@Override
					public void onSuccess() {
						Log.i(TAG, "Update WifiProfile table success");
					}
					
					@Override
					public void onFailure(int arg0, String msg) {
						Log.i(TAG, "Update WifiProfile table failed：" + msg);
					}
				});
			}
			
			@Override
			public void onFailed(String msg) {
				Log.i(TAG, "Failed to query wifi profile from server:" + macAddress);
				final WifiProfile wifi = new WifiProfile(WifiProfile.table_name_wifiunregistered);
				wifi.Ssid = infoScanned.getWifiName();
				wifi.MacAddr = infoScanned.getWifiMAC();
				wifi.Alias = null;
				wifi.Password = infoScanned.getWifiPwd();
				wifi.Banner = null;
				wifi.Type = WifiProfile.WifiType.WIFI_SUPPLY_BY_UNKNOWN;
				wifi.Sponser = null;
				wifi.Geometry = infoScanned.getGeometry();
				wifi.Income = 0.0f;
				wifi.StoreRemote(mContext, new DataCallback<WifiProfile>() {

					@Override
					public void onSuccess(WifiProfile object) {
						Log.i(TAG, "Add Data to WifiUnregister table success");
					}

					@Override
					public void onFailed(String msg) {
						Log.i(TAG, "Add Data to WifiUnregister table failed: " + msg);
						
					}
				});
			}
		});
	}
	
	public void queryWifiProfile(final WifiInfoScanned infoScanned) {
		WifiProfile wifiProfile = new WifiProfile();
		wifiProfile.QueryByMacAddress(mContext, infoScanned.getWifiMAC(), new DataCallback<WifiProfile>() {
			
			@Override
			public void onSuccess(final WifiProfile object) {
				Log.i(TAG, "Success to query wifi profile from server:" + infoScanned.getWifiMAC());
				
			}
			
			@Override
			public void onFailed(String msg) {
				Log.e(TAG, "Failed to query wifi profile from server" + infoScanned.getWifiMAC());
				
			}
		});
	}
	
	public void queryWifiProfile4Display(final WifiInfoScanned infoScanned) {
		WifiProfile wifiProfile = new WifiProfile();
		wifiProfile.QueryByMacAddress(mContext, infoScanned.getWifiMAC(), new DataCallback<WifiProfile>() {
			
			@Override
			public void onSuccess(final WifiProfile object) {
				Log.i(TAG, "Success to query wifi profile from server:" + infoScanned.getWifiMAC());
				Message msg = new Message();
				msg.what = WifiFragment.GET_WIFI_DETAILS;
				msg.obj = object;
				mHandler.sendMessage(msg);
			}
			
			@Override
			public void onFailed(String msg) {
				Log.e(TAG, "Failed to query wifi profile from server" + infoScanned.getWifiMAC());
				Message msgg = new Message();
				msgg.what = WifiFragment.GET_WIFI_DETAILS;
				WifiProfile wifiProfile = new WifiProfile();
				wifiProfile.Ssid = infoScanned.getWifiName();
				msgg.obj = wifiProfile;
				mHandler.sendMessage(msgg);
				//mHandler.sendEmptyMessage(((MainActivity)mContext).GET_WIFI_DETAILS);
			}
		});
	}
	
	public void queryWifiDynamic4Display(final WifiInfoScanned infoScanned) {
		WifiDynamic wifiDynamic = new WifiDynamic();
		wifiDynamic.QueryConnectedTimes(mContext, infoScanned.getWifiMAC(), new DataCallback<Long>() {

			@Override
			public void onSuccess(Long object) {
				Log.i(TAG, "Success to query wifi dynamic from server:" + infoScanned.getWifiMAC());
				Message msg = new Message();
				msg.what = WifiFragment.GET_WIFI_DETAILS;
				Log.i(TAG, "wifi connect count:" + object);
				infoScanned.setConnectedTimes(object);
				Log.i(TAG, "wifi connect count:" + infoScanned.getConnectedTimes());
				msg.obj = infoScanned;
				mHandler.sendMessage(msg);
			}

			@Override
			public void onFailed(String msg) {
				Log.e(TAG, "Failed to query wifi dynamic from server" + infoScanned.getWifiMAC());
				
			}
		});
	}
}
