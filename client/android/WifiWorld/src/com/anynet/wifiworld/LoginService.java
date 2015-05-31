/**
 * 
 */
package com.anynet.wifiworld;

import java.util.Timer;
import java.util.TimerTask;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import com.anynet.wifiworld.config.GlobalConfig;
import com.anynet.wifiworld.data.UserProfile;
import com.anynet.wifiworld.util.LoginHelper;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/** @author Yan */
public class LoginService extends Service {
	private static final String TAG = "LoginService";
	private IBinder binder = new LoginService.LocalBinder();
	private EventHandler mEventHandler;
	public static final String LOGIN_SERVICE_KEY = "com.anynet.wifiworld.loginservice.key";
	public static final String LOGIN_SERVICE_EVENT_GET_VERIFICATION_CODE = "com.anynet.wifiworld.loginservice.getverificode";
	public static final String LOGIN_SERVICE_EVENT_SUBMIT_VERIFICATION_CODE = "com.anynet.wifiworld.loginservice.sumbitverificode";
	public static final String LOGIN_SERVICE_EVENT_ERROR = "com.anynet.wifiworld.loginservice.error";

	public static final int LOGIN_SERVICE_INIT = 100000;
	public static final int LOGIN_SERVICE_GET_VERIFICODE = 100001;
	public static final int LOGIN_SERVICE_SUMBIT_VERIFICODE = 100002;
	public static final int LOGIN_SERVICE_DESTORY = 200000;
	String mPhone_code = null;
	String mPhoneNumber = null;
	String mSmsCode = null;
	LoginHelper mLoginHelper;
	//public static int CountDown = 0;
	//private TimerTask mTask;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		int actionID = intent.getIntExtra(LOGIN_SERVICE_KEY, LOGIN_SERVICE_INIT);
		Log.e(TAG,"action ID:"+actionID);
		switch (actionID) {
		case LOGIN_SERVICE_INIT: {
		}
			break;
		case LOGIN_SERVICE_GET_VERIFICODE: {
			mPhone_code = intent.getStringExtra("code");
			mPhoneNumber = intent.getStringExtra("phone");
			getVerificationCode(mPhone_code, mPhoneNumber);
		}
			break;
		case LOGIN_SERVICE_SUMBIT_VERIFICODE: {
			mPhone_code = intent.getStringExtra("code");
			mPhoneNumber = intent.getStringExtra("phone");
			mSmsCode = intent.getStringExtra("smscode");
			submitVerificationCode(mPhone_code, mPhoneNumber, mSmsCode);
		}

			break;
		case LOGIN_SERVICE_DESTORY: {
			//CountDown = -1;
			stopSelf();
		}

			break;
		default:
			break;
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		// initialize sms
		//Log.e(TAG,"onCreate:");
		//CountDown = 60;
		mLoginHelper = LoginHelper.getInstance(getApplicationContext());
		mEventHandler = new EventHandler() {
			public void afterEvent(int event, int result, Object data) {
				if (result == SMSSDK.RESULT_COMPLETE) {
					// 回调完成
					if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
						// 提交验证码成功
						//UserProfile user = new UserProfile();
						//user.setUsername(mPhoneNumber);
						//user.setPassword(mSmsCode);
						//mLoginHelper.Login(user);
						LoginService.this.sendBroadcast(new Intent(LOGIN_SERVICE_EVENT_SUBMIT_VERIFICATION_CODE));
						//showToast("服务器验证成功，正在登陆......");
					} else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
						LoginService.this.sendBroadcast(new Intent(LOGIN_SERVICE_EVENT_GET_VERIFICATION_CODE));
					} else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
						// 返回支持发送验证码的国家列表
					}
				} else {
					((Throwable) data).printStackTrace();
					LoginService.this.sendBroadcast(new Intent(LOGIN_SERVICE_EVENT_ERROR));
				}
			}
		};
		// 注册回调监听接口
		SMSSDK.registerEventHandler(mEventHandler);
	}

	//Timer timer = new Timer();

	private void getVerificationCode(String mPhone_code, String mPhoneNumber) {
		// 获取验证码
		SMSSDK.getVerificationCode(mPhone_code, mPhoneNumber);
		//Log.e(TAG,"getVerificationCode:"+mPhone_code+" "+mPhoneNumber);
		//mTask = new TimerTask() {

		//	@Override
		//	public void run() {
		//		if (CountDown <= 0) {
		//			mTask.cancel();
		//		} else {
		//		}
		//		--CountDown;
		//	}
		//};
		//CountDown = 60;
		//timer.schedule(mTask, 0);
	}

	private void submitVerificationCode(String mPhone_code, String mPhoneNumber, String mSmsCode) {
		//Log.e(TAG,"getVerificationCode:"+mPhone_code+" "+mPhoneNumber+"  "+mSmsCode);
		SMSSDK.submitVerificationCode(mPhone_code, mPhoneNumber, mSmsCode);
		//LoginService.this.sendBroadcast(new Intent(LOGIN_SERVICE_EVENT_SUBMIT_VERIFICATION_CODE));
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		//Log.e(TAG,"onDestroy:");
		super.onDestroy();
		SMSSDK.unregisterEventHandler(mEventHandler);
	}

	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		Log.e(TAG,"onLowMemory:");
		super.onLowMemory();
	}

	@Override
	public void onRebind(Intent intent) {
		// TODO Auto-generated method stub
		Log.e(TAG,"onRebind:");
		super.onRebind(intent);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		Log.e(TAG,"onUnbind:");
		return super.onUnbind(intent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	// 定义内容类继承Binder
	public class LocalBinder extends Binder {
		// 返回本地服务
		LoginService getService() {
			return LoginService.this;
		}
	}

}
