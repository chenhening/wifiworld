package com.anynet.wifiworld.wifi;

public class WifiInfoScanned {
	private String wifi_name;
	private String wifi_pwd;
	private WifiAdmin.WifiCipherType wifi_type;
	private Integer wifi_level;
	
	public WifiInfoScanned(String name, String pwd, WifiAdmin.WifiCipherType type, Integer level) {
		wifi_name = name;
		wifi_pwd = pwd;
		wifi_type = type;
		wifi_level = level;
	}
	
	public String getWifi_pwd() {
		return wifi_pwd;
	}

	public void setWifi_pwd(String wifi_pwd) {
		this.wifi_pwd = wifi_pwd;
	}

	public WifiAdmin.WifiCipherType getWifi_type() {
		return wifi_type;
	}

	public void setWifi_type(WifiAdmin.WifiCipherType wifi_type) {
		this.wifi_type = wifi_type;
	}

	public Integer getWifi_level() {
		return wifi_level;
	}

	public void setWifi_level(Integer wifi_level) {
		this.wifi_level = wifi_level;
	}

	public String getWifi_name() {
		return wifi_name;
	}
	public void setWifi_name(String wifi_name) {
		this.wifi_name = wifi_name;
	}
}
