package com.anynet.wifiworld.map;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.widget.Toast;

import com.anynet.wifiworld.me.WifiProfile;
import com.anynet.wifiworld.me.WifiType;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class TestForWifiData {

	public void QueryData() {
		
	}
	
	public void AddData(final Activity activity, BmobGeoPoint position,
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
    	    	Toast.makeText(activity.getApplicationContext(), 
    	    			"添加数据成功，返回objectId为："+ wifi.getObjectId(), 0);
    	    }

    	    @Override
    	    public void onFailure(int code, String arg0) {
    	        // TODO Auto-generated method stub
    	        // 添加失败
    	    }
    	});
	}
}
