package com.anynet.wifiworld.me;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import com.anynet.wifiworld.R.layout;
import com.anynet.wifiworld.R.string;
import com.anynet.wifiworld.api.WifiListHelper;
import com.anynet.wifiworld.app.BaseActivity;
import com.anynet.wifiworld.data.DataCallback;
import com.anynet.wifiworld.data.WifiProfile;
import com.anynet.wifiworld.util.LocationHelper;
import com.anynet.wifiworld.util.XLLog;

public class WifiProviderRigisterFirstActivity extends BaseActivity {
	
	private WifiListHelper mWifiHelper = null;
	private WifiProfile mWifiProfile = new WifiProfile();
	private LocationHelper mLocationHelper = null;
	
	//UI
	private EditText met_password = null;
	private TextView mtv_location = null;
	private Spinner msp_typelist = null;
	private Bitmap mLogo = null;
	private ImageView mWifiLogo = null;
	
	private String[] items = new String[] { "选择本地图片", "拍照" };
	private static final String IMAGE_FILE_NAME = "image.jpg";/** 头像名称 */
	private static final int IMAGE_REQUEST_CODE = 0;/** 请求码 */
	private static final int CAMERA_REQUEST_CODE = 1;
	private static final int RESULT_REQUEST_CODE = 2;
	
	private void bingdingTitleUI() {
		mTitlebar.ivHeaderLeft.setVisibility(View.VISIBLE);
		mTitlebar.llFinish.setVisibility(View.VISIBLE);
		mTitlebar.llHeaderMy.setVisibility(View.INVISIBLE);
		mTitlebar.tvHeaderRight.setVisibility(View.VISIBLE);
		mTitlebar.tvHeaderRight.setText(R.string.next_step);
		mTitlebar.tvHeaderRight.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(WifiProviderRigisterFirstActivity.this,
						WifiProviderRigisterSecondActivity.class);
				startActivity(i);
			}
		});
		mTitlebar.tvTitle.setText(getString(R.string.merchant_certify));
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
		setContentView(R.layout.bussess_partner_certify_first);
		super.onCreate(savedInstanceState);		
		bingdingTitleUI();
		
		//自动获取网络ssid
		setSSIDUI();
		//处理登陆密码
		setPasswordUI();
		//点击地理位置按钮，自动获取地理位置
		setLocationUI();
		//点击选择类型
		setNetTypeUI();
		//点击获取设置LOGO
		setLogoUI();
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
		mWifiHelper = WifiListHelper.getInstance(getApplicationContext());
		mWifiProfile.MacAddr = mWifiHelper.getWifiAdmin().getWifiConnection().getBSSID();
		String ssid = mWifiHelper.getWifiAdmin().getWifiNameConnection();
		if (ssid.equals("")) {
			showToast("WiFi SSID获取失败，请确认是否连接上WiFI。");
			return;
		}
		mWifiProfile.Ssid = ssid;
		TextView tv_ssid = (TextView)this.findViewById(R.id.et_wifi_provider_ssid);
		tv_ssid.setText(ssid);
	}
	
	private void setPasswordUI() {
		met_password = (EditText)this.findViewById(R.id.et_wifi_provider_password);
		met_password.setOnFocusChangeListener(
			new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					String password = met_password.getText().toString();
					if (password.length() >= 8) { //现在wifi的密码都要求8位以上
						showToast("正在验证WiFi密码......");
						//首先尝试登陆路由器
						//mWifiHelper.getWifiAdmin().connectToConfiguredNetwork(ctx, config, reassociate)
						//然后查询数据库此wifi是否被占用
						mWifiProfile.QueryByMacAddress(getApplicationContext(), mWifiProfile.MacAddr,
							new DataCallback<WifiProfile>() {

								@Override
								public void onSuccess(WifiProfile object) {
									met_password.post(new Runnable() {

										@Override
										public void run() {
											showToast("此WiFi账号已经被别人认证，如果您是WiFi本人请点击申请。");
											//TODO(buffer):需要调到wifi申请找回仲裁
										}
										
									});
								}

								@Override
								public void onFailed(String msg) {
									met_password.post(new Runnable() {

										@Override
										public void run() {
											showToast("WiFi验证成功，请继续填写其他项目。");
											met_password.setEnabled(false);
										}
									});
								}
							
						});
					}
				}
				
		});
	}
	
	private void setLocationUI() {
		mtv_location = (TextView)this.findViewById(R.id.tv_wifi_provider_location_desc);
		this.findViewById(R.id.img_wifi_provider_location_click).setOnClickListener(
			new OnClickListener() {

				@Override
				public void onClick(View v) {
					mLocationHelper = LocationHelper.getInstance(getApplicationContext());
					mLocationHelper.refreshLocation();
					mWifiProfile.Geometry = new BmobGeoPoint(mLocationHelper.getLongitude(), 
						mLocationHelper.getLatitude());
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
		list.add("商家网络");//TODO(binfei)还需要细分类型
        list.add("公司网络");
		final ArrayAdapter<String> adapter = 
			new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		msp_typelist.setAdapter(adapter);
		msp_typelist.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {  
                arg0.setVisibility(View.VISIBLE);
            }    
            public void onNothingSelected(AdapterView<?> arg0) {
                arg0.setVisibility(View.VISIBLE);    
            }    
        });
	}
	
	private void setLogoUI() {
		this.findViewById(R.id.img_wifi_provider_logo).setOnClickListener(
			new OnClickListener() {

				@Override
				public void onClick(View v) {
					showLogoDialog();
				}
				
			});
	}
	
	private void showLogoDialog() {
		new AlertDialog.Builder(this).setTitle("设置头像").setItems(items, 
			new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case 0:
						Intent intentFromGallery = new Intent();
						intentFromGallery.setType("image/*"); // 设置文件类型
						intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
						startActivityForResult(intentFromGallery,IMAGE_REQUEST_CODE);
						break;
					case 1:
						Intent intentFromCapture = new Intent(
								MediaStore.ACTION_IMAGE_CAPTURE);
						// 判断存储卡是否可以用，可用进行存储
						String state = Environment.getExternalStorageState();
						if (state.equals(Environment.MEDIA_MOUNTED)) {
							File path = Environment.getExternalStoragePublicDirectory(
								Environment.DIRECTORY_DCIM);
							File file = new File(path, IMAGE_FILE_NAME);
							intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT,
									Uri.fromFile(file));
						}

						startActivityForResult(intentFromCapture,
								CAMERA_REQUEST_CODE);
						break;
					}
				}
			})
			.setNegativeButton("取消", new DialogInterface.OnClickListener() {

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
					File path = Environment
							.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
					File tempFile = new File(path, IMAGE_FILE_NAME);
					startPhotoZoom(Uri.fromFile(tempFile));
				} else {
					Toast.makeText(getApplicationContext(), "未找到存储卡，无法存储照片！",
							Toast.LENGTH_SHORT).show();
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

	/**
	 * 裁剪图片方法实现
	 * 
	 * @param uri
	 */
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
	
	/**
	 * 保存裁剪之后的图片数据
	 * 
	 * @param picdata
	 */
	private void getImageToView(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			mLogo = extras.getParcelable("data");
			XLLog.e(TAG, "mLogo W:"+mLogo.getWidth()+" H:"+mLogo.getHeight());
			Drawable drawable = new BitmapDrawable(this.getResources(), mLogo);
			mWifiLogo = (ImageView)this.findViewById(R.id.img_wifi_provider_logo_preview);
			mWifiLogo.setImageDrawable(drawable);
		}
	}
}