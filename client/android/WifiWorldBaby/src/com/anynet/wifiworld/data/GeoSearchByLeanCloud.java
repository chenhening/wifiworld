package com.anynet.wifiworld.data;

import java.util.ArrayList;
import java.util.List;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.LogUtil.log;

//因为bmob地理位置查询的不稳定性，采用结合leancloud的方式查询地理位置，然后再根据情况做数据迁移到服务更好的baas
public class GeoSearchByLeanCloud {
	private String TAG = "GeoSearchByLeanCloud";
	private final static String key_geo = "Geometry";
	private final static String key_key = "key";
	private String mTablename = null;
	
	public String mKey;
	public AVGeoPoint mGeometry;
	
	public GeoSearchByLeanCloud(String tablename) {
		mTablename = tablename;
	}
	
	public void setGeometry(double Longitude, double Latitude) {
		mGeometry = new AVGeoPoint(Longitude, Latitude);
	}
	
	public double getLongitude() {
		if (mGeometry == null) {
			return 0.0;
		}
		
		return mGeometry.getLongitude();
	}
	
	public double Latitude() {
		if (mGeometry == null) {
			return 0.0;
		}
		
		return mGeometry.getLatitude();
	}
	
	public void setKey(String key) {
		mKey = key;
	}
	
	//存储
	public boolean StoreRemote() {
		AVObject mTable = new AVObject(mTablename);
		mTable.put(key_geo, mGeometry);
		mTable.put(key_key, mKey);
		try {
			mTable.save();
			log.d(TAG, "保存到leancloud成功");
			
			return true;
		} catch (AVException e) {
		    log.d(TAG, "保存到leancloud失败： " + e.getMessage());
		    
		    return false;
		}
	}
	
	//查询
	public List<String> QueryInRadians(double radians, int limit) {
		AVQuery<AVObject> query = new AVQuery<AVObject>(mTablename);
		query.whereWithinRadians(key_geo, mGeometry, radians);
		query.setLimit(limit);
		try {
			List<AVObject> result = query.find();
			List<String> keys = new ArrayList<String>();
			for(AVObject i : result) {
				keys.add(i.getString(key_key));
			}
			
			return keys;
		} catch (AVException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return null;
		}
	}
}
