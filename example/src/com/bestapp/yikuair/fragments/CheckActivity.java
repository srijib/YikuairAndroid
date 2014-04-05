package com.bestapp.yikuair.fragments;

import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.bestapp.yikuair.R;

public class CheckActivity extends Activity {

	private EditText checkResult;
	private TextView checkQuestion;
	private int answer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.check_dialog);
		checkResult = (EditText) findViewById(R.id.et_answer);
		checkResult.setInputType(EditorInfo.TYPE_CLASS_NUMBER);

		checkQuestion = (TextView) findViewById(R.id.tv_question);
		checkQuestion.setText(getCheckQuestion());
	}
  
	private String getCheckQuestion() {
		String question;
		Random r = new Random();
		int a = r.nextInt(10);
		int b = r.nextInt(10);
		question = String.valueOf(a) + " + " + String.valueOf(b) + " = ?";
		answer = a + b;
		return question;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return true;
	}

	public void toContinue(View v) {
		boolean isRight = checkResult();
		Intent intent = new Intent();
		intent.putExtra("isRight", isRight);
		setResult(1, intent);
		finish();
	}

	public boolean checkResult() {
		String ans = checkResult.getText().toString();
		if(ans.equals(String.valueOf(answer))){
			return true;
		}else{
			return false;
		}
	}
}
