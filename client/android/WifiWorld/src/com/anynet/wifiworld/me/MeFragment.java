package com.anynet.wifiworld.me;

import java.util.regex.Pattern;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cn.bmob.v3.Bmob;

import com.anynet.wifiworld.R;

public class MeFragment extends Fragment{
	private View view_ = null;
	
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
		view_.findViewById(R.id.button_sms).setOnClickListener(btn_listener_);
		view_.findViewById(R.id.button_login).setOnClickListener(btn_listener_);
		//SystemWebView webView = (SystemWebView)view_.findViewById(R.id.cordovaWebView);
        //cordovaWebView = new CordovaWebViewImpl(getActivity(), new SystemWebViewEngine(webView));
        //Config.init(this.getActivity());
        //cordovaWebView.init(this, Config.getPluginEntries(), Config.getPreferences());
        //cordovaWebView.loadUrl("file:///android_asset/www/index.html");
		
		return view_;
	}
	
	//---------------------------------------------------------------------------------------------
	//for click event
	View.OnClickListener btn_listener_ = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.button_sms:
				EditText ett_username = (EditText)(view_.findViewById(R.id.editText_username));
				String phone_number = ett_username.getText().toString();
				Pattern pattern = Pattern.compile("/^1[3|5|7|8][0-9]//d{4,8}$/");
				if (!pattern.matcher(phone_number).find()) {
					String message = "请输入11位手机正确号码.";
					Toast.makeText(getActivity().getApplicationContext(), message, message.length()).show();
					return;
				}
				break;
			case R.id.button_login:
				break;
			}
		}
		
	};
}
