package com.anynet.wifiworld;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import cn.bmob.v3.Bmob;

import com.dlnetwork.Data;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.IUmengUnregisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.update.UmengUpdateAgent;

//import com.xunlei.common.member.XLLog;
import com.anynet.wifiworld.api.AppRestClient;
import com.anynet.wifiworld.api.callback.ResponseCallback;
import com.anynet.wifiworld.app.BaseActivity;
import com.anynet.wifiworld.app.BaseFragment;
import com.anynet.wifiworld.app.WifiWorldApplication;
import com.anynet.wifiworld.bean.Msg;
import com.anynet.wifiworld.bean.SystemMsgResp;
import com.anynet.wifiworld.config.GlobalConfig;
import com.anynet.wifiworld.constant.Const;
import com.anynet.wifiworld.dao.DBHelper;
import com.anynet.wifiworld.discover.DiscoverFragment;
import com.anynet.wifiworld.map.MapFragment;
import com.anynet.wifiworld.me.MeFragment;
import com.anynet.wifiworld.report.ReportUtil;
import com.anynet.wifiworld.util.HandlerUtil.MessageListener;
import com.anynet.wifiworld.util.HandlerUtil.StaticHandler;
import com.anynet.wifiworld.util.AppInfoUtil;
import com.anynet.wifiworld.util.LocationHelper;
import com.anynet.wifiworld.util.LoginHelper;
import com.anynet.wifiworld.util.NetHelper;
import com.anynet.wifiworld.util.PreferenceHelper;
import com.anynet.wifiworld.wifi.WifiFragment;
import com.anynet.wifiworld.R;

public class MainActivity extends BaseActivity implements MessageListener {

	private static final String TAG = MainActivity.class.getSimpleName();

	public FragmentTabHost mTabHost;
	private Button[] mTabs;
	private WifiFragment wifiFragment;
	private MapFragment mapFragment;
	private DiscoverFragment discoverFragment;
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

	public static void startActivity(BaseActivity baseActivity, boolean isFromWelcomeActivity) {
		Intent i = new Intent(baseActivity, MainActivity.class);
		i.putExtra("isFromWelcomeActivity", isFromWelcomeActivity);
		if (isFromWelcomeActivity) {
			i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		}
		baseActivity.startActivity(i);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		mLoginHelper = LoginHelper.getInstance(this);
		mLoginHelper.getClass();
		mLoginHelper.AutoLogin();
		mLocationHelper = LocationHelper.getInstance(this);
		mLocationHelper.getClass();
		dbHelper = DBHelper.getInstance(this);

		Bmob.initialize(this, GlobalConfig.BMOB_KEY);

		Intent i = getIntent();
		i.getBooleanExtra("isFromWelcomeActivity", false);
		setContentView(R.layout.activity_main);
		initView();

		wifiFragment = new WifiFragment();
		mapFragment = new MapFragment();
		discoverFragment = new DiscoverFragment();
		meFragment = new MeFragment();

		fragments = new MainFragment[] { wifiFragment, mapFragment, discoverFragment, meFragment };
		// 添加显示第一个fragment
		getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, wifiFragment)
				.add(R.id.fragment_container, mapFragment).add(R.id.fragment_container, discoverFragment)
				.add(R.id.fragment_container, meFragment).hide(mapFragment).hide(discoverFragment).hide(meFragment)
				.show(wifiFragment).commit();

		// 友盟自动更新
		UmengUpdateAgent.update(this);

		// 打开友盟推送
		mPushAgent = PushAgent.getInstance(this);
		mPushAgent.onAppStart();
		mPushAgent.enable(mRegisterCallback);
		String device_token = mPushAgent.getRegistrationId();
		String appVersion = AppInfoUtil.getVersionName(this);
/*		if (null != device_token && !device_token.equals("")) {
			reportDeviceToken(appVersion, device_token);
		}
*/
		// Dianle SDK provision
		Data.initGoogleContext(this, "072cb4d9d9d5dfd23ed2981e5e33fe59");
		Data.setCurrentUserID(this, "123456789");

		changeToConnect();

		updateData();
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
		mTabs = new Button[4];
		mTabs[0] = (Button) findViewById(R.id.btn_connect);
		mTabs[1] = (Button) findViewById(R.id.btn_nearby);
		mTabs[2] = (Button) findViewById(R.id.btn_find);
		mTabs[3] = (Button) findViewById(R.id.btn_my);
		ivMyNew = (ImageView) findViewById(R.id.iv_my_new);
	}

	/** button点击事件
	 * 
	 * @param view */
	public void onTabClicked(View view) {
		switch (view.getId()) {
		case R.id.btn_connect:
			changeToConnect();
			ReportUtil.reportClickTabDig(this);
			break;
		case R.id.btn_nearby:
			chageToNearby();
			ReportUtil.reportClickTabWithDraw(this);
			break;
		case R.id.btn_find:
			chageToFind();
			ReportUtil.reportClickTabWithDraw(this);
			break;
		case R.id.btn_my:
			chageToMy();
			ReportUtil.reportClickTabWithMY(this);
			break;
		}

	}

	private void changeToConnect() {
		index = 0;
		reflesh();

	}

	private void chageToNearby() {
		index = 1;
		reflesh();

	}

	private void chageToFind() {
		index = 2;
		reflesh();

	}

	private void chageToMy() {
		index = 3;
		reflesh();
	}

	private void reflesh() {
		if (currentTabIndex != index) {
			FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
			trx.hide(fragments[currentTabIndex]);
			if (!fragments[index].isAdded()) {
				trx.add(R.id.fragment_container, fragments[index]);
			}
			// fragments[index].onResume();
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
		// 红点消失
		if (PreferenceHelper.getInstance().getBoolean(Const.SYS_NEW_MSG, false)) {
			ivMyNew.setVisibility(View.VISIBLE);
		} else {
			ivMyNew.setVisibility(View.INVISIBLE);
		}
		MobclickAgent.onPageStart("MainScreen"); // 统计页面
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

	/** 获取系统推送 */
	private void updateData() {

		getSystemMsg(dbHelper.getMaxServId());

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

	/** 查询水晶信息 */
	private void getSystemMsg(long maxServId) {

		// 通过上拉刷新方法 所有数据都插入数据库中
		AppRestClient.getSystemMsg(maxServId, 0, Const.PAGE_SIZE, new ResponseCallback<SystemMsgResp>(
				WifiWorldApplication.getInstance()) {

			public void onSuccess(JSONObject paramJSONObject, SystemMsgResp systemMsgResp) {
				// 返回数据ok则更新
				if (systemMsgResp.isOK()) {
					if (systemMsgResp.getMsg().length > 0) {
						ivMyNew.setVisibility(View.INVISIBLE);

						long maxSysMsgId = PreferenceHelper.getInstance().getLong(Const.MAX_SYS_MSG_ID, 0);
						for (Msg msg : systemMsgResp.getMsg()) {
							// 新消息
							if (msg.getId() > maxSysMsgId) {
								ivMyNew.setVisibility(View.VISIBLE);
								PreferenceHelper.getInstance().setBoolean(Const.SYS_NEW_MSG, true);
								break;
							}
						}
					} else {
						ivMyNew.setVisibility(View.INVISIBLE);
					}

				} else {
					// 业务错误
					// XLLog.e(TAG, systemMsgResp.getReturnDesc());
					ivMyNew.setVisibility(View.INVISIBLE);
				}

			}

			public void onFailure(int paramInt, Throwable paramThrowable) {
				// 网络问题
				// XLLog.e(TAG, paramThrowable.toString());
				ivMyNew.setVisibility(View.INVISIBLE);
			}

		});

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
