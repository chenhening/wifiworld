package com.anynet.wifiworld.data;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class WifiProfile extends BmobObject{

	private static final long serialVersionUID = 1L;
	private static final String unique_key = "MacAddr";

	public String MacAddr; //mac地址, 唯一键
	public String Ssid; //wifi的ssid
	public String Password; //wifi的密码，经过base64后保存
	public String Sponser; //绑定的用户账号，wifi提供者
	public int Type; //wifi的类型
	public String encryptType; //wifi加密类型
	public Bitmap Logo; //用户自定义的logo信息
	public String Alias; //用户自定义的wifi别名
	public BmobGeoPoint Geometry; //WiFi的地理位置
	public String ExtAddress;
	public String Banner; //wifi的展示页内容(TODO(binfei)还需要定义更多)
	public float Income; //wifi 收入

	public static final String CryptoKey = "Wifi2Key";//8bits
// ------------------------------------------------------------------------------------------------
	public void QueryByMacAddress(
		final Context context, String Mac, DataCallback<WifiProfile> callback) {
		final DataCallback<WifiProfile> _callback = callback;
		BmobQuery<WifiProfile> query = new BmobQuery<WifiProfile>();
		query.addWhereEqualTo(unique_key, Mac);
		Log.d("findObjects", "开始查询QueryByMacAddress");
		query.findObjects(context, new FindListener<WifiProfile>() {
			@Override
			public void onSuccess(List<WifiProfile> object) {
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
		Log.d("findObjects", "结束查询QueryByMacAddress");
	}
	
	public void StoreRemote(final Context context, DataCallback<WifiProfile> callback) {
		final DataCallback<WifiProfile> _callback = callback;
		final WifiProfile wifi = this;
		//先查询，如果有数据就更新，否则增加一条新记录
		QueryByMacAddress(context, MacAddr, new DataCallback<WifiProfile>() {

			@Override
			public void onSuccess(final WifiProfile object) {
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
}
