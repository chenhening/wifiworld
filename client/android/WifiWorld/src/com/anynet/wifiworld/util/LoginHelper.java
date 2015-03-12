package com.xunlei.crystalandroid.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import android.content.Context;
import android.util.Log;
import com.xunlei.common.encrypt.PeerID;
import com.xunlei.common.member.XLErrorCode;
import com.xunlei.common.member.XLOnUserListener;
import com.xunlei.common.member.XLUserInfo;
import com.xunlei.common.member.XLUserInfo.USERINFOKEY;
import com.xunlei.common.member.XLUserUtil;
import com.xunlei.common.member.xl_hspeed_capacity;
import com.xunlei.crystalandroid.LoginActivity;
import com.xunlei.crystalandroid.app.CrystalApplication;
import com.xunlei.crystalandroid.constant.Const;



public class LoginHelper
{
    
    private final String TAG = LoginHelper.class.getSimpleName();
    
    /**
     * 登录类型
     */
    private int mLoginType = LOGIN_FROME_USERCENTER;
    
    public static final int LOGIN_FROME_AUTO = 0;
    
    public static final int LOGIN_FROME_USERCENTER = 1;
    
    public static final int LOGIN_FROME_YUNBO = 2;
    
    /**
     * 登出类型
     */
    private int mLogoutType = LOGOUT_BY_USER;
    
    public static final int LOGOUT_BY_USER = 10;
    
    public static final int LOGOUT_KICKOUT = 11;
    
    public static final int LOGOUT_TIMEOUT = 12;
    
    public static final int LOGOUT_OTHER = 13;
    
    /**
     * 当前用户状态
     */
    private int mCurLoginStatus = STATUS_LOGOUT;
    
    public static final int STATUS_LOGOUT = 0;
    
    public static final int STATUS_LOGINING = 1;
    
    public static final int STATUS_UNLOGIN = 2;
    
    public static final int STATUS_LOGINED = 3;
    
    //产品类型
    public static final int BUSSNISS_TYPE = 61;
    
    private PreferenceHelper pref = PreferenceHelper.getInstance();
    
    private static LoginHelper mInstance = null;
    
    private XLUserUtil mLoginUilt;
    
    private List<LogoutObserver> mLogoutObservers;
    
    private List<LoginObserver> mLoginObservers;
    
    private List<RefreshUserInfoObserver> mRefreshUserInfoObservers;
    
    private XLOnUserListener mLoginListener;
    
    public static LoginHelper getInstance()
    {
        if (null == mInstance)
        {
            mInstance = new LoginHelper();
        }
        return mInstance;
    }
    
    private LoginHelper()
    {
        
        mLogoutObservers = new ArrayList<LogoutObserver>();
        mLoginObservers = new ArrayList<LoginObserver>();
        mRefreshUserInfoObservers = new ArrayList<RefreshUserInfoObserver>();
    }
    
    /**
     * 保存和更新用户信息
     */
    public void storeLoginInfo()
    
    {
        //记录当前时间
        long curTime = System.currentTimeMillis();
        
        XLUserInfo userInfo = XLUserUtil.getInstance().getCurrentUser();

        //保存昵称
        pref.setString(Const.NICK_NAME, userInfo.getStringValue(USERINFOKEY.NickName));
        
        pref.setString(Const.EN_PWD, userInfo.getStringValue(USERINFOKEY.EncryptedPassword));
        pref.setString(Const.PWD_CHECK_NUM, userInfo.getStringValue(USERINFOKEY.PasswordCheckNum));
        pref.setString(Const.USER_ID, String.valueOf(userInfo.getIntValue(USERINFOKEY.UserID)));
        
        //保存时间
        pref.setLong(Const.LAST_TIME, curTime);
        
        storeAutoLogWhenLaunch(true); 
        
    }
    
    /**
     * 重置用户登录信息
     */
    public void resetLoginInfo()
    {
        
        pref.setString(Const.EN_PWD, null);
        pref.setString(Const.PWD_CHECK_NUM, null);
        pref.setString(Const.NICK_NAME, null);
        
        /** 不重置userid */
        // pref.setString(Const.USER_ID,null);
        
      
        
    }
    
    public void init(Context cxt)
    {
        mLoginUilt = XLUserUtil.getInstance();
        mLoginUilt.Init(cxt, BUSSNISS_TYPE, "1.0.0", PeerID.getPeerId(cxt));
        
        mLoginListener = new XLOnUserListener()
        {
            @Override
            public boolean onUserLogin(int errorCode, XLUserInfo userInfo, Object userdata, String errorDesc)
            {
                onLoginCompleted(errorCode, userInfo, userdata);
                return super.onUserLogin(errorCode, userInfo, userdata, errorDesc);
            }
            
            @Override
            public boolean onUserLogout(int errorCode, XLUserInfo userInfo, Object userdata)
            {
                onLogoutCompleted(errorCode, userInfo);
                return super.onUserLogout(errorCode, userInfo, userdata);
            }
            
            @Override
            public boolean onUserInfoCatched(int errorCode, List<USERINFOKEY> catchedInfoList, XLUserInfo userInfo, Object userdata)
            {
                onRefreshUserInfoCompleted(errorCode, catchedInfoList, userInfo);
                return super.onUserInfoCatched(errorCode, catchedInfoList, userInfo, userdata);
            }
            
            @Override
            public boolean onUserActivated(int errorCode, XLUserInfo userInfo, Object userdata, String errorDesc)
            {
                return super.onUserActivated(errorCode, userInfo, userdata, errorDesc);
            }
            
            @Override
            public boolean onHighSpeedCatched(int errorCode, XLUserInfo userInfo, xl_hspeed_capacity hcap, Object userdata)
            {
                
                return super.onHighSpeedCatched(errorCode, userInfo, hcap, userdata);
            }
            
            public boolean onUserVerifyCodeUpdated(int errorCode, String verifyKey, int imageType, final byte[] imageContent, Object userdata)
            {
                return true;
            }
            
        };
        mLoginUilt.attachListener(mLoginListener);
        
    }
    
  
    
    
    public void storeAutoLogWhenLaunch(boolean auto) {
        pref.setBoolean(Const.USER_AUTO_LOGIN,
                 auto);
    }
    
    /**
     * 用户直接退出
     * @param logoutType
     */
    public void logout(int logoutType)
    {
        
        mLoginType = logoutType;
        mCurLoginStatus = STATUS_LOGOUT;
        resetLoginInfo();
        storeAutoLogWhenLaunch(false); 
        mLoginUilt.userLogout(mLoginListener, null);
    }
    
    
    /**
     * 自动退出，一般是点返回键
     */
    public void autoLogout()
    {
        
        mCurLoginStatus = STATUS_LOGOUT;
        mLoginUilt.userLogout(null, null);
    }
    
    
    
    //返回登录页面,需要中心输入密码
    public  void gotoLogin(Context context, String reason)
    {
        //退出登录
        LoginHelper.getInstance().storeAutoLogWhenLaunch(false);
        /** 返回登录无需释放,否则登录失败空指针 */
        //释放对象
        //LoginHelper.getInstance().unInit();

        CrystalApplication.getInstance().finishAllActivity();
        LoginActivity.startForKick(CrystalApplication.getInstance(), reason);
        
    }
    
    
    public void onLoginCompleted(final int errCode, XLUserInfo info, Object userdata)
    {
        if (errCode == XLErrorCode.SUCCESS)
        {
            //成功
            mCurLoginStatus = STATUS_LOGINED;
            
        }
        else if (errCode == XLErrorCode.ACCOUNT_INVALID)
        {
            //账户名不存在
            mCurLoginStatus = STATUS_UNLOGIN;
            
        }
        else if (errCode == XLErrorCode.PASSWORD_ERROR)
        {
            
            //密码错误
            mCurLoginStatus = STATUS_UNLOGIN;
            if (mLoginType != LOGIN_FROME_AUTO)
            {
                
            }
        }
        else
        {
            mCurLoginStatus = STATUS_UNLOGIN;
        }
        
        if (mLoginObservers != null)
        {
            Iterator<LoginObserver> iterator = mLoginObservers.iterator();
            while (iterator.hasNext())
            {
                iterator.next().OnLoginCompleted(errCode, (Integer) userdata);
            }
        }
        
    }
    
    public void onLogoutCompleted(int errCode, XLUserInfo userInfo)
    {
        Log.v("logout", "onUserLogoutCompleted,errCode = " + errCode);
        
        mCurLoginStatus = STATUS_LOGOUT;
        
        if (errCode == XLErrorCode.SUCCESS)
        {
            mLogoutType = LOGOUT_BY_USER;
        }
        else if (errCode == XLErrorCode.SESSIONID_KICKOUT)
        {
            mLogoutType = LOGOUT_KICKOUT;
        }
        else if (errCode == XLErrorCode.SESSIONID_TIMEOUT)
        {
            mLogoutType = LOGOUT_TIMEOUT;
        }
        else
        {
            mLogoutType = LOGOUT_OTHER;
        }
        
        //通知别的监听器
        if (null != mLogoutObservers)
        {
            for (int i = 0; i < mLogoutObservers.size(); i++)
            {
                if (mLogoutObservers.get(i) != null)
                {
                    mLogoutObservers.get(i).OnLogout(mLogoutType);
                }
            }
        }
        
    }
    
    public void onRefreshUserInfoCompleted(int errorCode, List<USERINFOKEY> catchedInfoList, XLUserInfo userInfo)
    {
        Log.v("refresh", "refresh errCode=" + errorCode);
        if (errorCode == XLErrorCode.SUCCESS)
        {
            boolean infoNew = false;
            Log.v("refresh", "refreshUserInfoObserverNotify,infoNew = " + infoNew);
            refreshUserInfoObserverNotify(errorCode, infoNew);
        }
        else
        {
            refreshUserInfoObserverNotify(errorCode, false);
        }
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
    }
    
    
    public int getCurLoginStatus() {
        return mCurLoginStatus;
    }

    public void setCurLoginStatus(int status) {
        this.mCurLoginStatus = status;
    }
    
    
    public void unInit() {
        if (mLoginUilt != null) {
            mLoginUilt.Uninit();
            mLoginUilt = null;
            mLoginListener = null;
        }
    }
    
}
