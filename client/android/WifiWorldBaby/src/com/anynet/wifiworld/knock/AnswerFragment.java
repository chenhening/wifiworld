package com.anynet.wifiworld.knock;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.app.BaseFragment;
import com.anynet.wifiworld.bean.SetupFragmentBean;

public class AnswerFragment extends BaseFragment implements OnClickListener {
	private SetupFragmentBean mSetupFragmentBean = new SetupFragmentBean();

	private TextView mQuestion;
	private TextView mAnswer1;
	private TextView mAnswer2;
	private TextView mAnswer3;
	private TextView mAnswer4;
	
	private int mRightAnswer = 1;
	private List<String> mData;

	public AnswerFragment(List<String> data) {
		mData = data;
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
		switch (v.getId()) {
		case R.id.answer1:
			mRightAnswer = 1;
			break;
		case R.id.answer2:
			mRightAnswer = 2;
			break;
		case R.id.answer3:
			mRightAnswer = 3;
			break;
		case R.id.answer4:
			mRightAnswer = 4;
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
		//TODO(binfei):拉取服务器信息
		mQuestion.setText(mData.get(0));
		mAnswer1 = (TextView) this.findViewById(R.id.answer1);
		mAnswer1.setText(mData.get(1));
		mAnswer1.setOnClickListener(this);
		mAnswer2 = (TextView) this.findViewById(R.id.answer2);
		mAnswer2.setText(mData.get(2));
		mAnswer2.setOnClickListener(this);
		mAnswer3 = (TextView) this.findViewById(R.id.answer3);
		mAnswer3.setText(mData.get(3));
		mAnswer3.setOnClickListener(this);
		mAnswer4 = (TextView) this.findViewById(R.id.answer4);
		mAnswer4.setText(mData.get(4));
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
}
