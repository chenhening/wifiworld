package com.anynet.wifiworld.wifi;

import java.net.NetworkInterface;

import org.apache.cordova.LOG;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.util.Log;

import cn.bmob.v3.listener.UpdateListener;

import com.anynet.wifiworld.data.DataCallback;
import com.anynet.wifiworld.data.UserProfile;
import com.anynet.wifiworld.data.WifiDynamic;
import com.anynet.wifiworld.data.WifiProfile;
import com.anynet.wifiworld.data.WifiType;
import com.anynet.wifiworld.util.LoginHelper;

public class WifiHandleDB {
	private final static String TAG = WifiHandleDB.class.getSimpleName();
	
	private Context mContext;
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
	
	public void updateWifiProfile(WifiInfoScanned infoScanned) {
		mWifiProfile.Ssid = infoScanned.getWifiName();
		mWifiProfile.MacAddr = infoScanned.getWifiMAC();
		mWifiProfile.Alias = "逗比";
		mWifiProfile.Password = infoScanned.getWifiPwd();
		mWifiProfile.Banner = null;
		mWifiProfile.Type = WifiType.WIFI_SUPPLY_BY_UNKNOWN;
		mWifiProfile.Sponser = "无名氏";
		mWifiProfile.Geometry = infoScanned.getGeometry();
		mWifiProfile.Income = 0.0f;
		mWifiProfile.ConnectedDuration = 998;
		mWifiProfile.ConnectedTimes = 123;
		mWifiProfile.Ranking = 278;
		mWifiProfile.Rating = 4.7f;
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
				wifi.Type = WifiType.WIFI_SUPPLY_BY_HOME;
				wifi.encryptType = infoScanned.getEncryptType();
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
				wifi.Type = WifiType.WIFI_SUPPLY_BY_UNKNOWN;
				wifi.encryptType = infoScanned.getEncryptType();
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
				infoScanned.setConnectedDuration(object.ConnectedDuration);
				infoScanned.setConnectedTimes(object.ConnectedTimes);
				infoScanned.setRanking(object.Ranking);
				infoScanned.setRating(object.Rating);
				
			}
			
			@Override
			public void onFailed(String msg) {
				Log.e(TAG, "Failed to query wifi profile from server" + infoScanned.getWifiMAC());
				
			}
		});
	}
	
	public void updateWifiDynamic(WifiInfo wifiInfo) {
		WifiDynamic wifiDynamic = new WifiDynamic();
		wifiDynamic.MacAddr = wifiInfo.getBSSID();
		wifiDynamic.Geometry = WifiAdmin.getWifiGeometry(mContext, wifiInfo.getRssi());
		wifiDynamic.MarkLoginTime();
		UserProfile user;
		if ((user = LoginHelper.getInstance(mContext).getCurLoginUserInfo()) != null) {
			wifiDynamic.Userid = user.PhoneNumber;
		} else {
			wifiDynamic.Userid = "user_" + DeviceUID.getLocalMacAddressFromIp(mContext);
		}
		wifiDynamic.StoreRemote(mContext, new DataCallback<WifiDynamic>() {

			@Override
			public void onSuccess(WifiDynamic object) {
				Log.i(TAG, "Success to store wifi dynamic info to server");
				
			}

			@Override
			public void onFailed(String msg) {
				Log.i(TAG, "Failed to store wifi dynamic info to server:" + msg);
				
			}
		});
	}
}
