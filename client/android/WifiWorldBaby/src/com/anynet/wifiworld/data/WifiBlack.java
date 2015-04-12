package com.anynet.wifiworld.data;

import android.content.Context;
import android.provider.ContactsContract.Contacts.Data;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.SaveListener;

public class WifiBlack extends BmobObject {

	private static final long serialVersionUID = 1L;
	
	public String MacAddr; //路由器地址
	public String Userid; //用户账号
	public boolean WrongPwd; //密码错误
	public boolean LowNetworkSpeed; //网速太慢
	public boolean NotSafe; //网络不安全
	public String others; //其他原因
	public long ReportTime; //网络被举报的时间
	
	public void ReportWifi(final Context context, DataCallback<WifiBlack> callback) {
		final DataCallback<WifiBlack> _callback = callback;
		final WifiBlack user = this;
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
	
	public void RemoveReport (final Context context, DataCallback<WifiBlack> callback) {
		final DataCallback<WifiBlack> _callback = callback;
		final WifiBlack user = this;
		user.delete(context, new DeleteListener() {
			
			@Override
			public void onSuccess() {
				_callback.onSuccess(null);
				
			}
			
			@Override
			public void onFailure(int code, String msg) {
				_callback.onFailed(msg);
				
			}
		});
	}
	
	public void MarkReportTime() {
		ReportTime = System.currentTimeMillis();
	}
}
