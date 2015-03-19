/*
 * Copyright (C) 2015 AnyNet Corporation.  All rights reserved.
 * @Author: buffer(179770346@qq.com or binfeix.li@intel.com)
 * @Time: 2015/01/22
 * 
 * 	v0.1(2015/01/22,1:20): add map2d feature
 * 	v0.2(2015/01/23,1:40): replace map2d to 3dmap and add location feature
 */
package com.anynet.wifiworld.map;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.FindListener;

import com.anynet.wifiworld.MainActivity.MainFragment;
import com.anynet.wifiworld.R;
import com.anynet.wifiworld.data.WifiProfile;
import com.anynet.wifiworld.util.LoginHelper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;

public class MapFragment extends MainFragment implements LocationSource,
		AMapLocationListener, OnMarkerClickListener, OnInfoWindowClickListener,
		InfoWindowAdapter {

	private String BMOB_KEY = "b20905c46c6f0ae1edee547057f04589";
	
	private MapView mapView;
	private AMap aMap;
	private OnLocationChangedListener mListener;
	private LocationManagerProxy mAMapLocationManager;

	// ---------------------------------------------------------------------------------------------
	// for Fragment

	private void bingdingTitleUI() {
		mTitlebar.ivHeaderLeft.setVisibility(View.INVISIBLE);
		mTitlebar.llFinish.setVisibility(View.VISIBLE);
		mTitlebar.llHeaderMy.setVisibility(View.INVISIBLE);
		mTitlebar.tvHeaderRight.setVisibility(View.INVISIBLE);
		// mTitlebar.llFinish.setOnClickListener(this);
		mTitlebar.tvTitle.setText(getString(R.string.nearby));
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mPageRoot = inflater.inflate(R.layout.activity_multy_location, null);
		mapView = (MapView) mPageRoot.findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);
		if (aMap == null) {
			aMap = mapView.getMap();
			setUpMap();
		}
		// 初始化 Bmob SDK
		Bmob.initialize(getActivity(), BMOB_KEY);
		super.onCreateView(inflater, container, savedInstanceState);
		bingdingTitleUI();
		return mPageRoot;
	}

	@Override
	public void onPause() {
		super.onPause();
		mapView.onPause();
		deactivate();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onStart() {
		super.onStart();
		mapView.startLayoutAnimation();
	}

	@Override
	public void onResume() {
		super.onResume();
		mapView.onResume();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

	// ---------------------------------------------------------------------------------------------
	// for LocationSource
	/**
	 * 激活定位
	 */
	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		if (mAMapLocationManager == null) {
			mAMapLocationManager = LocationManagerProxy.getInstance(this
					.getActivity());
			// 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
			// 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），
			// 并且在合适时间调用removeUpdates()方法来取消定位请求
			// 在定位结束后，在合适的生命周期调用destroy()方法
			// 其中如果间隔时间为-1，则定位只定一次
			// 在单次定位情况下，定位无论成功与否，都无需调用removeUpdates()方法移除请求
			mAMapLocationManager.requestLocationData(
					LocationProviderProxy.AMapNetwork, 300 * 1000, 10, this);
		}
	}

	/**
	 * 停止定位
	 */
	@Override
	public void deactivate() {
		mListener = null;
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.destroy();
		}
		mAMapLocationManager = null;
	}

	// ---------------------------------------------------------------------------------------------
	// for AMapLocationListener
	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub

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

	/**
	 * 定位成功后回调函数
	 */
	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		if (mListener != null && amapLocation != null) {
			if (amapLocation != null
					&& amapLocation.getAMapException().getErrorCode() == 0) {
				mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
				CameraUpdate update = CameraUpdateFactory.zoomBy(3);
				aMap.moveCamera(update);
				
				double Longitude = amapLocation.getLongitude();
				double Latitude = amapLocation.getLatitude();
				LoginHelper loginHelper = LoginHelper.getInstance(getApplicationContext());
				loginHelper.setLongitude(Longitude);
				loginHelper.setLatitude(Latitude);

				//query wifi nearby
				BmobQuery<WifiProfile> bmobQuery = new BmobQuery<WifiProfile>();
				bmobQuery.addWhereNear("Geometry", new BmobGeoPoint(Longitude, Latitude));
				bmobQuery.setLimit(10);    //获取最接近用户地点的10条数据
				bmobQuery.findObjects(getActivity().getApplicationContext(), new FindListener<WifiProfile>() {
				    @Override
				    public void onSuccess(List<WifiProfile> object) {
				        // TODO Auto-generated method stub
				    	showToast("查询周围wifi成功：共" + object.size() + "条数据。");
				    	// add wifi label
				    	DisplayNearbyWifi(object);
				    }
				    @Override
				    public void onError(int code, String msg) {
				        // TODO Auto-generated method stub
				    	showToast("您附近还没有可用wifi信号，请更换位置。");
				    }
				});

				aMap.setOnMarkerClickListener(this);
				aMap.setOnInfoWindowClickListener(this);
				aMap.setInfoWindowAdapter(this);
			} else {
				Log.e("AmapErr", "Location ERR:"
						+ amapLocation.getAMapException().getErrorCode());
			}
		}
	}

	// ---------------------------------------------------------------------------------------------
	// for MarkerClickListener
	@Override
	public boolean onMarkerClick(Marker arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	// ---------------------------------------------------------------------------------------------
	// for OnInfoWindowClickListener
	@Override
	public void onInfoWindowClick(Marker arg0) {
		// TODO Auto-generated method stub

	}

	// ---------------------------------------------------------------------------------------------
	// for InfoWindowAdapter
	@Override
	public View getInfoContents(Marker arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View getInfoWindow(Marker arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	// ---------------------------------------------------------------------------------------------
	// for self-define functions
	/**
	 * 设置一些amap的属性
	 */
	private void setUpMap() {
		MyLocationStyle myLocationStyle = new MyLocationStyle();
		// myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.gps));
		myLocationStyle.strokeColor(Color.BLACK);
		myLocationStyle.strokeWidth(5);
		aMap.setMyLocationStyle(myLocationStyle);

		aMap.setLocationSource(this);// 设置定位监听
		aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
		// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
		aMap.setMyLocationEnabled(true);
		// 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
		aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_FOLLOW);
	}

	private void DisplayNearbyWifi(List<WifiProfile> wifilist) {
		for(int i = 0; i < wifilist.size(); ++i) {
			WifiProfile wifi = wifilist.get(i);
			LatLng llwifi1 = 
				new LatLng(wifi.Geometry.getLatitude(), wifi.Geometry.getLongitude());
			aMap.addMarker(
				new MarkerOptions().position(llwifi1).title(wifi.Alias)
					.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_geo))
						.draggable(true)).showInfoWindow();
		}
	}
}
