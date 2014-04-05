package com.bestapp.yikuair.fragments;

import java.io.IOException;

import com.bestapp.yikuair.AppstartActivity;
import com.bestapp.yikuair.LoginActivity;
import com.bestapp.yikuair.MainApp;
import com.bestapp.yikuair.R;
import com.bestapp.yikuair.utils.SharedPreferencesUtil;
import com.bestapp.yikuair.utils.StringWidthWeightRandom;
import com.bestapp.yikuair.utils.UserInfo;
import com.bestapp.yikuair.fragments.ChatActivity;
import com.bestapp.yikuair.fragments.ClientSocket;
import com.bestapp.yikuair.fragments.PersonalProfileActivity;
import com.bestapp.yikuair.fragments.ResponsiveUIActivity;
import com.bestapp.yikuair.fragments.ScheduleAddActivity;
import com.bestapp.yikuair.fragments.SelectMemberActivity;
import com.bestapp.yikuair.fragments.SettingActivity;

import android.R.color;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;

public class ExitSettingsPW extends PopupWindow {

	Button btn_exit, btn_cancel;
	Activity mContext;

	public ExitSettingsPW(Activity context) {
		super(context);
		mContext = context;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.exit_dialog_from_settings, null);

		btn_exit = (Button) view.findViewById(R.id.exitBtn0);
		btn_cancel = (Button) view.findViewById(R.id.exitBtn1);

		ColorDrawable dw = new ColorDrawable(color.transparent);
		this.setBackgroundDrawable(dw);

		btn_cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dismiss();
			}
		});

		btn_exit.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				UserInfo.isLogin = false;
				UserInfo.isExit = true;
				ClientSocket client = new ClientSocket(mContext);
				client.sendMessage(null, 101,
						StringWidthWeightRandom.getNextString(), null, null,
						null, null, null, null, null, null, false);

				if (UserInfo.isFirstLogin) {
					UserInfo.isFirstLogin = false;
				}

				UserInfo.isRecreateConnection = false;

				SharedPreferencesUtil shared = new SharedPreferencesUtil(
						mContext);
				shared.saveUserInfo();
				shared.savePhotoUrl();
				if (SettingActivity.instance != null)
					SettingActivity.instance.finish();
				if (PersonalProfileActivity.instance != null)
					PersonalProfileActivity.instance.finish();
				if (ResponsiveUIActivity.instance != null)
					ResponsiveUIActivity.instance.finish();
				if (ChatActivity.instance != null)
					ChatActivity.instance.finish();
				if (SelectMemberActivity.instance != null)
					SelectMemberActivity.instance.finish();
				if (ScheduleAddActivity.instance != null)
					ScheduleAddActivity.instance.finish();

				if (AppstartActivity.instance != null)
					AppstartActivity.instance.finish();
				
				if (LoginActivity.instance == null) {
					Intent intent = new Intent(mContext, LoginActivity.class);
					intent.putExtra("username", UserInfo.id);
					mContext.startActivity(intent);
				}

				/*
				 * if (ClientSocket.rbr != null){
				 * mContext.unregisterReceiver(ClientSocket.rbr);
				 * ClientSocket.rbr = null; }
				 */
				MainApp.getInstance().exitApp();
				dismiss();
			}
		});

		this.setContentView(view);

		this.setWidth(LayoutParams.FILL_PARENT);

		this.setHeight(LayoutParams.FILL_PARENT);
		this.setFocusable(true);
		view.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					dismiss();
				}
				return true;
			}
		});
	}
}

//btn_exit.setOnClickListener(new OnClickListener() {
//	public void onClick(View v) {
//
//		UserInfo.isLogin = false;
//		UserInfo.isExit = true;
//		ClientSocket client = new ClientSocket(mContext);
//		client.sendMessage(null, 101,
//				StringWidthWeightRandom.getNextString(), null, null,
//				null, null, null, null, null, null, false);
//
//		if (UserInfo.isFirstLogin) {
//			UserInfo.isFirstLogin = false;
//		}
//
//		UserInfo.isRecreateConnection = false;
//
//		SharedPreferencesUtil shared = new SharedPreferencesUtil(
//				mContext);
//		shared.saveUserInfo();
//		shared.savePhotoUrl();
//
//		MainApp.getInstance().exitApp();
//	}
//});
