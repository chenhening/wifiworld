package com.anynet.wifiworld.me;

import java.io.File;
import java.util.Calendar;

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
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.listener.UpdateListener;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.app.BaseActivity;
import com.anynet.wifiworld.data.UserProfile;
import com.anynet.wifiworld.util.BitmapUtil;
import com.anynet.wifiworld.util.LoginHelper;
import com.anynet.wifiworld.util.XLLog;
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
		
		//显示头像
		if (mUserProfile.Avatar != null) {
			Drawable drawable = new BitmapDrawable(this.getResources(), BitmapUtil.Bytes2Bimap(mUserProfile.Avatar));
			ImageView iv_avatar = (ImageView) this.findViewById(R.id.person_icon);
			iv_avatar.setImageDrawable(drawable);
		}

		final TextView tvName = (TextView) findViewById(R.id.person_name);
		si = (SettingItemView) findViewById(R.id.siv_account);
		final TextView tvContent = (TextView) si.findViewById(R.id.setting_item_content);
		tvName.setText(mUserProfile.getUsername());
		tvContent.setText(mUserProfile.getUsername());
		si.findViewById(R.id.setting_item_next).setVisibility(View.INVISIBLE);

		final SettingEditItemView nicknameIV = (SettingEditItemView) findViewById(R.id.siv_alias);
		if (mUserProfile.NickName == null || mUserProfile.NickName.equals("")) {
			nicknameIV.setContent(mUserProfile.getUsername());
		} else {
			nicknameIV.setContent(mUserProfile.NickName);
		}

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

		final SettingEditItemView email = (SettingEditItemView) findViewById(R.id.sev_email);
		if (mUserProfile.getEmail() == null || mUserProfile.getEmail().equals("")) {
			email.setContent("");
		} else {
			email.setContent(mUserProfile.getEmail());
		}
		email.setClickEditButtonListener(new ClickEditButtonListener() {

			@Override
			public void onSave(CharSequence charSequence) {
				mUserProfile.setEmail(charSequence.toString());
				email.setContent(mUserProfile.getEmail());
				mUserProfile.update(getApplicationContext(), new UpdateListener() {

					@Override
					public void onSuccess() {
						Toast.makeText(MyAccountActivity.this, "保存成功！", Toast.LENGTH_LONG).show();
					}

					@Override
					public void onFailure(int arg0, String arg1) {
						Toast.makeText(MyAccountActivity.this, "失败！int：" + arg0 + " String:" + arg1, Toast.LENGTH_LONG)
								.show();
					}
				});
			}

			@Override
			public void beforeEdit() {
				// TODO Auto-generated method stub

			}
		});

		final SettingEditItemView age = (SettingEditItemView) findViewById(R.id.sev_age);
		if (mUserProfile.Age == null || mUserProfile.Age.equals("")) {
			age.setContent("");
		} else {
			age.setContent(mUserProfile.Age);
		}
		age.setClickButtonListener(new ClickButtonListener() {
			@Override
			public void onClick(CharSequence charSequence) {
				final Calendar c = Calendar.getInstance();
				DatePickerDialog dialog = new DatePickerDialog(MyAccountActivity.this,
						new DatePickerDialog.OnDateSetListener() {
							@Override
							public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
								c.set(year, monthOfYear, dayOfMonth);
								String content  = DateFormat.format("yyy-MM-dd", c).toString();
								age.setContent(content);
								
								mUserProfile.Age = content;
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
							}
						}, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
				dialog.show();
			}
		});

		final SettingEditItemView job = (SettingEditItemView) findViewById(R.id.sev_job);
		if (mUserProfile.Job == null || mUserProfile.Job.equals("")) {
			job.setContent("");
		} else {
			job.setContent(mUserProfile.Job);
		}
		job.setClickEditButtonListener(new ClickEditButtonListener() {

			@Override
			public void onSave(CharSequence charSequence) {
				// TODO Auto-generated method stub
				mUserProfile.Job = charSequence.toString();
				job.setContent(mUserProfile.Job);
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
			}

			@Override
			public void beforeEdit() {
			}
		});

		final SettingEditItemView interest = (SettingEditItemView) findViewById(R.id.sev_interest);
		if (mUserProfile.Interest == null || mUserProfile.Interest.equals("")) {
			interest.setContent("");
		} else {
			interest.setContent(mUserProfile.Interest);
		}
		interest.setClickEditButtonListener(new ClickEditButtonListener() {

			@Override
			public void onSave(CharSequence charSequence) {
				// TODO Auto-generated method stub
				mUserProfile.Interest = charSequence.toString();
				interest.setContent(mUserProfile.Interest);
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
			}

			@Override
			public void beforeEdit() {
				// TODO Auto-generated method stub

			}
		});
		
		//设置头像
		findViewById(R.id.person_signin_tip).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showLogoDialog();
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
			Bitmap avatar = extras.getParcelable("data");
			Drawable drawable = new BitmapDrawable(this.getResources(), avatar);
			ImageView iv_avatar = (ImageView) this.findViewById(R.id.person_icon);
			iv_avatar.setImageDrawable(drawable);
			mUserProfile.Avatar = BitmapUtil.Bitmap2Bytes(avatar);
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
		}
	}

}
