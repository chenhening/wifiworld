package com.anynet.wifiworld.data;

public interface DataCallback<T> {
	public void onSuccess(T object);
	public void onFailed(String msg);
}
