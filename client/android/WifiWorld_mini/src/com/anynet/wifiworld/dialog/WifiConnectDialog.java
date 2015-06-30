/*
 * Copyright 2015 Anynet Corporation All Rights Reserved.
 *
 * The source code contained or described herein and all documents related to
 * the source code ("Material") are owned by Anynet Corporation or its suppliers
 * or licensors. Title to the Material remains with Anynet Corporation or its
 * suppliers and licensors. The Material contains trade secrets and proprietary
 * and confidential information of Anynet or its suppliers and licensors. The
 * Material is protected by worldwide copyright and trade secret laws and
 * treaty provisions.
 * No part of the Material may be used, copied, reproduced, modified, published
 * , uploaded, posted, transmitted, distributed, or disclosed in any way
 * without Anynet's prior express written permission.
 *
 * No license under any patent, copyright, trade secret or other intellectual
 * property right is granted to or conferred upon you by disclosure or delivery
 * of the Materials, either expressly, by implication, inducement, estoppel or
 * otherwise. Any license under such intellectual property rights must be
 * express and approved by Anynet in writing.
 *
 * @brief ANLog is the custom log for wifiworld project.
 * @date 2015-06-04
 * @author Jason.Chen
 *
 */

package com.anynet.wifiworld.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.dialog.ANBaseDialog;
import com.anynet.wifiworld.wifi.ChangingAwareEditText;

public class WifiConnectDialog extends ANBaseDialog {
	public static final String TAG = WifiConnectDialog.class.getSimpleName();
	
	public enum DialogType {
		DEFAULT,
		PASSWORD
	};
	
	private Context mContext;

	private TextView mTitle;
	private TextView mLeftBtn;
	private TextView mRightBtn;
	private ImageView mLeftIcon;
	private Object mUserData;
	
	private LinearLayout mDefaultLayout;
	private TextView mConnectTip;
	
	private LinearLayout mPwdLayout;
	private ChangingAwareEditText mPassword;
	private CheckBox mPwdCheckBox;

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
			mTitle.setText("提示");
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
	@SuppressWarnings("deprecation")
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
	
	public void setDefaultContent(String str) {
		mConnectTip.setText(str);
	}

	private void initUI(DialogType dialogType) {
		View dlgView = LayoutInflater.from(mContext).inflate(R.layout.dialog_wifi_connect, null);
		mTitle = (TextView)dlgView.findViewById(R.id.wifi_title);
		mRightBtn = (TextView)dlgView.findViewById(R.id.btn_connect);
		mLeftBtn = (TextView)dlgView.findViewById(R.id.btn_cancel);
		
		//default layout
		mDefaultLayout = (LinearLayout)dlgView.findViewById(R.id.ll_dialog_default);
		mConnectTip = (TextView)dlgView.findViewById(R.id.tv_connect_tip);
		
		//password layout
		mPwdLayout = (LinearLayout)dlgView.findViewById(R.id.ll_dialog_pwd);
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
		setDialogContent(dialogType);
		
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

	public void setDialogContent(DialogType dialogType) {
		switch (dialogType) {
		case DEFAULT:
			mDefaultLayout.setVisibility(View.VISIBLE);
			mPwdLayout.setVisibility(View.GONE);
			break;
		case PASSWORD:
			mDefaultLayout.setVisibility(View.GONE);
			mPwdLayout.setVisibility(View.VISIBLE);
			break;

		default:
			break;
		}
	}
}
