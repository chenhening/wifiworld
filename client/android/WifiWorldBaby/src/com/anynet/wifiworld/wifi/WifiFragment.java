package com.anynet.wifiworld.wifi;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.anynet.wifiworld.MainActivity.MainFragment;
import com.anynet.wifiworld.R;
import com.anynet.wifiworld.data.DataCallback;
import com.anynet.wifiworld.data.DataListenerHelper;
import com.anynet.wifiworld.data.MultiDataCallback;
import com.anynet.wifiworld.data.WifiComments;
import com.anynet.wifiworld.data.WifiProfile;
import com.anynet.wifiworld.data.WifiQuestions;
import com.anynet.wifiworld.knock.KnockStepFirstActivity;
import com.anynet.wifiworld.me.WifiProviderRigisterFirstActivity;
import com.anynet.wifiworld.me.WifiProviderRigisterLicenseActivity;
import com.anynet.wifiworld.util.LoginHelper;
import com.anynet.wifiworld.wifi.WifiBRService.OnWifiStatusListener;
import com.anynet.wifiworld.wifi.ui.WifiCommentActivity;
import com.anynet.wifiworld.wifi.ui.WifiCommentsAdapter;
import com.avos.avoscloud.LogUtil.log;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class WifiFragment extends MainFragment implements OnClickListener {
	private final static String TAG = WifiFragment.class.getSimpleName();

	public static final int WIFI_CONNECT_CONFIRM = 1;
	public static final int WIFI_COMMENT = 2;

	public final static int UPDATE_WIFI_LIST = 99;
	public final static int GET_WIFI_DETAILS = 98;

	private WifiAdmin mWifiAdmin;
	private PullToRefreshListView mWifiListView;
	private WifiListAdapter mWifiListAdapter;
	private List<WifiInfoScanned> mWifiAuth = new ArrayList<WifiInfoScanned>();
	private List<WifiInfoScanned> mWifiFree = new ArrayList<WifiInfoScanned>();
	private List<WifiInfoScanned> mWifiEncrypt = new ArrayList<WifiInfoScanned>();
	private WifiListHelper mWifiListHelper;
	private LoginHelper mLoginHelper = null;

	private TextView mWifiNameView;
	private ImageView mWifiLogoView;
	private Button mOpenWifiBtn;
	private ToggleButton mWifiSwitch;

	private LinearLayout mWifiSquareLayout;
	private View mPopupView;
	private WifiSpeedTester mWifiSpeedTester;
	private PopupWindow mWifiSquarePopup;
	private LinearLayout mWifiSpeedLayout;
	private LinearLayout mWifiShareLayout;
	private LinearLayout mWifiLouderLayout;

	private WifiConnectDialog mWifiConnectDialog;
	private WifiConnectDialog mWifiConnectPwdDialog;

	private WifiInfo mLastWifiInfo = null;
	private WifiInfoScanned mLastWifiInfoScanned = null;
	private WifiInfoScanned mWifiItemClick;

	private boolean isPwdConnect = false;

	private WifiCommentsAdapter mWifiCommentsAdapter;
	private List<String> mWifiCommentsList = new ArrayList<String>();

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			Log.i(TAG, "handle wifi list helper message");
			int value = msg.what;
			if (value == UPDATE_WIFI_LIST) {
				mWifiAuth = mWifiListHelper.getWifiAuths();
				mWifiFree = mWifiListHelper.getWifiFrees();
				mWifiEncrypt = mWifiListHelper.getWifiEncrypts();
				if (mWifiListAdapter != null) {
					mWifiListAdapter.refreshWifiList(mWifiAuth, mWifiFree, mWifiEncrypt);
				}
				refreshWifiTitleInfo();
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
			mWifiMonitorService = ((WifiBRService.WifiMonitorService.WifiStatusBinder) service).getService();
			mWifiMonitorService.setOnWifiStatusListener(new OnWifiStatusListener() {

				@Override
				public void onWifiStatChanged(boolean isEnabled) {
					if (isEnabled) {
						mPageRoot.findViewById(R.id.wifi_disable_layout).setVisibility(View.INVISIBLE);
						mPageRoot.findViewById(R.id.wifi_enable_layout).setVisibility(View.VISIBLE);
						WifiBRService.setWifiScannable(true);
					} else {
						mPageRoot.findViewById(R.id.wifi_disable_layout).setVisibility(View.VISIBLE);
						mPageRoot.findViewById(R.id.wifi_enable_layout).setVisibility(View.INVISIBLE);
					}
				}

				@Override
				public void onNetWorkChanged(boolean isConnected, String str) {
					if (isConnected) {
						// 一旦网络状态发生变化后停止监听服务
						WifiBRService.setWifiSupplicant(false);
					}
					// refresh WiFi list and WiFi title info
					mWifiListHelper.fillWifiList();

					// refreshWifiTitleInfo();
					isPwdConnect = false;

					if (isConnected) {

						WifiInfo curwifi = WifiAdmin.getInstance(getApplicationContext()).getWifiConnected();
						if (curwifi == null)
							return;
						boolean isExist = false;
						for (WifiInfoScanned item : mWifiAuth) {
							if (item.getWifiName().equals(curwifi.getSSID())) {
								isExist = true;
							}
						}// 发现wifi未认证不做数据监听
						if (!isExist)
							return;

						// 一旦打开连接wifi，如果是认证的wifi需要做监听wifi提供者实时共享子信息
						String CurMac = curwifi.getBSSID();
						WifiProfile data_listener = new WifiProfile();
						data_listener.startListenRowUpdate(getActivity(), "WifiProfile", WifiProfile.unique_key, CurMac,
								DataListenerHelper.Type.UPDATE, new DataCallback<WifiProfile>() {

									@Override
									public void onFailed(String msg) {
										Log.d(TAG, msg);
									}

									@Override
									public void onSuccess(WifiProfile object) {
										// 通知下线
										if (!object.isShared()) {
											getActivity().runOnUiThread(new Runnable() {

												@Override
												public void run() {
													showToast("对不起，此网络主人停止了网络分享，请保存数据退出网络。");
													WifiAdmin.getInstance(getApplicationContext()).disConnectionWifi(
															mWifiAdmin.getWifiConnected().getNetworkId());
												}
											});
										}
									}
								});
					}
				}

				@Override
				public void onScannableChanged() {
					mWifiListHelper.fillWifiList();
				}

				@Override
				public void onSupplicantChanged(String statusStr) {
					mWifiNameView.setText(statusStr);
					mWifiNameView.setTextColor(Color.BLACK);
					WifiInfoScanned wifiInfoCurrent = WifiListHelper.getInstance(getActivity()).mWifiInfoCur;
					if (wifiInfoCurrent != null && wifiInfoCurrent.getWifiLogo() != null) {
						mWifiLogoView.setImageBitmap(wifiInfoCurrent.getWifiLogo());
					} else {
						mWifiLogoView.setImageResource(R.drawable.icon_invalid);
					}
				}

				@Override
				public void onSupplicantDisconnected(String statusStr) {
					if (isPwdConnect) {
						Log.i(TAG, "forget network: " + mWifiItemClick.getNetworkId());
						mWifiAdmin.forgetNetwork(mWifiItemClick.getNetworkId());
						isPwdConnect = false;
						String titleStr = "密码输入错误，请重新输入密码";
						showWifiConnectPwdConfirmDialog(titleStr, mWifiItemClick, mWifiConnectPwdDialog);
					} else {
						mWifiListHelper.fillWifiList();
					}
				}
			});
		}
	};

	private void bingdingTitleUI() {
		mTitlebar.ivHeaderLeft.setVisibility(View.INVISIBLE);
		mTitlebar.llFinish.setVisibility(View.VISIBLE);
		// mTitlebar.llHeaderMy.setVisibility(View.INVISIBLE);
		mTitlebar.tvHeaderRight.setVisibility(View.INVISIBLE);
		// mTitlebar.llFinish.setOnClickListener(this);
		mTitlebar.tvTitle.setText(getString(R.string.connect));
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mWifiListHelper = WifiListHelper.getInstance(getActivity(), mHandler);
		mWifiAdmin = mWifiListHelper.getWifiAdmin();

		WifiBRService.bindWifiService(getActivity(), conn);
		mWifiConnectDialog = new WifiConnectDialog(getActivity(), false);
		mWifiConnectPwdDialog = new WifiConnectDialog(getActivity(), true);

		mLoginHelper = LoginHelper.getInstance(getApplicationContext());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		mPageRoot = inflater.inflate(R.layout.fragment_wifi, null);
		if (mWifiAdmin.isWifiEnabled()) {
			mPageRoot.findViewById(R.id.wifi_disable_layout).setVisibility(View.INVISIBLE);
			mPageRoot.findViewById(R.id.wifi_enable_layout).setVisibility(View.VISIBLE);
		} else {
			mPageRoot.findViewById(R.id.wifi_disable_layout).setVisibility(View.VISIBLE);
			mPageRoot.findViewById(R.id.wifi_enable_layout).setVisibility(View.INVISIBLE);
		}

		// handle WIFI square view
		mWifiSquareLayout = (LinearLayout) mPageRoot.findViewById(R.id.wifi_square);
		setWifiSquareListener(mWifiSquareLayout);
		// initial WIFI square pop-up view
		initWifiSquarePopupView();
		// display WIFI SSID which is connected or not
		mWifiNameView = (TextView) mPageRoot.findViewById(R.id.wifi_name);
		mWifiLogoView = (ImageView) mPageRoot.findViewById(R.id.wifi_logo);
		// String connected_name = mWifiAdmin.getWifiNameConnection();
		// if (!connected_name.equals("") && !connected_name.equals("0x")) {
		// mWifiNameView.setText("已连接" +
		// WifiAdmin.convertToNonQuotedString(mWifiAdmin.getWifiNameConnection()));
		// mWifiNameView.setTextColor(Color.BLACK);
		// }

		// WIFI list view display and operation
		mWifiListView = (PullToRefreshListView) mPageRoot.findViewById(R.id.wifi_list_view);
		mWifiListAdapter = new WifiListAdapter(this.getActivity(), mWifiAuth, mWifiFree, mWifiEncrypt);
		mWifiListView.setAdapter(mWifiListAdapter);
		mWifiListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				int index_auth = (mWifiAuth.size() + 2);
				if (position < index_auth) {
					mWifiItemClick = mWifiAuth.get(position - 2);
					// 判断是否敲门，没有敲门提醒其去敲门
					if (mLoginHelper.canAccessDirectly(mWifiItemClick.getWifiMAC()) || mLoginHelper.mKnockList.contains(mWifiItemClick.getWifiMAC())) {
						showWifiConnectConfirmDialog(mWifiItemClick, true);
					} else {
						// 弹出询问对话框
						new AlertDialog.Builder(getActivity()).setTitle("Wi-Fi敲门").setMessage("当前Wi-Fi的门没有敲开，是否去敲门？")
								.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {
										// 拉取敲门问题
										WifiQuestions wifiQuestions = new WifiQuestions();
										wifiQuestions.QueryByMacAddress(getApplicationContext(), mWifiItemClick.getWifiMAC(),
												new DataCallback<WifiQuestions>() {

													@Override
													public void onSuccess(WifiQuestions object) {
														KnockStepFirstActivity.start(WifiFragment.this.getActivity(), "WifiDetailsActivity", object);
													}

													@Override
													public void onFailed(String msg) {
														showToast("获取敲门信息失败，请稍后重试:" + msg);
													}
												});
									}
								}).setNegativeButton("取消", null).show();

					}
				} else if (position < (index_auth + mWifiFree.size() + 1)) {
					mWifiItemClick = mWifiFree.get(position - 1 - index_auth);
					showWifiConnectConfirmDialog(mWifiItemClick, false);
				} else {
					mWifiItemClick = mWifiEncrypt.get(position - 2 - mWifiFree.size() - index_auth);
					String titleStr = "连接到：" + mWifiItemClick.getWifiName();
					showWifiConnectPwdConfirmDialog(titleStr, mWifiItemClick, mWifiConnectPwdDialog);

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
					WifiInfo wifiInfo = mWifiAdmin.getWifiConnected();
					if (wifiInfo != null) {
						mWifiAdmin.disConnectionWifi(wifiInfo.getNetworkId());
					}
					refreshWifiTitleInfo();
				}
			}
		});

		mWifiListHelper.fillWifiList();
		// refreshWifiTitleInfo();

		// bind common title UI
		super.onCreateView(inflater, container, savedInstanceState);
		bingdingTitleUI();
		curSquareIdx = -1;
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

		if (requestCode == WIFI_COMMENT && resultCode == android.app.Activity.RESULT_OK) {
			String commentStr = data.getStringExtra(WifiCommentActivity.WIFI_COMMENT_ADD);
			mWifiCommentsList.add(commentStr);
			mWifiCommentsAdapter.refreshCommentsList();
		}
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onPause() {
		// if (mSupplicantBRRegisterd) {
		// getActivity().unregisterReceiver(mSupplicantReceiver);
		// }

		// getActivity().unregisterReceiver(mLoginReceiver);

		super.onPause();
	}

	@Override
	public void onResume() {
		// mSupplicantReceiver = mWifiSupplicant.mReceiver;
		//
		// final IntentFilter filter = new
		// IntentFilter(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
		// getActivity().registerReceiver(mSupplicantReceiver, filter);
		// mSupplicantBRRegisterd = true;

		IntentFilter loginFilter = new IntentFilter();
		loginFilter.addAction(LoginHelper.LOGIN_SUCCESS);
		// getActivity().registerReceiver(mLoginReceiver, loginFilter);

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

	public void setListViewHeightBasedOnChildren(ListView listView) {  
	    ListAdapter listAdapter = listView.getAdapter();   
	    if (listAdapter == null) {  
	        return;  
	    }  
	 
	    int totalHeight = 0;  
	    for (int i = 0; i < listAdapter.getCount(); i++) {  
	        View listItem = listAdapter.getView(i, null, listView);  
	        listItem.measure(0, 0);  
	        totalHeight += listItem.getMeasuredHeight();  
	    }  
	 
	    ViewGroup.LayoutParams params = listView.getLayoutParams();  
	    params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
	    listView.setLayoutParams(params);  
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
				WifiBRService.setWifiSupplicant(true);

				boolean connResult = false;
				WifiConfiguration cfgSelected = mWifiAdmin.getWifiConfiguration(wifiInfoScanned);
				if (cfgSelected != null) {
					connResult = mWifiAdmin.connectToConfiguredNetwork(getActivity(), cfgSelected, true);
					// Log.d(TAG, "reconnect saved wifi with " +
					// wifiInfoScanned.getWifiName() + ", " +
					// wifiInfoScanned.getWifiPwd());
				} else {
					connResult = mWifiAdmin.connectToNewNetwork(getActivity(), wifiInfoScanned, true, false);
					// Log.d(TAG, "reconnect wifi with " +
					// wifiInfoScanned.getWifiName() + ", " +
					// wifiInfoScanned.getWifiPwd());
				}
				dialog.dismiss();
				if (!connResult) {
					Toast.makeText(getActivity(), "不能连接到网络：" + wifiInfoScanned.getWifiName() + ", 准备重启WiFi请稍后再试。", Toast.LENGTH_LONG).show();
					mWifiAdmin.closeWifi();
					mWifiAdmin.openWifi();
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

	private void showWifiConnectPwdConfirmDialog(String title, final WifiInfoScanned wifiInfoScanned, final WifiConnectDialog wifiConnectDialog) {
		wifiConnectDialog.setTitle(title);
		wifiConnectDialog.setSignal(String.valueOf(wifiInfoScanned.getWifiStrength()));

		wifiConnectDialog.setLeftBtnStr("取消");
		wifiConnectDialog.setRightBtnStr("确定");
		wifiConnectDialog.clearPwdEditText();

		wifiConnectDialog.setRightBtnListener(new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				boolean connResult = false;
				String inputedPwd = wifiConnectDialog.getPwdContent();
				if (inputedPwd.equals("")) {
					Toast.makeText(getActivity(), "请输入密码。", Toast.LENGTH_LONG).show();
					return;
				}

				isPwdConnect = true;
				WifiBRService.setWifiSupplicant(true);
				wifiInfoScanned.setWifiPwd(inputedPwd);
				connResult = mWifiAdmin.connectToNewNetwork(getActivity(), wifiInfoScanned, true, true);
				dialog.dismiss();
				if (!connResult) {
					Toast.makeText(getActivity(), "不能连接到网络：" + wifiInfoScanned.getWifiName() + ", 正在重启WiFi请稍后再试。", Toast.LENGTH_LONG).show();
					mWifiAdmin.closeWifi();
					mWifiAdmin.openWifi();
				}
			}
		});

		wifiConnectDialog.setLeftBtnListener(new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				dialog.dismiss();

			}
		});

		wifiConnectDialog.show();
	}

	private void refreshWifiTitleInfo() {
		WifiInfo wifiCurInfo = mWifiAdmin.getWifiConnected();

		// update WiFi title info
		if (wifiCurInfo != null) {
			WifiInfoScanned wifiInfoCurrent = WifiListHelper.getInstance(getActivity()).mWifiInfoCur;
			if (wifiInfoCurrent != null && wifiInfoCurrent.getWifiLogo() != null) {
				mWifiLogoView.setImageBitmap(wifiInfoCurrent.getWifiLogo());
			} else {
				mWifiLogoView.setImageResource(R.drawable.icon_invalid);
			}
			if (wifiInfoCurrent != null && wifiInfoCurrent.getAlias() != null && wifiInfoCurrent.getAlias().length() > 0) {
				mWifiNameView.setText("已连接: " + wifiInfoCurrent.getAlias());
			} else {
				mWifiNameView.setText("已连接: " + WifiAdmin.convertToNonQuotedString(wifiCurInfo.getSSID()));
			}
			mWifiNameView.setTextColor(Color.BLACK);

			mWifiSwitch.setVisibility(View.VISIBLE);
			mWifiSwitch.setChecked(true);
			mWifiSquareLayout.setVisibility(View.VISIBLE);
		} else {
			mWifiNameView.setText("未连接任何WiFi");
			mWifiNameView.setTextColor(Color.GRAY);
			mWifiLogoView.setImageResource(R.drawable.icon_invalid);

			mWifiSwitch.setVisibility(View.GONE);
			mWifiSwitch.setChecked(false);
			mWifiSquareLayout.setVisibility(View.GONE);
		}

		// forget last WiFi connected configuration info
		if (mLastWifiInfo != null && mLastWifiInfoScanned != null && mLastWifiInfoScanned.isAuthWifi() && !mLastWifiInfoScanned.isLocalSave()) {
			Log.i(TAG, "erase WiFi config which has been shared and not local save");
			mWifiAdmin.forgetNetwork(mLastWifiInfo);
		}
		mLastWifiInfo = wifiCurInfo;
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
			mPopupView = layoutInflater.inflate(R.layout.wifi_popup_view, null);

			mWifiSpeedLayout = (LinearLayout) mPopupView.findViewById(R.id.wifi_speed_layout);
			mWifiShareLayout = (LinearLayout) mPopupView.findViewById(R.id.wifi_share_layout);
			mWifiLouderLayout = (LinearLayout) mPopupView.findViewById(R.id.wifi_louder_layout);

			// create one pop-up window object
			mWifiSquarePopup = new PopupWindow(mPopupView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			mWifiSquarePopup.setFocusable(false);
			mWifiSquarePopup.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
			// mWifiSquarePopup.setSoftInputMode(LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
			// mWifiSquarePopup.showAtLocation(this, Gravity.BOTTOM, 0, 0);

			// 测速ui
			mWifiSpeedTester = new WifiSpeedTester(mPopupView);
			Button testBtn = (Button) mPopupView.findViewById(R.id.start_button);
			testBtn.setOnClickListener(mWifiSpeedTester);
			mWifiSquarePopup.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss() {
					Log.i(TAG, "on popup window dismiss");
					mWifiSpeedTester.stopSpeedTest();
				}
			});

			// shared ui
			TextView mTVLinkLicense = (TextView) mWifiShareLayout.findViewById(R.id.tv_link_license);
			final String sText = "认证即表明您同意我们的<br><a href=\"activity.special.scheme://127.0.0.1\">《网络宝服务协议》</a>";
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
					// 如果用户未登陆提醒其登陆
					if (!checkIsLogined()) {
						showToast("需要登录之后才能认证。");
						return;
					}

					// 验证当前登录用户是否已经登记了wifi，目前只支持一人绑定一个wifi
					WifiProfile wifi = LoginHelper.getInstance(getApplicationContext()).mWifiProfile;
					if (wifi != null) {
						showToast("你已经绑定了一个Wi-Fi，当前支持一个账号绑定一个Wi-Fi");
						return;
					}

					// TODO（binfei） 验证当前wifi是否已经被人绑定了，如果绑定可以提请申诉
					Intent i = new Intent(getApplicationContext(), WifiProviderRigisterFirstActivity.class);
					startActivity(i);
				}
			});

			// 评论ui
			final TextView send_btn = (TextView) mPopupView.findViewById(R.id.tv_button_sms);
			send_btn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view) {
					// if (!LoginHelper.getInstance(getActivity()).isLogined())
					// {
					// UserLoginActivity.start((BaseActivity) getActivity());
					// return;
					// }
					Intent intent = new Intent("com.anynet.wifiworld.wifi.ui.WIFI_COMMENT");
					startActivityForResult(intent, WIFI_COMMENT);
				}
			});
		}
	}

	private void loadWifiComments(View popupView) {
		WifiInfoScanned wifiInfoScanned = WifiListHelper.getInstance(getActivity()).mWifiInfoCur;

		final ListView commentsListView = (ListView) popupView.findViewById(R.id.wifi_list_comments);
		WifiComments wifiComments = new WifiComments();
		wifiComments.QueryByMacAddress(getActivity(), wifiInfoScanned.getWifiMAC(), new MultiDataCallback<WifiComments>() {

			@Override
			public void onSuccess(List<WifiComments> objects) {
				mWifiCommentsList.clear();
				for (WifiComments obj : objects) {
					mWifiCommentsList.add(obj.Comment);
				}
				mWifiCommentsAdapter = new WifiCommentsAdapter(getActivity(), mWifiCommentsList);
				commentsListView.setAdapter(mWifiCommentsAdapter);
				setListViewHeightBasedOnChildren(commentsListView);
			}

			@Override
			public void onFailed(String msg) {
				Log.e(TAG, msg + ". Failed to pull wifi comments");
			}
		});
	}

	private void setWifiSquareListener(LinearLayout wifiTasteLayout) {
		TextView wifiSpeed = (TextView) wifiTasteLayout.findViewById(R.id.wifi_speed);
		wifiSpeed.setOnClickListener(this);

		TextView wifiShare = (TextView) wifiTasteLayout.findViewById(R.id.wifi_share);
		wifiShare.setOnClickListener(this);

		TextView wifiLouder = (TextView) wifiTasteLayout.findViewById(R.id.wifi_louder);
		wifiLouder.setOnClickListener(this);
	}

	private int curSquareIdx = -1;

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub

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
			// WiFi comments list
			loadWifiComments(mPopupView);
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
}
