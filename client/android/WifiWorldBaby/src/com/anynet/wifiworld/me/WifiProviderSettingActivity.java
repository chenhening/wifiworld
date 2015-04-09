package com.anynet.wifiworld.me;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.anynet.wifiworld.MainActivity;
import com.anynet.wifiworld.R;
import com.anynet.wifiworld.app.BaseActivity;
import com.anynet.wifiworld.data.DataCallback;
import com.anynet.wifiworld.data.WifiMessages;
import com.anynet.wifiworld.knock.KnockTopActivity;
import com.anynet.wifiworld.util.LoginHelper;

public class WifiProviderSettingActivity extends BaseActivity {

	//IPC
	private Intent mIntent = null;
	private Activity activity = this;
	
	private void bingdingTitleUI() {
		mTitlebar.ivHeaderLeft.setVisibility(View.VISIBLE);
		mTitlebar.llFinish.setVisibility(View.VISIBLE);
		mTitlebar.tvHeaderRight.setVisibility(View.INVISIBLE);
		mTitlebar.tvTitle.setText(getString(R.string.wifi_provider));
		mTitlebar.ivMySetting.setVisibility(View.GONE);
		mTitlebar.ivHeaderLeft.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mIntent = getIntent();
		setContentView(R.layout.wifi_provider_setting);
		super.onCreate(savedInstanceState);
		bingdingTitleUI();
		
		//取消wifi
		this.findViewById(R.id.slv_change_provider_info).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				new AlertDialog.Builder(activity)
					.setTitle("取消WiFi共享").setMessage("确定解绑并取消共享此WiFi?")   
					.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							LoginHelper.getInstance(getApplicationContext()).mWifiProfile.deleteRemote(getApplicationContext());
							mIntent.setClass(getApplicationContext(), MainActivity.class);
							startActivity(mIntent);
						}
						
					})  
					.setNegativeButton("取消", null)
					.show();
			}
		});
		this.findViewById(R.id.slv_cancle_provider_info).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				new AlertDialog.Builder(activity)
					.setTitle("取消WiFi共享").setMessage("确定解绑并取消共享此WiFi?")  
					.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
	
						@Override
						public void onClick(DialogInterface dialog, int which) {
							LoginHelper.getInstance(getApplicationContext()).mWifiProfile.deleteRemote(getApplicationContext());
							mIntent.setClass(getApplicationContext(), MainActivity.class);
							startActivity(mIntent);
						}
						
					})  
					.setNegativeButton("取消", null)
					.show();
			}
		});
		
		this.findViewById(R.id.slv_my_knock_setting).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), KnockTopActivity.class);
				startActivity(i);
			}
		});
		
		//提交和重置
		final EditText edit = (EditText) this.findViewById(R.id.ec_wifi_message_content);
		this.findViewById(R.id.btn_wifi_message_send).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String str = edit.getText().toString();
				if (str.length() <= 0) {
					showToast("未输入任何信息，请重新输入。");
					return;
				}
				WifiMessages msg = new WifiMessages();
				msg.MacAddr = LoginHelper.getInstance(getApplicationContext()).mWifiProfile.MacAddr;
				msg.Message = edit.getText().toString();
				msg.MarkSendTime();
				msg.StoreRemote(getApplicationContext(), new DataCallback<WifiMessages>() {

					@Override
					public void onSuccess(WifiMessages object) {
						showToast("提交动态信息成功。");
					}

					@Override
					public void onFailed(String msg) {
						showToast("提交动态信息失败。");
					}
					
				});
			}	
		});
		this.findViewById(R.id.btn_wifi_message_clear).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				edit.getText().clear();
			}
			
		});
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
		super.onDestroy();
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

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}
}
