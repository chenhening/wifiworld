package com.anynet.wifiworld.app;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.DialerFilter;
import android.widget.Toast;

import com.testin.agent.TestinAgent;
import com.umeng.analytics.MobclickAgent;
import com.anynet.wifiworld.R;
import com.anynet.wifiworld.broadcast.GlobalBroadcast;
import com.anynet.wifiworld.broadcast.NetworkStateListener;
import com.anynet.wifiworld.dialog.XLTwoButtonDialog;
import com.anynet.wifiworld.dialog.XLWaitingDialog;
import com.anynet.wifiworld.report.LogcatHelper;
import com.anynet.wifiworld.util.ActivityUtil;
import com.anynet.wifiworld.util.LoginHelper;
import com.anynet.wifiworld.util.LoginHelper.LogoutObserver;
import com.anynet.wifiworld.util.NetHelper;
import com.anynet.wifiworld.util.ViewBinder;
import com.anynet.wifiworld.view.TitlebarHolder;

/**
 * 如果acitivity 断网后自动返回登录界面 请继承这个activity
 * 
 * @author lzl
 * 
 */
public class BaseActivity extends FragmentActivity
{
    
    // 通用标题栏局部，子类需要先调用setContentView,在调用super.onCreate方法
    protected TitlebarHolder mTitlebar;
    
    protected final String TAG = this.getClass().getSimpleName();
    
    protected XLWaitingDialog waitingDialog;
    
    private XLTwoButtonDialog networkConfirm;
    
    private   boolean isKick = false;
    
    // 处理登录退出情形
    private LogoutObserver mLogoutListener = new LogoutObserver()
    {
        
        @Override
        public void OnLogout(int logoutType)
        {
            
            updateLoginState(logoutType);
            
        }
    };
    private NetworkStateListener mNetworkListener = new NetworkStateListener()
    {
        @Override
        public void onNetworkStateChange(Intent intent)
        {
            
            NetworkInfo networkInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);

            //网络断了的时候
            if (!networkInfo.isConnected() && !networkConfirm.isShowing())
            {

                doDisNetworkConnected();
                //展示对话框
                showNetworkDisconnectedDialog();
            }
            else
            {
                doNetworkConnected();
                if (networkConfirm != null){
                	networkConfirm.dismiss();
                }
            }
            
        }
    };
    
    /**
     * 网络断开时的操作
     */
    protected void doNetworkConnected()
    {
        
    }
    
    /**
     * 网络重连时的操作
     */
    protected void doDisNetworkConnected()
    {
        
    }
    
    private void showNetworkDisconnectedDialog()
    {
        
        networkConfirm.setTitle("提示");
        String content = "网络已断开，请稍后重试";
        networkConfirm.setContent(content);
        
        networkConfirm.setRightBtnStr("检查网络");
        
        //重尝试会进到刷新网络中
        networkConfirm.setRightBtnListener(new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                
                dialog.dismiss();
                
                //wifi列表
                //Intent intent =  new Intent(Settings.ACTION_WIFI_SETTINGS);
                
                //移动网络设置
                //Intent intent =  new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);  
                
                //设置列表
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                startActivity(intent);
                
            }
        });
        
        //取消会进到断网视图
        networkConfirm.setLeftBtnListener(new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                
                dialog.dismiss();
                
            }
        });

        networkConfirm.show();
    }
    
    private void updateLoginState(int loginStatus)
    {
//    	if (!ActivityUtil.isRunningForeground(this)){
//    		return;
//    	}
//        
//        if (loginStatus == LoginHelper.LOGOUT_KICKOUT)
//        {
//        	Log.d("login",this.getLocalClassName() + "  LOGOUT_KICKOUT");
//            showOneButtonDialog(getString(R.string.sessoin_fail_for_kickout));
//            
//        }
//        else if (loginStatus == LoginHelper.LOGOUT_TIMEOUT)
//        {
//        	Log.d("login",this.getLocalClassName() + "  LOGOUT_TIMEOUT");
//
//            showOneButtonDialog(getString(R.string.sessoin_fail_for_timeout));
//            
//        }
//        else if (loginStatus == LoginHelper.STATUS_LOGOUT)
//        {
//        	Log.d("login",this.getLocalClassName() + "  STATUS_LOGOUT");
//
//            showOneButtonDialog(getString(R.string.sessoin_fail_for_timeout));
//            
//        }
//        else if (loginStatus == LoginHelper.LOGOUT_BY_USER)
//        {
//            
//            //用户自己登出， 不需要处理，在登出的时候处理
//            
//        }
//        else
//        {
//        	Log.d("login",this.getLocalClassName() + "  other logout");
//
//            showOneButtonDialog(getString(R.string.sessoin_fail_for_timeout));
//        }
        
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        
        super.onCreate(savedInstanceState);
        LogcatHelper lh = LogcatHelper.getInstance(this);
        lh.start(); 
        bindingView();
        // 将Activity放入堆栈中
        WifiWorldApplication.getInstance().activityCreated(this);
        LoginHelper.getInstance().addLogoutObserver(mLogoutListener);
        mTitlebar = new TitlebarHolder(this);
        networkConfirm = new XLTwoButtonDialog(this);        
    }
    
    protected void onStart()
    {
        
        super.onStart();
        GlobalBroadcast.registerBroadcastListener(mNetworkListener);
    }
    
    protected void onStop()
    {
        
        super.onStop();
        GlobalBroadcast.unregisterBroadcastListener(mNetworkListener);
        
        // 用于崩溃上报
        TestinAgent.onStop(this);
        
    }
    
    protected void onDestroy()
    {
        
        super.onDestroy();
        if (mLogoutListener != null)
        {
            LoginHelper.getInstance().removeLogoutObserver(mLogoutListener);
        }
        WifiWorldApplication.getInstance().activityDestroyed(this);
        LogcatHelper.getInstance(this).start(); 
    }
    
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
    }
    
    // 用于友盟统计
    protected void onResume()
    {
        
        super.onResume();
        MobclickAgent.onResume(this);
        
        // 用于崩溃上报
        TestinAgent.onResume(this);
    }
    
    // 用于友盟统计
    protected void onPause()
    {
        super.onPause();
        MobclickAgent.onPause(this);
    }
    
    public void showToast(String str)
    {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }
    
    public void showToast(int resId)
    {
        showToast(getString(resId));
    }
    
    public void bindingView()
    {
        View decorView = getWindow().getDecorView();
        ViewBinder.bindingView(decorView, this);
    }
    
    public void showWaitingDialog(String text)
    {
        if (null == waitingDialog)
        {
            waitingDialog = new XLWaitingDialog(this);
        }
        waitingDialog.setProHintStr(text);
        if (!waitingDialog.isShowing())
        {
            waitingDialog.show();
        }
    }
    
    public void showWaitingDialog(int resId)
    {
        showWaitingDialog(getString(resId));
    }
    
    public void dismissWaitingDialog()
    {
        if (null != waitingDialog && waitingDialog.isShowing())
        {
            waitingDialog.dismiss();
        }
    }
    
    private void showOneButtonDialog(final String content)
    {

        	 if (!isKick)
             {
                 isKick = true;
                 LoginHelper.getInstance().gotoLogin(BaseActivity.this, content);
             }
    }
    
    protected String formatNumByRange(double initValue)
    {
        
        if (initValue <= 0.0f)
        {
            return getResources().getString(R.string.zero);
        }
        else if (initValue < 0.001f)
        {
            return getResources().getString(R.string.small_to_dot001);
        }
        else
        {
            
            return String.format("%.3f", initValue);
        }
        
    }

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Toast.makeText(getApplicationContext(), "accccccccccccccc", Toast.LENGTH_LONG).show();
		super.onBackPressed();
	}
    
    
}
