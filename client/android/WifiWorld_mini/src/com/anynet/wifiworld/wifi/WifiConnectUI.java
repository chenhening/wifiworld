package com.anynet.wifiworld.wifi;

import com.anynet.wifiworld.MainActivity;
import com.anynet.wifiworld.R;
import com.anynet.wifiworld.util.GlobalHandler;
import com.anynet.wifiworld.wifi.WifiBRService.OnWifiStatusListener;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.net.wifi.WifiInfo;
import android.os.IBinder;
import android.util.Log;
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
	private WifiAuthListAdapter mWifiAuthList;
	private WifiNotAuthListAdapter mWifiNotAuthList;
	
	private ImageView mWifiConLogo;
	private TextView mWifiName;
	private TextView mWifiStatus;
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
			if (mWifiCurrent.getWifiListItem() != null) {
				mWifiName.setText(mWifiCurrent.getWifiListItem().getAlias());
				Bitmap logo = mWifiCurrent.getWifiListItem().getLogo();
				if (logo != null) {
					mWifiConLogo.setImageBitmap(logo);
				} else {
					mWifiConLogo.setImageResource(R.drawable.wifi_connected_icon);
				}
			} else {
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
		mWifiAuthListView = (ListView)mContext.findViewById(R.id.lv_wifi_free_list);
		mWifiAuthList = new WifiAuthListAdapter(mContext, mWifiListScanned.getFreeList());
		mWifiAuthListView.setAdapter(mWifiAuthList);
		
		mWifiNotAuthListView = (ListView)mContext.findViewById(R.id.lv_wifi_encrypt_list);
		mWifiNotAuthList = new WifiNotAuthListAdapter(mContext, mWifiListScanned.getEncryptList());
		mWifiNotAuthListView.setAdapter(mWifiNotAuthList);
	}
	
	private void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		Log.i(TAG, "list item size: " + listAdapter.getCount());
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
			Log.i(TAG, "list item height: " + listItem.getMeasuredHeight());
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		Log.i(TAG, "list height: " + params.height);
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
}
