package com.anynet.wifiworld.me;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.cordova.Config;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewImpl;
import org.apache.cordova.engine.SystemWebView;
import org.apache.cordova.engine.SystemWebViewEngine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cn.bmob.v3.Bmob;

import com.anynet.wifiworld.R;

public class MeFragment extends Fragment  implements CordovaInterface {
	private View view_ = null;
	private CordovaWebView cordovaWebView = null; 
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
         // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
         // 初始化 Bmob SDK
        // 使用时请将第二个参数Application ID替换成你在Bmob服务器端创建的Application ID
        Bmob.initialize(getActivity(), "b20905c46c6f0ae1edee547057f04589");
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		//hybrid
		view_ = inflater.inflate(R.layout.fragment_me, null);
		//SystemWebView webView = (SystemWebView)view_.findViewById(R.id.cordovaWebView);
        //cordovaWebView = new CordovaWebViewImpl(getActivity(), new SystemWebViewEngine(webView));
        //Config.init(this.getActivity());
        //cordovaWebView.init(this, Config.getPluginEntries(), Config.getPreferences());
        //cordovaWebView.loadUrl("file:///android_asset/www/index.html");
		
		return view_;
	}

	@Override
	public ExecutorService getThreadPool() {
	    return Executors.newSingleThreadExecutor();
	}

	@Override
	public Object onMessage(String arg0, Object arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setActivityResultCallback(CordovaPlugin arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startActivityForResult(CordovaPlugin arg0, Intent arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
}
