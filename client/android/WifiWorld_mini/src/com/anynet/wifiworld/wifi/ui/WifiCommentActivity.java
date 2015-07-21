package com.anynet.wifiworld.wifi.ui;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.data.DataCallback;
import com.anynet.wifiworld.data.UserProfile;
import com.anynet.wifiworld.data.WifiComments;
import com.anynet.wifiworld.util.LoginHelper;
import com.anynet.wifiworld.wifi.WifiAdmin;
import com.anynet.wifiworld.wifi.WifiCurrent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class WifiCommentActivity extends Activity {
	private final static String TAG = WifiCommentActivity.class.getSimpleName();
	
	public final static String WIFI_COMMENT_ADD = "wifi_comment_add";
	private float mRateValue = 0.0f;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_wifi_comment);
		super.onCreate(savedInstanceState);
		
		RatingBar ratingBar = (RatingBar) findViewById(R.id.rb_wifi_score);
		ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			
			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
				mRateValue = rating;
			}
		});
		
		final EditText commentEdit = (EditText)findViewById(R.id.comment_edit);
		TextView commentBtn = (TextView)findViewById(R.id.comment_send);
		commentBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				final String commentString = commentEdit.getText().toString();
				if (commentString == "" || commentString.isEmpty()) {
					Toast.makeText(getBaseContext(), "Input text can not be empty", Toast.LENGTH_LONG).show();
					return;
				}
				final WifiComments wifiComments = new WifiComments();
				wifiComments.Comment = commentString;
				wifiComments.MacAddr = WifiCurrent.getInstance(getApplicationContext()).getWifiListItem().getWifiMac();
				wifiComments.Rating = mRateValue;
				UserProfile userProfile = LoginHelper.getInstance(getBaseContext()).getCurLoginUserInfo();
				if (userProfile != null && userProfile.NickName != null) {
					wifiComments.UserId = userProfile.NickName;
					wifiComments.UserAccount = userProfile.getUsername();
				} else {
					wifiComments.UserId = "无名氏";
					wifiComments.UserAccount = "";
				}
				wifiComments.MarkSendTime();
				wifiComments.StoreRemote(getBaseContext(), new DataCallback<WifiComments>() {
					
					@Override
					public void onSuccess(WifiComments object) {
						Intent intent = new Intent();
						Bundle bundle = new Bundle();
						bundle.putSerializable(WIFI_COMMENT_ADD, wifiComments);
						intent.putExtras(bundle);
						setResult(RESULT_OK, intent);
						//shutdown soft keyboard if soft keyboard is actived
						InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
						if (imm.isActive()) {
							imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
						}
						finish();
					}
					
					@Override
					public void onFailed(String msg) {
						Toast.makeText(getBaseContext(), "Failed to add comment, Pls try again later." + msg, Toast.LENGTH_LONG).show();
					}
				});
			}
		});
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	
}
