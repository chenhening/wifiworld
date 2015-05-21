package com.anynet.wifiworld.wifi.ui;

import com.wolf.routermanager.bean.AllRouterInfoBean;
import com.wolf.routermanager.inter.ConnInfoCallBack;
import com.wolf.routermanager.login.CheckLogin;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

public class RouterLoginThread extends Thread {
	private final static String TAG = RouterLoginThread.class.getSimpleName();
	
	private Context mContext;
	private Handler mHandler;
	private CheckLogin mCheckLogin;
	private String LoginName;
	private String LoginPwd;
	
	public RouterLoginThread(Context context, Handler handler) {
		mContext = context;
		mHandler = handler;
		mCheckLogin = new CheckLogin(mContext);
	}

	public void setRouterAccount(String name, String pwd) {
		LoginName = name;
		LoginPwd = pwd;
	}
	
	@Override
	public void run() {
		if (LoginName == null || LoginName == "") {
			Toast.makeText(mContext, "路由器登录名不能为空", Toast.LENGTH_LONG).show();
			return;
		}
		
		mCheckLogin.login(LoginName, LoginPwd, new ConnInfoCallBack() {
			
			@Override
			public void putData(boolean flag) {
				if (!flag) {
					Toast.makeText(mContext, "Failed to login router", Toast.LENGTH_LONG).show();
					mHandler.sendEmptyMessage(WifiAdvanceActivity.LOGIN_FAILED);
				} else {
					mHandler.sendEmptyMessage(WifiAdvanceActivity.LOGIN_SUCCESSED);
				}
			}
		});
		super.run();
	}
}
