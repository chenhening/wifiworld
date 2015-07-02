package com.anynet.wifiworld.knock;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.BaseFragment;
import com.anynet.wifiworld.bean.SetupFragmentBean;

public class StepFragment extends BaseFragment implements OnClickListener, OnFocusChangeListener {

	private SetupFragmentBean mSetupFragmentBean = new SetupFragmentBean();

	private EditText questionET;
	private ImageButton btn_question_delete;
	private TextView question_categoryTV;
	private TextView change_questionTV;
	private View btn_cameraLL;
	private View btn_recordLL;

	private CheckBox check1;
	private EditText answer1;
	private ImageButton a1_delete;
	private CheckBox check2;
	private EditText answer2;
	private ImageButton a2_delete;
	private CheckBox check3;
	private EditText answer3;
	private ImageButton a3_delete;
	private CheckBox check4;
	private EditText answer4;
	private ImageButton a4_delete;
	
	private List<String> mData;
	int fragmentID;
	private static int id=0;
	
	public int getFragmentID() {
		
		return fragmentID;
	}

	public void setFragmentID(int fragmentID) {
		this.fragmentID = fragmentID;
	}

	public StepFragment(List<String> data) {
		this(data,id++);
	}
		
	public StepFragment(List<String> data,int id) {
		mData = data;
		setFragmentID(id);
	}
	

	public SetupFragmentBean getFragmentData() {
		return mSetupFragmentBean;
	}

	public void setFragmentBean(SetupFragmentBean mSetupFragmentBean) {
		this.mSetupFragmentBean = mSetupFragmentBean;
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mPageRoot = inflater.inflate(R.layout.knockstep1, null);
		super.onCreateView(inflater, container, savedInstanceState);
		setupUI();
		return mPageRoot;
	}

	private void setupUI() {
		questionET = (EditText) findViewById(R.id.question);
		questionET.setText(mData.get(0));
		questionET.setOnFocusChangeListener(this);
		
		btn_question_delete = (ImageButton) findViewById(R.id.btn_question_delete);
		btn_question_delete.setOnClickListener(this);
		question_categoryTV = (TextView) findViewById(R.id.question_category);
		change_questionTV = (TextView) findViewById(R.id.knock_change_question);
		btn_cameraLL = findViewById(R.id.btn_camera);
		btn_recordLL = findViewById(R.id.btn_record);
		
		check1 = (CheckBox) findViewById(R.id.check1);
		answer1 = (EditText) findViewById(R.id.answer1);
		answer1.setText(mData.get(1));
		answer1.setOnFocusChangeListener(this);
		a1_delete = (ImageButton) findViewById(R.id.a1_delete);
		a1_delete.setOnClickListener(this);
		
		check2 = (CheckBox) findViewById(R.id.check2);
		answer2 = (EditText) findViewById(R.id.answer2);
		answer2.setText(mData.get(2));
		answer2.setOnFocusChangeListener(this);
		a2_delete = (ImageButton) findViewById(R.id.a2_delete);
		a2_delete.setOnClickListener(this);
		
		check3 = (CheckBox) findViewById(R.id.check3);
		answer3 = (EditText) findViewById(R.id.answer3);
		answer3.setText(mData.get(3));
		answer3.setOnFocusChangeListener(this);
		a3_delete = (ImageButton) findViewById(R.id.a3_delete);
		a3_delete.setOnClickListener(this);
		
		check4 = (CheckBox) findViewById(R.id.check4);
		answer4 = (EditText) findViewById(R.id.answer4);
		answer4.setText(mData.get(4));
		answer4.setOnFocusChangeListener(this);
		a4_delete = (ImageButton) findViewById(R.id.a4_delete);
		a4_delete.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.question: {
			break;
		}
		case R.id.btn_question_delete: {
			questionET.setText("");
			break;
		}
		case R.id.question_category: {
			break;
		}
		case R.id.knock_change_question: {
			break;
		}
		case R.id.btn_camera: {
			break;
		}
		case R.id.btn_record: {
			break;
		}
		case R.id.check1: {
			//check1.setChecked(!check1.isChecked());
			break;
		}
		case R.id.answer1: {
			break;
		}
		case R.id.a1_delete: {
			answer1.setText("");
			break;
		}
		case R.id.check2: {
			//check2.setChecked(!check2.isChecked());
			break;
		}
		case R.id.answer2: {
			break;
		}
		case R.id.a2_delete: {
			answer2.setText("");
			break;
		}
		case R.id.check3: {
			//check3.setChecked(!check3.isChecked());
			break;
		}
		case R.id.answer3: {
			break;
		}
		case R.id.a3_delete: {
			answer3.setText("");
			break;
		}
		case R.id.check4: {
			//check4.setChecked(!check4.isChecked());
			break;
		}
		case R.id.answer4: {
			break;
		}
		case R.id.a4_delete: {
			answer4.setText("");
			break;
		}
		}
	}
	
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (hasFocus)
			return;
		
		switch (v.getId()) {
		case R.id.question: {
			mData.set(0, questionET.getText().toString());
		}
		case R.id.answer1: {
			mData.set(1, answer1.getText().toString());
			break;
		}
		case R.id.answer2: {
			mData.set(2, answer2.getText().toString());
			break;
		}
		case R.id.answer3: {
			mData.set(3, answer3.getText().toString());
			break;
		}
		case R.id.answer4: {
			mData.set(4, answer4.getText().toString());
			break;
		}
		}
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

	public boolean getData(List<String> data) {
		//验证每一个选项
		String questions = questionET.getText().toString();
		if (questions.length() <= 0) {
			return false;
		}
		
		String str1 = answer1.getText().toString();
		String str2 = answer2.getText().toString();
		String str3 = answer3.getText().toString();
		String str4 = answer4.getText().toString();
		if (str1.length() <= 0 || str2.length() <= 0 || str3.length() <= 0 || str4.length() <= 0) {
			return false;
		}
		
		return true;
	}
}
