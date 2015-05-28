package com.anynet.wifiworld.data;

import java.util.List;

import android.content.Context;
import android.util.Log;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class WifiFollow extends BmobObject {
	private final static String TAG = WifiFollow.class.getSimpleName();
	private static final long serialVersionUID = 1L;
	
	private final static String MAC_ADDR = "MacAddr";
	private final static String USER_ID = "Userid";
	
	public String Userid; //用户账号
	public String MacAddr; //路由器地址
	public long FollowTime; //用户关注的时间
	
	public void QueryWifiByMac(final Context context, final String Mac, final String PhoneNumber, 
			MultiDataCallback<WifiFollow> callback) {
		final MultiDataCallback<WifiFollow> _callback = callback;
		final BmobQuery<WifiFollow> query = new BmobQuery<WifiFollow>();
		query.addWhereEqualTo(MAC_ADDR, Mac);
		query.addWhereEqualTo(USER_ID, PhoneNumber);
		Log.d(TAG, "Start to query wifi follow table for:" + Mac + "," + PhoneNumber);
		new Thread(new Runnable() {

			@Override
			public void run() {
				query.findObjects(context, new FindListener<WifiFollow>() {
					@Override
					public void onSuccess(List<WifiFollow> object) {
						_callback.onSuccess(object);
					}

					@Override
					public void onError(int code, String msg) {
						_callback.onFailed(msg);
					}
					
				});
				Log.d(TAG, "Finish to query wifi messages");
			}

		}).start();
	}
	
	public void QueryWifiByUser(final Context context, final String PhoneNumber, 
			MultiDataCallback<WifiFollow> callback) {
		final MultiDataCallback<WifiFollow> _callback = callback;
		final BmobQuery<WifiFollow> query = new BmobQuery<WifiFollow>();
		query.addWhereEqualTo(USER_ID, PhoneNumber);
		Log.d(TAG, "Start to query wifi follow table for: " + PhoneNumber);
		new Thread(new Runnable() {

			@Override
			public void run() {
				query.findObjects(context, new FindListener<WifiFollow>() {
					@Override
					public void onSuccess(List<WifiFollow> object) {
						_callback.onSuccess(object);
					}

					@Override
					public void onError(int code, String msg) {
						_callback.onFailed(msg);
					}
					
				});
				Log.d(TAG, "Finish to query wifi messages");
			}

		}).start();
	}
	
	public void FollowWifi(final Context context, DataCallback<WifiFollow> callback) {
		final DataCallback<WifiFollow> _callback = callback;
		final WifiFollow user = this;
		user.save(context, new SaveListener() {
			
			@Override
			public void onSuccess() {
				_callback.onSuccess(user);
			}

			@Override
			public void onFailure(int code, String msg) {
				_callback.onFailed(msg);
			}
		});
	}
	
	public void CancelFollow (final Context context, DataCallback<WifiFollow> callback) {
		final DataCallback<WifiFollow> _callback = callback;
		final WifiFollow user = this;
		user.delete(context, new DeleteListener() {
			
			@Override
			public void onSuccess() {
				_callback.onSuccess(user);
				
			}
			
			@Override
			public void onFailure(int code, String msg) {
				_callback.onFailed(msg);
				
			}
		});
	}
	
	public void MarkFollowTime() {
		FollowTime = System.currentTimeMillis();
	}
}
