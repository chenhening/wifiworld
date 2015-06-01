package com.anynet.wifiworld.dialog;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.anynet.wifiworld.R;

/**
 * 进度条对话框
 * 
 */
public class XLProgressDialog extends XLBaseDialog {
	public static final String TAG = "XLProgressDialog";
	private static int FRESH_PROGRESS = 1000;
	private Context mContext;
	private long mMax;
	private long mCurrent;
	private int mPermillage;
	private TextView mProHint;
	private TextView mPercentageText;


	public XLProgressDialog(Context context) {
		super(context, R.style.bt_dialog);
		mContext = context.getApplicationContext();
		initUI();
	}

	public void setMessage(String hintString) {
		if (hintString != null) {
			mProHint.setText(hintString);
		}
	}

	private int getPermillage(long numerator, long denominator) {
		if (denominator == 0) {
			return 0;
		}
		int permillage = (int) ((float) numerator / denominator * 1000);
		if (permillage < 0) {
			permillage = 0;
		}
		if (permillage > 1000) {
			permillage = 1000;
		}
		return permillage;
	}

	public void setMax(long maxValue) {
	 
		mMax = maxValue;
	}

	public void setProgress(long currentValue) {
		int permillage = getPermillage(currentValue, mMax);

		mCurrent = currentValue;
		mPermillage = permillage;
		mAnimHandler.sendEmptyMessage(FRESH_PROGRESS);
	}

	private void initUI() {
		View dlgView = LayoutInflater.from(mContext).inflate(R.layout.xl_progress_dialog, null);

		mProHint = (TextView) dlgView.findViewById(R.id.xl_dlg_pro_hint);
		
		mPercentageText = (TextView) dlgView.findViewById(R.id.xl_dlg_pro_percentage);
	
		setMax(0);
		setProgress(0);
		setContentView(dlgView);
	}

	// 刷进度handler
	private Handler mAnimHandler = new Handler() {

		@Override
		public void dispatchMessage(Message msg) {
			if (msg.what == FRESH_PROGRESS) {
				mPercentageText.setText("" + mPermillage / 10 + "%");
				
			}
		}

	};

}
