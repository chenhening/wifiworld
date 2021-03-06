package com.anynet.wifiworld.wifi.ui;

import java.security.PublicKey;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.wifi.WifiAdmin;
import com.wolf.routermanager.bean.AllRouterInfoBean;
import com.wolf.routermanager.common.RouterUtilFactory;
import com.wolf.routermanager.inter.ConnInfoCallBack;

import android.R.bool;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WifiAdvanceActivity extends Activity {
	private final static String TAG = WifiAdvanceActivity.class.getSimpleName();

	private Context mContext;
	
	private TextView mWifiName;
	private WifiAdmin mWifiAdmin;
	
	private static int checkIdx = 0;
	private RouterLoginThread mRouterLoginThread;
	
	private RouterLoginDialog mLoginDialog;
	private int mLoginSuccessFlag = -1;
	private Handler mCheckHandler = new Handler();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.wifi_advance_activity);
		super.onCreate(savedInstanceState);
		
		mContext = this;
		
		Intent intent = getIntent();
		final String security = intent.getStringExtra("security");
		
		mWifiAdmin = WifiAdmin.getInstance(this);
		mWifiName = (TextView)findViewById(R.id.wifi_advance_name);
		mWifiName.setText(WifiAdmin.convertToNonQuotedString(mWifiAdmin.getWifiNameConnection()));
		
		mRouterLoginThread = new RouterLoginThread(mContext);
		
		final int[] progressBars = {R.id.encrypt_process, R.id.dns_process, R.id.arp_process, R.id.wps_process,
				R.id.reported_process, R.id.firewall_process};
		final int[] imageResults = {R.id.encrypt_result, R.id.dns_result, R.id.arp_result, R.id.wps_result,
				R.id.reported_result, R.id.firewall_result};
		final TextView textView = (TextView)findViewById(R.id.wifi_encrypt_text);
		checkIdx = 0;
		findViewById(progressBars[checkIdx]).setVisibility(View.VISIBLE);
		Runnable checkRunnable = new Runnable() {
			
			@Override
			public void run() {
				findViewById(progressBars[checkIdx]).setVisibility(View.GONE);
				findViewById(imageResults[checkIdx]).setVisibility(View.VISIBLE);
				if (checkIdx++ == 0) {
					textView.setText(textView.getText() + "(" + security + ")");
				} else if (checkIdx > 5) {
					return;
				}
				findViewById(progressBars[checkIdx]).setVisibility(View.VISIBLE);
				mCheckHandler.postDelayed(this, 1000);
			}
		};
		mCheckHandler.post(checkRunnable);
		
		setDeviceConnectedClickListner();
		
		mLoginDialog = new RouterLoginDialog(mContext);
		setRouterBtnClickListner();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onBackPressed() {
		finishWithAnimation();
	}
	
	private void finishWithAnimation() {
		mCheckHandler.removeCallbacksAndMessages(null);
		finish();
		overridePendingTransition(R.anim.hold, R.anim.slide_left_out);
	}
	
	private void setDeviceConnectedClickListner() {
		LinearLayout deviceLayout = (LinearLayout)findViewById(R.id.devices_connected);
		deviceLayout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				Intent peerIntent = new Intent(mContext, WifiPeersListActivity.class);
				startActivity(peerIntent);
			}
		});
	}
	
	private void setRouterBtnClickListner() {
		LinearLayout rebootLayout = (LinearLayout)findViewById(R.id.router_rebbot);
		rebootLayout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				//need thread to supervise mLoginSuccessFlag
				if (RouterUtilFactory.isSupportRouter(mContext)) {
					boolean autoLoginFlag = isAutoLogin(new LoginSuccessCallback() {
						
						@Override
						public void routerOperator() {
							AllRouterInfoBean.routerUtilInterface.reboot(new ConnInfoCallBack() {

								@Override
								public void putData(boolean flag) {
									if (!flag) {
										Toast.makeText(mContext,"Failed to reboot router",
														Toast.LENGTH_LONG).show();
									}
								}
							});
						}
					});
					if (!autoLoginFlag) {
						showRouterLoginDialog("自动登录路由器失败，请输入路由信息");
					}
				} else {
					Toast.makeText(mContext, "Not Support Router", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	
	private void showRouterLoginDialog(String title) {
		mLoginDialog.setTitle(title);

		mLoginDialog.setLeftBtnStr("取消");
		mLoginDialog.setRightBtnStr("登录");
		mLoginDialog.clearPwdEditText();

		mLoginDialog.setRightBtnListener(new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				String name = mLoginDialog.getUserAccountName();
				String pwd = mLoginDialog.getPwdContent();
				if (name.equals("")) {
					Toast.makeText(mContext, "请输入路由器用户名", Toast.LENGTH_LONG).show();
					return;
				}
				
				//shutdown soft keyboard if soft keyboard is actived
				InputMethodManager imm = (InputMethodManager)mContext.getSystemService(mContext.INPUT_METHOD_SERVICE);
				if (imm.isActive()) {
					imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
				}
				dialog.dismiss();
				
				RouterLoginThread routerLoginThread = new RouterLoginThread(mContext);
				routerLoginThread.setRouterAccount(name, pwd);
				routerLoginThread.run(new RouterLoginThread.LoginRouterCallback() {
					
					@Override
					public void onLoginSuccess() {
						Toast.makeText(mContext, "Login Router Success", Toast.LENGTH_LONG).show();
					}
					
					@Override
					public void onLoginFailed() {
						Toast.makeText(mContext, "Login Router Failed", Toast.LENGTH_LONG).show();
					}
				});
			}
		});

		mLoginDialog.setLeftBtnListener(new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				dialog.dismiss();

			}
		});

		mLoginDialog.show();
	}
	
	private boolean isAutoLogin(final LoginSuccessCallback callback) {
			String loginName = RouterUtilFactory.getRouterAccountName(mContext);
			if (loginName == null) {
				return false;
			} else {
				mRouterLoginThread.setRouterAccount(loginName, RouterUtilFactory.getRouterAccountPwd(mContext));
				mRouterLoginThread.run(new RouterLoginThread.LoginRouterCallback() {
					
					@Override
					public void onLoginSuccess() {
						 callback.routerOperator();
					}
					
					@Override
					public void onLoginFailed() {
						showRouterLoginDialog("自动登录路由器失败，请重新输入");
					}
				});
				return true;
			}
	}
	
	public interface LoginSuccessCallback {
		public void routerOperator();
	}
}
