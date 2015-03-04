package com.anynet.wifiworld;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.umeng.update.UmengDialogButtonListener;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;
import com.anynet.wifiworld.app.BaseActivity;
import com.anynet.wifiworld.util.AppInfoUtil;
import com.anynet.wifiworld.util.NetHelper;
import com.anynet.wifiworld.R;

public class SettingAboutActivity extends BaseActivity  implements OnClickListener
{
    
    /**
     * 头部资源
     */
    private TextView tvVersion;
    
    private RelativeLayout rlCheckUpdate;
    

    
    private TextView tvVersionState;
    
    private LinearLayout llSettingAboutRemind;
    

    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setContentView(R.layout.setting_about);
        super.onCreate(savedInstanceState);
      
        
        bingdingTitleUI();
     
        bindingUI();
        bingdingEvent();
        
        getData();
        
    }
    
    private void bingdingTitleUI()
    {

        mTitlebar.tvTitle.setText(getString(R.string.setting_title_about));
        
    }
    
    private void bingdingEvent()
    {
        
        rlCheckUpdate.setOnClickListener(this);
        
    }
    
    
    
    @Override
    public void onClick(View v)
    {
        

        switch (v.getId())
        {
            
           //强迫更新
            case R.id.rl_check_update:
                
                if (!NetHelper.isNetworkAvailable(SettingAboutActivity.this))
                {
                    showToast("网络已经断开");
                    return;
                }
                
                UmengUpdateAgent.setDefault();
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
                                UmengUpdateAgent.showUpdateDialog(SettingAboutActivity.this,
                                        updateInfo);

                                
                                break;
                            case UpdateStatus.No: // has no update
                                tvVersionState.setText(R.string.setting_about_has_update);
                                llSettingAboutRemind.setVisibility(View.VISIBLE);
                                 
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
                                
                                //更新成功则去除new提示
                                tvVersionState.setText(R.string.setting_about_is_newest_version);
                                
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
                
                UmengUpdateAgent.forceUpdate(SettingAboutActivity.this);
                break;
        }
    }
    
    private void bindingUI()
    
    {
        
        tvVersion = (TextView) findViewById(R.id.setting_about_version);
        rlCheckUpdate = (RelativeLayout) findViewById(R.id.rl_check_update);
        
        
    }
    
    //获取版本号
    private void getData()
    {
        
        tvVersion.setText("V " + AppInfoUtil.getVersionName(this));
        
        // 检查是否有新版本
        checkIfHasNewVersion();
        
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
                        tvVersionState.setText(R.string.setting_about_find_new_version);
                        break;
                        
                    case UpdateStatus.No: //无新版本
                        tvVersionState.setText(R.string.setting_about_has_update);
                        break;
                }
            }
        });
        UmengUpdateAgent.update(this);
        
    }
    
    
}
