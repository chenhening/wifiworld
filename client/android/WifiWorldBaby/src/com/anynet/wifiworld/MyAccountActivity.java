package com.anynet.wifiworld;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cn.bmob.v3.listener.UpdateListener;

import com.anynet.wifiworld.app.BaseActivity;
import com.anynet.wifiworld.data.UserProfile;
import com.anynet.wifiworld.util.LoginHelper;
import com.anynet.wifiworld.view.SettingEditItemView;
import com.anynet.wifiworld.view.SettingEditItemView.ClickEditButtonListener;
import com.anynet.wifiworld.view.SettingItemView;

public class MyAccountActivity extends BaseActivity {

	UserProfile mUserProfile;
	LoginHelper mLoginHelper;
	SettingItemView sexIV;
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

	SettingItemView si;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setContentView(R.layout.user_homepage_activity);
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
		List<String> datas = new ArrayList<String>();
		for (String string : UserProfile.SexArray) {
			datas.add(string);
		}
		nicknameIV.setDatas(datas);
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
		/*
		 * nicknameIV.findViewById(R.id.setting_item_next).setOnClickListener(new
		 * OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { // TODO Auto-generated method
		 * stub AlertDialog.Builder builder = new
		 * AlertDialog.Builder(MyAccountActivity.this); final EditText
		 * inputServer = new EditText(MyAccountActivity.this);
		 * builder.setTitle("昵称"
		 * ).setIcon(android.R.drawable.ic_dialog_info).setView(inputServer)
		 * .setNegativeButton("取消", null); builder.setPositiveButton("保存", new
		 * DialogInterface.OnClickListener() {
		 * 
		 * public void onClick(DialogInterface dialog, int which) {
		 * mUserProfile.NickName = inputServer.getText().toString();
		 * mUserProfile.update(getApplicationContext(), new UpdateListener() {
		 * 
		 * @Override public void onSuccess() { // TODO Auto-generated method
		 * stub Toast.makeText(MyAccountActivity.this, "保存成功！",
		 * Toast.LENGTH_LONG).show(); }
		 * 
		 * @Override public void onFailure(int arg0, String arg1) { // TODO
		 * Auto-generated method stub Toast.makeText(MyAccountActivity.this,
		 * "失败！int：" + arg0 + " String:" + arg1, Toast.LENGTH_LONG).show(); }
		 * }); nicknameIV.setContent(mUserProfile.NickName); } });
		 * builder.show();
		 * 
		 * } });
		 */
		// si = (SettingItemView) findViewById(R.id.siv_psw);
		// String psw =
		// (LoginHelper.getInstance(getApplicationContext()).getCurLoginUserInfo()).Password;
		// try {
		// if (psw != null && !psw.equals(""))
		// si.setContent(StringCrypto.decryptDES(psw, UserProfile.CryptoKey));
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		sexIV = (SettingItemView) findViewById(R.id.siv_sex);
		sexIV.setContent(mUserProfile.getSex());
		sexIV.findViewById(R.id.setting_item_next).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// ListDialogFragment.createBuilder(MyAccountActivity.this,
				// getSupportFragmentManager())
				// .setChoiceMode(AbsListView.CHOICE_MODE_SINGLE).setTitle("选择性别").setItems(UserProfile.SexArray)
				// .setRequestCode(REQUEST_LIST_SIMPLE).show();
				new AlertDialog.Builder(MyAccountActivity.this) // build
																// AlertDialog
						.setTitle("选择性别") // title
						.setItems(UserProfile.SexArray, new DialogInterface.OnClickListener() {
							// content

							@Override
							public void onClick(DialogInterface dialog, int which) {
								mUserProfile.setSex(UserProfile.SexArray[which]);
								mUserProfile.update(getApplicationContext(), new UpdateListener() {

									@Override
									public void onSuccess() {
										// TODO Auto-generatedmethod stub
										Toast.makeText(MyAccountActivity.this, "保存成功！", Toast.LENGTH_LONG).show();
									}

									@Override
									public void onFailure(int arg0, String arg1) {
										// TODO Auto-generated method stub
										Toast.makeText(MyAccountActivity.this, "失败！int：" + arg0 + " String:" + arg1,
												Toast.LENGTH_LONG).show();
									}
								});
								sexIV.setContent(UserProfile.SexArray[which]);
							}
						}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss(); // 关闭alertDialog
							}
						}).show();
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
