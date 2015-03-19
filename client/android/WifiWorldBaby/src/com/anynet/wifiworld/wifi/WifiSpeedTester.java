package com.anynet.wifiworld.wifi;

import java.util.ArrayList;
import java.util.List;

import com.anynet.wifiworld.R;

import android.R.string;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.renderscript.RenderScript.Priority;
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
	private NetworkTester mSpeedTester = null;
	private UpdateMeter mUpdateMeter = null;
	
	private List<Long> mSpeedList = new ArrayList<Long>();
	
	public WifiSpeedTester(View context) {
		mContext = context;
		mTestFlag = false;
		
		mNeedleView = (ImageView) mContext.findViewById(R.id.wifi_speed_needle);
		mSpeedAvgView = (TextView) mContext.findViewById(R.id.wifi_speed_avg_num);
		mSpeedCurView = (TextView) mContext.findViewById(R.id.wifi_speed_cur_num);
		mSpeedStart = (Button) mContext.findViewById(R.id.start_button);
	}
	
	@Override
	public void onClick(View arg0) {
		mTestFlag = !mTestFlag;
		mStartAngle = 0;
		if (mTestFlag) {
			mSpeedList.clear();
			mSpeedStart.setText("停止");
			
			mDownloadedFileParams = new DownloadedFileParams();
			mSpeedTester = new NetworkTester(TestURL, mDownloadedFileParams);
			mSpeedTester.start();
			//create another thread to update UI
			mUpdateMeter = new UpdateMeter(mDownloadedFileParams);
			mUpdateMeter.start();
		} else {
			mSpeedStart.setText("测速");
			if (mSpeedTester != null) {
				mSpeedTester.stopDownload();
				mSpeedTester.interrupt();
				mSpeedTester = null;
			}
			if (mUpdateMeter != null) {
				mUpdateMeter.stopUpdateMeter();
				mUpdateMeter.interrupt();
				mUpdateMeter = null;
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
		rotateAnimation.setDuration(500);
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
		List<Long> speedList = new ArrayList<Long>();

		@Override
		public void handleMessage(Message msg) {
			int value = msg.what;
			switch (value) {
			case UPDATE_SPEED:
				curSpeed = mDownloadedFileParams.speed / 1024;
				speedList.add(curSpeed);
				Log.i(TAG, "Current Network Speed: " + curSpeed);
				for (Long numberLong : speedList) {
					numberTotal += numberLong;
				}
				avgSpeed = numberTotal / speedList.size();
				numberTotal = 0;
				mSpeedCurView.setText(curSpeed + " kb/s");
				mSpeedAvgView.setText(avgSpeed + " kb/s");
				startAnimation(Double.parseDouble(curSpeed+""));
				break;
			case UPDATE_DNOE:
				mSpeedStart.setText("测速");
				if (mSpeedTester != null) {
					mSpeedTester.stopDownload();
					mSpeedTester.interrupt();
					mSpeedTester = null;
				}
				if (mUpdateMeter != null) {
					mUpdateMeter.stopUpdateMeter();
					mUpdateMeter.interrupt();
					mUpdateMeter = null;
				}
				curSpeed = 0;
				avgSpeed = 0;
				numberTotal = 0;
				speedList.clear();
			default:
				break;
			}
		}
	};
	
	public class UpdateMeter extends Thread {
		public boolean stopFlag = false;
		private DownloadedFileParams mParams;
		
		public UpdateMeter(DownloadedFileParams params) {
			mParams = params;
			stopFlag = false;
		}
		
		@Override
		public void run() {
			Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
			Log.i(TAG, "Start to update UI");
			while (mParams.downloadedBytes < mParams.totalBytes && !stopFlag) {
				Log.i(TAG, "Downloaded bytes feedback: " + mParams.downloadedBytes);
				try {
					sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				handler.sendEmptyMessage(UPDATE_SPEED);
				yield();
			}
			if (mParams.downloadedBytes == mParams.totalBytes) {
				handler.sendEmptyMessage(UPDATE_DNOE);
			}
			super.run();
		}
		
		public void stopUpdateMeter() {
			stopFlag = true;
		}
		
	}
	
	public class DownloadedFileParams {
		public long speed;
		public long downloadedBytes;
		public long totalBytes; //byte
		public String netWorkType;
		public float downloadedPercent;
		
		public DownloadedFileParams() {
			speed = 0;
			downloadedBytes = 0;
			totalBytes = 1024;
			netWorkType = null;
			downloadedPercent = 0f;
		}
	}

}
