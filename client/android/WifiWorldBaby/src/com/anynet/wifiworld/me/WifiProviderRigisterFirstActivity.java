package com.anynet.wifiworld.me;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.datatype.BmobGeoPoint;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.app.BaseActivity;
import com.anynet.wifiworld.data.DataCallback;
import com.anynet.wifiworld.data.WifiProfile;
import com.anynet.wifiworld.util.LocationHelper;
import com.anynet.wifiworld.util.LoginHelper;
import com.anynet.wifiworld.util.XLLog;
import com.anynet.wifiworld.wifi.WifiAdmin;
import com.anynet.wifiworld.wifi.WifiListHelper;

public class WifiProviderRigisterFirstActivity extends BaseActivity {
	public WifiProfile mWifiProfile = null;
	private LoginHelper mLoginHelper = null;
	private LocationHelper mLocationHelper = null;

	// UI
	private EditText met_password = null;
	private TextView mtv_location = null;
	private Spinner msp_typelist = null;
	private Bitmap mLogo = null;
	private ImageView mWifiLogo = null;

	private boolean mWifiVerfied = false;

	private String[] items = new String[] { "选择本地图片", "拍照" };
	private static final String IMAGE_FILE_NAME = "image.jpg";
	/** 头像名称 */
	private static final int IMAGE_REQUEST_CODE = 0;
	/** 请求码 */
	private static final int CAMERA_REQUEST_CODE = 1;
	private static final int RESULT_REQUEST_CODE = 2;

	private void bingdingTitleUI() {
		mTitlebar.ivHeaderLeft.setVisibility(View.VISIBLE);
		mTitlebar.llFinish.setVisibility(View.VISIBLE);
		// mTitlebar.llHeaderMy.setVisibility(View.INVISIBLE);
		mTitlebar.tvHeaderRight.setVisibility(View.VISIBLE);
		mTitlebar.tvHeaderRight.setText(R.string.next_step);
		mTitlebar.tvHeaderRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!mWifiVerfied) {
					verifyPassword();
					mTitlebar.tvHeaderRight.setEnabled(false);
					return;
				}
				// 如果第一页有未填写字段，则提示填写
				if (!checkRequiredFields() || mWifiProfile == null) {
					return;
				}

				Intent intent = null;
				if (msp_typelist.getSelectedItem().toString() == "商家网络") {
					intent = new Intent(WifiProviderRigisterFirstActivity.this,
							WifiProviderRigisterSecondBusinessActivity.class);
				} else {
					intent = new Intent(WifiProviderRigisterFirstActivity.this,
							WifiProviderRigisterSecondHomeActivity.class);
				}
				intent.putExtra("wifiprofile", (Serializable) mWifiProfile);
				startActivity(intent);
			}
		});
		mTitlebar.tvTitle.setText("Wi-Fi认证登记");
		mTitlebar.ivHeaderLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	// ---------------------------------------------------------------------------------------------
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.wifi_provider_certify_first);
		super.onCreate(savedInstanceState);
		bingdingTitleUI();
		
		//立刻去查询当前wifi的共享情况，如果已经被人共享，提示并退出
		WifiInfo curwifi = WifiAdmin.getInstance(this).getWifiConnected();
		if (curwifi == null) {
			showToast("当前网络不稳定，请稍后再试。");
			finish();
		}
		
		mWifiProfile = new WifiProfile();
		mWifiProfile.Ssid = curwifi.getSSID();
		mWifiProfile.MacAddr = curwifi.getBSSID();
		mWifiProfile.VerfyIsShared(this, mWifiProfile.MacAddr, new DataCallback<Boolean>() {

				@Override
				public void onSuccess(final Boolean object) {
					getActivity().runOnUiThread(new Runnable() {

						@Override
						public void run() {
							if (object) {
								showToast("此WiFi账号已经被别人认证，如果您是WiFi本人请申请二级仲裁。");
								finish();
								// TODO(buffer):需要调到wifi申请找回仲裁
							} else {
								// 获得sponser
								mLoginHelper = LoginHelper.getInstance(getApplicationContext());
								mWifiProfile.Sponser = mLoginHelper.getUserid();

								// 自动获取网络ssid
								setSSIDUI();
								// 处理登陆密码
								setPasswordUI();
								// 点击地理位置按钮，自动获取地理位置
								setLocationUI();
								// 点击选择类型
								setNetTypeUI();
								// 点击获取设置LOGO
								setLogoUI();
							}
							return;
						}

					});
				}

				@Override
				public void onFailed(String msg) {
					getActivity().runOnUiThread(new Runnable() {

						@Override
						public void run() {
							showToast("当前网络不稳定，请稍后再试。");
							finish();
						}
					});
				}

			});
	}

	protected Activity getActivity() {
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

	// ---------------------------------------------------------------------------------------------
	private void setSSIDUI() {
		TextView tv_ssid = (TextView) this.findViewById(R.id.et_wifi_provider_ssid);
		tv_ssid.setText(mWifiProfile.Ssid);
	}

	private void setPasswordUI() {
		met_password = (EditText) this.findViewById(R.id.et_wifi_provider_password);
		met_password.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus)
					return;
				verifyPassword();
			}

		});
	}

	private void setLocationUI() {
		mtv_location = (TextView) this.findViewById(R.id.tv_wifi_provider_location_desc);
		this.findViewById(R.id.img_wifi_provider_location_click).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mLocationHelper = LocationHelper.getInstance(getApplicationContext());
				mLocationHelper.refreshLocation();
				mWifiProfile.Geometry = new BmobGeoPoint(mLocationHelper.getLongitude(), mLocationHelper.getLatitude());
				String address = mLocationHelper.getLocalDescription();
				mtv_location.setText(address);
				mWifiProfile.ExtAddress = address;
			}

		});
	}

	private void setNetTypeUI() {
		msp_typelist = (Spinner) this.findViewById(R.id.sp_wifi_provider_type_value);
		List<String> list = new ArrayList<String>();
		list.add("家庭网络");
		list.add("商家网络");// TODO(binfei)还需要细分类型
		list.add("公司网络");
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		msp_typelist.setAdapter(adapter);
		msp_typelist.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				arg0.setVisibility(View.VISIBLE);
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				arg0.setVisibility(View.VISIBLE);
			}
		});
	}

	private void setLogoUI() {
		this.findViewById(R.id.img_wifi_provider_logo).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showLogoDialog();
			}

		});
	}

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
			mLogo = extras.getParcelable("data");
			XLLog.e(TAG, "mLogo W:" + mLogo.getWidth() + " H:" + mLogo.getHeight());
			Drawable drawable = new BitmapDrawable(this.getResources(), mLogo);
			mWifiLogo = (ImageView) this.findViewById(R.id.img_wifi_provider_logo_preview);
			mWifiLogo.setImageDrawable(drawable);
			mWifiProfile.setLogo(mLogo);
		}
	}

	private boolean checkRequiredFields() {
		if (!mWifiVerfied) {
			showToast("验证路由器密码失败，请检查是否输入密码正确。");
			return false;
		}

		EditText et_alias = (EditText) this.findViewById(R.id.et_wifi_provider_alias);
		mWifiProfile.Alias = et_alias.getText().toString();
		if (mWifiProfile.Alias.equals("")) {
			showToast("请务必填写WiFi别名。");
			return false;
		}

		mWifiProfile.ExtAddress = mtv_location.getText().toString();
		if (mWifiProfile.ExtAddress.equals("") || mWifiProfile.ExtAddress.startsWith("未能自动识别")) {
			showToast("获取地理位置失败，请重新点击获取地理位置");
			return false;
		}

		EditText et_detail_addr = (EditText) this.findViewById(R.id.et_wifi_provider_detail_address);
		mWifiProfile.ExtAddress += " " + et_detail_addr.getText().toString();

		EditText et_desc = (EditText) this.findViewById(R.id.et_wifi_provider_desc_content);
		mWifiProfile.Banner = et_desc.getText().toString();

		return true;
	}
	
	private boolean verifyPassword() {
		String password = met_password.getText().toString();
		if (password.length() >= 8) { // 现在wifi的密码都要求8位以上
			// 首先尝试登陆路由器
			boolean result = WifiAdmin.getInstance(getApplicationContext()).checkWifiPwd(mWifiProfile.Ssid, 
				mWifiProfile.MacAddr, password, new DataCallback<Boolean>() {

				@Override
                public void onSuccess(Boolean object) {
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							showToast("验证密码成功，请继续填写其他项目。");
							met_password.setEnabled(false);
							mTitlebar.tvHeaderRight.setEnabled(true);
							mWifiVerfied = true;
							mWifiProfile.Password = met_password.getText().toString();
						}
					});
                }

				@Override
                public void onFailed(final String msg) {
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							showToast(msg);
						}
					});
                }
				
			});
			if (!result) {
				showToast("您输入的WiFi密码验证不通过，请重新输入");
				return false;
			}
			showToast("正在后台验证密码，请继续填写其他项目。");
		}
		
		return true;
	}
}
