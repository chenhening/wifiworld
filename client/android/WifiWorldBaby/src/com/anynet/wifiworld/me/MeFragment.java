package com.anynet.wifiworld.me;

import android.content.BroadcastReceiver;
import android.content.Context;
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
import com.anynet.wifiworld.MyAccountActivity;
import com.anynet.wifiworld.R;
import com.anynet.wifiworld.UserLoginActivity;
import com.anynet.wifiworld.app.BaseActivity;
import com.anynet.wifiworld.data.UserProfile;
import com.anynet.wifiworld.util.LoginHelper;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;

public class MeFragment extends MainFragment {
	// for saved data
	private LoginHelper mLoginHelper;

	private ProviderIncomeChartView mLineChart = null;

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
		mLoginHelper = LoginHelper.getInstance(getActivity());
		// 监听登录
		IntentFilter filter = new IntentFilter();
		filter.addAction(LoginHelper.LOGIN_SUCCESS);
		filter.addAction(LoginHelper.LOGIN_FAIL);
		filter.addAction(LoginHelper.LOGIN_OUT);
		getActivity().registerReceiver(loginBR, filter);
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
		// DisplayIncomeChart();
		mPageRoot.findViewById(R.id.login_text).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), UserLoginActivity.class);
				startActivity(i);
			}
		});

		mPageRoot.findViewById(R.id.cl_iam_wifi_provider).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				/*
				 * //查询是否登录 if (!mLoginHelper.getCurLoginStatus()) { Intent i =
				 * new Intent(getApplicationContext(),UserLoginActivity.class);
				 * startActivity(i); return; }
				 */

				Intent i = new Intent(getApplicationContext(), WifiProviderDetailActivity.class);
				startActivity(i);
				// 去服务器上查询是否已经登记了自己的wifi
				/*
				 * WifiProfile wifi = new WifiProfile(); wifi.Sponser =
				 * "18688339822"
				 * ;//mLoginHelper.getCurLoginUserInfo().PhoneNumber;
				 * wifi.QueryBySponser(getApplicationContext(), wifi.Sponser,
				 * new MultiDataCallback<WifiProfile>() {
				 * 
				 * @Override public void onSuccess(List<WifiProfile> objects) {
				 * Intent i = new Intent(getApplicationContext(),
				 * WifiProviderDetailActivity.class);
				 * i.putExtra(WifiProfile.class.getName(), objects.get(0));
				 * startActivity(i); }
				 * 
				 * @Override public void onFailed(String msg) { Intent i = new
				 * Intent(getApplicationContext(),
				 * WifiProviderRigisterActivity.class); startActivity(i); }
				 * 
				 * });
				 */
			}
		});
		mPageRoot.findViewById(R.id.person_icon).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getApplicationContext(), MyAccountActivity.class);
				startActivity(i);
			}
		});

		mPageRoot.findViewById(R.id.setiing_share_layout).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				/*
				 * Intent i = new Intent(getApplicationContext(),
				 * ShareActivity.class); startActivity(i);
				 */
				if (!mLoginHelper.isLogined()) {
					UserLoginActivity.start((BaseActivity) getActivity());
					return;
				}
				UserProfile mUP = mLoginHelper.getCurLoginUserInfo();
				UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
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
						if(arg1 == 200){
							Toast.makeText(getActivity(), "分享完成", Toast.LENGTH_SHORT).show();
						}
					}
				});
			}
		});
		return mPageRoot;
	}

	// ---------------------------------------------------------------------------------------------
	// for custom UI
	private void bingdingTitleUI() {
		mTitlebar.ivHeaderLeft.setVisibility(View.INVISIBLE);
		mTitlebar.llFinish.setVisibility(View.VISIBLE);
		mTitlebar.llHeaderMy.setVisibility(View.INVISIBLE);
		mTitlebar.tvHeaderRight.setVisibility(View.INVISIBLE);
		mTitlebar.tvTitle.setText(getString(R.string.my));
	}

	private void setLoginedUI(boolean isLogined) {
		if (isLogined && mLoginHelper.isLogined()) {
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

	/*
	 * private void DisplayIncomeChart() { LinearLayout chartLayout =
	 * (LinearLayout)findViewById(R.id.ll_money_get); // 图表显示范围在占屏幕大小的90%的区域内
	 * int scrWidth = chartLayout.getLayoutParams().width; int scrHeight =
	 * chartLayout.getLayoutParams().height; RelativeLayout.LayoutParams
	 * layoutParams = new RelativeLayout.LayoutParams( scrWidth, scrHeight); //
	 * 居中显示 layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT); mLineChart =
	 * new ProviderIncomeChartView(getActivity());
	 * chartLayout.addView(mLineChart, layoutParams); }
	 */
}
