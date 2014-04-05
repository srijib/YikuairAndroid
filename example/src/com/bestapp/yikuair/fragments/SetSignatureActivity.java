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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bestapp.yikuair.R;
import com.bestapp.yikuair.utils.FriendEntity;
import com.bestapp.yikuair.utils.MessageInfo;
import com.bestapp.yikuair.utils.StringWidthWeightRandom;
import com.bestapp.yikuair.utils.UserInfo;

public class SetSignatureActivity extends Activity {

	private EditText setSignature;
	private TextView tv_num;
	private TextView tv_success;
	final int num = 30;
	private Toast toast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_signature_layout);
		initView();
	}

	public void initView() {
		setSignature = (EditText) findViewById(R.id.et_set_signature);
		tv_num = (TextView) findViewById(R.id.tv_num);
		tv_num.setText(String.valueOf(num));
		tv_success = (TextView) findViewById(R.id.tv_submit_success);
		if (UserInfo.signature != null && UserInfo.signature.length() > 0) {
			setSignature.setText(UserInfo.signature);
		}
		setSignature.addTextChangedListener(new TextWatcher() {
			private CharSequence temp;
			private int selectionStart;
			private int selectionEnd;

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				temp = s;
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				int number = num - s.length();
				tv_num.setText("" + number);
				selectionStart = setSignature.getSelectionStart();
				selectionEnd = setSignature.getSelectionEnd();
				if (temp.length() > num) {
					s.delete(selectionStart - 1, selectionEnd);
					int tempSelection = selectionStart;
					setSignature.setText(s);
					setSignature.setSelection(tempSelection);
				}
			}
		});
	}

	public void checkSetSignature(View view) {
		ClientSocket client = new ClientSocket(this);
		String sig = setSignature.getText().toString();
		UserInfo.signature = sig;
		client.sendMessage(sig, 12, StringWidthWeightRandom.getNextString(),
				UserInfo.db_id, null, null, null, null, null, "signature",
				null, false);
		tv_success.setVisibility(View.VISIBLE);
		quit(true);
		// showToast(getResources().getString(R.string.set_signature_success));
	}

	/*
	 * public void showToast(String str) { toast =
	 * Toast.makeText(getApplication(), str, Toast.LENGTH_LONG);
	 * toast.setGravity(Gravity.CENTER, 0, 0); toast.show(); }
	 */

	public void cancelSetSignature(View view) {
		quit(false);
	}

	public void quit(boolean isSuccess) {
		if (isSuccess) {
			Intent intent = new Intent();
			intent.putExtra("signature", setSignature.getText().toString());
			setResult(8, intent);
		}
		finish();
		overridePendingTransition(R.anim.in_from_left, R.anim.out_of_right);
	}

	@Override
	public void onBackPressed() {
		cancelSetSignature(null);
	}
}
