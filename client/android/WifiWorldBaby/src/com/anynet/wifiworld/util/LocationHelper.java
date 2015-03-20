package com.anynet.wifiworld.util;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;

import com.amap.api.maps.AMap.OnMyLocationChangeListener;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;

public class LocationHelper implements LocationSource, AMapLocationListener {
	private MapView mapView;
	private AMap aMap;
	private LocationManagerProxy mAMapLocationManager;
	private AMapLocation mAMapLocation;
	
	private static LocationHelper mInstance = null;
	private Context globalContext = null;

// ------------------------------------------------------------------------------------------------
	public static LocationHelper getInstance(Context context) {
		if (null == mInstance) {
			mInstance = new LocationHelper(context);
		}
		return mInstance;
	}
	
	public LocationHelper(Context context) {
		globalContext = context;
		mapView = new MapView(context);
		aMap = mapView.getMap();
		aMap.setLocationSource(this);
		aMap.setMyLocationEnabled(true);
		aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_FOLLOW);
	}

	@Override
    public void activate(OnLocationChangedListener listener) {
		if (mAMapLocationManager == null) {
			mAMapLocationManager = 
					LocationManagerProxy.getInstance(globalContext);
			// 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
			// 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），
			// 并且在合适时间调用removeUpdates()方法来取消定位请求
			// 在定位结束后，在合适的生命周期调用destroy()方法
			// 其中如果间隔时间为-1，则定位只定一次
			// 在单次定位情况下，定位无论成功与否，都无需调用removeUpdates()方法移除请求
			mAMapLocationManager.requestLocationData(
				LocationProviderProxy.AMapNetwork, -1, 3, this);
			mAMapLocationManager.setGpsEnable(false);
		}
    }

	@Override
    public void deactivate() {
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.destroy();
		}
		mAMapLocationManager = null;
    }

	@Override
    public void onLocationChanged(Location mLocation) {
	    // TODO Auto-generated method stub
		if(mAMapLocation == null || mAMapLocation.getCity() == null )mAMapLocation = (AMapLocation) mLocation;
    }

	@Override
    public void onProviderDisabled(String arg0) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void onProviderEnabled(String arg0) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void onLocationChanged(AMapLocation amapLocation) {
		mAMapLocation = amapLocation;
    }
	
// ------------------------------------------------------------------------------------------------
	public void refreshLocation() {
		mAMapLocationManager.requestLocationData(
				LocationProviderProxy.AMapNetwork, -1, 3, this);
			mAMapLocationManager.setGpsEnable(false);
	}
	
	//经度
	public double getLongitude() {
		return mAMapLocation.getLongitude();
	}
	
	//纬度
	public double getLatitude() {
		return mAMapLocation.getLatitude();
	}
	
	//省份
	public String getProvince() {
		return mAMapLocation.getProvince();
	}
	
	//城市
	public String getCity() {
		return mAMapLocation.getCity();
	}
	
	//街道
	public String getDistrict() {
		return mAMapLocation.getDistrict();
	}
	
	//地理位置的相关描述
	public String getLocalDescription() {
		return mAMapLocation.getExtras().getString("desc");
	}

}
