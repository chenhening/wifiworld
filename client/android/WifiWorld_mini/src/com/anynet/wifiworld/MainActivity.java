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
import android.os.Bundle;
import android.view.Menu;

import com.anynet.wifiworld.wifi.WifiConnectUI;

public class MainActivity extends Activity {
	private WifiConnectUI mWifiConnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mWifiConnect = new WifiConnectUI(this);
        mWifiConnect.setWifiConnectedContent();
        //mWifiConnect.setWifiListContent();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
