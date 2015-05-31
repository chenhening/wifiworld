package com.anynet.wifiworld.wifi.ui;

import com.wolf.routermanager.bean.AllRouterInfoBean;
import com.wolf.routermanager.inter.ConnInfoCallBack;
import com.wolf.routermanager.login.CheckLogin;

import android.content.Context;
import android.widget.Toast;

public class RouterLoginThread {
	private final static String TAG = RouterLoginThread.class.getSimpleName();
	
	private Context mContext;
	private CheckLogin mCheckLogin;
	private String LoginName;
	private String LoginPwd;
	
	public RouterLoginThread(Context context) {
		mContext = context;
		mCheckLogin = new CheckLogin(mContext);
	}

	public void setRouterAccount(String name, String pwd) {
		LoginName = name;
		LoginPwd = pwd;
	}
	
	public interface LoginRouterCallback {
		public void onLoginSuccess();
		public void onLoginFailed();
	}
	
	public void run(final LoginRouterCallback loginRouterCallback) {
		if (LoginName == null || LoginName == "") {
			Toast.makeText(mContext, "路由器登录名不能为空", Toast.LENGTH_LONG).show();
			return;
		}
		
		if (AllRouterInfoBean.hasLogin) {
			loginRouterCallback.onLoginSuccess();
			return;
		}
		
		mCheckLogin.login(LoginName, LoginPwd, new ConnInfoCallBack() {
			
			@Override
			public void putData(boolean flag) {
				if (!flag) {
					Toast.makeText(mContext, "Failed to login router", Toast.LENGTH_LONG).show();
					loginRouterCallback.onLoginFailed();
				} else {
					loginRouterCallback.onLoginSuccess();
				}
			}
		});
	}
}
