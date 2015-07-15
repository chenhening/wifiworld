package com.anynet.wifiworld.me;

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.bmob.v3.Bmob;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.BaseActivity;
import com.anynet.wifiworld.GlobalConfig;
import com.anynet.wifiworld.data.UserProfile;
import com.anynet.wifiworld.me.MyAccountActivity;
import com.anynet.wifiworld.util.LoginHelper;

public class UserLoginActivity extends BaseActivity {
	public static final String FILTER_NAME = "f";
	// for SMS verify
	private String mPhone_code = "86"; // 目前只支持中国区
	private TimerTask mTask;
	private LoginHelper mLoginHelper;
	private EditText mET_Account; // account input
	private EditText mET_SMS; // sms input
	private Button mLL_Login;
	private LinearLayout mLL_Verify;
	private TextView mTV_Verify;
	private String mPhoneNumber;
	private String mSmsCode;
	private int CountDown = 0;

	public static void start(BaseActivity ctx) {
		Intent intent = new Intent(ctx, UserLoginActivity.class);
		ctx.startActivity(intent);
		ctx.overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out); 
	}

	private void bingdingTitleUI() {
		mTitlebar.tvTitle.setText(getString(R.string.login_login));
		mTitlebar.ivHeaderLeft.setVisibility(View.VISIBLE);
		mTitlebar.ivHeaderLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	BroadcastReceiver loginBR = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(LoginHelper.AUTO_LOGIN_FAIL)) {
				Toast.makeText(getApplicationContext(), "登录失败!", Toast.LENGTH_LONG).show();
				mLL_Login.setEnabled(true);
			} else if (action.equals(LoginHelper.AUTO_LOGIN_SUCCESS)) {
				Toast.makeText(getApplicationContext(), "登录成功!", Toast.LENGTH_LONG).show();
//				//登陆成功后直接进入修改个人资料页面
//				Intent i = new Intent();
//				i.setClass(getApplicationContext(), MyAccountActivity.class);
//				UserLoginActivity.this.startActivity(i);
				UserLoginActivity.this.finish();
			} else if (action.equals(LoginHelper.AUTO_LOGIN_NEVERLOGIN)) {
				Toast.makeText(getApplicationContext(), "自动登录失败!", Toast.LENGTH_LONG).show();
			} else if (action.equals(LoginService.LOGIN_SERVICE_EVENT_SUBMIT_VERIFICATION_CODE)) {
				UserProfile user = new UserProfile();
				user.setUsername(mPhoneNumber);
				user.setPassword(mSmsCode);
				mLoginHelper.Login(user);
				((Activity) getActivity()).runOnUiThread(new Runnable() {
					@Override
					public void run() {
						showToast("服务器验证成功，正在登陆......");
						// setLoginedUI(false);
					}
				});
			} else if (action.equals(LoginService.LOGIN_SERVICE_EVENT_GET_VERIFICATION_CODE)) {
				// 获取验证码成功
				// TODO(binfei): 将类似的函数整理成一个公共函数
				((Activity) getActivity()).runOnUiThread(new Runnable() {
					@Override
					public void run() {
						showToast("获取验证码成功，请输入验证码点击登陆.");
						mET_Account.setEnabled(false);
						mLL_Login.setEnabled(true);
					}
				});
			} else if (action.equals(LoginService.LOGIN_SERVICE_EVENT_ERROR)) {
				((Activity) getActivity()).runOnUiThread(new Runnable() {
					@Override
					public void run() {
						showToast("验证失败，请重新操作.");
						ResetLoginUI();
					}
				});
			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_user_login);
		super.onCreate(savedInstanceState);
		bingdingTitleUI();

		mLoginHelper = LoginHelper.getInstance(getActivity());

		setUIEvent();
		ResetLoginUI();
		Intent intent = new Intent(this, LoginService.class);
		intent.putExtra(LoginService.LOGIN_SERVICE_KEY, LoginService.LOGIN_SERVICE_INIT);
		startService(intent);
		IntentFilter filter = new IntentFilter();
		filter.addAction(LoginHelper.AUTO_LOGIN_SUCCESS);
		filter.addAction(LoginHelper.AUTO_LOGIN_FAIL);
		filter.addAction(LoginService.LOGIN_SERVICE_EVENT_GET_VERIFICATION_CODE);
		filter.addAction(LoginService.LOGIN_SERVICE_EVENT_SUBMIT_VERIFICATION_CODE);
		filter.addAction(LoginService.LOGIN_SERVICE_EVENT_ERROR);
		getActivity().registerReceiver(loginBR, filter);
	}

	private void setUIEvent() {

		mLL_Verify = (LinearLayout) findViewById(R.id.button_sms);
		mLL_Verify.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!setUsernameUI()) {
					return;
				}

				// 发送验证码
				Intent intent = new Intent(UserLoginActivity.this, LoginService.class);
				intent.putExtra(LoginService.LOGIN_SERVICE_KEY,
						LoginService.LOGIN_SERVICE_GET_VERIFICODE);
				intent.putExtra("phone", mPhoneNumber);
				intent.putExtra("code", mPhone_code);
				UserLoginActivity.this.startService(intent);
				// 按钮进入60秒倒计时
				CountDown = 60;
				mTV_Verify = (TextView) findViewById(R.id.tv_button_sms);
				mLL_Verify.setEnabled(false);
				final Timer timer = new Timer();
				mTask = new TimerTask() {

					@Override
					public void run() {
						((Activity) getActivity()).runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (--CountDown <= 0) {
									mET_Account.setEnabled(true);
									mLL_Verify.setEnabled(true);
									mTV_Verify.setText("点击再次发送");
									mLL_Login.setEnabled(true);
									mTask.cancel();
								} else {
									mTV_Verify.setText("验证码" + "(" + CountDown + ")");
								}
							}
						});
					}
				};
				timer.schedule(mTask, 0, 1000);
			}
		});

		// login
		mLL_Login = (Button) findViewById(R.id.button_logout);
		mLL_Login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!setUsernameUI()) {
					return;
				}
				
				mET_SMS = ((EditText) findViewById(R.id.tv_login_sms));
				mSmsCode = mET_SMS.getText().toString().trim();
				Pattern pattern = Pattern.compile("^[0-9]{4}$");
				if (!pattern.matcher(mSmsCode).find()) {
					showToast("请输入正确的4位验证码.");
					return;
				}
				mLL_Login.setEnabled(false);
				Intent intent = new Intent(UserLoginActivity.this, LoginService.class);
				intent.putExtra(LoginService.LOGIN_SERVICE_KEY, LoginService.LOGIN_SERVICE_SUMBIT_VERIFICODE);
				intent.putExtra("phone", mPhoneNumber);
				intent.putExtra("code", mPhone_code);
				intent.putExtra("smscode", mSmsCode);
				UserLoginActivity.this.startService(intent);
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
	
	private boolean setUsernameUI() {
		mET_Account = (EditText) findViewById(R.id.tv_login_account);
		mPhoneNumber = mET_Account.getText().toString().trim();
		Pattern pattern = Pattern.compile("^1[3|4|5|7|8][0-9]{9}$");
		if (!pattern.matcher(mPhoneNumber).find()) {
			showToast("请输入11位手机正确号码.");
			return false;
		}
		
		return true;
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
		unregisterReceiver(loginBR);
		super.onDestroy();
		Intent intent = new Intent(UserLoginActivity.this, LoginService.class);
		intent.putExtra(LoginService.LOGIN_SERVICE_KEY, LoginService.LOGIN_SERVICE_DESTORY);
		UserLoginActivity.this.stopService(intent);
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
		InputMethodManager imm = ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE));
		View v = getCurrentFocus();
		if (imm != null && v != null){
			imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

}
