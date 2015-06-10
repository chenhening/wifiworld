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

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.wifi.WifiAdmin;
import com.anynet.wifiworld.wifi.WifiConnectUI;

public class MainActivity extends Activity {
	
	private WifiConnectUI mWifiConnect;
	private ToggleButton mWifiSwitch;
	private WifiAdmin mWifiAdmin;
	
	private AnimationDrawable mAnimSearch;
	private Animation mAnimNeedle;
	private ImageView mImageSearch;
	private ImageView mImageNeedle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mWifiConnect = new WifiConnectUI(this);
        mWifiConnect.setWifiConnectedContent();
        mWifiConnect.setWifiListContent();
		mWifiAdmin = WifiAdmin.getInstance(this);
        
        //断开连接
        mWifiSwitch = (ToggleButton)findViewById(R.id.tb_wifi_switch);
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
        mImageNeedle = (ImageView) findViewById(R.id.iv_wifi_search_needle);
        mAnimNeedle = AnimationUtils.loadAnimation(this, R.animator.animation_needle);
        mImageSearch = (ImageView) findViewById(R.id.iv_wifi_search_heart);
        mImageSearch.setImageResource(R.animator.animation_search);
		mAnimSearch = (AnimationDrawable)mImageSearch.getDrawable();
        this.findViewById(R.id.rl_wifi_search).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DoSearchAnimation(!mAnimSearch.isRunning());
			}
        	
        });
        DoSearchAnimation(true); //程序一启动起来默认搜索
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    //-----------------------------------------------------------------------------------------------------------------
    //custom functions
    private void DoSearchAnimation(boolean start) {
    	if (start) {
    		mAnimSearch.start();
    		mImageNeedle.startAnimation(mAnimNeedle);
    	} else {
    		mAnimSearch.stop();
    		mImageNeedle.clearAnimation();
    	}
    }

}
