package com.anynet.wifiworld;

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.bmob.v3.Bmob;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.app.BaseActivity;
import com.anynet.wifiworld.config.GlobalConfig;
import com.anynet.wifiworld.data.UserProfile;
import com.anynet.wifiworld.util.LoginHelper;

public class UserLoginActivity extends BaseActivity {

	// for SMS verify
	private String mPhone_code = "86"; // 目前只支持中国区
	private int mVerifyTime = 60;
	private EventHandler mEventHandler;
	private TimerTask mTask;
	private LoginHelper mLoginHelper;
	private EditText mET_Account; // account input
	private EditText mET_SMS; // sms input
	private Button mLL_Login;
	private LinearLayout mLL_Verify;
	private TextView mTV_Verify;
	private String mPhoneNumber;
	private String mSmsCode;
	private void bingdingTitleUI() {
		mTitlebar.ivHeaderLeft.setVisibility(View.VISIBLE);
		mTitlebar.llFinish.setVisibility(View.VISIBLE);
		mTitlebar.llHeaderMy.setVisibility(View.INVISIBLE);
		mTitlebar.tvHeaderRight.setVisibility(View.INVISIBLE);
		mTitlebar.tvHeaderRight.setText(R.string.next_step);
		mTitlebar.tvHeaderRight.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(UserLoginActivity.this,
						MainActivity.class);
				startActivity(i);
			}
		});
		mTitlebar.tvTitle.setText(getString(R.string.login_login));
		mTitlebar.ivHeaderLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setContentView(R.layout.user_login);
		super.onCreate(savedInstanceState);
		bingdingTitleUI();
		
		mLoginHelper = LoginHelper.getInstance(getActivity());
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
						// 通过bmob保存到服务器，以便于做数据验证
						UserProfile user = new UserProfile();
						user.PhoneNumber = mPhoneNumber;
						user.Password = mSmsCode;
						mLoginHelper.Login(user);
						((Activity) getActivity()).runOnUiThread(new Runnable() {
							@Override
							public void run() {
								showToast("服务器验证成功，正在登陆......");
								//setLoginedUI(false);
							}
						});
					} else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
						// 获取验证码成功
						// TODO(binfei): 将类似的函数整理成一个公共函数
						((Activity) getActivity()).runOnUiThread(new Runnable() {
							@Override
							public void run() {
								showToast("获取验证码成功，请输入验证码点击登陆.");
							//	mET_Account.setEnabled(false);
							//	mLL_Login.setEnabled(true);
							}
						});
					} else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
						// 返回支持发送验证码的国家列表
					}
				} else {
					((Throwable) data).printStackTrace();
					// TODO(binfei): 将类似的函数整理成一个公共函数
					((Activity) getActivity()).runOnUiThread(new Runnable() {
						@Override
						public void run() {
							showToast("验证失败，请重新操作.");
//							ResetLoginUI();
						}
					});
				}
			}
		};
		// 注册回调监听接口
		SMSSDK.registerEventHandler(mEventHandler);
		setUIEvent();
		ResetLoginUI();
	}

	private void setUIEvent(){
		
		mLL_Verify = (LinearLayout) findViewById(R.id.button_sms);
		mLL_Verify.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mET_Account = (EditText)findViewById(R.id.tv_login_account);
				mPhoneNumber = mET_Account.getText().toString().trim();
				Pattern pattern = Pattern.compile("^1[3|4|5|7|8][0-9]{9}$");
				if (!pattern.matcher(mPhoneNumber).find()) {
					showToast("请输入11位手机正确号码.");
					return;
				}

				// 发送验证码
				SMSSDK.getVerificationCode(mPhone_code, mPhoneNumber);

				// 按钮进入60秒倒计时
				mTV_Verify = (TextView) findViewById(R.id.tv_button_sms);
				mLL_Verify.setEnabled(false);
				final Timer timer = new Timer();
				mTask = new TimerTask() {

					@Override
					public void run() {
						((Activity) getActivity()).runOnUiThread(new Runnable() {
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
		mLL_Login = (Button)findViewById(R.id.button_login);
		mLL_Login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mLL_Login.setEnabled(false);
				mET_SMS = ((EditText)findViewById(R.id.tv_login_sms));
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
	
	private void ResetLoginUI() {
		if (mET_SMS != null)
			mET_SMS.setEnabled(true);
		if (mLL_Login != null)
			mLL_Login.setEnabled(true);
	}
	
	private Context getActivity() {
		// TODO Auto-generated method stub
		return this;
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
		SMSSDK.unregisterEventHandler(mEventHandler);
		super.onDestroy();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

}
