package com.anynet.wifiworld.wifi.ui;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.dialog.WifiConnectDialog;
import com.anynet.wifiworld.dialog.WifiConnectDialog.DialogType;
import com.anynet.wifiworld.util.GlobalHandler;
import com.anynet.wifiworld.util.UIHelper;
import com.anynet.wifiworld.wifi.WifiAdmin;
import com.anynet.wifiworld.wifi.WifiBRService;
import com.anynet.wifiworld.wifi.WifiBRService.OnWifiStatusListener;
import com.anynet.wifiworld.wifi.WifiCurrent;
import com.anynet.wifiworld.wifi.WifiListItem;
import com.anynet.wifiworld.wifi.WifiListScanned;

public class WifiConnectUI {
	private final static String TAG = WifiConnectUI.class.getSimpleName();
	
	private View mView;
	private WifiAdmin mWifiAdmin;
	private WifiCurrent mWifiCurrent;
	private WifiListScanned mWifiListScanned;
	private WifiAuthListAdapter mWifiAuthList;
	private WifiNotAuthListAdapter mWifiNotAuthList;
	
	private ImageView mWifiConLogo;
	private AnimationDrawable mAnimWifiCon;
	private boolean mIsWifiConnecting;
	private TextView mWifiName;
	private TextView mWifiStatus;
	private TextView mWifiAlias;
	private TextView mWifiAuthDesc;
	private ListView mWifiAuthListView;
	private ListView mWifiNotAuthListView;
	
	private ToggleButton mWifiSwitch;
	private AnimationDrawable mAnimSearch;
	private Animation mAnimNeedle;
	private ImageView mImageSearch;
	private ImageView mImageNeedle;
	
	private GlobalHandler wifiListHandler = new GlobalHandler() {
		
		@Override
		public void onWifiListRefreshed() {
			setWifiConnectedContent();
			if (mWifiAuthList != null) {
				mWifiAuthList.refreshWifiList(mWifiListScanned.getAuthList());
				UIHelper.setListViewHeightBasedOnChildren(mWifiAuthListView);
			}
			if (mWifiNotAuthList != null) {
				mWifiNotAuthList.refreshWifiList(mWifiListScanned.getNotAuthList());
				UIHelper.setListViewHeightBasedOnChildren(mWifiNotAuthListView);
			}
			
			//停止搜索动画
			doSearchAnimation(false);
		}
	};
	
	ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
		}
	};
	
	private OnWifiStatusListener mWifiStatusListener = new OnWifiStatusListener() {
		@Override
		public void onNetWorkConnected(String str) {
			mWifiStatus.setText(str);
			mWifiListScanned.refresh();
			mIsWifiConnecting = false;
			doConnectingAnimation(mIsWifiConnecting);
		}
		
		@Override
		public void onNetWorkDisconnected(String str) {
			mWifiStatus.setText(str);
			mWifiListScanned.refresh();
			mIsWifiConnecting = false;
			doConnectingAnimation(mIsWifiConnecting);
		}

		@Override
		public void onSupplicantChanged(String statusStr) {
			mWifiStatus.setText(statusStr);
			//启动连接动画
			mIsWifiConnecting = true;
			doConnectingAnimation(mIsWifiConnecting);
		}

		@Override
		public void onSupplicantDisconnected(String statusStr) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onScannableAvaliable() {
			//仍需要做修改
			mWifiListScanned.refresh();
		}
		
		@Override
		public void onWifiStatChanged(boolean isEnabled) {
		}
	};
	
	private OnItemClickListener mAuthItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			WifiListItem wifiListItem = mWifiListScanned.getAuthList().get(position);
			if (wifiListItem.isAuthWifi()) {
				showWifiConnectDialog(wifiListItem, WifiConnectDialog.DialogType.DEFAULT);
			} else {
				//TODO(binfei)显示认证说明简介
			}
		}
	};
	
	private OnItemClickListener mNotAuthItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			WifiListItem wifiListItem = mWifiListScanned.getNotAuthList().get(position);
			if (wifiListItem.isLocalWifi() || wifiListItem.isOpenWifi()) {
				showWifiConnectDialog(wifiListItem, WifiConnectDialog.DialogType.DEFAULT);
			} else {
				showWifiConnectDialog(wifiListItem, WifiConnectDialog.DialogType.PASSWORD);
			}
		}
	};
	
	public WifiConnectUI(View view) {
		mView = view;
		//mWifiAdmin = WifiAdmin.getInstance(mView.getContext());
		mWifiCurrent = WifiCurrent.getInstance(mView.getContext());
		mWifiListScanned = WifiListScanned.getInstance(mView.getContext(), wifiListHandler);
		WifiBRService.setOnWifiStatusListener(mWifiStatusListener);
		WifiBRService.bindWifiService(mView.getContext(), conn);
		getViewHolder();
		
		//启动动画，更新WiFi扫描列表
		doSearchAnimation(true);
		mWifiListScanned.refresh();
	}
	
	private void setWifiConnectedContent() {
		if (mWifiCurrent.isConnected()) {
			WifiListItem item = mWifiCurrent.getWifiListItem();
			if (item != null && item.isAuthWifi()) { //如果是认证，显示认证信息
				mWifiName.setText(item.getAlias());
				mWifiAlias.setVisibility(View.VISIBLE);
				mWifiAlias.setText("[" + mWifiCurrent.getWifiName() + "]");
				mWifiAuthDesc.setVisibility(View.VISIBLE);
				mWifiAuthDesc.setText(item.getBanner());
				Bitmap logo = item.getLogo();
				if (logo != null) {
					mWifiConLogo.setImageBitmap(logo);
				} else {
					mWifiConLogo.setImageResource(mWifiCurrent.getDefaultLogoID());
				}
			} else { //如果非认证显示默认信息
				mWifiAlias.setVisibility(View.INVISIBLE);
				mWifiName.setText(mWifiCurrent.getWifiName());
				mWifiConLogo.setImageResource(mWifiCurrent.getDefaultLogoID());
				mWifiAuthDesc.setText("[未认证]");
			}
		} else {
			mWifiAlias.setVisibility(View.INVISIBLE);
			mWifiName.setText("未连接WiFi");
			if (!mIsWifiConnecting)
				mWifiConLogo.setImageResource(R.drawable.ic_wifi_disconnected);
			else
				WifiBRService.setWifiSupplicant(true);
			mWifiAuthDesc.setText("[未认证]");
		}
	}
	
	private void getViewHolder() {
		//断开连接
		mWifiSwitch = (ToggleButton)mView.findViewById(R.id.tb_wifi_switch);
		mWifiSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				WifiInfo wifiInfo = mWifiAdmin.getWifiInfo();
				if (wifiInfo != null) {
					mWifiAdmin.disConnectionWifi(wifiInfo.getNetworkId());
				}
			}
		    	
		});
		
		//点击搜索附近WiFi
		mImageNeedle = (ImageView)mView.findViewById(R.id.iv_wifi_search_needle);
		mAnimNeedle = AnimationUtils.loadAnimation(mView.getContext(), R.animator.animation_needle);
		mAnimNeedle.setInterpolator(new BounceInterpolator());
		mImageSearch = (ImageView)mView.findViewById(R.id.iv_wifi_search_heart);
		mImageSearch.setImageResource(R.animator.animation_search);
		mAnimSearch = (AnimationDrawable)mImageSearch.getDrawable();
		mView.findViewById(R.id.rl_wifi_search).setOnClickListener(new OnClickListener() {
		
			@Override
			public void onClick(View v) {
				if (!mAnimSearch.isRunning()) {
					doSearchAnimation(true);
					mWifiListScanned.refresh();
				} else {
					doSearchAnimation(false);
				}
			}
			
		});
		
		mWifiConLogo = (ImageView)mView.findViewById(R.id.iv_wifi_connected_logo);
		mWifiName = (TextView)mView.findViewById(R.id.tv_wifi_connected_name);
		mWifiStatus = (TextView)mView.findViewById(R.id.tv_wifi_options);
		mWifiAlias = (TextView)mView.findViewById(R.id.tv_wifi_connected_alias);
		mWifiAuthDesc = (TextView)mView.findViewById(R.id.tv_wifi_connected_desc);
		
		mWifiAuthListView = (ListView)mView.findViewById(R.id.lv_wifi_free_list);
		mWifiAuthList = new WifiAuthListAdapter(mView.getContext(), mWifiListScanned.getAuthList());
		mWifiAuthListView.setAdapter(mWifiAuthList);
		mWifiAuthListView.setOnItemClickListener(mAuthItemClickListener);
		
		mWifiNotAuthListView = (ListView)mView.findViewById(R.id.lv_wifi_encrypt_list);
		mWifiNotAuthList = new WifiNotAuthListAdapter(mView.getContext(), mWifiListScanned.getNotAuthList());
		mWifiNotAuthListView.setAdapter(mWifiNotAuthList);
		mWifiNotAuthListView.setOnItemClickListener(mNotAuthItemClickListener);
	}
	
    //-----------------------------------------------------------------------------------------------------------------
    //custom functions
    private void doSearchAnimation(boolean start) {
    	if (start) {
    		mAnimSearch.start();
    		mImageNeedle.startAnimation(mAnimNeedle);
    	} else {
    		mAnimSearch.stop();
    		mAnimSearch.selectDrawable(0);
    		mImageNeedle.clearAnimation();
    	}
    }
    
    private void doConnectingAnimation(boolean start) {
    	if (start) {
    		if (mAnimWifiCon != null && mAnimWifiCon.isRunning())
    			return;
    		
    		mWifiConLogo.setImageResource(R.animator.animation_connecting);
    		mAnimWifiCon = (AnimationDrawable)mWifiConLogo.getDrawable();
    		mAnimWifiCon.start();
    	} else if(mAnimWifiCon != null) {
    		if (!mAnimWifiCon.isRunning())
    			return;
    		
    		mAnimWifiCon.stop();
    		mAnimWifiCon.selectDrawable(0);
    	}
    }
    
    private void showWifiConnectDialog(final WifiListItem wifiListItem, final DialogType dialogType) {
    	final WifiConnectDialog wifiConnectDialog = new WifiConnectDialog(mView.getContext(), dialogType);
    	
    	wifiConnectDialog.setTitle("连接到：" + wifiListItem.getWifiName());
    	wifiConnectDialog.setLeftBtnStr("取消");
    	wifiConnectDialog.setRightBtnStr("确定");
    	
    	if (dialogType == DialogType.PASSWORD) {
    		wifiConnectDialog.clearPwdEditText();
	}
    	
    	wifiConnectDialog.setLeftBtnListener(new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
    	
    	wifiConnectDialog.setRightBtnListener(new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				WifiBRService.setWifiSupplicant(true);
				boolean connResult = false;
				switch (dialogType) {
				case DEFAULT:
					if (wifiListItem.isAuthWifi() || wifiListItem.isOpenWifi()) {
						connResult = mWifiAdmin.connectToNewNetwork(wifiListItem, true, false);
					} else if (wifiListItem.isLocalWifi()) {
						WifiConfiguration cfgSelected = mWifiAdmin.getWifiConfiguration(wifiListItem);
						connResult = mWifiAdmin.connectToConfiguredNetwork(cfgSelected, true);
					} else {
						Toast.makeText(mView.getContext(), "错误的WiFi类型", Toast.LENGTH_LONG).show();
					}
					break;
				case PASSWORD:
					if (wifiListItem.isEncryptWifi()) {
						String inputedPwd = wifiConnectDialog.getPwdContent();
						if (inputedPwd.equals("")) {
							Toast.makeText(mView.getContext(), "请输入密码。", Toast.LENGTH_LONG).show();
							return;
						}
						wifiListItem.setWifiPwd(inputedPwd);
						connResult = mWifiAdmin.connectToNewNetwork(wifiListItem, true, false);
						//shutdown soft keyboard if soft keyboard is actived
						InputMethodManager imm = (InputMethodManager)mView.getContext().getSystemService(mView.getContext().INPUT_METHOD_SERVICE);
						if (imm.isActive()) {
							imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
						}
					}
					break;

				default:
					break;
				}
				dialog.dismiss();
				if (!connResult) {
					Toast.makeText(mView.getContext(), "无法连接到网络：" + wifiListItem.getWifiName(), Toast.LENGTH_LONG).show();
				}
			}
		});
    	
    	wifiConnectDialog.show();
    }
}
