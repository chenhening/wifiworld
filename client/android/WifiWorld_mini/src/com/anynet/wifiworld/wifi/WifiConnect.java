package com.anynet.wifiworld.wifi;

import com.anynet.wifiworld.MainActivity;
import com.anynet.wifiworld.R;
import com.anynet.wifiworld.data.WifiProfile;
import com.anynet.wifiworld.util.GlobalHandler;
import com.anynet.wifiworld.wifi.WifiBRService.OnWifiStatusListener;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.net.wifi.WifiInfo;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class WifiConnect {
	private final static String TAG = WifiConnect.class.getSimpleName();
	
	private MainActivity mContext;
	private WifiAdmin mWifiAdmin;
	private WifiCurrent mWifiCurrent;
	private WifiListScanned mWifiListScanned;
	private WifiFreeListAdapter mWifiFreeList;
	private WifiEncryptListAdapter mWifiEncryptList;
	
	private TextView mWifiName;
	
	private GlobalHandler wifiListHandler = new GlobalHandler() {
		
		@Override
		public void onWifiListRefreshed() {
			setWifiConnectedContent();
			if (mWifiFreeList != null) {
				mWifiFreeList.refreshWifiList(mWifiListScanned.getFreeList());
			}
			if (mWifiEncryptList != null) {
				mWifiEncryptList.refreshWifiList(mWifiListScanned.getEncryptList());
			}
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
//					if (isEnabled) {
//						mPageRoot.findViewById(R.id.wifi_disable_layout).setVisibility(View.INVISIBLE);
//						mPageRoot.findViewById(R.id.wifi_enable_layout).setVisibility(View.VISIBLE);
//						WifiBRService.setWifiScannable(true);
//					} else {
//						mPageRoot.findViewById(R.id.wifi_disable_layout).setVisibility(View.VISIBLE);
//						mPageRoot.findViewById(R.id.wifi_enable_layout).setVisibility(View.INVISIBLE);
//					}
				}

				@Override
				public void onNetWorkChanged(boolean isConnected, String str) {
//					if (isConnected) {
//						// 一旦网络状态发生变化后停止监听服务
//						WifiBRService.setWifiSupplicant(false);
//						if (isPwdConnect) {
//							mWifiAdmin.saveConfig();
//							isPwdConnect = false;
//						}
//					}
//					// refresh WiFi list and WiFi title info
//					mWifiListHelper.fillWifiList();
//
//					// refreshWifiTitleInfo();
//					//isPwdConnect = false;
//
//					if (isConnected) {
//
//						WifiInfo curwifi = WifiAdmin.getInstance(getApplicationContext()).getWifiConnected();
//						if (curwifi == null)
//							return;
//						boolean isExist = false;
//						for (WifiInfoScanned item : mWifiAuth) {
//							if (item.getWifiName().equals(curwifi.getSSID())) {
//								isExist = true;
//							}
//						}// 发现wifi未认证不做数据监听
//						if (!isExist)
//							return;
//
//						// 一旦打开连接wifi，如果是认证的wifi需要做监听wifi提供者实时共享子信息
//						String CurMac = curwifi.getBSSID();
//						WifiProfile data_listener = new WifiProfile();
//						data_listener.startListenRowUpdate(getActivity(), "WifiProfile", WifiProfile.unique_key, CurMac,
//								DataListenerHelper.Type.UPDATE, new DataCallback<WifiProfile>() {
//
//									@Override
//									public void onFailed(String msg) {
//										Log.d(TAG, msg);
//									}
//
//									@Override
//									public void onSuccess(WifiProfile object) {
//										// 通知下线
//										if (!object.isShared()) {
//											getActivity().runOnUiThread(new Runnable() {
//
//												@Override
//												public void run() {
//													showToast("对不起，此网络主人停止了网络分享，请保存数据退出网络。");
//													WifiAdmin.getInstance(getApplicationContext()).disConnectionWifi(
//															mWifiAdmin.getWifiConnected().getNetworkId());
//												}
//											});
//										}
//									}
//								});
//					}
				}

				@Override
				public void onScannableAvaliable() {
					mWifiListScanned.refreshWifiList();
				}

				@Override
				public void onSupplicantChanged(String statusStr) {
//					mWifiNameView.setText(statusStr);
//					WifiInfoScanned wifiInfoCurrent = WifiListHelper.getInstance(getActivity()).mWifiInfoCur;
//					if (wifiInfoCurrent != null && wifiInfoCurrent.getWifiLogo() != null) {
//						mWifiLogoView.setImageBitmap(wifiInfoCurrent.getWifiLogo());
//					} else {
//						mWifiLogoView.setImageResource(R.drawable.icon_invalid);
//					}
				}

				@Override
				public void onSupplicantDisconnected(String statusStr) {
//					if (isPwdConnect) {
//						if (mWifiItemClick.getNetworkId() != -1) {
//							mWifiAdmin.forgetNetwork(mWifiItemClick.getNetworkId());
//						}
//						mWifiListHelper.fillWifiList();
//						isPwdConnect = false;
//						String titleStr = "密码输入错误，请重新输入密码";
//						showWifiConnectPwdConfirmDialog(titleStr, mWifiItemClick, mWifiConnectPwdDialog);
//					} else {
//						mWifiListHelper.fillWifiList();
//					}
				}
			});
		}
	};
	
	public WifiConnect(Context context) {
		mContext = (MainActivity)context;
		getViewHolder();
		mWifiCurrent = WifiCurrent.getInstance(context);
		mWifiListScanned = WifiListScanned.getInstance(context, wifiListHandler);
		WifiBRService.bindWifiService(mContext, conn);
	}
	
	public void setWifiConnectedContent() {
		if (mWifiCurrent.isConnected()) {
			mWifiName.setText(mWifiCurrent.getWifiName());
		} else if (mWifiCurrent.isConnecting()) {
			//active WiFi status supervise
		} else {
			mWifiName.setText("未连接WiFi");
		}
	}
	
	public void setWifiListContent() {
		mWifiListScanned.refresh();
	}
	
	private void getViewHolder() {
		mWifiName = (TextView)mContext.findViewById(R.id.tv_wifi_connected_name);
	}
}
