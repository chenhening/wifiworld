/*
 * 文件名称 : UmengHelper.java
 * <p>
 * 作者信息 : liuzongyao
 * <p>
 * 创建时间 : 2014-12-5, 下午5:00:51
 * <p>
 * 版权声明 : Copyright (c) 2009-2012 Hydb Ltd. All rights reserved
 * <p>
 * 评审记录 :
 * <p>
 */

package com.anynet.wifiworld.util;

import java.lang.ref.WeakReference;

import com.testin.agent.TestinAgent;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.anynet.wifiworld.app.BaseActivity;
import com.anynet.wifiworld.app.WifiWorldApplication;
import com.anynet.wifiworld.R;

/**
 * 请在这里增加文件描述
 * <p>
 */
public class UmengHelper
{
    
    
    public static  void setUmeng(BaseActivity act)
    {
        WeakReference<BaseActivity> weakAct = new WeakReference<BaseActivity>(act);
        
        UmengUpdateAgent.setDefault();
        //默认只有在wifi下才自动提醒， 这个设置在什么网络下都提醒
 
        //根据签名判断不同的包
        if (PackageSignHelper.isRelease(WifiWorldApplication.getInstance()))
        {
            //TestinAgent.init(weakAct.get(), weakAct.get().getResources().getString(R.string.release_testin_crash_key));
            UmengUpdateAgent.setAppkey(weakAct.get().getResources().getString(R.string.release_umeng_appkey));
            AnalyticsConfig.setAppkey(weakAct.get().getResources().getString(R.string.release_umeng_appkey));
            AnalyticsConfig.setChannel(weakAct.get().getResources().getString(R.string.release_umeng_channel));
        }
        else
        {
           // TestinAgent.init(weakAct.get(), act.getResources().getString(R.string.test_testin_crash_key));
            UmengUpdateAgent.setAppkey(weakAct.get().getResources().getString(R.string.test_umeng_appkey));
            AnalyticsConfig.setAppkey(weakAct.get().getResources().getString(R.string.test_umeng_appkey));
            AnalyticsConfig.setChannel(weakAct.get().getResources().getString(R.string.test_umeng_channel));
        }
        
        
        //友盟统计，发送数据
        MobclickAgent.updateOnlineConfig(weakAct.get());
        UmengUpdateAgent.setUpdateOnlyWifi(false);
       
        
    }
    
}
