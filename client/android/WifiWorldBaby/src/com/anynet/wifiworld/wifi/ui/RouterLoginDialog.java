package com.anynet.wifiworld.wifi.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.dialog.XLBaseDialog;
import com.anynet.wifiworld.wifi.ChangingAwareEditText;

public class RouterLoginDialog extends XLBaseDialog {
	public static final String TAG = RouterLoginDialog.class.getSimpleName();
	
	private Context mContext;

	private TextView mTitle;
	private TextView mLeftBtn;
	private TextView mRightBtn;
	
	private EditText mUserName;
	private ChangingAwareEditText mPassword;
	private CheckBox mPwdCheckBox;

	public RouterLoginDialog(Context context) {
		super(context, R.style.bt_dialog);// 透明背景对话框
		mContext = context;
		initUI();
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
				lListener.onClick(RouterLoginDialog.this, 0);
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
				lListener.onClick(RouterLoginDialog.this, 0);
			}
		});
	}
	
	public String getPwdContent() {
		return mPassword.getText().toString();
	}
	
	public void clearPwdEditText() {
		mPassword.setText("");
	}
	
	public String getUserAccountName() {
		return mUserName.getText().toString();
	}
	
	public void clearUserAccountName() {
		mUserName.setText("");
	}

	private void initUI() {
		View dlgView = LayoutInflater.from(mContext).inflate(R.layout.router_login_dialog, null);
		
		mTitle = (TextView)dlgView.findViewById(R.id.router_title);
		mRightBtn = (TextView)dlgView.findViewById(R.id.btn_login);
		mLeftBtn = (TextView)dlgView.findViewById(R.id.btn_cancel);
		
		mUserName = (EditText)dlgView.findViewById(R.id.router_login_account);
		mPassword = (ChangingAwareEditText)dlgView.findViewById(R.id.router_pwd);
		mPwdCheckBox = (CheckBox)dlgView.findViewById(R.id.ShowPassword_CheckBox);
		mPwdCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mPassword.setInputType(InputType.TYPE_CLASS_TEXT |
									  (isChecked ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
									  :InputType.TYPE_TEXT_VARIATION_PASSWORD));
				
			}
		});
		
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

//	public void setContentGravity(int gravity) {
//		mContent.setGravity(gravity);
//	}
}
