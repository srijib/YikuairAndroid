package com.bestapp.yikuair.officialaccount;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.bestapp.yikuair.OfficialAccountBaseActivity;
import com.bestapp.yikuair.R;
import com.bestapp.yikuair.adapter.SubscriptionAdapter;
import com.bestapp.yikuair.fragments.ChatMsgEntity;
import com.bestapp.yikuair.utils.AccountInfomation;
//import com.bestapp.yikuair.utils.Client;
import com.bestapp.yikuair.utils.CustomToast;
import com.bestapp.yikuair.utils.DBlog;
import com.bestapp.yikuair.utils.MessageInfo;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class SubActivity extends OfficialAccountBaseActivity implements
		OnItemClickListener {

	public static SubActivity instance;
	private ListView mSubListView;
	private int itemID;
	private SubscriptionAdapter subscriptionAdapter;
	// private ArrayList<AccountInfomation> mInfos = new
	// ArrayList<AccountInfomation>();
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

	private void AddOfficialAccountSuccess(AccountInfomation subscripitionInfo) {
		CustomToast.showToast(SubActivity.this,
				R.string.after_success_subscription, 1000);
		subscripitionInfo.setSub(true);
	}

	private void CancelOfficialAccountSuccess(
			AccountInfomation subscripitionInfo) {

		CustomToast.showToast(SubActivity.this,
				SubActivity.this.getString(R.string.after_cancel_subscription)
						.replace("ta", subscripitionInfo.getRealname()), 1000);
		subscripitionInfo.setSub(false);
	}

	private boolean refresh = true;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (data == null)
			return;
		if (resultCode == 1) {
			boolean tag = data.getBooleanExtra("sub", false);
			if (MessageInfo.OfficeAccountList_MINE.get(itemID).isSub() != data
					.getBooleanExtra("sub", false)) {
				MessageInfo.OfficeAccountList_MINE.get(itemID).setSub(tag);
				subscriptionAdapter.notifyDataSetChanged();

			}
			if (tag) {
				String content = data.getStringExtra("content");
				String fullTime = data.getStringExtra("fullTime");
				MessageInfo.OfficeAccountList_MINE.get(itemID)
						.setTime(fullTime);
				MessageInfo.OfficeAccountList_MINE.get(itemID).setInformation(
						content);
			}
		}

		super.onActivityResult(requestCode, resultCode, data);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sub);
		instance = this;
		refresh = true;
		initView();
		mDialog = new AlertDialog.Builder(this).create();

	}

	@Override
	protected void onDestroy() {
		refresh = true;
		instance = null;

		super.onDestroy();
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

//	@Override
//	public void onWindowFocusChanged(boolean hasFocus) {
//		refreshAccountList();
//		refresh = false;
//		super.onWindowFocusChanged(hasFocus);
//	};

//	private void refreshAccountList() {
//		if (refresh) {
//			getInfomation();
//		}
//	}

//	private void getInfomation() {
//		Client.get(ResponseHandler);
//	}

	final AsyncHttpResponseHandler ResponseHandler = new AsyncHttpResponseHandler() {
		@Override
		public void onSuccess(int arg0, String arg1) {
			DBlog.e("onSuccess", arg1);

			getJson(arg1);
		};

		@Override
		public void onFailure(Throwable arg0, String arg1) {
			DBlog.e("onFailure", arg1);
		};
	};

	private void getJson(String josn) {
		try {
			JSONObject jsonObject = new JSONObject(josn.toString());
			JSONArray jsonArray = jsonObject.getJSONArray("data");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject2 = (JSONObject) jsonArray.opt(i);
				AccountInfomation accountInfomation = new AccountInfomation();

				accountInfomation.setRealname(jsonObject2.getString("realname")
						.replace(" ", ""));
				accountInfomation.setHeadurl(jsonObject2.getString("headurl"));
				accountInfomation.setId(jsonObject2.getString("id"));
				if (accountInfomation.getId().equals("1")
						|| accountInfomation.getId().equals("2")) {
					continue;
				}
				accountInfomation.setSignature(jsonObject2
						.getString("signature"));
				if (!MessageInfo.OfficeAccountList_MINE
						.contains(accountInfomation)) {
					accountInfomation.setSub(false);
					MessageInfo.OfficeAccountList_MINE.add(accountInfomation);
				}
			}
			if (MessageInfo.OfficeAccountList_MINE.size() > 0)
				subscriptionAdapter.notifyDataSetChanged();
		} catch (JSONException e) {
			DBlog.e("JSONException", e.toString());
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	private void initView() {

		Intent intent = getIntent();
		// mInfos.addAll((Collection<? extends AccountItion>) intent
		// .getParcelableArrayListExtra("sub_list")nfoma);
		// mInfos = (ArrayList<AccountInfomation>)
		// MessageInfo.OfficeAccountList_MINE;
		mSubListView = (ListView) findViewById(R.id.sub_list);
		subscriptionAdapter = new SubscriptionAdapter(
				this,
				(ArrayList<AccountInfomation>) MessageInfo.OfficeAccountList_MINE,
				getItemHeight());
		mSubListView.setAdapter(subscriptionAdapter);
		mSubListView.setOnItemClickListener(this);
		View back = findViewById(R.id.sub_back);

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SubActivity.this,
						OfficialAccountFragment.class);
				ArrayList<AccountInfomation> newList = new ArrayList<AccountInfomation>();
				for (AccountInfomation iterable_element : MessageInfo.OfficeAccountList_MINE) {
					if (iterable_element.isSub()) {
						newList.add(iterable_element);
					}
				}
				intent.putParcelableArrayListExtra("AccountInfomationList",
						newList);
				setResult(1, intent);
				finish();
				overridePendingTransition(R.anim.in_from_left,
						R.anim.out_of_right);

			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		itemID = arg2;
		Intent intent = new Intent(SubActivity.this,
				SubAccountInfoActivity.class);
		// intent.putExtra("AccountInfomation",
		// MessageInfo.OfficeAccountList_MINE.get(arg2));
		intent.putExtra("AccountInfomation", arg2);
		startActivityForResult(intent, 1);
		overridePendingTransition(R.anim.in_from_right, R.anim.out_of_left);

	}

	public void SendAddOfficialAccount(String fromId, String toId) {
		OfficialAccountFragment.instance.mClientSocket.SendAddOfficialAccount(
				fromId, toId);
	}

	public void sendCancelOfficiaAccount(String fromId, String toId) {
		OfficialAccountFragment.instance.mClientSocket
				.SendCancelOfficialAccount(fromId, toId);
	}

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			ChatMsgEntity entity = (ChatMsgEntity) bundle
					.getSerializable("message");
			if (entity.getChatType() == 4) {
				final AccountInfomation subscripitionInfo = MessageInfo.OfficeAccountList_MINE
						.get(subscriptionAdapter.getChangeId());
				if (subscripitionInfo.isSub()) {
					CancelOfficialAccountSuccess(subscripitionInfo);
				} else {
					AddOfficialAccountSuccess(subscripitionInfo);
				}
				subscriptionAdapter.notifyDataSetChanged();
			} else {

			}

			closeLoadingDialog();
		}

	};

	private String ACTION_NAME = MessageInfo.MessageBroadCastName;

	private void registerBoradcastReceiver() {

		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(ACTION_NAME);
		registerReceiver(mBroadcastReceiver, myIntentFilter);

	}

	private void unRegisterBoradcastReceiver() {
		unregisterReceiver(mBroadcastReceiver);
	}
}
