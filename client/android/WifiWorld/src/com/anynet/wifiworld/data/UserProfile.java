package com.anynet.wifiworld.data;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.CloudCodeListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import com.alibaba.fastjson.JSON;
import com.anynet.wifiworld.util.StringCrypto;

public class UserProfile extends BmobUser {

	private static final long serialVersionUID = 1L;
	private static final String unique_key = "username";

	public static final String CryptoKey = "userprof";// 8bits

	// public String Userid; //用户账号
	//public String PhoneNumber; // 用户手机号，作为表的唯一建
	//public String Password; // 密码
	public int Type; // 用户类型，
	public float Wallet; // 用户钱包
	public String NickName; //昵称
	//public String Email; //用于找回账号的邮箱
	public String CustomPwd; //用于固定登录的密码
	private int Sex; //性别，男女
	public String Age; //年龄，用滴答数表示
	public String Job; //职业
	public String Interest; //兴趣
	public byte[] Avatar; //头像 

	public static final String[] SexArray = {"女的","男的","弯的","奇怪的"};
	
	public static class UserType {
		public static int USER_UNKNOWN = 0; //未知用户
		public static int USER_FREE = 1; //免费使用网络的用户
		public static int USER_VIP = 2; //付费使用高质量服务的用户
	}
	
	public String getSex() {
		String r = SexArray[0];
		if(Sex<=3 && Sex >=0){
			r = SexArray[Sex];
		}
		return r;
	}

	public void setSex(String sex) {
		int r = 0;
		if(sex.equals(SexArray[0])){
			r = 0;
		}else if(sex.equals(SexArray[1])){
			r = 1;
		}else if(sex.equals(SexArray[2])){
			r = 2;
		}else{
			r = 3;
		}
		Sex = r;
	}

	// ------------------------------------------------------------------------------------------------
	public void QueryByPhoneNumber(
		final Context context, String number, DataCallback<UserProfile> callback) {
		final DataCallback<UserProfile> _callback = callback;
		
		//测试网络是否在wifi下，否则失败
		//if (!NetHelper.isWifiNet(context)) {
		//	callback.onFailed("当前网络不在wifi环境下，请使用wifi。");
		//	return;
		//}
		
		final BmobQuery<UserProfile> query = new BmobQuery<UserProfile>();
		query.addWhereEqualTo(unique_key, number);
		Log.d("findObjects", "开始查询QueryByPhoneNumber");
		new Thread(new Runnable() {

			@Override
			public void run() {
				query.findObjects(context, new FindListener<UserProfile>() {
					@Override
					public void onSuccess(List<UserProfile> object) {
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
		
		}).start();
	}
	
	//注册用户
	public void signUp(final Context context, DataCallback<UserProfile> callback) {
		//测试网络是否在wifi下，否则失败
		//if (!NetHelper.isWifiNet(context)) {
		//	callback.onFailed("当前网络不在wifi环境下，请使用wifi。");
		//	return;
		//}
		
		final DataCallback<UserProfile> _callback = callback;
		final UserProfile user = this;
		//对密码进行加密
		try {
			user.setPassword(StringCrypto.encryptDES(user.getPassword(), CryptoKey));
		} catch (Exception e) {
			_callback.onFailed("对密码加密失败: " + e.getMessage());
			return;
		}
		user.signUp(context, new SaveListener() {

			@Override
            public void onFailure(int code, String msg) {
				//很可能是存在账号，那么更新密码
				if (code == 202) {
					//调用云端代码
					AsyncCustomEndpoints ace = new AsyncCustomEndpoints();
					JSONObject para = new JSONObject();
					try {
	                    para.put("username", user.getUsername());
	                    para.put("password", user.getPassword());
                    } catch (JSONException e) {
                    	_callback.onFailed("注册账号失败." + e.getMessage());
                    	return;
                    }
					ace.callEndpoint(context, "resetPassword", para, new CloudCodeListener() {

						@Override
                        public void onFailure(int arg0, String msg) {
							_callback.onFailed("注册账号失败." + msg);
                        }

						@Override
                        public void onSuccess(Object arg0) {
							user.QueryByPhoneNumber(context, user.getUsername(), new DataCallback<UserProfile>() {

								@Override
								public void onSuccess(UserProfile object) {
									object.setUsername(user.getUsername());
									object.setPassword(user.getPassword());
									object.login(context, _callback);
								}

								@Override
								public void onFailed(String msg) {
									_callback.onFailed("注册账号失败." + msg);
								}
								
							});
                        }
					});
				} else {
					_callback.onFailed("注册账号失败." + msg);
				}
            }

			@Override
            public void onSuccess() {
				_callback.onSuccess(user);
            }
			
		});
	}
	
	//登录用户
	public void login(final Context context, DataCallback<UserProfile> callback) {
		//测试网络是否在wifi下，否则失败
		//if (!NetHelper.isWifiNet(context)) {
		//	callback.onFailed("当前网络不在wifi环境下，请使用wifi。");
		//	return;
		//}
				
		final DataCallback<UserProfile> _callback = callback;
		final UserProfile user = this;
		user.login(context, new SaveListener() {

			@Override
            public void onFailure(int arg0, String msg) {
				_callback.onFailed("登陆账号失败：" + msg);
            }

			@Override
            public void onSuccess() {
				_callback.onSuccess(user);
            }
			
		});
	}
	
	//更新用户
	public void update(final Context context, DataCallback<UserProfile> callback) {
		//测试网络是否在wifi下，否则失败
		//if (!NetHelper.isWifiNet(context)) {
		//	callback.onFailed("当前网络不在wifi环境下，请使用wifi。");
		//	return;
		//}
		
		final DataCallback<UserProfile> _callback = callback;
		final UserProfile user = this;
		//对密码进行加密
		String Password = user.getPassword();
		if (Password != null && Password.equals("")) {
			try {
				user.setPassword(StringCrypto.encryptDES(Password, CryptoKey));
			} catch (Exception e) {
				_callback.onFailed("无法保存数据: " + e.getMessage());
				return;
			}
		}
		
		user.update(context, new UpdateListener() {

			@Override
            public void onFailure(int arg0, String msg) {
				_callback.onFailed(msg);
            }

			@Override
            public void onSuccess() {
				_callback.onSuccess(user);
            }
			
		});
	}
	
	//退出登陆
	public void logout(Context context) {
		logOut(context);   //清除缓存用户对象
	}
	
	/*public void StoreRemote(final Context context, DataCallback<UserProfile> callback) {
		//测试网络是否在wifi下，否则失败
		if (!NetHelper.isWifiNet(context)) {
			callback.onFailed("当前网络不在wifi环境下，请使用wifi。");
			return;
		}
		
		final DataCallback<UserProfile> _callback = callback;
		final UserProfile user = this;
		//对密码进行加密
		try {
			user.Password = StringCrypto.encryptDES(Password, CryptoKey);
		} catch (Exception e) {
			_callback.onFailed("无法保存数据: " + e.getMessage());
			return;
		}
		
		//先查询，如果有数据就更新，否则增加一条新记录
		QueryByPhoneNumber(context, user.PhoneNumber, new DataCallback<UserProfile>() {

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
	}*/
}
