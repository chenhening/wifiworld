package com.anynet.wifiworld.me;

import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.BaseActivity;
import com.anynet.wifiworld.util.NetHelper;

public class MySettingActivity extends BaseActivity {

	// IPC
	private Intent mIntent = null;
	private Activity activity = this;

	private void bingdingTitleUI() {
		mTitlebar.ivHeaderLeft.setVisibility(View.VISIBLE);
		mTitlebar.tvHeaderRight.setVisibility(View.INVISIBLE);
		mTitlebar.tvTitle.setText("我的设置");
		mTitlebar.ivHeaderLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mIntent = getIntent();
		setContentView(R.layout.activity_my_setting);
		super.onCreate(savedInstanceState);
		bingdingTitleUI();

		//自动连接，默认关闭
		TextView connectAutoTV = (TextView)findViewById(R.id.tv_connect_auto);
		ToggleButton connectAuto = (ToggleButton)findViewById(R.id.tb_connect_auto);
		connectAuto.setChecked(false);
		connectAutoTV.setTextColor(Color.GRAY);
		connectAuto.setEnabled(false);
		
		//移动网络上网
		TextView mobileSwitchTV = (TextView)findViewById(R.id.tv_mobile_switch);
		ToggleButton mobileSwitch = (ToggleButton)findViewById(R.id.tb_mobile_switch);
		if (!hasSim()) {
			mobileSwitch.setChecked(false);
			mobileSwitch.setEnabled(false);
			mobileSwitchTV.setTextColor(Color.GRAY);
		} else if (NetHelper.isMobileNet(this)) {
			mobileSwitch.setChecked(true);
		} else {
			mobileSwitch.setChecked(false);
		}
		mobileSwitch.setOnCheckedChangeListener(new ToggleButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				setMobileData(getApplicationContext(), isChecked);
			}
		});
		
		//消息通知，默认开启
		TextView msgNotifyTV = (TextView)findViewById(R.id.tv_msg_notify);
		ToggleButton msgNotify = (ToggleButton)findViewById(R.id.tb_msg_notify);
		msgNotify.setChecked(true);
		msgNotifyTV.setTextColor(Color.GRAY);
		msgNotify.setEnabled(false);
	}
	
	private boolean hasSim() {
		boolean hasSim = false;
		//需考虑欠费上不了网的情况
		TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        switch (manager.getSimState()) {
        case TelephonyManager.SIM_STATE_READY:
        	hasSim = true;
            break;
        case TelephonyManager.SIM_STATE_ABSENT:
        	hasSim = false;
            break;
        default:
            Log.d(TAG, "SIM卡被锁定或未知状态");
            hasSim = false;
            break;
        }
        return hasSim;
	}
	
	/**
	 * 设置手机的移动数据
	 */
	public static void setMobileData(Context pContext, boolean pBoolean) {
	    try {
	        ConnectivityManager mConnectivityManager = (ConnectivityManager) pContext.getSystemService(Context.CONNECTIVITY_SERVICE);  
	        Class ownerClass = mConnectivityManager.getClass();
	        Class[] argsClass = new Class[1];
	        argsClass[0] = boolean.class;
	        Method method = ownerClass.getMethod("setMobileDataEnabled", argsClass);
	        method.invoke(mConnectivityManager, pBoolean);
	    } catch (Exception e) {
	        e.printStackTrace();
	        System.out.println("移动数据设置错误: " + e.toString());
	    }
	}
	
	/**
	 * 返回手机移动数据的状态
	 *
	 * @param pContext
	 * @param arg
	 *            默认填null
	 * @return true 连接 false 未连接
	 */
	public static boolean getMobileDataState(Context pContext, Object[] arg) {
	    try {
	        ConnectivityManager mConnectivityManager = (ConnectivityManager) pContext.getSystemService(Context.CONNECTIVITY_SERVICE);  
	        Class ownerClass = mConnectivityManager.getClass();
	        Class[] argsClass = null;
	        if (arg != null) {
	            argsClass = new Class[1];
	            argsClass[0] = arg.getClass();
	        }
	        Method method = ownerClass.getMethod("getMobileDataEnabled", argsClass);
	        Boolean isOpen = (Boolean) method.invoke(mConnectivityManager, arg);
	        return isOpen;
	    } catch (Exception e) {
	        System.out.println("得到移动数据状态出错");
	        return false;
	    }
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub

		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		InputMethodManager imm = ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE));
		View v = getCurrentFocus();
		if (imm != null && v != null) {
			imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}
}
