package com.anynet.wifiworld.wifi;

import java.util.Map;

import org.apache.cordova.LOG;

import android.content.Context;
import cn.bmob.v3.datatype.BmobGeoPoint;

import com.anynet.wifiworld.data.DataCallback;
import com.anynet.wifiworld.data.WifiProfile;
import com.anynet.wifiworld.data.WifiType;

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
	
	public void updateOneRow(WifiInfoScanned infoScanned) {
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
}
