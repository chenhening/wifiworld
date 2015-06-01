package com.anynet.wifiworld.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.UpdateListener;

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

	private UserProfile mUser = null;
	private boolean mIsLogin = false;
	private static LoginHelper mInstance = null;
	private Context globalContext = null;
	
	//private static String mUserprofileDataFile = "userprofile.conf";
	//private SharedPreferences mPreferences = null;
	private static String mRecordDataFile = "record.data";
	private static String key_record = "objectid";
	private static String key_logoff = "logoff";
	private static String key_login = "login";
	private SharedPreferences mCurRecord = null;//记录当前使用wifi的记录
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
		mCurRecord = globalContext.getSharedPreferences(mRecordDataFile, Context.MODE_PRIVATE);
	}

	// ------------------------------------------------------------------------------------------------
	public void Login(UserProfile profile) {
		mUser = profile;
		mUser.signUp(globalContext, new DataCallback<UserProfile>() {

			@Override
			public void onSuccess(UserProfile object) {
				Log.i(TAG, "用户注册成功。");
				//SaveProfileLocal(object);
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
		// 读取本地保存的账号密码文件
		mUser = (UserProfile) BmobUser.getCurrentUser(globalContext, UserProfile.class);
		if (mUser == null) {
			Log.d(TAG, "用户未登录过");
			return;
		}
		//登录成功
		mIsLogin = true;
		globalContext.sendBroadcast(new Intent(AUTO_LOGIN_SUCCESS));
		Log.d(TAG, "用户自动登陆成功。");
		pullWifiProfile();
	}

	public void logout() {
		mIsLogin = false;
		mUser.logout(globalContext);
		mUser = null;
		mKnockList.clear();
		mWifiProfile = null;
		globalContext.sendBroadcast(new Intent(LOGIN_OUT));
		Log.d(TAG, "用户退出成功");
	}
	
	//掉线
	public void logoff() {
		globalContext.sendBroadcast(new Intent(LOGIN_OFF));
		//mIsLogin = false;
		SharedPreferences.Editor sharedata = mCurRecord.edit();
		sharedata.putLong(key_logoff, System.currentTimeMillis()); //只记录数据那条记录的id
		sharedata.commit();
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
			return mUser.getUsername();
		return null;
	}

	private void pullWifiProfile() {
		if (!mIsLogin)
			return;
		
		// 去服务器上查询是否已经登记了自己的wifi
		WifiProfile wifi = new WifiProfile();
		wifi.Sponser = mUser.getUsername();
		wifi.QueryBySponser(globalContext, wifi.Sponser, new MultiDataCallback<WifiProfile>() {
	
			@Override
			public boolean onSuccess(List<WifiProfile> objects) {
				if (objects.size() >= 1) {
					mWifiProfile = objects.get(0); // TODO(binfei)目前一个账号才对应一个wifi
				}
				return false;
			}
	
			@Override
			public boolean onFailed(String msg) {
				Log.d(TAG, "查询登记的wifi失败，正在重试: " + msg);
				pullWifiProfile();//TODO(binfei)这样做有可能造成堆栈溢出
				return false;
			}
		});
	}
	
	public void updateWifiDynamic() {
		String objectid = mCurRecord.getString(key_record, "");
		long logintime = mCurRecord.getLong(key_login, 0);
		long logofftime = mCurRecord.getLong(key_logoff, 0);
		if (objectid != null) {
			WifiDynamic record = new WifiDynamic();
			record.setObjectId(objectid);
			record.LoginTime = logintime;
			record.LogoutTime = logofftime;
			record.update(globalContext, new UpdateListener() {

				@Override
                public void onFailure(int arg0, String arg1) {
                }

				@Override
                public void onSuccess() {
					//SharedPreferences.Editor sharedata = mCurRecord.edit();
					//sharedata.clear();
					//sharedata.commit();
                }
			});
		}
		
		if (WifiListHelper.getInstance(globalContext).mWifiInfoCur == null)
			return;
		
		WifiDynamic record = new WifiDynamic();
		record.MacAddr = WifiListHelper.getInstance(globalContext).mWifiInfoCur.getWifiMAC();
		double lat = LocationHelper.getInstance(globalContext).getLatitude();
		double lng = LocationHelper.getInstance(globalContext).getLongitude();
		record.Geometry = new BmobGeoPoint(lng, lat);
		record.MarkLoginTime();
		if (mUser != null && mUser.getUsername() != null && !mUser.getUsername().equals("")) {
			record.Userid = mUser.getUsername();
		} else {
			record.Userid = "user_" + DeviceUID.getLocalMacAddressFromIp(globalContext);
		}
		record.StoreRemote(globalContext, new DataCallback<WifiDynamic>() {

			@Override
			public void onSuccess(WifiDynamic object) {
				SharedPreferences.Editor sharedata = mCurRecord.edit();
				sharedata.putString(key_record, object.getObjectId()); //只记录数据那条记录的id
				sharedata.putLong(key_login, object.LoginTime);
				sharedata.commit();
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
