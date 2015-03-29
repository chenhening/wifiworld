package com.anynet.wifiworld.data;

import java.sql.Time;
import java.util.List;

import android.content.Context;
import android.util.Log;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobQuery.CachePolicy;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

//TODO(binfei)：目前将用户使用wifi信息和wifi被使用信息合并成一个文档便于实现
//由于插入删除频率比较高所以，计划以后将表根据不同类型的用户或者wifi类型来拆分表
public class WifiDynamic extends BmobObject {

	private static final long serialVersionUID = 1L;
	private static final String key_user = "Userid";
	private static final String key_wifi = "MacAddr";
	private static final String key_login = "LoginTime";
	//private static final String key_logout = "LogoutTime";
	
	//半周毫秒数
	private static final long halfweekmillis = (long)(3.5*24*60*60*1000);
	private static final long halfdaymillis = (long)(12*60*60*1000);

	public String Userid; //用户的id即phone number
	public String MacAddr; //所使用网络的mac地址
	public BmobGeoPoint Geometry; //用户使用网络的位置
	public long LoginTime; //用户登陆网络的时间
	public long LogoutTime; //用户退出网络的时间
	
// ------------------------------------------------------------------------------------------------
	public void QueryUserInOneWeek(
		final Context context, long time, MultiDataCallback<WifiDynamic> callback) {
		final MultiDataCallback<WifiDynamic> _callback = callback;
		final BmobQuery<WifiDynamic> query = new BmobQuery<WifiDynamic>();
		query.setCachePolicy(CachePolicy.CACHE_THEN_NETWORK); // 先从缓存获取数据，再拉取网络数据更新
		query.addWhereEqualTo(key_user, Userid);
		query.addWhereGreaterThanOrEqualTo(key_login, time - halfweekmillis);
		query.addWhereLessThan(key_login, time + halfweekmillis);
		Log.d("findObjects", "开始查询QueryUserInOneWeek");
		new Thread(new Runnable() {

			@Override
			public void run() {
				query.findObjects(context, new FindListener<WifiDynamic>() {
					@Override
					public void onSuccess(List<WifiDynamic> objects) {
						_callback.onSuccess(objects);
					}
			
					@Override
					public void onError(int code, String msg) {
						_callback.onFailed(msg);
					}
				});
				Log.d("findObjects", "结束查询QueryUserInOneWeek");
			}
			
		}).start();
	}
	
	public void QueryWiFiInOneWeek(
		final Context context, long time, MultiDataCallback<WifiDynamic> callback) {
		final MultiDataCallback<WifiDynamic> _callback = callback;
		final BmobQuery<WifiDynamic> query = new BmobQuery<WifiDynamic>();
		query.setCachePolicy(CachePolicy.CACHE_THEN_NETWORK); // 先从缓存获取数据，再拉取网络数据更新
		query.addWhereEqualTo(key_wifi, MacAddr);
		query.addWhereGreaterThanOrEqualTo(key_login, time - halfdaymillis);
		query.addWhereLessThan(key_login, time + halfdaymillis);
		Log.d("findObjects", "开始查询QueryWiFiInOneWeek");
		new Thread(new Runnable() {

			@Override
			public void run() {
				query.findObjects(context, new FindListener<WifiDynamic>() {
					@Override
					public void onSuccess(List<WifiDynamic> objects) {
						_callback.onSuccess(objects);
					}
			
					@Override
					public void onError(int code, String msg) {
						_callback.onFailed(msg);
					}
				});
				Log.d("findObjects", "结束查询QueryWiFiInOneWeek");
			}
			
		}).start();
	}
	
	public void QueryWiFiInOneDay(
		final Context context, long time, MultiDataCallback<WifiDynamic> callback) {
		final MultiDataCallback<WifiDynamic> _callback = callback;
		final BmobQuery<WifiDynamic> query = new BmobQuery<WifiDynamic>();
		query.setCachePolicy(CachePolicy.CACHE_THEN_NETWORK); // 先从缓存获取数据，再拉取网络数据更新
		query.addWhereEqualTo(key_wifi, MacAddr);
		query.addWhereGreaterThanOrEqualTo(key_login, time - halfweekmillis);
		query.addWhereLessThan(key_login, time + halfweekmillis);
		Log.d("findObjects", "开始查询QueryWiFiInOneDay");
		new Thread(new Runnable() {

			@Override
			public void run() {
				query.findObjects(context, new FindListener<WifiDynamic>() {
					@Override
					public void onSuccess(List<WifiDynamic> objects) {
						_callback.onSuccess(objects);
					}
			
					@Override
					public void onError(int code, String msg) {
						_callback.onFailed(msg);
					}
				});
				Log.d("findObjects", "结束查询QueryWiFiInOneDay");
			}
			
		}).start();
	}
	
	public void GetConnectedTime(final Context context, String macaddr,
		DataCallback<Long> callback) {
		final DataCallback<Long> _callback = callback;
		final BmobQuery<WifiDynamic> query = new BmobQuery<WifiDynamic>();
		query.addWhereEqualTo("MacAddr", macaddr);
		query.count(context, WifiDynamic.class, new CountListener() {

			@Override
            public void onFailure(int arg0, String msg) {
				_callback.onFailed("查询失败： " + msg);
            }

			@Override
            public void onSuccess(int arg0) {
				_callback.onSuccess((long) arg0);
            }
			
		});
	}
	
	public void StoreRemote(final Context context, DataCallback<WifiDynamic> callback) {
		final DataCallback<WifiDynamic> _callback = callback;
		final WifiDynamic user = this;
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
	
	public void MarkLoginTime() {
		LoginTime = System.currentTimeMillis();
	}
	
	public void MarkLogoutTime() {
		LogoutTime = System.currentTimeMillis();
	}
}
