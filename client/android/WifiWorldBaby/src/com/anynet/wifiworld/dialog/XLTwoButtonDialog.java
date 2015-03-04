package com.anynet.wifiworld.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.anynet.wifiworld.R;

/**
 * 基本的2Btn弹窗。默认左键取消，右键确定，默认title="提示"
 * 
 * @author admin
 * 
 */
public class XLTwoButtonDialog extends XLBaseDialog {
	public static final String TAG = "XLAlarmDialog";

	private Context mContext;

	private TextView mTitle;// 默认-提示
	private TextView mContent;
	private ImageView mLeftIcon;
	private TextView mLeftBtn;
	private TextView mRightBtn;
	private Object mUserData;

	public XLTwoButtonDialog(Context context) {
		super(context, R.style.bt_dialog);// 透明背景对话框
		mContext = context;
		initUI();
	}

	public void setUserData(Object userData) {
		mUserData = userData;
	}

	public Object getUserData() {
		return mUserData;
	}

	/**
	 * 设置title字符串，默认:提示
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
	 * 设置content的行间距
	 * @param lineSpace
	 */
	public void setContentLineSpacing(int lineSpace)
	{
		mContent.setLineSpacing(lineSpace, 1);
	}
	
	/**
	 * 设置content内容字符串
	 * 
	 * @param titleStr
	 */
	public void setContent(SpannableString text) {
		if (text != null) {
			mContent.setText(text);
		}
	}

	/**
	 * 设置content字符串，采用SpannableString类型，用于实现个别字体的颜色不一样
	 * 
	 * @param titleStr
	 */
	public void setSpanContent(SpannableString titleStr) {
		if (titleStr != null) {
			mContent.setText(titleStr);
		}
	}

	/**
	 * 设置弹窗图标（左）-不设置则不显示(图标请使用xl_dlg_xxx!)
	 * 
	 * @param drawable
	 */
	public void setIcon(Drawable drawable) {
		if (drawable != null) {
			mLeftIcon.setVisibility(View.VISIBLE);
			mLeftIcon.setImageDrawable(drawable);
		}else{
			mLeftIcon.setVisibility(View.GONE);
		}
	}

	/**
	 * 设置左键字符串-默认取消
	 * 
	 * @param lStr
	 */
	public void setLeftBtnStr(String lStr) {
		if (lStr != null) {
			mLeftBtn.setText(lStr);
		}
	}

	/**
	 * 设置右键字符串-默认确定
	 * 
	 * @param rStr
	 */
	public void setRightBtnStr(String rStr) {
		if (rStr != null) {
			mRightBtn.setText(rStr);
		}
	}

	/**
	 * 设置右键背景 蓝色背景-dlg_right_btn_selecotr 白色背景-dlg_left_btn_selecotr
	 * 
	 * @param drawable
	 */
	public void setRightBtnBackground(Drawable drawable) {
		if (drawable != null) {
			mRightBtn.setBackgroundDrawable(drawable);
		}
	}

	public void setRightBtnBackground(int resid) {
		mRightBtn.setBackgroundResource(resid);
	}

	public void setRightBtnTextColor(int color) {
		mRightBtn.setTextColor(color);
	}

	/**
	 * 设置左键键响应-默认使dialog消失
	 * 
	 * @param lListener
	 */
	public void setLeftBtnListener(DialogInterface.OnClickListener lListener) {
		mLeftBtn.setTag(lListener);
		mLeftBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				DialogInterface.OnClickListener lListener = (DialogInterface.OnClickListener) mLeftBtn.getTag();
				lListener.onClick(XLTwoButtonDialog.this, 0);
			}
		});
	}

	/**
	 * 设置右键键响应-默认使dialog消失
	 * 
	 * @param rListener
	 */
	public void setRightBtnListener(DialogInterface.OnClickListener rListener) {
		mRightBtn.setTag(rListener);
		mRightBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				DialogInterface.OnClickListener lListener = (DialogInterface.OnClickListener) mRightBtn.getTag();
				lListener.onClick(XLTwoButtonDialog.this, 0);
			}
		});
	}

	private void initUI() {
		View dlgView = LayoutInflater.from(mContext).inflate(R.layout.xl_two_button_dialog, null);

		mTitle = (TextView) dlgView.findViewById(R.id.dlg_title);
		mContent = (TextView) dlgView.findViewById(R.id.dlg_content);

		
		mLeftBtn = (TextView) dlgView.findViewById(R.id.dlg_left_btn);
		mRightBtn = (TextView) dlgView.findViewById(R.id.dlg_right_btn);

		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				dialog.dismiss();
			}
		};
		setLeftBtnListener(listener);
		setRightBtnListener(listener);
		setContentView(dlgView);
		setCanceledOnTouchOutside(true);
	}

	public void setContentGravity(int gravity) {
		mContent.setGravity(gravity);
	}

}
