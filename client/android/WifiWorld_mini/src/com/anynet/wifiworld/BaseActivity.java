package com.anynet.wifiworld;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;

import com.anynet.wifiworld.util.GlobalBroadcast;
import com.anynet.wifiworld.util.NetworkStateListener;
import com.anynet.wifiworld.util.ViewBinder;
import com.anynet.wifiworld.view.TitlebarHolder;
import com.umeng.analytics.MobclickAgent;

/** 
 * 如果acitivity 断网后自动返回登录界面 请继承这个activity
*/
public class BaseActivity extends FragmentActivity {
	protected final String TAG = this.getClass().getSimpleName();

	// 通用标题栏局部，子类需要先调用setContentView,在调用super.onCreate方法
	protected TitlebarHolder mTitlebar;

	//protected XLWaitingDialog waitingDialog;

	//private XLTwoButtonDialog networkConfirm;

	private boolean isKick = false;

	private NetworkStateListener mNetworkListener = new NetworkStateListener() {
		@Override
		public void onNetworkStateChange(Intent intent) {
		}
	};

	/** 网络重连时的操作 */
	protected void doNetworkConnected() {
	}

	/** 网络断开时的操作 */
	protected void doDisNetworkConnected() {
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 将Activity放入堆栈中
		WifiWorldApplication.getInstance().activityCreated(this);
		bindingView();
		mTitlebar = new TitlebarHolder(this);
	}

	protected void onStart() {

		super.onStart();
		GlobalBroadcast.registerBroadcastListener(mNetworkListener);
	}

	protected void onStop() {

		super.onStop();
		GlobalBroadcast.unregisterBroadcastListener(mNetworkListener);
	}

	protected void onDestroy() {
		super.onDestroy();
		WifiWorldApplication.getInstance().activityDestroyed(this);
	}

	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
	}

	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		MobclickAgent.onPageStart("MainScreen"); // 统计页面
	}

	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		MobclickAgent.onPageEnd("MainScreen");
	}

	public void showToast(String str) {
		showToast(str,false);
	}

	public void showToast(String str, boolean Long) {
		Toast.makeText(this, str, Long ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
	}

	public void showToast(int resId) {
		showToast(getString(resId),false);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}
	
	public void bindingView() {
		View decorView = getWindow().getDecorView();
		ViewBinder.bindingView(decorView, this);
	}

}
