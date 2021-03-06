/*


 * 
 * Copyright (C) 2015 AnyNet Corporation.  All rights reserved.
 * @Author: buffer(179770346@qq.com or binfeix.li@intel.com)
 * @Time: 2015/01/22
 * 
 * 	v0.1(2015/01/22,1:20): add map2d feature
 * 	v0.2(2015/01/23,1:40): replace map2d to 3dmap and add location feature
 */
package com.anynet.wifiworld.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.datatype.BmobGeoPoint;

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
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.overlay.WalkRouteOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.RouteSearch.FromAndTo;
import com.amap.api.services.route.RouteSearch.OnRouteSearchListener;
import com.amap.api.services.route.RouteSearch.WalkRouteQuery;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.anynet.wifiworld.BaseFragment.MainFragment;
import com.anynet.wifiworld.R;
import com.anynet.wifiworld.data.MultiDataCallback;
import com.anynet.wifiworld.data.WifiProfile;
import com.anynet.wifiworld.map.SlidingUpPanelLayout.PanelSlideListener;
import com.anynet.wifiworld.wifi.ui.WifiDetailsActivity;

public class MapFragment extends MainFragment implements LocationSource, AMapLocationListener, OnMarkerClickListener,
		OnInfoWindowClickListener, InfoWindowAdapter, OnMapClickListener, OnRouteSearchListener {

	private static final String TAG = MapFragment.class.getSimpleName();
	private Context mContext;
	private MapView mapView;
	private AMap aMap;
	private OnLocationChangedListener mListener;
	private LocationManagerProxy mAMapLocationManager;
	private boolean mImageClickDown = false;
	private ImageView mImageView;
	private ListView mWifiListView;
	private WifiListMapAdapter mWifiListMapAdapter;
	private List<WifiProfile> wifiList;
	private Marker currentMarker;
	private Map<String, Marker> allMarkers = new HashMap<String, Marker>();
	private LatLng mMyPosition;
	private RouteSearch routeSearch;
	private PendingIntent mPendingIntent;
	private MarkerOptions markOptions;
	private CircleOptions circleOptions;
	private Circle mCircle;

    private boolean mLoaded = false;
	
	public static final String GEOFENCE_BROADCAST_ACTION = "com.location.apis.geofencedemo.broadcast";
	// ---------------------------------------------------------------------------------------------
	// for Fragment
	Long current;
	private void bingdingTitleUI() {
//		mTitlebar.ivHeaderLeft.setVisibility(View.INVISIBLE);
//		mTitlebar.llFinish.setVisibility(View.VISIBLE);
//		//mTitlebar.llHeaderMy.setVisibility(View.INVISIBLE);
//		mTitlebar.tvHeaderRight.setVisibility(View.INVISIBLE);
//		mTitlebar.tvTitle.setText(getString(R.string.nearby));
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this.getActivity();
		current = System.currentTimeMillis();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.e(TAG, "1 onCreateView:"+(System.currentTimeMillis()-current));
		mPageRoot = inflater.inflate(R.layout.fregment_map, null);
        mapView = (MapView) mPageRoot.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        super.onCreateView(inflater, container, savedInstanceState);

        return mPageRoot;
	}

    @Override
    public void startUpdte() {
        if (!mLoaded ) {
            if (aMap == null) {
                aMap = mapView.getMap();
                setUpMap();
            }
            aMap.setOnMapClickListener(this);
            aMap.setOnMarkerClickListener(this);
            aMap.setOnInfoWindowClickListener(this);
            aMap.setInfoWindowAdapter(this);// 设置自定义InfoWindow样式
            aMap.setOnMapClickListener(this);

            bingdingTitleUI();

            //设置上拉显示按钮
            final SlidingUpPanelLayout layout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
            layout.setShadowDrawable(getResources().getDrawable(R.drawable.above_shadow));
            layout.setAnchorPoint(0.3f);
            layout.setPanelSlideListener(new PanelSlideListener() {

                @Override
                public void onPanelSlide(View panel, float slideOffset) {

                }

                @Override
                public void onPanelExpanded(View panel) {
                    layout.setShadowDrawable(getResources().getDrawable(R.drawable.above_shadow));
                }

                @Override
                public void onPanelCollapsed(View panel) {

                }

                @Override
                public void onPanelAnchored(View panel) {

                }
            });

            wifiList = new ArrayList<WifiProfile>();
            mWifiListView = (ListView) mPageRoot.findViewById(R.id.wifi_list_map);
            mWifiListMapAdapter = new WifiListMapAdapter(getActivity(), wifiList);
            mWifiListView.setAdapter(mWifiListMapAdapter);
            mWifiListView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // TODO Auto-generated method stub
                    WifiListMapAdapter list = (WifiListMapAdapter) parent.getAdapter();
                    if(list!=null && list.getCount()>0){
                        WifiProfile  wf= (WifiProfile) list.getItem(position);
                        zoomAndDisplay(wf);
                        Toast.makeText(list.getContext(), "mac:"+wf.MacAddr, Toast.LENGTH_LONG).show();
                    }
                }
            });
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

            //初始化路径规划api
            routeSearch = new RouteSearch(this.getActivity());
            routeSearch.setRouteSearchListener(this);

            //设置地图围栏
            IntentFilter fliter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            fliter.addAction(GEOFENCE_BROADCAST_ACTION);
            getActivity().registerReceiver(mGeoFenceReceiver, fliter);

            Intent intent = new Intent(GEOFENCE_BROADCAST_ACTION);
            mPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);

            markOptions = new MarkerOptions();
            markOptions.title("currentLocation");
            markOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                    .decodeResource(getResources(), R.drawable.ic_location_current)));
            circleOptions = new CircleOptions();
            Log.e(TAG, "2 onCreateView:"+(System.currentTimeMillis()-current));
            mLoaded = true;
        }
    }
	
	private BroadcastReceiver mGeoFenceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 接受广播
        	String action = intent.getAction();
            if (action != null & action.equals(GEOFENCE_BROADCAST_ACTION)) {
                Bundle bundle = intent.getExtras();
                // 根据广播的status来确定是在区域内还是在区域外
                int status = bundle.getInt("status");
                if (status == 0) {
                    //Toast.makeText(getApplicationContext(), "不在区域",
                    //        Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(getApplicationContext(), "在区域内",
                    //        Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

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
	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		if (mAMapLocationManager == null) {
			mAMapLocationManager = LocationManagerProxy.getInstance(this.getActivity());
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
				reDisplayCenter();

				// query wifi nearby(250米范围内)
				WifiProfile wifis = new WifiProfile();
				WifiProfile.QueryInRadians(getApplicationContext(), new BmobGeoPoint(mAMapLocation.getLongitude(), 
					mAMapLocation.getLatitude()), 0.25, new MultiDataCallback<WifiProfile>() {

						@Override
						public boolean onSuccess(List<WifiProfile> wifiProfiles) {
							showToast("查询周围wifi成功：共" + wifiProfiles.size() + "条数据。");
							wifiList = wifiProfiles;
							DisplayNearbyWifi(wifiProfiles);
							mWifiListMapAdapter.setData(wifiProfiles);
							mWifiListMapAdapter.notifyDataSetChanged();
							return false;
						}

						@Override
						public boolean onFailed(String msg) {
							Log.d("map", "您附近还没有可用wifi信号，请更换位置再试一次。");
							showToast("您附近还没有可用wifi信号，请更换位置：" + msg);
							return false;
						}
				});
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
		// TODO Auto-generated method stub
		View infoWindow = getActivity().getLayoutInflater().inflate(R.layout.map_wifi_info_window, null);
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
						       
				Intent i = new Intent(mContext, WifiDetailsActivity.class);
				Bundle wifiData = new Bundle();
				wifiData.putSerializable(WifiProfile.TAG, (WifiProfile)currentMarker.getObject());
				i.putExtras(wifiData);
				mContext.startActivity(i);
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

	private void DisplayNearbyWifi(final List<WifiProfile> wifilist) {

		allMarkers.clear();
		for (int i = 0; i < wifilist.size(); ++i) {
			WifiProfile wifi = wifilist.get(i);
			Marker mM = getMarkerByWifiProfile(wifi);
			allMarkers.put(wifi.MacAddr, mM);
		}
	}
	
	public Marker getMarkerByWifiProfile(WifiProfile wifi){
		LatLng llwifi1 = new LatLng(wifi.Geometry.getLatitude(), wifi.Geometry.getLongitude());
		MarkerOptions mMO = new MarkerOptions();
		Marker mM = aMap.addMarker(mMO.position(llwifi1).title(wifi.Alias)
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_marker)).draggable(true));
		mM.setObject(wifi);
		return mM;
	}
	
	@Override
    public void onBusRouteSearched(BusRouteResult arg0, int arg1) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void onDriveRouteSearched(DriveRouteResult arg0, int arg1) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void onWalkRouteSearched(WalkRouteResult result, int rCode) {
		if (rCode == 0) {
			if (result != null && result.getPaths() != null
					&& result.getPaths().size() > 0) {
				WalkPath walkPath = result.getPaths().get(0);
				aMap.clear();// 清理地图上的所有覆盖物
				WalkRouteOverlay walkRouteOverlay = new WalkRouteOverlay(this.getActivity(),
						aMap, walkPath, result.getStartPos(), result.getTargetPos());
				walkRouteOverlay.removeFromMap();
				walkRouteOverlay.addToMap();
				walkRouteOverlay.zoomToSpan();
			}
		}
    }
	
	private void reDisplayCenter() {
		aMap.clear();
		Marker mGPSMarker = aMap.addMarker(markOptions);
		mGPSMarker.setPosition(mMyPosition);
		CameraUpdate update = CameraUpdateFactory.newLatLngZoom(mMyPosition, (float) 20.0);
		aMap.moveCamera(update);

		mAMapLocationManager.removeGeoFenceAlert(mPendingIntent);
		if (mCircle != null)
			mCircle.remove();
		mAMapLocationManager.addGeoFenceAlert(mMyPosition.latitude, mMyPosition.longitude, 50, 1000 * 60 * 30, mPendingIntent);
		circleOptions.center(mMyPosition).radius(50).fillColor(Color.argb(100, 255, 240, 207)).strokeColor(Color.argb(100, 254, 226, 207));
		mCircle = aMap.addCircle(circleOptions);
	}
	
	public void zoomAndDisplay(WifiProfile wp) {
		reDisplayCenter();
		if(!allMarkers.isEmpty()){
			Marker mk = allMarkers.get(wp.MacAddr);
			if(mk!=null)mk.showInfoWindow();
		}
	}

	@Override
	protected void onVisible() {
		// TODO Auto-generated method stub
        Log.d(TAG, "onVisible");

    }

	@Override
	protected void onInvisible() {
		// TODO Auto-generated method stub
		Log.d(TAG, "onInvisible");
    }
}
