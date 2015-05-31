package com.anynet.wifiworld.wifi;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.text.InputType;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.data.WifiReport;
import com.anynet.wifiworld.dialog.XLBaseDialog;

public class WifiConnectDialog extends XLBaseDialog {
	public static final String TAG = WifiConnectDialog.class.getSimpleName();

	public enum DialogType {
		DEFAULT,
		PASSWORD,
		REPORT
	};
	
	private Context mContext;

	private TextView mTitle;
	private TextView mSecurityView;
	private TextView mSignalView;
	private TextView mLeftBtn;
	private TextView mRightBtn;
	private ImageView mLeftIcon;
	private Object mUserData;
	
	private ChangingAwareEditText mPassword;
	private CheckBox mPwdCheckBox;
	
	private int mBlackTypeId;
	private EditText mBlackContent;

	public WifiConnectDialog(Context context, DialogType dialogType) {
		super(context, R.style.bt_dialog);// 透明背景对话框
		mContext = context;
		initUI(dialogType);
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
	 * 设置WiFi信号内容字符串
	 * 
	 * @param titleStr
	 */
	public void setSignal(String titleStr) {
		if (titleStr != null) {
			mSignalView.setText(titleStr);
		}
	}

	/**
	 * 设置content的行间距
	 * @param lineSpace
	 */
	public void setSignalLineSpacing(int lineSpace)
	{
		mSignalView.setLineSpacing(lineSpace, 1);
	}
	
	/**
	 * 设置content内容字符串
	 * 
	 * @param titleStr
	 */
	public void setSignal(SpannableString text) {
		if (text != null) {
			mSignalView.setText(text);
		}
	}

	/**
	 * 设置content字符串，采用SpannableString类型，用于实现个别字体的颜色不一样
	 * 
	 * @param titleStr
	 */
	public void setSpanSignal(SpannableString titleStr) {
		if (titleStr != null) {
			mSignalView.setText(titleStr);
		}
	}

	/**
	 * 设置WiFi信号内容字符串
	 * 
	 * @param titleStr
	 */
	public void setSecurity(String titleStr) {
		if (titleStr != null) {
			mSecurityView.setText(titleStr);
		}
	}

	/**
	 * 设置content的行间距
	 * @param lineSpace
	 */
	public void setSecurityLineSpacing(int lineSpace)
	{
		mSecurityView.setLineSpacing(lineSpace, 1);
	}
	
	/**
	 * 设置content内容字符串
	 * 
	 * @param titleStr
	 */
	public void setSecurity(SpannableString text) {
		if (text != null) {
			mSecurityView.setText(text);
		}
	}

	/**
	 * 设置content字符串，采用SpannableString类型，用于实现个别字体的颜色不一样
	 * 
	 * @param titleStr
	 */
	public void setSpanSecurity(SpannableString titleStr) {
		if (titleStr != null) {
			mSecurityView.setText(titleStr);
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
				lListener.onClick(WifiConnectDialog.this, 0);
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
				lListener.onClick(WifiConnectDialog.this, 0);
			}
		});
	}
	
	public String getPwdContent() {
		return mPassword.getText().toString();
	}
	
	public void clearPwdEditText() {
		mPassword.setText("");
	}
	
	public int getBlackType() {
		int blackType = -1;
		switch (mBlackTypeId) {
		case R.id.wrong_pwd:
			blackType = WifiReport.TypeReported.WRONG_PWD;
			break;
		case R.id.not_safe:
			blackType = WifiReport.TypeReported.NOT_SAFE;
			break;
		case R.id.others:
			blackType = WifiReport.TypeReported.OTHERS;
			break;
		default:
			break;
		}
		return blackType;
	}
	
	public String getBlackContent() {
		return mBlackContent.getText().toString();
	}
	
	public void clearBlackContent() {
		mBlackContent.setText("");
	}

	private void initUI(DialogType dialogType) {
		View dlgView = LayoutInflater.from(mContext).inflate(R.layout.wifi_connect_dialog, null);

		LinearLayout wifiBasic = (LinearLayout)dlgView.findViewById(R.id.wifi_basic_info);
		mTitle = (TextView)dlgView.findViewById(R.id.wifi_title);
		mSecurityView = (TextView)dlgView.findViewById(R.id.security_text);
		mSignalView = (TextView)dlgView.findViewById(R.id.signal_strength_text);
		mRightBtn = (TextView)dlgView.findViewById(R.id.btn_connect);
		mLeftBtn = (TextView)dlgView.findViewById(R.id.btn_cancel);
		
		//password layout
		LinearLayout pwdLayout = (LinearLayout) dlgView.findViewById(R.id.Password);
		mPassword = (ChangingAwareEditText)dlgView.findViewById(R.id.Password_EditText);
		mPwdCheckBox = (CheckBox)dlgView.findViewById(R.id.ShowPassword_CheckBox);
		mPwdCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mPassword.setInputType(InputType.TYPE_CLASS_TEXT |
									  (isChecked ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
									  :InputType.TYPE_TEXT_VARIATION_PASSWORD));
				
			}
		});
		
		//report layout
		LinearLayout reportLayout = (LinearLayout)dlgView.findViewById(R.id.report);
		mBlackContent = (EditText)dlgView.findViewById(R.id.report_reason);
		RadioGroup radioGroup = (RadioGroup)dlgView.findViewById(R.id.report_group);
		mBlackTypeId = radioGroup.getCheckedRadioButtonId();
		radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int ButtonIdx) {
				mBlackTypeId = group.getCheckedRadioButtonId();
			}
		});

		//display ui
		switch (dialogType) {
		case DEFAULT:
			wifiBasic.setVisibility(View.VISIBLE);
			pwdLayout.setVisibility(View.GONE);
			reportLayout.setVisibility(View.GONE);
			break;
		case PASSWORD:
			wifiBasic.setVisibility(View.VISIBLE);
			pwdLayout.setVisibility(View.VISIBLE);
			reportLayout.setVisibility(View.GONE);
			break;
		case REPORT:
			wifiBasic.setVisibility(View.GONE);
			pwdLayout.setVisibility(View.GONE);
			reportLayout.setVisibility(View.VISIBLE);
			break;

		default:
			break;
		}
		if (dialogType == DialogType.PASSWORD) {
			pwdLayout.setVisibility(View.VISIBLE);
		} else {
			
		}
		
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
