package com.anynet.wifiworld.map;

import org.apache.cordova.LOG;

import android.app.Activity;
import android.widget.Toast;

import com.anynet.wifiworld.me.WifiProfile;
import com.anynet.wifiworld.me.WifiType;

import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.SaveListener;

public class TestForWifiData {

	public static void QueryData() {
		int a = 0;
		a = 8;
	}
	
	public static void AddData(Activity activity, BmobGeoPoint position,
			String alias, String Banner) {
		final WifiProfile wifi = new WifiProfile();
		wifi.Wifiid = "";
		wifi.Alias = alias;
		wifi.Password = "12345678";
		wifi.Banner = Banner;
		wifi.Type = WifiType.WIFI_SUPPLY_BY_PUBLIC;
		wifi.Userid = "buffer";
		wifi.Geometry = position;
		wifi.Income = 10.0f;
		wifi.save(activity.getApplicationContext(), new SaveListener() {

    	    @Override
    	    public void onSuccess() {
    	        // TODO Auto-generated method stub
    	    	LOG.d("test", "添加数据成功，返回objectId为："+ wifi.getObjectId());
    	    }

    	    @Override
    	    public void onFailure(int code, String arg0) {
    	        // TODO Auto-generated method stub
    	        // 添加失败
    	    }
    	});
	}
}
