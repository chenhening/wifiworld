package com.anynet.wifiworld.data;

import java.sql.Time;

import android.content.Context;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.listener.SaveListener;

public class WifiMessages extends BmobObject {

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
	
	public void MarkSendTime() {
		SendTime = System.currentTimeMillis();
	}
}
