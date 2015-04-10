package com.anynet.wifiworld.data;

import java.util.List;

public interface DataCallback<T> {
	public void onSuccess(T object);
	public void onFailed(String msg);
}
