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
 * @author
 *
 */

package com.anynet.wifiworld.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;

public class ANBaseDialog extends Dialog {

	private Context mCtx;
	
	private Object mTag;
	
	public ANBaseDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		mCtx = context;
	}

	public ANBaseDialog(Context context, int theme) {
		super(context, theme);
		mCtx = context;
	}

	public ANBaseDialog(Context context) {
		super(context);
		mCtx = context;
	}	

	@Override
	public void dismiss() {

		try {
			
			if (mCtx instanceof Activity) {
				Activity aty = (Activity) mCtx;
				boolean isFinishing = aty.isFinishing();
				if (!isFinishing) {
					super.dismiss();
				}
			} else {
				if (isShowing()) {
					super.dismiss();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void show(){
		try {			
			Activity activity = this.getOwnerActivity();
			if (mCtx instanceof Activity) {
				activity = (Activity)mCtx;
			}
			if ( activity != null ){
				boolean isFinishing = activity.isFinishing();
				if (!isFinishing && activity.getWindow() != null) {
					super.show();
				}
			}else{
				super.show();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setTag(Object tag) {
		mTag = tag;
	}
	
	public Object getTag(){
		return mTag;
	}
}
