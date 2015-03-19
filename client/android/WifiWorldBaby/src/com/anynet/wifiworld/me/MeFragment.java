package com.anynet.wifiworld.me;

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.Bmob;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import com.anynet.wifiworld.MainActivity.MainFragment;
import com.anynet.wifiworld.R;
import com.anynet.wifiworld.config.GlobalConfig;
import com.anynet.wifiworld.data.UserProfile;
import com.anynet.wifiworld.util.Base64Util;
import com.anynet.wifiworld.util.LoginHelper;

public class MeFragment extends MainFragment {
	// for saved data
	private LoginHelper mLoginHelper;

	// for SMS verify
	private String mPhone_code = "86"; // 目前只支持中国区
	private int mVerifyTime = 60;
	private EventHandler mEventHandler;
	private TimerTask mTask;
	private EditText mET_Account; // account input
	private EditText mET_SMS; // sms input
	private LinearLayout mLL_Login;
	private LinearLayout mLL_Verify;
	private TextView mTV_Verify;
	private String mPhoneNumber;
	private String mSmsCode;

	BroadcastReceiver loginBR = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Toast.makeText(getApplicationContext(),
					"Login broadcast receiver!", Toast.LENGTH_LONG).show();
			setLoginedUI(false);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// 初始化 Bmob SDK
		Bmob.initialize(getActivity(), GlobalConfig.BMOB_KEY);
		// initialize sms
		SMSSDK.initSDK(getActivity(), GlobalConfig.SMSSDK_KEY,
				GlobalConfig.SMSSDK_SECRECT);
		mEventHandler = new EventHandler() {
			public void afterEvent(int event, int result, Object data) {
				if (result == SMSSDK.RESULT_COMPLETE) {
					// 回调完成
					if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
						// 提交验证码成功
						// TODO(binfei): 将类似的函数整理成一个公共函数
						// 得到加密后的密码
						String password = Base64Util.encode(mPhoneNumber
								+ mSmsCode);
						// 通过bmob保存到服务器，以便于做数据验证
						UserProfile user = new UserProfile();
						user.PhoneNumber = mPhoneNumber;
						user.Password = password.trim();
						mLoginHelper.Login(user);
						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								showToast("服务器验证成功，正在登陆......");
								setLoginedUI(false);
							}
						});
					} else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
						// 获取验证码成功
						// TODO(binfei): 将类似的函数整理成一个公共函数
						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								showToast("获取验证码成功，请输入验证码点击登陆.");
								mET_Account.setEnabled(false);
								mLL_Login.setEnabled(true);
							}
						});
					} else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
						// 返回支持发送验证码的国家列表
					}
				} else {
					((Throwable) data).printStackTrace();
					// TODO(binfei): 将类似的函数整理成一个公共函数
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							showToast("验证失败，请重新操作.");
							ResetLoginUI();
						}
					});
				}
			}
		};
		// 注册回调监听接口
		SMSSDK.registerEventHandler(mEventHandler);

		// initialize loginhelper
		mLoginHelper = LoginHelper.getInstance();
		mLoginHelper.init(getActivity());
		mLoginHelper.AutoLogin();
		// 监听登录
		IntentFilter filter = new IntentFilter();
		filter.addAction(LoginHelper.AUTO_LOGIN_SUCCESS);
		filter.addAction(LoginHelper.AUTO_LOGIN_FAIL);
		getActivity().registerReceiver(loginBR, filter);
	}

	@Override
	public void onDestroy() {
		// mLoginHelper.logout();
		// registerEventHandler必须和unregisterEventHandler配套使用，否则可能造成内存泄漏。
		SMSSDK.unregisterEventHandler(mEventHandler);
		getActivity().unregisterReceiver(loginBR);
		super.onDestroy();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO(binfei): need to be removed into functions onAttach for better
		mPageRoot = inflater.inflate(R.layout.fragment_me, null);
		super.onCreateView(inflater, container, savedInstanceState);
		bingdingTitleUI();
		setLoginedUI(false);
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

	private void setLoginedUI(boolean isLoginUI) {
		if (isLoginUI) {
			mTitlebar.tvTitle.setText(getString(R.string.login_login));
			mPageRoot.findViewById(R.id.ll_userprofile)
					.setVisibility(View.GONE);
			mPageRoot.findViewById(R.id.ll_login).setVisibility(View.VISIBLE);
			mTitlebar.ivHeaderLeft.setVisibility(View.VISIBLE);
			mTitlebar.ivHeaderLeft.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					setLoginedUI(false);
				}
			});
			
			mLL_Verify = (LinearLayout) mPageRoot.findViewById(R.id.button_sms);
			mLL_Verify.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					mET_Account = (EditText) mPageRoot
							.findViewById(R.id.tv_login_account);
					mPhoneNumber = mET_Account.getText().toString().trim();
					Pattern pattern = Pattern.compile("^1[3|4|5|7|8][0-9]{9}$");
					if (!pattern.matcher(mPhoneNumber).find()) {
						showToast("请输入11位手机正确号码.");
						return;
					}

					// 发送验证码
					SMSSDK.getVerificationCode(mPhone_code, mPhoneNumber);

					// 按钮进入60秒倒计时
					mTV_Verify = (TextView) mPageRoot
							.findViewById(R.id.tv_button_sms);
					mLL_Verify.setEnabled(false);
					final Timer timer = new Timer();
					mTask = new TimerTask() {

						@Override
						public void run() {
							getActivity().runOnUiThread(new Runnable() {
								@Override
								public void run() {
									if (mVerifyTime <= 0) {
										mET_Account.setEnabled(true);
										mLL_Verify.setEnabled(true);
										mTV_Verify.setText("点击再次发送");
										mLL_Login.setEnabled(true);
										mTask.cancel();
									} else {
										mTV_Verify.setText("验证码" + "("
												+ mVerifyTime + ")");
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

			// login
			mLL_Login = (LinearLayout) mPageRoot
					.findViewById(R.id.button_login);
			mLL_Login.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					mLL_Login.setEnabled(false);
					mET_SMS = ((EditText) mPageRoot
							.findViewById(R.id.tv_login_sms));
					mSmsCode = mET_SMS.getText().toString().trim();
					Pattern pattern = Pattern.compile("^[0-9]{4}$");
					if (!pattern.matcher(mSmsCode).find()) {
						showToast("请输入正确的4位验证码.");
						return;
					}

					SMSSDK.submitVerificationCode(mPhone_code, mPhoneNumber,
							mSmsCode);
				}
			});
			mLL_Login.setEnabled(false);
		} else {
			mTitlebar.tvTitle.setText(getString(R.string.my));
			mPageRoot.findViewById(R.id.ll_userprofile).setVisibility(
					View.VISIBLE);
			mPageRoot.findViewById(R.id.ll_login).setVisibility(View.GONE);
			mTitlebar.ivHeaderLeft.setVisibility(View.INVISIBLE);
			mPageRoot.findViewById(R.id.rl_top_provider).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							if (!mLoginHelper.getCurLoginStatus()) {
								setLoginedUI(true);
							} else
								startActivity(new Intent(getActivity(),
										WifiProviderListActivity.class));
						}
					});
			mPageRoot.findViewById(R.id.rl_top_wifiuser).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							if (!mLoginHelper.getCurLoginStatus()) {
								setLoginedUI(true);
							} else
								startActivity(new Intent(getActivity(),
										WifiProviderDetailActivity.class));
						}
					});
			mPageRoot.findViewById(R.id.rl_setting_my_account)
					.setOnClickListener(null);
			mPageRoot.findViewById(R.id.rl_setting_my_account)
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							if (!mLoginHelper.getCurLoginStatus()) {
								setLoginedUI(true);
							} else {

							}
						}
					});
			if (mLoginHelper.getCurLoginStatus()) {
				TextView tvid = (TextView) mPageRoot
						.findViewById(R.id.tv_ww_id);
				tvid.setText(mLoginHelper.getCurLoginUserInfo().PhoneNumber);
			}
		}

	}

	// ---------------------------------------------------------------------------------------------
	// UI event process functions

	private void RegistLogin() {
		// get verify code
		mLL_Verify = (LinearLayout) mPageRoot.findViewById(R.id.button_sms);
		mLL_Verify.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mET_Account = (EditText) mPageRoot
						.findViewById(R.id.tv_login_account);
				mPhoneNumber = mET_Account.getText().toString().trim();
				Pattern pattern = Pattern.compile("^1[3|4|5|7|8][0-9]{9}$");
				if (!pattern.matcher(mPhoneNumber).find()) {
					showToast("请输入11位手机正确号码.");
					return;
				}

				// 发送验证码
				SMSSDK.getVerificationCode(mPhone_code, mPhoneNumber);

				// 按钮进入60秒倒计时
				mTV_Verify = (TextView) mPageRoot
						.findViewById(R.id.tv_button_sms);
				mLL_Verify.setEnabled(false);
				final Timer timer = new Timer();
				mTask = new TimerTask() {

					@Override
					public void run() {
						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (mVerifyTime <= 0) {
									mET_Account.setEnabled(true);
									mLL_Verify.setEnabled(true);
									mTV_Verify.setText("点击再次发送");
									mLL_Login.setEnabled(true);
									mTask.cancel();
								} else {
									mTV_Verify.setText("验证码" + "("
											+ mVerifyTime + ")");
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

		// login
		mLL_Login = (LinearLayout) mPageRoot.findViewById(R.id.button_login);
		mLL_Login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mLL_Login.setEnabled(false);
				mET_SMS = ((EditText) mPageRoot.findViewById(R.id.tv_login_sms));
				mSmsCode = mET_SMS.getText().toString().trim();
				Pattern pattern = Pattern.compile("^[0-9]{4}$");
				if (!pattern.matcher(mSmsCode).find()) {
					showToast("请输入正确的4位验证码.");
					return;
				}

				SMSSDK.submitVerificationCode(mPhone_code, mPhoneNumber,
						mSmsCode);
			}
		});
		mLL_Login.setEnabled(false);
	}

	@Override
	public boolean onBackPressed() {
		// TODO Auto-generated method stub
		boolean isLogining = (mPageRoot.findViewById(R.id.ll_login)
				.getVisibility() == View.VISIBLE);
		if (isLogining) {
			setLoginedUI(false);
			return true;
		}
		return super.onBackPressed();
	}

	private void ResetLoginUI() {
		mET_SMS.setEnabled(true);
		mLL_Login.setEnabled(true);
	}

}
