package com.anynet.wifiworld.data;

import android.content.Context;
import java.sql.Time;
import java.util.List;

import android.content.Context;
import android.util.Log;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobQuery.CachePolicy;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class WifiMessages extends BmobObject {
	private final static String TAG = WifiMessages.class.getSimpleName();
	private final static String MACADDR_TAG = "MacAddr";
	
	private static final long serialVersionUID = 1L;
	
	public String MacAddr; //wifi的唯一标识
	public String Message; //这里指的是蹭网的用户id
	public long SendTime; //用户登陆网络的时间
	
	public void StoreRemote(final Context context, DataCallback<WifiMessages> callback) {
		final DataCallback<WifiMessages> _callback = callback;
		final WifiMessages user = this;
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
	
	public void QueryByMacAddress(final Context context, final String Mac, MultiDataCallback<WifiMessages> callback) {
		final MultiDataCallback<WifiMessages> _callback = callback;
		final BmobQuery<WifiMessages> query = new BmobQuery<WifiMessages>();
		query.addWhereEqualTo(MACADDR_TAG, Mac);
		Log.d(TAG, "Start to query wifi messages table for:" + Mac);
		new Thread(new Runnable() {

			@Override
			public void run() {
				query.findObjects(context, new FindListener<WifiMessages>() {
					@Override
					public void onSuccess(List<WifiMessages> object) {
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
	
	public void MarkSendTime() {
		SendTime = System.currentTimeMillis();
	}
}
