package com.anynet.wifiworld.wifi;

public class WifiInfoScanned {
	private String wifi_name;
	private Integer wifi_signal;
	
	public WifiInfoScanned(String name, Integer level) {
		wifi_name = name;
		wifi_signal = level;
	}
	
	public String getWifi_name() {
		return wifi_name;
	}
	public void setWifi_name(String wifi_name) {
		this.wifi_name = wifi_name;
	}
	public Integer getWifi_signal() {
		return wifi_signal;
	}
	public void setWifi_signal(Integer wifi_signal) {
		this.wifi_signal = wifi_signal;
	}
}
