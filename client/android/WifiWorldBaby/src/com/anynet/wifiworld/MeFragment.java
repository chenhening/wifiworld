package com.anynet.wifiworld;

import java.util.HashMap;
import java.util.regex.Pattern;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import cn.bmob.v3.Bmob;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import com.anynet.wifiworld.MainActivity.MainFragment;
import com.anynet.wifiworld.R;

import cn.smssdk.gui.RegisterPage;

public class MeFragment extends MainFragment{
	private String BMOB_KEY = "b20905c46c6f0ae1edee547057f04589";
	private String SMSSDK_KEY = "5ea9dee43eb2";
	private String SMSSDK_SECRECT = "6f332e8768e0fe21509cddbe804f016b";
	
	private void bingdingTitleUI() {

		mTitlebar.ivHeaderLeft.setVisibility(View.INVISIBLE);
		mTitlebar.llFinish.setVisibility(View.VISIBLE);
		mTitlebar.llHeaderMy.setVisibility(View.INVISIBLE);
		mTitlebar.tvHeaderRight.setVisibility(View.INVISIBLE);
		mTitlebar.tvTitle.setText(getString(R.string.my));

	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        // 初始化 Bmob SDK
        Bmob.initialize(getActivity(), BMOB_KEY);
        // initialize sms
        SMSSDK.initSDK(getActivity(), SMSSDK_KEY, SMSSDK_SECRECT);
        final Handler handler = new Handler();
		EventHandler eventHandler = new EventHandler() {
			public void afterEvent(int event, int result, Object data) {
				Message msg = new Message();
				msg.arg1 = event;
				msg.arg2 = result;
				msg.obj = data;
				handler.sendMessage(msg);
			}
		};
		// 注册回调监听接口
		SMSSDK.registerEventHandler(eventHandler);
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		//hybrid
		mPageRoot = inflater.inflate(R.layout.fragment_me, null);
		mPageRoot.findViewById(R.id.button_sms).setOnClickListener(btn_listener_);
		mPageRoot.findViewById(R.id.button_login).setOnClickListener(btn_listener_);
		//SystemWebView webView = (SystemWebView)mPageRoot.findViewById(R.id.cordovaWebView);
        //cordovaWebView = new CordovaWebViewImpl(getActivity(), new SystemWebViewEngine(webView));
        //Config.init(this.getActivity());
        //cordovaWebView.init(this, Config.getPluginEntries(), Config.getPreferences());
        //cordovaWebView.loadUrl("file:///android_asset/www/index.html");
		super.onCreateView(inflater, container, savedInstanceState);
		bingdingTitleUI();
		return mPageRoot;
	}
	
	//---------------------------------------------------------------------------------------------
	//for click event
	View.OnClickListener btn_listener_ = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.button_sms:
				EditText ett_username = (EditText)(mPageRoot.findViewById(R.id.editText_username));
				String phone_number = ett_username.getText().toString();
				Pattern pattern = Pattern.compile("^1[3|4|5|7|8][0-9]{9}$");
				if (!pattern.matcher(phone_number).find()) {
					showToast("请输入11位手机正确号码.");
					return;
				}
				// 打开注册页面
				RegisterPage registerPage = new RegisterPage();
				registerPage.setRegisterCallback(new EventHandler() {
					public void afterEvent(int event, int result, Object data) {
						// 解析注册结果
						if (result == SMSSDK.RESULT_COMPLETE) {
							@SuppressWarnings("unchecked")
							HashMap<String,Object> phoneMap = (HashMap<String, Object>) data;
							String country = (String) phoneMap.get("country");
							String phone = (String) phoneMap.get("phone");
							// 提交用户信息
							//registerUser(country, phone);
						}
					}
				});
				registerPage.show(getActivity().getApplicationContext());
				break;
			case R.id.button_login:
				break;
			}
		}
		
	};
}
