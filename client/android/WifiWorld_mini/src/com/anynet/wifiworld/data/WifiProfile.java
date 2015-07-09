/*
 * Copyright 2015 Anynet Corporation All Rights Reserved.
 *
 * The source code contained or described herein and all documents related to
 * the source code ("Material") are owned by Anynet Corporation or its suppliers
 * or licensors. Title to the Material remains with Anynet Corporation or its
 * suppliers and licensors. The Material contains trade secrets and proprietary
 * and confidential information of Anynet or its suppliers and licensors. The
 * Material is protected by worldwide copyright and trade secret laws and
 * treaty provisions.
 * No part of the Material may be used, copied, reproduced, modified, published
 * , uploaded, posted, transmitted, distributed, or disclosed in any way
 * without Anynet's prior express written permission.
 *
 * No license under any patent, copyright, trade secret or other intellectual
 * property right is granted to or conferred upon you by disclosure or delivery
 * of the Materials, either expressly, by implication, inducement, estoppel or
 * otherwise. Any license under such intellectual property rights must be
 * express and approved by Anynet in writing.
 *
 * @brief ANLog is the custom log for wifiworld project.
 * @date 2015-06-04
 * @author Buffer.Li
 *
 */

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

import com.anynet.wifiworld.util.BitmapUtil;
import com.anynet.wifiworld.util.StringCrypto;

public class WifiProfile extends BmobObject{
	public final static String TAG = WifiProfile.class.getSimpleName();

	private static final long serialVersionUID = 1L;
	public static final String unique_key = "MacAddr";

	public static final String table_name_wifiunregistered = "WifiUnregistered";
	public static final String CryptoKey = "Wifi2Key";	//8bits

	public static final String LOGO = "Logo";
	public static final String PASSWORD = "Password";
	public static final String BANNER = "Banner";

	private boolean isShared = false;
	public String MacAddr = ""; 				//Mac地址, 唯一键
	public String Ssid = ""; 				//Wifi的Ssid
	private String Password = ""; 			//Wifi的密码，经过base64后保存
	public String Sponser = ""; 				//绑定的用户账号，Wifi提供者电话号码
	public int Type = 0; 					//Wifi的类型
	private byte[] Logo = null; 				//用户自定义的Logo图片字节流

	public String Alias = ""; 				//用户自定义的Wifi别名、备注名
	public BmobGeoPoint Geometry=null; 		//WiFi的地理位置
	public String ExtAddress = "";
	public String Banner = ""; 				//WiFi的展示页文字签名内容(将来会加入图片展示模块)
	public float Income = 0; 				//WiFi收入

	public static class WifiType {
		public static int WIFI_SUPPLY_BY_UNKNOWN = 0; 	//未知WiFi
		public static int WIFI_SUPPLY_BY_PUBLIC = 1; 	//公共场所WiFi：如家、KFC
		public static int WIFI_SUPPLY_BY_BUSINESS = 2; 	//商户提供的WiFi
		public static int WIFI_SUPPLY_BY_HOME = 3; 		//家庭提供WiFi
	}
	
	public void deleteRemote(final Context context) {
		WifiProfile wifi = this;
		wifi.delete(context);
	}

	public Bitmap getLogo() {
		if (Logo != null)
			return BitmapUtil.Bytes2Bimap(Logo);
		else
			return null;
	}

	public void setLogo(Bitmap logo) {
		Logo = BitmapUtil.Bitmap2Bytes(logo);
	}

	public boolean isShared() {
		return isShared;
	}

	public void setShared(boolean shared) {
		isShared = shared;
	}

	public boolean equale(WifiProfile wp) {
		if (this.isShared == wp.isShared && this.MacAddr.equals(wp.MacAddr) && this.Ssid.equals(wp.Ssid) && this.ExtAddress.equals(wp.ExtAddress)
				&& this.Geometry.equals(wp.Geometry)) {
			return true;
		}
		return false;
	}


	public WifiProfile() {
	}

	// 支持多个同样字段的表
	public WifiProfile(String tablename) {
		this.setTableName(tablename);
	}

	public String toString() {
		return "Mac:" + MacAddr + " SSID:" + Ssid + " Sponser:" + Sponser + " Type:" + Type + " Alias:" + Alias + " Point(" + Geometry.getLongitude()
				+ "," + Geometry.getLatitude() + ") ExtAddress:" + ExtAddress;
	}

	public String getPassword() {
		return decryptPwd(Password);
	}
	
	private String decryptPwd(String pwd) {
		String decryptedStr = null;
		try {
			decryptedStr = StringCrypto.decryptDES(pwd, CryptoKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return decryptedStr;
	}
	
	public void setPassword(String pwd) {
		Password = encryptPwd(pwd);
	}
	
	private String encryptPwd(String pwd) {
		String encryptedStr = null;
		if (pwd != null) {
			try {
				encryptedStr = StringCrypto.encryptDES(pwd, CryptoKey);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return encryptedStr;
	}

	public void QueryByMacAddress(final Context context, final String Mac, DataCallback<WifiProfile> callback) {
		final DataCallback<WifiProfile> _callback = callback;
		final BmobQuery<WifiProfile> query = new BmobQuery<WifiProfile>();
		// query.setCachePolicy(CachePolicy.CACHE_THEN_NETWORK); //
		// 先从缓存获取数据，再拉取网络数据更新
		query.addWhereEqualTo(unique_key, Mac);
		Log.d(TAG, "开始查询QueryByMacAddress:" + Mac);
		new Thread(new Runnable() {

			@Override
			public void run() {
				query.findObjects(context, new FindListener<WifiProfile>() {
					@Override
					public void onSuccess(List<WifiProfile> object) {
						if (object.size() == 1) {
							WifiProfile returnProfile = object.get(0);
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
				Log.d(TAG, "结束查询QueryByMacAddress:" + Mac);
			}

		}).start();
	}

	public void VerfyIsShared(final Context context, final String Mac, DataCallback<Boolean> callback) {
		final DataCallback<Boolean> _callback = callback;
		final BmobQuery<WifiProfile> query = new BmobQuery<WifiProfile>();
		// query.setCachePolicy(CachePolicy.CACHE_THEN_NETWORK); //
		// 先从缓存获取数据，再拉取网络数据更新
		query.addWhereEqualTo(unique_key, Mac);
		Log.d(TAG, "开始查询VerfyIsShared:" + Mac);
		new Thread(new Runnable() {

			@Override
			public void run() {
				query.findObjects(context, new FindListener<WifiProfile>() {
					@Override
					public void onSuccess(List<WifiProfile> object) {
						if (object.size() == 1) {
							_callback.onSuccess(true);
						} else {
							_callback.onSuccess(false);
						}
					}

					@Override
					public void onError(int code, String msg) {
						_callback.onFailed(msg);
					}
				});
				Log.d(TAG, "结束查询VerfyIsShared:" + Mac);
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
		Log.d(TAG, "开始查询QueryTagByMacAddr:" + MacAddr);
		new Thread(new Runnable() {

			@Override
			public void run() {
				query.findObjects(context, new FindListener<WifiProfile>() {
					@Override
					public void onSuccess(List<WifiProfile> object) {
						if (object.size() == 1) {
							WifiProfile returnProfile = object.get(0);
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
				Log.d(TAG, "结束查询QueryTagByMacAddr:" + MacAddr);
			}

		}).start();
	}

	public void BatchQueryByMacAddress(final Context context, final List<String> Macs, boolean allkeys, MultiDataCallback<WifiProfile> callback) {
		final MultiDataCallback<WifiProfile> _callback = callback;
		final BmobQuery<WifiProfile> query = new BmobQuery<WifiProfile>();
		// query.setCachePolicy(CachePolicy.CACHE_THEN_NETWORK); //
		// 先从缓存获取数据，再拉取网络数据更新
		if (!allkeys)
			query.addQueryKeys("MacAddr,Password,Alias,Logo,Banner,Sponser");
		query.addWhereContainedIn(unique_key, Macs);
		Log.d(TAG, "开始查询BatchQueryByMacAddress:" + Macs.size());
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
				Log.d(TAG, "结束查询BatchQueryByMacAddress:" + Macs.size());
			}

		}).start();
	}

	// 以圆的半径查找
	public static void QueryInRadians(final Context context, final BmobGeoPoint center, final double radians, MultiDataCallback<WifiProfile> callback) {
		final MultiDataCallback<WifiProfile> _callback = callback;
		Log.d(TAG, "开始查询QueryInRadians");
		new Thread(new Runnable() {

			@Override
			public void run() {
				
				final BmobQuery<WifiProfile> query = new BmobQuery<WifiProfile>();
				query.addWhereWithinMiles("Geometry", center, radians).setLimit(30);// 最多查询到30个，多了用户也会疲劳
				query.findObjects(context, new FindListener<WifiProfile>() {
					@Override
					public void onSuccess(List<WifiProfile> object) {
						@SuppressWarnings("unused")
						boolean o = (object.size() > 0)?_callback.onSuccess(object):_callback.onFailed("数据库中没有数据。");
					}

					@Override
					public void onError(int code, String msg) {
						_callback.onFailed(msg);
					}
				});
				Log.d(TAG, "结束查询QueryInRadians");
			}

		}).start();
	}

	// 以提供者key来查找
	public void QueryBySponser(final Context context, final String sponser, MultiDataCallback<WifiProfile> callback) {
		final MultiDataCallback<WifiProfile> _callback = callback;
		final BmobQuery<WifiProfile> query = new BmobQuery<WifiProfile>();
		// query.setCachePolicy(CachePolicy.CACHE_THEN_NETWORK); //
		// 先从缓存获取数据，再拉取网络数据更新
		query.addWhereEqualTo("Sponser", sponser);
		Log.d(TAG, "开始查询QueryBySponser:" + sponser);
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
				Log.d(TAG, "结束查询QueryBySponser:" + sponser);
			}

		}).start();
	}

	public void StoreRemote(final Context context, DataCallback<WifiProfile> callback) {
		final DataCallback<WifiProfile> _callback = callback;
		final WifiProfile wifiProfile = this;

		new Thread(new Runnable() {

			@Override
			public void run() {
				// 先保存到leancloud去
				// GeoSearchByLeanCloud geo = new
				// GeoSearchByLeanCloud("WifiProfile");
				// geo.setKey(MacAddr);
				// if (Geometry != null)
				// geo.setGeometry(Geometry.getLatitude(),
				// Geometry.getLongitude());
				// if (!geo.StoreRemote()) {
				// return;
				// }

				// 先查询，如果有数据就更新，否则增加一条新记录
				QueryByMacAddress(context, MacAddr, new DataCallback<WifiProfile>() {

					@Override
					public void onSuccess(final WifiProfile object) {
						wifiProfile.setObjectId(object.getObjectId());
						wifiProfile.update(context, new UpdateListener() {

							@Override
							public void onSuccess() {
								_callback.onSuccess(wifiProfile);
							}

							@Override
							public void onFailure(int arg0, String msg) {
								_callback.onFailed(msg);
							}
						});
					}

					@Override
					public void onFailed(String msg) {
						wifiProfile.save(context, new SaveListener() {

							@Override
							public void onSuccess() {
								_callback.onSuccess(wifiProfile);
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
}
