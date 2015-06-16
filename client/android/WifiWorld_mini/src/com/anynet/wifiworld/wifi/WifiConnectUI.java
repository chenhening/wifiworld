package com.anynet.wifiworld.wifi;

import com.anynet.wifiworld.MainActivity;
import com.anynet.wifiworld.R;
import com.anynet.wifiworld.dialog.WifiConnectDialog;
import com.anynet.wifiworld.dialog.WifiConnectDialog.DialogType;
import com.anynet.wifiworld.util.GlobalHandler;
import com.anynet.wifiworld.wifi.WifiBRService.OnWifiStatusListener;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class WifiConnectUI {
	private final static String TAG = WifiConnectUI.class.getSimpleName();
	
	private MainActivity mContext;
	private WifiAdmin mWifiAdmin;
	private WifiCurrent mWifiCurrent;
	private WifiListScanned mWifiListScanned;
	private WifiAuthListAdapter mWifiAuthList;
	private WifiNotAuthListAdapter mWifiNotAuthList;
	
	private ImageView mWifiConLogo;
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
				setListViewHeight(mWifiAuthListView);
			}
			if (mWifiNotAuthList != null) {
				mWifiNotAuthList.refreshWifiList(mWifiListScanned.getNotAuthList());
				setListViewHeight(mWifiNotAuthListView);
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
			//refreshBg();
			mWifiStatus.setText(str);
		}
		
		@Override
		public void onNetWorkDisconnected(String str) {
			//refreshBg();
			mWifiStatus.setText(str);
		}

		@Override
		public void onSupplicantChanged(String statusStr) {
			mWifiStatus.setText(statusStr);
		}

		@Override
		public void onSupplicantDisconnected(String statusStr) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onScannableAvaliable() {
			//仍需要做修改
			mWifiListScanned.startWifiScanThread();
		}
		
		@Override
		public void onWifiStatChanged(boolean isEnabled) {
		}
	};
	
	private OnItemClickListener mAuthItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			WifiListItem wifiListItem = mWifiListScanned.getAuthList().get(position);
			showWifiConnectDialog(wifiListItem, WifiConnectDialog.DialogType.DEFAULT);
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
	
	public WifiConnectUI(Context context) {
		mContext = (MainActivity)context;
		mWifiAdmin = WifiAdmin.getInstance(mContext);
		mWifiCurrent = WifiCurrent.getInstance(context);
		mWifiListScanned = WifiListScanned.getInstance(context, wifiListHandler);
		WifiBRService.setOnWifiStatusListener(mWifiStatusListener);
		WifiBRService.bindWifiService(mContext, conn);
		getViewHolder();
	}
	
	public void refreshAnim() {
		//启动动画，更新WiFi扫描列表
		doSearchAnimation(true);
	}
	
	public void refreshBg() {
		setWifiListContent();
	}
	
	private void setWifiConnectedContent() {
		if (mWifiCurrent.isConnected()) {
			WifiListItem item = mWifiCurrent.getWifiListItem();
			if (item != null && item.isAuthWifi()) { //如果是认证，显示认证信息
				mWifiName.setText(item.getAlias());
				mWifiAlias.setText("[" + mWifiCurrent.getWifiName() + "]");
				mWifiAuthDesc.setText("[" + item.getBanner() + "]");
				Bitmap logo = item.getLogo();
				if (logo != null) {
					mWifiConLogo.setImageBitmap(logo);
				} else {
					mWifiConLogo.setImageResource(R.drawable.wifi_connected_icon);
				}
			} else { //如果非认证显示默认信息
				mWifiName.setText(mWifiCurrent.getWifiName());
				mWifiConLogo.setImageResource(R.drawable.wifi_connected_icon);
			}
		} else if (mWifiCurrent.isConnecting()) {
			WifiBRService.setWifiSupplicant(true);
		} else {
			mWifiName.setText("未连接WiFi");
			mWifiConLogo.setImageResource(R.drawable.wifi_connected_icon);
		}
	}
	
	private void setWifiListContent() {
		boolean success = mWifiListScanned.refresh(false);
		if (!success) {
			doSearchAnimation(false);
		}
	}
	
	private void getViewHolder() {
		//断开连接
		mWifiSwitch = (ToggleButton)mContext.findViewById(R.id.tb_wifi_switch);
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
		mImageNeedle = (ImageView)mContext.findViewById(R.id.iv_wifi_search_needle);
		mAnimNeedle = AnimationUtils.loadAnimation(mContext, R.animator.animation_needle);
		mImageSearch = (ImageView)mContext.findViewById(R.id.iv_wifi_search_heart);
		mImageSearch.setImageResource(R.animator.animation_search);
		mAnimSearch = (AnimationDrawable)mImageSearch.getDrawable();
		mContext.findViewById(R.id.rl_wifi_search).setOnClickListener(new OnClickListener() {
		
			@Override
			public void onClick(View v) {
				doSearchAnimation(!mAnimSearch.isRunning());
			}
			
		});
		
		mWifiConLogo = (ImageView)mContext.findViewById(R.id.iv_wifi_connected_logo);
		mWifiName = (TextView)mContext.findViewById(R.id.tv_wifi_connected_name);
		mWifiStatus = (TextView)mContext.findViewById(R.id.tv_wifi_options);
		mWifiAlias = (TextView)mContext.findViewById(R.id.tv_wifi_connected_alias);
		mWifiAuthDesc = (TextView)mContext.findViewById(R.id.tv_wifi_connected_desc);
		
		mWifiAuthListView = (ListView)mContext.findViewById(R.id.lv_wifi_free_list);
		mWifiAuthList = new WifiAuthListAdapter(mContext, mWifiListScanned.getAuthList());
		mWifiAuthListView.setAdapter(mWifiAuthList);
		mWifiAuthListView.setOnItemClickListener(mAuthItemClickListener);
		
		mWifiNotAuthListView = (ListView)mContext.findViewById(R.id.lv_wifi_encrypt_list);
		mWifiNotAuthList = new WifiNotAuthListAdapter(mContext, mWifiListScanned.getNotAuthList());
		mWifiNotAuthListView.setAdapter(mWifiNotAuthList);
		mWifiNotAuthListView.setOnItemClickListener(mNotAuthItemClickListener);
	}
	
	private void setListViewHeight(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		//for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(0, null, listView);
			listItem.measure(0, 0);
			totalHeight = listItem.getMeasuredHeight() * listAdapter.getCount();
		//}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}
	
    //-----------------------------------------------------------------------------------------------------------------
    //custom functions
    private void doSearchAnimation(boolean start) {
    	if (start) {
    		mAnimSearch.start();
    		mImageNeedle.startAnimation(mAnimNeedle);
    		setWifiListContent();
    	} else {
    		mAnimSearch.stop();
    		mAnimSearch.selectDrawable(0);
    		mImageNeedle.clearAnimation();
    	}
    }
    
    private void showWifiConnectDialog(final WifiListItem wifiListItem, final DialogType dialogType) {
    	final WifiConnectDialog wifiConnectDialog = new WifiConnectDialog(mContext, dialogType);
    	
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
						Toast.makeText(mContext, "错误的WiFi类型", Toast.LENGTH_LONG).show();
					}
					break;
				case PASSWORD:
					if (wifiListItem.isEncryptWifi()) {
						String inputedPwd = wifiConnectDialog.getPwdContent();
						if (inputedPwd.equals("")) {
							Toast.makeText(mContext, "请输入密码。", Toast.LENGTH_LONG).show();
							return;
						}
						wifiListItem.setWifiPwd(inputedPwd);
						connResult = mWifiAdmin.connectToNewNetwork(wifiListItem, true, false);
						//shutdown soft keyboard if soft keyboard is actived
						InputMethodManager imm = (InputMethodManager)mContext.getSystemService(mContext.INPUT_METHOD_SERVICE);
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
					Toast.makeText(mContext, "无法连接到网络：" + wifiListItem.getWifiName(), Toast.LENGTH_LONG).show();
				}
			}
		});
    	
    	wifiConnectDialog.show();
    }
}
