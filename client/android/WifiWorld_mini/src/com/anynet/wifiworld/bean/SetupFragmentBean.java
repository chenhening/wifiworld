package com.anynet.wifiworld.bean;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;

public class SetupFragmentBean {
	private String questionStr;
	private String questionType;
	private Bitmap attachImage;
	private String attachMedia;
	private String OKAnswer;
	private List<String> ErrorAnswer = new ArrayList<String>();

	public String getQuestionStr() {
		return questionStr;
	}

	public void setQuestionStr(String questionStr) {
		this.questionStr = questionStr;
	}

	public String getQuestionType() {
		return questionType;
	}

	public void setQuestionType(String questionType) {
		this.questionType = questionType;
	}

	public Bitmap getAttachImage() {
		return attachImage;
	}

	public void setAttachImage(Bitmap attachImage) {
		this.attachImage = attachImage;
	}

	public String getAttachMedia() {
		return attachMedia;
	}

	public void setAttachMedia(String attachMedia) {
		this.attachMedia = attachMedia;
	}

	public String getOKAnswer() {
		return OKAnswer;
	}

	public void setOKAnswer(String oKAnswer) {
		OKAnswer = oKAnswer;
	}

	public List<String> getErrorAnswer() {
		return ErrorAnswer;
	}

	public void setErrorAnswer(List<String> errorAnswer) {
		ErrorAnswer = errorAnswer;
	}

}
