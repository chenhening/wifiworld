package com.anynet.wifiworld.me;

import com.anynet.wifiworld.R;

import cn.bmob.v3.Bmob;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class MeFragment extends Fragment {
	private View view_;
	private WebView webview_;
	@Override
	public void onCreate(Bundle savedInstanceState) {
         // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
         // 初始化 Bmob SDK
        // 使用时请将第二个参数Application ID替换成你在Bmob服务器端创建的Application ID
        Bmob.initialize(getActivity(), "b20905c46c6f0ae1edee547057f04589");
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view_ = inflater.inflate(R.layout.fragment_me, null);
		//hybrid 
		webview_ = (WebView) view_.findViewById(R.id.webview_me);
		webview_.loadUrl("file:///android_asset/www/index.html");
		
		return view_;
	}
}
