/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.anynet.wifiworld;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.umeng.update.UmengDialogButtonListener;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;
import com.anynet.wifiworld.app.BaseActivity;
import com.anynet.wifiworld.app.WifiWorldApplication;
import com.anynet.wifiworld.constant.Const;
import com.anynet.wifiworld.dialog.NoTitleTwoButtonDialog;
import com.anynet.wifiworld.report.ReportUtil;
import com.anynet.wifiworld.util.AppInfoUtil;
import com.anynet.wifiworld.util.LoginHelper;
import com.anynet.wifiworld.util.NetHelper;
import com.anynet.wifiworld.util.PreferenceHelper;
import com.anynet.wifiworld.view.SlipButton;
import com.anynet.wifiworld.R;

/**
 * 设置界面
 * 
 * @author Administrator
 * 
 */
public class MySettingActivity extends BaseActivity implements OnClickListener
{
    

    private PreferenceHelper pref = PreferenceHelper.getInstance();

    private RelativeLayout rlSettingVersion;
    
    private TextView tvSettingVersion;
    
    private ImageView ivSettingVersion;
    
    private SlipButton tgPushSwitch;
    
    private PushAgent mPushAgent;
    
    private boolean isNewestVersion = false;
     
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        
        setContentView(R.layout.my_setting);
        
        super.onCreate(savedInstanceState);
        bingdingTitleUI();
        bingdingUI();
        bindingEvent();
        getData();
        
    }
    
    private void bingdingTitleUI()
    {
        
        mTitlebar.llFinish.setVisibility(View.INVISIBLE);
        mTitlebar.tvTitle.setText(getString(R.string.my_settting));
        
    }
    
    private void bingdingUI()
    {
        
        
        tgPushSwitch.setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                
                //设置是否推送
                
                boolean state = !tgPushSwitch.getSwitchState();
                if (state) {
                	if (!mPushAgent.isEnabled()) {
                		mPushAgent.enable();
                	}
                } else {
                	if (mPushAgent.isEnabled()) {
                		mPushAgent.disable();
                	}
                }
                //改变状态
                tgPushSwitch.setSwitchState(state, false);
                
                //设置状态
                pref.setBoolean(Const.PUSH_MSG, state);
                
            }
            
        });
        
      
     
        
    }
    
    private void bindingEvent()
    {
        

        rlSettingVersion.setOnClickListener(this);
      
        
    }
    
    private void getData()
    {
        
       
    	mPushAgent = PushAgent.getInstance(this);
    	
        tvSettingVersion.setText( getString(R.string.setting_version_newest, AppInfoUtil.getVersionName(this)));
        
        // 检查是否有新版本
        checkIfHasNewVersion();
        
        boolean state = pref.getBoolean(Const.PUSH_MSG, true);
        if (state) {
        	if (!mPushAgent.isEnabled()) {
        		mPushAgent.enable();
        	}
        } else {
        	if (mPushAgent.isEnabled()) {
        		mPushAgent.disable();
        	}
        }
        tgPushSwitch.setSwitchState(state, false);
        
    }
    

   
    
    private void checkIfHasNewVersion()
    {
        UmengUpdateAgent.setUpdateAutoPopup(false);
        UmengUpdateAgent.setUpdateListener(new UmengUpdateListener()
        {
            @Override
            public void onUpdateReturned(int updateStatus,
                    UpdateResponse updateInfo)
            {
                switch (updateStatus)
                {
                    case UpdateStatus.Yes: //有新版本
                         tvSettingVersion.setText( getString(R.string.setting_version_new));
                         
                         tvSettingVersion.setTextColor(getResources().getColor(R.color.setting_txt));
                         ivSettingVersion.setVisibility(View.VISIBLE);
                      
                        break;
                        
                    case UpdateStatus.No: //最新新版本
                        tvSettingVersion.setText( getString(R.string.setting_version_newest, AppInfoUtil.getVersionName(MySettingActivity.this)));
                        
                        isNewestVersion = true;
                        ivSettingVersion.setVisibility(View.INVISIBLE);
                        break;
                }
            }
        });
        UmengUpdateAgent.update(this);
        
    }
    
    
    
    
    @Override
    public void onClick(View v)
    {
        
      
        
        switch (v.getId())
        {
                
              //强迫更新
            case R.id.rl_setting_version:
                
                if (!NetHelper.isNetworkAvailable(MySettingActivity.this))
                {
                    showToast("网络已经断开");
                    return;
                }
                
                if (isNewestVersion)
                {
                    tvSettingVersion.setText( getString(R.string.setting_version_newest, AppInfoUtil.getVersionName(MySettingActivity.this)));
                    showToast(getString(R.string.setting_version_isnewst_toast));
                    return;
                }
                
                //UmengUpdateAgent.setDefault();
                UmengUpdateAgent.setUpdateAutoPopup(false);
                UmengUpdateAgent.setUpdateListener(new UmengUpdateListener()
                {
                    @Override
                    public void onUpdateReturned(int updateStatus,
                            UpdateResponse updateInfo)
                    {
                        switch (updateStatus)
                        {
                            case UpdateStatus.Yes: //has update
                                UmengUpdateAgent.showUpdateDialog(MySettingActivity.this,
                                        updateInfo);

                                
                                break;
                            case UpdateStatus.No: // has no update
                                tvSettingVersion.setText( getString(R.string.setting_version_newest, AppInfoUtil.getVersionName(MySettingActivity.this)));
                                showToast(getString(R.string.setting_version_isnewst_toast));
                                
                                 
                                break;
                            case UpdateStatus.NoneWifi: // none wifi
                                showToast("没有wifi连接， 只在wifi下更新");
                                
                                break;
                            case UpdateStatus.Timeout: // time out
                                
                                showToast("超时");
                                
                                break;
                        }
                    }
                });
                
                UmengUpdateAgent.setDialogListener(new UmengDialogButtonListener()
                {
                    
                    @Override
                    public void onClick(int status)
                    {
                        switch (status)
                        {
                           
                                
                            case UpdateStatus.Update:
                                
                                tvSettingVersion.setText( getString(R.string.setting_version_newest, AppInfoUtil.getVersionName(MySettingActivity.this)));
                                ivSettingVersion.setVisibility(View.INVISIBLE);
                                
                                break;
                            case UpdateStatus.Ignore:
                                
                                break;
                            case UpdateStatus.NotNow:
                                
                                break;
                                
                            case UpdateStatus.Timeout:
                                
                                break;
                            case UpdateStatus.NoneWifi:
                                break;
                         
                        }
                    }
                });
                
                UmengUpdateAgent.forceUpdate(MySettingActivity.this);
                break;
            
            default:
                break;
        }
        
    }


}
