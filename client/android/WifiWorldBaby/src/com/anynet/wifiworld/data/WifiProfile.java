package com.anynet.wifiworld.data;

import java.util.List;

import com.anynet.wifiworld.util.StringCrypto;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobQuery.CachePolicy;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class WifiProfile extends BmobObject{

	private static final long serialVersionUID = 1L;
	private static final String unique_key = "MacAddr";
	
	public static final String table_name_wifiunregistered = "WifiUnregistered";
	public static final String CryptoKey = "Wifi2Key";//8bits

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
// ------------------------------------------------------------------------------------------------
	public WifiProfile() {
	}
	
	//支持多个同样字段的表
	public WifiProfile(String tablename) {
		this.setTableName(tablename);
	}
	
	public void QueryByMacAddress(
		final Context context, String Mac, DataCallback<WifiProfile> callback) {
		final DataCallback<WifiProfile> _callback = callback;
		final BmobQuery<WifiProfile> query = new BmobQuery<WifiProfile>();
		query.setCachePolicy(CachePolicy.CACHE_THEN_NETWORK); // 先从缓存获取数据，再拉取网络数据更新
		query.addWhereEqualTo(unique_key, Mac);
		Log.d("findObjects", "开始查询QueryByMacAddress");
		new Thread(new Runnable() {

			@Override
			public void run() {
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
			
		}).start();
	}
	
	//以圆的半径查找
	public void QueryInRadians(final Context context, BmobGeoPoint center, int radians, 
		MultiDataCallback<WifiProfile> callback) {
		final MultiDataCallback<WifiProfile> _callback = callback;
		final BmobQuery<WifiProfile> query = new BmobQuery<WifiProfile>();
		query.setCachePolicy(CachePolicy.CACHE_THEN_NETWORK); // 先从缓存获取数据，再拉取网络数据更新
		query.addWhereWithinRadians("Geometry", center, radians);
		Log.d("findObjects", "开始查询QueryInRadians");
		new Thread(new Runnable() {

			@Override
			public void run() {
				query.findObjects(context, new FindListener<WifiProfile>() {
					@Override
					public void onSuccess(List<WifiProfile> object) {
						if (object.size() == 1) {
							_callback.onSuccess(object);
						} else {
							_callback.onFailed("数据库中没有数据。");
						}
					}

					@Override
					public void onError(int code, String msg) {
						_callback.onFailed(msg);
					}
				});
				Log.d("findObjects", "结束查询QueryInRadians");
			}
			
		}).start();
	}
	
	//以提供者key来查找
	public void QueryBySponser(final Context context, String sponser, 
		MultiDataCallback<WifiProfile> callback) {
		final MultiDataCallback<WifiProfile> _callback = callback;
		final BmobQuery<WifiProfile> query = new BmobQuery<WifiProfile>();
		query.setCachePolicy(CachePolicy.CACHE_THEN_NETWORK); // 先从缓存获取数据，再拉取网络数据更新
		query.addWhereEqualTo("Sponser", sponser);
		Log.d("findObjects", "开始查询QueryBySponser");
		new Thread(new Runnable() {

			@Override
			public void run() {
				query.findObjects(context, new FindListener<WifiProfile>() {
					@Override
					public void onSuccess(List<WifiProfile> object) {
						if (object.size() >= 1) {
							_callback.onSuccess(object);
						} else {
							_callback.onFailed("数据库中没有数据。");
						}
					}

					@Override
					public void onError(int code, String msg) {
						_callback.onFailed(msg);
					}
				});
				Log.d("findObjects", "结束查询QueryBySponser");
			}
			
		}).start();
	}
	
	public void StoreRemote(final Context context, DataCallback<WifiProfile> callback) {
		final DataCallback<WifiProfile> _callback = callback;
		final WifiProfile wifi = this;
		//对密码进行加密
		try {
			wifi.Password = StringCrypto.encryptDES(wifi.Password, CryptoKey);
		} catch (Exception e) {
			_callback.onFailed("无法保存数据: " + e.getMessage());
			return;
		}
		
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
