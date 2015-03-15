package com.anynet.wifiworld.app;

import java.lang.ref.SoftReference;
import java.util.Stack;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.os.Handler;
import android.os.Process;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;
//import com.xunlei.common.member.XLUserUtil;
import com.anynet.wifiworld.constant.Const;
import com.anynet.wifiworld.dao.DaoMaster;
import com.anynet.wifiworld.dao.DaoMaster.OpenHelper;
import com.anynet.wifiworld.dao.DaoSession;
import com.anynet.wifiworld.util.LoginHelper;
import com.anynet.wifiworld.util.PackageSignHelper;
import com.anynet.wifiworld.util.PreferenceHelper;
import com.anynet.wifiworld.util.XLLog;
import com.anynet.wifiworld.R;

public class WifiWorldApplication extends Application
{
    
    private static WifiWorldApplication mInstance;
    
    private Stack<SoftReference<Activity>> mActivityStack = new Stack<SoftReference<Activity>>();
    
    private PreferenceHelper pref = PreferenceHelper.getInstance();

    private String TAG = WifiWorldApplication.class.getSimpleName();
    
    private PushAgent mPushAgent;
    
    @Override
    public void onCreate()
    {
        super.onCreate();
        
        boolean isRelease = new PackageSignHelper().isRelease(this);
        XLLog.log(TAG , "isReleased:", isRelease);
        XLLog.setLog(!isRelease);
        
        //初始化Prefrence
        PreferenceHelper.getInstance().init(getApplicationContext());
        
        initDefaultSetting();
        initImageLoader(getApplicationContext());
        
        //登录初始化
        LoginHelper.getInstance().init(this);
        
        // 初始化友盟消息推送
        initUmengPushMessage(isRelease);
        
        mInstance = this;

       
    }
    
    private void initUmengPushMessage(boolean isRelease) {
    	mPushAgent = PushAgent.getInstance(this);
    	if (isRelease) {
    		mPushAgent.setDebugMode(false);
    		PushAgent.getInstance(this).setAppkeyAndSecret(
            		getResources().getString(R.string.release_umeng_appkey), 
            		getResources().getString(R.string.release_umeng_message_secret));
    	} else {
    		mPushAgent.setDebugMode(true);
    		PushAgent.getInstance(this).setAppkeyAndSecret(
            		getResources().getString(R.string.test_umeng_appkey), 
            		getResources().getString(R.string.test_umeng_message_secret));
    	}
		
		
		
		/**
		 * 该Handler是在IntentService中被调用，故
		 * 1. 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK
		 * 2. IntentService里的onHandleIntent方法是并不处于主线程中，因此，如果需调用到主线程，需如下所示;
		 * 	      或者可以直接启动Service
		 * */
		UmengMessageHandler messageHandler = new UmengMessageHandler(){
			@Override
			public void dealWithCustomMessage(final Context context, final UMessage msg) {
				new Handler(getMainLooper()).post(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						UTrack.getInstance(getApplicationContext()).trackMsgClick(msg);
						Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
					}
				});
			}
			
			@Override
			public Notification getNotification(Context context,
					UMessage msg) {
				switch (msg.builder_id) {
				case 1:
					NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
					RemoteViews myNotificationView = new RemoteViews(context.getPackageName(), R.layout.notification_view);
					myNotificationView.setTextViewText(R.id.notification_title, msg.title);
					myNotificationView.setTextViewText(R.id.notification_text, msg.text);
					myNotificationView.setImageViewBitmap(R.id.notification_large_icon, getLargeIcon(context, msg));
					myNotificationView.setImageViewResource(R.id.notification_small_icon, getSmallIconId(context, msg));
					builder.setContent(myNotificationView);
					builder.setAutoCancel(true);
					Notification mNotification = builder.build();
					//由于Android v4包的bug，在2.3及以下系统，Builder创建出来的Notification，并没有设置RemoteView，故需要添加此代码
					mNotification.contentView = myNotificationView;
					return mNotification;
				default:
					//默认为0，若填写的builder_id并不存在，也使用默认。
					return super.getNotification(context, msg);
				}
			}
		};
		mPushAgent.setMessageHandler(messageHandler);

		/**
		 * 该Handler是在BroadcastReceiver中被调用，故
		 * 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK
		 * */
		UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler(){
			@Override
			public void dealWithCustomAction(Context context, UMessage msg) {
				Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
			}
		};
		mPushAgent.setNotificationClickHandler(notificationClickHandler);
    }
    
    //初始化配置项的值，默认都开
    private  void initDefaultSetting()
    {

        
        if ( !pref.contains(Const.SYS_NEW_MSG))
        {
        pref.setBoolean(Const.SYS_NEW_MSG, false);
        }
        
        
        
    }
    

    
    public static void initImageLoader(Context context)
    {
        
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context).threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs()
                // Remove for release app
                .build();
        
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }
    
    public static WifiWorldApplication getInstance()
    {
        return mInstance;
    }
    
    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
    }
    
    @Override
    public void onTerminate()
    {
//        if (XLUserUtil.getInstance() != null)
//        {
//            XLUserUtil.getInstance().Uninit();
//        }
        super.onTerminate();
    }
    
    // 以下用户管理整个应用的activity
    
    public Activity getTopActivity()
    {
        return mActivityStack.peek().get();
    }
    
    public void exitAplication()
    {
        //if(LoginHelper.getInstance().getCurLoginStatus()==LoginHelper.STATUS_LOGINED){
        //    LoginHelper.getInstance().autoLogout();
        //}
         
 //       LoginHelper.getInstance().unInit();
        finishAllActivity();
        ActivityManager activityMgr = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        activityMgr.killBackgroundProcesses(this.getPackageName());
        Process.killProcess(Process.myPid());

    }
    
    public void activityCreated(Activity activity)
    {
        if (activity == null)
        {
            return;
        }
        SoftReference<Activity> softActivity = new SoftReference<Activity>(
                activity);
        mActivityStack.push(softActivity);
    }
    
    public void activityDestroyed(Activity activity)
    {
        if (activity == null || mActivityStack.isEmpty() == true)
        {
            return;
        }
        //可能被垃圾回收了
        if (mActivityStack.peek().get() != activity)
        {
            return;
        }
        mActivityStack.pop();
    }
    
    public void finishAllActivity()
    {
        while (mActivityStack.isEmpty() == false)
        {
            Activity activity = mActivityStack.pop().get();
            if (activity != null)
            {
                activity.finish();
            }
        }
        mActivityStack.clear();
    }
    
    
    
    private static DaoMaster daoMaster;
    private static DaoSession daoSession;
    
    /**
     * 取得DaoMaster
     *
     * @param context
     * @return
     */
    public static DaoMaster getDaoMaster(Context context)
    {
        if (daoMaster == null)
        {
            OpenHelper helper = new DaoMaster.DevOpenHelper(context, Const.DATABASE_NAME, null);
            daoMaster = new DaoMaster(helper.getWritableDatabase());
        }
        return daoMaster;
    }
    /**
     * 取得DaoSession
     *
     * @param context
     * @return
     */
    public static DaoSession getDaoSession(Context context)
    {
        if (daoSession == null)
        {
            if (daoMaster == null)
            {
                daoMaster = getDaoMaster(context);
            }
            daoSession = daoMaster.newSession();
        }
        return daoSession;
    }

	public static boolean isLogin() {
		// TODO Auto-generated method stub
		return false;
	}
    
}
