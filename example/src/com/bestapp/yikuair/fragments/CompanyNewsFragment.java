package com.bestapp.yikuair.fragments;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.ListView;

import com.bestapp.yikuair.R;
import com.bestapp.yikuair.database.DBOpenHelper;
import com.bestapp.yikuair.utils.MessageInfo;
import com.bestapp.yikuair.utils.PullToRefreshListView;
import com.bestapp.yikuair.utils.SharedPreferencesUtil;
import com.bestapp.yikuair.utils.StringWidthWeightRandom;
import com.bestapp.yikuair.utils.UploadFileUtil;
import com.bestapp.yikuair.utils.UserInfo;
import com.bestapp.yikuair.utils.PullToRefreshBase.OnRefreshListener;

public class CompanyNewsFragment extends Fragment implements OnClickListener {

	private ListView mListView;
	private ChatMsgViewAdapter mAdapter;
	private List<ChatMsgEntity> chatMsgList = new ArrayList<ChatMsgEntity>();
	public static LinkedList<ChatMsgEntity> mDataArrays = new LinkedList<ChatMsgEntity>();
	private List<ChatMsgEntity> lstMessage = new ArrayList<ChatMsgEntity>();
	private List<ChatMsgEntity> tempList = new ArrayList<ChatMsgEntity>();
	private HashMap<String, Integer> msguidMap = new HashMap<String, Integer>();
	private ChatBroadcastReceiver cbr;
	private static int SHOW_MAX_COUNT = 5;
	public static final int RESULT_OK = -1;
	private int itemCount;
	private String userId;
	private SharedPreferencesUtil chatSharedPre;
	private PullToRefreshListView mPullRefreshListView;
	private ImageButton leftBtn;
	private String prevDate = null;
	private ClientSocket client;
	public static CompanyNewsFragment instance = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		instance = this;
		return inflater.inflate(R.layout.company_news, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		getActivity().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		client = new ClientSocket(getActivity());
		if (UserInfo.companyNews_dbId == null
				|| UserInfo.companyNews_dbId.length() == 0) {
			String dbid = "";
			DBOpenHelper dbOpenHelper = new DBOpenHelper(getActivity());
			SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
			Cursor cursor = db.rawQuery(
					"select * from contactsTable where name=?",
					new String[] { getActivity().getResources().getString(
							R.string.company_news) });
			if (cursor.moveToFirst()) {
				dbid = cursor.getString(14);
			}
			userId = dbid;
			cursor.close();
			dbOpenHelper.close();
		} else
			userId = UserInfo.companyNews_dbId;

		Log.e("test", "companynews dbid :" + userId);

		initChatView();
		updateChatListView();

		// register broadcast
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(MessageInfo.MessageBroadCastName);
		cbr = new ChatBroadcastReceiver();
		getActivity().registerReceiver(cbr, myIntentFilter);
	}

	public class ChatBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i("test", "companynews broadcast receive...................");
			ChatMsgEntity entity = new ChatMsgEntity();
			Bundle bundle = intent.getExtras();
			Serializable data = bundle.getSerializable("message");
			if (data != null) {
				entity = (ChatMsgEntity) data;
				if (entity.getSenderId().equals(userId)
						|| entity.getReceiverId().equals(userId)) {
					Log.i("test", "company news new ...........");

					if (prevDate == null) {
						if (chatSharedPre.getChatDate(userId + "_"
								+ UserInfo.db_id) != null
								&& chatSharedPre.getChatDate(
										userId + "_" + UserInfo.db_id).length() > 0) {
							// Log.e("test", "asdfasdfafasdfasdfasdf");
							prevDate = chatSharedPre.getChatDate(userId + "_"
									+ UserInfo.db_id);
							if (MessageInfo.getChattingDate().equals(prevDate)) {
								entity.setDate("");
							} else {
								prevDate = MessageInfo.getChattingDate();
								entity.setDate(prevDate);
							}
						} else {
							prevDate = MessageInfo.getChattingDate();
							entity.setDate(prevDate);
						}
					} else {

						if (prevDate.equals(MessageInfo.getChattingDate())) {
							entity.setDate("");
						} else {
							prevDate = MessageInfo.getChattingDate();
							entity.setDate(prevDate);
						}
					}
					updateChatStatus(entity);
				}
			}
		}
	}

	public void getLocalMessage(List<ChatMsgEntity> chatInfoList) {
		List<ChatMsgEntity> tempList = new ArrayList<ChatMsgEntity>();
		// chatSharedPre.getUserInfo();

		if (chatSharedPre.readDataFromShared(userId + "_" + UserInfo.db_id) != null) {
			lstMessage = chatSharedPre.readDataFromShared(userId + "_"
					+ UserInfo.db_id);
			itemCount = lstMessage.size();
			if (itemCount > SHOW_MAX_COUNT) {
				tempList = lstMessage.subList(itemCount - SHOW_MAX_COUNT,
						itemCount);
				itemCount -= SHOW_MAX_COUNT;
				for (int j = 0; j < tempList.size(); j++) {
					updateChatView(tempList.get(j), null, false);
				}
			} else {
				itemCount = 0;
				for (int j = 0; j < lstMessage.size(); j++) {
					updateChatView(lstMessage.get(j), null, false);
				}
			}
		}

		if (chatInfoList != null) {
			Log.i("test",
					"company news chatInfoList.size : " + chatInfoList.size());
			for (int i = 0; i < chatInfoList.size(); i++) {
				Log.e("test", "receive localnew message");
				String tempReceiver = UserInfo.db_id;
				String tempSender = userId;
				int tempType = 1;
				client.sendMessage(null, 8, chatInfoList.get(i).getMsguuid(),
						tempSender, tempReceiver, null, null, null, null,
						String.valueOf(tempType), null, false);

				if (prevDate == null) {
					if (chatSharedPre
							.getChatDate(userId + "_" + UserInfo.db_id) != null
							&& chatSharedPre.getChatDate(
									userId + "_" + UserInfo.db_id).length() > 0) {
						prevDate = chatSharedPre.getChatDate(userId + "_"
								+ UserInfo.db_id);
						if (MessageInfo.getChattingDate().equals(prevDate)) {
							chatInfoList.get(i).setDate("");
						} else {
							prevDate = MessageInfo.getChattingDate();
							chatInfoList.get(i).setDate(prevDate);
						}
					} else {
						prevDate = MessageInfo.getChattingDate();
						chatInfoList.get(i).setDate(prevDate);
					}
				} else {

					if (prevDate.equals(MessageInfo.getChattingDate())) {
						chatInfoList.get(i).setDate("");
					} else {
						prevDate = MessageInfo.getChattingDate();
						chatInfoList.get(i).setDate(prevDate);
					}
				}
				updateChatView(chatInfoList.get(i), null, false);
			}
		}
	}

	private class GetDataTask extends
			AsyncTask<Void, Void, List<ChatMsgEntity>> {

		@Override
		protected List<ChatMsgEntity> doInBackground(Void... params) {
			// Simulates a background job.
			try {
				tempList.clear();
				if (itemCount > 0) {
					int count = (itemCount - SHOW_MAX_COUNT > 0 ? SHOW_MAX_COUNT
							: itemCount);
					int begin = (count == SHOW_MAX_COUNT ? (itemCount
							- SHOW_MAX_COUNT - 1) : 0);

					for (int i = itemCount - 1; i > begin - 1; i--) {
						tempList.add(lstMessage.get(i));
					}
					itemCount -= count;
				}
				Thread.sleep(500);
			} catch (Exception e) {

			}
			return mDataArrays;
		}

		@Override
		protected void onPostExecute(List<ChatMsgEntity> result) {

			if (tempList.size() > 0) {
				for (int i = 0; i < tempList.size(); i++)
					mDataArrays.addFirst(tempList.get(i));
				mAdapter.notifyDataSetChanged();
			}

			// Call onRefreshComplete when the list has been refreshed.
			mPullRefreshListView.onRefreshComplete();
			super.onPostExecute(result);
		}
	}

	public void initChatView() {
		mPullRefreshListView = (PullToRefreshListView) getActivity()
				.findViewById(R.id.pull_refresh_list);
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				new GetDataTask().execute();
			}
		});

		mListView = mPullRefreshListView.getRefreshableView();
		mListView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return false;
			}
		});

		leftBtn = (ImageButton) getActivity().findViewById(
				R.id.message_left_btn);
		leftBtn.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				sendMessageBroadcast();
				return false;
			}
		});

		chatSharedPre = new SharedPreferencesUtil(getActivity());
		mAdapter = new ChatMsgViewAdapter(getActivity(), mDataArrays);
		mListView.setAdapter(mAdapter);

		Log.i("test", "userID: " + userId);
		chatMsgList.addAll(MessageInfo.menuCompanyNewsList);
		MessageInfo.menuCompanyNewsList.clear();

		getLocalMessage(chatMsgList);
	}

	public void updateChatStatus(ChatMsgEntity entity) {
		int status = entity.getStatus();
		if (status == MessageInfo.RECEIVE_MESSAGE) {
			String senderId = entity.getSenderId();
			DBOpenHelper dbOpenHelper = new DBOpenHelper(getActivity());
			Cursor cursor;
			SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
			String sex = "";
			cursor = db.rawQuery("select * from contactsTable where dbid=?",
					new String[] { senderId });
			if (cursor.moveToFirst()) {
				sex = cursor.getString(13);
			}
			dbOpenHelper.close();
			cursor.close();
			entity.setSex(sex);
			updateChatView(entity, null, false);
			return;
		} else {
			if (entity.getMsguuid() == null)
				return;
			int listId = msguidMap.get(entity.getMsguuid());
			Log.i("test", "listId :" + msguidMap.get(entity.getMsguuid()));
			if (status == MessageInfo.SEND_ARRIVAL) {
				Log.i("test", "listId :" + listId);
				Log.i("test", "TYPE :" + mDataArrays.get(listId).getType());
				mDataArrays.get(listId).setAnimVisibile(View.GONE);
				mDataArrays.get(listId).setStateVisible(View.VISIBLE);
			} else if (status == MessageInfo.SEND_READED) {
				mDataArrays.get(listId).setBackground(R.drawable.ima_readed);
				mDataArrays.get(listId).setChatState(
						getResources().getString(R.string.readed));
			}
		}
		mAdapter.notifyDataSetChanged();
	}

	public void updateChatView(ChatMsgEntity entity, String msguuid,
			boolean isSend) {
		if (entity == null) {
			return;
		}

		entity.setMessageType(MessageInfo.COMPANY_NEWS);

		mDataArrays.add(entity);
		mAdapter.notifyDataSetChanged();
		if (isSend) {
			msguidMap.put(msguuid, mAdapter.getCount() - 1);
		}
		mListView.setSelection(mListView.getCount() - 1);
	}

	public void messageMenu() {
		Intent intent = new Intent(getActivity(), MessageTopDialog.class);
		intent.putExtra("userId", userId);
		startActivity(intent);
	}

	public void openTaskMember() {
		Intent intent = new Intent(getActivity(), TaskMemberActivity.class);
		startActivity(intent);
		getActivity().overridePendingTransition(R.anim.in_from_right,
				R.anim.out_of_left);
	}

	public void updateChatListView() {
		if (MessageInfo.messageEntityList != null
				&& MessageInfo.messageEntityList.size() > 0) {
			for (int i = 0; i < MessageInfo.messageEntityList.size(); i++) {
				ChatMsgEntity entity = MessageInfo.messageEntityList.get(i);
				if (entity.getSenderId().equals(userId)
						|| entity.getReceiverId().equals(userId)) {
					updateChatStatus(entity);
					MessageInfo.messageEntityList.remove(i);
				}
			}
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		if (UserInfo.isHomePressed) {
			ClientSocket client = new ClientSocket(getActivity());
			UserInfo.isSendBroadCast = false;

			UserInfo.isHomePressed = false;

			client.sendMessage(null, 0,
					StringWidthWeightRandom.getNextString(), null, null, null,
					null, null, null, null, null, true);
		}
		Log.i("test", "companynews onStart");
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.i("test", "companynews onResume");
	}

	@Override
	public void onPause() {
		super.onPause();
		MessageInfo.isChatting = false;
		Log.i("test", "companynews onPause");
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.e("test", "companynews :" + userId);
		Log.e("test", "size :" + mDataArrays.size());
		instance = null;
		chatSharedPre.saveDatatoShared(userId + "_" + UserInfo.db_id,
				mDataArrays);
		
	    if(prevDate == null || prevDate.length() == 0)
			prevDate = MessageInfo.getChattingDate();
		chatSharedPre.saveChatDate(userId + "_" + UserInfo.db_id, prevDate);

		if (cbr != null) {
			getActivity().unregisterReceiver(cbr);
			cbr = null;
		}
		Log.i("test", "companynews onStop");
	}

	@Override
	public void onDestroy() {
		Log.i("test", "companynews onDestroy");
		mDataArrays.clear();
		super.onDestroy();
	}

	// for deliver unreaded message count
	private void sendMessageBroadcast() {
		Intent intent = new Intent();
		intent.setAction(MessageInfo.MessageBroadCastName);
		intent.putExtra("name",
				getActivity().getResources().getString(R.string.company_news));
		getActivity().sendBroadcast(intent);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
	}
}
