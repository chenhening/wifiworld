package com.anynet.wifiworld.ui.main;

import java.util.ArrayList;
import java.util.List;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.wifi.WifiInfoScanned;
import com.anynet.wifiworld.wifi.WifiListAdapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.ListView;

public class WifiFragment extends Fragment {
	private final static String TAG = "WifiFragment";
	
	private View mView;
	private ListView mWifiListView;
	private WifiListAdapter mWifiListAdapter;
	private List<WifiInfoScanned> mWifiList;

	//list:数据集合
	private List<String> mListName = new ArrayList<String>();
	//listTag:Tag集合，其中Tag是分类的分割标签，每个分组的header
	private List<String> mListTag = new ArrayList<String>();
	 
	public void setData(){
		mListName.add("Free");
		mListTag.add("Free");
	        for(int i=0;i<3;i++){
	        	mListName.add("Fuck You"+i);
	        }
	        mListName.add("Advance");
	        mListTag.add("Advance");
	        for(int i=0;i<3;i++){
	        	mListName.add("Shit Binfei"+i);
	        }
	        mListName.add("Love");
	        mListTag.add("Love");
	        for(int i=0;i<30;i++){
	        	mListName.add("Jason"+i);
	        }
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setData();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i(TAG, "onCreateView");
		mView = inflater.inflate(R.layout.fragment_wifi, null);
		mWifiListView = (ListView) mView.findViewById(R.id.wifi_list_view);
		
		mWifiList = new ArrayList<WifiInfoScanned>();
		mWifiListAdapter = new WifiListAdapter(this.getActivity(), mListName, mListTag);
		mWifiListView.setAdapter(mWifiListAdapter);
		
		return mView;
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
}
