package com.bestapp.yikuair.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import com.bestapp.yikuair.R;

public class ChatTopRightDialog extends Activity {
	private LinearLayout layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_message_top_dialog);
		// dialog=new MyDialog(this);

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return true;
	}
	/*
	 * public void exitbutton1(View v) { this.finish(); } public void
	 * exitbutton0(View v) { this.finish();
	 * MainWeixin.instance.finish();//关闭Main 这个Activity }
	 */
}
