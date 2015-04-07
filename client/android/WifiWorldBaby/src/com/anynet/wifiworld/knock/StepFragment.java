package com.anynet.wifiworld.knock;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.anynet.wifiworld.R;
import com.anynet.wifiworld.MainActivity.MainFragment;
import com.anynet.wifiworld.bean.SetupFragmentBean;

public class StepFragment extends MainFragment implements OnClickListener {

	private SetupFragmentBean mSetupFragmentBean = new SetupFragmentBean();

	private EditText questionET;
	private ImageButton btn_question_delete;
	private TextView remainingTV;
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
		// TODO Auto-generated method stub
		questionET = (EditText) findViewById(R.id.question);
		btn_question_delete = (ImageButton) findViewById(R.id.btn_question_delete);
		remainingTV = (TextView) findViewById(R.id.text_remaining);
		question_categoryTV = (TextView) findViewById(R.id.question_category);
		change_questionTV = (TextView) findViewById(R.id.knock_change_question);
		btn_cameraLL = findViewById(R.id.btn_camera);
		btn_recordLL = findViewById(R.id.btn_record);
		check1 = (CheckBox) findViewById(R.id.check1);
		answer1 = (EditText) findViewById(R.id.answer1);
		a1_delete = (ImageButton) findViewById(R.id.a1_delete);
		a1_delete.setOnClickListener(this);
		check2 = (CheckBox) findViewById(R.id.check2);
		answer2 = (EditText) findViewById(R.id.answer2);
		a2_delete = (ImageButton) findViewById(R.id.a2_delete);
		check3 = (CheckBox) findViewById(R.id.check3);
		answer3 = (EditText) findViewById(R.id.answer3);
		a3_delete = (ImageButton) findViewById(R.id.a3_delete);
		check4 = (CheckBox) findViewById(R.id.check4);
		answer4 = (EditText) findViewById(R.id.answer4);
		a4_delete = (ImageButton) findViewById(R.id.a4_delete);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.question: {
			break;
		}
		case R.id.btn_question_delete: {
			questionET.setText("");
			break;
		}
		case R.id.text_remaining: {
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
			check1.setChecked(!check1.isChecked());
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
			check2.setChecked(!check2.isChecked());
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
			check3.setChecked(!check3.isChecked());
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
			check4.setChecked(!check4.isChecked());
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
