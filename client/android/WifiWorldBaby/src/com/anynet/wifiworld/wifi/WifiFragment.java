package com.anynet.wifiworld.wifi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.anynet.wifiworld.MainActivity.MainFragment;
import com.anynet.wifiworld.R.id;
import com.anynet.wifiworld.R.layout;
import com.anynet.wifiworld.R.string;
import com.anynet.wifiworld.R.style;
import com.anynet.wifiworld.MainActivity;
import com.anynet.wifiworld.R;

import android.app.ActionBar.LayoutParams;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class WifiFragment extends MainFragment {
	private final static String TAG = WifiFragment.class.getSimpleName();

	private WifiAdmin mWifiAdmin;
	private ListView mWifiListView;
	private WifiListAdapter mWifiListAdapter;
	private List<WifiInfoScanned> mWifiFree;
	private List<WifiInfoScanned> mWifiEncrypt;
	
	private TextView mWifiNameView;
	private LinearLayout mWifiTasteLayout;
	private PopupWindow mPopupWindow;

	public void organizeWifiList(final List<ScanResult> wifiList) {
		mWifiFree.clear();
		mWifiEncrypt.clear();
		
		for (int i = 0; i < wifiList.size(); i++) {
			ScanResult hotspot = wifiList.get(i);
			WifiConfiguration wifiCfg = mWifiAdmin.getWifiConfiguration(hotspot, null);
			if (wifiCfg != null) {
				String wifiName = hotspot.SSID;
				String wifiPwd = wifiCfg.preSharedKey;
				String wifiType = WifiAdmin.ConfigSec.getWifiConfigurationSecurity(wifiCfg);
				WifiInfoScanned wifiInfoScanned = new WifiInfoScanned(wifiName,
						wifiPwd, wifiType, mWifiAdmin.getWifiStrength(hotspot.level));
				mWifiFree.add(wifiInfoScanned);
			} else {
				mWifiEncrypt.add(new WifiInfoScanned(hotspot.SSID, null,
						null, mWifiAdmin.getWifiStrength(hotspot.level)));
			}
		}
	}

	private void bingdingTitleUI() {
		mTitlebar.ivHeaderLeft.setVisibility(View.INVISIBLE);
		mTitlebar.llFinish.setVisibility(View.VISIBLE);
		mTitlebar.llHeaderMy.setVisibility(View.INVISIBLE);
		mTitlebar.tvHeaderRight.setVisibility(View.INVISIBLE);
		// mTitlebar.llFinish.setOnClickListener(this);
		mTitlebar.tvTitle.setText(getString(R.string.connect));
	}

	private void scanWifi() {
		mWifiAdmin.openWifi();
		organizeWifiList(mWifiAdmin.scanWifi());
	}
	
	private void displayWifiConnected(TextView wifiNameView) {
		String wifiConnected = mWifiAdmin.getWifiNameConnection();
		if (!wifiConnected.equals("")) {
			wifiNameView.setText("已连接"
					+ wifiConnected.substring(1, wifiConnected.length() - 1));
			wifiNameView.setTextColor(Color.BLACK);
			rmWifiConnected(
					wifiConnected.substring(1, wifiConnected.length() - 1),
					mWifiFree, mWifiEncrypt);
			mWifiTasteLayout.setVisibility(View.VISIBLE);
		} else {
			wifiNameView.setText("未连接任何Wifi");
			mWifiTasteLayout.setVisibility(View.GONE);
		}
	}

	//从WIFI列表中删除已连接的WIFI，需要后续更新，比对加密方式
	private boolean rmWifiConnected(String wifiName,
			List<WifiInfoScanned> wifiFree, List<WifiInfoScanned> wifiEncrypt) {
		for (Iterator<WifiInfoScanned> it = wifiFree.iterator(); it.hasNext();) {
			WifiInfoScanned tmpInfo = it.next();
			if (wifiName.equals(tmpInfo.getWifiName())) {
				it.remove();
				return true;
			}
		}

		for (Iterator<WifiInfoScanned> it = wifiEncrypt.iterator(); it.hasNext();) {
			WifiInfoScanned tmpInfo = it.next();
			if (wifiName.equals(tmpInfo.getWifiName())) {
				it.remove();
				return true;
			}
		}

		return false;
	}
	
	private void initWififSquarePopupView() {
		if (mPopupWindow == null) {
			LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View popupView = layoutInflater.inflate(R.layout.wifi_popup_view, null);
			// 创建一个PopuWidow对象
			mPopupWindow = new PopupWindow(popupView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		}
	}
	
	@SuppressWarnings("deprecation")
	private void showPopupWindow(View view) {
		Log.i(TAG, "showPopupWindow");
		mPopupWindow.setFocusable(true);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		mPopupWindow.setAnimationStyle(R.style.PopupAnimation);
		mPopupWindow.showAsDropDown(view);
		Log.i(TAG, "showPopupWindow finish");
	}
	
	private void setWifiTasteListener(LinearLayout wifiTasteLayout) {
		TextView wifiSpeed = (TextView) wifiTasteLayout.findViewById(R.id.wifi_speed_test);
		wifiSpeed.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				showPopupWindow(view);
				
			}
		});
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mWifiAdmin = WifiAdmin.getInstance(getActivity());
		mWifiFree = new ArrayList<WifiInfoScanned>();
		mWifiEncrypt = new ArrayList<WifiInfoScanned>();
		
		scanWifi();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i(TAG, "onCreateView");

		initWififSquarePopupView();
		
		mPageRoot = inflater.inflate(R.layout.fragment_wifi, null);
		mWifiTasteLayout = (LinearLayout) mPageRoot.findViewById(R.id.wifi_taste);
		setWifiTasteListener(mWifiTasteLayout);
		mWifiNameView = (TextView) mPageRoot.findViewById(R.id.wifi_name);
		displayWifiConnected(mWifiNameView);

		mWifiListView = (ListView) mPageRoot.findViewById(R.id.wifi_list_view);
		mWifiListAdapter = new WifiListAdapter(this.getActivity(), mWifiFree,
				mWifiEncrypt);
		mWifiListView.setAdapter(mWifiListAdapter);
		
		mWifiListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position < (mWifiFree.size() + 1)) {
					WifiInfoScanned wifiSelected = mWifiFree.get(position - 1);
//					mWifiAdmin.Connect(wifiSelected.getWifiName(),
//							wifiSelected.getWifiPwd(),
//							wifiSelected.getWifiType());
					mWifiAdmin.connectToConfiguredNetwork(getActivity(), mWifiAdmin.getWifiConfiguration(wifiSelected), false);

					mWifiFree.remove(position - 1);
					mWifiListAdapter.refreshWifiList(mWifiFree, mWifiEncrypt);
					TextView wifi_connected = (TextView) mPageRoot
							.findViewById(R.id.wifi_name);
					String wifiConnected = mWifiAdmin.getWifiNameConnection();
					while (wifiConnected == "") {
						wifiConnected = mWifiAdmin.getWifiNameConnection();
					}
					wifi_connected.setText("已连接"
							+ wifiConnected.substring(1,
									wifiConnected.length() - 1));
					wifi_connected.setTextColor(Color.BLACK);
					wifi_connected.refreshDrawableState();
				}

			}

		});
		super.onCreateView(inflater, container, savedInstanceState);
		
		bingdingTitleUI();
		return mPageRoot;
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}

	@Override
	public void onPause() {
		super.onPause();
		getActivity().unregisterReceiver(mReceiver);
	}

	@Override
	public void onResume() {
		super.onResume();
		final IntentFilter filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		getActivity().registerReceiver(mReceiver, filter);
		scanWifi();
	}
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
				scanWifi();
				displayWifiConnected(mWifiNameView);
				mWifiListAdapter.refreshWifiList(mWifiFree, mWifiEncrypt);
			}
			
		}
	};

}