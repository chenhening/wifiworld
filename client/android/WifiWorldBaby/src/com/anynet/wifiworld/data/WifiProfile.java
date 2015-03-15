package com.anynet.wifiworld.data;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class WifiProfile extends BmobObject{

	private static final long serialVersionUID = 1L;

	public String MacAddr; //mac地址, 唯一建
	public String Ssid; //wifi的ssid
	public String Password; //wifi的密码，经过base64后保存
	public String Sponser; //绑定的用户账号，wifi提供者
	public WifiType Type; //wifi的类型
	public Bitmap Logo; //用户自定义的logo信息
	public String Alias; //用户自定义的wifi别名
	public BmobGeoPoint Geometry; //WiFi的地理位置
	public String Banner; //wifi的展示页内容(TODO(binfei)还需要定义更多)
	public float Income; //wifi 收入
	public Long ctime; //添加时间戳——使用Unix时间戳
	
	private String unique_key = "MacAddr";
	
    private Context globalContext = null;
    private WifiProfile itself = this;

 // ------------------------------------------------------------------------------------------------
    public void init(Context context) {
    	this.globalContext = context;
    }
    
	public void QueryByMacAddress(String Mac, DataCallback<WifiProfile> callback) {
		final DataCallback<WifiProfile> _callback = callback;
		BmobQuery<WifiProfile> query = new BmobQuery<WifiProfile>();
		query.addWhereEqualTo(unique_key, Mac);
		query.findObjects(globalContext, new FindListener<WifiProfile>() {
			@Override
			public void onSuccess(List<WifiProfile> object) {
				// TODO Auto-generated method stub
				if (object.size() == 1) {
					itself = object.get(0);
					_callback.onSuccess(itself);
				} else {
					_callback.onFailed("");
				}
			}

			@Override
			public void onError(int code, String msg) {
				_callback.onFailed(msg);
			}
		});
	}
	
	public void StoreRemote(DataCallback<WifiProfile> callback) {
		final DataCallback<WifiProfile> _callback = callback;
		//先查询，如果有数据就更新，否则增加一条新记录
		QueryByMacAddress(itself.MacAddr, new DataCallback<WifiProfile>() {

			@Override
			public void onSuccess(WifiProfile object) {
				object.update(globalContext, new UpdateListener() {

					@Override
					public void onSuccess() {
						_callback.onSuccess(itself);
					}
					
					@Override
					public void onFailure(int arg0, String msg) {
						_callback.onFailed(msg);
					}
				});
			}

			@Override
			public void onFailed(String msg) {
				_callback.onFailed(msg);
				itself.save(globalContext, new SaveListener() {

					@Override
					public void onSuccess() {
						_callback.onSuccess(itself);
					}

					@Override
					public void onFailure(int code, String msg) {
						_callback.onFailed(msg);
					}
				});
			}
			
		});
	}
}
