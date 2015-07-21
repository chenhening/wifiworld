package com.anynet.wifiworld.data;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class WifiKnock extends BmobObject {

	private static final long serialVersionUID = 1L;
	
	public String MacAddr;
	public List<ArrayList<String>> Question = new ArrayList<ArrayList<String>>();
	
	public WifiKnock() {
		//设置默认值
		Question.add(new ArrayList<String>());
		Question.get(0).add("有两个人掉进了井里，死的人叫死人，活的人叫什么？");
		Question.get(0).add("救命");
		Question.get(0).add("活人");
		Question.get(0).add("生还者");
		Question.get(0).add("小强");
		Question.add(new ArrayList<String>());
		Question.get(1).add("后宫佳丽三千人，__________");
		Question.get(1).add("三千宠爱在一身");
		Question.get(1).add("铁棒也会磨成针");
		Question.get(1).add("富则妻妾成群");
		Question.get(1).add("为伊消得人憔悴");
		Question.add(new ArrayList<String>());
		Question.get(2).add("1元钱一瓶汽水，喝完后两个空瓶换一瓶汽水，问：你有20元钱，最多可以喝到几瓶汽水？");
		Question.get(2).add("39");
		Question.get(2).add("38");
		Question.get(2).add("40");
		Question.get(2).add("无限喝");
	}
	
	public void StoreRemote(final Context context, DataCallback<WifiKnock> callback) {
		final DataCallback<WifiKnock> _callback = callback;
		final WifiKnock wifi = this;

		new Thread(new Runnable() {

			@Override
			public void run() {

				// 先查询，如果有数据就更新，否则增加一条新记录
				QueryByMacAddress(context, MacAddr, new DataCallback<WifiKnock>() {

					@Override
					public void onSuccess(final WifiKnock object) {
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

		}).start();
	}
	
	public void QueryByMacAddress(final Context context, final String Mac, DataCallback<WifiKnock> callback) {
		final DataCallback<WifiKnock> _callback = callback;
		final WifiKnock itself = this;
		final BmobQuery<WifiKnock> query = new BmobQuery<WifiKnock>();
		query.addWhereEqualTo("MacAddr", Mac);
		itself.MacAddr = Mac;
		new Thread(new Runnable() {

			@Override
			public void run() {
				query.findObjects(context, new FindListener<WifiKnock>() {
					@Override
					public void onSuccess(List<WifiKnock> object) {
						if (object.size() >= 1) {
							_callback.onSuccess(object.get(0));
						} else {
							_callback.onFailed("数据库中没有数据。");
						}
					}

					@Override
					public void onError(int code, String msg) {
						//_callback.onSuccess(itself); //TODO(binfei):网络数据不能访问的时候暂时提供默认的问题
						_callback.onFailed("访问网络失败。");
					}
				});
			}

		}).start();
	}
}
