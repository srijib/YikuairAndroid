package com.bestapp.yikuair.officialaccount;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.bestapp.yikuair.MainApp;
import com.bestapp.yikuair.OfficialAccountBaseActivity;
import com.bestapp.yikuair.R;
import com.bestapp.yikuair.adapter.PairFriendAdapter;
import com.bestapp.yikuair.fragments.ChatActivity;
import com.bestapp.yikuair.fragments.ChatMsgEntity;
import com.bestapp.yikuair.utils.AccountInfomation;
import com.bestapp.yikuair.utils.Client;
import com.bestapp.yikuair.utils.DBlog;
import com.bestapp.yikuair.utils.MessageInfo;
import com.bestapp.yikuair.utils.SharedPreferencesUtil;
import com.bestapp.yikuair.utils.UserInfo;
import com.loopj.android.http.AsyncHttpResponseHandler;


public class PairFriendActivity extends OfficialAccountBaseActivity implements
		OnItemClickListener {

	private ArrayList<AccountInfomation> mInfo = new ArrayList<AccountInfomation>();
	private PairFriendAdapter mAdapter;
	private ListView mListView;
	private ImageView no_people;
	private FriendChatBroadcastReceiver fbr;
	private SharedPreferencesUtil chatSharedPre;
	private List<ChatMsgEntity> lstMessage = new ArrayList<ChatMsgEntity>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pair_friend);
		chatSharedPre = new SharedPreferencesUtil(this);
		initView();
		new Thread(new Runnable() {

			@Override
			public void run() {
				Client.loadingMatchFriend(ResponseHandler);
			}
		}).start();

	}

	private void initView() {
		fbr = new FriendChatBroadcastReceiver();
		registerBroadcast();
		mListView = (ListView) findViewById(R.id.pair_friend_list);
		mListView.setOnItemClickListener(this);
		mAdapter = new PairFriendAdapter(PairFriendActivity.this, mInfo);
		mListView.setAdapter(mAdapter);
		no_people = (ImageView) findViewById(R.id.friend_list_null);
		if (mInfo.isEmpty()) {
			no_people.setVisibility(View.VISIBLE);
		} else {
			no_people.setVisibility(View.GONE);
		}
	}

	public void onBack(View view) {
		finish();
	}

	@Override
	public void onPause() {

		for (AccountInfomation element : mInfo) {
			saveInfo(element);

		}
		unregisterBroadcast();
		super.onPause();
	}

	@Override
	protected void onRestart() {
		registerBroadcast();
		super.onRestart();
	};

	private void saveInfo(AccountInfomation accountInfomation) {
		MainApp.preference.setSettings(
				UserInfo.db_id + accountInfomation.getId() + "friend_id",
				accountInfomation.getId());
		MainApp.preference.setSettings(
				UserInfo.db_id + accountInfomation.getId() + "friend_info",
				accountInfomation.getInformation());
		MainApp.preference.setSettings(
				UserInfo.db_id + accountInfomation.getId() + "friend_time",
				accountInfomation.getTime());
		MainApp.preference.setSettings(
				UserInfo.db_id + accountInfomation.getId() + "friend_num",
				accountInfomation.getInfor_num());
	}

	private AccountInfomation getInfo(AccountInfomation accountInfomation) {

		if (accountInfomation.getInformation() == null
				|| accountInfomation.getInformation().equals("")) {
			accountInfomation.setInformation(MainApp.preference.getSettings(
					UserInfo.db_id + accountInfomation.getId() + "friend_info",
					""));
			accountInfomation.setTime(MainApp.preference.getSettings(
					UserInfo.db_id + accountInfomation.getId() + "friend_time",
					""));
			accountInfomation.setInfor_num(Integer.parseInt(MainApp.preference
					.getSettings(UserInfo.db_id + accountInfomation.getId()
							+ "friend_num", "0")));
		}
		for (ChatMsgEntity element : MessageInfo.matchMessageEntityList) {
			if (element.getSenderId().equals(accountInfomation.getId())) {
				accountInfomation.setInfor_num(0);
			}
		}
		return accountInfomation;

	}

	private void getMessage() {

		if (MessageInfo.matchMessageEntityList.size() > 0) {
			for (ChatMsgEntity entity : MessageInfo.matchMessageEntityList) {
				AccountInfomation accountInfomation = new AccountInfomation();
				accountInfomation.setId(entity.getSenderId());
				accountInfomation.setTime(entity.getTime());
				accountInfomation.setInformation(entity.getContent());
				if (mInfo.contains(accountInfomation)) {
					int index = mInfo.indexOf(accountInfomation);
					AccountInfomation older = mInfo.get(index);
					if (entity.getStatus() != MessageInfo.SEND_READED) {
						accountInfomation
								.setInfor_num(older.getInfor_num() + 1);
					}
					accountInfomation.setTime(older.getTime());
					accountInfomation.setNickname(older.getNickname());
					accountInfomation.setHeadurl(older.getHeadurl());
					accountInfomation.setDistance(older.getDistance());
					mInfo.set(index, accountInfomation);
				}
			}
		}
	}

	final AsyncHttpResponseHandler ResponseHandler = new AsyncHttpResponseHandler() {
		@Override
		public void onSuccess(int arg0, String arg1) {
			DBlog.e("onSuccess", arg1);
			// loading friends
			getFriendJson(arg1);
		}
	};

	private ArrayList<AccountInfomation> getFriendJson(String content) {
		JSONObject object;
		ArrayList<AccountInfomation> list = new ArrayList<AccountInfomation>();
		try {
			object = new JSONObject(content);

			if (object.has("message")) {
				if (object.getString("message").trim().equals("success")) {
					JSONArray array = object.getJSONArray("data");
					for (int i = 0; i < array.length(); i++) {
						AccountInfomation accountInfomation = new AccountInfomation();
						ChatMsgEntity chat = new ChatMsgEntity();
						JSONObject object2 = (JSONObject) array.opt(i);
						String id = object2.getString("id");
						chat.setUserId(id);
						String headurl = object2.getString("headurl");
						String sex = object2.getString("sex");
						String lan = object2.getString("lan");
						String lon = object2.getString("lon");
						String distance = getDistatce(UserInfo.lan,
								Double.parseDouble(lan), UserInfo.lon,
								Double.parseDouble(lon));
						String time = getShowTime(object2
								.getString("uploadTime"));
						String nickname = object2.getString("nickname");
						accountInfomation.setId(id);
						accountInfomation.setHeadurl(headurl);
						accountInfomation.setSex(sex);
						accountInfomation.setDistance(distance);
						accountInfomation.setNickname(nickname);
						accountInfomation.setTime(time);
						accountInfomation = getInfo(accountInfomation);
						mInfo.add(accountInfomation);
						lstMessage.add(chat);
						DBlog.e("----", accountInfomation.toString());

					}
					getMessage();
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (mInfo.size() > 0) {
			no_people.setVisibility(View.GONE);
			mAdapter.notifyDataSetChanged();
		}
		chatSharedPre.saveDatatoShared(UserInfo.db_id + "_friend_list",
				lstMessage);
		return list;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		mInfo.get(arg2).setInfor_num(0);
		Intent intent = new Intent(this, ChatActivity.class);
		String id = mInfo.get(arg2).getId();
		String name = mInfo.get(arg2).getNickname();
		intent.putExtra("Id", id);
		intent.putExtra("name", name);
		intent.putExtra("fromname", name);
		DBlog.e("frome", name);
		intent.putExtra("chatmsgList", (Serializable) (getChatsById(id)));
		startActivityForResult(intent, 1);
		overridePendingTransition(R.anim.in_from_right, R.anim.out_of_left);
	}

	private String getShowTime(String time) {
		if (time == null || time.equals(""))
			return "";
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date now = null;
		Date date = null;

		long l = 0L;
		try {
			now = new Date();
			date = df.parse(time);
			l = now.getTime() - date.getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int day = (int) (l / (24 * 60 * 60 * 1000));
		int hour = (int) (l / (60 * 60 * 1000) - day * 24);
		int min = (int) ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
		StringBuffer timer = new StringBuffer();
		if (day != 0) {
			timer.append(day + "天");
			timer.append("前");
			return timer.toString();
		}
		if (hour != 0) {
			timer.append(hour + "小时");
			timer.append("前");
			return timer.toString();
		}
		if (min != 0) {
			timer.append(min + "分钟");
		}
		if (timer.toString().equals("")) {
			return "当前";
		}
		timer.append("前");
		return timer.toString();
	}

	public String getDistatce(double lat1, double lat2, double lon1, double lon2) {
		double R = 6371;
		double distance = 0.0;
		double dLat = (lat2 - lat1) * Math.PI / 180;
		double dLon = (lon2 - lon1) * Math.PI / 180;
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(lat1 * Math.PI / 180)
				* Math.cos(lat2 * Math.PI / 180) * Math.sin(dLon / 2)
				* Math.sin(dLon / 2);
		distance = (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))) * R;
		BigDecimal b = new BigDecimal(distance + "");
		double nwedistance = b.setScale(1, BigDecimal.ROUND_HALF_UP)
				.doubleValue();
		String distanceString = "";
		if (nwedistance < 1) {
			distanceString = "附近";
		} else {
			distanceString = nwedistance + "公里";
		}
		return distanceString;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null)
			return;
		boolean change = false;
		if (data.hasExtra("userId")) {
			DBlog.e("333333333", data.toString());
			String content = data.getStringExtra("content");
			String userId = data.getStringExtra("userId");
			String fullTime = getShowTime(data.getStringExtra("fullTime"));
			if (content != null && !content.equals("")) {
				change = true;
				AccountInfomation accountInfomation = new AccountInfomation();
				accountInfomation.setId(userId);
				accountInfomation.setTime(fullTime);
				accountInfomation.setInformation(content);
				int index = mInfo.indexOf(accountInfomation);
				if (index != -1) {
					accountInfomation.setNickname(mInfo.get(index)
							.getNickname());
					accountInfomation.setHeadurl(mInfo.get(index).getHeadurl());
					accountInfomation.setDistance(mInfo.get(index)
							.getDistance());
					mInfo.set(index, accountInfomation);
				}
			}

		}
		if (change) {
			mAdapter.notifyDataSetChanged();
			change = false;
		}
	}

	private ArrayList<ChatMsgEntity> getChatsById(String id) {
		ArrayList<ChatMsgEntity> newlist = new ArrayList<ChatMsgEntity>();
		for (ChatMsgEntity chatMsgEntity : MessageInfo.matchMessageEntityList) {
			if (chatMsgEntity.getSenderId().equals(id)) {
				newlist.add(chatMsgEntity);
				DBlog.e("t", chatMsgEntity.getType() + "");
			}
		}

		MessageInfo.matchMessageEntityList.removeAll(newlist);

		return newlist;
	}

	public class FriendChatBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			if (arg1 == null)
				return;
			if (arg1.getStringExtra("name").equals("friend")) {
				getMessage();
				mAdapter.notifyDataSetChanged();
			}
		}
	};

	private void registerBroadcast() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(MessageInfo.FriendMessageBroadCastName);
		registerReceiver(fbr, intentFilter);
	}

	private void unregisterBroadcast() {
		unregisterReceiver(fbr);
	}

}
