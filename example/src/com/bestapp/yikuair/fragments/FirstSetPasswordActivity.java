package com.bestapp.yikuair.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bestapp.yikuair.LoginActivity;
import com.bestapp.yikuair.R;
import com.bestapp.yikuair.utils.ContactUtil;
import com.bestapp.yikuair.utils.FriendEntity;
import com.bestapp.yikuair.utils.Md5Util;
import com.bestapp.yikuair.utils.MessageInfo;
import com.bestapp.yikuair.utils.ModifyPasswordUtil;
import com.bestapp.yikuair.utils.SharedPreferencesUtil;
import com.bestapp.yikuair.utils.UserInfo;

public class FirstSetPasswordActivity extends Activity {

	private EditText setPassword;
	private EditText setPasswordAgain;
	private Toast toast;
	private Dialog mDialog;
	private ModifyPasswordResultBroadcastReceiver mbr;
	private SharedPreferencesUtil shared;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.first_set_password_layout);
		initView();
	}

	public void initView() {
		shared = new SharedPreferencesUtil(this);
		mDialog = new AlertDialog.Builder(this).create();
		setPassword = (EditText) findViewById(R.id.et_login_password);
		setPasswordAgain = (EditText) findViewById(R.id.et_login_password_again);
		// register broadcast
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(MessageInfo.ModifyPasswordBroadcast);
		mbr = new ModifyPasswordResultBroadcastReceiver();
		registerReceiver(mbr, intentFilter);
	}

	class ModifyPasswordResultBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			Log.i("test", "receive modiy password result broadcast");
			int resultCode = arg1.getIntExtra("code", 0);
			closeLoadingDialog();
			if (resultCode == 200) {
				stepToFirstSetPersonalProfile();
			}else{
				Toast.makeText(
						getApplication(),
						getApplication().getString(
								R.string.modify_password_error),
						Toast.LENGTH_LONG).show();
			}
		}
	}

	public void showToast(String str) {
		toast = Toast.makeText(getApplication(), str, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	public void closeLoadingDialog() {
		if (mDialog.isShowing())
			mDialog.dismiss();
	}

	public void showRoundProcessDialog() {
		mDialog.show();
		mDialog.setCanceledOnTouchOutside(false);
		mDialog.setContentView(R.layout.modify_password_loading_dialog);
	}

	public void cancelSetPassword(View view) {
		// quit();
	}

	public void nextStep(View view) {
		String password_1 = setPassword.getText().toString();
		String password_2 = setPasswordAgain.getText().toString();
		if (!password_1.equals(password_2)) {
			showToast(getResources().getString(R.string.set_password_dismatch));
		} else if (password_1.length() == 0 && password_2.length() == 0) {
			showToast(getResources()
					.getString(R.string.set_password_null_error));
		} else {
			// stepToFirstSetPersonalProfile();
			showRoundProcessDialog();
			ModifyPasswordUtil util = new ModifyPasswordUtil(this, setPassword
					.getText().toString());
			util.ModifyPassword();
		}
	}

	public void stepToFirstSetPersonalProfile() {
		unregisterReceiver(mbr);
		UserInfo.cipher_password = Md5Util.MD5(setPassword.getText().toString());
		shared.saveUserInfo();
		Intent intent = new Intent(this, SetPersonalProfileActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.in_from_right, R.anim.out_of_left);
		finish();
	}

	public void quit() {
		finish();
		overridePendingTransition(R.anim.in_from_left, R.anim.out_of_right);
	}

	@Override
	public void onBackPressed() {
		// quit();
	}
}
