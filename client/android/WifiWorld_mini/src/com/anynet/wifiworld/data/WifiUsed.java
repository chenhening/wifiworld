package com.anynet.wifiworld.data;

import java.util.List;

import android.content.Context;
import android.util.Log;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

public class WifiUsed extends BmobObject {
	public final static String TAG = WifiProfile.class.getSimpleName();
	private static final long serialVersionUID = 1L;
	
	private String MacAddr;
	private int Count;
	private int Time;
	
	public String getMacAddr() {
		return MacAddr;
	}
	public void setMacAddr(String macAddr) {
		MacAddr = macAddr;
	}
	public int getCount() {
		return Count;
	}
	public void setCount(int count) {
		Count = count;
	}
	public int getTime() {
		return Time;
	}
	public void setTime(int time) {
		Time = time;
	}
	
	public void QueryByMacAddress(final Context context, final String Mac, DataCallback<WifiUsed> callback) {
		final DataCallback<WifiUsed> _callback = callback;
		final BmobQuery<WifiUsed> query = new BmobQuery<WifiUsed>();
		query.addWhereEqualTo("MacAddr", Mac);
		Log.d(TAG, "开始查询QueryByMacAddress:" + Mac);
		new Thread(new Runnable() {

			@Override
			public void run() {
				query.findObjects(context, new FindListener<WifiUsed>() {
					@Override
					public void onSuccess(List<WifiUsed> object) {
						if (object.size() == 1) {
							WifiUsed returnProfile = object.get(0);
							_callback.onSuccess(returnProfile);
						} else {
							_callback.onFailed("数据库中没有数据。");
						}
					}

					@Override
					public void onError(int code, String msg) {
						_callback.onFailed(msg);
					}
				});
				Log.d(TAG, "结束查询QueryByMacAddress:" + Mac);
			}

		}).start();
	}
}
