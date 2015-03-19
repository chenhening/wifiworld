package com.anynet.wifiworld.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.IntentSender.SendIntentException;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.anynet.wifiworld.data.DataCallback;
import com.anynet.wifiworld.data.UserProfile;

public class LoginHelper {

	private final String TAG = LoginHelper.class.getSimpleName();

	public static String AUTO_LOGIN_SUCCESS = "com.anynet.wifiworld.autologin.success";
	public static String AUTO_LOGIN_FAIL = "com.anynet.wifiworld.autologin.fail";
	public static String AUTO_LOGIN_NEVERLOGIN = "com.anynet.wifiworld.autologin.neverlogin";
	private static String mUserprofileDataFile = "userprofile.conf";
	private static String mAliasUser = "PhoneNumber";
	private static String mAliasPwd = "Password";
	private UserProfile mUser = null;
	private boolean mIsLogin = false;

	private static LoginHelper mInstance = null;
	private SharedPreferences mPreferences = null;
	private Context globalContext = null;

	private double Longitude = 0.0;
	private double Latitude = 0.0;

	public double getLongitude() {
		return Longitude;
	}

	public void setLongitude(double longitude) {
		if (globalContext == null)
			try {
				throw new Exception(
						"LoginHelper instance is not call init function!!!");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		Longitude = longitude;
	}

	public double getLatitude() {
		return Latitude;
	}

	public void setLatitude(double latitude) {
		Latitude = latitude;
	}

	// ------------------------------------------------------------------------------------------------
	public static LoginHelper getInstance(Context context) {
		if (null == mInstance) {
			mInstance = new LoginHelper(context);
		}
		return mInstance;
	}

	public LoginHelper(Context context) {
		this.globalContext = context;
		mPreferences = globalContext.getSharedPreferences(mUserprofileDataFile,
				Context.MODE_PRIVATE);

		LocationManager locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			Location location = locationManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (location != null) {
				Latitude = location.getLatitude();
				Longitude = location.getLongitude();
			}
		} else {
			LocationListener locationListener = new LocationListener() {

				// Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
				// Provider被enable时触发此函数，比如GPS被打开
				@Override
				public void onProviderEnabled(String provider) {

				}

				// Provider被disable时触发此函数，比如GPS被关闭
				@Override
				public void onProviderDisabled(String provider) {

				}

				// 当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
				@Override
				public void onLocationChanged(Location location) {
					if (location != null) {
						Log.e("Map",
								"Location changed : Lat: "
										+ location.getLatitude() + " Lng: "
										+ location.getLongitude());
					}
				}

				@Override
				public void onStatusChanged(String provider, int status,
						Bundle extras) {
					// TODO Auto-generated method stub

				}
			};
			locationManager
					.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
							1000, 0, locationListener);
			Location location = locationManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			if (location != null) {
				Latitude = location.getLatitude(); // 经度
				Longitude = location.getLongitude(); // 纬度
			}
		}

	}

//	public void ShowToast(final Context context, final CharSequence text,
//			final int duration) {
//		if (globalContext == null)
//			try {
//				throw new Exception(
//						"LoginHelper instance is not call init function!!!");
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		Activity activity = (Activity) globalContext;
//		globalContext.runOnUiThread(new Runnable() {
//			@Override
//			public void run() {
//				Toast.makeText(context, text, duration).show();
//			}
//		});
//	}

	// ------------------------------------------------------------------------------------------------
	public void Login(UserProfile profile) {
		mIsLogin = false;
		mUser = profile;
		if (globalContext == null)
			try {
				throw new Exception(
						"LoginHelper instance is not call init function!!!");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		mUser.StoreRemote(globalContext, new DataCallback<UserProfile>() {

			@Override
			public void onSuccess(UserProfile object) {
				Log.i(TAG, "用户信息更新成功。");
				//ShowToast(globalContext, "用户信息更新成功。", Toast.LENGTH_SHORT);
				SaveProfileLocal(object);
				mIsLogin = true;
				globalContext.sendBroadcast(new Intent(AUTO_LOGIN_SUCCESS));
			}

			@Override
			public void onFailed(String msg) {
				globalContext.sendBroadcast(new Intent(AUTO_LOGIN_FAIL));
				Log.i(TAG, "用户信息更新失败，请重新更新：");
				//ShowToast(globalContext, "用户信息更新失败，请重新更新：" + msg,Toast.LENGTH_SHORT);
			}
		});
	}

	public void AutoLogin() {
		if (globalContext == null)
			try {
				throw new Exception(
						"LoginHelper instance is not call init function!!!");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		mIsLogin = false;
		// 读取本地保存的账号密码文件
		mUser = new UserProfile();
		mUser.PhoneNumber = mPreferences.getString(mAliasUser, "").trim();
		mUser.Password = mPreferences.getString(mAliasPwd, "").trim();
		// 如果本地已经存有数据，那么取出来与服务器验证是否成功
		if (mUser.PhoneNumber == null || mUser.Password == null
				|| mUser.PhoneNumber.isEmpty() || mUser.Password.isEmpty()) {
			Log.d(TAG, "用户未登录过");
			globalContext.sendBroadcast(new Intent(AUTO_LOGIN_NEVERLOGIN));
			//ShowToast(globalContext, "用户未登录过。", Toast.LENGTH_SHORT);
			return;
		}

		final UserProfile remote_user = new UserProfile();
		remote_user.QueryByPhoneNumber(globalContext, mUser.PhoneNumber,
				new DataCallback<UserProfile>() {

					@Override
					public void onSuccess(UserProfile object) {
						if (object.Password.equals(mUser.Password)) {
							mIsLogin = true;
							globalContext.sendBroadcast(new Intent(
									AUTO_LOGIN_SUCCESS));
							Log.d(TAG, "用户自动登陆成功。");
							//ShowToast(globalContext, "用户自动登陆成功。",Toast.LENGTH_SHORT);
						} else {
							globalContext.sendBroadcast(new Intent(
									AUTO_LOGIN_FAIL));
							Log.d(TAG, "用户自动登陆失败，请重新登陆。");
							//ShowToast(globalContext, "用户自动登陆失败，请重新登陆。",Toast.LENGTH_SHORT);
						}
					}

					@Override
					public void onFailed(String msg) {
						Log.d(TAG, "用户自动登陆失败，用户未登陆过。");
						//ShowToast(globalContext, "用户自动登陆失败，用户未登陆过。",Toast.LENGTH_SHORT);
					}
				});
	}

	public void logout() {
		mIsLogin = false;
	}

	private void SaveProfileLocal(UserProfile user) {
		// 保存账号密码到本地用于下次登陆
		// TODO(binfei):先简单的保存在本地某个文件，以后改成sqlite3
		if (globalContext == null)
			try {
				throw new Exception(
						"LoginHelper instance is not call init function!!!");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		SharedPreferences.Editor sharedata = mPreferences.edit();
		sharedata.putString(mAliasUser, user.PhoneNumber);
		sharedata.putString(mAliasPwd, user.Password);
		sharedata.commit();
		Log.d(TAG, "用户密码本地保存成功。");
		//ShowToast(globalContext, "用户密码本地保存成功。", Toast.LENGTH_SHORT);
	}

	public boolean getCurLoginStatus() {
		return mIsLogin;
	}

	public UserProfile getCurLoginUserInfo() {
		return mUser;
	}

}
