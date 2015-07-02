package com.anynet.wifiworld.data;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class WifiQuestions extends BmobObject {

	private static final long serialVersionUID = 1L;
	
	public String MacAddr;
	public List<ArrayList<String>> Question = new ArrayList<ArrayList<String>>();
	
	public WifiQuestions() {
		//设置默认值
		Question.add(new ArrayList<String>());
		Question.get(0).add("今年过节不收礼啊，收礼只收脑白金。这句广告词出自那款品牌产品？");
		Question.get(0).add("脑白金");
		Question.get(0).add("九位地黄丸");
		Question.get(0).add("王老吉");
		Question.get(0).add("完达山加大数控刀具");
		Question.add(new ArrayList<String>());
		Question.get(1).add("神州行，我看行！这句广告词出自哪款品牌产品");
		Question.get(1).add("神州行电话卡");
		Question.get(1).add("神州旅行社");
		Question.get(1).add("神州笔记本");
		Question.get(1).add("小米手机");
		Question.add(new ArrayList<String>());
		Question.get(2).add("阿迪达斯的广告词是什么？");
		Question.get(2).add("Impossible is nothing");
		Question.get(2).add("Everything is possible.");
		Question.get(2).add("Just do it.");
		Question.get(2).add("Hi,Man");
	}
	
	public void StoreRemote(final Context context, DataCallback<WifiQuestions> callback) {
		final DataCallback<WifiQuestions> _callback = callback;
		final WifiQuestions wifi = this;

		new Thread(new Runnable() {

			@Override
			public void run() {

				// 先查询，如果有数据就更新，否则增加一条新记录
				QueryByMacAddress(context, MacAddr, new DataCallback<WifiQuestions>() {

					@Override
					public void onSuccess(final WifiQuestions object) {
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
	
	public void QueryByMacAddress(final Context context, final String Mac, DataCallback<WifiQuestions> callback) {
		final DataCallback<WifiQuestions> _callback = callback;
		final WifiQuestions itself = this;
		final BmobQuery<WifiQuestions> query = new BmobQuery<WifiQuestions>();
		query.addWhereEqualTo("MacAddr", Mac);
		itself.MacAddr = Mac;
		new Thread(new Runnable() {

			@Override
			public void run() {
				query.findObjects(context, new FindListener<WifiQuestions>() {
					@Override
					public void onSuccess(List<WifiQuestions> object) {
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
