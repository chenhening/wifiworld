package com.anynet.wifiworld.data;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class WifiFindHistory extends BmobObject {

	private static final long serialVersionUID = 1L;
	
	public String MacAddr; //路由器地址
	public List<Map<String, Object>> Clues; //保存线索
	
	public void QueryRemote(final Context context, String Mac, DataCallback<WifiFindHistory> callback) {
		final DataCallback<WifiFindHistory> _callback = callback;
		final BmobQuery<WifiFindHistory> query = new BmobQuery<WifiFindHistory>();
		//query.setCachePolicy(CachePolicy.CACHE_THEN_NETWORK); // 先从缓存获取数据，再拉取网络数据更新
		query.addWhereEqualTo("MacAddr", Mac);
		Log.d("findObjects", "开始查询WifiFindHistory.QueryRemote");
		new Thread(new Runnable() {

			@Override
			public void run() {
				query.findObjects(context, new FindListener<WifiFindHistory>() {
					@Override
					public void onSuccess(List<WifiFindHistory> object) {
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
				Log.d("findObjects", "结束查询WifiFindHistory.QueryRemote");
			}

		}).start();
	}
	
	public void StoreRemote(final Context context, DataCallback<WifiFindHistory> callback) {
		final DataCallback<WifiFindHistory> _callback = callback;
		final WifiFindHistory user = this;
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
