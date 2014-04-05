package com.bestapp.yikuair.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bestapp.yikuair.R;
import com.bestapp.yikuair.utils.FriendEntity;
import com.bestapp.yikuair.utils.MessageInfo;
import com.bestapp.yikuair.utils.StringWidthWeightRandom;
import com.bestapp.yikuair.utils.UserInfo;
import com.umeng.analytics.MobclickAgent;

public class ChangeMobileActivity extends Activity {

	private EditText changeMobile;
	private TextView tv_mobile;
	private TextView tv_success;
	private Toast toast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change_mobile_layout);
		initView();
	}

	public void initView() {
		changeMobile = (EditText) findViewById(R.id.et_change_mobile);
		changeMobile.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
		tv_success = (TextView) findViewById(R.id.tv_submit_success);
		tv_mobile = (TextView) findViewById(R.id.tv_mobile_number);
		if (UserInfo.mobile != null && UserInfo.mobile.length() > 0) {
			tv_mobile.setText(UserInfo.mobile);
		} else {
			tv_mobile.setText("空");
		}
	}

	public void checkModifyMobile(View view) {
		String mobile = changeMobile.getText().toString();
		if (mobile.length() > 11) {
			showToast(getResources().getString(R.string.set_mobile_error));
			return;
		}

		UserInfo.mobile = mobile;
		ClientSocket client = new ClientSocket(this);
		client.sendMessage(mobile, 12, StringWidthWeightRandom.getNextString(),
				UserInfo.db_id, null, null, null, null, null, "mobile", null, false);
		tv_success.setVisibility(View.VISIBLE);
		quit(true);
		// showToast(getResources().getString(R.string.set_signature_success));
	}

	public void showToast(String str) {
		toast = Toast.makeText(getApplication(), str, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	public void quitChangeMobile(View view) {
		quit(false);
	}

	public void quit(boolean isSuccess) {
		if (isSuccess) {
			Intent intent = new Intent();
			intent.putExtra("mobile", changeMobile.getText().toString());
			setResult(9, intent);
		}
		finish();
		overridePendingTransition(R.anim.in_from_left, R.anim.out_of_right);
	}

	@Override
	public void onBackPressed() {
		quit(false);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
