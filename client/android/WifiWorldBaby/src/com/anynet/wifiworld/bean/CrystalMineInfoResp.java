package com.anynet.wifiworld.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CrystalMineInfoResp extends CommonResp {
	//未入账红水晶
	@Expose
	@SerializedName("td_not_in_a")
	public int uncollectedCrystal;
	
	//矿机产量
	@Expose
	@SerializedName("td_dev_pdc")
	public int todayMineIncome;
	
	//宝箱产量
	@Expose
	@SerializedName("td_box_pdc")
	public int todayBoxIncome;
	
	//已经开启的宝箱
	@Expose
	@SerializedName("b_open")
	public int openedBoxCnt;
	
	//已经错过的宝箱
	@Expose
	@SerializedName("b_miss")
	public int missedBoxCnt;
	
	//未打开的宝箱F
	@Expose
	@SerializedName("b_unget")
	public int ungetBoxCnt;
}
