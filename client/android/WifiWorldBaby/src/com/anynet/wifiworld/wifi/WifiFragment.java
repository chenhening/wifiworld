package com.anynet.wifiworld.wifi;

import java.util.List;

import com.anynet.wifiworld.MainActivity.MainFragment;
import com.anynet.wifiworld.R;
import com.anynet.wifiworld.api.WifiListHelper;
import com.anynet.wifiworld.util.LoginHelper;
import com.anynet.wifiworld.wifi.WifiStatusReceiver.OnWifiStatusListener;

import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class WifiFragment extends MainFragment {
	private final static String TAG = WifiFragment.class.getSimpleName();
	
	static final int WIFI_CONNECT_CONFIRM = 0;
	
	private WifiAdmin mWifiAdmin;
	private ListView mWifiListView;
	private WifiListAdapter mWifiListAdapter;
	private List<WifiInfoScanned> mWifiFree;
	private List<WifiInfoScanned> mWifiEncrypt;
	private WifiListHelper mWifiListHelper;
	
	private TextView mWifiNameView;
	private LinearLayout mWifiSquareLayout;
	private PopupWindow mPopupWindow;
	
	private boolean mBroadcastRegistered;
	
	private WifiInfo mWifiInfo = null;

	private WifiStatusReceiver.WifiMonitorService mWifiMonitorService;
	ServiceConnection conn = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mWifiMonitorService = ((WifiStatusReceiver.WifiMonitorService.WifiStatusBinder)service).getService();
			mWifiMonitorService.setOnWifiStatusListener(new OnWifiStatusListener() {
				
				@Override
				public void onChanged(String str) {
					mWifiNameView.setText(str);
					mWifiNameView.setTextColor(Color.BLACK);
				}
			});
		}
	};
	
	private void bingdingTitleUI() {
		mTitlebar.ivHeaderLeft.setVisibility(View.INVISIBLE);
		mTitlebar.llFinish.setVisibility(View.VISIBLE);
		mTitlebar.llHeaderMy.setVisibility(View.INVISIBLE);
		mTitlebar.tvHeaderRight.setVisibility(View.INVISIBLE);
		// mTitlebar.llFinish.setOnClickListener(this);
		mTitlebar.tvTitle.setText(getString(R.string.connect));
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mWifiListHelper = WifiListHelper.getInstance(getActivity());
		mWifiAdmin = mWifiListHelper.getWifiAdmin();
		//WifiStatusReceiver.schedule(getActivity());
		WifiStatusReceiver.bindWifiService(getActivity(), conn);
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(LoginHelper.LOGIN_SUCCESS);
		getActivity().registerReceiver(mLoginReceiver, filter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		mPageRoot = inflater.inflate(R.layout.fragment_wifi, null);
		//handle WIFI square view
		mWifiSquareLayout = (LinearLayout) mPageRoot.findViewById(R.id.wifi_square);
		//initial WIFI square pop-up view
		initWifiSquarePopupView();
		//set click listener for WIFI square view
		setWifiSquareListener(mWifiSquareLayout);
		//display WIFI SSID which is connected or not
		mWifiNameView = (TextView) mPageRoot.findViewById(R.id.wifi_name);
		mWifiListHelper.fillWifiList();
		mWifiFree = mWifiListHelper.getWifiFrees();
		mWifiEncrypt = mWifiListHelper.getWifiEncrypts();
		updateWifiConMore(mWifiNameView);
		if (mWifiAdmin.getWifiNameConnection() != "") {
			mWifiNameView.setText("已连接"	+ WifiAdmin.convertToNonQuotedString(mWifiAdmin.getWifiNameConnection()));
			mWifiNameView.setTextColor(Color.BLACK);
		}
		
		//WIFI list view display and operation
		mWifiListView = (ListView) mPageRoot.findViewById(R.id.wifi_list_view);
		mWifiListAdapter = new WifiListAdapter(this.getActivity(), mWifiFree, mWifiEncrypt);
		mWifiListView.setAdapter(mWifiListAdapter);
		mWifiListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position < (mWifiFree.size() + 1)) {
					WifiInfoScanned wifiSelected = mWifiFree.get(position - 1);
					Intent intent = new Intent("com.farproc.wifi.connecter.action.CONNECT_WIFI_CONFIRM");
					Bundle bundle = new Bundle();
					bundle.putSerializable("WifiSelected", wifiSelected);
					intent.putExtras(bundle);
					startActivityForResult(intent, WIFI_CONNECT_CONFIRM);
					
//					boolean connResult = false;
//					WifiConfiguration cfgSelected = mWifiAdmin.getWifiConfiguration(wifiSelected);
//					if (cfgSelected != null) {
//						connResult = mWifiAdmin.connectToConfiguredNetwork(getActivity(),
//								mWifiAdmin.getWifiConfiguration(wifiSelected), true);
//					} else {
//						connResult = mWifiAdmin.connectToNewNetwork(getActivity(), wifiSelected, mNumOpenNetworksKept);
//					}
//					if (connResult) {
//						mWifiListHelper.fillWifiList();
//						mWifiFree = mWifiListHelper.getWifiFrees();
//						mWifiEncrypt = mWifiListHelper.getWifiEncrypts();
//						updateWifiConMore(mWifiNameView);
//						mWifiListAdapter.refreshWifiList(mWifiFree, mWifiEncrypt);
//					} else {
//						Toast.makeText(getActivity(), "Failed to connect to " + wifiSelected.getWifiName(), Toast.LENGTH_LONG).show();
//					}
				}
			}
		});
		
		//bind common title UI
		super.onCreateView(inflater, container, savedInstanceState);
		bingdingTitleUI();
		
		return mPageRoot;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == WIFI_CONNECT_CONFIRM && resultCode == android.app.Activity.RESULT_OK) {
			mWifiListHelper.fillWifiList();
			mWifiFree = mWifiListHelper.getWifiFrees();
			mWifiEncrypt = mWifiListHelper.getWifiEncrypts();
			updateWifiConMore(mWifiNameView);
			mWifiListAdapter.refreshWifiList(mWifiFree, mWifiEncrypt);
		}
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub		
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		getActivity().unregisterReceiver(mLoginReceiver);
		super.onDestroy();
	}

	@Override
	public void onPause() {
//		if (mBroadcastRegistered) {
//			getActivity().unregisterReceiver(mReceiver);
//		}
		
		super.onPause();
	}

	@Override
	public void onResume() {
		if (mWifiListAdapter != null) {
			mWifiListHelper.fillWifiList();
			mWifiFree = mWifiListHelper.getWifiFrees();
			mWifiEncrypt = mWifiListHelper.getWifiEncrypts();
			updateWifiConMore(mWifiNameView);
			mWifiListAdapter.refreshWifiList(mWifiFree, mWifiEncrypt);
		}
//		final IntentFilter filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
//		getActivity().registerReceiver(mReceiver, filter);
//		mBroadcastRegistered = true;
		super.onResume();
	}

//	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
//		
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			final String action = intent.getAction();
//			if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
//				new Thread() {
//					@Override
//					public void run() {
//						try {
//							sleep(5000);
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//						mWifiListHelper.fillWifiList();
//						mWifiFree = mWifiListHelper.getWifiFrees();
//						mWifiEncrypt = mWifiListHelper.getWifiEncrypts();
//						handler.sendEmptyMessage(UPDATE_VIEW);
//					}
//				}.start();
//			}
//		}
//	};
//
//	private Handler handler = new Handler() {
//
//		@Override
//		public void handleMessage(Message msg) {
//			int value = msg.what;
//			if (value == UPDATE_VIEW) {
//				displayWifiConnected(mWifiNameView);
//				mWifiListAdapter.refreshWifiList(mWifiFree, mWifiEncrypt);
//			}
//			super.handleMessage(msg);
//		}
//		
//	};
	
	BroadcastReceiver mLoginReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(LoginHelper.LOGIN_SUCCESS)) {
				if (mWifiAdmin.getWifiNameConnection() != "") {
					WifiHandleDB.getInstance(getActivity()).updateWifiDynamic(mWifiAdmin.getWifiConnection());
				}
			}
		}
	};
	
	private void updateWifiConMore(TextView wifiNameView) {
		WifiInfo wifiConnected = mWifiAdmin.getWifiConnection();
		if (!wifiConnected.getSSID().equals("")) {
//			wifiNameView.setText("已连接"	+ WifiAdmin.convertToNonQuotedString(wifiConnected.getSSID()));
//			wifiNameView.setTextColor(Color.BLACK);
			mWifiListHelper.rmWifiConnected(WifiAdmin.convertToNonQuotedString(wifiConnected.getSSID()));
			mWifiSquareLayout.setVisibility(View.VISIBLE);
			if (mWifiInfo == null || !mWifiInfo.getMacAddress().equals(wifiConnected.getMacAddress())) {
				WifiHandleDB.getInstance(getActivity()).updateWifiDynamic(wifiConnected);
			}
		} else {
//			wifiNameView.setText("未连接任何Wifi");
			mWifiSquareLayout.setVisibility(View.GONE);
		}
		
		mWifiInfo = wifiConnected;
	}
	
	private void initWifiSquarePopupView() {
		if (mPopupWindow == null) {
			LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View popupView = layoutInflater.inflate(R.layout.wifi_popup_view, null);
			//create one pop-up window object
			mPopupWindow = new PopupWindow(popupView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			Button testBtn = (Button) popupView.findViewById(R.id.start_button);
			testBtn.setOnClickListener(new WifiSpeedTester(popupView));
		}
	}
	
	private void showPopupWindow(View view) {
		Log.i(TAG, "show PopupWindow");
		mPopupWindow.setFocusable(true);
		mPopupWindow.setOutsideTouchable(false);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		mPopupWindow.setAnimationStyle(R.style.PopupAnimation);
		mPopupWindow.showAsDropDown(view);
	}
	
	private void setWifiSquareListener(LinearLayout wifiTasteLayout) {
		TextView wifiSpeed = (TextView) wifiTasteLayout.findViewById(R.id.wifi_speed_test);
		wifiSpeed.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				showPopupWindow(view);
			}
		});
	}
}
