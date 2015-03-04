package com.anynet.wifiworld.bean;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DevicesStatResp extends CommonResp {
	/**
	 * 设备id
	 */
	@Expose
	@SerializedName("c")
	private int count;

	
	@Expose
	@SerializedName("s")
	public int totalSpeed;
	
	@Expose
	@SerializedName("info")
	private  List<DeviceStat> mDevicesStat;;

	public int getCount() {
		return count;
	}

	public List<DeviceStat> getDevicesStat() {
		return mDevicesStat;
	}

	public static class DeviceStat {
		public static final int DEVICE_STATE_PAUSE = 0;

		public static final int DEVICE_STATE_ON = 1;

		public static final int DEVICE_STATE_OFFLINE = 2;

		/**
		 * 设备id
		 */
		@Expose
		@SerializedName("dv_id")
		private String deviceId;

		/**
		 * 0 暂停 1 开启 2离线 st=switch
		 */
		@Expose
		@SerializedName("st")
		private int deviceSwitch = DEVICE_STATE_OFFLINE;

		/**
		 * 上传速度
		 */
		@Expose
		@SerializedName("s")
		private String speed;

		
		/**
		 * 矿机名称
		 */
		@Expose
		@SerializedName("hn")
		private String name;

		
		/**
		 * 矿机名称
		 */
		@Expose
		@SerializedName("nm")
		private String nm;

		
		public String getDeviceId() {
			return deviceId;
		}

		public void setDeviceId(String deviceId) {
			this.deviceId = deviceId;
		}

		public int getDeviceSwitch() {
			return deviceSwitch;
		}
		
		public static  String formatDeviceSwitch(int st){
			switch(st){
			case DEVICE_STATE_PAUSE:
				return "暂停中";
			case DEVICE_STATE_ON:
				return "工作中";
			case DEVICE_STATE_OFFLINE:
				return "已停止";
			}
			return "已停止";
		}
		
		

		public void setDeviceSwitch(int deviceSwitch) {
			this.deviceSwitch = deviceSwitch;
		}

		public String getSpeed() {
			return speed;
		}
		
		public String getName(){
			return name;
		}
		
		public String getDeviceType(){
			return nm;
		}

		public void setSpeed(String speed) {
			this.speed = speed;
		}

		public double getSpeedValue() {
			try {
				return Double.parseDouble(speed);
			} catch (Exception e) {
				return 0;
			}
		}


		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return super.toString() + " devideId:" + deviceId + " name:" + name + " state:"
					+ deviceSwitch ;
		}
	}
}
