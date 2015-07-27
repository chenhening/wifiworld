package com.anynet.wifiworld.wifi.ui;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import cn.hugo.android.scanner.CaptureActivity;
import cn.hugo.android.scanner.decode.EncodingHandler;

import com.anynet.wifiworld.BaseActivity;
import com.anynet.wifiworld.R;
import com.anynet.wifiworld.me.UserLoginActivity;
import com.anynet.wifiworld.data.DataCallback;
import com.anynet.wifiworld.data.MultiDataCallback;
import com.anynet.wifiworld.data.WifiComments;
import com.anynet.wifiworld.data.WifiDynamic;
import com.anynet.wifiworld.data.WifiFollow;
import com.anynet.wifiworld.data.WifiMessages;
import com.anynet.wifiworld.data.WifiProfile;
import com.anynet.wifiworld.data.WifiKnock;
import com.anynet.wifiworld.data.WifiRank;
import com.anynet.wifiworld.data.WifiReport;
import com.anynet.wifiworld.knock.KnockStepFirstActivity;
import com.anynet.wifiworld.provider.WifiProviderRigisterActivity;
import com.anynet.wifiworld.provider.WifiProviderSettingActivity;
import com.anynet.wifiworld.util.LoginHelper;
import com.anynet.wifiworld.util.StringCrypto;
import com.anynet.wifiworld.util.UIHelper;
import com.anynet.wifiworld.wifi.WifiAdmin;
import com.anynet.wifiworld.dialog.WifiConnectDialog;
import com.anynet.wifiworld.wifi.WifiListItem;
import com.google.zxing.WriterException;

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
	
	private String mSSID;
	private String mMacid;
	
	private PopupWindow popupwindow;
	
	private void bingdingTitleUI() {
		mTitlebar.tvTitle.setText(WifiAdmin.convertToNonQuotedString(mSSID));
		mTitlebar.ivHeaderRight.setVisibility(View.VISIBLE);
		mTitlebar.ivHeaderRight.setImageResource(R.drawable.selector_wifi_plus);
		mTitlebar.ivHeaderRight.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (popupwindow != null && popupwindow.isShowing()) {
	                popupwindow.dismiss();
	            } else {
	                popupwindow.showAsDropDown(v, 0, 16);
	            }
			}
		});
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mContext = this;
		setContentView(R.layout.activity_wifi_details);
		super.onCreate(savedInstanceState);
		
		//init UI
		initMorePopWindows();
		mLogo = (ImageView)findViewById(R.id.iv_detail_wifi_logo);
		mAlias = (TextView)findViewById(R.id.tv_detail_wifi_name);
		mSponser = (TextView)findViewById(R.id.tv_detail_wifi_master);
		mBanner = (TextView)findViewById(R.id.tv_detail_wifi_banner);
		mRank = (TextView)findViewById(R.id.tv_detail_wifi_rank);
		mConnectTimes = (TextView)findViewById(R.id.tv_detail_wifi_times);
		mMessages = (TextView)findViewById(R.id.tv_detail_wifi_messages);
		mListComments = (ListView) findViewById(R.id.lv_detail_wifi_comments);
		
		//get intent data
		Intent intent = getIntent();
		mWifi = (WifiProfile) intent.getSerializableExtra(WifiProfile.TAG);
		if (mWifi == null) { //重用了detail的页面显示未认证wifi的详细信息
			List<String> data = intent.getStringArrayListExtra(WifiNotAuthListAdapter.TAG);
			mSSID = data.get(0);
			mMacid = data.get(1);
			TextView txt_title = (TextView) findViewById(R.id.tv_detail_knock_title);
			txt_title.setText("寻找网络主人");
			TextView txt_desc = (TextView) findViewById(R.id.tv_detail_knock_desc);
			txt_desc.setText("留下线索一起寻找主人吧！");
			Button btn_knock = (Button) findViewById(R.id.btn_knock_answer);
			btn_knock.setText("留下线索");
			btn_knock.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					WifiDetailsActivity.this.showToast("寻找网络主人功能正在完善中，感谢您的期待");
				}
				
			});
			
		} else {
			mSSID = mWifi.Ssid;
			mMacid = mWifi.MacAddr;
			mLogo.setImageBitmap(mWifi.getLogo());
			mAlias.setText(mWifi.Alias);
			//正则隐藏手机号中间4位
			mSponser.setText(mWifi.Sponser.replaceAll("(?<=\\d{3})\\d(?=\\d{3})", "*"));
			mBanner.setText(mWifi.Banner);
			
			//敲门
			findViewById(R.id.btn_knock_answer).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(final View v) {
					v.setEnabled(false);
					// 拉取敲门问题
					final WifiKnock wifiQuestions = new WifiKnock();
					wifiQuestions.QueryByMacAddress(getApplicationContext(), mWifi.MacAddr,
						new DataCallback<WifiKnock>() {
		
							@Override
							public void onSuccess(WifiKnock object) {
								KnockStepFirstActivity.start(mContext, "WifiDetailsActivity", object);
								v.setEnabled(true);
							}
		
							@Override
							public void onFailed(String msg) {
								KnockStepFirstActivity.start(mContext, "WifiDetailsActivity", wifiQuestions);
								v.setEnabled(true);
							}
					});
				}
			});
		}
		
		bingdingTitleUI();
		pullDataFromDB();
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
        		UIHelper.setListViewHeight(mListComments);
        		break;
        	}
        };  
    };
    
	private void pullDataFromDB() {
		WifiRank wifiRank = new WifiRank();
		wifiRank.QueryByMacAddress(this, mMacid, new DataCallback<WifiRank>() {
			
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
		wifiDynamic.QueryConnectedTimes(this, mMacid, new DataCallback<Long>() {

			@Override
			public void onSuccess(Long object) {
				Message message = Message.obtain();  
                message.obj = "" + object;  
                message.what = MSG_TIMES_READY;  
                mHandler.sendMessage(message);
			}

			@Override
			public void onFailed(String msg) {
				
			}
		});
		
		WifiMessages wifiMessages = new WifiMessages();
		wifiMessages.QueryByMacAddress(this, mMacid, new DataCallback<WifiMessages>() {
			
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
		wifiComments.QueryByMacAddress(this, mMacid, new MultiDataCallback<WifiComments>() {
			
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
	
	private void initMorePopWindows() {
        if (popupwindow == null) {
    		// 获取自定义布局文件pop.xml的视图  
			final LayoutInflater layoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View customView = layoutInflater.inflate(R.layout.popupwindow_detail_more, null, false);  
			popupwindow = new PopupWindow(customView);
    		popupwindow.setWidth(LayoutParams.WRAP_CONTENT);    
    		popupwindow.setHeight(LayoutParams.WRAP_CONTENT); 
    		popupwindow.setTouchable(true);
    		popupwindow.setFocusable(true);
    		popupwindow.setBackgroundDrawable(new BitmapDrawable());
    		popupwindow.setAnimationStyle(R.style.PopupFadeAnimation);
    		popupwindow.setOutsideTouchable(true);
                
    		//认证
    		customView.findViewById(R.id.ll_detail_more_shoucang).setOnClickListener(new OnClickListener() {

        		@Override
	            public void onClick(View v) {
        			popupwindow.dismiss();
        			if (!LoginHelper.getInstance(getBaseContext()).isLogined()) {
    					UserLoginActivity.start((BaseActivity) mContext);
    					return;
    				}
    				
        			WifiFollow wifiFollow = new WifiFollow();
					wifiFollow.MacAddr = mMacid;
					wifiFollow.Userid = LoginHelper.getInstance(mContext).getCurLoginUserInfo().getUsername();
					wifiFollow.MarkFollowTime();
					
					wifiFollow.FollowWifi(getBaseContext(), new DataCallback<WifiFollow>() {

						@Override
						public void onSuccess(WifiFollow object) {
							((BaseActivity) mContext).showToast("收藏当前网络成功");
						}

						@Override
						public void onFailed(String msg) {
							Log.e(TAG, "Failed to follow current WiFi: " + msg);
						}
						
					});
	            }
	                	
	        });	
	    		
    		//评论
    		customView.findViewById(R.id.ll_detail_more_jubao).setOnClickListener(new OnClickListener() {

        		@Override
	            public void onClick(View v) {
        			popupwindow.dismiss();
        			if (!LoginHelper.getInstance(getBaseContext()).isLogined()) {
    					UserLoginActivity.start((BaseActivity) mContext);
    					return;
    				}
        			final WifiConnectDialog wifiConnectDialog = new WifiConnectDialog(mContext, WifiConnectDialog.DialogType.REPORT);
        			wifiConnectDialog.setTitle("举报当前网络");
        			wifiConnectDialog.setDefaultContent("是否举报当前网络");
        			wifiConnectDialog.setLeftBtnStr("取消");
        			wifiConnectDialog.setRightBtnStr("确定");

        			wifiConnectDialog.setRightBtnListener(new DialogInterface.OnClickListener() {
        				public void onClick(DialogInterface dialog, int which) {
        					WifiReport wifiBlack = new WifiReport();
        					wifiBlack.MacAddr = mMacid;
        					wifiBlack.Userid = LoginHelper.getInstance(getBaseContext()).getCurLoginUserInfo().getUsername();
        					wifiBlack.ReportType = 1;
        					wifiBlack.Content = "密码不正确";
        					wifiBlack.MarkReportTime();

        					wifiBlack.ReportWifi(getBaseContext(), new DataCallback<WifiReport>() {

        						@Override
        						public void onSuccess(WifiReport object) {
        							((BaseActivity) mContext).showToast("举报当前网络成功");
        						}

        						@Override
        						public void onFailed(String msg) {
        							Log.e(TAG, "Failed to report WiFi:" + msg);
        						}

        					});
        					
        					dialog.dismiss();
        				}
        			});

        			wifiConnectDialog.show();
	            }
	                	
	        });
		}
	}
	
}
