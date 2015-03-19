package com.anynet.wifiworld.wifi;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.cordova.LOG;

import A.which;
import android.content.Context;
import cn.bmob.v3.datatype.BmobGeoPoint;

import com.anynet.wifiworld.data.DataCallback;
import com.anynet.wifiworld.data.WifiProfile;
import com.anynet.wifiworld.data.WifiType;

public class WifiHandleDB implements Runnable {
	private final static String TAG = WifiHandleDB.class.getSimpleName();
	
	private Context mContext;
	private Map<String, WifiInfoScanned> mWifiListScanned;
	
	public WifiHandleDB(Context context, Map<String, WifiInfoScanned> wifiListScanned) {
		mContext = context;
		mWifiListScanned = wifiListScanned;
	}
	
	@Override
	public void run() {
		WifiProfile wifiProfile = new WifiProfile();
		Iterator<String> iterator = mWifiListScanned.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			WifiInfoScanned wifiInfoScanned = mWifiListScanned.get(key);
			wifiProfile.QueryByMacAddress(mContext, wifiInfoScanned.getWifiMAC(),
					new DataCallback<WifiProfile>() {

						@Override
						public void onSuccess(WifiProfile object) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void onFailed(String msg) {
							// TODO Auto-generated method stub
							
						}
					});
		}
		//check whether exist in WifiProfile table
		
		

	}
	
	private void updateOneRow(WifiProfile wifiProfile, WifiInfoScanned infoScanned) {
		wifiProfile.Ssid = infoScanned.getWifiName();
		wifiProfile.MacAddr = infoScanned.getWifiMAC();
		wifiProfile.Alias = null;
		wifiProfile.Password = infoScanned.getWifiPwd();
		wifiProfile.Banner = null;
		wifiProfile.Type = WifiType.WIFI_SUPPLY_BY_UNKNOWN;
		wifiProfile.Sponser = null;
		wifiProfile.Geometry = new BmobGeoPoint(1.0, 1.0);
		wifiProfile.Income = 0.0f;
		wifiProfile.StoreRemote(mContext, new DataCallback<WifiProfile>() {

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
