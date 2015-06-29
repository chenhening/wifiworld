package com.anynet.wifiworld.data;

import java.util.List;

import android.content.Context;
import android.util.Log;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class WifiWhite extends BmobObject {

	private static final long serialVersionUID = 1L;
	
	public String MyUserid; //本人账号
	public String Whiteid; //加入白名单人的账号
	public int Friendliness; //两人之间的友好度
	public int AddType; //添加到白名单方式方式
	public long ReportTime; //加入时间
	
	public void addAWhiter(final Context context, DataCallback<WifiWhite> callback) {
		final DataCallback<WifiWhite> _callback = callback;
		final WifiWhite user = this;
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
	
	public void deleteAWhiter(final Context context, DataCallback<WifiWhite> callback) {
		final DataCallback<WifiWhite> _callback = callback;
		final WifiWhite user = this;
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
	
	public void QueryWhitersByUser(final Context context, final String PhoneNumber, 
			MultiDataCallback<WifiWhite> callback) {
		final MultiDataCallback<WifiWhite> _callback = callback;
		final BmobQuery<WifiWhite> query = new BmobQuery<WifiWhite>();
		query.addWhereEqualTo("MyUserid", PhoneNumber);
		new Thread(new Runnable() {

			@Override
			public void run() {
				query.findObjects(context, new FindListener<WifiWhite>() {
					@Override
					public void onSuccess(List<WifiWhite> object) {
						_callback.onSuccess(object);
					}

					@Override
					public void onError(int code, String msg) {
						_callback.onFailed(msg);
					}
					
				});
			}

		}).start();
	}
	
	public void MarkReportTime() {
		ReportTime = System.currentTimeMillis();
	}
	
	public String getTypeDesc() {
		switch(AddType) {
		case 0: {
			return "通过通信录批量导入添加";
		}
		case 1: {
			return "通过手动输入号码添加";
		}
		case 2: {
			return "通过使用对方网络自动添加";
		}
		}
		
		return "未知的添加方式";
	}
}
