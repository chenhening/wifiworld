package com.anynet.wifiworld.wifi;

import com.anynet.wifiworld.MainActivity;
import com.anynet.wifiworld.R;
import com.anynet.wifiworld.util.GlobalHandler;
import com.anynet.wifiworld.wifi.WifiBRService.OnWifiStatusListener;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.net.wifi.WifiInfo;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class WifiConnectUI {
	private final static String TAG = WifiConnectUI.class.getSimpleName();
	
	private MainActivity mContext;
	private WifiAdmin mWifiAdmin;
	private WifiCurrent mWifiCurrent;
	private WifiListScanned mWifiListScanned;
	private WifiFreeListAdapter mWifiAuthList;
	private WifiEncryptListAdapter mWifiNotAuthList;
	
	private TextView mWifiName;
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
				mWifiAuthList.refreshWifiList(mWifiListScanned.getFreeList());
				setListViewHeightBasedOnChildren(mWifiAuthListView);
			}
			if (mWifiNotAuthList != null) {
				mWifiNotAuthList.refreshWifiList(mWifiListScanned.getEncryptList());
				setListViewHeightBasedOnChildren(mWifiNotAuthListView);
			}
			
			//停止搜索动画
			DoSearchAnimation(false); 
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
	
	public WifiConnectUI(Context context) {
		mContext = (MainActivity)context;
		mWifiCurrent = WifiCurrent.getInstance(context);
		mWifiListScanned = WifiListScanned.getInstance(context, wifiListHandler);
		WifiBRService.bindWifiService(mContext, conn);
		getViewHolder();
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
		mWifiListScanned.refreshWifiList();
	}
	
	public void refreshWifiListContent() {
		//supervise WiFi scannable broadcast
		mWifiListScanned.refresh();
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
				DoSearchAnimation(!mAnimSearch.isRunning());
			}
			
		});
		DoSearchAnimation(true); //程序一启动起来默认搜索
		
		mWifiName = (TextView)mContext.findViewById(R.id.tv_wifi_connected_name);
		mWifiAuthListView = (ListView)mContext.findViewById(R.id.lv_wifi_free_list);
		mWifiAuthList = new WifiFreeListAdapter(mContext, mWifiListScanned.getFreeList());
		mWifiAuthListView.setAdapter(mWifiAuthList);
		setListViewHeightBasedOnChildren(mWifiAuthListView);
		
		mWifiNotAuthListView = (ListView)mContext.findViewById(R.id.lv_wifi_encrypt_list);
		mWifiNotAuthList = new WifiEncryptListAdapter(mContext, mWifiListScanned.getEncryptList());
		mWifiNotAuthListView.setAdapter(mWifiNotAuthList);
		setListViewHeightBasedOnChildren(mWifiNotAuthListView);
	}
	
	private void setListViewHeightBasedOnChildren(ListView listView) {
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
	
    //-----------------------------------------------------------------------------------------------------------------
    //custom functions
    private void DoSearchAnimation(boolean start) {
    	if (start) {
    		setWifiListContent();
    		mAnimSearch.start();
    		mImageNeedle.startAnimation(mAnimNeedle);
    	} else {
    		mAnimSearch.stop();
    		mAnimSearch.selectDrawable(0);
    		mImageNeedle.clearAnimation();
    	}
    }
}
