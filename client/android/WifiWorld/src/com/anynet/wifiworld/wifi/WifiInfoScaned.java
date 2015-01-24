package com.anynet.wifiworld.wifi;

public class WifiInfoScaned {
	private String wifi_name;
	private Integer wifi_signal;
	private String wifi_sectoin;
	
	public String getWifi_sectoin() {
		return wifi_sectoin;
	}
	public void setWifi_sectoin(String wifi_sectoin) {
		this.wifi_sectoin = wifi_sectoin;
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
