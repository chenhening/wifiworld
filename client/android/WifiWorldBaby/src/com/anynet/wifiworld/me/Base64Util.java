package com.anynet.wifiworld.me;

import android.util.Base64;

public class Base64Util {
 

    /**
     * 将数据编码为BASE64字符串
     * @param binaryData
     * @return
     */
    public static String encode(String src) {
    	byte[] input = src.getBytes();
        return Base64.encodeToString(input, Base64.DEFAULT);
    }
}
