package com.anynet.wifiworld.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import com.anynet.wifiworld.LoginActivity;
import com.anynet.wifiworld.app.WifiWorldApplication;
import com.anynet.wifiworld.constant.Const;
import com.anynet.wifiworld.me.UserProfile;



public class LoginHelper
{
    
    private final String TAG = LoginHelper.class.getSimpleName();
    
    /**
     * 登录类型
     */
    //private int mLoginType = LOGIN_FROME_USERCENTER;
    
    //public static final int LOGIN_FROME_AUTO = 0;
    
    //public static final int LOGIN_FROME_USERCENTER = 1;
    
    //public static final int LOGIN_FROME_YUNBO = 2;
    
    /**
     * 登出类型
     */
   // private int mLogoutType = LOGOUT_BY_USER;
    
    //public static final int LOGOUT_BY_USER = 10;
    
    //public static final int LOGOUT_KICKOUT = 11;
    
    //public static final int LOGOUT_TIMEOUT = 12;
    
   // public static final int LOGOUT_OTHER = 13;
    
    /**
     * 当前用户状态
     */
    //private int mCurLoginStatus = STATUS_LOGOUT;
    
    //public static final int STATUS_LOGOUT = 0;
    
    //public static final int STATUS_LOGINING = 1;
    
    //public static final int STATUS_UNLOGIN = 2;
    
    //public static final int STATUS_LOGINED = 3;
    
    //产品类型
    //public static final int BUSSNISS_TYPE = 61;
    
    //private PreferenceHelper pref = PreferenceHelper.getInstance();
    
	private static String mUserprofileDataFile = "userprofile.conf";
	private static String mAliasUser = "PhoneNumber";
	private static String mAliasPwd = "Password";
	private boolean mIsLogin = false;
	
    private static LoginHelper mInstance = null;
    private SharedPreferences mPreferences = null;
    private Context globalContext = null;
    
   // private XLUserUtil mLoginUilt;
    
    //private List<LogoutObserver> mLogoutObservers;
    
    //private List<LoginObserver> mLoginObservers;
    
    //private List<RefreshUserInfoObserver> mRefreshUserInfoObservers;
    
    //private XLOnUserListener mLoginListener;

// ------------------------------------------------------------------------------------------------
    public static LoginHelper getInstance() {
        if (null == mInstance) {
            mInstance = new LoginHelper();
        }
        return mInstance;
    }
    
    private LoginHelper() {
        //mLogoutObservers = new ArrayList<LogoutObserver>();
        //mLoginObservers = new ArrayList<LoginObserver>();
        //mRefreshUserInfoObservers = new ArrayList<RefreshUserInfoObserver>();
    }
    
    public void init(Context context) {
    	this.globalContext = context;
        mPreferences = globalContext.getSharedPreferences(mUserprofileDataFile, Context.MODE_PRIVATE);
    }
  
// ------------------------------------------------------------------------------------------------
    public void Login(UserProfile profile) {
    	mIsLogin = false;
        final UserProfile user = profile;
        
        //存储数据到数据库，先查询数据库存在数据，如果存在便更新
        BmobQuery<UserProfile> query = new BmobQuery<UserProfile>();
		query.addWhereEqualTo(user.PhoneNumber.getClass().getName(), user.PhoneNumber);
		query.findObjects(globalContext, new FindListener<UserProfile>() {
			
			@Override
			public void onSuccess(List<UserProfile> object) {
				// TODO Auto-generated method stub
				if (object.size() == 1) {
					object.get(0).Password = user.Password;
					object.get(0).update(globalContext,new UpdateListener() {

						@Override
						public void onSuccess() {
							// TODO Auto-generated method stub
							Log.d(TAG, "用户登陆成功。");
							SaveProfileLocal(user);
							mIsLogin = true;
							return;
						}
						
						@Override
						public void onFailure(int arg0, String arg1) {
							// TODO Auto-generated method stub
							
						}
					});
				} else {
					//查询失败则保存
					user.save(globalContext, new SaveListener() {

						@Override
						public void onSuccess() {
							SaveProfileLocal(user);
							mIsLogin = true;
						}

						@Override
						public void onFailure(int code, String arg0) {
						}
					});
				}
			}
	
			@Override
			public void onError(int code, String msg) {
			}
		});
    }
    
    public void AutoLogin() {
    	mIsLogin = false;
    	// 读取本地保存的账号密码文件
    	final UserProfile user = new UserProfile();
		SharedPreferences sharedata = globalContext.getSharedPreferences(mUserprofileDataFile, 0);
		user.PhoneNumber = sharedata.getString(mAliasUser, "").trim();
		user.Password = sharedata.getString(mAliasPwd, "").trim();
		mIsLogin = false;
		// 如果本地已经存有数据，那么取出来与服务器验证是否成功
		if (user.PhoneNumber == null || user.Password == null || user.PhoneNumber.isEmpty()
				|| user.Password.isEmpty()) {
			return;
		}

		BmobQuery<UserProfile> query = new BmobQuery<UserProfile>();
		query.addWhereEqualTo(mAliasPwd, user.Password);
		query.findObjects(globalContext, new FindListener<UserProfile>() {
			@Override
			public void onSuccess(List<UserProfile> object) {
				// TODO Auto-generated method stub
				if (object.size() == 1) {
					mIsLogin = true;
				} else {
				}
			}

			@Override
			public void onError(int code, String msg) {
				// TODO Auto-generated method stub
			}
		});
    }
    
    public void logout() {
    	mIsLogin = false; 
    }
    
    private void SaveProfileLocal(UserProfile user) {
    	// 保存账号密码到本地用于下次登陆
		// TODO(binfei):先简单的保存在本地某个文件，以后改成sqlite3
		SharedPreferences.Editor sharedata = globalContext
				.getSharedPreferences(mUserprofileDataFile, globalContext.MODE_PRIVATE).edit();
		sharedata.putString(mAliasUser, user.PhoneNumber);
		sharedata.putString(mAliasPwd, user.Password);
		sharedata.commit();
    }
    
    public boolean getCurLoginStatus() {
        return mIsLogin;
    }
    
    /*//返回登录页面,需要中心输入密码
    public  void gotoLogin(Context context, String reason)
    {
        //退出登录
        LoginHelper.getInstance().storeAutoLogWhenLaunch(false);
        //释放对象
        //LoginHelper.getInstance().unInit();

        WifiWorldApplication.getInstance().finishAllActivity();
        LoginActivity.startForKick(WifiWorldApplication.getInstance(), reason);
        
    }
      
    public void refreshUserInfoObserverNotify(int errCode, boolean isInfoNew)
    {
        if (null != mRefreshUserInfoObservers)
        {
            for (int i = 0; i < mRefreshUserInfoObservers.size(); i++)
            {
                if (mRefreshUserInfoObservers.get(i) != null)
                {
                    mRefreshUserInfoObservers.get(i).OnRefreshUserInfoCompleted(errCode, isInfoNew);
                }
            }
        }
    }
    
    public void addLogoutObserver(LogoutObserver obsr)
    {
        if (obsr == null || mLogoutObservers.contains(obsr))
            return;
        mLogoutObservers.add(obsr);
    }
    
    public void removeLogoutObserver(LogoutObserver obsr)
    {
        mLogoutObservers.remove(obsr);
    }
    
    public interface AutoLoginListener
    {
        public void onAutoLoginWithoutGui(String userName, String pwd);
    }
    
    public interface LoginObserver
    {
        public void OnLoginCompleted(int errCode, int loginType);
    }
    
    public interface RefreshUserInfoObserver
    {
        public void OnRefreshUserInfoCompleted(int errCode, boolean isInfoNew);
    }
    
    public interface LogoutObserver
    {
        public void OnLogout(int logoutType);
    }
    
    public interface LogingStateChangedObserver
    {
        public void OnLoginStateChanged(boolean isLogging);
    }*/
}
