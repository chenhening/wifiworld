package com.anynet.wifiworld.me;

import java.util.Timer;
import java.util.TimerTask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.anynet.wifiworld.BaseActivity;
import com.anynet.wifiworld.BaseFragment.MainFragment;
import com.anynet.wifiworld.R;
import com.anynet.wifiworld.data.WifiProfile;
import com.anynet.wifiworld.dialog.WifiConnectDialog;
import com.anynet.wifiworld.dialog.WifiConnectDialog.DialogType;
import com.anynet.wifiworld.provider.WifiProviderRigisterActivity;
import com.anynet.wifiworld.provider.WifiProviderSettingActivity;
import com.anynet.wifiworld.util.BitmapUtil;
import com.anynet.wifiworld.util.LoginHelper;

public class MeFragment extends MainFragment {
	private final static String TAG = MeFragment.class.getSimpleName();
	
	private LoginHelper mLoginHelper;
	private WifiProfile mWifiProfile;
	
	BroadcastReceiver loginBR = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			boolean isLogined = false;
			String action = intent.getAction();
			if (action.equals(LoginHelper.AUTO_LOGIN_SUCCESS)) {
				isLogined = true;
			}else if (action.equals(LoginHelper.LOGIN_OUT)) {
				isLogined = false;
			}
			setLoginedUI(isLogined);
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// 监听登录
		IntentFilter filter = new IntentFilter();
		filter.addAction(LoginHelper.LOGIN_SUCCESS);
		filter.addAction(LoginHelper.LOGIN_FAIL);
		filter.addAction(LoginHelper.LOGIN_OUT);
		getActivity().registerReceiver(loginBR, filter);

		mLoginHelper = LoginHelper.getInstance(getActivity());
	}
	
	@Override
	public void onDestroy() {
		getActivity().unregisterReceiver(loginBR);
		super.onDestroy();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		mPageRoot = inflater.inflate(R.layout.fragment_me, null);

		return mPageRoot;
	}
	
	private boolean mLoaded = false;
	@Override
    public void startUpdte() {
		if (!mLoaded) {
			setLoginedUI(mLoginHelper.isLogined());
			
			mPageRoot.findViewById(R.id.login_text).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (!mLoginHelper.isLogined()) {
						UserLoginActivity.start((BaseActivity) getActivity());
					} else {
						Intent i = new Intent();
						i.setClass(getActivity(), MyAccountActivity.class);
						getActivity().startActivity(i);
					}
				}
			});
			
			// 设置about
			this.findViewById(R.id.slv_about_app).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent i = new Intent();
					i.setClass(getApplicationContext(), AboutAppActivity.class);
					startActivity(i);
				}

			});
			
			// 点击进入account
			mPageRoot.findViewById(R.id.person_icon).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent i = new Intent(getApplicationContext(), MyAccountActivity.class);
					startActivity(i);
				}
			});
			
			// 点击查看WiFi提供者信息
			mPageRoot.findViewById(R.id.slv_i_am_wifi_provider).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					// 查询是否登录
					if (!checkIsLogined()) {
						return;
					}

					mWifiProfile = mLoginHelper.mWifiProfile;
					if (mWifiProfile != null) {
						Intent i = new Intent(getApplicationContext(), WifiProviderSettingActivity.class);
						startActivity(i);
					} else {
						WifiConnectDialog wifiConnectDialog = new WifiConnectDialog(getActivity(), DialogType.DEFAULT);
				    	
					    	wifiConnectDialog.setTitle("认证当前Wi-Fi");
					    	wifiConnectDialog.setDefaultContent("您目前未认证过您的Wi-Fi，是否马上认证?");
					    	wifiConnectDialog.setLeftBtnStr("取消");
					    	wifiConnectDialog.setRightBtnStr("确定");
					    	
					    	wifiConnectDialog.setLeftBtnListener(new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						});
				    	
					    	wifiConnectDialog.setRightBtnListener(new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									Intent i = new Intent(getApplicationContext(), WifiProviderRigisterActivity.class);
									startActivity(i);
									dialog.dismiss();
								}
							});
					    	wifiConnectDialog.show();
					}
				}
			});
		}
	}

	@Override
	protected void onVisible() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onInvisible() {
		// TODO Auto-generated method stub
		
	}
	
	private void setLoginedUI(boolean isLogined) {
		if (isLogined && mLoginHelper.isLogined() && mLoginHelper.getCurLoginUserInfo() != null) {
			mPageRoot.findViewById(R.id.login_content_layout).setVisibility(View.GONE);
			mPageRoot.findViewById(R.id.person_content_layout).setVisibility(View.VISIBLE);
			TextView tvName = (TextView) mPageRoot.findViewById(R.id.person_name);
			tvName.setText(mLoginHelper.getCurLoginUserInfo().getUsername());
			if (mLoginHelper.getCurLoginUserInfo().Avatar != null) {
				Drawable drawable = new BitmapDrawable(this.getResources(), BitmapUtil.Bytes2Bimap(mLoginHelper.getCurLoginUserInfo().Avatar));
				ImageView iv_avatar = (ImageView) this.findViewById(R.id.person_icon);
				iv_avatar.setImageDrawable(drawable);
			}
			
			new Timer().schedule(new TimerTask() {

				@Override
				public void run() {
					//提醒WiFi提供者多去更新其WiFi设置(TODO)以后要专门移动到一个服务里面
					/*if(mLoginHelper.mWifiProfile != null) {
						getActivity().runOnUiThread(new Runnable() {

							@Override
							public void run() {
								new AlertDialog.Builder(getActivity()).setTitle("更新WiFi信息").setMessage("您已经很久没有更新过您提供的WiFi敲门问题和动态信息啦，是否去更新？")
								.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {
										Intent i = new Intent();
										i.setClass(getApplicationContext(), WifiProviderSettingActivity.class);
										startActivity(i);
									}
								}).setNegativeButton("取消", null).show();
							}
							
						});
					}*/
				}
				
			}, 20000, 80000000);
			
		} else {
			mPageRoot.findViewById(R.id.login_content_layout).setVisibility(View.VISIBLE);
			mPageRoot.findViewById(R.id.person_content_layout).setVisibility(View.GONE);
		}
	}
}
