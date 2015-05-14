package com.anynet.wifiworld.wifi;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.cordova.Whitelist;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.BitmapDrawable;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import cn.hugo.android.scanner.CaptureActivity;

import com.anynet.wifiworld.MainActivity.MainFragment;
import com.anynet.wifiworld.R;
import com.anynet.wifiworld.UserLoginActivity;
import com.anynet.wifiworld.app.BaseActivity;
import com.anynet.wifiworld.data.DataCallback;
import com.anynet.wifiworld.data.DataListenerHelper;
import com.anynet.wifiworld.data.MultiDataCallback;
import com.anynet.wifiworld.data.WifiBlack;
import com.anynet.wifiworld.data.WifiComments;
import com.anynet.wifiworld.data.WifiProfile;
import com.anynet.wifiworld.data.WifiQuestions;
import com.anynet.wifiworld.data.WifiWhite;
import com.anynet.wifiworld.knock.KnockStepFirstActivity;
import com.anynet.wifiworld.util.LoginHelper;
import com.anynet.wifiworld.wifi.WifiBRService.OnWifiStatusListener;
import com.anynet.wifiworld.wifi.ui.WifiCommentActivity;
import com.anynet.wifiworld.wifi.ui.WifiSquarePopup;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class WifiFragment extends MainFragment {
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
	//private ToggleButton mWifiSwitch;
	private TextView mWifiMaster;
	private TextView mWifiDesc;

	private LinearLayout mWifiTitleLayout;
	
	private LinearLayout mWifiSquareLayout;
	private WifiSquarePopup mWifiSquarePopup;
	private int mLastSquareId = -1;

	private WifiConnectDialog mWifiConnectDialog;
	private WifiConnectDialog mWifiConnectPwdDialog;

	private WifiInfo mLastWifiInfo = null;
	private WifiInfoScanned mLastWifiInfoScanned = null;
	private WifiInfoScanned mWifiItemClick;
	
	private List<WifiWhite> mWifiWhite = null;
	private List<WifiBlack> mWifiBlack = null;

	private boolean isPwdConnect = false;
	public PopupWindow popupwindow;

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
				refreshWifiTitleInfo(true);
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
						if (isPwdConnect) {
							mWifiAdmin.saveConfig();
							isPwdConnect = false;
						}
					}
					// refresh WiFi list and WiFi title info
					mWifiListHelper.fillWifiList();

					// refreshWifiTitleInfo();
					//isPwdConnect = false;

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
						if (mWifiItemClick.getNetworkId() != -1) {
							mWifiAdmin.forgetNetwork(mWifiItemClick.getNetworkId());
						}
						mWifiListHelper.fillWifiList();
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

	private OnClickListener mWifiSquareClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.wifi_speed:
				mWifiSquarePopup.displayLayout(WifiSquarePopup.SquareType.SPEED_TESTER);
				break;
			case R.id.wifi_share:
				mWifiSquarePopup.displayLayout(WifiSquarePopup.SquareType.SHARE_PAGE);
				break;
			case R.id.wifi_louder:
				mWifiSquarePopup.displayLayout(WifiSquarePopup.SquareType.COMMENTS_PAGE);
				break;
			default:
				break;
			}
			if (!mWifiSquarePopup.isShowing()) {
				mWifiSquarePopup.show(view);
			} else if (mLastSquareId == view.getId()) {
				mWifiSquarePopup.dismiss();
			}

			mLastSquareId = view.getId();
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
	protected void onVisible() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onInvisible() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mWifiListHelper = WifiListHelper.getInstance(getActivity(), mHandler);
		mWifiAdmin = mWifiListHelper.getWifiAdmin();

		WifiBRService.bindWifiService(getActivity(), conn);
		mWifiConnectDialog = new WifiConnectDialog(getActivity(), WifiConnectDialog.DialogType.DEFAULT);
		mWifiConnectPwdDialog = new WifiConnectDialog(getActivity(), WifiConnectDialog.DialogType.PASSWORD);

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

		mWifiTitleLayout = (LinearLayout) mPageRoot.findViewById(R.id.wifi_connected_info);
		mWifiTitleLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent("com.anynet.wifiworld.wifi.ui.WIFI_ADVANCE");
				getActivity().startActivity(intent);
				getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.hold);
			}
		});
		// handle WIFI square view
		mWifiSquareLayout = (LinearLayout) mPageRoot.findViewById(R.id.wifi_square);
		setWifiSquareListener(mWifiSquareLayout);
		// initial WIFI square pop-up view
		mWifiSquarePopup = new WifiSquarePopup(getActivity(), this);
		// display WIFI SSID which is connected or not
		mWifiNameView = (TextView) mPageRoot.findViewById(R.id.tv_wifi_name);
		mWifiLogoView = (ImageView) mPageRoot.findViewById(R.id.wifi_logo);
		mWifiMaster = (TextView) mPageRoot.findViewById(R.id.tv_wifi_master_v);
		mWifiDesc = (TextView) mPageRoot.findViewById(R.id.tv_wifi_desc_v);

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
					List<WifiWhite> whiteList = mWifiItemClick.getWifiWhites();
					final String userId = mLoginHelper.getUserid();
					if (whiteList == null) {
						Toast.makeText(getActivity(), "Failed to pull white list, pls try again later", Toast.LENGTH_LONG).show();
					} else if (mLoginHelper.canAccessDirectly(mWifiItemClick.getWifiMAC()) || mLoginHelper.mKnockList.contains(mWifiItemClick.getWifiMAC())
							|| (whiteList.size() > 0 && userId != null && isContainUserId(userId, whiteList))) {
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

		//mWifiSwitch = (ToggleButton) mPageRoot.findViewById(R.id.wifi_control_toggle);
		//mWifiSwitch.setOnClickListener(new OnClickListener() {

		//	@Override
		//	public void onClick(View v) {
		//		WifiInfo wifiInfo = mWifiAdmin.getWifiConnected();
		//		if (wifiInfo != null) {
		//			mWifiAdmin.disConnectionWifi(wifiInfo.getNetworkId());
		//		}
		//		refreshWifiTitleInfo(false);
		//	}
		//});
		
		//设置wifi弹出式下拉菜单
		initMorePopWindows();
		this.findViewById(R.id.iv_wifi_more).setOnClickListener(new OnClickListener() {

			@Override
            public void onClick(View v) {
				if (popupwindow != null&&popupwindow.isShowing()) {
	                popupwindow.dismiss();
	                return;
	            } else {
	                popupwindow.showAsDropDown(v, 0, 5);
	            }
            }
			
		});

		mWifiListHelper.fillWifiList();
		// refreshWifiTitleInfo();

		// bind common title UI
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

		if (requestCode == WIFI_COMMENT && resultCode == android.app.Activity.RESULT_OK) {
			WifiComments commentCtn = (WifiComments) data.getSerializableExtra(WifiCommentActivity.WIFI_COMMENT_ADD);
			mWifiSquarePopup.addComment(commentCtn);
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
		super.onPause();
	}

	@Override
	public void onResume() {
		IntentFilter loginFilter = new IntentFilter();
		loginFilter.addAction(LoginHelper.LOGIN_SUCCESS);
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

	private boolean isContainUserId(String userId, List<WifiWhite> wifiWhites) {
		for (WifiWhite wifiWhite : wifiWhites) {
			if (wifiWhite.Whiteid.equals(userId)) {
				return true;
			}
		}
		
		return false;
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

				isPwdConnect = false;
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
				connResult = mWifiAdmin.connectToNewNetwork(getActivity(), wifiInfoScanned, true, false);
				//shutdown soft keyboard if soft keyboard is actived
				InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
				if (imm.isActive()) {
					imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
				}
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

	private void refreshWifiTitleInfo(boolean open) {
		WifiInfo wifiCurInfo = mWifiAdmin.getWifiConnected();

		mWifiMaster.setText("");
		mWifiDesc.setText("");
		// update WiFi title info
		if (open && wifiCurInfo != null) {
			WifiInfoScanned wifiInfoCurrent = WifiListHelper.getInstance(getActivity()).mWifiInfoCur;
			if (wifiInfoCurrent != null && wifiInfoCurrent.getWifiLogo() != null) {
				mWifiLogoView.setImageBitmap(wifiInfoCurrent.getWifiLogo());
			} else {
				mWifiLogoView.setImageResource(R.drawable.icon_invalid);
			}
			if (wifiInfoCurrent != null && wifiInfoCurrent.getAlias() != null && wifiInfoCurrent.getAlias().length() > 0) {
				mWifiNameView.setText("已连接: " + wifiInfoCurrent.getAlias());
				mWifiMaster.setText(wifiInfoCurrent.getSponser());
				mWifiDesc.setText(wifiInfoCurrent.getBanner());
			} else {
				mWifiNameView.setText("已连接: " + WifiAdmin.convertToNonQuotedString(wifiCurInfo.getSSID()));
			}
			// mWifiNameView.setTextColor(Color.BLACK);

			//mWifiSwitch.setVisibility(View.VISIBLE);
			//mWifiSwitch.setChecked(true);
			mWifiSquareLayout.setVisibility(View.VISIBLE);
			mWifiTitleLayout.setClickable(true);
		} else {
			mWifiNameView.setText("未连接任何WiFi");
			// mWifiNameView.setTextColor(Color.GRAY);
			mWifiLogoView.setImageResource(R.drawable.icon_invalid);

			//mWifiSwitch.setVisibility(View.GONE);
			//mWifiSwitch.setChecked(false);
			mWifiSquareLayout.setVisibility(View.GONE);
			mWifiTitleLayout.setClickable(false);
		}

		// forget last WiFi connected configuration info
		if (mLastWifiInfo != null && mLastWifiInfoScanned != null && mLastWifiInfoScanned.isAuthWifi() && !mLastWifiInfoScanned.isLocalSave()) {
			Log.i(TAG, "erase WiFi config which has been shared and not local save");
			mWifiAdmin.forgetNetwork(mLastWifiInfo);
		}
		mLastWifiInfo = wifiCurInfo;
		mLastWifiInfoScanned = mWifiListHelper.mWifiInfoCur;
	}

	private void setWifiSquareListener(LinearLayout wifiSquareLayout) {
		TextView wifiSpeed = (TextView) wifiSquareLayout.findViewById(R.id.wifi_speed);
		wifiSpeed.setOnClickListener(mWifiSquareClickListener);

		TextView wifiShare = (TextView) wifiSquareLayout.findViewById(R.id.wifi_share);
		wifiShare.setOnClickListener(mWifiSquareClickListener);

		TextView wifiLouder = (TextView) wifiSquareLayout.findViewById(R.id.wifi_louder);
		wifiLouder.setOnClickListener(mWifiSquareClickListener);
	}
	
	private void initMorePopWindows() {
        if (popupwindow == null) {
        		// 获取自定义布局文件pop.xml的视图  
    			LayoutInflater layoutInflater = (LayoutInflater)this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    			View customView = layoutInflater.inflate(R.layout.popupwindow_more, null, false);  
    			//创建PopupWindow实例,200,150分别是宽度和高度  
        		popupwindow = new PopupWindow(customView, 500, 280);
        		popupwindow.setTouchable(true);
        		popupwindow.setFocusable(true);
        		popupwindow.setBackgroundDrawable(new BitmapDrawable());
        		popupwindow.setAnimationStyle(R.style.PopupFadeAnimation);
        		popupwindow.setOutsideTouchable(true);
        		
        		customView.findViewById(R.id.ll_switch).setOnClickListener(new OnClickListener() {

        			@Override
                public void onClick(View v) {
        				WifiInfo wifiInfo = mWifiAdmin.getWifiConnected();
        				if (wifiInfo != null) {
        					mWifiAdmin.disConnectionWifi(wifiInfo.getNetworkId());
        				}
        				refreshWifiTitleInfo(false);
        				popupwindow.dismiss();
                }
                	
            });
                
            //扫一扫
            customView.findViewById(R.id.ll_scan).setOnClickListener(new OnClickListener() {

        			@Override
                public void onClick(View v) {
        				Intent i = new Intent();
        				i.setClass(getActivity(), CaptureActivity.class);
        				startActivity(i);
        				popupwindow.dismiss();
                }
                	
            });
		}
        // 设置动画效果 [R.style.AnimationFade 是自己事先定义好的]
        //popupwindow.setAnimationStyle(R.style.PopupAnimation);
        //先设置3s内自动退出
//        customView.postDelayed(new Runnable() {
//
//			@Override
//			public void run() {
//				if (popupwindow != null && popupwindow.isShowing()) {  
//                    popupwindow.dismiss();  
//                    popupwindow = null;  
//                }  
//			}
//        	
//        }, 3000);
        // 自定义view添加触摸事件  
//        customView.setOnTouchListener(new OnTouchListener() {  
//  
//			@Override
//            public boolean onTouch(View v, MotionEvent event) {
//				if (popupwindow != null && popupwindow.isShowing()) {  
//                    popupwindow.dismiss();  
//                    popupwindow = null;  
//                }  
//  
//                return false;
//            }  
//        });
	}
	
	
}
