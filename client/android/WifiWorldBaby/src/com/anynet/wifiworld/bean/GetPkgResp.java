package com.anynet.wifiworld.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetPkgResp extends CommonResp {
	@Expose
	@SerializedName("m")
	public int money;
	@Expose
	@SerializedName("t")
	public int type;
}
