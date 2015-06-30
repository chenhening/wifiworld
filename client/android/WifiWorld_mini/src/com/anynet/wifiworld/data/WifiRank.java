package com.anynet.wifiworld.data;

import java.util.List;

import android.content.Context;
import android.util.Log;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class WifiRank extends BmobObject {
	private final static String TAG = WifiRank.class.getSimpleName();
	private final static String MACADDR_TAG = "MacAddr";
	
	private static final long serialVersionUID = 1L;
	
	public String MacAddr; //wifi的唯一标识
	public long Rank; //Wifi排名
	public long Score; //Wifi总得分
	
	public void StoreRemote(final Context context, DataCallback<WifiRank> callback) {
		final DataCallback<WifiRank> _callback = callback;
		final WifiRank wifiRank = this;
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				wifiRank.save(context, new SaveListener() {
					
					@Override
					public void onSuccess() {
						_callback.onSuccess(wifiRank);
					}

					@Override
					public void onFailure(int code, String msg) {
						_callback.onFailed(msg);
					}
				});
			}
		}).start();
	}
	
	public void QueryByMacAddress(final Context context, final String Mac, DataCallback<WifiRank> callback) {
		final DataCallback<WifiRank> _callback = callback;
		final BmobQuery<WifiRank> query = new BmobQuery<WifiRank>();
		query.addWhereEqualTo(MACADDR_TAG, Mac);
		Log.d(TAG, "Start to query wifi rank table for:" + Mac);
		new Thread(new Runnable() {

			@Override
			public void run() {
				query.findObjects(context, new FindListener<WifiRank>() {
					@Override
					public void onSuccess(List<WifiRank> object) {
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
				Log.d(TAG, "Finish to query wifi rank");
			}

		}).start();
	}
}
