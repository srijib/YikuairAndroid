package com.bestapp.yikuair.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import com.bestapp.yikuair.R;
import com.bestapp.yikuair.utils.StringWidthWeightRandom;
import com.bestapp.yikuair.utils.UserInfo;

public class ExitActivity extends Activity {
	// private LinearLayout layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.exit_dialog);
		/*
		 * layout = (LinearLayout) findViewById(R.id.exit_layout);
		 * layout.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { // TODO Auto-generated method
		 * stub Toast.makeText(getApplicationContext(),
		 * "提示：点击窗口外部关闭窗口！",
		 * Toast.LENGTH_SHORT).show(); } });
		 */
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return true;
	}

	public void exitbutton1(View v) {
		this.finish();
	}

	public void exitbutton0(View v) {
		closeAllAcitivity();
	}

	private void closeAllAcitivity() {
		this.finish();
		/*
		 * UserInfo.isLogin = false; UserInfo.isExit = true; ClientSocket client
		 * = new ClientSocket(this); client.sendMessage(null, 102,
		 * StringWidthWeightRandom.getNextString(), null, null, null, null,
		 * null, null, null, false);
		 * 
		 * if (UserInfo.isFirstLogin) { UserInfo.isFirstLogin = false; }
		 * SharedPreferencesUtil shared = new SharedPreferencesUtil(this);
		 * shared.saveUserInfo(); shared.savePhotoUrl();
		 * 
		 * if (SettingActivity.instance != null)
		 * SettingActivity.instance.finish(); if
		 * (PersonalProfileActivity.instance != null)
		 * PersonalProfileActivity.instance.finish(); if
		 * (ResponsiveUIActivity.instance != null)
		 * ResponsiveUIActivity.instance.finish(); if (ChatActivity.instance !=
		 * null) ChatActivity.instance.finish(); if
		 * (SelectMemberActivity.instance != null)
		 * SelectMemberActivity.instance.finish(); if
		 * (ScheduleAddActivity.instance != null)
		 * ScheduleAddActivity.instance.finish(); if (LoginActivity.instance !=
		 * null) LoginActivity.instance.finish();
		 * 
		 * ClientSocket.socket = null;
		 */
		UserInfo.isHomePressed = true;
		ClientSocket client = new ClientSocket(this);
		client.sendMessage(null, 102, StringWidthWeightRandom.getNextString(),
				null, null, null, null, null, null, null, null, false);

		Intent i = new Intent(Intent.ACTION_MAIN);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.addCategory(Intent.CATEGORY_HOME);
		startActivity(i);
	}
}
