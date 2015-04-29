package com.anynet.wifiworld.data;

import java.util.List;

import android.content.Context;
import android.provider.ContactsContract.Contacts.Data;
import android.util.Log;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class WifiBlack extends BmobObject {

	private static final long serialVersionUID = 1L;
	private String TAG = "WifiBlack";
	
	public String MacAddr; //路由器地址
	public String Userid; //用户账号
	public int BlackType; //拉黑类型
	public String Content; //补充说明
	public long ReportTime; //用户关注的时间
	
	public static class Type {
		public final static int WrongPwd = 1; //密码错误
		public final static int LowNetworkSpeed = 2; //网速太慢
		public final static int NotSafe = 3; //网络不安全
		public final static int others = 0; //其他原因
	}
	
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
	
	public void QueryWifiByUser(final Context context, final String PhoneNumber, 
			MultiDataCallback<WifiBlack> callback) {
		final MultiDataCallback<WifiBlack> _callback = callback;
		final BmobQuery<WifiBlack> query = new BmobQuery<WifiBlack>();
		query.addWhereEqualTo("Userid", PhoneNumber);
		Log.d(TAG, "Start to query wifi follow table for: " + PhoneNumber);
		new Thread(new Runnable() {

			@Override
			public void run() {
				query.findObjects(context, new FindListener<WifiBlack>() {
					@Override
					public void onSuccess(List<WifiBlack> object) {
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
	
	public void MarkReportTime() {
		ReportTime = System.currentTimeMillis();
	}
}
