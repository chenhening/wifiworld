package com.anynet.wifiworld.data;

import java.util.List;

import android.content.Context;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class UserProfile extends BmobObject {

	private static final long serialVersionUID = 1L;
	
	//public String Userid; //用户账号
	public String PhoneNumber; //用户手机号，作为表的唯一建
	public String Password; //密码
	public UserType Type; //用户类型，
	public float Wallet; //用户钱包
	
	private String unique_key = "PhoneNumber";
    private Context globalContext = null;
    private UserProfile itself = this;
	
	// ------------------------------------------------------------------------------------------------
    public void init(Context context) {
    	this.globalContext = context;
    }
    
	public void QueryByPhoneNumber(String number, DataCallback<UserProfile> callback) {
		final DataCallback<UserProfile> _callback = callback;
		BmobQuery<UserProfile> query = new BmobQuery<UserProfile>();
		query.addWhereEqualTo(unique_key, number);
		query.findObjects(globalContext, new FindListener<UserProfile>() {
			@Override
			public void onSuccess(List<UserProfile> object) {
				// TODO Auto-generated method stub
				if (object.size() == 1) {
					itself = object.get(0);
					_callback.onSuccess(itself);
				} else {
					_callback.onFailed("");
				}
			}

			@Override
			public void onError(int code, String msg) {
				_callback.onFailed(msg);
			}
		});
	}
	
	public void StoreRemote(DataCallback<UserProfile> callback) {
		final DataCallback<UserProfile> _callback = callback;
		//先查询，如果有数据就更新，否则增加一条新记录
		QueryByPhoneNumber(itself.PhoneNumber, new DataCallback<UserProfile>() {

			@Override
			public void onSuccess(UserProfile object) {
				object.update(globalContext, new UpdateListener() {

					@Override
					public void onSuccess() {
						_callback.onSuccess(itself);
					}
					
					@Override
					public void onFailure(int arg0, String msg) {
						_callback.onFailed(msg);
					}
				});
			}

			@Override
			public void onFailed(String msg) {
				_callback.onFailed(msg);
				itself.save(globalContext, new SaveListener() {

					@Override
					public void onSuccess() {
						_callback.onSuccess(itself);
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
