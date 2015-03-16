package com.anynet.wifiworld.map;

import org.apache.cordova.LOG;

import android.app.Activity;

import com.anynet.wifiworld.data.DataCallback;
import com.anynet.wifiworld.data.WifiProfile;
import com.anynet.wifiworld.data.WifiType;

import cn.bmob.v3.datatype.BmobGeoPoint;

public class TestForWifiData {

	public static void QueryData() {
	}
	
	public static void AddData(Activity activity, BmobGeoPoint position,
			String alias, String Banner) {
		WifiProfile wifi = new WifiProfile();
		wifi.Ssid = "Tenda";
		wifi.MacAddr = "12:34:56:78";
		wifi.Alias = alias;
		wifi.Password = "12345678";
		wifi.Banner = Banner;
		wifi.Type = WifiType.WIFI_SUPPLY_BY_PUBLIC;
		wifi.Sponser = "buffer";
		wifi.Geometry = position;
		wifi.Income = 10.0f;
		wifi.StoreRemote(activity, new DataCallback<WifiProfile>() {

			@Override
            public void onSuccess(WifiProfile object) {
				LOG.d("test", "添加数据成功，返回objectId为："+ object.getObjectId());
            }

			@Override
            public void onFailed( String msg) {
				LOG.d("test", "添加数据失败：" + msg);
            }
		});
	}
}
