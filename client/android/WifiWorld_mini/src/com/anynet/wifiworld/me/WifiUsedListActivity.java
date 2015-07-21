package com.anynet.wifiworld.me;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.RouteSearch.FromAndTo;
import com.amap.api.services.route.RouteSearch.WalkRouteQuery;
import com.anynet.wifiworld.BaseActivity;
import com.anynet.wifiworld.R;
import com.anynet.wifiworld.data.MultiDataCallback;
import com.anynet.wifiworld.data.WifiDynamic;
import com.anynet.wifiworld.data.WifiProfile;
import com.anynet.wifiworld.util.LoginHelper;
import com.anynet.wifiworld.wifi.ui.WifiDetailsActivity;

public class WifiUsedListActivity extends BaseActivity implements OnMapClickListener,OnMarkerClickListener, 
	LocationSource, OnInfoWindowClickListener, InfoWindowAdapter, AMapLocationListener {

	//IPC
	private List<WifiProfile> mListData = new ArrayList<WifiProfile>();
	private static final String TAG = WifiUsedListActivity.class.getSimpleName();
	private MapView mapView;
	private AMap aMap;
	private OnLocationChangedListener mListener;
	private LocationManagerProxy mAMapLocationManager;
	private Marker currentMarker;
	private Map<String, Marker> allMarkers = new HashMap<String, Marker>();
	private LatLng mMyPosition;
	private RouteSearch routeSearch;
	private MarkerOptions markOptions;

	// ---------------------------------------------------------------------------------------------
	// for Fragment
	
	private void bingdingTitleUI() {
		mTitlebar.ivHeaderLeft.setVisibility(View.VISIBLE);
		mTitlebar.tvTitle.setText("我用过的网络");
		mTitlebar.ivHeaderLeft.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_wifi_used_list);
		super.onCreate(savedInstanceState);
		bingdingTitleUI();
		//地图
		mapView = (MapView)this.findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 必须要写
		aMap = mapView.getMap();
        setUpMap();
		aMap.setOnMapClickListener(this);
        aMap.setOnMarkerClickListener(this);
        aMap.setOnInfoWindowClickListener(this);
        aMap.setInfoWindowAdapter(this);// 设置自定义InfoWindow样式
        aMap.setOnMapClickListener(this);
        
        markOptions = new MarkerOptions();
        markOptions.title("currentLocation");
        markOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getResources(), R.drawable.ic_location_current)));

		//查询服务器
		WifiDynamic records = new WifiDynamic();
		records.Userid = LoginHelper.getInstance(this).getCurLoginUserInfo().getUsername();
		records.MarkLoginTime();
		records.QueryWiFiInOneWeek(this, records.LoginTime, new MultiDataCallback<WifiDynamic>() {

			@Override
            public boolean onSuccess(List<WifiDynamic> objects) {
	            //分析一周的上网记录
				analyseList(objects);
				return false;
            }

			@Override
            public boolean onFailed(String msg) {
				Log.d("WifiUsedListActivity", "当前网络不稳定，请稍后再试：" + msg);
				return false;
            }
			
		});
	}
	
	@Override
	public void onPause() {
		super.onPause();
		mapView.onPause();
		if (currentMarker != null) {
			currentMarker.hideInfoWindow();
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		deactivate();
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
	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		if (mAMapLocationManager == null) {
			mAMapLocationManager = LocationManagerProxy.getInstance(this);
			mAMapLocationManager.setGpsEnable(false);
			// 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
			// 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），
			// 并且在合适时间调用removeUpdates()方法来取消定位请求
			// 在定位结束后，在合适的生命周期调用destroy()方法
			// 其中如果间隔时间为-1，则定位只定一次
			// 在单次定位情况下，定位无论成功与否，都无需调用removeUpdates()方法移除请求
			mAMapLocationManager.requestLocationData(LocationProviderProxy.AMapNetwork, -1, 50, this);
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
	

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	// ---------------------------------------------------------------------------------------------
	// for AMapLocationListener
	
	AMapLocation mAMapLocation;
	
	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		if (mListener != null && amapLocation != null) {
			if (amapLocation != null && amapLocation.getAMapException().getErrorCode() == 0) {
				this.mAMapLocation = amapLocation;
				mListener.onLocationChanged(amapLocation);
				double Longitude = amapLocation.getLongitude();
				double Latitude = amapLocation.getLatitude();
				mMyPosition = new LatLng(Latitude, Longitude);
				
				aMap.clear();
				Marker mGPSMarker = aMap.addMarker(markOptions);
				mGPSMarker.setPosition(mMyPosition);
				//CameraUpdate update = CameraUpdateFactory.newLatLngZoom(mMyPosition, (float) 20.0);
				//aMap.moveCamera(update);
			    //aMap.setOnMarkerClickListener(this);
				//aMap.setOnInfoWindowClickListener(this);
				//aMap.setInfoWindowAdapter(this);
				
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
		if(currentMarker != null && currentMarker.getTitle() != "currentLocation"){
			currentMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_marker));
		}
		currentMarker = marker;
		if(currentMarker.getTitle() != "currentLocation"){
			marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_marker_selected));
		}
		marker.showInfoWindow();
		return true;
	}

	// ---------------------------------------------------------------------------------------------
	// for OnInfoWindowClickListener
	@Override
	public void onInfoWindowClick(Marker marker) {
	}

	// ---------------------------------------------------------------------------------------------
	// for InfoWindowAdapter
	@Override
	public View getInfoContents(Marker marker) {
		return null;
	}

	@Override
	public View getInfoWindow(Marker marker) {
		View infoWindow = this.getLayoutInflater().inflate(R.layout.used_wifi_info_window, null);
		currentMarker = marker;
		render(marker, infoWindow);
		return infoWindow;
	}

	/** 自定义infowinfow窗口，动态修改内容的 */
	private void render(final Marker marker, View infoWindow) {
		final WifiProfile mWP = (WifiProfile) marker.getObject();
		TextView disTV = (TextView) infoWindow.findViewById(R.id.distance);
		TextView wifiAlias = (TextView) infoWindow.findViewById(R.id.tv_wifi_alias);
		TextView wifiNameTV = (TextView) infoWindow.findViewById(R.id.wifi_name);
		TextView wifiNameExtTV = (TextView) infoWindow.findViewById(R.id.wifi_name_ext);
		
		//换算距离
		final LatLng pos = new LatLng(mWP.Geometry.getLatitude(), mWP.Geometry.getLongitude());
		float distance = AMapUtils.calculateLineDistance(mMyPosition, pos);
		disTV.setText((int)distance + "米");
		wifiAlias.setText(mWP.Ssid);
		wifiNameTV.setText(mWP.Alias);
		wifiNameExtTV.setText(mWP.ExtAddress);
		infoWindow.findViewById(R.id.distance_ext).setOnClickListener(new OnClickListener() {
			@Override
            public void onClick(View v) {
				
				LatLonPoint to_point = new LatLonPoint(pos.latitude, pos.longitude);
				LatLonPoint from_point = new LatLonPoint(mMyPosition.latitude, mMyPosition.longitude);
				FromAndTo fromAndTo = new RouteSearch.FromAndTo(from_point, to_point);
				WalkRouteQuery query = new WalkRouteQuery(fromAndTo, RouteSearch.WalkDefault);
				routeSearch.calculateWalkRouteAsyn(query);
            }
			
		});
		
		infoWindow.findViewById(R.id.ll_map_infowindows_desc).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
						       
				Intent i = new Intent(WifiUsedListActivity.this, WifiDetailsActivity.class);
				Bundle wifiData = new Bundle();
				wifiData.putSerializable(WifiProfile.TAG, (WifiProfile)currentMarker.getObject());
				i.putExtras(wifiData);
				WifiUsedListActivity.this.startActivity(i);
			}
		});
		
		//显示认证WiFi的logo
		ImageView logo = (ImageView) infoWindow.findViewById(R.id.iv_map_wifi_logo);
		logo.setImageBitmap(mWP.getLogo());
	}

	// 点击非marker区域，将显示的InfoWindow隐藏
	@Override
	public void onMapClick(LatLng latLng) {
		if (currentMarker != null) {
			currentMarker.hideInfoWindow();
			currentMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_marker));
		}
	}

	// ---------------------------------------------------------------------------------------------
	// for self-define functions
	private void setUpMap() {
		aMap.setLocationSource(this);// 设置定位监听
		aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
		// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
		aMap.setMyLocationEnabled(true);
		// 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
		aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
	}
	
	public Marker getMarkerByWifiProfile(WifiProfile wifi){
		LatLng llwifi1 = new LatLng(wifi.Geometry.getLatitude(), wifi.Geometry.getLongitude());
		MarkerOptions mMO = new MarkerOptions();
		Marker mM = aMap.addMarker(mMO.position(llwifi1).title(wifi.Alias)
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_marker)).draggable(true));
		mM.setObject(wifi);
		return mM;
	}
	
	private void analyseList(List<WifiDynamic> objects) {
		//先去重
		Map<String, Long> order = new HashMap<String, Long>();
		for (WifiDynamic item : objects) {
			if (!order.containsKey(item.MacAddr)) {
				order.put(item.MacAddr, (long) 0);
			}
			order.put(item.MacAddr, order.get(item.MacAddr) + 1);
		}
		
		//再查询 TODO(binfei):这里的查询调用的api可能过多，需要优化
		List<String> items = new ArrayList<String>();
		for (String item : order.keySet()) {
			items.add(item);
		}
		
		WifiProfile wifi = new WifiProfile();
		wifi.BatchQueryByMacAddress(getApplicationContext(), items, true, new MultiDataCallback<WifiProfile>() {

			@Override
            public boolean onFailed(String msg) {
                // TODO Auto-generated method stub
				return false;
            }

			@Override
            public boolean onSuccess(List<WifiProfile> objects) {
				PolylineOptions opt = new PolylineOptions();
				opt.width(20).color(Color.RED).setDottedLine(true).geodesic(true);
				opt.add(mMyPosition);
				LatLng southwest = mMyPosition;
				LatLng northeast = mMyPosition;
				mListData = objects;
				allMarkers.clear();
				for (int i = 0; i < mListData.size(); ++i) {
					WifiProfile wifi = mListData.get(i);
					Marker mM = getMarkerByWifiProfile(wifi);
					allMarkers.put(wifi.MacAddr, mM);
					LatLng tmp = new LatLng(wifi.Geometry.getLatitude(), wifi.Geometry.getLongitude());
					opt.add(tmp);
					if (southwest.latitude > wifi.Geometry.getLatitude() && 
						southwest.longitude > wifi.Geometry.getLongitude()) {
						southwest = tmp;
					} else if (northeast.latitude < wifi.Geometry.getLatitude() && 
						northeast.longitude < wifi.Geometry.getLongitude()) {
						northeast = tmp;
					}
				}
				//重新规划可视区域
				opt.add(mMyPosition);
				aMap.addPolyline(opt);
				LatLngBounds bounds = new LatLngBounds(southwest, northeast);
				CameraUpdate update = CameraUpdateFactory.newLatLngBounds(bounds, 1);
				aMap.moveCamera(update);
				return false;
            }
			
		});
	}
}
