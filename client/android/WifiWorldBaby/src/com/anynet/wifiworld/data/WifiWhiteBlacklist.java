package com.anynet.wifiworld.data;

import android.content.Context;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.listener.SaveListener;

public class WifiWhiteBlacklist extends BmobObject {

	private static final long serialVersionUID = 1L;
	
	public String Userid; //用户账号
	public String MacAddr; //路由器地址
	public boolean BeLove; //true表示关注
	
	public void StoreRemote(final Context context, DataCallback<WifiWhiteBlacklist> callback) {
		final DataCallback<WifiWhiteBlacklist> _callback = callback;
		final WifiWhiteBlacklist user = this;
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
}
