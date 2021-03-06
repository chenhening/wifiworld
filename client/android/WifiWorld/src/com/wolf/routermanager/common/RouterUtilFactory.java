package com.wolf.routermanager.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.wolf.routermanager.bean.AllRouterInfoBean;
import com.wolf.routermanager.common.constant.RouterDevicesConstant;
import com.wolf.routermanager.common.constant.SharepreferConstant;
import com.wolf.routermanager.http.*;
import com.wolf.routermanager.http.inter.RouterUtilInterface;
import com.wolf.routermanager.urlconstant.RouterASUSConstant;
import com.wolf.routermanager.urlconstant.RouterBilianBL410RConstant;
import com.wolf.routermanager.urlconstant.RouterDlinkConstant_612;
import com.wolf.routermanager.urlconstant.RouterNetCoreConstant;
import com.wolf.routermanager.urlconstant.RouterNewifiConstant;
import com.wolf.routermanager.urlconstant.RouterPolarConstant;
import com.wolf.routermanager.urlconstant.RouterStarWiFiConstant;
import com.wolf.routermanager.urlconstant.RouterTPLINKConstant;
import com.wolf.routermanager.urlconstant.RouterTenDaConstant;
import com.wolf.routermanager.urlconstant.inter.RouterConstantInterface;

/**
 * 生产路由连接工具类的工厂
 *
 * @author wuwf
 */
public class RouterUtilFactory {
	private Context context;
	private String mUserName;
	private String mPassword;

	public RouterUtilFactory(Context context) {
		this.context = context;
	}

	/**
	 * 检测路由是否为tplink
	 */
	@SuppressLint("DefaultLocale")
	public static boolean isSupportRouter(Context context) {
		if (WiFiUtil.getWifiLYMAC(context).length() < 6) {
			return false;
		}
		String mac = WiFiUtil.getWifiLYMAC(context).replace("-", "")
				.substring(0, 6);
		SharedPreferences share = context.getSharedPreferences(
				SharepreferConstant.ROUTER, Context.MODE_PRIVATE);
		Editor editor = share.edit();
		String[] maclib = RouterDevicesConstant.TP_LINK_MAC;
		for (int i = 0; i < maclib.length; i++) {
			if (mac.equals(maclib[i].toLowerCase())) {
				// 设置当前路由为tplink
				editor.putString(SharepreferConstant.NOW_ROUTER,
						SharepreferConstant.ROUTER_TPLINK);
				editor.commit();
				return true;
			}
		}
		if (mac.equals(RouterDevicesConstant.HEIMI_WIFI_MAC.toLowerCase())) {
			// 设置当前路由为747盒子
			editor.putString(SharepreferConstant.NOW_ROUTER,
					SharepreferConstant.ROUTER_STARWIFI);
			editor.commit();
			return true;
		}

		maclib = RouterDevicesConstant.DLINK;
		for (int i = 0; i < maclib.length; i++) {
			if (mac.equals(maclib[i].toLowerCase())) {
				// 设置当前路由为dlink
				editor.putString(SharepreferConstant.NOW_ROUTER,
						SharepreferConstant.ROUTER_DLINK);
				editor.commit();
				return true;
			}
		}
		if (mac.equals(RouterDevicesConstant.NETCORE)) {
			// 设置当前路由为磊科
			editor.putString(SharepreferConstant.NOW_ROUTER,
					SharepreferConstant.ROUTER_NETCORE);
			editor.commit();
			return true;
		}
		if (mac.equals(RouterDevicesConstant.TENDA_MAC.toLowerCase())) {
			// 设置当前路由为腾达
			editor.putString(SharepreferConstant.NOW_ROUTER,
					SharepreferConstant.ROUTER_TENGDA);
			editor.commit();
			return true;
		}
		if (mac.equals(RouterDevicesConstant.HIWIFI1.toLowerCase())
				|| mac.equals(RouterDevicesConstant.HIWIFI2.toLowerCase())) {
			// 设置当前路由为极路由
			editor.putString(SharepreferConstant.NOW_ROUTER,
					SharepreferConstant.ROUTER_POLAR);
			editor.commit();
			return true;
		}
		if (mac.equals(RouterDevicesConstant.NEWIFI.toLowerCase())) {
			// 设置当前路由为新路由
			editor.putString(SharepreferConstant.NOW_ROUTER,
					SharepreferConstant.ROUTER_NEWIFI);
			editor.commit();
			return true;
		}

		if (mac.equals(RouterDevicesConstant.ASUS.toLowerCase())) {
			// 设置当前路由为华硕路由
			editor.putString(SharepreferConstant.NOW_ROUTER,
					SharepreferConstant.ROUTER_ASUS);
			editor.commit();
			return true;
		}
		if (mac.equals(RouterDevicesConstant.BUFFALO.toLowerCase())) {
			// 设置当前路由为Buffalo路由
			editor.putString(SharepreferConstant.NOW_ROUTER,
					SharepreferConstant.ROUTER_BUFFALO);
			editor.commit();
			return true;
		}

		if (mac.equals(RouterDevicesConstant.BILIAN1.toLowerCase())) {
			// 设置当前路由为必联
			editor.putString(SharepreferConstant.NOW_ROUTER,
					SharepreferConstant.ROUTER_BILIAN);
			editor.commit();
			return true;
		}
		return false;
	}

	public RouterUtilInterface getRouterUtil(String username, String password) {
		SharedPreferences share = context.getSharedPreferences(
				SharepreferConstant.ROUTER, Context.MODE_PRIVATE);
		String nowRouterType = share.getString(SharepreferConstant.NOW_ROUTER,
				SharepreferConstant.ROUTER_TPLINK);
		RouterUtilInterface util = null;
		String ip = WiFiUtil.getWiFiLYIP(context);
		
//		if (username == null && password == null) {
//			username = "admin";
//			password = "admin";
//			// Dlink的管理员密码默认为空
//			if (nowRouterType.equals(SharepreferConstant.ROUTER_DLINK)) {
//				username = "admin";
//				password = "";
//			} else if (nowRouterType
//					.equals(SharepreferConstant.ROUTER_NETCORE)) {
//				username = "guest";
//				password = "guest";
//			}
//		}

		if (nowRouterType.equals(SharepreferConstant.ROUTER_STARWIFI)) {
			// 如果是starwifi
			RouterConstantInterface constant = new RouterStarWiFiConstant(ip);
			util = new RouterStarWifiUtil(context, constant, null, null);
		} else if (nowRouterType.equals(SharepreferConstant.ROUTER_TENGDA)) {
			// 如果是腾达
			RouterConstantInterface constant = new RouterTenDaConstant(ip);
			util = new RouterTenDaUtil(context, constant, username, password);
		} else if (nowRouterType.equals(SharepreferConstant.ROUTER_DLINK)) {
			// Dlink
			RouterConstantInterface constant = new RouterDlinkConstant_612(ip);
			util = new RouterDLinkUtil_612(context, constant, username,
					password);
		} else if (nowRouterType.equals(SharepreferConstant.ROUTER_NETCORE)) {
			// NetCore
			RouterConstantInterface constant = new RouterNetCoreConstant(ip);
			util = new RouterNetCoreUtil(context, constant, username, password);
		} else if (nowRouterType.equals(SharepreferConstant.ROUTER_BILIAN)) {
			// Bilian
			RouterConstantInterface constant = new RouterBilianBL410RConstant(
					ip);
			util = new RouterBilianUtil_BL_410R(context, constant, username,
					password);
		} else if (nowRouterType.equals(SharepreferConstant.ROUTER_NEWIFI)) {
			// Newifi 联想
			RouterConstantInterface constant = new RouterNewifiConstant(ip);
			util = new RouterNewifiUtil(context, constant, username, password);
		} else if (nowRouterType.equals(SharepreferConstant.ROUTER_POLAR)) {
			// 极路由
			RouterConstantInterface constant = new RouterPolarConstant(ip);
			util = new RouterPolarUtil(context, constant, username, password);
		} else if (nowRouterType.equals(SharepreferConstant.ROUTER_ASUS)) {
			// 华硕路由
			RouterConstantInterface constant = new RouterASUSConstant(ip);
			util = new RouterASUSUtil(context, constant, username, password);
		} else if (nowRouterType.equals(SharepreferConstant.ROUTER_BUFFALO)) {
			// Buffalo路由
			RouterConstantInterface constant = new RouterTenDaConstant(ip);
			util = new RouterBuffaloUtil(context, constant, username, password);
		} else {
			// 如果是tp_link
			RouterConstantInterface constant = new RouterTPLINKConstant(ip);
			util = new RouterTPLinkUtil(context, constant, username, password);
		}
		AllRouterInfoBean.routerUtilInterface = util;
		// 将拿来用的账号密码保存起来
		mUserName = username;
		mPassword = password;

		return util;
	}

	/**
	 * 将正确的账号密码存起来
	 */
	public void saveAccount() {
		SharedPreferences share = context.getSharedPreferences(
				SharepreferConstant.ROUTER, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = share.edit();
		
		editor.putString(SharepreferConstant.ROUTER_NAME, mUserName);
		editor.putString(SharepreferConstant.ROUTER_PWD, mPassword);
		
		editor.commit();
	}
	
	public static String getRouterAccountName(Context context) {
		SharedPreferences share = context.getSharedPreferences(
				SharepreferConstant.ROUTER, Context.MODE_PRIVATE);
		String name = share.getString(SharepreferConstant.ROUTER_NAME, null);
		
		return name;
	}
	
	public static String getRouterAccountPwd(Context context) {
		SharedPreferences share = context.getSharedPreferences(
				SharepreferConstant.ROUTER, Context.MODE_PRIVATE);
		String pwd = share.getString(SharepreferConstant.ROUTER_PWD, null);
		
		return pwd;
	}
}
