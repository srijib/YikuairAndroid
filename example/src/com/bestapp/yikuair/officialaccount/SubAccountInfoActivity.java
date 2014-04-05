package com.bestapp.yikuair.officialaccount;



import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bestapp.yikuair.MainApp;
import com.bestapp.yikuair.OfficialAccountBaseActivity;
import com.bestapp.yikuair.R;
import com.bestapp.yikuair.fragments.ChatActivity;
import com.bestapp.yikuair.fragments.ChatMsgEntity;
import com.bestapp.yikuair.fragments.ClientSocket;
import com.bestapp.yikuair.utils.CustomToast;
import com.bestapp.yikuair.utils.DataUtil;
import com.bestapp.yikuair.utils.MessageInfo;
import com.bestapp.yikuair.utils.UserInfo;

public class SubAccountInfoActivity extends OfficialAccountBaseActivity {

	private ImageView ico;
	private TextView name;
	private TextView intro;
	private Button sub;
	private Button show;
	private boolean isSub;
	private Button sub_cancel;
	private Button noparasitism;
	private Button parasitism;
	private ClientSocket mClientSocket;
	private String toId;
	private String name_string;
	private int num;
	// private AccountInfomation accountInfomation;
	public static SubAccountInfoActivity instance;
	private Dialog mDialog;

	public void showRoundProcessDialog() {
		if (mDialog != null && !mDialog.isShowing()) {
			mDialog.show();
			mDialog.setCanceledOnTouchOutside(false);
			mDialog.setContentView(R.layout.loading_dialog);

		}
	}

	public void closeLoadingDialog() {
		if (mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_subscriptioner_info);
		initView();
		mClientSocket = new ClientSocket(this);
		instance = this;
		mDialog = new AlertDialog.Builder(this).create();
	}

	private void initView() {

		Intent intent = getIntent();
		// accountInfomation = (AccountInfomation) intent
		// .getParcelableExtra("AccountInfomation");
		num = intent.getIntExtra("AccountInfomation", 0);
		name_string = MessageInfo.OfficeAccountList_MINE.get(num).getRealname();
		String intro_string = null;
		try {
			intro_string = new String(
					DataUtil.decodeBase64(MessageInfo.OfficeAccountList_MINE
							.get(num).getSignature()));

		} catch (Exception e) {
			e.printStackTrace();
		}
		toId = MessageInfo.OfficeAccountList_MINE.get(num).getId();
		isSub = MessageInfo.OfficeAccountList_MINE.get(num).isSub();
		LinearLayout view = (LinearLayout) findViewById(R.id.ico_view);
		ico = new ImageView(this);
		ico.setBackgroundResource(R.drawable.ico_boy);
		OfficialAccountFragment.instance.asyncImageLoader.loadBitmap(ico,
				MessageInfo.OfficeAccountList_MINE.get(num).getHeadurl(), 1);

		ico.setImageResource(R.drawable.personal_photo_frame);
		ico.setLayoutParams(new LayoutParams((int) (getWidth() / 5.3 * 2.3),
				(int) (getWidth() / 5.3 * 2.3)));
		view.addView(ico);
		noparasitism = (Button) findViewById(R.id.sub_info_sub_noparasitism);
		parasitism = (Button) findViewById(R.id.sub_info_sub_parasitism);
		name = (TextView) findViewById(R.id.sub_info_name);
		intro = (TextView) findViewById(R.id.sub_info_intro);
		sub = (Button) findViewById(R.id.sub_info_sub);
		sub_cancel = (Button) findViewById(R.id.sub_info_cancel);
		show = (Button) findViewById(R.id.sub_info_show);
		name.setText(name_string);
		if (intro_string != null && !intro_string.equals("")) {
			intro_string = getString(R.string.signature) + intro_string;

		} else {
			intro_string = getString(R.string.signature)
					+ MessageInfo.OfficeAccountList_MINE.get(num).getRealname();
		}
		intro.setText(intro_string);
		if (isSub) {
			show.setVisibility(View.VISIBLE);
			sub.setVisibility(View.GONE);
			noparasitism.setVisibility(View.VISIBLE);
			parasitism.setVisibility(View.GONE);
			sub_cancel.setVisibility(View.VISIBLE);
		} else {
			show.setVisibility(View.INVISIBLE);
			sub.setVisibility(View.VISIBLE);
			sub_cancel.setVisibility(View.GONE);
			noparasitism.setVisibility(View.GONE);
			parasitism.setVisibility(View.VISIBLE);

		}

	}

	private void AddOfficialAccountSuccess() {
		OfficialAccountFragment.instance.mDBOpenHelper
				.insert(MessageInfo.OfficeAccountList_MINE.get(num)
						.toStringArray());
		isSub = true;
		CustomToast.showToast(SubAccountInfoActivity.this,
				getString(R.string.after_success_subscription), 1000);
		show.setVisibility(View.VISIBLE);
		sub.setVisibility(View.GONE);
		sub_cancel.setVisibility(View.VISIBLE);

		String sub_id = MainApp.preference.getSettings(UserInfo.db_id, "");
		if (sub_id.equals("")) {
			sub_id = MessageInfo.OfficeAccountList_MINE.get(num).getId() + ";";
		} else {
			sub_id = sub_id
					+ MessageInfo.OfficeAccountList_MINE.get(num).getId() + ";";
		}
		MainApp.preference.setSettings(UserInfo.db_id, sub_id);
	}

	private void CancelOfficialAccountSuccess() {
		isSub = false;
		OfficialAccountFragment.instance.mDBOpenHelper.delete(
				MessageInfo.OfficeAccountList_MINE.get(num).getId(), "");
		CustomToast.showToast(
				SubAccountInfoActivity.this,
				getString(R.string.after_cancel_subscription).replace("ta",
						name_string), 1000);
		show.setVisibility(View.INVISIBLE);
		sub.setVisibility(View.VISIBLE);
		sub_cancel.setVisibility(View.GONE);

		String sub_id = MainApp.preference.getSettings(UserInfo.db_id, "");
		if (!sub_id.equals("")) {
			sub_id = sub_id.replaceFirst(MessageInfo.OfficeAccountList_MINE
					.get(num).getId() + ";", "");
		}
		MainApp.preference.setSettings(UserInfo.db_id, sub_id);
	}

	private void changeView() {

		if (isSub) {
			CancelOfficialAccountSuccess();
		} else {
			AddOfficialAccountSuccess();
		}
		closeLoadingDialog();

	}

	public void sub_info_show(View view) {
		Intent intent = new Intent(this, ChatActivity.class);
		intent.putExtra("Id", MessageInfo.OfficeAccountList_MINE.get(num)
				.getId());
		intent.putExtra("name", MessageInfo.OfficeAccountList_MINE.get(num)
				.getRealname());
		intent.putExtra("type", MessageInfo.OFFICEACCOUNT);
		startActivityForResult(intent, 1);
		overridePendingTransition(R.anim.in_from_right, R.anim.out_of_left);
	}

	public void sub_info_sub(View view) {
		showRoundProcessDialog();
		mClientSocket.SendAddOfficialAccount(UserInfo.db_id, toId);

	}

	public void sub_info_cancel(View view) {
		showRoundProcessDialog();
		mClientSocket.SendCancelOfficialAccount(UserInfo.db_id, toId);
	}

	public void sub_info_back(View view) {
		Intent intent = new Intent(this, SubActivity.class);
		intent.putExtra("sub", isSub);
		intent.putExtra("content", MessageInfo.OfficeAccountList_MINE.get(num)
				.getInformation());
		intent.putExtra("fullTime", MessageInfo.OfficeAccountList_MINE.get(num)
				.getTime());
		setResult(1, intent);
		finish();
		overridePendingTransition(R.anim.in_from_left, R.anim.out_of_right);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null)
			return;

		if (data.hasExtra("userId")) {
			String content = data.getStringExtra("content");
			String fullTime = data.getStringExtra("fullTime");
			MessageInfo.OfficeAccountList_MINE.get(num).setInformation(content);
			MessageInfo.OfficeAccountList_MINE.get(num).setTime(fullTime);
		}
	}

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			ChatMsgEntity entity = (ChatMsgEntity) bundle
					.getSerializable("message");

			Log.e("fuck", "=========" + entity);
			if (entity.getChatType() == 4) {
				changeView();
			} else {
				closeLoadingDialog();
			}

		}

	};

	@Override
	public void onPause() {
		super.onPause();
		unRegisterBoradcastReceiver();
	}

	@Override
	protected void onResume() {
		registerBoradcastReceiver();
		super.onResume();
	}

	private void registerBoradcastReceiver() {

		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(MessageInfo.MessageBroadCastName);
		registerReceiver(mBroadcastReceiver, myIntentFilter);

	}

	private void unRegisterBoradcastReceiver() {
		unregisterReceiver(mBroadcastReceiver);
	}
}
