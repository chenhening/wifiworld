package com.anynet.wifiworld;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import cn.bmob.v3.Bmob;
import cn.smssdk.SMSSDK;

import com.anynet.wifiworld.app.BaseActivity;
import com.anynet.wifiworld.app.BaseFragment;
import com.anynet.wifiworld.config.GlobalConfig;
import com.anynet.wifiworld.dao.DBHelper;
import com.anynet.wifiworld.map.MapFragment;
import com.anynet.wifiworld.me.MeFragment;
import com.anynet.wifiworld.util.AppInfoUtil;
import com.anynet.wifiworld.util.HandlerUtil.MessageListener;
import com.anynet.wifiworld.util.HandlerUtil.StaticHandler;
import com.anynet.wifiworld.util.LocationHelper;
import com.anynet.wifiworld.util.LoginHelper;
import com.anynet.wifiworld.util.NetHelper;
import com.anynet.wifiworld.wifi.WifiFragment;
import com.avos.avoscloud.AVOSCloud;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.IUmengUnregisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.update.UmengUpdateAgent;

//import com.anynet.wifiworld.discover.DiscoverFragment;

public class MainActivity extends BaseActivity implements MessageListener {

	private static final String TAG = MainActivity.class.getSimpleName();

	public FragmentTabHost mTabHost;
	private Button[] mTabs;
	private WifiFragment wifiFragment;
	private MapFragment mapFragment;
	// private DiscoverFragment discoverFragment;
	private MeFragment meFragment;
	private MainFragment[] fragments;
	private int index;
	// 当前fragment的index
	private int currentTabIndex;
	private DBHelper dbHelper;
	private ImageView ivMyNew;
	private StaticHandler handler = new StaticHandler(this);
	private PushAgent mPushAgent;
	// global instance
	private static LoginHelper mLoginHelper;
	private LocationHelper mLocationHelper;

	private final static int TAB_COUNTS = 3;
	private final static int CONNECT_TAB_IDX = 0;
	private final static int NEARBY_TAB_IDX = 1;
	// private final static int FIND_TAB_IDX = 2;
	private final static int MY_TAB_IDX = 2;

	public static void startActivity(BaseActivity baseActivity, boolean isFromWelcomeActivity) {
		Intent i = new Intent(baseActivity, MainActivity.class);
		i.putExtra("isFromWelcomeActivity", isFromWelcomeActivity);
		if (isFromWelcomeActivity) {
			i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		}
		baseActivity.startActivity(i);
		baseActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		initFragments();
		super.onCreate(savedInstanceState);

		// 友盟自动更新
		SMSSDK.initSDK(this, GlobalConfig.SMSSDK_KEY, GlobalConfig.SMSSDK_SECRECT);
		Bmob.initialize(this, GlobalConfig.BMOB_KEY);
		UmengUpdateAgent.update(this);
		FeedbackAgent agent = new FeedbackAgent(this);
		agent.sync();

		mLoginHelper = LoginHelper.getInstance(this);
		// mLoginHelper.AutoLogin();
		mLocationHelper = LocationHelper.getInstance(this);
		dbHelper = DBHelper.getInstance(this);

		Intent intent = getIntent();
		intent.getBooleanExtra("isFromWelcomeActivity", false);
		setContentView(R.layout.activity_main);
		initView();		

		FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
		// 添加显示第一个fragment
		for (int i = 0; i < fragments.length; i++) {
			if (!fragments[i].isAdded()) {
				trx.add(R.id.fragment_container, fragments[i]).hide(fragments[i]);
			}
		}
//		if (!fragments[0].isAdded()) {
//			trx.add(R.id.fragment_container, fragments[0]).hide(fragments[0]);
//		}
		trx.add(R.id.fragment_container, fragments[0]);
		trx.show(fragments[0]).commit();

		// 打开友盟推送
		mPushAgent = PushAgent.getInstance(this);
		mPushAgent.onAppStart();
		mPushAgent.enable(mRegisterCallback);
		// Dianle SDK provision
		// Data.initGoogleContext(this, "072cb4d9d9d5dfd23ed2981e5e33fe59");
		// Data.setCurrentUserID(this, "123456789");

		changeToConnect();
	}

	private void initFragments() {
		if (wifiFragment == null)
			wifiFragment = new WifiFragment();
		if (mapFragment == null)
			mapFragment = new MapFragment();
		// discoverFragment = new DiscoverFragment();
		if (meFragment == null)
			meFragment = new MeFragment();

		if (fragments == null || fragments.length < 2)
			fragments = new MainFragment[] { wifiFragment, mapFragment, /*
																		 * discoverFragment
																		 * ,
																		 */meFragment };
	}

	@Override
	public void onAttachFragment(Fragment fragment) {
		// TODO Auto-generated method stub
		super.onAttachFragment(fragment);
		Log.e(TAG, "onAttachFragment");
//		if (fragments == null) {
//			fragments = new MainFragment[3];
//		}wifiFragment == null && 
		if (fragment instanceof WifiFragment) {
			wifiFragment = (WifiFragment) fragment;
			fragments[CONNECT_TAB_IDX] = wifiFragment;
		}//mapFragment == null && 
		if (fragment instanceof MapFragment) {
			mapFragment = (MapFragment) fragment;
			fragments[NEARBY_TAB_IDX] = mapFragment;
		}
		// discoverFragment = new DiscoverFragment();meFragment == null && 
		if (fragment instanceof MeFragment) {
			meFragment = (MeFragment) fragment;
			fragments[MY_TAB_IDX] = meFragment;
		}
		reflesh();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void doDisNetworkConnected() {

		super.doDisNetworkConnected();
		// crystalFragment.stopUpdte();
	}

	@Override
	protected void doNetworkConnected() {
		// TODO Auto-generated method stub
		super.doNetworkConnected();
		if (index == 0) {
			// crystalFragment.startUpdte();
		}

	}

	/** 初始化组件 */
	private void initView() {
		mTabs = new Button[TAB_COUNTS];
		mTabs[CONNECT_TAB_IDX] = (Button) findViewById(R.id.btn_connect);
		mTabs[NEARBY_TAB_IDX] = (Button) findViewById(R.id.btn_nearby);
		// mTabs[FIND_TAB_IDX] = (Button) findViewById(R.id.btn_find);
		mTabs[MY_TAB_IDX] = (Button) findViewById(R.id.btn_my);
		ivMyNew = (ImageView) findViewById(R.id.iv_my_new);
	}

	/**
	 * button点击事件
	 * 
	 * @param view
	 */
	public void onTabClicked(View view) {
		switch (view.getId()) {
		case R.id.btn_connect:
			changeToConnect();
			break;
		case R.id.btn_nearby:
			chageToNearby();
			break;
		// case R.id.btn_find:
		// chageToFind();
		// ReportUtil.reportClickTabWithDraw(this);
		// break;
		case R.id.btn_my:
			chageToMy();
			break;
		}

	}

	private void changeToConnect() {
		index = CONNECT_TAB_IDX;
		reflesh();

	}

	private void chageToNearby() {
		index = NEARBY_TAB_IDX;
		reflesh();

	}

	// private void chageToFind() {
	// index = FIND_TAB_IDX;
	// reflesh();
	//
	// }

	private void chageToMy() {
		index = MY_TAB_IDX;
		reflesh();
	}

	private void reflesh() {
		FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
		if (currentTabIndex != index) {
			trx.hide(fragments[currentTabIndex]);
			if (!fragments[index].isAdded()) {
				trx.add(R.id.fragment_container, fragments[index]);
			}
			//fragments[index].onResume();
			trx.show(fragments[index]).commit();

			// 因为使用show和hide方法切换Fragment不会Fragment触发onResume/onPause方法回调，所以直接需要手动去更新一下状态
			if (NetHelper.isNetworkAvailable(getApplicationContext())) {

				for (int i = 0; i < fragments.length; i++) {
					if (i == index) {
						fragments[i].startUpdte();
					} else {
						fragments[i].stopUpdte();
					}
				}
			}
		}
		mTabs[currentTabIndex].setSelected(false);
		// 把当前tab设为选中状态
		mTabs[index].setSelected(true);
		currentTabIndex = index;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
				if (fragments[currentTabIndex].onBackPressed()) {
					return true;
				}
			}
			return super.dispatchKeyEvent(event);
		} else {
			return super.dispatchKeyEvent(event);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mPushAgent.disable();
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("MainScreen"); // 统计页面
		// reflesh();
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("MainScreen");
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
	}

	private int retryCnt = 0;

	private void reportDeviceToken(final String appVersion, final String deviceToken) {
		final int version = 1; // 接口版本
		final int type = 1; // 1 android; 2 ios appstore; 3 ios 黑
		Runnable reportDeviceTokenRunnable = new Runnable() {
			@Override
			public void run() {
				// AppRestClient.reportDeviceToken(version, type, appVersion,
				// deviceToken, new ResponseCallback<DeviceTokenResp>(
				// WifiWorldApplication.getInstance()) {
				// public void onSuccess(JSONObject paramJSONObject,
				// DeviceTokenResp deviceTokenResp) {
				// // 返回数据ok则更新
				// if (deviceTokenResp.isOK()) {
				//
				// }
				// }
				//
				// public void onFailure(int paramInt,
				// Throwable paramThrowable) {
				// /** 网络错误 */
				// // XLLog.e(TAG, paramThrowable.toString());
				// if (retryCnt < 5) {
				// retryCnt++;
				// run();
				// }
				// }
				// });
			}
		};
		handler.post(reportDeviceTokenRunnable);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			moveTaskToBack(true);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void handleMessage(Message msg) {
		// Auto-generated method stub

	}

	public static class MainFragment extends BaseFragment {

		public boolean checkIsLogined() {
			if (!mLoginHelper.isLogined()) {
				UserLoginActivity.start((BaseActivity) getActivity());
				return false;
			}
			return true;
		}

		public boolean isLogined() {
			if (!mLoginHelper.isLogined()) {
				return false;
			}
			return true;
		}

		
		public void startUpdte() {
			com.anynet.wifiworld.util.XLLog.log(TAG, "startUpdte");
		}

		public void stopUpdte() {
			com.anynet.wifiworld.util.XLLog.log(TAG, "stopUpdte");
		}
	}

	public IUmengRegisterCallback mRegisterCallback = new IUmengRegisterCallback() {

		@Override
		public void onRegistered(String registrationId) {
			handler.post(new Runnable() {

				@Override
				public void run() {
					String device_token = mPushAgent.getRegistrationId();
					String appVersion = AppInfoUtil.getVersionName(MainActivity.this);
					reportDeviceToken(appVersion, device_token);
					return;
				}
			});
		}
	};

	public IUmengUnregisterCallback mUnregisterCallback = new IUmengUnregisterCallback() {

		@Override
		public void onUnregistered(String registrationId) {
			// TODO Auto-generated method stub
			handler.post(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
				}
			});
		}
	};
}
