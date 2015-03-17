package com.anynet.wifiworld.config;

import android.location.Location;

import com.anynet.wifiworld.bean.KeyValue;
import com.anynet.wifiworld.constant.Const;
import com.anynet.wifiworld.util.PreferenceHelper;

/**
 * 请在这里增加文件描述
 * <p>
 */
public class GlobalConfig {

	public static final String BMOB_KEY = "b20905c46c6f0ae1edee547057f04589";
	public static final String SMSSDK_KEY = "5ea9dee43eb2";
	public static final String SMSSDK_SECRECT = "6f332e8768e0fe21509cddbe804f016b";

	private static GlobalConfig instanceConfig = new GlobalConfig();

	KeyValue configs[];

	private long noNeedLoginTimeDiv;

	private long updateDataDiv;

	public long getLastUpdateTime() {
		return PreferenceHelper.getInstance()
				.getLong(Const.LAST_UPDATE_TIME, 0);
		// return 0;

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
