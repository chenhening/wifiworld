package com.anynet.wifiworld.report;

import java.util.HashMap;

import org.json.JSONObject;

import com.umeng.analytics.MobclickAgent;
import com.anynet.wifiworld.api.AppRestClient;
import com.anynet.wifiworld.api.callback.ResponseCallback;
import com.anynet.wifiworld.app.WifiWorldApplication;
import com.anynet.wifiworld.constant.Const;
import com.anynet.wifiworld.util.PreferenceHelper;
import com.anynet.wifiworld.util.XLLog;

import android.content.Context;

public class ReportUtil {
    
    
    public static HashMap<String, String> getUserId()
    {
        HashMap<String, String>  h = new  HashMap<String, String>();
        String userId =   PreferenceHelper.getInstance().getString(Const.USER_ID, "");
        h.put("userid", userId);
         
         return h;
    }
    
	/***********切换tab*************/
	//点击挖水晶tab，在其他页面点击挖水晶
	public static void reportClickTabDig(Context context){
		MobclickAgent.onEvent(context, "clk_main_tabdig", getUserId() );
		reportUserBehavior(ReportActionId.TAB_CHANGE);
	}


	//点击提现tab，在其他页面点击提现tab
	public static void reportClickTabWithDraw(Context context){
		MobclickAgent.onEvent(context, "clk_main_tabwithdraw" , getUserId());
		reportUserBehavior(ReportActionId.TAB_CHANGE);

	}
	public static void reportClickTabWithMY(Context context){
		reportUserBehavior(ReportActionId.TAB_CHANGE);
	}

	public static void reportUserBehavior(int actionid) {
//
//		AppRestClient.reportStat(actionid,new  ResponseCallback<ReportStatResp>(){
//			@Override
//			public void onSuccess(JSONObject paramJSONObject,
//								  ReportStatResp paramT) {
//				// TODO Auto-generated method stub
//				super.onSuccess(paramJSONObject, paramT);
//			}
//
//			@Override
//			public void onFailure(int paramInt, Throwable paramThrowable) {
//				super.onFailure(paramInt, paramThrowable);
//			}
//		});
	}
	/***********挖水晶页面*************/
	//点击“立即收取”button
	public static void reportDigClickCollect(Context context){
		MobclickAgent.onEvent(context, "clk_dig_collect_crystal", getUserId());
		reportUserBehavior(ReportActionId.COLLECT);

	}
	//手机上收取水晶成功
	public static void reportDigCollectSuccess(Context context){
		MobclickAgent.onEvent(context, "clk_dig_collect_crystal_suc", getUserId());

	}
	//点击“宝箱”按钮
	public static void reportDigClickBox(Context context){
		MobclickAgent.onEvent(context, "clk_dig_box", getUserId());
//		AppRestClient.reportStat(ReportActionId.BOX_INDEX, new  ResponseCallback<ReportStatResp>(WifiWorldApplication.getInstance()){
//		    public void onSuccess(JSONObject paramJSONObject, ReportStatResp reportStatResp)
//            {
//                
//            }
//            
//            public void onFailure(int paramInt, Throwable paramThrowable)
//            {
//              
//              
//            }
//			
//		});

	}
	//点击“总速度”进入矿机详情
	public static void reportDigClickMachine(Context context){
		MobclickAgent.onEvent(context, "clk_dig_machine", getUserId());
		reportUserBehavior(ReportActionId.DEVICES_STAT);

	}
	//“挖水晶”页面点击“账户”按钮
	public static void reportDigClickAccount(Context context){
		MobclickAgent.onEvent(context, "clk_dig_account", getUserId());

	}
	/***********提现页面*************/
	//提现页面点击“账户”按钮
	public static void reportWithDrawClickAccount(Context context){
		MobclickAgent.onEvent(context, "clk_withdraw_account", getUserId());

	}



	//点击“info” icon
	public static void reportWithDrawClickInfo(Context context){
		MobclickAgent.onEvent(context, "clk_withdraw_info", getUserId());

	}
	//点击“一键领取” button
	public static void reportWithDrawClickHongbao(Context context){
		MobclickAgent.onEvent(context, "clk_withdraw_hongbao", getUserId());
		reportUserBehavior(ReportActionId.DRAW_PKG);

	}
	//获得红包成功的提示
	public static void reportWithDrawHongbaoSuccess(Context context){
		MobclickAgent.onEvent(context, "clk_withdraw_hongbao_suc", getUserId());
		reportUserBehavior(ReportActionId.DRAW_PKG_SUCCESS);
	}

	//获得红包成功的提示
	public static void reportWithDrawHongbaoFail(Context context){
		reportUserBehavior(ReportActionId.DRAW_PKG_FAIL);
	}
	/***********帐号信息页面*************/
	//点击“退出当前帐号”
	public static void reportAccoutClickLogout(Context context){
		MobclickAgent.onEvent(context, "clk_account_logout", getUserId());
		reportUserBehavior(ReportActionId.CLICK_LOGOUT);

	}
	
	/***********累计入账页面*************/
	//点击“累计入账”
	public static void reportAssertio(Context context){
		reportUserBehavior(ReportActionId.ASSETIO);

	}
	
	/***********登录页面*************/
	//点击“登录”
	public static void reportLogin(Context context){
		reportUserBehavior(ReportActionId.LOGIN);

	}
	//进入新矿场
	public static void reportEnterMineActivity(Context context) {
		reportUserBehavior(ReportActionId.ENTER_MINE);

	}

	//我的信息页面点击系统消息
	public static void reportMyFragmentClickSysMsg(Context context) {
		reportUserBehavior(ReportActionId.CLICK_SYS_MSG);

	}

	//我的信息页面点击设置
	public static void reportMyFragmentClickSet(Context context) {
		reportUserBehavior(ReportActionId.CLICK_SET);

	}


}
