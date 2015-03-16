package com.anynet.wifiworld.data;

import java.sql.Time;
import java.util.List;

import android.content.Context;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

//TODO(binfei)：目前将用户使用wifi信息和wifi被使用信息合并成一个文档便于实现
//由于插入删除频率比较高所以，计划以后将表根据不同类型的用户或者wifi类型来拆分表
public class UserDynamic extends BmobObject {

	private static final long serialVersionUID = 1L;
	private static final String key_user = "Userid";
	private static final String key_wifi = "MacAddr";
	private static final String key_login = "LoginTime";
	//private static final String key_logout = "LogoutTime";
	
	//半周毫秒数
	private static final long halfweekmillis = (long) (3.5*24*60*60*1000);

	public String Userid; //用户的id即phone number
	public String MacAddr; //所使用网络的mac地址
	public BmobGeoPoint Geometry; //用户使用网络的位置
	public long LoginTime; //用户登陆网络的时间
	public long LogoutTime; //用户退出网络的时间
	
// ------------------------------------------------------------------------------------------------
	public void QueryUserInOneWeek(
		final Context context, long time, MultiDataCallback<UserDynamic> callback) {
		final MultiDataCallback<UserDynamic> _callback = callback;
		BmobQuery<UserDynamic> query = new BmobQuery<UserDynamic>();
		query.addWhereEqualTo(key_user, Userid);
		query.addWhereGreaterThanOrEqualTo(key_login, time - halfweekmillis);
		query.addWhereLessThan(key_login, time + halfweekmillis);
		query.findObjects(context, new FindListener<UserDynamic>() {
			@Override
			public void onSuccess(List<UserDynamic> objects) {
				_callback.onSuccess(objects);
			}
	
			@Override
			public void onError(int code, String msg) {
				_callback.onFailed(msg);
			}
		});
	}
	
	public void QueryWiFiInOneWeek(
		final Context context, long time, MultiDataCallback<UserDynamic> callback) {
		final MultiDataCallback<UserDynamic> _callback = callback;
		BmobQuery<UserDynamic> query = new BmobQuery<UserDynamic>();
		query.addWhereEqualTo(key_wifi, MacAddr);
		query.addWhereGreaterThanOrEqualTo(key_login, time - halfweekmillis);
		query.addWhereLessThan(key_login, time + halfweekmillis);
		query.findObjects(context, new FindListener<UserDynamic>() {
			@Override
			public void onSuccess(List<UserDynamic> objects) {
				_callback.onSuccess(objects);
			}
	
			@Override
			public void onError(int code, String msg) {
				_callback.onFailed(msg);
			}
		});
	}
	
	public void StoreRemote(final Context context, DataCallback<UserDynamic> callback) {
		final DataCallback<UserDynamic> _callback = callback;
		final UserDynamic user = this;
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
