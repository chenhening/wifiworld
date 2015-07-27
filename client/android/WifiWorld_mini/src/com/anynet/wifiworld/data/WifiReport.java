package com.anynet.wifiworld.data;

import java.util.List;

import android.content.Context;
import android.util.Log;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class WifiReport extends BmobObject {

	private static final long serialVersionUID = 1L;
	private static String TAG = "WifiBlack";
	
	public String MacAddr; //路由器地址
	public String Userid; //用户账号
	public int ReportType; //拉黑类型
	public String Content; //补充说明
	public long ReportTime; //用户关注的时间
	
	public static class TypeReported {
		public static int OTHERS = 0;
		public static int WRONG_PWD = 1;
		public static int NOT_SAFE = 2;
	}
	
	public void ReportWifi(final Context context, DataCallback<WifiReport> callback) {
		final DataCallback<WifiReport> _callback = callback;
		final WifiReport user = this;
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
	
	public void RemoveReport (final Context context, DataCallback<WifiReport> callback) {
		final DataCallback<WifiReport> _callback = callback;
		final WifiReport user = this;
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
			MultiDataCallback<WifiReport> callback) {
		final MultiDataCallback<WifiReport> _callback = callback;
		final BmobQuery<WifiReport> query = new BmobQuery<WifiReport>();
		query.addWhereEqualTo("Userid", PhoneNumber);
		Log.d(TAG, "Start to query wifi follow table for: " + PhoneNumber);
		new Thread(new Runnable() {

			@Override
			public void run() {
				query.findObjects(context, new FindListener<WifiReport>() {
					@Override
					public void onSuccess(List<WifiReport> object) {
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
