package com.anynet.wifiworld.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.anynet.wifiworld.R;

/**
 * 单个button的基本对话框，默认按钮确定，默认title="提示"
 * 
 * @author admin
 * 
 */
public class XLOneButtonDialog extends XLBaseDialog {
	public static final String TAG = "XLOneButtonDialog";

	private Context mContext;

	private TextView mTitle;
	private TextView mContent;
	private TextView mBottomBtn;
	
	private DialogInterface.OnClickListener mBottomBtnListener = null;

	public XLOneButtonDialog(Context context) {
		super(context, R.style.bt_dialog);
		mContext = context;
		initUI();
	}

	/**
	 * 自定义title
	 * 
	 * @param titleStr
	 */
	public void setTitle(String titleStr) {
		if (titleStr != null) {
			mTitle.setText(titleStr);
		} else {
			mTitle.setText(R.string.tips);
		}
	}

	/**
	 * 设置content内容字符串
	 * 
	 * @param titleStr
	 */
	public void setContent(String titleStr) {
		if (titleStr != null) {
			mContent.setText(titleStr);
		}
	}

	/**
	 * 设置底部按钮字符串-默认确定
	 * 
	 * @param bottomStr
	 */
	public void setBottomBtnStr(String bottomStr) {
		if (bottomStr != null)
			mBottomBtn.setText(bottomStr);
	}

	/**
	 * 设置底部按钮响应-默认使dialog消失
	 * 
	 * @param bListener
	 */
	public void setBottomBtnListener(DialogInterface.OnClickListener bListener) {
		if (bListener != null) {
			mBottomBtnListener = bListener;
			mBottomBtn.setTag(bListener);
			mBottomBtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					DialogInterface.OnClickListener listener = (DialogInterface.OnClickListener) arg0.getTag();
					listener.onClick(XLOneButtonDialog.this, 0);
				}
			});
		}
	}



	public void setContentGravity(int gravity) {
		mContent.setGravity(gravity);
	}

	private void initUI() {
		View dlgView = LayoutInflater.from(mContext).inflate(R.layout.xl_one_button_dialog, null);

		mTitle = (TextView) dlgView.findViewById(R.id.dlg_title);
		mContent = (TextView) dlgView.findViewById(R.id.dlg_content);
		
		mBottomBtn = (TextView) dlgView.findViewById(R.id.tv_bottom_btn);
		
		
		mBottomBtn.setVisibility(View.VISIBLE);
		if (mBottomBtnListener == null) {// 这样不会覆盖调用者设定的监听
			setBottomBtnListener(new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dlg, int arg1) {
					dlg.dismiss();
				}
			});
		}
		setContentView(dlgView);
	}
}
