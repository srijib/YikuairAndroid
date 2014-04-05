package com.bestapp.yikuair.fragments;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.bestapp.yikuair.R;
import com.bestapp.yikuair.utils.Md5Util;
import com.bestapp.yikuair.utils.UserInfo;

public class TaskChatActivity extends Activity {

	private ImageButton titleLeftBtn;
	private ImageButton titleRightBtn;
	private String title;
	private String startTime;
	private String endTime;
	private int participantCount;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_chat);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		Intent intent = getIntent();
		title = intent.getStringExtra("title");		
		participantCount  = intent.getIntExtra("participantCount", 0);
		endTime = intent.getStringExtra("endTime");
		startTime = intent.getStringExtra("startTime");
	}

	public void backToTask(View view) {
		finish();
		overridePendingTransition(R.anim.in_from_left, R.anim.out_of_right);
	}

	public void groupMember(View view) {
		Intent intent = new Intent(this, TaskMemberActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.in_from_right, R.anim.out_of_left);
	}
	
	@Override
	public boolean onTouchEvent(android.view.MotionEvent event) {
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		return imm.hideSoftInputFromWindow(this.getCurrentFocus()
				.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

}
