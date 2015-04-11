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
import java.util.List;

import cn.bmob.v3.datatype.BmobGeoPoint;

import com.anynet.wifiworld.MainActivity.MainFragment;
import com.anynet.wifiworld.R;
import com.anynet.wifiworld.data.MultiDataCallback;
import com.anynet.wifiworld.data.WifiProfile;
import com.anynet.wifiworld.map.SlidingUpPanelLayout.PanelSlideListener;
import com.anynet.wifiworld.util.LoginHelper;
import com.anynet.wifiworld.wifi.WifiHandleDB;
import com.anynet.wifiworld.wifi.WifiInfoScanned;
import com.anynet.wifiworld.wifi.WifiListHelper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMapClickListener;
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

public class MapFragment extends MainFragment implements LocationSource, AMapLocationListener, OnMarkerClickListener,
		OnInfoWindowClickListener, InfoWindowAdapter, OnMapClickListener {

	private MapView mapView;
	private AMap aMap;
	private OnLocationChangedListener mListener;
	private LocationManagerProxy mAMapLocationManager;
	private boolean mImageClickDown = false;
	private ImageView mImageView;
	private ListView mWifiListView;
	private WifiListMapAdapter mWifiListMapAdapter;
	Marker currentMarker;

	// ---------------------------------------------------------------------------------------------
	// for Fragment

	private void bingdingTitleUI() {
		mTitlebar.ivHeaderLeft.setVisibility(View.INVISIBLE);
		mTitlebar.llFinish.setVisibility(View.VISIBLE);
		//mTitlebar.llHeaderMy.setVisibility(View.INVISIBLE);
		mTitlebar.tvHeaderRight.setVisibility(View.INVISIBLE);
		mTitlebar.tvTitle.setText(getString(R.string.nearby));
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mPageRoot = inflater.inflate(R.layout.fregment_map, null);
		mapView = (MapView) mPageRoot.findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);
		if (aMap == null) {
			aMap = mapView.getMap();
			setUpMap();
		}
		super.onCreateView(inflater, container, savedInstanceState);
		bingdingTitleUI();

		SlidingUpPanelLayout layout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
		layout.setShadowDrawable(getResources().getDrawable(R.drawable.above_shadow));
		layout.setAnchorPoint(0.3f);
		layout.setPanelSlideListener(new PanelSlideListener() {

			@Override
			public void onPanelSlide(View panel, float slideOffset) {

			}

			@Override
			public void onPanelExpanded(View panel) {

			}

			@Override
			public void onPanelCollapsed(View panel) {

			}

			@Override
			public void onPanelAnchored(View panel) {

			}
		});

		List<WifiInfoScanned> wifiList = new ArrayList<WifiInfoScanned>();
		WifiInfoScanned tempInfoScanned = new WifiInfoScanned();
		tempInfoScanned.mWifiDistance = 10;
		tempInfoScanned.setWifiName("shit");
		tempInfoScanned.setRemark("lalalalala");
		wifiList.add(tempInfoScanned);
		mWifiListView = (ListView) mPageRoot.findViewById(R.id.wifi_list_map);
		mWifiListMapAdapter = new WifiListMapAdapter(this.getActivity(), wifiList);
		mWifiListView.setAdapter(mWifiListMapAdapter);
		
		mImageView = (ImageView) mPageRoot.findViewById(R.id.brought_by);
		mImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				if (mImageClickDown) {
					mImageView.setImageResource(R.drawable.map_wifi_down);
				} else {
					mImageView.setImageResource(R.drawable.map_wifi_up);
				}
				mImageClickDown = !mImageClickDown;
			}
		});

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

		aMap.setOnMapClickListener(this);
		aMap.setOnMarkerClickListener(this);
		aMap.setOnInfoWindowClickListener(this);
		aMap.setInfoWindowAdapter(this);// 设置自定义InfoWindow样式
		aMap.setOnMapClickListener(this);
		// centerMarker.setAnimationListener(this);
		// locate.setOnClickListener(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

	// ---------------------------------------------------------------------------------------------
	// for LocationSource
	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		if (mAMapLocationManager == null) {
			mAMapLocationManager = LocationManagerProxy.getInstance(this.getActivity());
			// 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
			// 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），
			// 并且在合适时间调用removeUpdates()方法来取消定位请求
			// 在定位结束后，在合适的生命周期调用destroy()方法
			// 其中如果间隔时间为-1，则定位只定一次
			// 在单次定位情况下，定位无论成功与否，都无需调用removeUpdates()方法移除请求
			mAMapLocationManager.requestLocationData(LocationProviderProxy.AMapNetwork, -1, 3, this);
			mAMapLocationManager.setGpsEnable(false);
		}
	}

	/** 停止定位 */
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

	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		if (mListener != null && amapLocation != null) {
			if (amapLocation != null && amapLocation.getAMapException().getErrorCode() == 0) {
				mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
				CameraUpdate update = CameraUpdateFactory.zoomBy(3);
				aMap.moveCamera(update);

				double Longitude = amapLocation.getLongitude();
				double Latitude = amapLocation.getLatitude();
				LoginHelper loginHelper = LoginHelper.getInstance(getApplicationContext());
				loginHelper.setLongitude(Longitude);
				loginHelper.setLatitude(Latitude);

				// query wifi nearby
				WifiProfile wifis = new WifiProfile();
				wifis.QueryInRadians(getApplicationContext(), new BmobGeoPoint(Longitude, Latitude), 0.025,
						new MultiDataCallback<WifiProfile>() {

							@Override
							public void onSuccess(List<WifiProfile> objects) {
								Log.d("map", "查询周围未登记wifi成功：共" + objects.size() + "条数据。");
								showToast("查询周围wifi成功：共" + objects.size() + "条数据。");
								// add wifi label
								DisplayNearbyWifi(objects);
							}

							@Override
							public void onFailed(String msg) {
								Log.d("map", "您附近还没有可用wifi信号，请更换位置再试一次。");
								showToast("您附近还没有可用wifi信号，请更换位置：" + msg);
							}

						});

				/*WifiProfile wifisu = new WifiProfile(WifiProfile.table_name_wifiunregistered);
				wifisu.QueryInRadians(getApplicationContext(), new BmobGeoPoint(Longitude, Latitude), 1,
						new MultiDataCallback<WifiProfile>() {

							@Override
							public void onSuccess(List<WifiProfile> objects) {
								Log.d("map", "查询周围未登记wifi成功：共" + objects.size() + "条数据。");
								showToast("查询周围wifi成功：共" + objects.size() + "条数据。");
								// add wifi label
								DisplayNearbyWifi(objects);
							}

							@Override
							public void onFailed(String msg) {
								Log.d("map", "您附近还没有可用wifi信号，请更换位置再试一次。");
								showToast("您附近还没有可用wifi信号，请更换位置。");
							}

						});*/

				aMap.setOnMarkerClickListener(this);
				aMap.setOnInfoWindowClickListener(this);
				aMap.setInfoWindowAdapter(this);
			} else {
				Log.e("AmapErr", "Location ERR:" + amapLocation.getAMapException().getErrorCode());
			}
		}
	}

	// ---------------------------------------------------------------------------------------------
	// for MarkerClickListener
	@Override
	public boolean onMarkerClick(Marker marker) {
		// TODO Auto-generated method stub
		currentMarker = marker;
		WifiProfile mWP = (WifiProfile) marker.getObject();
		if (mWP != null)
			Log.e("marker", "Maker WifiProfile:" + mWP.toString());
		marker.showInfoWindow();
		return false;
	}

	// ---------------------------------------------------------------------------------------------
	// for OnInfoWindowClickListener
	@Override
	public void onInfoWindowClick(Marker marker) {
		// TODO Auto-generated method stub
		Log.e("marker", "onInfoWindowClick: " + marker.getId());
	}

	// ---------------------------------------------------------------------------------------------
	// for InfoWindowAdapter
	@Override
	public View getInfoContents(Marker marker) {
		// TODO Auto-generated method stub
		Log.e("marker", "getInfoContents: " + marker.getId());
		return null;
	}

	@Override
	public View getInfoWindow(Marker marker) {
		// TODO Auto-generated method stub
		View infoWindow = getActivity().getLayoutInflater().inflate(R.layout.map_wifi_info_window, null);
		currentMarker = marker;
		render(marker, infoWindow);
		return infoWindow;
	}

	/** 自定义infowinfow窗口，动态修改内容的 */
	private void render(Marker marker, View infoWindow) {
		// TODO Auto-generated method stub
		final WifiProfile mWP = (WifiProfile) marker.getObject();
		Log.e("marker", "marker :getInfoWindow  " + marker.getId());
		TextView disTV = (TextView) infoWindow.findViewById(R.id.distance);
		TextView disExtTV = (TextView) infoWindow.findViewById(R.id.distance_ext);
		TextView wifiNameTV = (TextView) infoWindow.findViewById(R.id.wifi_name);
		TextView wifiNameExtTV = (TextView) infoWindow.findViewById(R.id.wifi_name_ext);
		TextView winExtTV = (TextView) infoWindow.findViewById(R.id.window_ext_info);
		disTV.setText("20m");
		disExtTV.setText("与我当前距离");
		wifiNameTV.setText(mWP.Ssid);
		wifiNameExtTV.setText(mWP.ExtAddress);
		winExtTV.setText("测试中……………………");
		infoWindow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent("com.anynet.wifiworld.wifi.ui.DETAILS_DISPLAY");
				Bundle wifiData = new Bundle();
				WifiListHelper mWifiListHelper = WifiListHelper.getInstance(getActivity());
				List<WifiInfoScanned> list = mWifiListHelper.getWifiFrees();
				list.addAll(mWifiListHelper.getWifiEncrypts());
				for (WifiInfoScanned wifiInfoScanned : list) {
					if (wifiInfoScanned.getWifiMAC().equals(mWP.MacAddr)) {
						WifiHandleDB.getInstance(getActivity()).queryWifiProfile(wifiInfoScanned);
						wifiData.putSerializable("WifiSelected", wifiInfoScanned);
						intent.putExtras(wifiData);
						getActivity().startActivity(intent);
						return;
					}
				}
				Toast.makeText(getActivity(), "Wifi信息有错！", Toast.LENGTH_LONG).show();
			}
		});
	}

	// 点击非marker区域，将显示的InfoWindow隐藏
	@Override
	public void onMapClick(LatLng latLng) {
		if (currentMarker != null) {
			currentMarker.hideInfoWindow();
		}
	}

	// ---------------------------------------------------------------------------------------------
	// for self-define functions
	private void setUpMap() {
		MyLocationStyle myLocationStyle = new MyLocationStyle();
		// myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.gps));
		myLocationStyle.strokeColor(Color.GRAY);
		myLocationStyle.strokeWidth(1);
		aMap.setMyLocationStyle(myLocationStyle);

		aMap.setLocationSource(this);// 设置定位监听
		aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
		// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
		aMap.setMyLocationEnabled(true);
		// 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
		aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_FOLLOW);
	}

	private void DisplayNearbyWifi(final List<WifiProfile> wifilist) {
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < wifilist.size(); ++i) {
					WifiProfile wifi = wifilist.get(i);
					LatLng llwifi1 = new LatLng(wifi.Geometry.getLatitude(), wifi.Geometry.getLongitude());
					MarkerOptions mMO = new MarkerOptions();
					Marker mM = aMap.addMarker(mMO.position(llwifi1).title(wifi.Alias)
							.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_geo)).draggable(true));
					mM.setObject(wifi);
					mM.showInfoWindow();
				}
			}
		});
	}
}
