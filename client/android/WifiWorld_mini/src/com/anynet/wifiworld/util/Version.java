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
 * @brief get android sdk version.
 * @date 2015-06-04
 * @author Jason.Chen
 *
 */

package com.anynet.wifiworld.util;

import java.lang.reflect.Field;

import android.os.Build.VERSION;;

public class Version {
	
	public final static int SDK = get();
	
	@SuppressWarnings("deprecation")
	private static int get() {
		 final Class<VERSION> versionClass = VERSION.class;
		 try {
			 // First try to read the recommended field android.os.Build.VERSION.SDK_INT.
			final Field sdkIntField = versionClass.getField("SDK_INT");
			return sdkIntField.getInt(null);
		}catch (NoSuchFieldException e) {
			// If SDK_INT does not exist, read the deprecated field SDK.
			return Integer.valueOf(VERSION.SDK);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
