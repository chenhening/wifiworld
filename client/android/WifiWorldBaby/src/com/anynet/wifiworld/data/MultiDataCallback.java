package com.anynet.wifiworld.data;

import java.util.List;

public interface MultiDataCallback<T> {
	public void onSuccess(List<T> objects);
	public void onFailed(String msg);
}
