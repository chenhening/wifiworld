package com.anynet.wifiworld.me;

import java.io.File;
import java.util.Calendar;

import B.i;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.listener.UpdateListener;

import com.anynet.wifiworld.BaseActivity;
import com.anynet.wifiworld.R;
import com.anynet.wifiworld.data.UserProfile;
import com.anynet.wifiworld.util.BitmapUtil;
import com.anynet.wifiworld.util.LoginHelper;
import com.anynet.wifiworld.view.SettingEditItemView;
import com.anynet.wifiworld.view.SettingEditItemView.ClickButtonListener;
import com.anynet.wifiworld.view.SettingEditItemView.ClickEditButtonListener;

public class MyAccountActivity extends BaseActivity {
	public final static String TAG = MyAccountActivity.class.getSimpleName();

	public UserProfile mUserProfile;
	LoginHelper mLoginHelper;
	
	private Button mLogoutBtn;
	private Button mSaveBtn;
	
	SettingEditItemView mNicknameET;
	SettingEditItemView mEmailET;
	SettingEditItemView mSexTV;
	SettingEditItemView mBirthdayTV;
	SettingEditItemView mJobTV;
	SettingEditItemView mInterestET;
	
	private TextWatcher mTextWatcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			mSaveBtn.setEnabled(true);
		}
		
		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void afterTextChanged(Editable arg0) {
			// TODO Auto-generated method stub
			
		}
	};
	
	private void bingdingTitleUI() {
		mTitlebar.ivHeaderLeft.setVisibility(View.VISIBLE);
		mTitlebar.tvHeaderRight.setVisibility(View.INVISIBLE);
		mTitlebar.tvTitle.setText(getString(R.string.my_account_title));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_user_account);
		super.onCreate(savedInstanceState);
		bingdingTitleUI();

		mLoginHelper = LoginHelper.getInstance(getApplicationContext());
		mUserProfile = mLoginHelper.getCurLoginUserInfo();
		if (mUserProfile == null) {
			Toast.makeText(this, "未登录，请重新登录！", Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		
		mLogoutBtn = (Button) findViewById(R.id.button_logout);
		mSaveBtn = (Button) findViewById(R.id.button_save);
		
		//显示头像
		if (mUserProfile.Avatar != null) {
			Drawable drawable = new BitmapDrawable(this.getResources(), BitmapUtil.Bytes2Bimap(mUserProfile.Avatar));
			ImageView iv_avatar = (ImageView) this.findViewById(R.id.person_icon);
			iv_avatar.setImageDrawable(drawable);
		}

		TextView tvName = (TextView) findViewById(R.id.person_name);
		tvName.setText(mUserProfile.getUsername());

		mNicknameET = (SettingEditItemView) findViewById(R.id.siv_alias);
		if (mUserProfile.NickName == null || mUserProfile.NickName.equals("")) {
			mNicknameET.setEditContent(mUserProfile.getUsername());
		} else {
			mNicknameET.setEditContent(mUserProfile.NickName);
		}
		mNicknameET.addTextChangedListner(mTextWatcher);

		mEmailET = (SettingEditItemView) findViewById(R.id.sev_email);
		if (mUserProfile.getEmail() == null || mUserProfile.getEmail().equals("")) {
			mEmailET.setEditContent("");
		} else {
			mEmailET.setEditContent(mUserProfile.getEmail());
		}
		mEmailET.addTextChangedListner(mTextWatcher);
		
		mSexTV = (SettingEditItemView) findViewById(R.id.sev_sex);
		mSexTV.setContent(mUserProfile.getSex());
		mSexTV.addTextChangedListner(mTextWatcher);

		mBirthdayTV = (SettingEditItemView) findViewById(R.id.sev_age);
		if (mUserProfile.Age == null || mUserProfile.Age.equals("")) {
			mBirthdayTV.setContent("");
		} else {
			mBirthdayTV.setContent(mUserProfile.Age);
		}
		mBirthdayTV.setClickEditListner(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				final Calendar c = Calendar.getInstance();
				DatePickerDialog dialog = new DatePickerDialog(MyAccountActivity.this,
						new DatePickerDialog.OnDateSetListener() {
							@Override
							public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
								c.set(year, monthOfYear, dayOfMonth);
								String content  = DateFormat.format("yyy-MM-dd", c).toString();
								mBirthdayTV.setContentEdited(content);
							}
						}, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
				dialog.show();
			}
		});
		mBirthdayTV.addTextChangedListner(mTextWatcher);

		mJobTV = (SettingEditItemView) findViewById(R.id.sev_job);
		if (mUserProfile.Job == null || mUserProfile.Job.equals("")) {
			mJobTV.setContent("");
		} else {
			mJobTV.setContent(mUserProfile.Job);
		}
		mJobTV.addTextChangedListner(mTextWatcher);

		mInterestET = (SettingEditItemView) findViewById(R.id.sev_interest);
		if (mUserProfile.Interest == null || mUserProfile.Interest.equals("")) {
			mInterestET.setEditContent("");
		} else {
			mInterestET.setEditContent(mUserProfile.Interest);
		}
		mInterestET.addTextChangedListner(mTextWatcher);
		
		//设置头像
		findViewById(R.id.person_signin_tip).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showLogoDialog();
			}
			
		});
		
		mLogoutBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				LoginHelper.getInstance(getApplicationContext()).logout();
				finish();
			}
		});
		
		mSaveBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				String emailStr = mEmailET.getEditContent();
				if (!emailStr.contains("@")) {
					Toast.makeText(getApplicationContext(), "邮箱地址错误", Toast.LENGTH_LONG).show();
					return;
				}
				
				final UserProfile tempProfile = mUserProfile;
				mUserProfile.NickName = mNicknameET.getEditContent();
				mUserProfile.setEmail(emailStr);
				mUserProfile.setSex(mSexTV.getContent());
				mUserProfile.Age = mBirthdayTV.getContent();
				mUserProfile.Job = mJobTV.getContent();
				mUserProfile.Interest = mInterestET.getEditContent();
				mUserProfile.update(getApplicationContext(), new UpdateListener() {
					
					@Override
					public void onSuccess() {
						mNicknameET.setEditContent(mUserProfile.NickName);
						mEmailET.setEditContent(mUserProfile.getEmail());
						mSexTV.setContent(mUserProfile.getSex());
						mBirthdayTV.setContent(mUserProfile.Age);
						mJobTV.setContent(mUserProfile.Job);
						mInterestET.setEditContent(mUserProfile.Interest);
						mSaveBtn.setEnabled(false);
						Toast.makeText(MyAccountActivity.this, "保存成功！", Toast.LENGTH_LONG).show();
					}
					
					@Override
					public void onFailure(int arg0, String arg1) {
						mUserProfile.NickName = tempProfile.NickName;
						mUserProfile.setEmail(tempProfile.getEmail());
						mUserProfile.setSex(tempProfile.getSex());
						mUserProfile.Age = tempProfile.Age;
						mUserProfile.Job = tempProfile.Job;
						mUserProfile.Interest = tempProfile.Interest;
						Toast.makeText(MyAccountActivity.this, "失败！int：" + arg0 + " String:" + arg1, Toast.LENGTH_LONG)
										.show();
					}
				});
			}
		});
	}
	
	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
	
	private String[] items = new String[] { "选择本地图片", "拍照" };
	private static final String IMAGE_FILE_NAME = "image.jpg";
	/** 头像名称 */
	private static final int IMAGE_REQUEST_CODE = 0;
	/** 请求码 */
	private static final int CAMERA_REQUEST_CODE = 1;
	private static final int RESULT_REQUEST_CODE = 2;
	
	private void showLogoDialog() {
		new AlertDialog.Builder(this).setTitle("设置头像").setItems(items, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					Intent intentFromGallery = new Intent();
					intentFromGallery.setType("image/*"); // 设置文件类型
					intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
					startActivityForResult(intentFromGallery, IMAGE_REQUEST_CODE);
					break;
				case 1:
					Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					// 判断存储卡是否可以用，可用进行存储
					String state = Environment.getExternalStorageState();
					if (state.equals(Environment.MEDIA_MOUNTED)) {
						File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
						File file = new File(path, IMAGE_FILE_NAME);
						intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
					}

					startActivityForResult(intentFromCapture, CAMERA_REQUEST_CODE);
					break;
				}
			}
		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).show();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 结果码不等于取消时候
		if (resultCode != RESULT_CANCELED) {
			switch (requestCode) {
			case IMAGE_REQUEST_CODE:
				startPhotoZoom(data.getData());
				break;
			case CAMERA_REQUEST_CODE:
				// 判断存储卡是否可以用，可用进行存储
				String state = Environment.getExternalStorageState();
				if (state.equals(Environment.MEDIA_MOUNTED)) {
					File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
					File tempFile = new File(path, IMAGE_FILE_NAME);
					startPhotoZoom(Uri.fromFile(tempFile));
				} else {
					Toast.makeText(getApplicationContext(), "未找到存储卡，无法存储照片！", Toast.LENGTH_SHORT).show();
				}
				break;
			case RESULT_REQUEST_CODE: // 图片缩放完成后
				if (data != null) {
					getImageToView(data);
				}
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/** 裁剪图片方法实现
	 * 
	 * @param uri */
	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 设置裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 64);
		intent.putExtra("outputY", 64);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, RESULT_REQUEST_CODE);
	}

	/** 保存裁剪之后的图片数据
	 * 
	 * @param picdata */
	private void getImageToView(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap avatar = BitmapUtil.toRoundBitmap((Bitmap) extras.getParcelable("data"));
			Drawable drawable = new BitmapDrawable(this.getResources(), avatar);
			ImageView iv_avatar = (ImageView) this.findViewById(R.id.person_icon);
			iv_avatar.setImageDrawable(drawable);
			mUserProfile.Avatar = BitmapUtil.Bitmap2Bytes(avatar);
			mUserProfile.update(getApplicationContext(), new UpdateListener() {

				@Override
				public void onSuccess() {
					Intent intent = new Intent();
					intent.putExtra(TAG, mUserProfile.Avatar);
					setResult(RESULT_OK, intent);
					Toast.makeText(MyAccountActivity.this, "保存成功！", Toast.LENGTH_LONG).show();
				}

				@Override
				public void onFailure(int arg0, String arg1) {
					setResult(RESULT_CANCELED);
					Toast.makeText(MyAccountActivity.this, "失败！int：" + arg0 + " String:" + arg1, Toast.LENGTH_LONG)
							.show();
				}
			});
		}
	}

}
