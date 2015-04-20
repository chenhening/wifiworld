package com.anynet.wifiworld.data;

import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import cn.bmob.v3.BmobRealTimeData;
import cn.bmob.v3.listener.ValueEventListener;

public class DataListenerHelper {
	
	private static final String TAG = "DataListenerHelper";

	//private static DataListenerHelper mInstance;
	
	private Context mContext = null;
	private BmobRealTimeData mBrtd = new BmobRealTimeData();
	//private Map<String, MultiDataCallback<BmobObject>> mCallbacks = new HashMap<String, MultiDataCallback<BmobObject>>();

	//public static DataListenerHelper getInstance(Context context) {
	//	if (null == mInstance) {
	//		mInstance = new DataListenerHelper(context);
	//	}
	//	return mInstance;
	//}
	
	public DataListenerHelper(Context context) {
		mContext = context;
		//start();
	}
	
	public void start(Context context) {
		mBrtd.start(mContext, new ValueEventListener() {
		    @Override
		    public void onDataChange(JSONObject data) {
		        Log.d(TAG, "("+data.optString("action")+")"+"数据："+data);
		        JSONObject object = data.optJSONObject("data");
		        //mCallbacks.get(object.optJSONObject("objectId"));
		    }

		    @Override
		    public void onConnectCompleted() {
		        Log.d(TAG, "连接成功:"+mBrtd.isConnected());
		    }
		});
	}
	
	public static enum Type {
		UPDATE,
		DELETE
	}
	
	
}
