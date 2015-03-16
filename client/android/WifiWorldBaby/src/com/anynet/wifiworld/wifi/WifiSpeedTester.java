package com.anynet.wifiworld.wifi;

import java.util.ArrayList;
import java.util.List;

import com.anynet.wifiworld.MainActivity;
import com.anynet.wifiworld.R;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class WifiSpeedTester implements OnClickListener {
	private final static String TAG = WifiSpeedTester.class.getSimpleName();
	private final String TestURL = "http://bcscdn.baidu.com/netdisk/BaiduYunGuanjia_5.2.0.exe";
	
	private DownloadFile mDownloadFile;
	private DownloadedFileParams mDownloadedFileParams;
	private boolean mTestFlag;
	private final int UPDATE_SPEED = 1;// 进行中
	private final int UPDATE_DNOE = 0;// 完成下载
	private int mStartAngle;
	
	private View mContext;
	private ImageView mNeedleView;
	private TextView mSpeedAvgView;
	private TextView mSpeedCurView;
	private Button mSpeedStart;
	
	private List<Long> mSpeedList = new ArrayList<Long>();
	
	public WifiSpeedTester(View context) {
		mContext = context;
		mTestFlag = false;
		
		mNeedleView = (ImageView) mContext.findViewById(R.id.wifi_speed_needle);
		mSpeedAvgView = (TextView) mContext.findViewById(R.id.wifi_speed_avg_num);
		mSpeedCurView = (TextView) mContext.findViewById(R.id.wifi_speed_cur_num);
		mSpeedStart = (Button) mContext.findViewById(R.id.start_button);
		
		mDownloadedFileParams = new DownloadedFileParams();
	}
	
	@Override
	public void onClick(View arg0) {
		mTestFlag = !mTestFlag;
		if (mTestFlag) {
			mStartAngle = 0;
			mSpeedList.clear();
			mSpeedStart.setText("停止");
			
			mDownloadFile = new DownloadFile(mContext, mDownloadedFileParams);
			mDownloadFile.execute(TestURL);
			
			//create another thread to update UI
			new Thread() {
				@Override
				public void run() {
					Log.i(TAG, "Start to update UI");
					while (mDownloadedFileParams.downloadedBytes < mDownloadedFileParams.totalBytes) {
						try {
							sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						Log.i(TAG, "handle empty message");
						handler.sendEmptyMessage(UPDATE_SPEED);
					}
					if (mDownloadedFileParams.downloadedBytes == mDownloadedFileParams.totalBytes) {
						handler.sendEmptyMessage(UPDATE_DNOE);
						mDownloadFile.cancel(false);
					}

				}
			}.start();
		} else {
			mSpeedStart.setText("测速");
			if (mDownloadFile != null) {
				mDownloadFile.cancel(false);
			}
		}
		
	}
	
	protected void startAnimation(double d) {
		AnimationSet animationSet = new AnimationSet(true);
		/**
		 * 前两个参数定义旋转的起始和结束的度数，后两个参数定义圆心的位置
		 */
		int endAngle = getAngle(d);

		RotateAnimation rotateAnimation = new RotateAnimation(mStartAngle, endAngle, Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 1f);
		rotateAnimation.setDuration(1000);
		animationSet.addAnimation(rotateAnimation);
		mNeedleView.startAnimation(animationSet);
		mStartAngle = endAngle;
	}

	public int getAngle(double number) {
		double a = 0;
		if (number >= 0 && number <= 512) {
			a = number / 128 * 15;
		} else if (number > 521 && number <= 1024) {
			a = number / 256 * 15 + 30;
		} else if (number > 1024 && number <= 10 * 1024) {
			a = number / 512 * 5 + 80;
		} else {
			a = 180;
		}
		return (int) a;
	}

	private Handler handler = new Handler() {
		long curSpeed = 0;
		long avgSpeed = 0;
		long numberTotal = 0;

		@Override
		public void handleMessage(Message msg) {
			int value = msg.what;
			switch (value) {
			case UPDATE_SPEED:
				Log.i(TAG, "Handle speed message");
				curSpeed = mDownloadedFileParams.speed / 1024;
				mSpeedList.add(curSpeed);
				Log.i(TAG, "Current Network Speed: " + curSpeed);
				for (Long numberLong : mSpeedList) {
					numberTotal += numberLong;
				}
				avgSpeed = numberTotal / mSpeedList.size();
				numberTotal = 0;
				mSpeedCurView.setText(curSpeed + " kb/s");
				mSpeedAvgView.setText(avgSpeed + " kb/s");
				startAnimation(Double.parseDouble(String.valueOf(curSpeed)));
				break;
			default:
				break;
			}
		}
	};
	
	public static class DownloadedFileParams {
		public long speed;
		public long downloadedBytes;
		public long totalBytes = 1024; //byte
		public String netWorkType;
		public float downloadedPercent;
	}

}
