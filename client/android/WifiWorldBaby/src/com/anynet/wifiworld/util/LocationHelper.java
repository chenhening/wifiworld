package com.anynet.wifiworld.util;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;

public class LocationHelper implements LocationSource, AMapLocationListener {
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
		mAMapLocationManager = LocationManagerProxy.getInstance(globalContext);
		mAMapLocationManager.requestLocationData(LocationProviderProxy.AMapNetwork, -1, 3, this);
	}

	@Override
    public void activate(OnLocationChangedListener listener) {
		
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
		mAMapLocationManager.requestLocationData(LocationProviderProxy.AMapNetwork, -1, 3, this);
		mAMapLocation = mAMapLocationManager.getLastKnownLocation(LocationProviderProxy.AMapNetwork);
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
