package com.anynet.wifiworld.data;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.anynet.wifiworld.util.LoginHelper;

public class WifiDataAnalyseHelper {
	private final static String TAG = WifiDataAnalyseHelper.class.getSimpleName();
	
	private static WifiDataAnalyseHelper mInstance = null;
	private Context mContext = null;
	private int mReady = 0; //0:未下载     1：下载成功      2：下载失败
	private DataCallback<WifiDataAnalyseHelper> mCcallback = null;
	
	public static WifiDataAnalyseHelper getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new WifiDataAnalyseHelper(context);
		}
		return mInstance;
	}

	private WifiDataAnalyseHelper(Context context) {
		mContext = context;
		downloadData();
	}
	
	public void Start(boolean reload, DataCallback<WifiDataAnalyseHelper> callback) {
		if (!reload && mReady == 1) {
			callback.onSuccess(mInstance);
		} else {
			if (reload || mReady != 2)
				downloadData();
			mCcallback = callback;
		}
	}
	
	private static long day1 = 1*24*60*60*1000;
	public long headcount[] = new long[7]; //暂时统统计一周情况
	public long poscount[] = new long[4]; //暂时分东南西北四个方向
	public long timecount[] = new long[5]; //暂时分5个时间段
	private void downloadData() {
		//开启一个新的线程去下载数据
		new Thread(new Runnable() {

			@Override
            public void run() {
				//为了节省API调用一次性拉取一周数据下来
				final WifiDynamic record = new WifiDynamic();
				LoginHelper mLoginHelper = LoginHelper.getInstance(mContext);
				final WifiProfile mWifiProfile = mLoginHelper.mWifiProfile;
		        record.MacAddr = mWifiProfile.MacAddr;
		        record.MarkLoginTime();
		        //得到当前日期，取整到七天
		        final int today = (int) (record.LoginTime / day1);
		        record.LoginTime = (today+1) * day1;
			    record.QueryUserInOneWeek(mContext, record.LoginTime, new MultiDataCallback<WifiDynamic>() {

					@Override
		            public boolean onSuccess(List<WifiDynamic> objects) {
						for (int i=0; i<7; ++i)
							headcount[i] = 0;
						for (int i=0; i<4; ++i)
							poscount[i] = 0;
						for (int i=0; i<5; ++i)
							timecount[i] = 0;
						
						Calendar calendar = new GregorianCalendar();
						double router_x = mWifiProfile.Geometry.getLatitude();
						double router_y = mWifiProfile.Geometry.getLongitude();;
			            for (int i=0; i < objects.size(); ++i) {
			            	WifiDynamic one = objects.get(i);
			            	//分析时间段曲线
			            	int thatday = (int) (one.LoginTime / day1); //取整
			            	calendar.setTimeInMillis(one.LoginTime);
			            	int time = calendar.get(Calendar.HOUR_OF_DAY); 
			            	headcount[6 - today + thatday] += 1;
			            	if (time < 8) {
		            			timecount[0] += 1;
		            		} else if (time < 12) {
		            			timecount[1] += 1;
		            		} else if (time < 16) {
		            			timecount[2] += 1;
		            		} else if (time < 20) {
		            			timecount[3] += 1;
		            		} else {
		            			timecount[4] += 1;
		            		}
			            	
			            	//分析地理位置曲线
			            	double dist_x = one.Geometry.getLatitude() - router_x;
			            	double dist_y = one.Geometry.getLongitude() - router_y;
			            	if (dist_x >= 0) {
			            		if (dist_y >= 0) {
			            			poscount[0] += 1;
			            		} else {
			            			poscount[1] += 1;
			            		}
			            	} else {
			            		if (dist_y >= 0) {
			            			poscount[3] += 1;
			            		} else {
			            			poscount[2] += 1;
			            		}
			            	}
			            }
			            mReady = 1;
			            if (mCcallback != null) {
			            	mCcallback.onSuccess(mInstance);
			            }
			            Log.d(TAG, "查询到数据" + objects.size() + "条。");
			            return false;
		            }

					@Override
		            public boolean onFailed(String msg) {
			            Log.d(TAG, "查询一周数据失败。");
			            mReady = 2;
			            return false;
		            }
			    });
			}
        
        }).start();
	}
}
