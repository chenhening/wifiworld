package com.anynet.wifiworld.knock;

import im.yixin.algorithm.MD5;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.app.BaseFragment;
import com.anynet.wifiworld.bean.SetupFragmentBean;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;

public class AnswerFragment extends BaseFragment implements OnClickListener {
	private SetupFragmentBean mSetupFragmentBean = new SetupFragmentBean();

	private TextView mQuestion;
	private TextView mAnswer1;
	private TextView mAnswer2;
	private TextView mAnswer3;
	private TextView mAnswer4;
	
	private int mindex1 = 0;
	private int mindex2 = 0;
	private int mindex3 = 0;
	private int mindex4 = 0;
	
	private int mRightAnswer = 1;
	private List<String> mData;

	private int fragmentID;
	private static int id= 1;
	public int getFragmentID() {
		return fragmentID;
	}

	public void setFragmentID(int fragmentID) {
		this.fragmentID = fragmentID;
	}

	public AnswerFragment(List<String> data) {
		this(data,id++);
	}
		
	public AnswerFragment(List<String> data,int id) {
		mData = data;
		setFragmentID(id);
	}
	
	public SetupFragmentBean getFragmentData() {
		return mSetupFragmentBean;
	}

	public void setFragmentBean(SetupFragmentBean mSetupFragmentBean) {
		this.mSetupFragmentBean = mSetupFragmentBean;
	}
	
	public int getRightAnswer() {
		return mRightAnswer;
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}
	
	@Override
	public void onClick(View v) {
		this.findViewById(R.id.ll_knock_answer1).setBackgroundColor(Color.WHITE);
		this.findViewById(R.id.ll_knock_answer2).setBackgroundColor(Color.WHITE);
		this.findViewById(R.id.ll_knock_answer3).setBackgroundColor(Color.WHITE);
		this.findViewById(R.id.ll_knock_answer4).setBackgroundColor(Color.WHITE);
		switch (v.getId()) {
		case R.id.answer1:
			mRightAnswer = mindex1;
			this.findViewById(R.id.ll_knock_answer1).setBackgroundColor(Color.YELLOW);
			break;
		case R.id.answer2:
			mRightAnswer = mindex2;
			this.findViewById(R.id.ll_knock_answer2).setBackgroundColor(Color.YELLOW);
			break;
		case R.id.answer3:
			mRightAnswer = mindex3;
			this.findViewById(R.id.ll_knock_answer3).setBackgroundColor(Color.YELLOW);
			break;
		case R.id.answer4:
			mRightAnswer = mindex4;
			this.findViewById(R.id.ll_knock_answer4).setBackgroundColor(Color.YELLOW);
			break;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mPageRoot = inflater.inflate(R.layout.knock_answer, null);
		super.onCreateView(inflater, container, savedInstanceState);
		
		//显示问题或者图片
		mQuestion = (TextView) this.findViewById(R.id.question);
		mQuestion.setText(mData.get(0));
		
		//打乱顺序后存放
		List<Integer> order = reOrder();
		mAnswer1 = (TextView) this.findViewById(R.id.answer1);
		mindex1 = order.get(0);
		mAnswer1.setText(mData.get(mindex1));
		mAnswer1.setOnClickListener(this);
		mAnswer2 = (TextView) this.findViewById(R.id.answer2);
		mindex2 = order.get(1);
		mAnswer2.setText(mData.get(mindex2));
		mAnswer2.setOnClickListener(this);
		mAnswer3 = (TextView) this.findViewById(R.id.answer3);
		mindex3 = order.get(2);
		mAnswer3.setText(mData.get(mindex3));
		mAnswer3.setOnClickListener(this);
		mAnswer4 = (TextView) this.findViewById(R.id.answer4);
		mindex4 = order.get(3);
		mAnswer4.setText(mData.get(mindex4));
		mAnswer4.setOnClickListener(this);
		
		return mPageRoot;
	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public boolean onBackPressed() {
		// TODO Auto-generated method stub
		return super.onBackPressed();
	}
	
	//re-order 
	private List<Integer> reOrder() {
		List<Integer> list = new ArrayList<Integer>();
		for(int i=1; i<5; ++i)
			list.add(i);
		Collections.shuffle(list);
		return list;
	}
}
