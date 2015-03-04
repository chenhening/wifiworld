package com.anynet.wifiworld;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.anynet.wifiworld.api.AppRestClient;
import com.anynet.wifiworld.api.callback.ResponseCallback;
import com.anynet.wifiworld.app.BaseActivity;
import com.anynet.wifiworld.app.WifiWorldApplication;
import com.anynet.wifiworld.bean.DeviceStatResp;
import com.anynet.wifiworld.bean.DevicesStatResp;
import com.anynet.wifiworld.bean.LastSpeedStatResp;
import com.anynet.wifiworld.bean.DevicesStatResp.DeviceStat;
import com.anynet.wifiworld.report.ReportUtil;
import com.anynet.wifiworld.util.ConvertUtil;
import com.anynet.wifiworld.util.DipPixelUtil;
import com.anynet.wifiworld.util.XLLog;
import com.anynet.wifiworld.view.CurveGraphView;
import com.anynet.wifiworld.view.TitlebarHolder;
import com.anynet.wifiworld.view.CurveGraphView.CoordinateXFormater;
import com.anynet.wifiworld.view.CurveGraphView.Point;
import com.anynet.wifiworld.view.CurveGraphView.PointFormater;
import com.anynet.wifiworld.view.CurveGraphView.XYListener;
import com.anynet.wifiworld.R;

public class DiggerHistoryActivity extends BaseActivity {
	public static void startActivity(BaseActivity baseActivity) {
		Intent intent = new Intent(baseActivity, DiggerHistoryActivity.class);
		baseActivity.startActivity(intent);
		ReportUtil.reportDigClickMachine(baseActivity);

	}

	private Handler mHandler = new Handler();

	private TextView tvTotalCount;

	private TextView tvDiggerSpeed;

	private TextView tvkbps;

	private final int MAX_Y_COORDINATE_VALUE = 100;

	/** 增加两格空余位置 CurveGraphView.BLANK_SPACE=4 便于显示 */
	private final int MAX_X_COORDINATE_VALUE = 54;

	private CurveGraphView cgvDiggerHistory;

	private double totalSpeed = 0;

	private LastSpeedStatResp mLastSpeedStatResp = new LastSpeedStatResp();

	private DevicesStatResp mDeviceStatResp = new DevicesStatResp();

	private int totalSt = DeviceStatResp.DEVICE_STATE_OFFLINE;

	private int totalWorkingDevicesCnt = 0;

	private double maxScale = 0.7;

	private int currentSpeedPointColor = Color.GRAY;

	private boolean isUpdating = false;
	
	public List<DeviceStat> mDevicesStat = new ArrayList<DeviceStat>();

	private DevicesAdapter adapter;
	private LayoutInflater layoutInflater;
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

					if (devicesStatResp.isOK()) {
						mDeviceStatResp = devicesStatResp;
						calcTotalDeviceStat();
						List<DeviceStat> devicesStat = devicesStatResp
								.getDevicesStat();
						totalSpeed = 0;
						for (DeviceStat stat : devicesStat) {
							totalSpeed += stat.getSpeedValue();
						}
						setDiggerSpeed(totalSpeed);
						// 更新矿机状态
						tvTotalCount.setText(String.format("总速度 (共%d台矿机工作中)",
								totalWorkingDevicesCnt));
						tinkleCurrentSpeedPoint();
						cgvDiggerHistory.invalidate();
						sortDevicesStat(devicesStat);
						adapter.notifyDataSetChanged();

					} else {
						OnGetDevicesStatFailed();
						// 业务错误
						XLLog.e(TAG, devicesStatResp.getReturnDesc());
					}

				}

				public void onFailure(int paramInt, Throwable paramThrowable) {
			
					OnGetDevicesStatFailed();
					// 网络问题
					XLLog.e(TAG, paramThrowable.toString());
				}

			},false);

			mHandler.postDelayed(this, 1000 * 5);

		}
	};



	private void OnGetDevicesStatFailed() {
		totalSpeed = 0;
		setDiggerSpeed(totalSpeed);
		tvkbps.setText("Kbps");
		cgvDiggerHistory.invalidate();
		mDevicesStat.clear();
		adapter.notifyDataSetChanged();
	}

	private ListView listview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.diggerhistory_activity);
		mTitlebar = new TitlebarHolder(this);
		mTitlebar.tvTitle.setText("矿机监控");
		tvTotalCount = (TextView) findViewById(R.id.tvTotalCount);
		tvkbps = (TextView) findViewById(R.id.kbps);
		tvDiggerSpeed = (TextView) findViewById(R.id.totalSpeed);
		cgvDiggerHistory = (CurveGraphView) findViewById(R.id.cgvDiggerHistory);
		layoutInflater = getLayoutInflater();
		adapter = new DevicesAdapter();
		
		initUi();
		startUpdate();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopUpdate();
	}

	private void initUi() {
		
		listview = (ListView) findViewById(R.id.listview);
		listview.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		
		
		cgvDiggerHistory.setxCoordinateMinValue(0);
		cgvDiggerHistory.setxCoordinateMaxValue(MAX_X_COORDINATE_VALUE);

		cgvDiggerHistory.setyCoordinateMinValue(0);

		cgvDiggerHistory.setyCoordinateMaxValue(MAX_Y_COORDINATE_VALUE);
		cgvDiggerHistory.setxCoordinateOffset(0);

		cgvDiggerHistory.setDrawLineAsBezier(false);

		cgvDiggerHistory.setColorOfCoordinateSystem(0XFFCCCCCC);
		cgvDiggerHistory.setLineWidth(3);
		cgvDiggerHistory.setLineWidthOfCoordinateSystem(1);
		cgvDiggerHistory
				.setLineStartColor(getResources().getColor(R.color.red));
		cgvDiggerHistory.setLineEndColor(getResources().getColor(R.color.red));
		cgvDiggerHistory.setShadowStartColor(0x8fca2525);
		cgvDiggerHistory.setShadowEndColor(0x4fca2525);

		cgvDiggerHistory.setXyListener(new XYListener() {

			@Override
			public int getValueY(int valueX) {
				int valueY = 0;
				if (valueX % 2 == 0) {
					valueY = Integer.MIN_VALUE + 1;
				} else {
					double maxSpeed = getMaxSpeed();
					maxSpeed = maxSpeed <= 0 ? 1 : maxSpeed;
					if (valueX < MAX_X_COORDINATE_VALUE
							- CurveGraphView.BLANK_SPACE - 1) { // 小于49
						double speed = mLastSpeedStatResp.getSpeed(valueX / 2);
						valueY = (int) (speed / maxSpeed * maxScale * MAX_Y_COORDINATE_VALUE);
					} else {
						valueY = (int) (getCurrentSpeed() / maxSpeed * MAX_Y_COORDINATE_VALUE);
						valueY = (int) (valueY >= MAX_Y_COORDINATE_VALUE ? MAX_Y_COORDINATE_VALUE
								* maxScale
								: valueY * maxScale);
						valueY = valueY < 0 ? 0 : valueY;
					}
				}

				// XLLog.log(TAG, "getValueY:", valueY, " x:", valueX);
				return valueY;
			}

			

			@Override
			public CoordinateXFormater getFormaterX(
					CoordinateXFormater formater, int valueX) {
				if (null == formater) {
					formater = new CoordinateXFormater();
				}

				formater.textSize = DipPixelUtil.sp2px(DiggerHistoryActivity.this, 9);
				formater.textColor = 0xFFCCCCCC;
				if (valueX % 2 == 0) {
					/** 调用时减去2，偏移了一个位置，这里得加一个小时 */
					String timeByOffset = mLastSpeedStatResp
							.getTimeByOffset(valueX / 2 + 1);

					if (valueX % 6 == 0) {
						formater.text = timeByOffset;
						formater.assiteLine = 10;
					} else if (valueX % 2 == 0 && valueX % 6 != 0) {
						formater.text = null;
						formater.assiteLine = 4;
					}

					if ("0:00".equals(timeByOffset)) {
						formater.assiteLine = CoordinateXFormater.ASSITE_LINE_MATCH_COORDINATE;
						formater.assisteLineHint = false;
					} else {
						formater.assisteLineHint = true;
						formater.assisteLineHintWidth = 1;
						formater.assisteLineHintColor = 0xFFE9EAEB;
					}
				} else {
					formater.text = null;
					formater.assiteLine = CoordinateXFormater.ASSITE_LINE_NONE;
					formater.assisteLineHint = false;
				}

				return formater;
			}

			@Override
			public PointFormater getPointFormater(PointFormater formater,
					int valueX, Point location) {
				formater = null == formater ? new PointFormater() : formater;

				// 奇数点绘制
				int colorRed = getResources().getColor(R.color.red);
				formater.show = true;
				formater.radius = 5;
				formater.colorOfStroke = Color.WHITE;
				formater.color = colorRed;

				if (valueX < (MAX_X_COORDINATE_VALUE - CurveGraphView.BLANK_SPACE) / 2 - 1) {
					formater.style = Style.STROKE;
					formater.value = null;
				} else {
					formater.style = Style.FILL_AND_STROKE;
					formater.valueSize = DipPixelUtil.sp2px(DiggerHistoryActivity.this, 9);

					switch (totalSt) {
					case DeviceStatResp.DEVICE_STATE_OFFLINE:
						formater.value = null;
						currentSpeedPointColor = Color.GRAY;
						break;

					case DeviceStatResp.DEVICE_STATE_ON:
						if (9999 < totalSpeed) {
							// 速度大于9999时，显示为Mbps，小数点后仅保留一位
							DecimalFormat df = new DecimalFormat("#.#");
							String currentSpeed = df.format(totalSpeed / 1024);
							formater.value = String.format("%.1f",
									ConvertUtil.roundDown(currentSpeed, 1))
									+ "Mbps";
						} else {
							formater.value = String.format("%.0f",
									ConvertUtil.roundDown(totalSpeed + "", 0))
									+ "Kbps";
						}

						currentSpeedPointColor = currentSpeedPointColor == Color.GRAY ? colorRed
								: Color.GRAY;
						break;

					case DeviceStatResp.DEVICE_STATE_PAUSE:
					//	formater.value = getString(R.string.pause);
						formater.value = "0Kbps";
						currentSpeedPointColor = Color.GRAY;
						break;

					default:
						break;
					}
					formater.valueColor = colorRed;
					formater.color = currentSpeedPointColor;

				}

				// XLLog.log(TAG, "getPointFormater valueX:", valueX);

				return formater;
			}
		});
	}
	
	public void startUpdate() {
        isUpdating = true;
        mHandler.post(mDeviceStateRunnable);
        mHandler.post(mLastSpeedRefreshRunnable);
	}
	
    public void stopUpdate(){
        isUpdating = false;
	    mHandler.removeCallbacks(mDeviceStateRunnable);
	    mHandler.removeCallbacks(mLastSpeedRefreshRunnable);
	    mHandler.removeCallbacks(mCurrentSpeedPointAnimationRunnable);
	}
	
    private double getCurrentSpeed()
    {
        return totalSpeed;
    }
    
    private double getMaxSpeed()
    {
        double maxSpeed = null != mLastSpeedStatResp ? mLastSpeedStatResp.getMaxSpeed() : 0d;
        return Math.max(getCurrentSpeed(), maxSpeed);
    }


	private void setDiggerSpeed(double totalSpeed) {

		if (9999 < totalSpeed) {
			// 速度大于9999时，显示为Mbps，小数点后仅保留一位
			DecimalFormat df = new DecimalFormat("#.#");
			String currentSpeed = df.format(totalSpeed / 1024);
			tvDiggerSpeed.setText(currentSpeed);
			tvkbps.setText("Mbps");
		} else {
			// 设置当前速度
			tvDiggerSpeed.setText((int)totalSpeed + "");
			tvkbps.setText("Kbps");
		}

	}


	private void calcTotalDeviceStat() {
		totalWorkingDevicesCnt = 0;

		if (mDeviceStatResp != null) {
			List<DeviceStat> devicesStat = mDeviceStatResp.getDevicesStat();
			if (devicesStat != null && devicesStat.size() > 0) {
				int offlineCnt = 0;
				for (DeviceStat stat : devicesStat) {
					if (stat.getDeviceSwitch() == DeviceStat.DEVICE_STATE_ON) {
						totalWorkingDevicesCnt++;
					} else if (stat.getDeviceSwitch() == DeviceStat.DEVICE_STATE_OFFLINE) {
						offlineCnt++;
					}
				}
				if (totalWorkingDevicesCnt > 0) {
					totalSt = DeviceStatResp.DEVICE_STATE_ON;
				} else if (offlineCnt == devicesStat.size()) {
					totalSt = DeviceStatResp.DEVICE_STATE_OFFLINE;
				} else {
					totalSt = DeviceStatResp.DEVICE_STATE_PAUSE;
				}
			} else {
				totalSt = DeviceStatResp.DEVICE_STATE_OFFLINE;
			}
		} else {
			totalSt = DeviceStatResp.DEVICE_STATE_OFFLINE;
		}
	}

	/**
	 * 当前速度点更新
	 */
	private void tinkleCurrentSpeedPoint() {
		mHandler.removeCallbacks(mCurrentSpeedPointAnimationRunnable);
		if (null != mDeviceStatResp
				&& totalSt == DeviceStatResp.DEVICE_STATE_ON) {
			mHandler.post(mCurrentSpeedPointAnimationRunnable);
		}
	}

	/**
	 * 图形上的当前速度点
	 */
	private Runnable mCurrentSpeedPointAnimationRunnable = new Runnable() {
		public void run() {
			switch (totalSt) {
			case DeviceStatResp.DEVICE_STATE_OFFLINE:

				break;

			case DeviceStatResp.DEVICE_STATE_ON:

				mHandler.postDelayed(this, 500);

				break;

			case DeviceStatResp.DEVICE_STATE_PAUSE:
				break;

			default:
				break;
			}
			cgvDiggerHistory.invalidate();
		}
	};
	
    /**
     * 最近状态
     */
	private int retryCnt = 0;
    private Runnable mLastSpeedRefreshRunnable = new Runnable()
    {
        
        @Override
        public void run()
        {
            AppRestClient.lastSpeedStat(new ResponseCallback<LastSpeedStatResp>(WifiWorldApplication.getInstance())
            {
                
                public void onSuccess(JSONObject paramJSONObject, LastSpeedStatResp lastSpeedStatResp)
                {
                    XLLog.log(TAG, "lastSpeedStat ", lastSpeedStatResp.isOK(), " ", lastSpeedStatResp);
                    
                    //返回数据ok则更新
                    if (lastSpeedStatResp.isOK())
                    {
                        mLastSpeedStatResp = lastSpeedStatResp;
                        cgvDiggerHistory.invalidate();
                    }
                    
                }
                
                public void onFailure(int paramInt, Throwable paramThrowable)
                {
                    //网络问题
                    XLLog.e(TAG, "last speed failure : " + paramThrowable.toString());
					if (mLastSpeedStatResp.isEmpty() && retryCnt < 5){
						run();
						retryCnt++;
						XLLog.e(TAG, "last speed failure :retry   " + retryCnt);

						return;
					}
                }
                
            });
            mHandler.postDelayed(this, 5 * 60 * 1000);
        }
    };
    
   

	private void sortDevicesStat(List<DeviceStat> devicesStat) {
		if (devicesStat != null && !devicesStat.isEmpty()) {
		//	devicesStat.add(devicesStat.get(0));
			mDevicesStat.clear();

			/** 在线状态的放前面 */
			for (DeviceStat device : devicesStat) {
				if (device.getDeviceSwitch() == DeviceStat.DEVICE_STATE_ON) {
					mDevicesStat.add(0, device);
				} else {
					mDevicesStat.add(device);
				}
			}
		}
	}
	private class ViewHolder {
		public View icon;
		public TextView deviceName;
		public TextView deviceStat;
		public TextView speed;
		public TextView speed_unit;
		public View middleline;
		public View bottomline;
	}

    private class DevicesAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mDevicesStat.size();

		}

		@Override
		public Object getItem(int i) {
			return mDevicesStat.get(i);
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getView(int i, View convertView, ViewGroup arg2) {
			ViewHolder vh = null;
			if (convertView == null) {
				convertView = layoutInflater.inflate(R.layout.device_stat_item,
						null);
				vh = new ViewHolder();
				vh.icon = convertView.findViewById(R.id.icon);
				vh.deviceName = (TextView) convertView.findViewById(R.id.name);
				vh.deviceStat = (TextView) convertView
						.findViewById(R.id.deviceStat);
				vh.speed = (TextView) convertView.findViewById(R.id.speed);
				vh.speed_unit = (TextView) convertView.findViewById(R.id.item_kbps);
				vh.bottomline = convertView.findViewById(R.id.bottomline);
				vh.middleline = convertView.findViewById(R.id.middleline);
				convertView.setTag(vh);
			} else {
				vh = (ViewHolder) convertView.getTag();
			}
			DeviceStat stat = (DeviceStat) getItem(i);
			inflateViewByItem(vh, stat,i);
			return convertView;
		}

		private int getDeviceSwitchBg(int st) {
			switch (st) {
			case DeviceStat.DEVICE_STATE_PAUSE:
				return R.drawable.device_state_pause;
			case DeviceStat.DEVICE_STATE_ON:
				return R.drawable.device_state_working;
			case DeviceStat.DEVICE_STATE_OFFLINE:
				return R.drawable.device_state_offline;
			}
			return R.drawable.device_state_pause;
		}

		private void inflateViewByItem(ViewHolder vh, DeviceStat stat,int index) {
			if (stat != null && stat.getDeviceType().equals("PC")) {
				vh.icon.setBackgroundResource(R.drawable.crystal_digger_computer);
			} else {
				vh.icon.setBackgroundResource(R.drawable.crystal_digger_router);
			}
			vh.deviceName.setText(stat.getName());
			vh.deviceStat.setText(DeviceStat.formatDeviceSwitch(stat
					.getDeviceSwitch()));
			vh.deviceStat.setBackgroundResource(getDeviceSwitchBg(stat
					.getDeviceSwitch()));
			
			double speedValue = stat.getSpeedValue();
			String currentSpeed;
			if (9999 < speedValue) {
	        	// 速度大于9999时，显示为Mbps，小数点后仅保留一位
				speedValue /= 1024;
	        	DecimalFormat df = new DecimalFormat("#.#");    
	        	currentSpeed = df.format(speedValue);
	        	vh.speed.setText(currentSpeed);
	        	vh.speed_unit.setText("Mbps");
	        } else {
	        	// 设置当前速度
	            currentSpeed = (int) speedValue + "";
	            vh.speed.setText(currentSpeed);
	            vh.speed_unit.setText("Kbps");
	        }
			if (index == mDevicesStat.size() - 1){
				vh.bottomline.setVisibility(View.VISIBLE);
				vh.middleline.setVisibility(View.INVISIBLE);

			} else {
				vh.bottomline.setVisibility(View.INVISIBLE);
				vh.middleline.setVisibility(View.VISIBLE);
			}
		}

	}


}
