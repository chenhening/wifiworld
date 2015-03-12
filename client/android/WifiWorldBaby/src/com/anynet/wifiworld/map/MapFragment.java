/*
 * Copyright (C) 2015 AnyNet Corporation.  All rights reserved.
 * @Author: buffer(179770346@qq.com or binfeix.li@intel.com)
 * @Time: 2015/01/22
 * 
 * 	v0.1(2015/01/22,1:20): add map2d feature
 * 	v0.2(2015/01/23,1:40): replace map2d to 3dmap and add location feature
 */
package com.anynet.wifiworld.map;

import java.util.ArrayList;

import cn.bmob.v3.datatype.BmobGeoPoint;

import com.anynet.wifiworld.MainActivity.MainFragment;
import com.anynet.wifiworld.R.drawable;
import com.anynet.wifiworld.R.id;
import com.anynet.wifiworld.R.layout;
import com.anynet.wifiworld.R.string;
import com.anynet.wifiworld.MainActivity;
import com.anynet.wifiworld.R;
import com.anynet.wifiworld.app.BaseFragment;

import android.app.Activity;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
					LocationProviderProxy.AMapNetwork, 60 * 1000, 10, this);
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
				
				TestForWifiData wifi = new TestForWifiData();
				double y = amapLocation.getLongitude();
				double x = amapLocation.getLatitude();
				wifi.AddData(getActivity(), new BmobGeoPoint(x + 0.0002, y + 0.0002),
						"好用又便宜的wifi", "我的电话是多少？");
				wifi.AddData(getActivity(), new BmobGeoPoint(x + 0.0002, y + 0.0002),
						"wifi出租", "我的电话是多少？");
				wifi.AddData(getActivity(), new BmobGeoPoint(x + 0.0002, y + 0.0002),
						"我家的wifi免费", "我的电话是多少？");
				wifi.AddData(getActivity(), new BmobGeoPoint(x + 0.0002, y + 0.0002),
						"想用wifi点我私聊", "我的电话是多少？");
				wifi.AddData(getActivity(), new BmobGeoPoint(x + 0.0002, y + 0.0002),
						"思聪的私人wifi", "我的电话是多少？");
				wifi.AddData(getActivity(), new BmobGeoPoint(x + 0.0002, y + 0.0002),
						"思聪的公共wifi", "我的电话是多少？");

				// add wifi label
				float scale = aMap.getScalePerPixel();
				float r = amapLocation.getAccuracy();
				y = amapLocation.getLongitude();
				x = amapLocation.getLatitude();
				LatLng llwifi1 = new LatLng(x + 0.0002, y + 0.0002);
				aMap.addMarker(
					new MarkerOptions()
						.position(llwifi1)
						.title("好用又便宜的wifi")
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.icon_geo))
						.draggable(true)).showInfoWindow();
				LatLng llwifi2 = new LatLng(x + 0.0003, y + 0.0003);
				aMap.addMarker(
					new MarkerOptions()
						.position(llwifi2)
						.title("wifi出租")
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.icon_geo))
						.draggable(true)).showInfoWindow();
				LatLng llwifi4 = new LatLng(x - 0.0004, y + 0.0004);
				aMap.addMarker(
					new MarkerOptions()
						.position(llwifi4)
						.title("我家的wifi免费")
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.icon_geo))
						.draggable(true)).showInfoWindow();
				LatLng llwifi5 = new LatLng(x - 0.0005, y - 0.0005);
				aMap.addMarker(
					new MarkerOptions()
						.position(llwifi5)
						.title("想用wifi点我私聊")
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.icon_geo))
							.draggable(true)).showInfoWindow();
				LatLng llwifi3 = new LatLng(x - 0.0004, y - 0.0006);
				aMap.addMarker(
					new MarkerOptions()
						.position(llwifi3)
						.title("思聪的私人wifi")
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.icon_geo))
						.draggable(true)).showInfoWindow();
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

	/**
	 *  * 选择矢量地图/卫星地图/夜景地图事件的响应  
	 */
	private void setLayer(String layerName) {
		if (layerName.equals(getString(R.string.normal))) {
			aMap.setMapType(AMap.MAP_TYPE_NORMAL);// 矢量地图模式
		} else if (layerName.equals(getString(R.string.satellite))) {
			aMap.setMapType(AMap.MAP_TYPE_SATELLITE);// 卫星地图模式
		} else if (layerName.equals(getString(R.string.night_mode))) {
			aMap.setMapType(AMap.MAP_TYPE_NIGHT);// 夜景地图模式
		}
	}

	private void setTrafficEnabled(boolean set) {
		aMap.setTrafficEnabled(set);
	}
}
