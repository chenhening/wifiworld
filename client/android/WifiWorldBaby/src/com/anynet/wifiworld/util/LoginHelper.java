package com.anynet.wifiworld.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import cn.bmob.v3.datatype.BmobGeoPoint;

import com.anynet.wifiworld.data.DataCallback;
import com.anynet.wifiworld.data.MultiDataCallback;
import com.anynet.wifiworld.data.UserProfile;
import com.anynet.wifiworld.data.WifiDynamic;
import com.anynet.wifiworld.data.WifiProfile;
import com.anynet.wifiworld.wifi.DeviceUID;
import com.anynet.wifiworld.wifi.WifiListHelper;

public class LoginHelper {

	public static final int LOGOUT_KICKOUT = 0;

	public static final int LOGOUT_TIMEOUT = 1;


	public static final int STATUS_LOGOUT = 2;

	public static final int LOGOUT_BY_USER = 3;
	
	private final String TAG = LoginHelper.class.getSimpleName();

	public static String AUTO_LOGIN_SUCCESS = "com.anynet.wifiworld.autologin.success";
	public static String AUTO_LOGIN_FAIL = "com.anynet.wifiworld.autologin.fail";
	public static String AUTO_LOGIN_NEVERLOGIN = "com.anynet.wifiworld.autologin.neverlogin";
	public static String LOGIN_OUT = "com.anynet.wifiworld.login.out";
	public static String LOGIN_OFF = "com.anynet.wifiworld.login.off";
	
	public static String LOGIN_SUCCESS = AUTO_LOGIN_SUCCESS;
	public static String LOGIN_FAIL = AUTO_LOGIN_FAIL;
	public static String LOGIN_NEVERLOGIN = AUTO_LOGIN_NEVERLOGIN;

	private static String mUserprofileDataFile = "userprofile.conf";
	private static String mAliasPwd = "Password";
	private static String mAliasUser = "PhoneNumber";
	private UserProfile mUser = null;
	private boolean mIsLogin = false;

	private static LoginHelper mInstance = null;
	private SharedPreferences mPreferences = null;
	private Context globalContext = null;
	public Set<String> mKnockList = new HashSet<String>();// 保存敲门历史到本地

	private double Longitude = 0.0;
	private double Latitude = 0.0;

	public WifiProfile mWifiProfile = null;

	public WifiProfile getWifiProfile() {
		return mWifiProfile;
	}

	public void setWifiProfile(WifiProfile mWifiProfile) {
		this.mWifiProfile = mWifiProfile;
	}

	public double getLongitude() {
		return Longitude;
	}

	public void setLongitude(double longitude) {
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
		mPreferences = globalContext.getSharedPreferences(mUserprofileDataFile, Context.MODE_PRIVATE);
	}

	// ------------------------------------------------------------------------------------------------
	public void Login(UserProfile profile) {
		mIsLogin = false;
		mUser = profile;
		mUser.StoreRemote(globalContext, new DataCallback<UserProfile>() {

			@Override
			public void onSuccess(UserProfile object) {
				Log.i(TAG, "用户信息更新成功。");
				SaveProfileLocal(object);
				mUser = object;
				mIsLogin = true;
				globalContext.sendBroadcast(new Intent(AUTO_LOGIN_SUCCESS));
				pullWifiProfile();
				updateWifiDynamic();
			}

			@Override
			public void onFailed(String msg) {
				globalContext.sendBroadcast(new Intent(AUTO_LOGIN_FAIL));
				Log.i(TAG, "用户信息更新失败，请重新更新：" + msg);
			}
		});
	}

	public void AutoLogin() {
		//mIsLogin = false;
		// 读取本地保存的账号密码文件
		mUser = new UserProfile();
		mUser.PhoneNumber = mPreferences.getString(mAliasUser, "");
		mUser.Password = mPreferences.getString(mAliasPwd, "");
		// 如果本地已经存有数据，那么取出来与服务器验证是否成功
		if (mUser.PhoneNumber == null || mUser.Password == null || mUser.PhoneNumber.isEmpty()
				|| mUser.Password.isEmpty()) {
			Log.d(TAG, "用户未登录过");
			globalContext.sendBroadcast(new Intent(AUTO_LOGIN_NEVERLOGIN));
			// ShowToast(globalContext, "用户未登录过。", Toast.LENGTH_SHORT);
			return;
		}

		final UserProfile remote_user = new UserProfile();
		remote_user.QueryByPhoneNumber(globalContext, mUser.PhoneNumber, new DataCallback<UserProfile>() {

			@Override
			public void onSuccess(UserProfile object) {
 				if (object.Password.equals(mUser.Password)) {
					mIsLogin = true;
					mUser = object;
					globalContext.sendBroadcast(new Intent(AUTO_LOGIN_SUCCESS));
					Log.d(TAG, "用户自动登陆成功。");
					pullWifiProfile();
					// ShowToast(globalContext, "用户自动登陆成功。",Toast.LENGTH_SHORT);
				} else {
					globalContext.sendBroadcast(new Intent(AUTO_LOGIN_FAIL));
					Log.d(TAG, "用户自动登陆失败，请重新登陆。");
					// ShowToast(globalContext,
					// "用户自动登陆失败，请重新登陆。",Toast.LENGTH_SHORT);
				}
			}

			@Override
			public void onFailed(String msg) {
				Log.d(TAG, "当前网络不稳定，请稍后再试。");
				//AutoLogin();
				// ShowToast(globalContext,
				// "用户自动登陆失败，用户未登陆过。",Toast.LENGTH_SHORT);
			}
		});
	}

	public void logout() {
		globalContext.sendBroadcast(new Intent(LOGIN_OUT));
		mIsLogin = false;
		mUser = null;
		SharedPreferences.Editor sharedata = mPreferences.edit();
		sharedata.clear();
		sharedata.commit();
		Log.d(TAG, "用户退出成功");
	}
	
	//掉线
	public void logoff() {
		globalContext.sendBroadcast(new Intent(LOGIN_OFF));
		mIsLogin = false;
	}

	private void SaveProfileLocal(UserProfile user) {
		// 保存账号密码到本地用于下次登陆
		// TODO(binfei):先简单的保存在本地某个文件，以后改成sqlite3
		SharedPreferences.Editor sharedata = mPreferences.edit();
		sharedata.putString(mAliasUser, user.PhoneNumber);
		sharedata.putString(mAliasPwd, user.Password);
		sharedata.commit();
		Log.d(TAG, "用户密码本地保存成功。");
		// ShowToast(globalContext, "用户密码本地保存成功。", Toast.LENGTH_SHORT);
	}

	public boolean getCurLoginStatus() {
		return mIsLogin && mUser != null;
	}

	public boolean isLogined() {
		return mIsLogin && mUser != null;
	}

	public UserProfile getCurLoginUserInfo() {
		if (mIsLogin) {
			return mUser;
		}
		return null;
	}
	
	//只要用户登录过就会有一个id，不管它 在线不在线
	public String getUserid() {
		if (mUser != null)
			return mUser.PhoneNumber;
		return null;
	}

	private void pullWifiProfile() {
		if (!mIsLogin)
			return;
		
		// 去服务器上查询是否已经登记了自己的wifi
		WifiProfile wifi = new WifiProfile();
		wifi.Sponser = getCurLoginUserInfo().PhoneNumber;
		wifi.QueryBySponser(globalContext, wifi.Sponser, new MultiDataCallback<WifiProfile>() {
	
			@Override
			public void onSuccess(List<WifiProfile> objects) {
				if (objects.size() >= 1) {
					mWifiProfile = objects.get(0); // TODO(binfei)目前一个账号才对应一个wifi
				}
			}
	
			@Override
			public void onFailed(String msg) {
				Log.d(TAG, "查询登记的wifi失败，正在重试: " + msg);
				pullWifiProfile();//TODO(binfei)这样做有可能造成堆栈溢出
			}
		});
	}
	
	public void updateWifiDynamic() {
		if (WifiListHelper.getInstance(globalContext).mWifiInfoCur == null)
			return;
		
		WifiDynamic record = new WifiDynamic();
		record.MacAddr = WifiListHelper.getInstance(globalContext).mWifiInfoCur.getWifiMAC();
		double lat = LocationHelper.getInstance(globalContext).getLatitude();
		double lng = LocationHelper.getInstance(globalContext).getLongitude();
		record.Geometry = new BmobGeoPoint(lng, lat);
		record.MarkLoginTime();
		if (mUser != null && mUser.PhoneNumber != null && !mUser.PhoneNumber.equals("")) {
			record.Userid = mUser.PhoneNumber;
		} else {
			record.Userid = "user_" + DeviceUID.getLocalMacAddressFromIp(globalContext);
		}
		record.StoreRemote(globalContext, new DataCallback<WifiDynamic>() {

			@Override
			public void onSuccess(WifiDynamic object) {
				Log.i(TAG, "Success to store wifi dynamic info to server");
			}

			@Override
			public void onFailed(String msg) {
				Log.i(TAG, "Failed to store wifi dynamic info to server:" + msg);
			}
		});
	}
	
	public boolean canAccessDirectly(String MacAddr) {
		if (mWifiProfile != null && mWifiProfile.MacAddr.equals(MacAddr))
			return true;
		return false;
	}
}
