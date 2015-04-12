package com.anynet.wifiworld.me;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cn.bmob.v3.listener.UpdateListener;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.app.BaseActivity;
import com.anynet.wifiworld.data.UserProfile;
import com.anynet.wifiworld.util.LoginHelper;
import com.anynet.wifiworld.view.SettingEditItemView;
import com.anynet.wifiworld.view.SettingEditItemView.ClickButtonListener;
import com.anynet.wifiworld.view.SettingEditItemView.ClickEditButtonListener;
import com.anynet.wifiworld.view.SettingItemView;

public class MyAccountActivity extends BaseActivity {

	UserProfile mUserProfile;
	LoginHelper mLoginHelper;
	SettingEditItemView sexIV;
	SettingItemView si;
	private static int REQUEST_LIST_SIMPLE = 10000;

	private void bingdingTitleUI() {
		mTitlebar.ivHeaderLeft.setVisibility(View.VISIBLE);
		mTitlebar.llFinish.setVisibility(View.VISIBLE);
		// mTitlebar.llHeaderMy.setVisibility(View.INVISIBLE);
		mTitlebar.tvHeaderRight.setVisibility(View.INVISIBLE);
		mTitlebar.tvTitle.setText(getString(R.string.my_account_title));
		mTitlebar.ivHeaderLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_user_homepage);
		super.onCreate(savedInstanceState);
		bingdingTitleUI();
		mLoginHelper = LoginHelper.getInstance(getApplicationContext());
		mUserProfile = mLoginHelper.getCurLoginUserInfo();
		if (mUserProfile == null) {
			Toast.makeText(this, "未登录，请重新登录！", Toast.LENGTH_LONG).show();
			finish();
			return;
		}

		final TextView tvName = (TextView) findViewById(R.id.person_name);
		si = (SettingItemView) findViewById(R.id.siv_account);
		final TextView tvContent = (TextView) si.findViewById(R.id.setting_item_content);
		tvName.setText(mUserProfile.PhoneNumber);
		tvContent.setText(mUserProfile.PhoneNumber);
		si.findViewById(R.id.setting_item_next).setVisibility(View.INVISIBLE);

		final SettingEditItemView nicknameIV = (SettingEditItemView) findViewById(R.id.siv_alias);
		if (mUserProfile.NickName == null || mUserProfile.NickName.equals("")) {
			nicknameIV.setContent(mUserProfile.PhoneNumber);
		} else {
			nicknameIV.setContent(mUserProfile.NickName);
		}
		// String[] titleArr = getResources().getStringArray(R.array.gender);

		nicknameIV.setClickEditButtonListener(new ClickEditButtonListener() {

			@Override
			public void onSave(CharSequence data) {
				// TODO Auto-generated method stub
				mUserProfile.NickName = data.toString();
				mUserProfile.update(getApplicationContext(), new UpdateListener() {

					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						Toast.makeText(MyAccountActivity.this, "保存成功！", Toast.LENGTH_LONG).show();
					}

					@Override
					public void onFailure(int arg0, String arg1) {
						// TODO Auto-generated method stub
						Toast.makeText(MyAccountActivity.this, "失败！int：" + arg0 + " String:" + arg1, Toast.LENGTH_LONG)
								.show();
					}
				});
				nicknameIV.setContent(mUserProfile.NickName);
			}

			@Override
			public void beforeEdit() {
				// TODO Auto-generated method stub
			}
		});

		sexIV = (SettingEditItemView) findViewById(R.id.sev_sex);
		sexIV.setContent(mUserProfile.getSex());
		sexIV.setClickEditButtonListener(new ClickEditButtonListener() {

			@Override
			public void onSave(CharSequence charSequence) {
				// TODO Auto-generated method stub
				mUserProfile.setSex(charSequence.toString());
				mUserProfile.update(getApplicationContext(), new UpdateListener() {

					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						Toast.makeText(MyAccountActivity.this, "保存成功！", Toast.LENGTH_LONG).show();
					}

					@Override
					public void onFailure(int arg0, String arg1) {
						// TODO Auto-generated method stub
						Toast.makeText(MyAccountActivity.this, "失败！int：" + arg0 + " String:" + arg1, Toast.LENGTH_LONG)
								.show();
					}
				});
				sexIV.setContent(mUserProfile.getSex());
			}

			@Override
			public void beforeEdit() {
				// TODO Auto-generated method stub

			}
		});

		findViewById(R.id.button_login).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LoginHelper.getInstance(getApplicationContext()).logout();
				finish();
			}
		});

		

		((SettingEditItemView) findViewById(R.id.sev_email)).setClickButtonListener(new ClickButtonListener() {
			@Override
			public void onClick(CharSequence charSequence) {
//				final EditText inputServer = new EditText(MyAccountActivity.this);
//				AlertDialog.Builder builder = new AlertDialog.Builder(MyAccountActivity.this);
//				builder.setTitle("Server").setIcon(android.R.drawable.ic_dialog_info).setView(inputServer)
//						.setNegativeButton("Cancel", null);
//				builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//
//					public void onClick(DialogInterface dialog, int which) {
//						String value = inputServer.getText().toString();
//						((SettingEditItemView) findViewById(R.id.sev_email)).setContent(value);
//					}
//				});
//				builder.show();
			}
		});
		((SettingEditItemView) findViewById(R.id.sev_password)).setClickButtonListener(new ClickButtonListener() {
			@Override
			public void onClick(CharSequence charSequence) {

			}
		});

		((SettingEditItemView) findViewById(R.id.sev_sex)).setClickButtonListener(new ClickButtonListener() {
			@Override
			public void onClick(CharSequence charSequence) {
			}
		});
		((SettingEditItemView) findViewById(R.id.sev_age)).setClickButtonListener(new ClickButtonListener() {
			@Override
			public void onClick(CharSequence charSequence) {
				final Calendar c = Calendar.getInstance();
				DatePickerDialog dialog = new DatePickerDialog(MyAccountActivity.this,
						new DatePickerDialog.OnDateSetListener() {
							@Override
							public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
								c.set(year, monthOfYear, dayOfMonth);
								((SettingEditItemView) findViewById(R.id.sev_age)).setContent(DateFormat.format(
										"yyy-MM-dd", c).toString());
							}
						}, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
				dialog.show();
			}
		});
		((SettingEditItemView) findViewById(R.id.sev_job)).setClickButtonListener(new ClickButtonListener() {
			@Override
			public void onClick(CharSequence charSequence) {
			}
		});
		((SettingEditItemView) findViewById(R.id.sev_interest)).setClickButtonListener(new ClickButtonListener() {
			@Override
			public void onClick(CharSequence charSequence) {
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

}
