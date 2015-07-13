package com.anynet.wifiworld.me;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.PendingIntent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.location.LocationManagerProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.route.RouteSearch;
import com.anynet.wifiworld.BaseActivity;
import com.anynet.wifiworld.R;
import com.anynet.wifiworld.data.MultiDataCallback;
import com.anynet.wifiworld.data.WifiDynamic;
import com.anynet.wifiworld.data.WifiProfile;
import com.anynet.wifiworld.util.LoginHelper;

public class WifiUsedListActivity extends BaseActivity implements OnMapClickListener,OnMarkerClickListener, LocationSource {

	//IPC
	private List<WifiProfile> mListData = new ArrayList<WifiProfile>();
	//private List<WifiDynamic> mListData;
	private ListAdapter mAdapter;
	
	private static final String TAG = MapFragment.class.getSimpleName();
	private MapView mapView;
	private AMap aMap;
	private OnLocationChangedListener mListener;
	private LocationManagerProxy mAMapLocationManager;
	private ImageView mImageView;
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
	
	private void bingdingTitleUI() {
		mTitlebar.ivHeaderLeft.setVisibility(View.VISIBLE);
		mTitlebar.tvTitle.setText("我用过的Wi-Fi");
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

		if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
		aMap.setOnMapClickListener(this);
        aMap.setOnMarkerClickListener(this);
        //aMap.setOnInfoWindowClickListener(this);
        //aMap.setInfoWindowAdapter(this);// 设置自定义InfoWindow样式
        aMap.setOnMapClickListener(this);
        
        markOptions = new MarkerOptions();
        markOptions.title("currentLocation");
        markOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getResources(), R.drawable.ic_location_current)));
        circleOptions = new CircleOptions();
        mLoaded = true;
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
	            //showToast("当前网络不稳定，请稍后再试：" + msg);
				return false;
            }
			
		});
	}

	private void setUpMap() {
		aMap.setLocationSource(this);// 设置定位监听
		aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
		// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
		aMap.setMyLocationEnabled(true);
		// 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
		aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
	}
	
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		mapView.startLayoutAnimation();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mapView.onDestroy();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub		
		super.onResume();
		mapView.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mapView.onPause();
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		mapView.onSaveInstanceState(savedInstanceState);
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
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
				mListData = objects;
				displayList();
				return false;
            }
			
		});
	}
		
	
	public Marker getMarkerByWifiProfile(WifiProfile wifi){
		LatLng llwifi1 = new LatLng(wifi.Geometry.getLatitude(), wifi.Geometry.getLongitude());
		MarkerOptions mMO = new MarkerOptions();
		Marker mM = aMap.addMarker(mMO.position(llwifi1).title(wifi.Alias)
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_marker)).draggable(true));
		mM.setObject(wifi);
		return mM;
	}
	private void displayList() {
		
		mAdapter = new ListAdapter();
		
		allMarkers.clear();
		for (int i = 0; i < mListData.size(); ++i) {
			WifiProfile wifi = mListData.get(i);
			Marker mM = getMarkerByWifiProfile(wifi);
			allMarkers.put(wifi.MacAddr, mM);
		}
		// step 1. create a MenuCreator
		
		// set creator
		
		// set SwipeListener
		
		
	}
	
	class ListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mListData.size();
		}

		@Override
		public WifiProfile getItem(int position) {
			return mListData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			ViewHolder holder = (ViewHolder) convertView.getTag();
			WifiProfile item = getItem(position);
			//holder.iv_icon.setImageDrawable(item.loadIcon(getPackageManager()));
			holder.tv_wifi_name.setText(item.Ssid);
			holder.tv_wifi_alias.setText(item.Alias);
			holder.tv_wifi_addr.setText(item.ExtAddress);
			holder.tv_connect_count.setText("已连接46次");
			holder.tv_connect_time.setText("已连接17小时");
			return convertView;
		}

		class ViewHolder {
			//ImageView iv_icon;
			TextView tv_wifi_name;
			TextView tv_wifi_alias;
			TextView tv_wifi_addr;
			TextView tv_connect_count;
			TextView tv_connect_time;

			public ViewHolder(View view) {
				//iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
				
				view.setTag(this);
			}
		}
	}

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}

	@Override
	public void onMapClick(LatLng arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onMarkerClick(Marker arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void activate(OnLocationChangedListener arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deactivate() {
		// TODO Auto-generated method stub
		
	}
}
