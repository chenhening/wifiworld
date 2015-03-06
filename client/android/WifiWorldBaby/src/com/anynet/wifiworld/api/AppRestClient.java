package com.anynet.wifiworld.api;

import android.R.integer;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.anynet.wifiworld.api.callback.AppHttpHandler;
import com.anynet.wifiworld.api.callback.ResponseCallback;
import com.anynet.wifiworld.app.WifiWorldApplication;
import com.anynet.wifiworld.bean.*;
import com.anynet.wifiworld.util.AppInfoUtil;
import com.anynet.wifiworld.util.NetHelper;
import com.anynet.wifiworld.util.PackageSignHelper;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

public class AppRestClient {
	public static boolean TEST = true;
	private static boolean isReal = true;
	private static boolean isToastNetworkDisconnect = false;
	// private static final String TEST_URL = "http://test.red.local/index.php";

	private static final String TEST_URL = "http://10.10.159.33/index.php";

	private static final String BASE_URL = "https://red.xunlei.com/";

	// private static final String BASE_URL =
	// "https://61.155.184.110/index.php";

	private static AsyncHttpClient client = new AsyncHttpClient();

	// 增加cookie
	static {
		client.allowRetryExceptionClass(SocketTimeoutException.class);
		client.setTimeout(25000); // 设置超时时间
		//client.setMaxRetriesAndTimeout(5, 1000);
	}

	public static void addAuth() {
		client.addHeader("Cookie", getCookie());
	}

	public static void get(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		// AsyncHttpClient client = new AsyncHttpClient();
		Log.e("Crystal", "get url:" + url);
		//client.get(getAbsoluteUrl(url), params, responseHandler);

	}

	public static void post(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		//client.post(getAbsoluteUrl(url), params, responseHandler);
	}

	
	
	
	
	
	
	
	
	public static String getCookie() {
		int userId = 0;

		String sessionId = null;

//		XLUserInfo userInfo = XLUserUtil.getInstance().getCurrentUser();
//		String nickname = null;
//
//		if (userInfo != null) {
//
//			userId = userInfo.getIntValue(USERINFOKEY.UserID);
//
//			sessionId = userInfo.getStringValue(USERINFOKEY.SessionID); // session
//																		// id
//
//			nickname = userInfo.getStringValue(USERINFOKEY.NickName);
//			if (TextUtils.isEmpty(nickname)) {
//				nickname = userInfo.getStringValue(USERINFOKEY.UserName);
//			}
//			nickname = URLEncoder.encode(nickname);
//		}
//		// origin 1-android 2-ios
//		String authFmt = "sessionid=%1$s; userid=%2$s; origin=1;nickname=%3$s";
//		String auth = String.format(authFmt, String.valueOf(sessionId),
//				String.valueOf(userId), String.valueOf(nickname));
		return null;
	}

	/**
	 * 8.1.1.1 矿机摘要
	 * 
	 * @param paramResponseCallback
	 */
	public static void crystalIncomeSummary(
			ResponseCallback<CrystalIncomeSummaryResp> paramResponseCallback) {
		addPublic();
		RequestParams params = new RequestParams();

		params.put("r", "mine/summary");

		params.put("addgft", 1);

		AppRestClient.post("mine_summary.json", params,
				new AppHttpHandler<CrystalIncomeSummaryResp>(
						paramResponseCallback) {
				});

	}

	/**
	 * 8.1.2.6 新首页的信息
	 * 
	 * @param paramResponseCallback
	 */
	public static void crystalMineInfo(
			ResponseCallback<CrystalMineInfoResp> paramResponseCallback) {
		addPublic();
		RequestParams params = new RequestParams();

		params.put("r", "mine/info");

		AppRestClient.post("mine_summary.json", params,
				new AppHttpHandler<CrystalMineInfoResp>(paramResponseCallback) {
				});

	}

	/**
	 * 8.1.1.3 过去24小时的速度统计
	 * 
	 * @param paramResponseCallback
	 */
	public static void lastSpeedStat(
			ResponseCallback<LastSpeedStatResp> paramResponseCallback) {
		addPublic();
		RequestParams params = new RequestParams();
		params.put("r", "mine/speed_stat");

		AppRestClient.post("last_speed_stat.json", params,
				new AppHttpHandler<LastSpeedStatResp>(paramResponseCallback) {
				});

	}

	/**
	 * 权限判断
	 * 
	 * @param paramResponseCallback
	 */
	public static void privilege(
			ResponseCallback<PriviledgeResp> paramResponseCallback) {
		addPublic();
		RequestParams params = new RequestParams();
		params.put("r", "usr/privilege");

		AppRestClient.post("privilege.json", params,
				new AppHttpHandler<PriviledgeResp>(paramResponseCallback) {
				});

	}

	/**
	 * 获取用户设备状态
	 * 
	 * @param paramResponseCallback
	 */
	public static void getDeviceStat(
			ResponseCallback<DeviceStatResp> paramResponseCallback) {
		addPublic();
		RequestParams params = new RequestParams();
		params.put("r", "mine/devices_stat");
		params.put("total", "1");

		AppRestClient.post("device_stat.json", params,
				new AppHttpHandler<DeviceStatResp>(paramResponseCallback) {
				});

	}

	/**
	 * 获取用户设备状态(多设备)
	 * 
	 * @param paramResponseCallback
	 * @paramparam statFlag 是否查询统计数据
	 */
	public static void getDevicesStat(
			ResponseCallback<DevicesStatResp> paramResponseCallback,
			boolean statFlag) {
		addPublic();
		RequestParams params = new RequestParams();
		params.put("r", "mine/devices_stat");
		if (statFlag) {
			params.put("total", "1");
		}
		AppRestClient.post("devices_stat.json", params,
				new AppHttpHandler<DevicesStatResp>(paramResponseCallback) {
				});

	}

	/**
	 * 获取系统消息
	 * 
	 * @param maxServId
	 *            最大页数
	 * @param page
	 *            当前页
	 * @param pageSize
	 *            每页条目数
	 * @param paramResponseCallback
	 */
	public static void getSystemMsg(long maxServId, int page, int pageSize,
			ResponseCallback<SystemMsgResp> paramResponseCallback) {
		addPublic();
		RequestParams params = new RequestParams();
		params.put("v", 1);
		params.put("id", maxServId);
		params.put("p", page);
		params.put("ps", pageSize);
		params.put("r", "sys/msg");
		AppRestClient.post("get_system_msg.json", params,
				new AppHttpHandler<SystemMsgResp>(paramResponseCallback) {
				});

	}

	/**
	 * 获取个人财产
	 * 
	 * @param paramResponseCallback
	 */
	public static void getMyAsset(
			ResponseCallback<MyAssetResp> paramResponseCallback) {
		addPublic();
		RequestParams params = new RequestParams();
		params.put("r", "usr/asset");

		AppRestClient.post("asset.json", params,
				new AppHttpHandler<MyAssetResp>(paramResponseCallback) {
				});

	}

	public static void collectCrystal(
			ResponseCallback<CommonResp> paramResponseCallback) {
		addPublic();
		RequestParams params = new RequestParams();
		params.put("r", "mine/collect");
		AppRestClient.post("collect_redcrystal.json", params,
				new AppHttpHandler<CommonResp>(paramResponseCallback) {
				});
	}

	public static void getPkg(
			ResponseCallback<GetPkgResp> paramResponseCallback, String v) {
		addPublic();
		RequestParams params = new RequestParams();
		params.put("r", "usr/drawpkg");
		params.put("v", v);
		AppRestClient.post("drawpkg.json", params,
				new AppHttpHandler<GetPkgResp>(paramResponseCallback) {
				});
	}

	public static void loadCrystalRecord(int filter, int pageIndex,
			int pageSize, ResponseCallback<CrystalRecordResp> callback) {
		addPublic();
		RequestParams params = new RequestParams();
		params.put("tp", filter);
		params.put("p", pageIndex);
		params.put("ps", pageSize);
		params.put("r", "usr/assetio");
		AppRestClient.post("drawpkg.json", params,
				new AppHttpHandler<CrystalRecordResp>(callback) {
				});
	}

	public static void getBroadCast(int id,
			ResponseCallback<SystemMsgResp> callback) {
		addPublic();
		RequestParams params = new RequestParams();
		params.put("id", id);
		params.put("r", "sys/brc");
		AppRestClient.post("brc.json", params,
				new AppHttpHandler<SystemMsgResp>(callback) {
				});
	}

	public static void reportStat(int actionid,
			ResponseCallback<ReportStatResp> callback) {
		addPublic();
		RequestParams params = new RequestParams();
		params.put("actid", actionid);
		params.put("r", "usr/hand");
		AppRestClient.get("hand.json", params,
				new AppHttpHandler<ReportStatResp>(callback) {
				});
	}

	public static void reportMineActivity(long stayTime, long delayTime,
			ResponseCallback<MineActivityResp> callback) {
		addPublic();
		RequestParams params = new RequestParams();
		params.put("v", 1);
		params.put("r", "usr/reportPlayTime");
		params.put("t", stayTime);
		params.put("dl", delayTime);
		AppRestClient.post("reportPlayTime.json", params,
				new AppHttpHandler<MineActivityResp>(callback) {
				});

	}

	/**
	 * 获取全局配置数组
	 * 
	 * @param paramResponseCallback
	 */
	public static void getAppConfig(
			ResponseCallback<AppConfigResp> paramResponseCallback) {
		addPublic();
		RequestParams params = new RequestParams();
		params.put("r", "sys/config");

		AppRestClient.post("get_config.json", params,
				new AppHttpHandler<AppConfigResp>(paramResponseCallback) {
				});
	}

	/**
	 * 上报device_token
	 * 
	 * @param paramResponseCallback
	 */
	public static void reportDeviceToken(int version, int type,
			String appVersion, String deviceToken,
			ResponseCallback<DeviceTokenResp> paramResponseCallback) {
		addPublic();
		RequestParams params = new RequestParams();
		params.put("v", version);
		params.put("c", type);
		params.put("dtn", deviceToken);
		params.put("appv", appVersion);
		params.put("r", "usr/reportDevice");

		AppRestClient.post("report_device_token.json", params,
				new AppHttpHandler<DeviceTokenResp>(paramResponseCallback) {
				});
	}

	public static void removeDeviceToken(int version, String userid,
			ResponseCallback<DeviceTokenResp> paramResponseCallback) {
		addPublic();
		RequestParams params = new RequestParams();
		params.put("v", version);
		params.put("userid", userid);
		params.put("r", "usr/removeToken");

		AppRestClient.post("remove_device_token.json", params,
				new AppHttpHandler<DeviceTokenResp>(paramResponseCallback) {
				});
	}

	private static void addPublic() {

		if (!NetHelper.isNetworkAvailable(WifiWorldApplication.getInstance())) {
			/** 增加个开关，防止toast一直弹 */
			if (isToastNetworkDisconnect == false) {
				Toast.makeText(WifiWorldApplication.getInstance(), "网络已经断开",
						Toast.LENGTH_SHORT).show();
			}
			isToastNetworkDisconnect = true;

			return;
		} else {
			isToastNetworkDisconnect = false;
		}

		addAuth();

	}

	public static String getAbsoluteUrl(String relativeUrl) {

		if (PackageSignHelper.isRelease(WifiWorldApplication.getInstance())) {
			return BASE_URL;
		} else {
			return TEST_URL;

			// return BASE_URL;
		}

	}

}