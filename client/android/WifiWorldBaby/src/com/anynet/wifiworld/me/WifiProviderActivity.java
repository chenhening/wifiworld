package com.anynet.wifiworld.me;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

import cn.bmob.v3.datatype.BmobGeoPoint;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.api.WifiListHelper;
import com.anynet.wifiworld.app.BaseActivity;
import com.anynet.wifiworld.data.DataCallback;
import com.anynet.wifiworld.data.WifiProfile;
import com.anynet.wifiworld.util.LocationHelper;
import com.anynet.wifiworld.util.LoginHelper;
import com.anynet.wifiworld.util.StringCrypto;
import com.anynet.wifiworld.util.XLLog;
import com.anynet.wifiworld.wifi.WifiInfoScanned;

public class WifiProviderActivity extends BaseActivity {

	private WifiProfile mWifiProfile = new WifiProfile();
	private Bitmap mLogo;
	private String[] items = new String[] { "选择本地图片", "拍照" };
	/** 头像名称 */
	private static final String IMAGE_FILE_NAME = "image.jpg";
	/** 请求码 */
	private static final int IMAGE_REQUEST_CODE = 0;
	private static final int CAMERA_REQUEST_CODE = 1;
	private static final int RESULT_REQUEST_CODE = 2;
	private ImageView WifiLogo;
	private EditText SsidAccount;
	private PopupWindow popupWindow;
	private ListView lsvAccount;
	private WifiInfoScanned mSelectedWifi;
	private LoginHelper mLoginHelper;

	private boolean saveWifiProfile() throws Exception {
		showToast("开始保存数据！");
		WifiListHelper mWH = WifiListHelper.getInstance(getApplicationContext());
		String ssid = mWH.getWifiAdmin().getWifiNameConnection();//mSelectedWifi.getWifiName().toString();
		String edssid = ((EditText) findViewById(R.id.et_wifi_ssid)).getText().toString();
		//自动获取的数据
		mWifiProfile.Ssid = ssid; //ssid.equals(edssid) ? ssid : edssid;// mSelectedWifi.getWifiName().toString();
		mWifiProfile.MacAddr = ssid.equals(edssid) ? mWH.getWifiAdmin().getWifiConnection().getBSSID():"";
		mWifiProfile.Geometry = getLocation();
		String ExtAddress = ((EditText) findViewById(R.id.et_wifi_geo)).getText().toString();
		mWifiProfile.ExtAddress = ExtAddress;
		mWifiProfile.Sponser = mLoginHelper.getCurLoginUserInfo().PhoneNumber;
		//用户填写的数据
		String Password = ((EditText) findViewById(R.id.et_wifi_psw)).getText().toString();
		if(Password==null || Password.equals("")){
			Toast.makeText(getApplicationContext(), "请输入密码", Toast.LENGTH_LONG).show();
			return false;
		}else {
			mWifiProfile.Password = Password; //加密部分放到底层去做
		}
		mWifiProfile.Alias = ((EditText) findViewById(R.id.et_wifi_asia)).getText().toString();
		mWifiProfile.Logo = mLogo != null ? mLogo : BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
		mWifiProfile.Banner = ((EditText) findViewById(R.id.et_wifi_info)).getText().toString();
		mWifiProfile.StoreRemote(getApplicationContext(), new DataCallback<WifiProfile>() {

			@Override
            public void onSuccess(WifiProfile object) {
				showToast("上传wifi数据：" + object.Ssid + "成功。");
				finish();
            }

			@Override
            public void onFailed(String msg) {
				showToast("上传wifi数据失败：" + msg);
				findViewById(R.id.button_save).setEnabled(true);
            }
			
		});
		showToast("等待远程服务器反馈！");
		return true;
	}

	private void bingdingTitleUI() {
		mTitlebar.ivHeaderLeft.setVisibility(View.VISIBLE);
		mTitlebar.llFinish.setVisibility(View.VISIBLE);
		mTitlebar.llHeaderMy.setVisibility(View.INVISIBLE);
		mTitlebar.tvHeaderRight.setVisibility(View.INVISIBLE);
		mTitlebar.tvTitle.setText(getString(R.string.wifi_provider));
		mTitlebar.ivHeaderLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setContentView(R.layout.wifi_provider_activity);
		super.onCreate(savedInstanceState);
		bingdingTitleUI();
		findViewById(R.id.button_save).setOnClickListener(
			new OnClickListener() {

				@Override
				public void onClick(View v) {
					showToast("正在上传数据请稍等");
					findViewById(R.id.button_save).setEnabled(false);
					try {
						saveWifiProfile();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		EditText Geo =  (EditText) findViewById(R.id.et_wifi_geo);
		TextView Geo_add =  (TextView) findViewById(R.id.tv_wifi_geo_add);
		LocationHelper mLocationHelper = LocationHelper.getInstance(this);
		String Geo_str = mLocationHelper.getProvince()+" "+mLocationHelper.getCity()+" "+mLocationHelper.getDistrict();
		Geo_add.setText(Geo_str);
		Geo_str = Geo_str+"  LocalDescription:"+mLocationHelper.getLocalDescription();
		Toast.makeText(this, Geo_str, Toast.LENGTH_LONG).show();
		Geo.setText(mLocationHelper.getLocalDescription());
		SsidAccount = (EditText) findViewById(R.id.et_wifi_ssid);
		WifiListHelper mWH = WifiListHelper.getInstance(getApplicationContext());
		SsidAccount.setText(mWH.getWifiAdmin().getWifiNameConnection());
		SsidAccount.setEnabled(false);

		WifiLogo = (ImageView) findViewById(R.id.et_wifi_logo);
		WifiLogo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog();
			}

		});
		mLoginHelper = LoginHelper.getInstance(getApplicationContext());
		if(!mLoginHelper.isLogined()){
			Toast.makeText(this, "未登录！", Toast.LENGTH_LONG).show();
			finish();
		}
	}

	private void showDialog() {

		new AlertDialog.Builder(this)
				.setTitle("设置头像")
				.setItems(items, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							Intent intentFromGallery = new Intent();
							intentFromGallery.setType("image/*"); // 设置文件类型
							intentFromGallery
									.setAction(Intent.ACTION_GET_CONTENT);
							startActivityForResult(intentFromGallery,
									IMAGE_REQUEST_CODE);
							break;
						case 1:
							Intent intentFromCapture = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);
							// 判断存储卡是否可以用，可用进行存储
							String state = Environment
									.getExternalStorageState();
							if (state.equals(Environment.MEDIA_MOUNTED)) {
								File path = Environment
										.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
								File file = new File(path, IMAGE_FILE_NAME);
								intentFromCapture.putExtra(
										MediaStore.EXTRA_OUTPUT,
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
			WifiLogo.setImageDrawable(drawable);
		}
	}

	// 设置和显示PopupWindow
	private void showPopupWindow() {
		// 获取要显示在PopupWindow上的窗体视图
		LayoutInflater inflater = getLayoutInflater();
		View view = inflater.inflate(R.layout.ssid_list, null);
		// 实例化并且设置PopupWindow显示的视图
		popupWindow = new PopupWindow(view, SsidAccount.getWidth(),
				LayoutParams.WRAP_CONTENT);

		// 获取PopupWindow中的控件
		lsvAccount = (ListView) view.findViewById(R.id.listview);

		QuickAdapter<WifiInfoScanned> LostAdapter = new QuickAdapter<WifiInfoScanned>(
				this, R.layout.item_list) {
			@Override
			protected void convert(BaseAdapterHelper helper,
					WifiInfoScanned wifiScanned) {
				helper.setText(R.id.tv_ssid, wifiScanned.getWifiName())
						.setText(R.id.tv_ctime, wifiScanned.getEncryptType())
						.setText(R.id.tv_income,
								Integer.toString(wifiScanned.getWifiStrength()));
			}
		};

		lsvAccount.setAdapter(LostAdapter);

		List<WifiInfoScanned> list = new ArrayList<WifiInfoScanned>();
		WifiListHelper mWH = WifiListHelper
				.getInstance(getApplicationContext());
		mWH.fillWifiList();
		list.addAll(mWH.getWifiFrees());
		list.addAll(mWH.getWifiEncrypts());
		LostAdapter.addAll(list);

		// 想要让PopupWindow中的控件能够使用，就必须设置PopupWindow为focusable
		popupWindow.setFocusable(true);

		// 想做到在你点击PopupWindow以外的区域时候让PopupWindow消失就做如下两步操作
		// 1：设置setOutsideTouchable为ture，这个很容易理解吧，跟AlertDialog一样的
		popupWindow.setOutsideTouchable(true);
		// 2：设置PopupWindow的背景不能为空，所以你就随便设置个背景吧，你不用担心背景会影响你的显示效果
		popupWindow.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.ic_launcher));

		// 设置PopupWindow消失的时候触发的事件
		popupWindow.setOnDismissListener(new OnDismissListener() {

			public void onDismiss() {
				// Toast.makeText(MainActivity.this, "我消失了",
				// Toast.LENGTH_SHORT).show();
			}
		});

		// 设置ListView点击事件
		lsvAccount.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				mSelectedWifi = (WifiInfoScanned) lsvAccount
						.getItemAtPosition(position);
				SsidAccount.setText(mSelectedWifi.getWifiName());
				dismissPopupWindow();
			}
		});

		// 显示PopupWindow有3个方法
		// popupWindow.showAsDropDown(anchor)
		// popupWindow.showAsDropDown(anchor, xoff, yoff)
		// popupWindow.showAtLocation(parent, gravity, x, y)
		// 需要注意的是以上三个方法必须在触发事件中使用，比如在点击某个按钮的时候
		popupWindow.showAsDropDown(SsidAccount, 0, 0);
	}

	// 让PopupWindow消失
	private void dismissPopupWindow() {
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
		}
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

	// 获取Location通过LocationManger获取！
	public BmobGeoPoint getLocation() {
		LocationHelper location = LocationHelper.getInstance(this);
		location.refreshLocation();
		return new BmobGeoPoint(location.getLongitude(), location.getLatitude());
	}
}
