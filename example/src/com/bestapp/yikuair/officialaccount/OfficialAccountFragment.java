package com.bestapp.yikuair.officialaccount;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bestapp.yikuair.MainApp;
import com.bestapp.yikuair.R;
import com.bestapp.yikuair.adapter.SubscripionAccountAdapter;
import com.bestapp.yikuair.database.DBOpenHelper;
import com.bestapp.yikuair.fragments.ChatActivity;
import com.bestapp.yikuair.fragments.ChatMsgEntity;
import com.bestapp.yikuair.fragments.ClientSocket;
import com.bestapp.yikuair.utils.AccountInfomation;
import com.bestapp.yikuair.utils.AsyncImageLoader;
import com.bestapp.yikuair.utils.DBlog;
import com.bestapp.yikuair.utils.MessageInfo;
import com.bestapp.yikuair.utils.SharedPreferencesUtil;
import com.bestapp.yikuair.utils.UserInfo;

public class OfficialAccountFragment extends Fragment implements
		OnClickListener, OnItemClickListener, OnItemLongClickListener {

	private ListView mListView;
	private SubscripionAccountAdapter mAdapter;
	private ArrayList<AccountInfomation> mInfos = new ArrayList<AccountInfomation>();
	private ImageView NO_DATA;
	public static OfficialAccountFragment instance;
	private Dialog mDialog;
	private int showDeleteId = 0;
	private boolean change = false;
	public DBOpenHelper mDBOpenHelper;
	public AsyncImageLoader asyncImageLoader;
	public ClientSocket mClientSocket;

	public void removeInfo(String id) {
		mDBOpenHelper.delete(id, null);
		MainApp.preference.removeSetting(UserInfo.db_id + id + "id");
		MainApp.preference.removeSetting(UserInfo.db_id + id + "info");
		MainApp.preference.removeSetting(UserInfo.db_id + id + "time");
		MainApp.preference.removeSetting(UserInfo.db_id + id + "num");
	}

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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.activity_main, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
		chatSharedPre = new SharedPreferencesUtil(getActivity());
		instance = this;
	}

	@Override
	public void onDestroy() {
		Loaded = false;
		instance = null;
		DBlog.e("destory", "---------------------");
		super.onDestroy();
	};

	@Override
	public void onPause() {
		// for (AccountInfomation iterable_element : mInfos) {
		// saveInfo(iterable_element);
		// }
		unregisterBroadcast();
		for (AccountInfomation iterable_element : MessageInfo.OfficeAccountList_MINE) {
			saveInfo(iterable_element);
		}

		sendBroadcast();
		super.onPause();
	};

	private void sendBroadcast() {
		Intent intent = new Intent();
		intent.putExtra("name", "office");
		intent.putExtra("number", MessageInfo.OfficeAccountList.size());
		intent.setAction(MessageInfo.FriendBroadCastName);
		getActivity().sendBroadcast(intent);
	}

	private static boolean Loaded = false;

	@Override
	public void onResume() {
		id = "";
		registerBroadcast();
		super.onResume();
	};

	private void initView() {
		registerBroadcast();
		// mInfos = (ArrayList<AccountInfomation>)
		// MessageInfo.OfficeAccountList_MINE;
		mClientSocket = new ClientSocket(getActivity());
		mDBOpenHelper = new DBOpenHelper(getActivity());
		asyncImageLoader = new AsyncImageLoader(getActivity());
		View view = getActivity().findViewById(R.id.offcical_content);
		NO_DATA = (ImageView) getActivity().findViewById(R.id.no_data);
		if (MessageInfo.OfficeAccountList_MINE.size() > 0) {
			NO_DATA.setVisibility(View.GONE);
			for (AccountInfomation element : MessageInfo.OfficeAccountList_MINE) {
				getInfo(element);
			}
		} else {
			NO_DATA.setVisibility(View.VISIBLE);

		}
		mListView = (ListView) getActivity()
				.findViewById(R.id.accout_info_list);
		mAdapter = new SubscripionAccountAdapter(
				getActivity(),
				(ArrayList<AccountInfomation>) MessageInfo.OfficeAccountList_MINE);
		mListView.setAdapter(mAdapter);
		ImageButton suButton = (ImageButton) getActivity().findViewById(
				R.id.sub_button);
		mListView.setOnItemClickListener(this);
		mListView.setOnItemLongClickListener(this);
		view.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_DOWN
						&& showDeleteId != 0) {
					mAdapter.notifyDataSetChanged();
					showDeleteId = 0;
				}
				return false;
			}
		});
		getMessage();
		suButton.setOnClickListener(this);
	}

	public void getMessage() {

		if (MessageInfo.OfficeAccountList.size() > 0) {
			for (ChatMsgEntity entity : MessageInfo.OfficeAccountList) {
				AccountInfomation accountInfomation = new AccountInfomation();
				accountInfomation.setId(entity.getSenderId());
				accountInfomation.setTime(entity.getTime());
				accountInfomation.setInformation(entity.getContent());
				if (MessageInfo.OfficeAccountList_MINE
						.contains(accountInfomation)) {
					int index = MessageInfo.OfficeAccountList_MINE
							.indexOf(accountInfomation);
					AccountInfomation older = MessageInfo.OfficeAccountList_MINE
							.get(index);
					if (entity.getStatus() != MessageInfo.SEND_READED) {
						accountInfomation
								.setInfor_num(older.getInfor_num() + 1);
					}
					accountInfomation.setRealname(older.getRealname());
					accountInfomation.setHeadurl(older.getHeadurl());
					accountInfomation.setSub(older.isSub());
					MessageInfo.OfficeAccountList_MINE.set(index,
							accountInfomation);
				}
			}
		}
		mAdapter.notifyDataSetChanged();
		if (MessageInfo.OfficeAccountList_MINE.size() == 0) {
			NO_DATA.setVisibility(View.VISIBLE);
		} else {

			NO_DATA.setVisibility(View.GONE);
		}

		if (!id.equals("")) {
			Log.e("test", "send officialaccotn broadcast");

			Intent intent = new Intent();
			intent.setAction(MessageInfo.MessageBroadCastName);
			intent.putExtra("Id", id);
			intent.putExtra("name", name);
			intent.putExtra("type", MessageInfo.OFFICEACCOUNT);
			intent.putExtra("message", (getChatsById(id).get(0)));
			getActivity().sendBroadcast(intent);
		}

	}

	private void saveInfo(AccountInfomation accountInfomation) {
		MainApp.preference.setSettings(
				UserInfo.db_id + accountInfomation.getId() + "id",
				accountInfomation.getId());
		MainApp.preference.setSettings(
				UserInfo.db_id + accountInfomation.getId() + "info",
				accountInfomation.getInformation());
		MainApp.preference.setSettings(
				UserInfo.db_id + accountInfomation.getId() + "time",
				accountInfomation.getTime());
		MainApp.preference.setSettings(
				UserInfo.db_id + accountInfomation.getId() + "num",
				accountInfomation.getInfor_num());
	}

	private AccountInfomation getInfo(AccountInfomation accountInfomation) {
		if (accountInfomation.getInformation() == null
				|| accountInfomation.getInformation().equals("")) {
			accountInfomation.setInformation(MainApp.preference.getSettings(
					UserInfo.db_id + accountInfomation.getId() + "info", ""));
			accountInfomation.setTime(MainApp.preference.getSettings(
					UserInfo.db_id + accountInfomation.getId() + "time", ""));
			accountInfomation.setInfor_num(Integer.parseInt(MainApp.preference
					.getSettings(UserInfo.db_id + accountInfomation.getId()
							+ "num", "0")));
		}
		for (ChatMsgEntity element : MessageInfo.OfficeAccountList) {
			if (element.getSenderId().equals(accountInfomation.getId())) {
				accountInfomation.setInfor_num(0);
			}
		}
		return accountInfomation;

	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null)
			return;

		if (data.hasExtra("AccountInfomationList")) {
			ArrayList<AccountInfomation> newlist = data
					.getParcelableArrayListExtra("AccountInfomationList");
			if (newlist.size() != MessageInfo.OfficeAccountList_MINE.size()) {
				change = true;
			}
			for (AccountInfomation accountInfomation : MessageInfo.OfficeAccountList_MINE) {
				if (newlist.contains(accountInfomation)) {
					int index = newlist.indexOf(accountInfomation);
					AccountInfomation obj = newlist.get(index);
					if (obj.getTime() != null && obj.getTime().equals("")) {
						newlist.get(index).setTime(accountInfomation.getTime());
					} else {
						change = true;
					}
				}

			}
			MessageInfo.OfficeAccountList_MINE.clear();
			MessageInfo.OfficeAccountList_MINE.addAll(newlist);

		} else if (data.hasExtra("userId")) {

			String content = data.getStringExtra("content");
			String userId = data.getStringExtra("userId");
			String fullTime = data.getStringExtra("fullTime");
			if (content != null && !content.equals("")) {
				change = true;
				// AccountInfomation accountInfomation = new
				// AccountInfomation();
				// accountInfomation.setId(userId);
				// accountInfomation.setTime(fullTime);
				// accountInfomation.setInformation(content);
				// accountInfomation.setSub(true);
				// int index = mInfos.indexOf(accountInfomation);
				// if (index != -1) {
				// accountInfomation.setRealname(mInfos.get(index)
				// .getRealname());
				// accountInfomation
				// .setHeadurl(mInfos.get(index).getHeadurl());
				// mInfos.set(index, accountInfomation);
				// }
			}

		}
		if (change) {
			mAdapter.notifyDataSetChanged();
			change = false;
		}

	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(getActivity(), SubActivity.class);
		// intent.putParcelableArrayListExtra("sub_list", mInfos);
		startActivityForResult(intent, 1);
		getActivity().overridePendingTransition(R.anim.in_from_right,
				R.anim.out_of_left);
	}

	private String id = "";
	private String name;

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		MessageInfo.OfficeAccountList_MINE.get(arg2).setInfor_num(0);
		Intent intent = new Intent(getActivity(), ChatActivity.class);
		id = MessageInfo.OfficeAccountList_MINE.get(arg2).getId();
		name = MessageInfo.OfficeAccountList_MINE.get(arg2).getRealname();
		intent.putExtra("Id", id);
		intent.putExtra("name", MessageInfo.OfficeAccountList_MINE.get(arg2)
				.getRealname());
		intent.putExtra("type", MessageInfo.OFFICEACCOUNT);

		intent.putExtra("chatmsgList", (Serializable) (getChatsById(id)));
		startActivityForResult(intent, 1);
		getActivity().overridePendingTransition(R.anim.in_from_right,
				R.anim.out_of_left);
		sendBroadcast();
	}

	private ArrayList<ChatMsgEntity> getChatsById(String id) {
		ArrayList<ChatMsgEntity> newlist = new ArrayList<ChatMsgEntity>();
		for (ChatMsgEntity chatMsgEntity : MessageInfo.OfficeAccountList) {
			if (chatMsgEntity.getSenderId().equals(id)) {
				newlist.add(chatMsgEntity);
				DBlog.e("t", chatMsgEntity.getType() + "");
			}
		}

		MessageInfo.OfficeAccountList.removeAll(newlist);

		return newlist;
	}

	private SharedPreferencesUtil chatSharedPre;
	public static LinkedList<ChatMsgEntity> mDataArrays = new LinkedList<ChatMsgEntity>();

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		Button textView = (Button) arg1.findViewById(R.id.delete_info);
		textView.setVisibility(View.VISIBLE);
		Animation animation = AnimationUtils.loadAnimation(getActivity(),
				R.anim.in_from_right);
		textView.setAnimation(animation);
		animation.start();
		showDeleteId++;
		textView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int position = (Integer) v.getTag();
				removeInfo(MessageInfo.OfficeAccountList_MINE.get(position)
						.getId());
				MessageInfo.OfficeAccountList_MINE.get(position)
						.setInformation("");
				MessageInfo.OfficeAccountList_MINE.get(position).setTime("");
				mAdapter.notifyDataSetChanged();
				chatSharedPre.saveDatatoShared(
						MessageInfo.OfficeAccountList_MINE.get(position)
								.getId() + "_" + UserInfo.db_id, mDataArrays);
				showDeleteId = 0;
			}
		});
		textView.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					v.setVisibility(View.GONE);
					showDeleteId = 0;
				}
			}
		});
		return true;
	}

	BroadcastReceiver fbt = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			if (arg1 == null)
				return;
			if (arg1.getStringExtra("name").equals("office")) {
				getMessage();
			}
		}
	};

	private void registerBroadcast() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(MessageInfo.FriendMessageBroadCastName);
		getActivity().registerReceiver(fbt, intentFilter);
	}

	private void unregisterBroadcast() {
		getActivity().unregisterReceiver(fbt);
	}
}
