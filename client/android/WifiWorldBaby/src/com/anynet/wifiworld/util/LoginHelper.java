package com.anynet.wifiworld.util;

import java.util.List;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import com.anynet.wifiworld.me.UserProfile;

public class LoginHelper {
    
    private final String TAG = LoginHelper.class.getSimpleName();
    
	private static String mUserprofileDataFile = "userprofile.conf";
	private static String mAliasUser = "PhoneNumber";
	private static String mAliasPwd = "Password";
	private UserProfile mUser;
	private boolean mIsLogin = false;
	
    private static LoginHelper mInstance = null;
    private SharedPreferences mPreferences = null;
    private Context globalContext = null;

// ------------------------------------------------------------------------------------------------
    public static LoginHelper getInstance() {
        if (null == mInstance) {
            mInstance = new LoginHelper();
        }
        return mInstance;
    }
    
    private LoginHelper() {
    }
    
    public void init(Context context) {
    	this.globalContext = context;
        mPreferences = globalContext.getSharedPreferences(mUserprofileDataFile, Context.MODE_PRIVATE);
    }
  
// ------------------------------------------------------------------------------------------------
    public void Login(UserProfile profile) {
    	mIsLogin = false;
    	mUser = profile;
        
        //存储数据到数据库，先查询数据库存在数据，如果存在便更新
        BmobQuery<UserProfile> query = new BmobQuery<UserProfile>();
		query.addWhereEqualTo(mAliasUser, mUser.PhoneNumber);
		query.findObjects(globalContext, new FindListener<UserProfile>() {
			
			@Override
			public void onSuccess(List<UserProfile> object) {
				Log.d(TAG, "用户旧密码查询成功。");
				if (object.size() == 1) {
					object.get(0).Password = mUser.Password;
					object.get(0).update(globalContext,new UpdateListener() {

						@Override
						public void onSuccess() {
							Log.d(TAG, "用户密码更新成功。");
							Toast.makeText(globalContext, "用户密码更新成功。", Toast.LENGTH_SHORT).show();
							SaveProfileLocal(mUser);
							mIsLogin = true;
							return;
						}
						
						@Override
						public void onFailure(int arg0, String arg1) {
							Log.d(TAG, "用户密码更新失败。");
						}
					});
				} else {
					SaveProfileRemote(mUser);
					SaveProfileLocal(mUser);
				}
			}
	
			@Override
			public void onError(int code, String msg) {
				Log.d(TAG, "用户旧密码查询失败。");
				SaveProfileRemote(mUser);
				SaveProfileLocal(mUser);
			}
		});
    }
    
    public void AutoLogin() {
    	mIsLogin = false;
    	// 读取本地保存的账号密码文件
    	mUser = new UserProfile();
    	mUser.PhoneNumber = mPreferences.getString(mAliasUser, "").trim();
    	mUser.Password = mPreferences.getString(mAliasPwd, "").trim();
		mIsLogin = false;
		// 如果本地已经存有数据，那么取出来与服务器验证是否成功
		if (mUser.PhoneNumber == null || mUser.Password == null || mUser.PhoneNumber.isEmpty()
				|| mUser.Password.isEmpty()) {
			Log.d(TAG, "用户自动登陆失败，本地未保存登陆密码。");
			return;
		}

		BmobQuery<UserProfile> query = new BmobQuery<UserProfile>();
		query.addWhereEqualTo(mAliasUser, mUser.PhoneNumber);
		query.findObjects(globalContext, new FindListener<UserProfile>() {
			@Override
			public void onSuccess(List<UserProfile> object) {
				// TODO Auto-generated method stub
				if (object.size() == 1) {
					String password = object.get(0).Password = mUser.Password;
					if (password.equals(mUser.Password)) {
						mIsLogin = true;
						Log.d(TAG, "用户自动登陆成功。");
						Toast.makeText(globalContext, "用户自动登陆成功。", Toast.LENGTH_SHORT).show();
					} else {
						Log.d(TAG, "用户自动登陆失败，请重新登陆。");
						Toast.makeText(globalContext, "用户自动登陆失败，请重新登陆。", Toast.LENGTH_SHORT).show();
					}
				} else {
					Log.d(TAG, "用户自动登陆失败，用户未登陆过。");
					Toast.makeText(globalContext, "用户自动登陆失败，用户未登陆过。", Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onError(int code, String msg) {
				Log.d(TAG, "用户自动登陆失败，用户未登陆过。");
			}
		});
    }
    
    public void logout() {
    	mIsLogin = false; 
    }
    
    private void SaveProfileRemote(UserProfile profile) {
    	final UserProfile user = profile;
    	//查询失败则保存
		user.save(globalContext, new SaveListener() {

			@Override
			public void onSuccess() {
				SaveProfileLocal(user);
				mIsLogin = true;
				Log.d(TAG, "用户密码远程保存成功。");
				Toast.makeText(globalContext, "用户密码远程保存成功。", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onFailure(int code, String arg0) {
				Log.d(TAG, "用户密码远程保存失败。");
			}
		});
    }
    
    private void SaveProfileLocal(UserProfile user) {
    	// 保存账号密码到本地用于下次登陆
		// TODO(binfei):先简单的保存在本地某个文件，以后改成sqlite3
		SharedPreferences.Editor sharedata = mPreferences.edit();
		sharedata.putString(mAliasUser, user.PhoneNumber);
		sharedata.putString(mAliasPwd, user.Password);
		sharedata.commit();
		Log.d(TAG, "用户密码本地保存成功。");
		Toast.makeText(globalContext, "用户密码本地保存成功。", Toast.LENGTH_SHORT).show();
    }
    
    public boolean getCurLoginStatus() {
        return mIsLogin;
    }
    
    public UserProfile getCurLoginUserInfo() {
    	return mUser;
    }
}
