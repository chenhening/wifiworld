package com.anynet.wifiworld.me;

import java.util.List;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.anynet.wifiworld.MainActivity.MainFragment;
import com.anynet.wifiworld.R;
import com.anynet.wifiworld.config.GlobalConfig;
import com.anynet.wifiworld.data.MultiDataCallback;
import com.anynet.wifiworld.data.UserProfile;
import com.anynet.wifiworld.data.WifiProfile;
import com.anynet.wifiworld.util.LoginHelper;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.common.SocializeConstants;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.yixin.controller.UMYXHandler;

public class MeFragment extends MainFragment {
	// for saved data
	private LoginHelper mLoginHelper;
	WifiProfile mWifiProfile;
	BroadcastReceiver loginBR = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			boolean isLogined = false;
			String action = intent.getAction();
			if (action.equals(LoginHelper.AUTO_LOGIN_FAIL)) {
				Toast.makeText(getApplicationContext(), "登录失败!", Toast.LENGTH_LONG).show();
			} else if (action.equals(LoginHelper.AUTO_LOGIN_SUCCESS)) {
				isLogined = true;
				Toast.makeText(getApplicationContext(), "登录成功!", Toast.LENGTH_LONG).show();
			} else if (action.equals(LoginHelper.AUTO_LOGIN_NEVERLOGIN)) {
				Toast.makeText(getApplicationContext(), "自动登录失败!", Toast.LENGTH_LONG).show();
			} else if (action.equals(LoginHelper.LOGIN_OUT)) {
				Toast.makeText(getApplicationContext(), "退出登录!", Toast.LENGTH_LONG).show();
				isLogined = false;
			}
			setLoginedUI(isLogined);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
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
		// TODO(binfei): need to be removed into functions onAttach for better
		mPageRoot = inflater.inflate(R.layout.fragment_person, null);
		super.onCreateView(inflater, container, savedInstanceState);
		bingdingTitleUI();
		setLoginedUI(false);
		
		mPageRoot.findViewById(R.id.login_text).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!checkIsLogined()) {
					return;
				}
			}
		});

		mPageRoot.findViewById(R.id.slv_i_am_wifi_provider).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// 查询是否登录
				if (!checkIsLogined()) {
					return;
				}
				
				mWifiProfile = mLoginHelper.mWifiProfile;
				if (mWifiProfile != null) {
					Intent i = new Intent(getApplicationContext(), WifiProviderDetailActivity.class);
					startActivity(i);
				} else {
					new AlertDialog.Builder(getActivity())
					.setTitle("共享WiFi").setMessage("您目前还没有共享过WiFi，是否要共享当前WiFi并进行绑定?")  
					.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
	
						@Override
						public void onClick(DialogInterface dialog, int which) {
							//mLoginHelper.mWifiProfile = new WifiProfile();
							Intent i = new Intent(getApplicationContext(), WifiProviderRigisterFirstActivity.class);
							startActivity(i);
						}					
					})  
					.setNegativeButton("取消", null)
					.show();
				}
			}
		});
		
		//我用的wifi
		mPageRoot.findViewById(R.id.slv_iam_wifi_user).setOnClickListener(new OnClickListener() {

			@Override
            public void onClick(View v) {
				// 查询是否登录
				if (!checkIsLogined()) {
					return;
				}
				Intent i = new Intent(getApplicationContext(), WifiUsedListActivity.class);
				startActivity(i); 
            }
			
		});
		
		mPageRoot.findViewById(R.id.person_icon).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!checkIsLogined()) {
					return;
				}
				Intent i = new Intent(getApplicationContext(), MyAccountActivity.class);
				startActivity(i);
			}
		});
		
		mPageRoot.findViewById(R.id.setiing_share_layout).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!checkIsLogined()) {
					return;
				}
				/* 代码添加Appkey，如果设置了非null值，SocialSDK将使用该值. */
				SocializeConstants.APPKEY = GlobalConfig.UMENG_SHARE_KEY;
				com.umeng.socialize.utils.Log.LOG = true;

				UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
				/*
				 * UMQQSsoHandler qqSsoHandler = new
				 * UMQQSsoHandler(getActivity(), "", "");
				 * qqSsoHandler.addToSocialSDK(); UMWXHandler wxSsoHandler = new
				 * UMWXHandler(getActivity(), "", "");
				 * wxSsoHandler.addToSocialSDK(); UMWXHandler wxCircleSsoHandler
				 * = new UMWXHandler(getActivity(), "", "");
				 * wxCircleSsoHandler.setToCircle(true);
				 * wxCircleSsoHandler.addToSocialSDK();
				 */
				// 添加易信平台,参数1为当前activity, 参数2为在易信开放平台申请到的app id
				UMYXHandler yixinHandler = new UMYXHandler(getActivity(), GlobalConfig.YIXIN_APPKEY);
				// 关闭分享时的等待Dialog
				yixinHandler.enableLoadingDialog(false);
				// 把易信添加到SDK中
				yixinHandler.addToSocialSDK();
				// UMLWHandler umlwHandler = new UMLWHandler(getActivity(),
				// "laiwangd497e70d4", "d497e70d4c3e4efeab1381476bac4c5e");
				// umlwHandler.addToSocialSDK();
				// umlwHandler.setMessageFrom("友盟分享组件");
				// 设置分享面板上显示的平台
				mController.getConfig().setPlatforms(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ,
						SHARE_MEDIA.SINA, SHARE_MEDIA.YIXIN, SHARE_MEDIA.LAIWANG, SHARE_MEDIA.RENREN,
						SHARE_MEDIA.DOUBAN);
				UserProfile mUP = mLoginHelper.getCurLoginUserInfo();
				mController.setShareContent(mUP.PhoneNumber + "邀请你使用：" + getString(R.string.app_name)
						+ "。闲置WIFI是不是很浪费？" + getString(R.string.app_name) + "可以利用闲置的WIFI给自己赚钱啦！");
				mController.openShare(getActivity(), new SnsPostListener() {

					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						Toast.makeText(getActivity(), "开始分享", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onComplete(SHARE_MEDIA arg0, int arg1, SocializeEntity arg2) {
						// TODO Auto-generated method stub
						if (arg1 == 200) {
							Toast.makeText(getActivity(), "分享完成", Toast.LENGTH_SHORT).show();
						}
					}
				});
			}
		});
		
		mPageRoot.findViewById(R.id.slv_my_setting).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), MySettingActivity.class);
				startActivity(i);
			}
		});
		
		//我的关注
		mPageRoot.findViewById(R.id.slv_my_attention).setOnClickListener(new OnClickListener() {

			@Override
            public void onClick(View v) {
				// 查询是否登录
				if (!checkIsLogined()) {
					return;
				}
				Intent i = new Intent(getApplicationContext(), WifiFollowListActivity.class);
				startActivity(i); 
            }
			
		});
		//我的黑名单
		mPageRoot.findViewById(R.id.slv_my_blacklist).setOnClickListener(new OnClickListener() {

			@Override
            public void onClick(View v) {
				// 查询是否登录
				if (!checkIsLogined()) {
					return;
				}
				Intent i = new Intent(getApplicationContext(), WifiFollowListActivity.class);
				startActivity(i); 
            }
			
		});
		
		//设置about
		this.findViewById(R.id.slv_about_app).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent i = new Intent();
				i.setClass(getApplicationContext(), AboutAppActivity.class);
				startActivity(i);
			}
			
		});
		
		return mPageRoot;
	}

	// ---------------------------------------------------------------------------------------------
	// for custom UI
	private void bingdingTitleUI() {
		mTitlebar.ivHeaderLeft.setVisibility(View.INVISIBLE);
		mTitlebar.llFinish.setVisibility(View.VISIBLE);
		//mTitlebar.llHeaderMy.setVisibility(View.INVISIBLE);
		mTitlebar.tvHeaderRight.setVisibility(View.INVISIBLE);
		mTitlebar.tvTitle.setText(getString(R.string.my));
	}

	private void setLoginedUI(boolean isLogined) {
		if (isLogined && mLoginHelper.isLogined() && mLoginHelper.getCurLoginUserInfo()!=null) {
			mTitlebar.ivHeaderLeft.setVisibility(View.INVISIBLE);
			mPageRoot.findViewById(R.id.login_content_layout).setVisibility(View.GONE);
			mPageRoot.findViewById(R.id.person_content_layout).setVisibility(View.VISIBLE);
			TextView tvName = (TextView) mPageRoot.findViewById(R.id.person_name);
			tvName.setText(mLoginHelper.getCurLoginUserInfo().PhoneNumber);
		} else {
			mTitlebar.tvTitle.setText(getString(R.string.my));
			mTitlebar.ivHeaderLeft.setVisibility(View.INVISIBLE);
			mPageRoot.findViewById(R.id.login_content_layout).setVisibility(View.VISIBLE);
			mPageRoot.findViewById(R.id.person_content_layout).setVisibility(View.GONE);
		}
	}
}
