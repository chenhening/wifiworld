package com.anynet.wifiworld.me;

import cn.bmob.v3.Bmob;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public class MeFragment extends Fragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
         // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
         // 初始化 Bmob SDK
        // 使用时请将第二个参数Application ID替换成你在Bmob服务器端创建的Application ID
        Bmob.initialize(this.getActivity(), "b20905c46c6f0ae1edee547057f04589");
    }
}
