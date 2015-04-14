package com.anynet.wifiworld.wifi.ui;

import java.io.ByteArrayOutputStream;
import java.util.List;

import com.anynet.wifiworld.MainActivity;
import com.anynet.wifiworld.R;
import com.anynet.wifiworld.R.color;
import com.anynet.wifiworld.UserLoginActivity;
import com.anynet.wifiworld.app.BaseActivity;
import com.anynet.wifiworld.data.DataCallback;
import com.anynet.wifiworld.data.MultiDataCallback;
import com.anynet.wifiworld.data.WifiBlack;
import com.anynet.wifiworld.data.WifiComments;
import com.anynet.wifiworld.data.WifiDynamic;
import com.anynet.wifiworld.data.WifiFollow;
import com.anynet.wifiworld.data.WifiMessages;
import com.anynet.wifiworld.data.WifiProfile;
import com.anynet.wifiworld.data.WifiQuestions;
import com.anynet.wifiworld.knock.KnockStepFirstActivity;
import com.anynet.wifiworld.util.LoginHelper;
import com.anynet.wifiworld.wifi.WifiInfoScanned;
import com.anynet.wifiworld.wifi.WifiListHelper;
import com.avos.avoscloud.LogUtil.log;

import android.R.integer;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class WifiDetailsActivity extends BaseActivity {
	private final static String TAG = WifiDetailsActivity.class.getSimpleName();
	private final static int GET_DATA_DEFAULT = 0;
	private final static int GET_DATA_FAILED = 1;
	private final static int GET_DATA_SUCCESS = 2;
	
	private Context mContext;
	
	private WifiInfoScanned mWifiInfoScanned;
	
	private ViewGroup mLayoutRoot;
	private ProgressBar mProcessBar;
	private TextView mRankText;
	private TextView mRateText;
	private TextView mConnectedCnt;
	private TextView mConnectedTime;
	private TextView mWifiMessage;
	private ListView mListComments;
	private TextView mFollowView;
	private TextView mBlackView;
	
	private Handler mUpdateViewHandler = new Handler();
	private Runnable mMonitorDataRunnable = null;
	private int mDynamicFlag = GET_DATA_DEFAULT;
	private int mMessagesFlag = GET_DATA_DEFAULT;
	private int mCommentsFlag = GET_DATA_DEFAULT;
	private int mFollowFlag = GET_DATA_DEFAULT;
	private WifiFollow mFollowed = null;
	
	private void bingdingTitleUI() {
		mTitlebar.ivHeaderLeft.setVisibility(View.VISIBLE);
		mTitlebar.tvTitle.setText(getString(R.string.my));
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_wifi_details);
		super.onCreate(savedInstanceState);
		bingdingTitleUI();
		mContext = this;
		
		//Get intent data
		Intent intent = getIntent();
		mWifiInfoScanned = (WifiInfoScanned) intent.getSerializableExtra("WifiSelected");
		getWifiProfiles(mWifiInfoScanned);
		//Set title text and back button listener
		mTitlebar.tvTitle.setText(mWifiInfoScanned.getWifiName());
		mTitlebar.ivHeaderLeft.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		
		mRankText = (TextView)findViewById(R.id.wifi_account_rank);
		mRateText = (TextView)findViewById(R.id.wifi_rate);
		mConnectedCnt = (TextView)findViewById(R.id.wifi_connect_times_num);
		mConnectedTime = (TextView)findViewById(R.id.wifi_connect_time_num);
		
		mWifiMessage = (TextView)findViewById(R.id.wifi_messages_content);
		//Set WiFi logo image
//		if (wifiSelected.getWifiLogo() != null) {
//			ImageView logo = (ImageView)findViewById(R.id.wifi_account_portral);
//			logo.setImageBitmap(wifiSelected.getWifiLogo());
//		}
		
		mListComments = (ListView) findViewById(R.id.wifi_list_comments);
		
		//Process WiFi follow
		mFollowView = (TextView) findViewById(R.id.wifi_follow);
		mFollowView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (!LoginHelper.getInstance(getBaseContext()).isLogined()) {
					UserLoginActivity.start((BaseActivity) mContext);
					return;
				}
				
				if (mFollowed != null) {
					mFollowed.CancelFollow(getBaseContext(), new DataCallback<WifiFollow>() {

						@Override
						public void onSuccess(WifiFollow object) {
							Log.i(TAG, "Success to cancel follow current WiFi");
							mFollowView.setText("收藏WiFi");
							mFollowView.setBackgroundColor(color.orange);
							mFollowed = null;
						}

						@Override
						public void onFailed(String msg) {
							Log.e(TAG, "Failed to cancel follow current WiFi");
							
						}
						
					});
				} else {
					WifiFollow wifiFollow = new WifiFollow();
					wifiFollow.MacAddr = mWifiInfoScanned.getWifiMAC();
					wifiFollow.Userid = LoginHelper.getInstance(getBaseContext()).getCurLoginUserInfo().PhoneNumber;
					wifiFollow.MarkFollowTime();
					
					wifiFollow.FollowWifi(getBaseContext(), new DataCallback<WifiFollow>() {

						@Override
						public void onSuccess(WifiFollow object) {
							Log.i(TAG, "Success to follow current WiFi");
							mFollowView.setText("取消收藏");
							mFollowView.setBackgroundColor(color.gray);
							mFollowed = object;
						}

						@Override
						public void onFailed(String msg) {
							Log.e(TAG, "Failed to follow current WiFi");
							
						}
						
					});
				}
			}
		});
		
		// Process WiFi black report
		mBlackView = (TextView) findViewById(R.id.wifi_black);
		mBlackView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (!LoginHelper.getInstance(getBaseContext()).isLogined()) {
					UserLoginActivity.start((BaseActivity) mContext);
					return;
				}
				WifiBlack wifiBlack = new WifiBlack();
				wifiBlack.MacAddr = mWifiInfoScanned.getWifiMAC();
				wifiBlack.Userid = LoginHelper.getInstance(getBaseContext())
						.getCurLoginUserInfo().PhoneNumber;
				wifiBlack.NotSafe = true;
				wifiBlack.others = "shit, shit";
				wifiBlack.MarkReportTime();

				wifiBlack.ReportWifi(getBaseContext(), new DataCallback<WifiBlack>() {

							@Override
							public void onSuccess(WifiBlack object) {
								Log.i(TAG, "Success to report WiFi");
							}

							@Override
							public void onFailed(String msg) {
								Log.e(TAG, "Failed to report WiFi:" + msg);
							}

						});
			}
		});
		
		pullDataFromDB();
		initializeProcessBar();
		monitorDataDownload();
		
		//敲门
		if (LoginHelper.getInstance(this).mKnockList.contains(mWifiInfoScanned.getWifiMAC())) {
			((TextView)this.findViewById(R.id.tv_wifi_used_addr)).setHint("已经敲过门，可直接使用网络");
			this.findViewById(R.id.ll_wifi_knock).setEnabled(false);
		} else {
			this.findViewById(R.id.ll_wifi_knock).setOnClickListener(new OnClickListener() {
	
				@Override
				public void onClick(View arg0) {
					//拉取敲门问题
					WifiQuestions wifiQuestions = new WifiQuestions();
					wifiQuestions.QueryByMacAddress(getApplicationContext(), mWifiInfoScanned.getWifiMAC(), new DataCallback<WifiQuestions>() {
						
						@Override
						public void onSuccess(WifiQuestions object) {
							Intent i = new Intent(getApplicationContext(), KnockStepFirstActivity.class);
							i.putExtra("whoami", "WifiDetailsActivity");
							i.putExtra("data", object);
							startActivity(i);
						}
						
						@Override
						public void onFailed(String msg) {
							showToast("获取敲门信息失败，请稍后重试:" + msg);
						}
					});
				}
				
			});
		}
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
		mUpdateViewHandler.removeCallbacks(mMonitorDataRunnable);
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
	
	private void pullDataFromDB() {
		WifiDynamic wifiDynamic = new WifiDynamic();
		wifiDynamic.QueryConnectedTimes(this, mWifiInfoScanned.getWifiMAC(), new DataCallback<Long>() {

			@Override
			public void onSuccess(Long object) {
				Log.i(TAG, "Success to query wifi dynamic from server:" + mWifiInfoScanned.getWifiMAC());
				mWifiInfoScanned.setConnectedTimes(object);
				mDynamicFlag = GET_DATA_SUCCESS;
			}

			@Override
			public void onFailed(String msg) {
				Log.e(TAG, "Failed to query wifi dynamic from server:" + mWifiInfoScanned.getWifiMAC());
				mDynamicFlag = GET_DATA_FAILED;
			}
		});
		
		WifiMessages wifiMessages = new WifiMessages();
		wifiMessages.QueryByMacAddress(this, mWifiInfoScanned.getWifiMAC(), new MultiDataCallback<WifiMessages>() {
			
			@Override
			public void onSuccess(List<WifiMessages> object) {
				Log.i(TAG, "Success to query wifi messages from server:" + object.size());
				for (WifiMessages wifiMessages : object) {
					mWifiInfoScanned.addMessage(wifiMessages.Message);
				}
				mMessagesFlag = GET_DATA_SUCCESS;
			}
			
			@Override
			public void onFailed(String msg) {
				Log.e(TAG, "Failed to query wifi messages from server:" + mWifiInfoScanned.getWifiMAC());
				mMessagesFlag = GET_DATA_FAILED;
			}
		});
		
		WifiComments wifiComments = new WifiComments();
		wifiComments.QueryByMacAddress(this, mWifiInfoScanned.getWifiMAC(), new MultiDataCallback<WifiComments>() {
			
			@Override
			public void onSuccess(List<WifiComments> object) {
				Log.i(TAG, "Success to query wifi comments from server:" + object.size());
				for (WifiComments wifiComments : object) {
					mWifiInfoScanned.addComment(wifiComments.Comment);
				}
				mCommentsFlag = GET_DATA_SUCCESS;
			}
			
			@Override
			public void onFailed(String msg) {
				Log.e(TAG, "Failed to query wifi comments from server:" + mWifiInfoScanned.getWifiMAC());
				mCommentsFlag = GET_DATA_FAILED;
				
			}
		});
		
		if (LoginHelper.getInstance(this).isLogined()) {
			WifiFollow wifiFollow = new WifiFollow();
			wifiFollow.QueryWifiByMac(this, mWifiInfoScanned.getWifiMAC(),
					LoginHelper.getInstance(this).getCurLoginUserInfo().PhoneNumber, new DataCallback<WifiFollow>() {

						@Override
						public void onSuccess(WifiFollow object) {
							Log.i(TAG, "Success to find wifi info from wifi follow table");
							mFollowView.setText("取消收藏");
							mFollowView.setBackgroundColor(color.gray);
							mFollowed = object;
							mFollowFlag = GET_DATA_SUCCESS;
						}

						@Override
						public void onFailed(String msg) {
							Log.e(TAG, "Failed to find wifi info from wifi follow table");
							mFollowView.setText("收藏WiFi");
							mFollowView.setBackgroundColor(color.orange);
							mFollowFlag = GET_DATA_FAILED;
						}
					});
		} else {
			mFollowFlag = GET_DATA_FAILED;
		}
		
	}
	
	private void initializeProcessBar() {
		mProcessBar = new ProgressBar(this, null, android.R.attr.progressBarStyleLarge);
		mLayoutRoot = (ViewGroup) this.findViewById(android.R.id.content).getRootView();
		mProcessBar.setIndeterminate(true);
		mProcessBar.setVisibility(View.VISIBLE);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		RelativeLayout rl = new RelativeLayout(this);
		rl.setGravity(Gravity.CENTER);
		rl.addView(mProcessBar);
		mLayoutRoot.addView(rl, params);
	}
	
	private void monitorDataDownload() {
		if (mMonitorDataRunnable == null) {
			mMonitorDataRunnable = new Runnable() {
				
				@Override
				public void run() {
					if (mCommentsFlag==GET_DATA_SUCCESS) {
						mRankText.setText("排名：" + String.valueOf(mWifiInfoScanned.getRanking()));
						mRateText.setText(String.valueOf(mWifiInfoScanned.getWifiStrength()));
						mConnectedCnt.setText(String.valueOf(mWifiInfoScanned.getConnectedTimes()) + "次");
						mConnectedTime.setText(String.valueOf(mWifiInfoScanned.getConnectedDuration()) + "小时");
					}
					if (mDynamicFlag==GET_DATA_SUCCESS) {
						if (mWifiInfoScanned.getMessages().size() > 0) {
							mWifiMessage.setText(mWifiInfoScanned.getMessages().get(0));
						}
					}
					if (mMessagesFlag==GET_DATA_SUCCESS) {
						List<String> comment_item = mWifiInfoScanned.getComments();
						mListComments.setAdapter(new ArrayAdapter<String>(getBaseContext(), R.layout.list_view_item, comment_item));
					}
					if (mCommentsFlag != GET_DATA_DEFAULT && mDynamicFlag != GET_DATA_DEFAULT
							&& mMessagesFlag != GET_DATA_DEFAULT && mFollowFlag != GET_DATA_DEFAULT) {
						mProcessBar.setVisibility(View.GONE);
						mLayoutRoot.removeView(mProcessBar);
						return;
					}
					
					mUpdateViewHandler.postDelayed(this, 500);
				}
			};
			mUpdateViewHandler.post(mMonitorDataRunnable);
		}
	}
	
	private void getWifiProfiles(WifiInfoScanned wifiInfoScanned) {
		List<WifiProfile> listProfiles = WifiListHelper.getInstance(this).mWifiProfiles;
		if (listProfiles != null) {
			for (WifiProfile wifiProfile : listProfiles) {
				if (wifiProfile.MacAddr.equals(wifiInfoScanned.getWifiMAC())) {
					wifiInfoScanned.setWifiLogo(wifiProfile.Logo);
					break;
				}
			}
		} else {
			Log.i(TAG, "Wifi Profile table return null");
		}
	}
	
	private byte[] bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		boolean convertFlag = bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		Log.i(TAG, "convert to png: " + convertFlag);
		return baos.toByteArray();
	}
	
	private Bitmap byte2Bitmap(byte[] b) {
		if (b.length != 0) {
			return BitmapFactory.decodeByteArray(b,  0, b.length);
		}
		return null;
	}

}
