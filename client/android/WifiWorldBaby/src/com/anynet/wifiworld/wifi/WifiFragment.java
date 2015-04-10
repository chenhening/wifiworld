package com.anynet.wifiworld.wifi;

import java.util.ArrayList;
import java.util.List;

import com.anynet.wifiworld.MainActivity;
import com.anynet.wifiworld.MainActivity.MainFragment;
import com.anynet.wifiworld.R;
import com.anynet.wifiworld.util.LoginHelper;
import com.anynet.wifiworld.wifi.WifiStatusReceiver.OnWifiStatusListener;

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
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class WifiFragment extends MainFragment {
	private final static String TAG = WifiFragment.class.getSimpleName();
	
	static final int WIFI_CONNECT_CONFIRM = 1;
	
	private WifiAdmin mWifiAdmin;
	private ListView mWifiListView;
	private WifiListAdapter mWifiListAdapter;
	private List<WifiInfoScanned> mWifiFree = new ArrayList<WifiInfoScanned>();
	private List<WifiInfoScanned> mWifiEncrypt = new ArrayList<WifiInfoScanned>();
	private WifiListHelper mWifiListHelper;
	
	private TextView mWifiNameView;
	private Button mOpenWifiBtn;
	private ToggleButton mWifiSwitch;
	private LinearLayout mWifiSquareLayout;
	private PopupWindow mPopupWindow;
	
	private boolean mBroadcastRegistered;
	
	private WifiInfo mWifiInfo = null;
	private WifiInfoScanned mWifiItemClick;

	private Handler mHandler = new Handler() {
		
				@Override
				public void handleMessage(Message msg) {
					Log.i(TAG, "handle wifi list helper message");
					int value = msg.what;
					if (value == ((MainActivity)getActivity()).UPDATE_WIFI_LIST) {
						mWifiFree = mWifiListHelper.getWifiFrees();
						mWifiEncrypt = mWifiListHelper.getWifiEncrypts();
						if (mWifiListAdapter != null) {
							mWifiListAdapter.refreshWifiList(mWifiFree, mWifiEncrypt);
						}
						displayWifiSquare();
					}
					super.handleMessage(msg);
				}
				
			};
	
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
				public void onWifiStatChanged(boolean isEnabled) {
					if (isEnabled) {
						mPageRoot.findViewById(R.id.wifi_disable_layout).setVisibility(View.INVISIBLE);
						mPageRoot.findViewById(R.id.wifi_enable_layout).setVisibility(View.VISIBLE);
						if (mWifiListAdapter != null) {
							Log.i(TAG, "start scan wifi list");
							boolean success = mWifiListHelper.fillWifiList();
							Log.i(TAG, "start scan wifi list:"+success);
//							mWifiFree = mWifiListHelper.getWifiFrees();
//							mWifiEncrypt = mWifiListHelper.getWifiEncrypts();
//							Log.i(TAG, "start scan wifi list:"+mWifiFree.size() + " " + mWifiEncrypt.size());
//							updateWifiConMore(mWifiNameView);
//							mWifiListAdapter.refreshWifiList(mWifiFree, mWifiEncrypt);
						}
					} else {
						mPageRoot.findViewById(R.id.wifi_disable_layout).setVisibility(View.VISIBLE);
						mPageRoot.findViewById(R.id.wifi_enable_layout).setVisibility(View.INVISIBLE);
					}
					
				}

				@Override
				public void onNetWorkChanged(String str) {
					mWifiNameView.setText(str);
					mWifiNameView.setTextColor(Color.BLACK);
					mWifiListHelper.fillWifiList();
				}

				@Override
				public void onSupplicantChanged(String str) {
					mWifiNameView.setText(str);
					mWifiNameView.setTextColor(Color.BLACK);
					
				}
			});
		}
	};
	
	private void bingdingTitleUI() {
		mTitlebar.ivHeaderLeft.setVisibility(View.INVISIBLE);
		mTitlebar.llFinish.setVisibility(View.VISIBLE);
		//mTitlebar.llHeaderMy.setVisibility(View.INVISIBLE);
		mTitlebar.tvHeaderRight.setVisibility(View.INVISIBLE);
		// mTitlebar.llFinish.setOnClickListener(this);
		mTitlebar.tvTitle.setText(getString(R.string.connect));
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mWifiListHelper = WifiListHelper.getInstance(getActivity(), mHandler);
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
		if (mWifiAdmin.isWifiEnabled()) {
			mPageRoot.findViewById(R.id.wifi_disable_layout).setVisibility(View.INVISIBLE);
			mPageRoot.findViewById(R.id.wifi_enable_layout).setVisibility(View.VISIBLE);
		} else {
			mPageRoot.findViewById(R.id.wifi_disable_layout).setVisibility(View.VISIBLE);
			mPageRoot.findViewById(R.id.wifi_enable_layout).setVisibility(View.INVISIBLE);
		}
		
		//handle WIFI square view
		mWifiSquareLayout = (LinearLayout) mPageRoot.findViewById(R.id.wifi_square);
		//initial WIFI square pop-up view
		initWifiSquarePopupView();
		//set click listener for WIFI square view
		setWifiSquareListener(mWifiSquareLayout);
		//display WIFI SSID which is connected or not
		mWifiNameView = (TextView) mPageRoot.findViewById(R.id.wifi_name);
//		mWifiListHelper.fillWifiList();
//		mWifiFree = mWifiListHelper.getWifiFrees();
//		mWifiEncrypt = mWifiListHelper.getWifiEncrypts();
//		updateWifiConMore(mWifiNameView);
		
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
					mWifiItemClick = mWifiFree.get(position - 1);
					Intent intent = new Intent("com.farproc.wifi.connecter.action.CONNECT_WIFI_CONFIRM");
					Bundle bundle = new Bundle();
					bundle.putSerializable("WifiSelected", mWifiItemClick);
					intent.putExtras(bundle);
					startActivityForResult(intent, WIFI_CONNECT_CONFIRM);
				}
			}
		});
		
		mOpenWifiBtn = (Button) mPageRoot.findViewById(R.id.open_wifi_btn);
		mOpenWifiBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				mWifiAdmin.openWifi();
			}
		});
		
		mWifiSwitch = (ToggleButton) mPageRoot.findViewById(R.id.wifi_control_toggle);
		mWifiSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				if (!isChecked) {
					WifiInfo wifiInfo = mWifiAdmin.getWifiConnection();
					mWifiAdmin.disConnectionWifi(wifiInfo.getNetworkId());
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
			WifiStatusReceiver.stopWifiService(getActivity());
//			mWifiFree = mWifiListHelper.getWifiFrees();
//			mWifiEncrypt = mWifiListHelper.getWifiEncrypts();
//			updateWifiConMore(mWifiNameView);
//			mWifiListAdapter.refreshWifiList(mWifiFree, mWifiEncrypt);
		} else if (requestCode == WIFI_CONNECT_CONFIRM && resultCode != android.app.Activity.RESULT_CANCELED) {
			if (mWifiItemClick != null) {
				Toast.makeText(getActivity(), "Failed to connect to " + mWifiItemClick.getWifiName(), Toast.LENGTH_LONG).show();
			}
			WifiStatusReceiver.stopWifiService(getActivity());
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
//		if (mWifiListAdapter != null) {
			mWifiListHelper.fillWifiList();
//			mWifiFree = mWifiListHelper.getWifiFrees();
//			mWifiEncrypt = mWifiListHelper.getWifiEncrypts();
//			updateWifiConMore(mWifiNameView);
//			mWifiListAdapter.refreshWifiList(mWifiFree, mWifiEncrypt);
//		}
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
	
	private void displayWifiSquare() {
		WifiInfo wifiConnected = mWifiAdmin.getWifiConnection();
		if (!wifiConnected.getSSID().equals("")) {
			mWifiSwitch.setVisibility(View.VISIBLE);
			mWifiSwitch.setChecked(true);
			
			mWifiSquareLayout.setVisibility(View.VISIBLE);
			if (mWifiInfo == null || !mWifiInfo.getMacAddress().equals(wifiConnected.getMacAddress())) {
				WifiHandleDB.getInstance(getActivity()).updateWifiDynamic(wifiConnected);
			}
		} else {
			mWifiSwitch.setVisibility(View.GONE);
			mWifiSwitch.setChecked(false);
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
