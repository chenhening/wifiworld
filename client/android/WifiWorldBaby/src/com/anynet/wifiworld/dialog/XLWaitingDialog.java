package com.anynet.wifiworld.dialog;

import android.content.Context;
import android.widget.TextView;

import com.anynet.wifiworld.R;

/**
 * 转菊花的等待框-setProHintStr设置菊花下方的提示字符串
 * 
 * @author admin
 * 
 */
public class XLWaitingDialog extends XLBaseDialog {

	public static final String TAG = XLWaitingDialog.class.getSimpleName();
	private TextView mProHint;

	public XLWaitingDialog(Context context) {
		super(context, R.style.unified_loading_dialog);
		setContentView(R.layout.xl_loading_dialog);
		initUI();
	}

	/**
	 * 设置菊花底部的提示字符串
	 * 
	 * @param hintString
	 */
	public void setProHintStr(String hintString) {
		mProHint.setText(hintString);
	}

	private void initUI() {
		mProHint = (TextView) findViewById(R.id.unified_loading_view_text);
	}

}
