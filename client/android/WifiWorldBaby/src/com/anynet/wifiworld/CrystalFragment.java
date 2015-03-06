package com.anynet.wifiworld;

import java.text.DecimalFormat;
import java.util.List;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.anynet.wifiworld.util.DisplayUtil;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.anynet.wifiworld.MainActivity.MainFragment;
import com.anynet.wifiworld.api.AppRestClient;
import com.anynet.wifiworld.api.callback.ResponseCallback;
import com.anynet.wifiworld.app.WifiWorldApplication;
import com.anynet.wifiworld.bean.CommonResp;
import com.anynet.wifiworld.bean.CrystalMineInfoResp;
import com.anynet.wifiworld.bean.DevicesStatResp;
import com.anynet.wifiworld.bean.SystemMsgResp;
import com.anynet.wifiworld.bean.DevicesStatResp.DeviceStat;
import com.anynet.wifiworld.report.ReportUtil;
import com.anynet.wifiworld.util.HandlerUtil.MessageListener;
import com.anynet.wifiworld.util.HandlerUtil.StaticHandler;
import com.anynet.wifiworld.util.XLLog;
import com.anynet.wifiworld.R;

/**
 * 水晶页面
 * 
 */
public class CrystalFragment extends MainFragment implements OnClickListener,
		MessageListener {

	/************* 视图组件 **************/
	private TextView tvCrystalTodayCollected;
	private TextView tvBoxIncome;
	private TextView tvBoxTips;
	private RelativeLayout rlRoot;
	private ImageView ivWorld;
	
	private ImageButton tvCrystalCollectedHelp;
	private View btnCrystalEnterMine;
	private TextView tvCrystalUncollected;

	private View crystalCollect;

	private View checkDeviceLayout;

	private TextView tvDiggerSpeed;

	private TextView tvkbps;
	private ImageView inner_circle;
	// private CurveGraphView cgvDiggerHistory;
	private View ivCrystalBox;

	@Override
	public void onDestroy() {
		super.onDestroy();

	}


	private boolean isUpdating = false;

	/*************** 数据成员 *******************/
	private StaticHandler mHandler = new StaticHandler(this);

	/** 查询收益状况 */
	private Runnable crystalIncomeRunnable = new Runnable() {

		@Override
		public void run() {
			crystalMineInfo();
			mHandler.postDelayed(this, 5 * 60 * 1000);
		}
	};
	//新首页的信息
	private  CrystalMineInfoResp mCrystalMineInfoResp = new CrystalMineInfoResp();

	private DevicesStatResp mLastDevicesStatResp = new DevicesStatResp();
	/**
	 * 当前速度和状态
	 */
	private Runnable mDeviceStateRunnable = new Runnable() {

		@Override
		public void run() {

			AppRestClient.getDevicesStat(new ResponseCallback<DevicesStatResp>(
					WifiWorldApplication.getInstance()) {
				public void onSuccess(JSONObject paramJSONObject,
						DevicesStatResp devicesStatResp) {
					XLLog.log(TAG, "DeviceStat:", devicesStatResp);
					if (isDetached()){
						return;
					}
					mLastDevicesStatResp = devicesStatResp;
					if (devicesStatResp.isOK()) {
						tvDiggerSpeed.setText(devicesStatResp.totalSpeed + "");
					} else {
						// 业务错误
						XLLog.e(TAG, devicesStatResp.getReturnDesc());
						tvDiggerSpeed.setText("0");
					}

				}

				public void onFailure(int paramInt, Throwable paramThrowable) {
					tvDiggerSpeed.setText("0");
					// 网络问题
					XLLog.e(TAG, paramThrowable.toString());
				}

			},true);

			mHandler.postDelayed(this, 1000 * 6);

		}
	};



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mPageRoot = inflater.inflate(R.layout.fragment_crystal, container,
				false);
		super.onCreateView(inflater, container, savedInstanceState);
		binddingUI();
		getData();
		return mPageRoot;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		startUpdte();
	}

	@Override
	public void onPause() {
		stopUpdte();
		super.onPause();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void binddingUI() {
		mTitlebar.llHeaderLeft.setVisibility(View.GONE);
		mTitlebar.tvTitle.setText(R.string.crystal_fragment_title);

		mTitlebar.llFinish.setVisibility(View.VISIBLE);
		mTitlebar.llHeaderMy.setVisibility(View.VISIBLE);
		mTitlebar.tvHeaderRight.setVisibility(View.INVISIBLE);
		mTitlebar.llFinish.setOnClickListener(this);
		mTitlebar.llFinish.setVisibility(View.GONE);

		checkDeviceLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				DiggerHistoryActivity.startActivity(mActivity);
			}
		});
//		btnCrystalEnterMine.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				animate = new EnterWorldAnimate(rlRoot,ivWorld);
//				animate.setEnterWorldAnimateInterface(new EnterWorldAnimate.EnterWorldAnimateInterface() {
//					@Override
//					public void onEnterWorldAnimateDone() {
//						MineActivity.startActivity(mActivity);
//					}
//				});
//				animate.startAnimate();
//				MineActivity.startActivity(mActivity, mCrystalMineInfoResp.todayMineIncome,
//						mCrystalMineInfoResp.todayBoxIncome, mCrystalMineInfoResp.openedBoxCnt,
//						mCrystalMineInfoResp.missedBoxCnt, mLastDevicesStatResp.totalSpeed, false);
//			}
//		});
		tvDiggerSpeed.setText("0");
		inner_circle.setAlpha((int) (0.4f * 255));
		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) ivWorld.getLayoutParams();
		layoutParams.topMargin = -DisplayUtil.getStatusBarHeight(getActivity());
		setCrystalIncome(0, 0, 0);

	}



	private void getData() {

		showAllMsg(5);

	}

	private void showAllMsg(int id) {

		AppRestClient.getBroadCast(id, new ResponseCallback<SystemMsgResp>() {
			@Override
			public void onSuccess(JSONObject paramJSONObject,
					SystemMsgResp paramT) {
				super.onSuccess(paramJSONObject, paramT);
				if (isDetached()){
					return;
				}
				if (paramT.isOK()) {
					if (paramT.getMsg().length > 0) {

					} else {
					}

				} else {

					XLLog.e(TAG, paramT.getReturnDesc());
				}

			}

			@Override
			public void onFailure(int paramInt, Throwable paramThrowable) {

				super.onFailure(paramInt, paramThrowable);

				XLLog.e(TAG, paramThrowable.toString());
			}
		});

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		default:
			break;
		}
	}

	public void startUpdte() {
		super.startUpdte();
		if (isUpdating) // 防止重复回调。
		{
			return;
		}

		crystalMineInfo();

		isUpdating = true;
		mHandler.post(crystalIncomeRunnable);
		mHandler.post(
				mDeviceStateRunnable);

		// showAllMsg(5);
	}

	public void stopUpdte() {
		super.stopUpdte();
		isUpdating = false;
		mHandler.removeCallbacks(crystalIncomeRunnable);
		mHandler.removeCallbacks(mDeviceStateRunnable);

	}
	
	private String formatIncome(int income){
		String str = "--";
		if (income > 999999) {
			str = "999999+";
		} else {
			str = income  + "" ;
		}
		return str;
	}

	private  String formatUncollectedIncome(int income) {
		String str = "--";
		if (income > 9999) {
			str = "9999+";
		} else {
			str = income  + "" ;
		}
		return str;
	}

	private void setCrystalIncome(int uncollected, int todayMineIncome,int todayBoxIncome) {
		// 未收取
		tvCrystalUncollected.setText( formatUncollectedIncome(uncollected));

		// 今日矿机收益
		tvCrystalTodayCollected.setText( formatIncome(todayMineIncome));

		
		// 今日宝箱收益
		tvBoxIncome.setText( formatIncome(todayBoxIncome));
	}



	@Override
	public void handleMessage(Message msg) {
	}

	private void crystalMineInfo() {
		AppRestClient
				.crystalMineInfo(new ResponseCallback<CrystalMineInfoResp>() {
					public void onSuccess(org.json.JSONObject paramJSONObject,
							CrystalMineInfoResp paramT) {
						XLLog.log(TAG, "crystalIncomeSummary success:", paramT);
						if (isDetached()){
							return;
						}
						mCrystalMineInfoResp = paramT;
						if (paramT.isOK()) {
							if (isVisible()) {
								setCrystalIncome( paramT.uncollectedCrystal,
										paramT.todayMineIncome,paramT.todayBoxIncome);

								// 设置新宝箱
								if (paramT.ungetBoxCnt > 0) {
									// rlBox.setSelected(true);
									ivCrystalBox.setVisibility(View.VISIBLE);
								} else {
									// rlBox.setSelected(false);
									ivCrystalBox.setVisibility(View.INVISIBLE);

								}
								
								String boxTipsStr = String.format("已开启%d个宝箱",paramT.openedBoxCnt,paramT.missedBoxCnt );
								tvBoxTips.setText(boxTipsStr);
							}
						}

					};

					public void onFailure(int paramInt, Throwable paramThrowable) {
						XLLog.log(TAG, "crystalIncomeSummary failure:",
								paramInt, paramThrowable);
					};
				});
	}

}