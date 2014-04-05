package com.bestapp.yikuair.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import com.bestapp.yikuair.R;
import com.bestapp.yikuair.utils.UserInfo;

public class ExitFromSettings extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.exit_dialog_from_settings);
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
		UserInfo.isLogin = false;
		UserInfo.isExit = true;
		//UserInfo.clientsocket.closeSocket();
		this.finish();
		SettingActivity.instance.finish();
		ResponsiveUIActivity.instance.finish();
	}
}
