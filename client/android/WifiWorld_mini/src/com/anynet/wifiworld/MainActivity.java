/*
 * Copyright 2015 Anynet Corporation All Rights Reserved.
 *
 * The source code contained or described herein and all documents related to
 * the source code ("Material") are owned by Anynet Corporation or its suppliers
 * or licensors. Title to the Material remains with Anynet Corporation or its
 * suppliers and licensors. The Material contains trade secrets and proprietary
 * and confidential information of Anynet or its suppliers and licensors. The
 * Material is protected by worldwide copyright and trade secret laws and
 * treaty provisions.
 * No part of the Material may be used, copied, reproduced, modified, published
 * , uploaded, posted, transmitted, distributed, or disclosed in any way
 * without Anynet's prior express written permission.
 *
 * No license under any patent, copyright, trade secret or other intellectual
 * property right is granted to or conferred upon you by disclosure or delivery
 * of the Materials, either expressly, by implication, inducement, estoppel or
 * otherwise. Any license under such intellectual property rights must be
 * express and approved by Anynet in writing.
 *
 * @brief ANLog is the custom log for wifiworld project.
 * @date 2015-06-04
 * @author Jason.Chen
 *
 */

package com.anynet.wifiworld;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.anynet.wifiworld.map.MapFragment;
import com.anynet.wifiworld.me.MeFragment;
import com.anynet.wifiworld.util.AppInfoUtil;
import com.anynet.wifiworld.util.HandlerUtil.MessageListener;
import com.anynet.wifiworld.util.HandlerUtil.StaticHandler;
import com.anynet.wifiworld.wifi.ui.WifiFragment;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.IUmengUnregisterCallback;
import com.umeng.message.PushAgent;

public class MainActivity extends BaseActivity implements MessageListener {
	
	private final static int TAB_COUNTS = 3;
	private final static int CONNECT_TAB_IDX = 0;
	private final static int NEARBY_TAB_IDX = 1;
	private final static int MY_TAB_IDX = 2;
	
	public FragmentTabHost mTabHost;
	private Button[] mTabs;
	private WifiFragment wifiFragment;
	private MapFragment mapFragment;
	private MeFragment meFragment;
	private MainFragment[] fragments;
	private int index;
	private int currentTabIndex;
	
	private PushAgent mPushAgent;
    private StaticHandler handler = new StaticHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // 打开友盟推送
 		mPushAgent = PushAgent.getInstance(this);
 		mPushAgent.onAppStart();
 		mPushAgent.enable(mRegisterCallback);
        
        initView();
        initFragments();
        FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
		for (int i = 0; i < fragments.length; i++) {
			if (!fragments[i].isAdded()) {
				trx.add(R.id.fragment_container, fragments[i]).hide(fragments[i]);
			}
		}
		trx.show(fragments[0]).commit();
		reflesh();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			moveTaskToBack(true);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
    
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	private void reportDeviceToken(final String appVersion, final String deviceToken) {
		Runnable reportDeviceTokenRunnable = new Runnable() {
			@Override
			public void run() {
			}
		};
		handler.post(reportDeviceTokenRunnable);
	}
	
	public IUmengRegisterCallback mRegisterCallback = new IUmengRegisterCallback() {

		@Override
		public void onRegistered(String registrationId) {
			handler.post(new Runnable() {

				@Override
				public void run() {
					String device_token = mPushAgent.getRegistrationId();
					String appVersion = AppInfoUtil.getVersionName(MainActivity.this);
					reportDeviceToken(appVersion, device_token);
					return;
				}
			});
		}
	};

	public IUmengUnregisterCallback mUnregisterCallback = new IUmengUnregisterCallback() {

		@Override
		public void onUnregistered(String registrationId) {
			// TODO Auto-generated method stub
			handler.post(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
				}
			});
		}
	};
	
	//-------------------------------------------------------------------------------------------------------------
	//custom functions
	private void initFragments() {
		if (wifiFragment == null)
			wifiFragment = new WifiFragment();
		if (mapFragment == null)
			mapFragment = new MapFragment();
		if (meFragment == null)
			meFragment = new MeFragment();

		if (fragments == null || fragments.length < 2)
			fragments = new MainFragment[] { wifiFragment, mapFragment, meFragment };
	}

	private void initView() {
		mTabs = new Button[TAB_COUNTS];
		mTabs[CONNECT_TAB_IDX] = (Button) findViewById(R.id.btn_connect);
		mTabs[NEARBY_TAB_IDX] = (Button) findViewById(R.id.btn_map);
		mTabs[MY_TAB_IDX] = (Button) findViewById(R.id.btn_my);
	}
	
	private void reflesh() {
		FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
		if (currentTabIndex != index) {
			trx.hide(fragments[currentTabIndex]);
			if (!fragments[index].isAdded()) {
				trx.add(R.id.fragment_container, fragments[index]);
			}
			trx.show(fragments[index]).commit();
		}
		if (mTabs != null && mTabs[currentTabIndex] != null)
			mTabs[currentTabIndex].setSelected(false);
		// 把当前tab设为选中状态
		if (mTabs != null && mTabs[index] != null)
			mTabs[index].setSelected(true);
		currentTabIndex = index;
	}
	
	/**
	 * button点击事件
	 * 
	 * @param view
	 */
	public void onTabClicked(View view) {
		switch (view.getId()) {
		case R.id.btn_connect:
			index = CONNECT_TAB_IDX;
			break;
		case R.id.btn_map:
			index = NEARBY_TAB_IDX;
			reflesh();
			break;
		case R.id.btn_my:
			index = MY_TAB_IDX;
			break;
		}
		reflesh();
	}
    
	//-------------------------------------------------------------------------------------------------------------
	//custom base UI
	public static abstract class MainFragment extends BaseFragment {

		protected boolean isVisible;
		
		public void startUpdte() {
			com.anynet.wifiworld.util.XLLog.log(TAG, "startUpdte");
		}

		public void stopUpdte() {
			com.anynet.wifiworld.util.XLLog.log(TAG, "stopUpdte");
		}

		/**
		 * 在这里实现Fragment数据的缓加载.
		 * 
		 * @param isVisibleToUser
		 */
		@Override
		public void setUserVisibleHint(boolean isVisibleToUser) {
			super.setUserVisibleHint(isVisibleToUser);
			if (getUserVisibleHint()) {
				isVisible = true;
				onVisible();
			} else {
				isVisible = false;
				onInvisible();
			}
		}

		protected abstract void onVisible();

		protected abstract void onInvisible();
	}

	@Override
	public void handleMessage(Message msg) {
		// TODO Auto-generated method stub
		
	}
}
