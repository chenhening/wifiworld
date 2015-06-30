package com.anynet.wifiworld.data;

import java.io.Serializable;
import java.util.List;

import android.content.Context;
import android.util.Log;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class WifiComments extends BmobObject implements Serializable {
	private final static String TAG = WifiComments.class.getSimpleName();
	private final static String MACADDR_TAG = "MacAddr";
	
	private static final long serialVersionUID = 1L;
	
	public String MacAddr; //wifi的唯一标识
	public String Comment; //评论的内容
	public String UserId;  //评论的用户账号昵称
	public String UserAccount;  //评论的用户账号
	public long SendTime; //用户登陆网络的时间
	public int Type;
	
	public static class CommentType {
		public static int COMMENT_GOOD = 0; //好评
		public static int COMMENT_BAD = 1; //差评
	}
	
	public void StoreRemote(final Context context, DataCallback<WifiComments> callback) {
		final DataCallback<WifiComments> _callback = callback;
		final WifiComments user = this;
		user.save(context, new SaveListener() {
			
			@Override
			public void onSuccess() {
				_callback.onSuccess(user);
			}

			@Override
			public void onFailure(int code, String msg) {
				_callback.onFailed(msg);
			}
		});
	}
	
	public void QueryByMacAddress(final Context context, final String Mac, MultiDataCallback<WifiComments> callback) {
		final MultiDataCallback<WifiComments> _callback = callback;
		final BmobQuery<WifiComments> query = new BmobQuery<WifiComments>();
		query.addWhereEqualTo(MACADDR_TAG, Mac);
		Log.d(TAG, "Start to query wifi comments table for:" + Mac);
		new Thread(new Runnable() {

			@Override
			public void run() {
				query.findObjects(context, new FindListener<WifiComments>() {
					@Override
					public void onSuccess(List<WifiComments> object) {
						_callback.onSuccess(object);
					}

					@Override
					public void onError(int code, String msg) {
						_callback.onFailed(msg);
					}
				});
				Log.d(TAG, "Finish to query wifi comments");
			}

		}).start();
	}
	
	public void MarkSendTime() {
		SendTime = System.currentTimeMillis();
	}
}
