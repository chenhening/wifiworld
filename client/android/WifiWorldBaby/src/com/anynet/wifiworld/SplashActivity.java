package com.anynet.wifiworld;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
//
//import com.xunlei.common.member.XLErrorCode;
//import com.xunlei.common.member.XLOnUserListener;
//import com.xunlei.common.member.XLUserInfo;
//import com.xunlei.common.member.XLUserUtil;
import com.anynet.wifiworld.app.BaseActivity;
import com.anynet.wifiworld.config.GlobalConfig;
import com.anynet.wifiworld.constant.Const;
import com.anynet.wifiworld.util.AppInfoUtil;
import com.anynet.wifiworld.util.LoginHelper;
import com.anynet.wifiworld.util.NetHelper;
import com.anynet.wifiworld.util.PreferenceHelper;
import com.anynet.wifiworld.util.UmengHelper;
import com.anynet.wifiworld.R;

public class SplashActivity extends BaseActivity
{
    
    /**
     * 头部资源
     */
    private TextView tvVersion;
    
    private PreferenceHelper pref = PreferenceHelper.getInstance();
    
 //   XLOnUserListener mLoginListener;
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        tvVersion = (TextView) findViewById(R.id.tv_splash_version);
        tvVersion.setText("V " + AppInfoUtil.getVersionName(this));
        
        
//      
//        //登录监听器
//        mLoginListener = new XLOnUserListener()
//        {
//            public boolean onUserLogin(int errorCode, XLUserInfo userInfo, Object userdata, String errorDesc)
//            {
//                
//                Intent intent = null;
//                switch (errorCode)
//                {
//                
//                    case XLErrorCode.SUCCESS:
//                        
//                        UmengHelper.setUmeng(SplashActivity.this);
//                        
//                        LoginHelper.getInstance().storeLoginInfo();
//                        //设置心跳
//                      //  XLUserUtil.getInstance().setKeepAlive(true, -1);
//
//                        long appLastUpdateTime = AppInfoUtil.getLastUpdateTime(SplashActivity.this);
//                        if (GlobalConfig.getInstance().getLastUpdateTime() >= appLastUpdateTime){
//                            MainActivity.startActivity(SplashActivity.this,false);
//                            ReportUtil.reportLogin(SplashActivity.this);
//                        } else {
//                            NewVersionActivity.startActivity(SplashActivity.this);
//                            GlobalConfig.getInstance().setLastUpdateTime(appLastUpdateTime);
//                        }
//
//                        SplashActivity.this.finish();
//                        
//                        break;
//                    
//                    default:
//                        
//                        gotoLogin();
//                        
//                        break;
//                }
//                return true;
//            }
//        };
        
    }
    
    
    @Override
    protected void onResume()
    {
      
        super.onResume();

        new Handler().postDelayed(new Runnable()
        {
            public void run()
            {
                
                toAutoLogin();
              
            }
        }, 1000);
    }

    
    private void gotoLogin()
    {
        //Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        //startActivity(intent);
        //SplashActivity.this.finish();
    }
    
    public void onDestroy() {
    	super.onDestroy();
    }
 
    
    /**
     * 自动登录
     */
    private void toAutoLogin()
    {
        
        //判断网络
        if (!NetHelper.isNetworkAvailable(getApplicationContext()))
        {
            
            showToast("当前网络不可用");
            
            gotoLogin();
            
        }
        
        long curTime = System.currentTimeMillis();
        long lastTime = pref.getLong(Const.LAST_TIME, -1);
        String userAccount = pref.getString(Const.USER_ACCOUNT, "");
        String encryptedPassword = pref.getString(Const.EN_PWD, null);
        String passwordCheckNum = pref.getString(Const.PWD_CHECK_NUM, null);
        boolean isAutoLogin = pref.getBoolean(Const.USER_AUTO_LOGIN, false);
        
        //符合免登陆条件
        if (lastTime != -1 && curTime - lastTime < GlobalConfig.getInstance().getNoNeedLoginTimeDiv() && encryptedPassword != null && passwordCheckNum != null
                && isAutoLogin && userAccount != null)
        {
            
//            XLUserUtil.getInstance().userLogin(userAccount, false, encryptedPassword, passwordCheckNum, null, null,
//            
//            mLoginListener, null);
            
        }
        else
        {
            
            gotoLogin();
            
        }
        
    }
    
}