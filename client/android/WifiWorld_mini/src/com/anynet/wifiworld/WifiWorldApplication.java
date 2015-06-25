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
 * @date 2015-06-07
 * @author buffer
 *
 */
package com.anynet.wifiworld;

import java.lang.ref.SoftReference;
import java.util.Stack;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Process;
import cn.bmob.v3.Bmob;

import com.anynet.wifiworld.util.GlobalBroadcast;
import com.anynet.wifiworld.util.NetworkStateListener;
import com.anynet.wifiworld.wifi.WifiAdmin;

public class WifiWorldApplication extends Application {
	private String TAG = WifiWorldApplication.class.getSimpleName();
	
    private static WifiWorldApplication mInstance;
    private Stack<SoftReference<Activity>> mActivityStack = new Stack<SoftReference<Activity>>();
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        mInstance = this;
        //检测系统的WiFi是否打开，强行打开
        WifiAdmin.getInstance(this).openWifi();
        //初始化组件
        GlobalBroadcast.registerBroadcastListener(mNetworkListener);
        Bmob.initialize(this, GlobalConfig.BMOB_KEY);
    }
    
    public static WifiWorldApplication getInstance() {
        return mInstance;
    }
    
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
    
    @Override
    public void onTerminate() {
        super.onTerminate();
        GlobalBroadcast.unregisterBroadcastListener(mNetworkListener);
    }
    
    // 以下用户管理整个应用的activity
    public Activity getTopActivity() {
        return mActivityStack.peek().get();
    }
    
    public void exitAplication() {
        finishAllActivity();
        ActivityManager activityMgr = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        activityMgr.killBackgroundProcesses(this.getPackageName());
        Process.killProcess(Process.myPid());
    }
    
    public void activityCreated(Activity activity) {
        if (activity == null) {
            return;
        }
        SoftReference<Activity> softActivity = new SoftReference<Activity>(activity);
        mActivityStack.push(softActivity);
    }
    
    public void activityDestroyed(Activity activity) {
        if (activity == null || mActivityStack.isEmpty() == true) {
            return;
        }
        //可能被垃圾回收了
        if (mActivityStack.peek().get() != activity) {
            return;
        }
        mActivityStack.pop();
    }
    
    public void finishAllActivity()
    {
        while (mActivityStack.isEmpty() == false)
        {
            Activity activity = mActivityStack.pop().get();
            if (activity != null)
            {
                activity.finish();
            }
        }
        mActivityStack.clear();
    }

	public static boolean isLogin() {
		// TODO Auto-generated method stub
		return false;
	}
	
	private NetworkStateListener mNetworkListener = new NetworkStateListener() {
		@Override
		public void onNetworkStateChange(Intent intent) {

			NetworkInfo networkInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);

			// 网络断了的时候
			if (!networkInfo.isConnected()) {
			} else {
				// 友盟自动更新
		        /*UmengUpdateAgent.setUpdateOnlyWifi(true);
		        UmengUpdateAgent.silentUpdate(mInstance);
		        
		        Toast.makeText(mInstance, "非正式版每次连接只能免费5分钟，请及时更新到正式版本。" , Toast.LENGTH_SHORT).show();
		        final Timer timer_of_cutoff = new Timer();
		        timer_of_cutoff.schedule(new TimerTask() {

					@Override
					public void run() {
						WifiAdmin wifi = WifiAdmin.getInstance(mInstance);
						WifiInfo wifiInfo = wifi.getWifiInfo();
						if (wifiInfo != null) {
							wifi.disConnectionWifi(wifiInfo.getNetworkId());
						}
					}
                	
                }, 300*1000);
		        
		        // 当更新到达时候开始计时，防止用户作弊
		        UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
		            @Override
		            public void onUpdateReturned(int updateStatus,UpdateResponse updateInfo) {
		                switch (updateStatus) {
		                case UpdateStatus.Yes: // has update
		                    UmengUpdateAgent.showUpdateDialog(mInstance, updateInfo);
		                    break;
		                case UpdateStatus.No: // has no update
		                    Toast.makeText(mInstance, "没有更新", Toast.LENGTH_SHORT).show();
		                    break;
		                case UpdateStatus.NoneWifi: // none wifi
		                    Toast.makeText(mInstance, "没有wifi连接， 只在wifi下更新", Toast.LENGTH_SHORT).show();
		                    break;
		                case UpdateStatus.Timeout: // time out
		                    Toast.makeText(mInstance, "超时", Toast.LENGTH_SHORT).show();
		                    break;
		                }
		            }
		        });
		        
		        // 当用户点击弹窗的时候对选择进行计时
		        UmengUpdateAgent.setDialogListener(new UmengDialogButtonListener() {

		            @Override
		            public void onClick(int status) {
		                switch (status) {
		                case UpdateStatus.Update:
		                    Toast.makeText(mInstance, "正在进行更新正式版，请耐心等待......" , Toast.LENGTH_SHORT).show();
		                    timer_of_cutoff.cancel();
		                    break;
		                case UpdateStatus.Ignore:
		                case UpdateStatus.NotNow:
		                    break;
		                }
		            }
		        });*/
			}

		}
	};
    
}
