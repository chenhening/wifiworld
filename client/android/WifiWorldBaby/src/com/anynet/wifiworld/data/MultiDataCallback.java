package com.anynet.wifiworld.data;

import java.util.List;

public interface MultiDataCallback<T> {
	public boolean onSuccess(List<T> objects);
	public boolean onFailed(String msg);
}
