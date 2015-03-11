package com.anynet.wifiworld;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import android.widget.*;
import com.anynet.wifiworld.util.*;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView.OnEditorActionListener;

import com.testin.agent.TestinAgent;
//import com.xunlei.common.member.XLErrorCode;
//import com.xunlei.common.member.XLOnUserListener;
//import com.xunlei.common.member.XLUserInfo;
//import com.xunlei.common.member.XLUserInfo.USERINFOKEY;
//import com.xunlei.common.member.XLUserUtil;
import com.anynet.wifiworld.api.AppRestClient;
import com.anynet.wifiworld.api.GsonHelper;
import com.anynet.wifiworld.api.callback.ResponseCallback;
import com.anynet.wifiworld.app.BaseActivity;
import com.anynet.wifiworld.app.WifiWorldApplication;
import com.anynet.wifiworld.bean.AppConfigResp;
import com.anynet.wifiworld.bean.PriviledgeResp;
import com.anynet.wifiworld.config.GlobalConfig;
import com.anynet.wifiworld.constant.Const;
import com.anynet.wifiworld.constant.ErrCode;
import com.anynet.wifiworld.report.ReportUtil;
import com.anynet.wifiworld.view.AnimationDot;
import com.anynet.wifiworld.R;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class LoginActivity extends BaseActivity implements OnClickListener {

	/**
	 * 登录按钮
	 */
	private LinearLayout llLogin;

	private EditText etLoginAccount;

//	private EditText etLoginPasswd;

	private Button btClearLoginAccount;

//	private Button btClearLoginPasswd;

	private TextView tvLoginErrortip;

	private LinearLayout llLoginErrortip;

	private TextView tvLogin;

	private PreferenceHelper pref = PreferenceHelper.getInstance();

	private String account;

	private String passwd;

	private String saveAccount;

	private String savePasswd;

	private int userId;

//	private ScrollView slLogin;

	private int screenHeight;

	// 是否已经被踢
	private static boolean isHasKick = false;

	// 表明当前是否在登录中状态
	private boolean isNowLogining = false;

	private static Long startLoginingTime = 0l;
	private static int retryCount = 0;
	// 保存临时数据

	private static final String SAVE_USER_ACCOUNT = "user_account";

	private static final String SAVE_USER_PASSWD = "user_passwd";

	/**
	 * 点点动画
	 */
	private AnimationDot mAnidot;

//	XLOnUserListener mLoginListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		setContentView(R.layout.login);
		super.onCreate(savedInstanceState);

		binddingUI();
		binddingEvent();

		startLoginingTime = System.currentTimeMillis();
		retryCount = 1;

//		// 登录监听器
//		mLoginListener = new XLOnUserListener() {
//			public boolean onUserLogin(int errorCode, XLUserInfo userInfo,
//					Object userdata, String errorDesc) {
//
//				switch (errorCode) {
//				case XLErrorCode.SUCCESS:
//
//					UmengHelper.setUmeng(LoginActivity.this);
//
//					hideSoftInput();
//
//					/** 先对比userid是否切换了用户 ,切换用户需要清除掉通知栏消息 */
//					String lastUserid = PreferenceHelper.getInstance()
//							.getString(Const.USER_ID, "");
//					if (lastUserid != null
//							&& !lastUserid.equals("")
//							&& !lastUserid.equals(String.valueOf(userInfo
//									.getIntValue(USERINFOKEY.UserID)))) {
//						// 清楚标题栏通知消息
//						NotificationManager notiManager = (NotificationManager) LoginActivity.this
//								.getApplicationContext().getSystemService(
//										NOTIFICATION_SERVICE);
//						notiManager.cancelAll();
//					}
//
//					LoginHelper.getInstance().storeLoginInfo();
//
//					pref.setString(Const.USER_ACCOUNT, account);
//
//					// setTheLoginingState(false);
//					llLoginErrortip.setVisibility(View.INVISIBLE);
//
//					// 设置心跳
//					// XLUserUtil.getInstance().setKeepAlive(true, -1);
//
//					getLogoURL();
//					break;
//
//				case XLErrorCode.ACCOUNT_INVALID:
//				case XLErrorCode.PASSWORD_ERROR:
//					tvLoginErrortip.setText("账户或密码错误");
//					setTheLoginingState(false);
//
//					break;
//				case XLErrorCode.SOCKET_TIMEOUT_ERROR:
//					if (System.currentTimeMillis() - startLoginingTime < 15000
//							&& retryCount < 5) {
//						processLogin();
//						retryCount++;
//						Toast.makeText(LoginActivity.this,
//								" s重试：" + retryCount, Toast.LENGTH_LONG).show();
//					} else {
//						tvLoginErrortip.setText("网络超时");
//						setTheLoginingState(false);
//						startLoginingTime = System.currentTimeMillis();
//						retryCount = 0;
//					}
//					break;
//				case XLErrorCode.UNKNOWN_ERROR:
//				case XLErrorCode.SOCKET_ERROR:
//					if (System.currentTimeMillis() - startLoginingTime < 15000
//							&& retryCount < 5) {
//						processLogin();
//						retryCount++;
//						Toast.makeText(LoginActivity.this,
//								" e重试：" + retryCount, Toast.LENGTH_LONG).show();
//					} else {
//						tvLoginErrortip.setText("系统忙， 请稍后再试");
//						setTheLoginingState(false);
//						startLoginingTime = System.currentTimeMillis();
//						retryCount = 0;
//
//					}
//					break;
//				default:
//
//					tvLoginErrortip.setText("系统忙， 请稍后再试");
//					setTheLoginingState(false);
//					break;
//				}
//				return true;
//			}
//		};

		llLoginErrortip.setVisibility(View.INVISIBLE);

		screenHeight = DisplayUtil.getDisplayHeight(this);

		account = pref.getString(Const.USER_ACCOUNT, "");

		// 获取并设置保存的用户名和密码
		if (!TextUtils.isEmpty(account)) {
			etLoginAccount.setText(account);
		}

		if (savedInstanceState != null) {

			saveAccount = savedInstanceState.getString(SAVE_USER_ACCOUNT);
			savePasswd = savedInstanceState.getString(SAVE_USER_PASSWD);
			if (!TextUtils.isEmpty(saveAccount)) {
				etLoginAccount.setText(saveAccount);
			}

//			if (!TextUtils.isEmpty(savePasswd)) {
//				etLoginPasswd.setText(savePasswd);
//			}

		}

		Bundle bundle = getIntent().getExtras();

		if (bundle != null) {

			String reasonDesc = bundle.getString(Const.EXIT_DESC);

			if (!TextUtils.isEmpty(reasonDesc)) {

				showToast(getString(R.string.sessoin_fail_for_timeout));

			}

		}

	}

	/**
	 * 由于被踢而进入登录页
	 * 
	 * @param context
	 * @param reasonDesc
	 */
	public static void startForKick(Context context, String reasonDesc) {

		// 防止不断重负提示
		// if (!isHasKick)
		// {
		Intent intent = new Intent(context, LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(Const.EXIT_DESC, reasonDesc);
		context.startActivity(intent);
		// isHasKick = true;
		// }
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// finish();
		super.onNewIntent(intent);
	}

	@Override
	protected void onResume() {

		super.onResume();
		hideSoftInput();

		// //自动登录
		// if (!isAutoLogin())
		// {
		// //手动登录
		// initManualLogin();
		// }

		initManualLogin();

	}

	private void initManualLogin() {

		//etLoginPasswd.setText("");

		// 以下是手动登录的初始化操作

		if (account.equals("") || account == null) {
			changeScrollView();
			showInputMethod(etLoginAccount);
			llLogin.setClickable(false);
		} else {

			hideSoftInput();

		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString(SAVE_USER_ACCOUNT, etLoginAccount.getText()
				.toString().trim());
//		outState.putString(SAVE_USER_PASSWD, etLoginPasswd.getText().toString()
//				.trim());
		super.onSaveInstanceState(outState);

	}

	@Override
	protected void onPause() {

		super.onPause();

	}

	@Override
	protected void onStop() {
		super.onStop();

	}

	private void binddingUI() {

		llLogin = (LinearLayout) findViewById(R.id.button_login);

		etLoginAccount = (EditText) findViewById(R.id.tv_login_account);
		//etLoginPasswd = (EditText) findViewById(R.id.tv_login_passwd);

		// 清除按钮
		btClearLoginAccount = (Button) findViewById(R.id.bt_clear_login_account);
		//btClearLoginPasswd = (Button) findViewById(R.id.bt_clear_login_passwd);

		tvLoginErrortip = (TextView) findViewById(R.id.tv_login_errortip);

		llLoginErrortip = (LinearLayout) findViewById(R.id.ll_login_errortip);

		// 登录文字
		tvLogin = (TextView) findViewById(R.id.tv_login);

//		slLogin = (ScrollView) findViewById(R.id.sl_login);

		mAnidot = (AnimationDot) findViewById(R.id.login_animation_dot);

	}

	private void binddingEvent() {

		llLogin.setOnClickListener(this);

		btClearLoginAccount.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				etLoginAccount.setText("");
//				etLoginPasswd.setText("");

				// 删除账号资料
				pref.setString(Const.USER_ACCOUNT, "");

				etLoginAccount.requestFocus();
				llLoginErrortip.setVisibility(View.INVISIBLE);
				showInputMethod(etLoginAccount);
			}
		});

//		btClearLoginPasswd.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v) {
//				etLoginPasswd.setText("");
//				etLoginPasswd.requestFocus();
//				llLoginErrortip.setVisibility(View.INVISIBLE);
//				showInputMethod(etLoginPasswd);
//			}
//		});

		// 监听变化
//		etLoginAccount.addTextChangedListener(new LoginTextWatcher(
//				btClearLoginAccount));
//		etLoginPasswd.addTextChangedListener(new LoginTextWatcher(
//				btClearLoginPasswd));

		// 账号焦点改变
//		etLoginAccount
//				.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//					public void onFocusChange(View v, boolean hasFocus) {
//						if (hasFocus) {
//
//							btClearLoginPasswd.setVisibility(View.INVISIBLE);
//							btClearLoginAccount.setVisibility(etLoginAccount
//									.length() > 0 ? View.VISIBLE
//									: View.INVISIBLE);
//
//						} else {
//							btClearLoginAccount.setVisibility(View.INVISIBLE);
//
//						}
//					}
//				});
//
//		// 密码焦点改变
//		etLoginPasswd
//				.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//					public void onFocusChange(View v, boolean hasFocus) {
//						if (hasFocus) {
//
//							btClearLoginAccount.setVisibility(View.INVISIBLE);
//							btClearLoginPasswd.setVisibility(etLoginPasswd
//									.length() > 0 ? View.VISIBLE
//									: View.INVISIBLE);
//
//						} else {
//							btClearLoginPasswd.setVisibility(View.INVISIBLE);
//
//						}
//					}
//				});

		// 下一步，密码框取得焦点
//		etLoginAccount.setOnEditorActionListener(new OnEditorActionListener() {
//
//			@Override
//			public boolean onEditorAction(TextView v, int actionId,
//					KeyEvent event) {
//				if (actionId == EditorInfo.IME_ACTION_NEXT
//						|| actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
//					etLoginPasswd.requestFocus();
//					return true;
//				}
//				return false;
//			}
//		});

		// 点击键盘完成键触发登录
//		etLoginPasswd.setOnEditorActionListener(new OnEditorActionListener() {
//
//			@Override
//			public boolean onEditorAction(TextView v, int actionId,
//					KeyEvent event) {
//				if (actionId == EditorInfo.IME_ACTION_DONE
//						|| actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
//					initLogin();
//					processLogin();
//					return true;
//				}
//				return false;
//			}
//		});
//
//		// 解决键盘遮挡问题
//		etLoginAccount.setOnTouchListener(new OnTouchListener() {
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				llLoginErrortip.setVisibility(View.INVISIBLE);
//				tvLoginErrortip.setText("");
//				btClearLoginAccount.setVisibility(etLoginAccount.length() > 0 ? View.VISIBLE
//						: View.INVISIBLE);
//
//				// 解决键盘遮挡问题
//				changeScrollView();
//				return false;
//			}
//		});
//
//		etLoginPasswd.setOnTouchListener(new OnTouchListener() {
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				llLoginErrortip.setVisibility(View.INVISIBLE);
//				tvLoginErrortip.setText("");
//				btClearLoginPasswd.setVisibility(etLoginPasswd.length() > 0 ? View.VISIBLE
//						: View.INVISIBLE);
//
//				// 解决键盘遮挡问题
//				changeScrollView();
//				return false;
//			}
//		});
//
	}

	// 出键盘
	private void showInputMethod(View view) {
		InputMethodManager inputMethodManager = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_FORCED);
	}

	public void hideSoftInput() {
		InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		im.hideSoftInputFromWindow(etLoginAccount.getWindowToken(), 0);
//		im.hideSoftInputFromWindow(etLoginPasswd.getWindowToken(), 0);
	}

	/**
	 * 用于监控账号和密码中的字符变化
	 * 
	 * @author admin
	 * 
	 */
	private class LoginTextWatcher implements TextWatcher {
		Button mClearBtn;

		LoginTextWatcher(Button button) {
			mClearBtn = button;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			mClearBtn.setVisibility(s.length() > 0 ? View.VISIBLE
					: View.INVISIBLE);

			llLogin.setClickable(true);

			llLoginErrortip.setVisibility(View.INVISIBLE);
			// 去掉错误提示信息
			tvLoginErrortip.setText("");
		}

	}

	/**
	 * 开始启动动画
	 */
	private void startLoadingAnimation() {
		mAnidot.show();
	}

	/**
	 * 停止动画
	 */
	private void stopLoadingAnimation() {

		mAnidot.hide();

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.button_login:

			initLogin();
			processLogin();
			break;

		default:

			break;
		}
	}

	// 初始化登录
	private void initLogin() {
		// 收键盘
		hideSoftInput();

		// 复位
		resetScrollView();

		// 隐藏清除
		btClearLoginAccount.setVisibility(View.INVISIBLE);
//		btClearLoginPasswd.setVisibility(View.INVISIBLE);

	}

	/**
	 * 设置是否登录状态
	 * 
	 */
	private void setTheLoginingState(boolean isLogining) {

		// 登录中则 登录键不能点
		if (isLogining) {

			// 点击登录， 开始转圈圈
			startLoadingAnimation();
			// 去掉错误信息

			llLoginErrortip.setVisibility(View.INVISIBLE);
			tvLoginErrortip.setText("");
			llLogin.setEnabled(false);
			llLogin.setSelected(false);

			// 账号和密码设置不可用状态
			etLoginAccount.setEnabled(false);
//			etLoginPasswd.setEnabled(false);

			// 设置状态
			isNowLogining = true;

			// 设置为登录中
			tvLogin.setText(getResources().getString(R.string.login_logining));
			mAnidot.show();

		}
		// 非登录中则恢复可点击状态，并停止动画
		else {
			llLoginErrortip.setVisibility(View.VISIBLE);

			llLogin.setEnabled(true);
			llLogin.setSelected(true);

			// 账号和密码恢复为可用状态
			etLoginAccount.setEnabled(true);
//			etLoginPasswd.setEnabled(true);

			// 设置状态
			isNowLogining = false;

			stopLoadingAnimation();
			tvLogin.setText(getResources().getString(R.string.login_login));
			mAnidot.hide();
		}

	}

	// 手动登录
	private void processLogin() {
		setTheLoginingState(true);

		if (!NetHelper.isNetworkAvailable(getApplicationContext())) {

			llLoginErrortip.setVisibility(View.VISIBLE);
			tvLoginErrortip.setText("当前网络不可用");
			setTheLoginingState(false);
			return;

		}

		// 获取账号密码
		account = etLoginAccount.getText().toString().trim();
//		passwd = etLoginPasswd.getText().toString().trim();

		// 判断名称和密码
		if (TextUtils.isEmpty(account)) {

			tvLoginErrortip.setText("用户名不能为空，请重新填写");
			setTheLoginingState(false);

			return;
		}
		if (TextUtils.isEmpty(passwd)) {

			tvLoginErrortip.setText("密码不能为空，请重新填写");
			setTheLoginingState(false);
			return;
		}
		
//		XLUserUtil.getInstance().userLogin(account, false, passwd, "", "", "",
//				mLoginListener,// 登录回调
//				null);
	}

	/**
	 * 自动登录
	 */
	private boolean isAutoLogin() {

		initLogin();

//		etLoginPasswd.setText("********");

		// 判断网络
		if (!NetHelper.isNetworkAvailable(getApplicationContext())) {

			llLoginErrortip.setVisibility(View.VISIBLE);
			tvLoginErrortip.setText("当前网络不可用");
			setTheLoginingState(false);
			return false;

		}

		long curTime = System.currentTimeMillis();
		long lastTime = pref.getLong(Const.LAST_TIME, -1);
		String userAccount = pref.getString(Const.USER_ACCOUNT, "");
		String encryptedPassword = pref.getString(Const.EN_PWD, null);
		String passwordCheckNum = pref.getString(Const.PWD_CHECK_NUM, null);
		boolean isAutoLogin = pref.getBoolean(Const.USER_AUTO_LOGIN, false);

		// 符合免登录条件
		if (lastTime != -1
				&& curTime - lastTime < GlobalConfig.getInstance()
						.getNoNeedLoginTimeDiv() && encryptedPassword != null
				&& passwordCheckNum != null && isAutoLogin
				&& userAccount != null) {
			setTheLoginingState(true);
			hideSoftInput();

//			XLUserUtil.getInstance().userLogin(userAccount, false,
//					encryptedPassword, passwordCheckNum, null, null,
//
//					mLoginListener, null);
			return true;
		} else {

			return false;

		}

	}

	private void getLogoURL() {
		// 获取用户logo
//		List<XLUserInfo.USERINFOKEY> infoKeyList = new ArrayList<XLUserInfo.USERINFOKEY>();
//
//		infoKeyList.add(USERINFOKEY.ImgURL);
//		XLUserUtil.getInstance().getUserInfo(infoKeyList,
//				new XLOnUserListener() {
//
//					public boolean onUserInfoCatched(int errorCode,
//							List<USERINFOKEY> catchedInfoList,
//							XLUserInfo userInfo, Object userdata) {
//
//						if (0 == errorCode) {
//
//							pref.setString(Const.IMG_URL,
//									userInfo.getStringValue(USERINFOKEY.ImgURL));
//
//						} else {
//
//							// 获取图片失败
//							XLLog.w(TAG, errorCode + "获取logo图片失败");
//
//						}
//
//						privilege();
//
//						// 上报对应的userid，以便知道用户
//
//						TestinAgent.setUserInfo(userId + "");
//
//						return true;
//					}
//
//				}, null);

	}

	private static TrustManager myX509TrustManager = new X509TrustManager() {

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}

		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}
	};

	static public String SendHttpsPOST(String url, List<NameValuePair> param,
			String data) throws Exception {
		String result = null;

		// 使用此工具可以将键值对编码成"Key=Value&amp;Key2=Value2&amp;Key3=Value3&rdquo;形式的请求参数
		String requestParam = URLEncodedUtils.format(param, "UTF-8");
		// 设置SSLContext
		SSLContext sslcontext = SSLContext.getInstance("TLS");
		sslcontext.init(null, new TrustManager[] { myX509TrustManager }, null);

		// 打开连接
		// 要发送的POST请求url?Key=Value&amp;Key2=Value2&amp;Key3=Value3的形式
		URL requestUrl = new URL(url + "?" + requestParam);

		HttpsURLConnection httpsConn = (HttpsURLConnection) requestUrl
				.openConnection();
		httpsConn.setRequestProperty("Cookie", AppRestClient.getCookie());
		// 设置套接工厂
		httpsConn.setSSLSocketFactory(sslcontext.getSocketFactory());
		httpsConn.setConnectTimeout(5000);
		// 加入数据
		httpsConn.setRequestMethod("POST");
		httpsConn.setDoOutput(true);
		for (int i = 0; i < 10; i++)
			try {
				DataOutputStream out = new DataOutputStream(
						httpsConn.getOutputStream());
				if (data != null)
					out.writeBytes(data);

				out.flush();
				out.close();

				// 获取输入流
				BufferedReader in = new BufferedReader(new InputStreamReader(
						httpsConn.getInputStream()));
				int code = httpsConn.getResponseCode();
				if (HttpsURLConnection.HTTP_OK == code) {
					String temp = in.readLine();
					/* 连接成一个字符串 */
					while (temp != null) {
						if (result != null)
							result += temp;
						else
							result = temp;
						temp = in.readLine();
					}
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		return result;
	}

	private int retryCnt = 0;

	/**
	 * 查询用户是否是白名单
	 */
	private void privilege() {

//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				try {
//					List<NameValuePair> pairs = new ArrayList<NameValuePair>();
//					NameValuePair pair = new BasicNameValuePair("r",
//							"usr/privilege");
//					pairs.add(pair);
//					String resp = SendHttpsPOST("https://red.xunlei.com/",
//							pairs, null);
//					final PriviledgeResp priviledgeResp = GsonHelper
//							.getGsonInstance().fromJson(resp,
//									PriviledgeResp.class);
//					runOnUiThread(new Runnable() {
//						@Override
//						public void run() {
//							int retCode = priviledgeResp.getReturnCode();
//							XLLog.log(TAG, "privilege onSuccess");
//
//							// 是白名单，具有交易资格
//							if (retCode == ErrCode.OK) {
//
//								// 获取系统配置值
//								updateConfig();
//
//								// 保存用户的邮箱
//								pref.setString(Const.USER_MID,
//										priviledgeResp.getMid());
//
//								pref.setString(Const.USER_PHONE,
//										priviledgeResp.getPhone());
//								long appLastUpdateTime = AppInfoUtil
//										.getLastUpdateTime(LoginActivity.this);
//								if (GlobalConfig.getInstance()
//										.getLastUpdateTime() >= appLastUpdateTime) {
//									MainActivity.startActivity(
//											LoginActivity.this, false);
//									ReportUtil.reportLogin(LoginActivity.this);
//								} else {
//									NewVersionActivity
//											.startActivity(LoginActivity.this);
//									GlobalConfig.getInstance()
//											.setLastUpdateTime(
//													appLastUpdateTime);
//								}
//
//								finish();
//							} else if (retCode == ErrCode.WHITE_FAIL_NOPRIVILEGE
//									|| retCode == ErrCode.WHITE_FAIL_OLDPRIVILEGE) {
//
//								pref.setBoolean(Const.USER_AUTO_LOGIN, false);
//
//								Intent intent = new Intent();
//
//								intent.setClass(LoginActivity.this,
//										LoginFailActivity.class);
//
//								startActivity(intent);
//								// finish();
//
//							} else {
//								tvLoginErrortip.setText(priviledgeResp
//										.getReturnDesc());
//								XLLog.e(TAG, priviledgeResp.getReturnDesc());
//								setTheLoginingState(false);
//
//							}
//
//						}
//					});
//
//				} catch (Exception e) {
//					e.printStackTrace();
//					runOnUiThread(new Runnable() {
//						@Override
//						public void run() {
//							tvLoginErrortip.setText("网络不可用，请检查网络设置");
//							setTheLoginingState(false);
//						}
//					});
//				}
//			}
//		}).start();
//		
		
		AppRestClient.privilege(new ResponseCallback<PriviledgeResp>(
				WifiWorldApplication.getInstance()) {

			public void onSuccess(JSONObject paramJSONObject,
					PriviledgeResp priviledgeResp) {
				int retCode = priviledgeResp.getReturnCode();

				// 是白名单，具有交易资格
				if (retCode == ErrCode.OK) {

					// 获取系统配置值
					updateConfig();

					// 保存用户的邮箱
					pref.setString(Const.USER_MID, priviledgeResp.getMid());

					pref.setString(Const.USER_PHONE, priviledgeResp.getPhone());
					long appLastUpdateTime = AppInfoUtil
							.getLastUpdateTime(LoginActivity.this);
					if (GlobalConfig.getInstance().getLastUpdateTime() >= appLastUpdateTime) {
						MainActivity.startActivity(LoginActivity.this, false);
						ReportUtil.reportLogin(LoginActivity.this);
					} else {
						NewVersionActivity.startActivity(LoginActivity.this);
						GlobalConfig.getInstance().setLastUpdateTime(
								appLastUpdateTime);
					}

					finish();
				} else if (retCode == ErrCode.WHITE_FAIL_NOPRIVILEGE
						|| retCode == ErrCode.WHITE_FAIL_OLDPRIVILEGE) {

					pref.setBoolean(Const.USER_AUTO_LOGIN, false);

					Intent intent = new Intent();

					intent.setClass(LoginActivity.this, LoginFailActivity.class);

					startActivity(intent);
					// finish();

				} else {
					tvLoginErrortip.setText(priviledgeResp.getReturnDesc());
					XLLog.e(TAG, priviledgeResp.getReturnDesc());
					setTheLoginingState(false);

				}

			}

			public void onFailure(int paramInt, Throwable paramThrowable) {
				XLLog.log(TAG, "privilege onFailure");
				// if (retryCnt < 3){
				// privilege();
				// retryCnt++;
				// XLLog.log(TAG, "privilege retryCnt:" + retryCnt);
				//
				// return;
				// }
				if (paramThrowable.toString().startsWith(
						"javax.net.ssl.SSLPeerUnverifiedException")) {
					tvLoginErrortip.setText("请检查手机时间是否正确");
				} else {
					tvLoginErrortip.setText("网络不可用，请检查网络设置");
				}

				XLLog.e(TAG, paramThrowable.toString());
				setTheLoginingState(false);

			}

		});

	}

	/**
	 * 获取全局值
	 */
	private void updateConfig() {

		AppRestClient.getAppConfig(new ResponseCallback<AppConfigResp>(
				WifiWorldApplication.getInstance()) {

			public void onSuccess(JSONObject paramJSONObject,
					AppConfigResp appConfigResp) {
				int retCode = appConfigResp.getReturnCode();

				// 是白名单，具有交易资格
				if (retCode == ErrCode.OK) {
					GlobalConfig.getInstance().updateConfig(
							appConfigResp.getConfig());

				} else {
					XLLog.e(TAG, appConfigResp.getReturnDesc());
				}

			}

			public void onFailure(int paramInt, Throwable paramThrowable) {
				XLLog.e(TAG, paramThrowable);
			}

		});

	}

	@Override
	protected void doDisNetworkConnected() {

		super.doDisNetworkConnected();
	}

	@Override
	protected void doNetworkConnected() {

		super.doNetworkConnected();

		hideSoftInput();

		// 网络恢复会尝试自动登录
		if (!isAutoLogin()) {
			// 手动登录
			initManualLogin();
		}

	}

	// 解决键盘遮挡
	/**
	 * 使ScrollView指向底部
	 */

	Handler h = new Handler();

	private void changeScrollView() {
		h.postDelayed(new Runnable() {
			@Override
			public void run() {
				//slLogin.scrollTo(0, slLogin.getHeight());
				// slLogin.scrollTo(0, screenHeight);
			}
		}, 300);
	}

	private void resetScrollView() {
		h.postDelayed(new Runnable() {
			@Override
			public void run() {
				//slLogin.scrollTo(0, 0);
			}
		}, 300);
	}

	/**
	 * 取消登录
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:

			if (isNowLogining) {
				setTheLoginingState(false);
				llLoginErrortip.setVisibility(View.INVISIBLE);
			} else {
				onPressBack();
			}

			return true;
		case KeyEvent.KEYCODE_HOME:

		}
		return super.onKeyDown(keyCode, event);
	}

	private long mLastPressBackTime = 0;

	private final long BACK_TIME_GAP = 2000;

	private void onPressBack() {
		long curPressBackTime = System.currentTimeMillis();
		long diffTime = curPressBackTime - mLastPressBackTime;
		mLastPressBackTime = curPressBackTime;
		if (diffTime > BACK_TIME_GAP) {
			Toast.makeText(this, "再按一次退出", Toast.LENGTH_LONG).show();
		} else {
			WifiWorldApplication.getInstance().exitAplication();
		}
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		super.onActivityResult(arg0, arg1, arg2);
	}

}
