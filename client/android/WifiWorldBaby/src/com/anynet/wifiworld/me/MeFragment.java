package com.anynet.wifiworld.me;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.Bmob;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import com.anynet.wifiworld.MainActivity.MainFragment;
import com.anynet.wifiworld.R.id;
import com.anynet.wifiworld.R.layout;
import com.anynet.wifiworld.R.string;
import com.anynet.wifiworld.MainActivity;
import com.anynet.wifiworld.R;
import com.anynet.wifiworld.app.WifiWorldApplication;

import cn.smssdk.gui.RegisterPage;

public class MeFragment extends MainFragment {
	private String BMOB_KEY = "b20905c46c6f0ae1edee547057f04589";
	private String SMSSDK_KEY = "5ea9dee43eb2";
	private String SMSSDK_SECRECT = "6f332e8768e0fe21509cddbe804f016b";
	
	//for SMSSDK
	private EventHandler mEventHandler;
	private int mVerifyTime = 60;
	private TimerTask mTask;

	private void bingdingTitleUI(boolean isLogin) {
		mTitlebar.ivHeaderLeft.setVisibility(View.INVISIBLE);
		mTitlebar.llFinish.setVisibility(View.VISIBLE);
		mTitlebar.llHeaderMy.setVisibility(View.INVISIBLE);
		mTitlebar.tvHeaderRight.setVisibility(View.INVISIBLE);
		if (isLogin)
			mTitlebar.tvTitle.setText(getString(R.string.login_login));
		else
			mTitlebar.tvTitle.setText(getString(R.string.my));
	}

	private void bingdingTitleUI() {
		bingdingTitleUI(false);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// 初始化 Bmob SDK
		Bmob.initialize(getActivity(), BMOB_KEY);
		// initialize sms
		SMSSDK.initSDK(getActivity(), SMSSDK_KEY, SMSSDK_SECRECT);
		mEventHandler = new EventHandler() {
			public void afterEvent(int event, int result, Object data) {
				if (result == SMSSDK.RESULT_COMPLETE) {
	                //回调完成
	                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
	                //提交验证码成功
	                }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
	                	getActivity().runOnUiThread(new Runnable() {
	                		@Override  
                            public void run() { 
	                			showToast("获取验证码成功，请输入验证码点击登陆.");
	                		}
	                	});
	                //获取验证码成功
	                }else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){
	                //返回支持发送验证码的国家列表
	                } 
				 }else{                                                                 
					 ((Throwable)data).printStackTrace(); 
				 }
			}
		};
		// 注册回调监听接口
		SMSSDK.registerEventHandler(mEventHandler);
	}
	
	@Override
	public void onDestroy() {
		//registerEventHandler必须和unregisterEventHandler配套使用，否则可能造成内存泄漏。
		SMSSDK.unregisterEventHandler(mEventHandler);
		super.onDestroy();
	}

	@Override
	public View onCreateView(
			LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// hybrid
		boolean isLogin = WifiWorldApplication.isLogin();
		if (!isLogin) {
			mPageRoot = inflater.inflate(R.layout.login, null);
			mPageRoot.findViewById(R.id.button_sms).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String phone_number =((EditText)
						mPageRoot.findViewById(R.id.tv_login_account)).getText().toString().trim();
					Pattern pattern = Pattern.compile("^1[3|4|5|7|8][0-9]{9}$");
					if (!pattern.matcher(phone_number).find()) {
						showToast("请输入11位手机正确号码.");
						return;
					}
					
					//发送验证码
					SMSSDK.getVerificationCode("86", phone_number);
					
					//按钮进入60秒倒计时
					final TextView tv_verify = 
						(TextView)mPageRoot.findViewById(R.id.tv_button_sms);
					tv_verify.setEnabled(false);
					final String verify_txt = getString(R.string.smschecknum);
					final Timer timer = new Timer();
					mTask = new TimerTask() {

                        @Override  
                        public void run() {  
                            getActivity().runOnUiThread(new Runnable() {
                                @Override  
                                public void run() {  
                                    if (mVerifyTime <= 0) {  
                                    	tv_verify.setEnabled(true);  
                                    	tv_verify.setText(verify_txt);  
                                    	mTask.cancel();  
                                    } else {  
                                    	tv_verify.setText(verify_txt + "(" + mVerifyTime + ")"); 
                                    }
                                    --mVerifyTime;
                                }  
                            });  
                        }  
                    };
                    mVerifyTime = 60;
                    timer.schedule(mTask, 0, 1000);  
				}
			});
			mPageRoot.findViewById(R.id.button_login).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					SMSSDK.submitVerificationCode("+86", 
						((EditText) mPageRoot.findViewById(R.id.tv_login_account)).getText().toString().trim(), 
						((EditText) mPageRoot.findViewById(R.id.tv_login_sms)).getText().toString().trim());
				}
			});
			// SystemWebView webView =
			// (SystemWebView)mPageRoot.findViewById(R.id.cordovaWebView);
			// cordovaWebView = new CordovaWebViewImpl(getActivity(), new
			// SystemWebViewEngine(webView));
			// Config.init(this.getActivity());
			// cordovaWebView.init(this, Config.getPluginEntries(),
			// Config.getPreferences());
			// cordovaWebView.loadUrl("file:///android_asset/www/index.html");
			super.onCreateView(inflater, container, savedInstanceState);
			bingdingTitleUI(true);
			return mPageRoot;
		} else {
			mPageRoot = inflater.inflate(R.layout.fragment_me, null);
			super.onCreateView(inflater, container, savedInstanceState);
			bingdingTitleUI();
			return mPageRoot;
		}
	}

	// ---------------------------------------------------------------------------------------------
	// for click event
	View.OnClickListener btn_listener_ = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.button_login:
				// 打开注册页面
				RegisterPage registerPage = new RegisterPage();
				registerPage.setRegisterCallback(new EventHandler() {
					public void afterEvent(int event, int result, Object data) {
						// 解析注册结果
						if (result == SMSSDK.RESULT_COMPLETE) {
							HashMap<String, Object> phoneMap = (HashMap<String, Object>) data;
							String country = (String) phoneMap.get("country");
							String phone = (String) phoneMap.get("phone");
							// 提交用户信息
							// registerUser(country, phone);
						}
					}
				});
				registerPage.show(getActivity());
				break;
			}
		}

	};
}
