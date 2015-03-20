package com.anynet.wifiworld.data;

import java.util.List;

import android.content.Context;
import android.util.Log;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class UserProfile extends BmobObject {

	private static final long serialVersionUID = 1L;
	private static final String unique_key = "PhoneNumber";
	
	//public String Userid; //用户账号
	public String PhoneNumber; //用户手机号，作为表的唯一建
	public String Password; //密码
	public int Type; //用户类型，
	public float Wallet; //用户钱包
	
// ------------------------------------------------------------------------------------------------
	public void QueryByPhoneNumber(
		final Context context, String number, DataCallback<UserProfile> callback) {
		final DataCallback<UserProfile> _callback = callback;
		BmobQuery<UserProfile> query = new BmobQuery<UserProfile>();
		query.addWhereEqualTo(unique_key, number);
		Log.d("findObjects", "开始查询QueryByPhoneNumber");
		query.findObjects(context, new FindListener<UserProfile>() {
			@Override
			public void onSuccess(List<UserProfile> object) {
				// TODO Auto-generated method stub
				if (object.size() == 1) {
					_callback.onSuccess(object.get(0));
				} else {
					_callback.onFailed("此用户不存在。");
				}
			}

			@Override
			public void onError(int code, String msg) {
				_callback.onFailed(msg);
			}
		});
		Log.d("findObjects", "结束查询QueryByPhoneNumber");
	}
	
	public void StoreRemote(final Context context, DataCallback<UserProfile> callback) {
		final DataCallback<UserProfile> _callback = callback;
		final UserProfile user = this;
		//先查询，如果有数据就更新，否则增加一条新记录
		QueryByPhoneNumber(context, PhoneNumber, new DataCallback<UserProfile>() {

			@Override
			public void onSuccess(final UserProfile object) {
				user.setObjectId(object.getObjectId());
				user.update(context, new UpdateListener() {

					@Override
					public void onSuccess() {
						_callback.onSuccess(user);
					}
					
					@Override
					public void onFailure(int arg0, String msg) {
						_callback.onFailed(msg);
					}
				});
			}

			@Override
			public void onFailed(String msg) {
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
		});
	}
}
