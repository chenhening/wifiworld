package com.anynet.wifiworld.wifi;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.anynet.wifiworld.MainActivity;
import com.anynet.wifiworld.MainActivity.MainFragment;
import com.anynet.wifiworld.R;
import com.anynet.wifiworld.data.DataCallback;
import com.anynet.wifiworld.data.WifiProfile;
import com.anynet.wifiworld.data.WifiQuestions;
import com.anynet.wifiworld.knock.KnockStepFirstActivity;
import com.anynet.wifiworld.me.WifiProviderRigisterFirstActivity;
import com.anynet.wifiworld.me.WifiProviderRigisterLicenseActivity;
import com.anynet.wifiworld.util.LoginHelper;
import com.anynet.wifiworld.wifi.WifiBRService.OnWifiStatusListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class WifiFragment extends MainFragment {
	private final static String TAG = WifiFragment.class.getSimpleName();
	
	static final int WIFI_CONNECT_CONFIRM = 1;
	
	private WifiAdmin mWifiAdmin;
	private PullToRefreshListView mWifiListView;
	private WifiListAdapter mWifiListAdapter;
	private List<WifiInfoScanned> mWifiAuth = new ArrayList<WifiInfoScanned>();
	private List<WifiInfoScanned> mWifiFree = new ArrayList<WifiInfoScanned>();
	private List<WifiInfoScanned> mWifiEncrypt = new ArrayList<WifiInfoScanned>();
	private WifiListHelper mWifiListHelper;
	private WifiBRService mWifiBRService;
	
	private TextView mWifiNameView;
	private Button mOpenWifiBtn;
	private ToggleButton mWifiSwitch;
	private LinearLayout mWifiSquareLayout;
	private PopupWindow mWifiSquarePopup;
	private LinearLayout mWifiSpeedLayout;
	private LinearLayout mWifiShareLayout;
	private LinearLayout mWifiLouderLayout;
	
	private WifiConnectDialog mWifiConnectDialog;
	
	private boolean mSupplicantBRRegisterd;
	
	private WifiInfo mLastWifiInfo = null;
	private WifiInfoScanned mLastWifiInfoScanned = null;
	private WifiInfoScanned mWifiItemClick;

	private Handler mHandler = new Handler() {
		
		@Override
		public void handleMessage(Message msg) {
			Log.i(TAG, "handle wifi list helper message");
			int value = msg.what;
			if (value == MainActivity.UPDATE_WIFI_LIST) {
				mWifiAuth = mWifiListHelper.getWifiAuths();
				mWifiFree = mWifiListHelper.getWifiFrees();
				mWifiEncrypt = mWifiListHelper.getWifiEncrypts();
				if (mWifiListAdapter != null) {
					mWifiListAdapter.refreshWifiList(mWifiAuth, mWifiFree, mWifiEncrypt);
				}
			}
			mWifiListView.onRefreshComplete();
		}
		
	};
	
	private WifiBRService.WifiMonitorService mWifiMonitorService;
	ServiceConnection conn = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mWifiMonitorService = ((WifiBRService.WifiMonitorService.WifiStatusBinder)service).getService();
			mWifiMonitorService.setOnWifiStatusListener(new OnWifiStatusListener() {

				@Override
				public void onWifiStatChanged(boolean isEnabled) {
					if (isEnabled) {
						mPageRoot.findViewById(R.id.wifi_disable_layout).setVisibility(View.INVISIBLE);
						mPageRoot.findViewById(R.id.wifi_enable_layout).setVisibility(View.VISIBLE);
						mWifiBRService.setWifiScannable(true);
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
					displayWifiSquare();
					
					//一旦网络连接上后停止监听服务
					if (mSupplicantBRRegisterd) {
						getActivity().unregisterReceiver(WifiBroadcastReceiver.getInstance(getActivity(), mWifiNameView).mSupplicantReceiver);
						mSupplicantBRRegisterd = false;
					}
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
		mWifiBRService = WifiBRService.getInstance(getActivity(), mWifiListHelper);
		//WifiStatusReceiver.schedule(getActivity());
		mWifiBRService.bindWifiService(getActivity(), conn);
		mSupplicantBRRegisterd = false;
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(LoginHelper.LOGIN_SUCCESS);
		getActivity().registerReceiver(mLoginReceiver, filter);
		
		mWifiConnectDialog = new WifiConnectDialog(getActivity());
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
		setWifiSquareListener(mWifiSquareLayout);
		//initial WIFI square pop-up view
		initWifiSquarePopupView();
		//display WIFI SSID which is connected or not
		mWifiNameView = (TextView) mPageRoot.findViewById(R.id.wifi_name);
		String connected_name = mWifiAdmin.getWifiNameConnection();
		if (!connected_name.equals("") && !connected_name.equals("0x")) {
			mWifiNameView.setText("已连接"	+ WifiAdmin.convertToNonQuotedString(mWifiAdmin.getWifiNameConnection()));
			mWifiNameView.setTextColor(Color.BLACK);
		}
		
		//WIFI list view display and operation
		mWifiListView = (PullToRefreshListView) mPageRoot.findViewById(R.id.wifi_list_view);
		mWifiListAdapter = new WifiListAdapter(this.getActivity(), mWifiAuth, mWifiFree, mWifiEncrypt);
		mWifiListView.setAdapter(mWifiListAdapter);
		mWifiListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				int index_auth = (mWifiAuth.size() + 2);
				if (position < index_auth) {
					mWifiItemClick = mWifiAuth.get(position - 2);
					//判断是否敲门，没有敲门提醒其去敲门
					if (LoginHelper.getInstance(getApplicationContext()).mKnockList.contains(mWifiItemClick.getWifiMAC())) {
						showWifiConnectConfirmDialog(mWifiItemClick, true);
					} else {
						//弹出询问对话框
						new AlertDialog.Builder(getActivity())
						.setTitle("Wi-Fi敲门").setMessage("当前Wi-Fi的门没有敲开，是否去敲门？")  
						.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
		
							@Override
							public void onClick(DialogInterface dialog, int which) {
								//拉取敲门问题
								WifiQuestions wifiQuestions = new WifiQuestions();
								wifiQuestions.QueryByMacAddress(getApplicationContext(), mWifiItemClick.getWifiMAC(), new DataCallback<WifiQuestions>() {
									
									@Override
									public void onSuccess(WifiQuestions object) {
										Intent i = new Intent(getApplicationContext(), KnockStepFirstActivity.class);
										i.putExtra("whoami", "WifiDetailsActivity");
										i.putExtra("data", object);
										startActivity(i);
									}
									
									@Override
									public void onFailed(String msg) {
										showToast("获取敲门信息失败，请稍后重试:" + msg);
									}
								});
							}					
						})  
						.setNegativeButton("取消", null)
						.show();
						
					}
				} else if (position < (index_auth + mWifiFree.size() + 1)) {
					mWifiItemClick = mWifiFree.get(position - 1 - index_auth);
					showWifiConnectConfirmDialog(mWifiItemClick, false);
				}
			}
		});
		mWifiListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				mWifiListHelper.fillWifiList();
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
		
		mWifiListHelper.fillWifiList();
		displayWifiSquare();
		
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
		} else if (requestCode == WIFI_CONNECT_CONFIRM && resultCode != android.app.Activity.RESULT_CANCELED) {
			if (mWifiItemClick != null) {
				Toast.makeText(getActivity(), "Failed to connect to " + mWifiItemClick.getWifiName(), Toast.LENGTH_LONG).show();
			}
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
//			mWifiListHelper.fillWifiList();
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
	
	@Override
	public boolean onBackPressed() {
		if (mWifiSquarePopup.isShowing()) {
			mWifiSquarePopup.dismiss();
			return true;
		} else {
			return super.onBackPressed();
		}
	}

	private void showWifiConnectConfirmDialog(final WifiInfoScanned wifiInfoScanned, boolean beAuth) {
		mWifiConnectDialog.setTitle("连接到：" + wifiInfoScanned.getWifiName());
		mWifiConnectDialog.setSignal(String.valueOf(wifiInfoScanned.getWifiStrength()));
		if (beAuth) {
			mWifiConnectDialog.setSecurity("该WiFi已经认证，请放心愉快的使用！");
		} else {
			mWifiConnectDialog.setSecurity("该WiFi未经认证，请谨慎使用！");
		}
		
		mWifiConnectDialog.setLeftBtnStr("取消");
		mWifiConnectDialog.setRightBtnStr("确定");
		
		mWifiConnectDialog.setRightBtnListener(new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				final IntentFilter filter = new IntentFilter(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
				getActivity().registerReceiver(WifiBroadcastReceiver.getInstance(getActivity(), mWifiNameView).mSupplicantReceiver, filter);
				mSupplicantBRRegisterd = true;
				
				boolean connResult = false;
				WifiConfiguration cfgSelected = mWifiAdmin.getWifiConfiguration(wifiInfoScanned);
				if (cfgSelected != null) {
					connResult = mWifiAdmin.connectToConfiguredNetwork(getActivity(), mWifiAdmin.getWifiConfiguration(wifiInfoScanned), false);
					//Log.d(TAG, "reconnect saved wifi with " + wifiInfoScanned.getWifiName() + ", " + wifiInfoScanned.getWifiPwd());
				} else {
					connResult = mWifiAdmin.connectToNewNetwork(getActivity(), wifiInfoScanned);
					//Log.d(TAG, "reconnect wifi with " + wifiInfoScanned.getWifiName() + ", " + wifiInfoScanned.getWifiPwd());
				}
				dialog.dismiss();
				if (connResult) {
					mWifiListHelper.fillWifiList();
				} else {
					Toast.makeText(getActivity(), "Failed to connect to " + wifiInfoScanned.getWifiName(), Toast.LENGTH_LONG).show();
				}
			}
		});
		
		mWifiConnectDialog.setLeftBtnListener(new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				dialog.dismiss();

			}
		});

		mWifiConnectDialog.show();
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

	private BroadcastReceiver mLoginReceiver = new BroadcastReceiver() {

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
		if (wifiConnected.getNetworkId() != -1) {
			mWifiSwitch.setVisibility(View.VISIBLE);
			mWifiSwitch.setChecked(true);
			
			mWifiSquareLayout.setVisibility(View.VISIBLE);
			if (mLastWifiInfo == null || !mLastWifiInfo.getMacAddress().equals(wifiConnected.getMacAddress())) {
				WifiHandleDB.getInstance(getActivity()).updateWifiDynamic(wifiConnected);
			}
		} else {
			mWifiSwitch.setVisibility(View.GONE);
			mWifiSwitch.setChecked(false);
			mWifiSquareLayout.setVisibility(View.GONE);
		}
		
		if (mLastWifiInfoScanned != null && mLastWifiInfoScanned.isAuthWifi()) {
			mWifiAdmin.forgetNetwork(mLastWifiInfo);
		}
		
		mLastWifiInfo = wifiConnected;
		mLastWifiInfoScanned = mWifiListHelper.mWifiInfoCur;
	}
	
	private void showPopupWindow(View view, PopupWindow popupWindow) {
		Log.i(TAG, "Show Wifi Square PopupWindow");
		popupWindow.setFocusable(false);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setAnimationStyle(R.style.PopupAnimation);
		popupWindow.showAsDropDown(view);
	}
	
	private void initWifiSquarePopupView() {
		if (mWifiSquarePopup == null) {
			LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View popupView = layoutInflater.inflate(R.layout.wifi_popup_view, null); 
			
			mWifiSpeedLayout = (LinearLayout) popupView.findViewById(R.id.wifi_speed_layout);
			mWifiShareLayout = (LinearLayout) popupView.findViewById(R.id.wifi_share_layout);
			mWifiLouderLayout = (LinearLayout) popupView.findViewById(R.id.wifi_louder_layout);
			
			//create one pop-up window object
			mWifiSquarePopup = new PopupWindow(popupView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT); 
			mWifiSquarePopup.setFocusable(false);
			mWifiSquarePopup.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);  
			//mWifiSquarePopup.setSoftInputMode(LayoutParams.SOFT_INPUT_ADJUST_RESIZE);  
			//mWifiSquarePopup.showAtLocation(this, Gravity.BOTTOM, 0, 0); 
			
			//测速ui
			Button testBtn = (Button) popupView.findViewById(R.id.start_button);
			testBtn.setOnClickListener(new WifiSpeedTester(popupView));
			
			//shared ui
			TextView mTVLinkLicense = (TextView)mWifiShareLayout.findViewById(R.id.tv_link_license);
			final String sText = "认证即表明您同意我们的<br><a href=\"activity.special.scheme://127.0.0.1\">《网络宝商户服务协议》</a>";
			mTVLinkLicense.setText(Html.fromHtml(sText));
			mTVLinkLicense.setClickable(true);
			mTVLinkLicense.setMovementMethod(LinkMovementMethod.getInstance());
			mTVLinkLicense.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent i = new Intent(getApplicationContext(), WifiProviderRigisterLicenseActivity.class);
					startActivity(i);
				}
			});
			TextView mAcceptTv = (TextView) mWifiShareLayout.findViewById(R.id.certify_button);
			mAcceptTv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					//如果用户未登陆提醒其登陆
					if (!checkIsLogined()) {
						showToast("需要登录之后才能认证。");
						return;
					}
					
					//验证当前登录用户是否已经登记了wifi，目前只支持一人绑定一个wifi
					WifiProfile wifi = LoginHelper.getInstance(getApplicationContext()).mWifiProfile;
					if (wifi != null) {
						showToast("你已经绑定了一个Wi-Fi，当前支持一个账号绑定一个Wi-Fi");
						return;
					}
					
					//TODO（binfei） 验证当前wifi是否已经被人绑定了，如果绑定可以提请申诉
					Intent i = new Intent(getApplicationContext(), WifiProviderRigisterFirstActivity.class);
					startActivity(i);
				}
			});
			
			//评论ui
			final EditText comment_edit = (EditText) popupView.findViewById(R.id.wifi_input_frame);
			comment_edit.setFocusable(true);
			popupView.findViewById(R.id.tv_button_sms).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View view) {
					String message = comment_edit.getText().toString();
					if (message.length() <= 0) {
						showToast("请输入评论。");
						return;
					}
					comment_edit.setText("");
				}
			});
		}
	}
	
	private int curSquareIdx = R.id.wifi_speed;
	private OnClickListener mSquareClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.wifi_speed:
				mWifiSpeedLayout.setVisibility(View.VISIBLE);
				mWifiShareLayout.setVisibility(View.GONE);
				mWifiLouderLayout.setVisibility(View.GONE);
				break;
			case R.id.wifi_share:
				mWifiSpeedLayout.setVisibility(View.GONE);
				mWifiShareLayout.setVisibility(View.VISIBLE);
				mWifiLouderLayout.setVisibility(View.GONE);
				break;
			case R.id.wifi_louder:
				mWifiSpeedLayout.setVisibility(View.GONE);
				mWifiShareLayout.setVisibility(View.GONE);
				mWifiLouderLayout.setVisibility(View.VISIBLE);
				break;
			default:
				break;
			}
			if (!mWifiSquarePopup.isShowing()) {
				showPopupWindow(view, mWifiSquarePopup);
			} else if (curSquareIdx == view.getId()) {
				mWifiSquarePopup.dismiss();
			}
			
			curSquareIdx = view.getId();
		}
	};
	private void setWifiSquareListener(LinearLayout wifiTasteLayout) {
		TextView wifiSpeed = (TextView) wifiTasteLayout.findViewById(R.id.wifi_speed);
		wifiSpeed.setOnClickListener(mSquareClickListener);
		
		TextView wifiShare = (TextView) wifiTasteLayout.findViewById(R.id.wifi_share);
		wifiShare.setOnClickListener(mSquareClickListener);
		
		TextView wifiLouder = (TextView) wifiTasteLayout.findViewById(R.id.wifi_louder);
		wifiLouder.setOnClickListener(mSquareClickListener);
	}
	
	private void openInputMethod(final EditText editText) {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				InputMethodManager inputManager = (InputMethodManager) editText.getContext()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(editText, 0);
				
			}
		}, 200);
	}
}
