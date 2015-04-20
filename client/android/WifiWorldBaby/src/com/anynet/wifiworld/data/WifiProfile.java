package com.anynet.wifiworld.data;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobRealTimeData;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.FindCallback;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.ValueEventListener;

import com.anynet.wifiworld.data.DataListenerHelper.Type;
import com.anynet.wifiworld.util.StringCrypto;

public class WifiProfile extends BmobObject {
	private final static String TAG = WifiProfile.class.getSimpleName();

	private static final long serialVersionUID = 1L;
	public static final String unique_key = "MacAddr";

	public static final String table_name_wifiunregistered = "WifiUnregistered";
	public static final String CryptoKey = "Wifi2Key";// 8bits
	
	public static final String LOGO = "Logo";
	public static final String PASSWORD = "Password";
	public static final String BANNER = "Banner";

	private boolean isShared = false;
	public String MacAddr; // mac地址, 唯一键
	public String Ssid; // wifi的ssid
	public String Password; // wifi的密码，经过base64后保存
	public String Sponser; // 绑定的用户账号，wifi提供者
	public int Type; // wifi的类型
	// public String encryptType; //wifi加密类型
	public byte[] Logo; // 用户自定义的logo信息

	public String Alias; // 用户自定义的wifi别名
	public BmobGeoPoint Geometry; // WiFi的地理位置
	public String ExtAddress;
	public String Banner; // wifi的展示页内容(TODO(binfei)还需要定义更多)
	public float Income; // wifi 收入

	// public float Rating;
	// public int Ranking;
	// public int ConnectedTimes;
	// public int ConnectedDuration;
	// ------------------------------------------------------------------------------------------------
	public static class WifiType {
		public static int WIFI_SUPPLY_BY_UNKNOWN = 0; //未知wifi
		public static int WIFI_SUPPLY_BY_PUBLIC = 1; //公共场所WiFi，如家，kfc
		public static int WIFI_SUPPLY_BY_BUSINESS = 2; //商户提供的WiFi
		public static int WIFI_SUPPLY_BY_HOME = 3; //家庭提供wifi
	}
	
	public WifiProfile() {
	}

	// 支持多个同样字段的表
	public WifiProfile(String tablename) {
		this.setTableName(tablename);
	}

	public String toString() {
		return "Mac:" + MacAddr + " SSID:" + Ssid + " Sponser:" + Sponser + " Type:" + Type + " Alias:" + Alias
				+ " Point(" + Geometry.getLongitude() + "," + Geometry.getLatitude() + ") ExtAddress:" + ExtAddress;
	}

	public String decryptPwd(String pwd) {
		String decryptedStr = null;
		try {
			decryptedStr = StringCrypto.decryptDES(pwd, CryptoKey);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return decryptedStr;
	}

	public void QueryByMacAddress(final Context context, String Mac, DataCallback<WifiProfile> callback) {
		final DataCallback<WifiProfile> _callback = callback;
		final BmobQuery<WifiProfile> query = new BmobQuery<WifiProfile>();
		// query.setCachePolicy(CachePolicy.CACHE_THEN_NETWORK); //
		// 先从缓存获取数据，再拉取网络数据更新
		query.addWhereEqualTo(unique_key, Mac);
		Log.d("findObjects", "开始查询QueryByMacAddress");
		new Thread(new Runnable() {

			@Override
			public void run() {
				query.findObjects(context, new FindListener<WifiProfile>() {
					@Override
					public void onSuccess(List<WifiProfile> object) {
						if (object.size() == 1) {
							WifiProfile returnProfile = object.get(0);
							returnProfile.Password = decryptPwd(returnProfile.Password);
							_callback.onSuccess(returnProfile);
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
	
	public void QueryTagByMacAddr(final Context context, String Mac, String QueryTag, DataCallback<WifiProfile> callback) {
		final DataCallback<WifiProfile> _callback = callback;
		final BmobQuery<WifiProfile> query = new BmobQuery<WifiProfile>();
		// query.setCachePolicy(CachePolicy.CACHE_THEN_NETWORK); //
		// 先从缓存获取数据，再拉取网络数据更新
		query.addQueryKeys("MacAddr" + "," + QueryTag);
		query.addWhereEqualTo(unique_key, Mac);
		Log.d(TAG, "开始查询QueryTagByMacAddr");
		new Thread(new Runnable() {

			@Override
			public void run() {
				query.findObjects(context, new FindListener<WifiProfile>() {
					@Override
					public void onSuccess(List<WifiProfile> object) {
						if (object.size() == 1) {
							WifiProfile returnProfile = object.get(0);
							if (returnProfile.Password != null) {
								returnProfile.Password = decryptPwd(returnProfile.Password);
							}
							_callback.onSuccess(returnProfile);
						} else {
							_callback.onFailed("数据库中没有数据。");
						}
					}

					@Override
					public void onError(int code, String msg) {
						_callback.onFailed(msg);
					}
				});
				Log.d("findObjects", "结束查询QueryTagByMacAddr");
			}

		}).start();
	}

	public void BatchQueryByMacAddress(final Context context, List<String> Macs, MultiDataCallback<WifiProfile> callback) {
		final MultiDataCallback<WifiProfile> _callback = callback;
		final BmobQuery<WifiProfile> query = new BmobQuery<WifiProfile>();
		// query.setCachePolicy(CachePolicy.CACHE_THEN_NETWORK); //
		// 先从缓存获取数据，再拉取网络数据更新
		query.addQueryKeys("MacAddr,Password,Alias");
		query.addWhereContainedIn(unique_key, Macs);
		Log.d("findObjects", "开始查询BatchQueryByMacAddress");
		new Thread(new Runnable() {

			@Override
			public void run() {
				query.findObjects(context, new FindListener<WifiProfile>() {
					@Override
					public void onSuccess(List<WifiProfile> object) {
						if (object.size() >= 1) {
							for (WifiProfile wifiProfile : object) {
								wifiProfile.Password = decryptPwd(wifiProfile.Password);
							}
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
				Log.d("findObjects", "结束查询BatchQueryByMacAddress");
			}

		}).start();
	}

	// 以圆的半径查找
	public void QueryInRadians(final Context context, final BmobGeoPoint center, final double radians,
			MultiDataCallback<WifiProfile> callback) {
		final MultiDataCallback<WifiProfile> _callback = callback;
		new Thread(new Runnable() {

			@Override
			public void run() {
				//先从leancloud上拉取数据
				//GeoSearchByLeanCloud geo = new GeoSearchByLeanCloud("WifiProfile");
				//geo.setGeometry(center.getLatitude(), center.getLongitude());
				//List<String> result = geo.QueryInRadians(radians, 30);
				//if (result == null) {
				//	_callback.onFailed("数据库中没有数据。");
				//	return;
				//}
				
				final BmobQuery<WifiProfile> query = new BmobQuery<WifiProfile>();
				//query.setCachePolicy(CachePolicy.CACHE_THEN_NETWORK); //
				//query.addWhereWithinRadians("Geometry", center, radians);
				//query.addWhereWithinKilometers("Geometry", center, radians);
				query.addWhereWithinMiles("Geometry", center, radians);
				//query.addWhereContainedIn(unique_key, result);
				//query.addWhereNear("Geometry", center);
				query.setLimit(30);// 最多查询到30个，多了用户也会疲劳
				Log.d("findObjects", "开始查询QueryInRadians");

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
				Log.d("findObjects", "结束查询QueryInRadians");
			}

		}).start();
	}

	// 以提供者key来查找
	public void QueryBySponser(final Context context, String sponser, MultiDataCallback<WifiProfile> callback) {
		final MultiDataCallback<WifiProfile> _callback = callback;
		final BmobQuery<WifiProfile> query = new BmobQuery<WifiProfile>();
		// query.setCachePolicy(CachePolicy.CACHE_THEN_NETWORK); //
		// 先从缓存获取数据，再拉取网络数据更新
		query.addWhereEqualTo("Sponser", sponser);
		Log.d("findObjects", "开始查询QueryBySponser");
		new Thread(new Runnable() {

			@Override
			public void run() {
				query.findObjects(context, new FindListener<WifiProfile>() {
					@Override
					public void onSuccess(List<WifiProfile> object) {
						// if (object.size() >= 1) {
						_callback.onSuccess(object);
						// } else {
						// _callback.onFailed("数据库中没有数据。");
						// }
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

		new Thread(new Runnable() {

			@Override
			public void run() {
				// 先保存到leancloud去
				GeoSearchByLeanCloud geo = new GeoSearchByLeanCloud("WifiProfile");
				geo.setKey(MacAddr);
				if (Geometry != null)
					geo.setGeometry(Geometry.getLatitude(), Geometry.getLongitude());
				if (!geo.StoreRemote()) {
					//return;
				}

				// 先查询，如果有数据就更新，否则增加一条新记录
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
						// 只需要第一次保存的时候对密码进行加密
						if (wifi.Password != null) {
							try {
								wifi.Password = StringCrypto.encryptDES(wifi.Password, CryptoKey);
							} catch (Exception e) {
								_callback.onFailed("无法保存数据: " + e.getMessage());
								return;
							}
						}
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

	public void deleteRemote(final Context context) {
		final WifiProfile wifi = this;
		wifi.delete(context);
	}

	public Bitmap getLogo() {
		if (Logo != null)
			return Bytes2Bimap(Logo);
		else
			return null;
	}

	public void setLogo(Bitmap logo) {
		Logo = Bitmap2Bytes(logo);
		// Logo = logo;
	}

	public Bitmap Bytes2Bimap(byte[] b) {
		if (b != null && b.length != 0) {
			return BitmapFactory.decodeByteArray(b, 0, b.length);
		} else {
			return null;
		}
	}

	public byte[] Bitmap2Bytes(Bitmap bm) {
		if (bm == null) {
			return null;
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	public boolean isShared() {
		return isShared;
	}

	public void setShared(boolean shared) {
		isShared = shared;
		// Logo = logo;
	}
	
	//设置监听数据
	public void startListenRowUpdate(final Context context, final String tablename, final String fieldname, 
		final String fieldvalue, final Type type, final DataCallback<WifiProfile> callback) {
		final WifiProfile itslef = this;
		final BmobRealTimeData mBrtd = new BmobRealTimeData();
		mBrtd.start(context, new ValueEventListener() {
		    @Override
		    public void onDataChange(JSONObject data) {
		        Log.e(TAG, "("+data.optString("action")+")"+"数据："+data);
		        
		        itslef.isShared = data.optJSONObject("data").optBoolean("isShared");
		        callback.onSuccess(itslef);
		    }

		    @Override
		    public void onConnectCompleted() {
		        Log.e(TAG, "连接成功:"+mBrtd.isConnected());
		      //先去数据库中查询
				BmobQuery<WifiProfile> query = new BmobQuery<WifiProfile>();
				query.addQueryKeys("objectId");
				query.addWhereEqualTo(fieldname, fieldvalue);
				query.findObjects(context, new FindListener<WifiProfile>() {

					@Override
					public void onError(int arg0, String msg) {
						callback.onFailed("无法查询到要监听的数据：" + msg);
					}

					@Override
					public void onSuccess(List<WifiProfile> objects) {
						if (objects.size() <= 0) {
							callback.onFailed("数据库中没有您要监听的数据");
							Log.d(TAG, "监听失败:数据库没有对应数据");
						} else {
							//查询成功后，设置监听
							for (int i=0; i < objects.size(); ++i) {
								WifiProfile item = objects.get(i);
								String id = item.getObjectId();
								switch(type) {
								case UPDATE:
									mBrtd.subRowUpdate(tablename, id);
									break;
								case DELETE:
									mBrtd.subRowDelete(tablename, id);
								}
								Log.d(TAG, "监听成功:" + id);
							}
						}
					}
					
				});
		    }
		});
	}
}
