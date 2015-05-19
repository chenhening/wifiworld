package com.anynet.wifiworld.wifi.ui;

import com.anynet.wifiworld.R;
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
		
		final ProgressBar progressBar = (ProgressBar)findViewById(R.id.encrypt_process);
		final ImageView imageView = (ImageView)findViewById(R.id.encrypt_result);
		final TextView textView = (TextView)findViewById(R.id.wifi_encrypt_text);
		progressBar.setVisibility(View.VISIBLE);
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				progressBar.setVisibility(View.GONE);
				imageView.setVisibility(View.VISIBLE);
				textView.setText(textView.getText() + "(" + security + ")");
			}
		}, 1000);
		
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
