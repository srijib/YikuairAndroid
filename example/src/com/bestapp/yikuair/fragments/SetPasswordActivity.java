package com.bestapp.yikuair.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.bestapp.yikuair.R;
import com.bestapp.yikuair.utils.Md5Util;
import com.bestapp.yikuair.utils.MessageInfo;
import com.bestapp.yikuair.utils.ModifyPasswordUtil;
import com.bestapp.yikuair.utils.UserInfo;

public class SetPasswordActivity extends Activity {

	private EditText oldPassword;
	private EditText setPassword;
	private EditText setPasswordAgain;
	private Toast toast;
	private TextView tv_success;
	private Dialog mDialog;
	private ModifyPasswordResultBroadcastReceiver mbr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_password_layout);
		initView();
	}

	public void initView() {
		oldPassword = (EditText) findViewById(R.id.et_old_password);
		setPassword = (EditText) findViewById(R.id.et_login_password);
		setPasswordAgain = (EditText) findViewById(R.id.et_login_password_again);
		tv_success = (TextView) findViewById(R.id.tv_submit_success);
		mDialog = new AlertDialog.Builder(this).create();
		// register broadcast
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(MessageInfo.ModifyPasswordBroadcast);
		mbr = new ModifyPasswordResultBroadcastReceiver();
		registerReceiver(mbr, intentFilter);
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

	class ModifyPasswordResultBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			Log.i("test", "receive modiy password result broadcast");
			int resultCode = arg1.getIntExtra("code", 0);
			closeLoadingDialog();
			if (resultCode == 200) {
				// showToast(getResources().getString(R.string.set_successful));
				UserInfo.cipher_password = Md5Util.MD5(setPassword.getText()
						.toString());
				tv_success.setVisibility(View.VISIBLE);
				quit();
			} else {
				showToast(getResources().getString(
						R.string.modify_password_error));
			}
		}
	}

	public void showToast(String str) {
		toast = Toast.makeText(getApplication(), str, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	public void cancelSetPassword(View view) {
		quit();
	}

	public void checkSetPassword(View view) {
		String old_password = oldPassword.getText().toString();
		String password_1 = setPassword.getText().toString();
		String password_2 = setPasswordAgain.getText().toString();
		if (old_password == null || old_password.length() == 0
				|| password_1 == null || password_1.length() == 0
				|| password_2 == null || password_2.length() == 0) {
			showToast(getResources()
					.getString(R.string.set_password_null_error));
			return;
		}
		if (!password_1.equals(password_2)) {
			showToast(getResources().getString(R.string.set_password_dismatch));
		} else {
			Log.e("test", "password ::" + UserInfo.password);
			if (old_password.equals(password_1)) {
				showToast(getResources().getString(
						R.string.set_password_same_error));
				return;
			}else if(!old_password.equals(UserInfo.password)){
				showToast(getResources().getString(
						R.string.old_password_error));
				return;
			}
			UserInfo.password = password_1;
			showRoundProcessDialog();
			ModifyPasswordUtil util = new ModifyPasswordUtil(this, setPassword
					.getText().toString());
			util.ModifyPassword();
		}
	}

	public void quit() {
		finish();
		overridePendingTransition(R.anim.in_from_left, R.anim.out_of_right);
	}

	@Override
	public void onStop() {
		super.onStop();
		if (mbr != null) {
			unregisterReceiver(mbr);
			mbr = null;
		}
		Log.i("test", "setpassword onStop");
	}

	@Override
	public void onBackPressed() {
		quit();
	}
}
