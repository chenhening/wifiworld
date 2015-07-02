package com.anynet.wifiworld.wifi.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.anynet.wifiworld.BaseActivity;
import com.anynet.wifiworld.R;
import com.anynet.wifiworld.data.DataCallback;
import com.anynet.wifiworld.data.MultiDataCallback;
import com.anynet.wifiworld.data.WifiComments;
import com.anynet.wifiworld.data.WifiDynamic;
import com.anynet.wifiworld.data.WifiMessages;
import com.anynet.wifiworld.data.WifiProfile;
import com.anynet.wifiworld.data.WifiQuestions;
import com.anynet.wifiworld.data.WifiRank;
import com.anynet.wifiworld.knock.KnockStepFirstActivity;
import com.anynet.wifiworld.util.BitmapUtil;
import com.anynet.wifiworld.util.UIHelper;

public class WifiDetailsActivity extends BaseActivity {
	private final static String TAG = WifiDetailsActivity.class.getSimpleName();
	
	private Context mContext;
	private WifiProfile mWifi;
	
	//UI
	private ImageView mLogo;
	private TextView mAlias;
	private TextView mSponser;
	private TextView mBanner;
	private TextView mRank;
	private TextView mConnectTimes;
	private TextView mMessages;
	private ListView mListComments;
	
	private void bingdingTitleUI() {
		mTitlebar.tvTitle.setText(mWifi.Ssid);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mContext = this;
		setContentView(R.layout.activity_wifi_details);
		super.onCreate(savedInstanceState);
		
		//get intent data
		Intent intent = getIntent();
		mWifi = (WifiProfile) intent.getSerializableExtra(WifiProfile.TAG);
		if (mWifi == null) {
			finish();
			return;
		}
		
		bingdingTitleUI();
		pullDataFromDB();
		
		//init UI
		mLogo = (ImageView)findViewById(R.id.iv_detail_wifi_logo);
		mLogo.setImageBitmap(BitmapUtil.Bytes2Bimap(mWifi.Logo));
		mAlias = (TextView)findViewById(R.id.tv_detail_wifi_name);
		mAlias.setText(mWifi.Alias);
		mSponser = (TextView)findViewById(R.id.tv_detail_wifi_master);
		mSponser.setText(mWifi.Sponser);
		mBanner = (TextView)findViewById(R.id.tv_detail_wifi_banner);
		mBanner.setText(mWifi.Banner);
		mRank = (TextView)findViewById(R.id.tv_detail_wifi_rank);
		mConnectTimes = (TextView)findViewById(R.id.tv_detail_wifi_times);
		mMessages = (TextView)findViewById(R.id.tv_detail_wifi_messages);
		mListComments = (ListView) findViewById(R.id.lv_detail_wifi_comments);
		
		//敲门
		findViewById(R.id.btn_knock_answer).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 拉取敲门问题
				final WifiQuestions wifiQuestions = new WifiQuestions();
				wifiQuestions.QueryByMacAddress(getApplicationContext(), mWifi.MacAddr,
					new DataCallback<WifiQuestions>() {

						@Override
						public void onSuccess(WifiQuestions object) {
							KnockStepFirstActivity.start(mContext, "WifiDetailsActivity", object);
						}

						@Override
						public void onFailed(String msg) {
							KnockStepFirstActivity.start(mContext, "WifiDetailsActivity", wifiQuestions);
						}
				});
			}
		});
	}

	protected Context getActivity() {
		return this;
	}

	@Override
	protected void onDestroy() {
		Log.d(TAG, "onDestroy");
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		Log.d(TAG, "onResume");
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onStart() {
		Log.d(TAG, "onStart");
		super.onStart();
	}
	
	@Override
	protected void onStop() {
		Log.d(TAG, "onSop");
		super.onStop();
	}
	
	//-----------------------------------------------------------------------------------------------------------------
    //custom functions
	private final static int MSG_RNAK_READY = 1;
	private final static int MSG_TIMES_READY = 2;
	private final static int MSG_MESSAGE_READY = 3;
	private final static int MSG_COMMENTS_READY = 4;
	private Handler mHandler = new Handler() {  
		  
        @Override  
        public void handleMessage(Message msg) {  
        	switch(msg.what) {
        	case MSG_RNAK_READY:
        		mRank.setText((String)msg.obj);
        		break;
        	case MSG_TIMES_READY:
        		mConnectTimes.setText((String)msg.obj);
        		break;
        	case MSG_MESSAGE_READY:
        		mMessages.setText((String)msg.obj);
        		break;
        	case MSG_COMMENTS_READY:
        		mListComments.setAdapter(new WifiCommentsListAdapter(mContext, (List<String>)msg.obj));
        		UIHelper.setListViewHeightBasedOnChildren(mListComments);
        		break;
        	}
        };  
    };
    
	private void pullDataFromDB() {
		WifiRank wifiRank = new WifiRank();
		wifiRank.QueryByMacAddress(this, mWifi.MacAddr, new DataCallback<WifiRank>() {
			
			@Override
			public void onSuccess(WifiRank object) {
				Message message = Message.obtain();  
                message.obj = "" + object.Rank;  
                message.what = MSG_RNAK_READY;  
                mHandler.sendMessage(message);
			}
			
			@Override
			public void onFailed(String msg) {
				
			}
		});
		
		WifiDynamic wifiDynamic = new WifiDynamic();
		wifiDynamic.QueryConnectedTimes(this, mWifi.MacAddr, new DataCallback<Long>() {

			@Override
			public void onSuccess(Long object) {
				Message message = Message.obtain();  
                message.obj = object + "次";  
                message.what = MSG_TIMES_READY;  
                mHandler.sendMessage(message);
			}

			@Override
			public void onFailed(String msg) {
				
			}
		});
		
		WifiMessages wifiMessages = new WifiMessages();
		wifiMessages.QueryByMacAddress(this, mWifi.MacAddr, new DataCallback<WifiMessages>() {
			
			@Override
			public void onSuccess(WifiMessages object) {
				Message message = Message.obtain();  
                message.obj = object.Message;  
                message.what = MSG_MESSAGE_READY;  
                mHandler.sendMessage(message);
			}
			
			@Override
			public void onFailed(String msg) {
				
			}
		});
		
		WifiComments wifiComments = new WifiComments();
		wifiComments.QueryByMacAddress(this, mWifi.MacAddr, new MultiDataCallback<WifiComments>() {
			
			@Override
			public boolean onSuccess(List<WifiComments> object) {
				List<String> comment_item = new ArrayList<String>();
				for (WifiComments item : object) {
					comment_item.add(item.Comment);
				}
				Message message = Message.obtain();  
                message.obj = comment_item;  
                message.what = MSG_COMMENTS_READY;  
                mHandler.sendMessage(message);
				return false;
			}
			
			@Override
			public boolean onFailed(String msg) {
				return false;
			}
		});
	}
}
