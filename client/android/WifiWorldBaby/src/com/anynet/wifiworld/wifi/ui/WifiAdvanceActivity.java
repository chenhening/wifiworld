package com.anynet.wifiworld.wifi.ui;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.util.HandlerUtil.StaticHandler;
import com.anynet.wifiworld.wifi.WifiAdmin;
import com.wolf.routermanager.bean.AllRouterInfoBean;
import com.wolf.routermanager.common.RouterUtilFactory;
import com.wolf.routermanager.inter.ConnInfoCallBack;
import com.wolf.routermanager.login.CheckLogin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class WifiAdvanceActivity extends Activity {
	private final static String TAG = WifiAdvanceActivity.class.getSimpleName();

	private Context mContext;
	private CheckLogin mCheckLogin;
	
	private TextView mWifiName;
	private WifiAdmin mWifiAdmin;
	
	private static int checkIdx = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.wifi_advance_activity);
		super.onCreate(savedInstanceState);
		
		mContext = getApplicationContext();
		mCheckLogin = new CheckLogin(mContext);
		
		Intent intent = getIntent();
		final String security = intent.getStringExtra("security");
		
		mWifiAdmin = WifiAdmin.getInstance(this);
		mWifiName = (TextView)findViewById(R.id.wifi_advance_name);
		mWifiName.setText(WifiAdmin.convertToNonQuotedString(mWifiAdmin.getWifiNameConnection()));
		
		final int[] progressBars = {R.id.encrypt_process, R.id.dns_process, R.id.arp_process, R.id.wps_process,
				R.id.reported_process, R.id.firewall_process};
		final int[] imageResults = {R.id.encrypt_result, R.id.dns_result, R.id.arp_result, R.id.wps_result,
				R.id.reported_result, R.id.firewall_result};
		final TextView textView = (TextView)findViewById(R.id.wifi_encrypt_text);
		final Handler checkHandler = new Handler();
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
				checkHandler.postDelayed(this, 1000);
			}
		};
		checkHandler.post(checkRunnable);
		
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
		finish();
		overridePendingTransition(R.anim.hold, R.anim.slide_left_out);
	}
	
	private void setRouterBtnClickListner() {
		LinearLayout rebootLayout = (LinearLayout)findViewById(R.id.router_rebbot);
		rebootLayout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				if (RouterUtilFactory.isSupportRouter(mContext)) {
					mCheckLogin.login("admin", "admin", new ConnInfoCallBack() {
						
						@Override
						public void putData(boolean flag) {
							if (!flag) {
								Toast.makeText(mContext, "Failed to login router", Toast.LENGTH_LONG).show();
							} else {
								AllRouterInfoBean.routerUtilInterface.reboot(new ConnInfoCallBack() {
									
									@Override
									public void putData(boolean flag) {
										if (!flag) {
											Toast.makeText(mContext, "Failed to reboot router", Toast.LENGTH_LONG).show();
										}
									}
								});
							}
						}
					});
				} else {
					Toast.makeText(mContext, "Not Supported Router", Toast.LENGTH_LONG).show();
				}
			}
		});
	}
}
