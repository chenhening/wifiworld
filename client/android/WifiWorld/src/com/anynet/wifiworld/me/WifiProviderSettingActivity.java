package com.anynet.wifiworld.me;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.anynet.wifiworld.MainActivity;
import com.anynet.wifiworld.R;
import com.anynet.wifiworld.app.BaseActivity;
import com.anynet.wifiworld.data.DataCallback;
import com.anynet.wifiworld.data.WifiMessages;
import com.anynet.wifiworld.data.WifiProfile;
import com.anynet.wifiworld.data.WifiQuestions;
import com.anynet.wifiworld.knock.KnockStepFirstActivity;
import com.anynet.wifiworld.util.LoginHelper;
import com.anynet.wifiworld.wifi.ui.WifiDetailsActivity;

public class WifiProviderSettingActivity extends BaseActivity {

	// IPC
	private Intent mIntent = null;
	private Activity activity = this;
	LoginHelper mLoginHelper;
	WifiProfile mWifiProfile;

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
		mLoginHelper = LoginHelper.getInstance(getApplicationContext());
		mWifiProfile = mLoginHelper.mWifiProfile;
		// 取消wifi
		this.findViewById(R.id.slv_change_provider_info).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				new AlertDialog.Builder(activity).setTitle("取消WiFi共享").setMessage("确定解绑并取消共享此WiFi?")
						.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {

								mWifiProfile.deleteRemote(getApplicationContext());
								mLoginHelper.mWifiProfile = mWifiProfile = null;
								mIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
								mIntent.setClass(getApplicationContext(), MainActivity.class);
								startActivity(mIntent);
							}

						}).setNegativeButton("取消", null).show();
			}
		});
		this.findViewById(R.id.slv_cancle_provider_info).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				new AlertDialog.Builder(activity).setTitle("取消WiFi共享").setMessage("确定解绑并取消共享此WiFi?")
						.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								mWifiProfile.deleteRemote(getApplicationContext());
								mIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
								mLoginHelper.mWifiProfile = mWifiProfile = null;
								mIntent.setClass(getApplicationContext(), MainActivity.class);
								startActivity(mIntent);
							}

						}).setNegativeButton("取消", null).show();
			}
		});

		findViewById(R.id.tv_setting_knock).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CheckBox ksCB = (CheckBox) findViewById(R.id.knock_switch);
				if (ksCB.isChecked()) {
					ksCB.setChecked(false);
					findViewById(R.id.specific_setting_layout).setVisibility(View.GONE);
				} else {
					ksCB.setChecked(true);
					findViewById(R.id.specific_setting_layout).setVisibility(View.VISIBLE);
					findViewById(R.id.question_setting).setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							//拉取敲门问题
							final WifiQuestions wifiQuestions = new WifiQuestions();
							wifiQuestions.QueryByMacAddress(getApplicationContext(), mWifiProfile.MacAddr, new DataCallback<WifiQuestions>() {
								
								@Override
								public void onSuccess(WifiQuestions object) {
									KnockStepFirstActivity.start(WifiProviderSettingActivity.this.getActivity(), 
										WifiProviderSettingActivity.class.getName(), object);
								}
								
								@Override
								public void onFailed(String msg) {
									KnockStepFirstActivity.start(WifiProviderSettingActivity.this.getActivity(), 
											WifiProviderSettingActivity.class.getName(), wifiQuestions);
								}
							});
						}
					});
				}

			}
		});

		findViewById(R.id.rl_setting_message).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CheckBox ksCB = (CheckBox) findViewById(R.id.message_switch);
				if (ksCB.isChecked()) {
					ksCB.setChecked(false);
					findViewById(R.id.ll_wifi_message).setVisibility(View.GONE);
				} else {
					ksCB.setChecked(true);
					findViewById(R.id.ll_wifi_message).setVisibility(View.VISIBLE);
					// 提交和重置
					final EditText edit = (EditText) findViewById(R.id.ec_wifi_message_content);
					findViewById(R.id.btn_wifi_message_send).setOnClickListener(new OnClickListener() {

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
									edit.setText("");
								}

								@Override
								public void onFailed(String msg) {
									showToast("提交动态信息失败。");
								}

							});
						}
					});

					findViewById(R.id.btn_wifi_message_clear).setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							edit.getText().clear();
						}

					});
				}

			}
		});
	}

	protected Context getActivity() {
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
