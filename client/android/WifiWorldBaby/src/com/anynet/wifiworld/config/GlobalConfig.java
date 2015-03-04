/*
 * 文件名称 : GlobalConfig.java
 * <p>
 * 作者信息 : liuzongyao
 * <p>
 * 创建时间 : 2014-10-10, 下午5:28:19
 * <p>
 * 版权声明 : Copyright (c) 2009-2012 Hydb Ltd. All rights reserved
 * <p>
 * 评审记录 :
 * <p>
 */

package com.anynet.wifiworld.config;

import com.anynet.wifiworld.bean.KeyValue;
import com.anynet.wifiworld.constant.Const;
import com.anynet.wifiworld.util.PreferenceHelper;

/**
 * 请在这里增加文件描述
 * <p>
 */
public class GlobalConfig {

	private static GlobalConfig instanceConfig = new GlobalConfig();

	KeyValue configs[];

	private long noNeedLoginTimeDiv;

	private long updateDataDiv;

	public long getLastUpdateTime() {
		return PreferenceHelper.getInstance().getLong(
				Const.LAST_UPDATE_TIME, 0);
	//	return  0;

	}

	public void setLastUpdateTime(long lastUpdateTime) {
		PreferenceHelper.getInstance().setLong(Const.LAST_UPDATE_TIME,
				lastUpdateTime);
	}

	/**
	 * 时间间隔下次开始生效，由于登录先于获取配置数据
	 * 
	 * @return
	 */
	public long getNoNeedLoginTimeDiv() {
		noNeedLoginTimeDiv = PreferenceHelper.getInstance().getLong(
				Const.PREF_NO_NEED_LOGIN_TIME_DIV, 0);
		return (noNeedLoginTimeDiv <= 0) ? Const.NO_NEED_LOGIN_TIME_DIV
				: noNeedLoginTimeDiv * 1000;
	}

	public void setNoNeedLoginTimeDiv(long noNeedLoginTimeDiv) {
		this.noNeedLoginTimeDiv = noNeedLoginTimeDiv;

		PreferenceHelper.getInstance().setLong(
				Const.PREF_NO_NEED_LOGIN_TIME_DIV, noNeedLoginTimeDiv);
	}

	public long getUpdateDataDiv() {

		return (updateDataDiv <= 0) ? Const.UPDATE_DATA_DIV : updateDataDiv;
	}

	public void setUpdateDataDiv(long updateDataDiv) {
		this.updateDataDiv = updateDataDiv;
	}

	private GlobalConfig() {

	}

	public static GlobalConfig getInstance() {
		return instanceConfig;
	}

	public void updateConfig(KeyValue[] configs) {

		this.configs = configs;

		for (KeyValue config : configs) {
			if (config.getKey().toLowerCase().equals("autologin_div_time")) {

				setNoNeedLoginTimeDiv(Long.valueOf(config.getValue()));
			} else if (config.getKey().toLowerCase()
					.endsWith("update_div_time")) {
				setUpdateDataDiv(Long.valueOf(config.getValue()));
			}

		}

	}

	public String getConfig(String value) {

		return "";
	}

}
