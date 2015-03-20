package com.anynet.wifiworld.data;

import java.util.List;

import android.content.Context;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class WifiUnregistered extends WifiProfile {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String unique_key = "MacAddr";

// ------------------------------------------------------------------------------------------------
	public void QueryData(final Context context, String Mac, DataCallback<WifiUnregistered> callback) {
		final DataCallback<WifiUnregistered> _callback = callback;
		BmobQuery<WifiUnregistered> query = new BmobQuery<WifiUnregistered>();
		query.addWhereEqualTo(unique_key, Mac);
		query.findObjects(context, new FindListener<WifiUnregistered>() {
			@Override
			public void onSuccess(List<WifiUnregistered> object) {
				if (object.size() == 1) {
					_callback.onSuccess(object.get(0));
				} else {
					_callback.onFailed("数据库中没有数据。");
				}
			}

			@Override
			public void onError(int code, String msg) {
				_callback.onFailed(msg);
			}
		});
	}
	
	public void StoreData(final Context context, DataCallback<WifiUnregistered> callback) {
		final DataCallback<WifiUnregistered> _callback = callback;
		final WifiUnregistered wifi = this;
		//先查询，如果有数据就更新，否则增加一条新记录
		QueryData(context, MacAddr, new DataCallback<WifiUnregistered>() {

			@Override
			public void onSuccess(final WifiUnregistered object) {
				wifi.setObjectId(object.getObjectId());
				wifi.update(context, new UpdateListener() {

					@Override
					public void onSuccess() {
						_callback.onSuccess(wifi);
					}
					
					@Override
					public void onFailure(int arg0, String msg) {
						_callback.onFailed(msg);
					}
				});
			}

			@Override
			public void onFailed(String msg) {
				wifi.save(context, new SaveListener() {

					@Override
					public void onSuccess() {
						_callback.onSuccess(wifi);
					}

					@Override
					public void onFailure(int code, String msg) {
						_callback.onFailed(msg);
					}
				});
			}
			
		});
	}
}
