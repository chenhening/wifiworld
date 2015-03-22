package com.anynet.wifiworld.me;

import java.util.TimerTask;

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
import cn.bmob.v3.Bmob;
import cn.smssdk.EventHandler;

import com.anynet.wifiworld.MainActivity.MainFragment;
import com.anynet.wifiworld.R;
import com.anynet.wifiworld.UserLoginActivity;
import com.anynet.wifiworld.config.GlobalConfig;
import com.anynet.wifiworld.util.LoginHelper;

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
				Toast.makeText(getApplicationContext(), "登录失败!",
						Toast.LENGTH_LONG).show();
			} else if (action.equals(LoginHelper.AUTO_LOGIN_SUCCESS)) {
				isLogined = true;
				Toast.makeText(getApplicationContext(), "登录成功!",
						Toast.LENGTH_LONG).show();
			} else if (action.equals(LoginHelper.AUTO_LOGIN_NEVERLOGIN)) {
				Toast.makeText(getApplicationContext(), "自动登录失败!",
						Toast.LENGTH_LONG).show();
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
		filter.addAction(LoginHelper.AUTO_LOGIN_SUCCESS);
		filter.addAction(LoginHelper.AUTO_LOGIN_FAIL);
		getActivity().registerReceiver(loginBR, filter);
	}

	@Override
	public void onDestroy() {
		getActivity().unregisterReceiver(loginBR);
		super.onDestroy();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO(binfei): need to be removed into functions onAttach for better
		mPageRoot = inflater.inflate(R.layout.fragment_person, null);
		super.onCreateView(inflater, container, savedInstanceState);
		bingdingTitleUI();
		setLoginedUI(false);
		// DisplayIncomeChart();
		mPageRoot.findViewById(R.id.login_text).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getApplicationContext(),
						UserLoginActivity.class);
				startActivity(i);
			}
		});
		
		mPageRoot.findViewById(R.id.cl_iam_wifi_user).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent i = new Intent(getApplicationContext(),
								BussessPartnerActivity.class);
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
		mTitlebar.llHeaderMy.setVisibility(View.INVISIBLE);
		mTitlebar.tvHeaderRight.setVisibility(View.INVISIBLE);
		mTitlebar.tvTitle.setText(getString(R.string.my));
	}

	private void setLoginedUI(boolean isLogined) {
		if (isLogined && mLoginHelper.isLogined()) {
			mTitlebar.ivHeaderLeft.setVisibility(View.INVISIBLE);
			mPageRoot.findViewById(R.id.login_content_layout).setVisibility(
					View.GONE);
			mPageRoot.findViewById(R.id.person_content_layout).setVisibility(
					View.VISIBLE);
			TextView tvName = (TextView) mPageRoot
					.findViewById(R.id.person_name);
			tvName.setText(mLoginHelper.getCurLoginUserInfo().PhoneNumber);
		} else {
			mTitlebar.tvTitle.setText(getString(R.string.my));
			mTitlebar.ivHeaderLeft.setVisibility(View.INVISIBLE);
			mPageRoot.findViewById(R.id.login_content_layout).setVisibility(
					View.GONE);
			mPageRoot.findViewById(R.id.person_content_layout).setVisibility(
					View.GONE);
		}
	}

	/*
	private void DisplayIncomeChart() {
		LinearLayout chartLayout = (LinearLayout)findViewById(R.id.ll_money_get);
		// 图表显示范围在占屏幕大小的90%的区域内
		int scrWidth = chartLayout.getLayoutParams().width;
		int scrHeight = chartLayout.getLayoutParams().height;
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				scrWidth, scrHeight);
		// 居中显示
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		mLineChart = new ProviderIncomeChartView(getActivity());
		chartLayout.addView(mLineChart, layoutParams);
	}
*/
}
