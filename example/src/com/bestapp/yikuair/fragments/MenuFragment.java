package com.bestapp.yikuair.fragments;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bestapp.yikuair.R;
import com.bestapp.yikuair.database.DBOpenHelper;
import com.bestapp.yikuair.officialaccount.MyPhotoManager;
import com.bestapp.yikuair.officialaccount.OfficialAccountFragment;
import com.bestapp.yikuair.officialaccount.SpeedFriendFragment;
import com.bestapp.yikuair.utils.AccountInfomation;
import com.bestapp.yikuair.utils.Client;
import com.bestapp.yikuair.utils.DBlog;
import com.bestapp.yikuair.utils.ImgButton;
import com.bestapp.yikuair.utils.MessageInfo;
import com.bestapp.yikuair.utils.SharedPreferencesUtil;
import com.bestapp.yikuair.utils.UserInfo;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class MenuFragment extends Fragment implements MyPhotoManager.BackInfo {

	private TextView staffName;
	private TextView jobPosition;
	private TextView department;
	private ListView menuList;
	private listAdapter listAda;
	private static final int COMPANY_NEWS = 0;
	private static final int COMPANY_SCHEDULE = 1;
	private static final int COMPANY_MESSAGE = 2;
	private static final int COMPANY_ADDRESS_LIST = 3;
	private static final int COMPANY_USER_FEEDBACK = 6;
	private static final int COMPANY_OFFICIAL_ACCOUNT = 4;
	private static final int COMPANY_FRIENDS = 5;
	private int unReadedMessageCount;
	public static MenuFragment instance = null;
	public SharedPreferencesUtil menuShared = null;
	public static final String MENUNAME = "MenuName";
	private int currentPos;
	private Fragment newContent;
	private View preView;
	private ImgButton setBtn;
	private ImageView staff_photo;
	private RelativeLayout rl_user_info;
	private ChatBroadcastReceiver cbr = null;
	private HeadModifiedBroadcastReceiver hbr = null;
	public ArrayList<AccountInfomation> mInfos = new ArrayList<AccountInfomation>();
	private List<MenuItemInfo> list = new ArrayList<MenuItemInfo>();
	private HashMap<Integer, List<ChatMsgEntity>> msgMap = new HashMap<Integer, List<ChatMsgEntity>>();
	private DBOpenHelper dbOpenHelper;
	private Cursor cursor;
	private static boolean Loaded = false;
	private MyPhotoManager mPhotoManager;
	FriendChatBroadcastReceiver fbr;

	public class FriendChatBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			if (arg1 == null)
				return;
			if (arg1.getStringExtra("name").equals("friend")) {
				if (MessageInfo.matchMessageEntityList.size() > 0) {
					list.get(COMPANY_FRIENDS).setNum(
							MessageInfo.matchMessageEntityList.size());
					list.get(COMPANY_FRIENDS).setNumVisible(View.VISIBLE);
					listAda.notifyDataSetChanged();
				}
			} else {
				if (MessageInfo.OfficeAccountList.size() > 0) {
					list.get(COMPANY_OFFICIAL_ACCOUNT).setNum(
							MessageInfo.OfficeAccountList.size());
					list.get(COMPANY_OFFICIAL_ACCOUNT).setNumVisible(
							View.VISIBLE);
					listAda.notifyDataSetChanged();
				} else {
					list.get(COMPANY_OFFICIAL_ACCOUNT).setNumVisible(View.GONE);
					listAda.notifyDataSetChanged();
				}
			}

		}
	};

	private void registerBroadcast() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(MessageInfo.FriendBroadCastName);
		getActivity().registerReceiver(fbr, intentFilter);
	}

	private void unregisterBroadcast() {
		getActivity().unregisterReceiver(fbr);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mPhotoManager = new MyPhotoManager(getActivity(), this);
		return inflater.inflate(R.layout.menu_list, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		menuShared = new SharedPreferencesUtil(getActivity());
		currentPos = COMPANY_MESSAGE;
		initView();

		getMenuDataFromShared();

		Log.i("test", "menu onActivityCreated ...............");
	}

	AsyncHttpResponseHandler ResponseHandler = new AsyncHttpResponseHandler() {
		@Override
		public void onSuccess(int arg0, String arg1) {
			DBlog.e("订阅ID", arg1);
			JSONObject jsonObject;
			try {
				jsonObject = new JSONObject(arg1.toString());
				JSONArray jsonArray = jsonObject.getJSONArray("data");
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject2 = (JSONObject) jsonArray.opt(i);
					if (jsonObject2.has("id") && jsonObject2.has("headurl")) {
						AccountInfomation accountInfomation = new AccountInfomation();

						accountInfomation.setRealname(jsonObject2.getString(
								"realname").trim());
						accountInfomation.setHeadurl(jsonObject2
								.getString("headurl"));
						accountInfomation.setId(jsonObject2.getString("id"));
						if (accountInfomation.getId().equals("1")
								|| accountInfomation.getId().equals("2")) {
							continue;
						}
						accountInfomation.setSignature(jsonObject2
								.getString("signature"));
						int Index = MessageInfo.OfficeAccountList_MINE
								.indexOf(accountInfomation);
						if (Index != -1) {
							accountInfomation.setSub(true);
							MessageInfo.OfficeAccountList_MINE.remove(Index);
							MessageInfo.OfficeAccountList_MINE
									.add(accountInfomation);
						}

					} else {
						AccountInfomation accountInfomation = new AccountInfomation();
						accountInfomation.setId(jsonObject2.getString("id"));
						MessageInfo.OfficeAccountList_MINE.add(accountInfomation);

						mInfos.add(accountInfomation);
						
						
					}

				}
				if (!Loaded) {
					Loaded = true;
					Client.get(ResponseHandler);
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		};

		@Override
		public void onFailure(Throwable arg0, String arg1) {
		};
	};

	public void initView() {
		Log.e("test", "menu initView....");

		if (!Loaded) {

			new Thread(new Runnable() {

				@Override
				public void run() {
					mPhotoManager.loadingMyPhotoUrl();
					Client.getSub(ResponseHandler);
				}
			}).start();
		}

		fbr = new FriendChatBroadcastReceiver();
		registerBroadcast();
		SharedPreferencesUtil shared = new SharedPreferencesUtil(getActivity());
		shared.getUserInfo();
		shared.getPhotoUrl();

		rl_user_info = (RelativeLayout) getActivity().findViewById(
				R.id.user_info);
		rl_user_info.setOnClickListener(btnClick);
		staffName = (TextView) getActivity().findViewById(R.id.staff_name);
		jobPosition = (TextView) getActivity().findViewById(R.id.job_position);
		department = (TextView) getActivity().findViewById(R.id.job_department);
		staff_photo = (ImageView) getActivity().findViewById(R.id.staff_photo);
		menuList = (ListView) getActivity().findViewById(R.id.m_list);
		listAda = new listAdapter(getActivity(), list);
		menuList.setAdapter(listAda);

		if (UserInfo.LocalphotoPath != null
				&& UserInfo.LocalphotoPath.length() > 0) {

			BitmapFactory.Options opts = new BitmapFactory.Options();

			opts.inJustDecodeBounds = true;

			BitmapFactory.decodeFile(UserInfo.LocalphotoPath, opts);

			opts.inSampleSize = computeSampleSize(opts, -1, 128 * 128);

			// 这里必然要将其设置回false，因为之前我们将其设置成了true

			opts.inJustDecodeBounds = false;

			try {
				Bitmap bmp = BitmapFactory.decodeFile(UserInfo.LocalphotoPath,
						opts);
				if (bmp == null) {
					if (UserInfo.sex.equals("0"))
						staff_photo.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.girl));
					else
						staff_photo.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.boy));
				} else {
					staff_photo.setBackgroundDrawable(new BitmapDrawable(bmp));
				}
			} catch (OutOfMemoryError err) {
			}

		} else {
			if (UserInfo.sex.equals("0"))
				staff_photo.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.girl));
			else
				staff_photo.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.boy));
		}

		staffName.setText(UserInfo.realName);
		jobPosition.setText(UserInfo.duty);
		department.setText(UserInfo.team);

		setBtn = (ImgButton) getActivity().findViewById(R.id.set_btn);
		setBtn.setOnClickListener(btnClick);

		list.add(new MenuItemInfo(getActivity().getResources().getString(
				R.string.company_news), 0, View.GONE, R.drawable.left_0_ico));
		list.add(new MenuItemInfo(getActivity().getResources().getString(
				R.string.company_agenda), 0, View.GONE, R.drawable.left_1_ico));
		list.add(new MenuItemInfo(getActivity().getResources().getString(
				R.string.company_message), 0, View.GONE, R.drawable.left_2_ico));
		list.add(new MenuItemInfo(getActivity().getResources().getString(
				R.string.company_address_list), 0, View.GONE,
				R.drawable.left_3_ico));

		list.add(new MenuItemInfo(getActivity().getResources().getString(
				R.string.company_official_account),
				MessageInfo.OfficeAccountList.size(), View.GONE,
				R.drawable.ico_rss_normal));
		list.add(new MenuItemInfo(getActivity().getResources().getString(
				R.string.company_friends), 0, View.GONE,
				R.drawable.ico_friend_normal));
		list.add(new MenuItemInfo(getActivity().getResources().getString(
				R.string.user_feedback), 0, View.GONE, R.drawable.left_4_ico));


		Log.i("test", "UserInfo.isfirstlogin... :" + UserInfo.isFirstLogin);
		Log.i("test", "UserInfo.feedback... :" + UserInfo.feedback_dbId);
		Log.i("test", "UserInfo.copanynewdbid... :" + UserInfo.companyNews_dbId);

		if (isFirstLogin()) {
			ChatMsgEntity entity = new ChatMsgEntity();
			entity.setIsComing(true);
			entity.setContent(getActivity().getResources().getString(
					R.string.user_feedback_first_message));
			entity.setType(MessageInfo.TEXT);
			entity.setTime(MessageInfo.getChatTime());
			entity.setSenderId(UserInfo.feedback_dbId);
			entity.setReceiverId(UserInfo.feedback_dbId);
			entity.setStatus(MessageInfo.RECEIVE_MESSAGE);
			updataMenuList(COMPANY_USER_FEEDBACK, 1, entity);

			ChatMsgEntity ent = new ChatMsgEntity();
			ent.setIsComing(true);
			ent.setContent(getActivity().getResources().getString(
					R.string.welcome_news));
			ent.setType(MessageInfo.TEXT);
			ent.setTime(MessageInfo.getChatTime());
			ent.setSenderId(UserInfo.companyNews_dbId);
			ent.setReceiverId(UserInfo.companyNews_dbId);
			ent.setStatus(MessageInfo.RECEIVE_MESSAGE);
			updataMenuList(COMPANY_NEWS, 1, ent);
			/*********************************************/
			/*
			 * MessageInfo.messageEntityList.add(entity);
			 * MessageInfo.messageEntityList.add(ent);
			 */
		}
		menuList.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				currentPos = arg2;
				if (preView != null) {
					preView.setBackgroundColor(Color.TRANSPARENT);
					ImageView leftBar = (ImageView) preView
							.findViewById(R.id.iv_left_bar);
					leftBar.setVisibility(View.INVISIBLE);
					TextView tvItem = (TextView) preView
							.findViewById(R.id.item_function);
					tvItem.setTextColor(getResources().getColor(
							R.color.menu_item1_tv_normal));
					ImageView rightBar = (ImageView) preView
							.findViewById(R.id.iv_right_bar);
					rightBar.setBackgroundResource(R.drawable.mm_submenu_normal);

					ImageView leftIcon = (ImageView) preView
							.findViewById(R.id.iv_left_icon);

					if (tvItem
							.getText()
							.toString()
							.equals(getActivity().getResources().getString(
									R.string.company_news))) {
						leftIcon.setBackgroundResource(R.drawable.left_0_ico);
					} else if (tvItem
							.getText()
							.toString()
							.equals(getActivity().getResources().getString(
									R.string.company_agenda))) {
						leftIcon.setBackgroundResource(R.drawable.left_1_ico);
					} else if (tvItem
							.getText()
							.toString()
							.equals(getActivity().getResources().getString(
									R.string.company_message))) {
						leftIcon.setBackgroundResource(R.drawable.left_2_ico);
					} else if (tvItem
							.getText()
							.toString()
							.equals(getActivity().getResources().getString(
									R.string.company_address_list))) {
						leftIcon.setBackgroundResource(R.drawable.left_3_ico);
					} else if (tvItem
							.getText()
							.toString()
							.equals(getActivity().getResources().getString(
									R.string.user_feedback))) {
						leftIcon.setBackgroundResource(R.drawable.left_4_ico);
					} else if (tvItem
							.getText()
							.toString()
							.equals(getActivity().getResources().getString(
									R.string.company_official_account))) {
						leftIcon.setBackgroundResource(R.drawable.ico_rss_normal);
					} else if (tvItem
							.getText()
							.toString()
							.equals(getActivity().getResources().getString(
									R.string.company_friends))) {
						leftIcon.setBackgroundResource(R.drawable.ico_rss_normal);
					}
				}
				arg1.setBackgroundColor(Color.BLACK);

				ImageView leftBar = (ImageView) arg1
						.findViewById(R.id.iv_left_bar);
				leftBar.setVisibility(View.VISIBLE);

				ImageView leftIcon = (ImageView) arg1
						.findViewById(R.id.iv_left_icon);

				TextView tvItem = (TextView) arg1
						.findViewById(R.id.item_function);
				tvItem.setTextColor(Color.WHITE);

				ImageView rightBar = (ImageView) arg1
						.findViewById(R.id.iv_right_bar);
				rightBar.setBackgroundResource(R.drawable.mm_submenu_pressed);

				if (tvItem
						.getText()
						.toString()
						.equals(getActivity().getResources().getString(
								R.string.company_news))) {
					leftIcon.setBackgroundResource(R.drawable.left_0_ico_highlighted);
				} else if (tvItem
						.getText()
						.toString()
						.equals(getActivity().getResources().getString(
								R.string.company_agenda))) {
					leftIcon.setBackgroundResource(R.drawable.left_1_ico_highlighted);
				} else if (tvItem
						.getText()
						.toString()
						.equals(getActivity().getResources().getString(
								R.string.company_message))) {
					leftIcon.setBackgroundResource(R.drawable.left_2_ico_highlighted);
				} else if (tvItem
						.getText()
						.toString()
						.equals(getActivity().getResources().getString(
								R.string.company_address_list))) {
					leftIcon.setBackgroundResource(R.drawable.left_3_ico_highlighted);
				} else if (tvItem
						.getText()
						.toString()
						.equals(getActivity().getResources().getString(
								R.string.user_feedback))) {
					leftIcon.setBackgroundResource(R.drawable.left_4_ico_highlighted);
				} else if (tvItem
						.getText()
						.toString()
						.equals(getActivity().getResources().getString(
								R.string.company_official_account))) {
					leftIcon.setBackgroundResource(R.drawable.ico_rss_pressed);
				} else if (tvItem
						.getText()
						.toString()
						.equals(getActivity().getResources().getString(
								R.string.company_friends))) {
					leftIcon.setBackgroundResource(R.drawable.ico_friend_pressed);
				}

				preView = arg1;
				currentPos = arg2;

				newContent = null;
				switch (arg2) {
				case COMPANY_NEWS:
					if (msgMap.get(COMPANY_NEWS) != null) {
						updataMenuList(COMPANY_NEWS, 0, null);
						for (int i = 0; i < msgMap.get(COMPANY_NEWS).size(); i++) {
							MessageInfo.menuCompanyNewsList.add(msgMap.get(
									COMPANY_NEWS).get(i));
						}
						msgMap.get(COMPANY_NEWS).clear();
					}
					list.get(COMPANY_NEWS).setNum(0);
					list.get(COMPANY_NEWS).setNumVisible(View.GONE);
					newContent = new CompanyNewsFragment();
					break;
				case COMPANY_SCHEDULE:
					newContent = new ScheduleFragment();
					break;
				case COMPANY_MESSAGE:
					Log.e("test", "messageintitylist.size :"
							+ MessageInfo.messageEntityList.size());

					if (msgMap.get(COMPANY_MESSAGE) != null) {
						updataMenuList(COMPANY_MESSAGE, 0, null);
						for (int i = 0; i < msgMap.get(COMPANY_MESSAGE).size(); i++) {
							MessageInfo.messageEntityList.add(msgMap.get(
									COMPANY_MESSAGE).get(i));
						}
						msgMap.get(COMPANY_MESSAGE).clear();
					}
					list.get(COMPANY_MESSAGE).setNum(0);
					list.get(COMPANY_MESSAGE).setNumVisible(View.GONE);

					newContent = new MessageFragment();
					break;
				case COMPANY_ADDRESS_LIST:
					newContent = new PhoneBookFragment();
					break;
				case COMPANY_USER_FEEDBACK:
					if (msgMap.get(COMPANY_USER_FEEDBACK) != null) {
						updataMenuList(COMPANY_USER_FEEDBACK, 0, null);
						for (int i = 0; i < msgMap.get(COMPANY_USER_FEEDBACK)
								.size(); i++) {
							MessageInfo.menuFeedbackList.add(msgMap.get(
									COMPANY_USER_FEEDBACK).get(i));
						}
						msgMap.get(COMPANY_USER_FEEDBACK).clear();
					}

					list.get(COMPANY_USER_FEEDBACK).setNum(0);
					list.get(COMPANY_USER_FEEDBACK).setNumVisible(View.GONE);
					newContent = new FeedBackFragment();
					break;

				case COMPANY_OFFICIAL_ACCOUNT:
					newContent = new OfficialAccountFragment();
					break;

				case COMPANY_FRIENDS:
					newContent = new SpeedFriendFragment();
					break;
				}

				if (newContent != null)
					switchFragment(newContent);
			}
		});
	}

	public void readDataFromLocal() {

		Log.e("test", "size :" + MessageInfo.messageEntityList.size());

		if (MessageInfo.messageEntityList != null
				&& !MessageInfo.messageEntityList.isEmpty()) {

			for (int i = 0; i < MessageInfo.messageEntityList.size(); i++) {
				int menuItem = -1;

				if (MessageInfo.messageEntityList.get(i).getStatus() != MessageInfo.SEND_MESSAGE
						&& MessageInfo.messageEntityList.get(i).getStatus() != MessageInfo.RECEIVE_MESSAGE
						&& MessageInfo.messageEntityList.get(i).getStatus() != MessageInfo.GROUP_MODIFY)
					continue;

				Log.e("test", "receiver  ss :"
						+ MessageInfo.messageEntityList.get(i).getReceiverId());
				if ((MessageInfo.messageEntityList.get(i).getStatus() == MessageInfo.SEND_MESSAGE)
						|| (MessageInfo.messageEntityList.get(i).getChatType() == MessageInfo.GROUP && MessageInfo.groupMap
								.containsKey(MessageInfo.messageEntityList.get(
										i).getReceiverId()))
						|| (MessageInfo.messageEntityList.get(i)
								.getReceiverId() != null && MessageInfo.messageEntityList
								.get(i).getReceiverId().equals(UserInfo.db_id))
						|| MessageInfo.messageEntityList.get(i).getStatus() == MessageInfo.GROUP_MODIFY) {
					Log.e("test", "company message..........");
					menuItem = COMPANY_MESSAGE;
				} else if (MessageInfo.messageEntityList.get(i).getSenderId() != null
						&& MessageInfo.messageEntityList.get(i).getSenderId()
								.equals(UserInfo.companyNews_dbId)) {
					Log.e("test", "company news.........");
					menuItem = COMPANY_NEWS;
				} else if (MessageInfo.messageEntityList.get(i).getSenderId() != null
						&& MessageInfo.messageEntityList.get(i).getSenderId()
								.equals(UserInfo.feedback_dbId)) {
					Log.e("test", "company user feedback............");
					menuItem = COMPANY_USER_FEEDBACK;
				}
				int count = 0;
				if (MessageInfo.messageEntityList.get(i).getStatus() != MessageInfo.SEND_MESSAGE)// send
																									// message
																									// is
																									// not
																									// show
																									// count
					count = 1;
				Log.e("test", "count is " + count);
				updataMenuList(menuItem, count,
						MessageInfo.messageEntityList.get(i));
			}
			MessageInfo.messageEntityList.clear();
		}
	}

	/*******************************************/
	public void getMenuDataFromShared() {
		if (menuShared == null)
			menuShared = new SharedPreferencesUtil(getActivity());
		if (menuShared.readMenuDataFromShared(MENUNAME + UserInfo.db_id) != null) {
			msgMap = menuShared.readMenuDataFromShared(MENUNAME
					+ UserInfo.db_id);
			if (msgMap != null && !msgMap.isEmpty()) {
				MessageInfo.messageEntityList.clear();
				Set set = msgMap.keySet();
				Iterator it = set.iterator();

				while (it.hasNext()) {
					int key = (Integer) it.next();
					List<ChatMsgEntity> list = msgMap.get(key);
					if (list != null && !list.isEmpty())
						for (int i = 0; i < list.size(); i++) {
							MessageInfo.messageEntityList.add(list.get(i));
						}
				}
				msgMap.clear();
				readDataFromLocal();
			}
		}
	}

	public class ChatBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.e("test", "menu broadcast receive...................");
			ChatMsgEntity entity = new ChatMsgEntity();
			Bundle bundle = intent.getExtras();
			Serializable data = bundle.getSerializable("message");
			if (data != null) {
				entity = (ChatMsgEntity) data;
				AccountInfomation accountInfomation = new AccountInfomation();
				accountInfomation.setId(entity.getSenderId());
				if (MessageInfo.OfficeAccountList_MINE
						.contains(accountInfomation)) {
					return;
				}

				if (entity.getStatus() != MessageInfo.RECEIVE_MESSAGE
						&& entity.getStatus() != MessageInfo.GROUP_MODIFY)
					return;
				int itemNum = -1;
				if (entity.getFromname() != null
						&& !entity.getFromname().equals("")) {
					itemNum = COMPANY_FRIENDS;

				} else {
					if (entity.getSenderId().equals(UserInfo.companyNews_dbId))
						itemNum = COMPANY_NEWS;
					else if (entity.getSenderId()
							.equals(UserInfo.feedback_dbId))
						itemNum = COMPANY_USER_FEEDBACK;
					else if (entity.getChatType() == MessageInfo.GROUP
							&& MessageInfo.groupMap.containsKey(entity
									.getReceiverId())
							|| entity.getReceiverId().equals(UserInfo.db_id)) {
						itemNum = COMPANY_MESSAGE;
					}
				}
				Log.e("test", "companyNews_dbid :" + UserInfo.companyNews_dbId);
				Log.e("test", "companyfeedback_dbid :" + UserInfo.feedback_dbId);
				Log.e("test", "id_id :" + UserInfo.db_id);

				Log.i("test", "from :" + entity.getSenderId());
				Log.i("test", "itemnum : " + itemNum);
				Log.i("test", "to :" + entity.getReceiverId());
				updataMenuList(itemNum, 1, entity);
			} else {
				// Log.e("test", "count is " +
				// MessageInfo.unReadedMessageCount);
				String name = intent.getStringExtra("name");
				Log.e("test", "name is " + name);
				if (name != null && name.length() > 0)
					if (name.equals(getActivity().getResources().getString(
							R.string.company_news))) {
						list.get(COMPANY_NEWS).setNum(0);
						list.get(COMPANY_NEWS).setNumVisible(View.GONE);
					} else if (name.equals(getActivity().getResources()
							.getString(R.string.company_message))) {
						unReadedMessageCount = MessageInfo.unReadedMessageCount;
						Log.e("test", "unreaded count is "
								+ unReadedMessageCount);
						if (unReadedMessageCount > 0) {
							list.get(COMPANY_MESSAGE).setNum(
									unReadedMessageCount);
							list.get(COMPANY_MESSAGE).setNumVisible(
									View.VISIBLE);
						} else {
							list.get(COMPANY_MESSAGE).setNum(0);
							list.get(COMPANY_MESSAGE).setNumVisible(View.GONE);
						}
					} else if (name.equals(getActivity().getResources()
							.getString(R.string.user_feedback))) {
						list.get(COMPANY_USER_FEEDBACK).setNum(0);
						list.get(COMPANY_USER_FEEDBACK)
								.setNumVisible(View.GONE);
					} else if (name.equals("office")) {
						if (MessageInfo.OfficeAccountList.size() == 0) {
							return;
						}
						int num = intent.getIntExtra("number", 0);
						Log.e("test", "number :" + num);
						if (num == 0) {
							list.get(COMPANY_OFFICIAL_ACCOUNT).setNum(0);
							list.get(COMPANY_OFFICIAL_ACCOUNT).setNumVisible(
									View.GONE);
						} else {
							list.get(COMPANY_OFFICIAL_ACCOUNT).setNum(num);
							list.get(COMPANY_OFFICIAL_ACCOUNT).setNumVisible(
									View.VISIBLE);
						}
					} else if (name.equals("friend")) {
						int num = intent.getIntExtra("number", 0);
						Log.e("test", "number :" + num);
						if (num == 0) {
							list.get(COMPANY_FRIENDS).setNum(0);
							list.get(COMPANY_FRIENDS).setNumVisible(View.GONE);
						} else {
							list.get(COMPANY_FRIENDS).setNum(num);
							list.get(COMPANY_FRIENDS).setNumVisible(
									View.VISIBLE);
						}
					}
				listAda.notifyDataSetChanged();
			}
		}
	}

	public class HeadModifiedBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.e("test", "menu broadcast receive...................");
			if (UserInfo.LocalphotoPath != null
					&& UserInfo.LocalphotoPath.length() > 0) {
				BitmapFactory.Options opts = new BitmapFactory.Options();

				opts.inJustDecodeBounds = true;

				BitmapFactory.decodeFile(UserInfo.LocalphotoPath, opts);

				opts.inSampleSize = computeSampleSize(opts, -1, 128 * 128);

				// 这里必然要将其设置回false，因为之前我们将其设置成了true

				opts.inJustDecodeBounds = false;

				try {
					Bitmap bmp = BitmapFactory.decodeFile(
							UserInfo.LocalphotoPath, opts);
					staff_photo.setBackgroundDrawable(new BitmapDrawable(bmp));
				} catch (OutOfMemoryError err) {
				}

			}
		}
	}

	private OnClickListener btnClick = new OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.user_info:
				Intent intent = new Intent(getActivity(),
						PersonalProfileActivity.class);
				intent.putExtra("individualInfo", " ");
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.in_from_right,
						R.anim.out_of_left);
				break;
			case R.id.set_btn:/*
							 * Intent intent2 = new Intent(getActivity(),
							 * SettingActivity.class); startActivity(intent2);
							 * getActivity
							 * ().overridePendingTransition(R.anim.in_from_right
							 * , R.anim.out_of_left);
							 */
				newContent = new SettingFragment();
				switchFragment(newContent);
			}
		}
	};

	private boolean isFirstLogin() {
		SharedPreferences setting = getActivity().getSharedPreferences(
				"yikuairMenu", 0);
		Boolean user_first = setting.getBoolean("FIRST", true);
		if (user_first) {
			setting.edit().putBoolean("FIRST", false).commit();
			UserInfo.isFirstLogin = true;
			return true;
		} else {
			UserInfo.isFirstLogin = false;
			return false;
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.i("test", "menu onstart....................");
		// SharedPreferencesUtil shared = new
		// SharedPreferencesUtil(getActivity());
		// shared.getUserInfo();

		dbOpenHelper = new DBOpenHelper(getActivity());

		if (UserInfo.companyNews_dbId == null
				|| UserInfo.companyNews_dbId.length() == 0) {
			UserInfo.companyNews_dbId = getDBIdFromDB(getActivity()
					.getResources().getString(R.string.company_news));
			Log.e("test", "...........company dbid :"
					+ UserInfo.companyNews_dbId);
		}
		if (UserInfo.feedback_dbId == null
				|| UserInfo.feedback_dbId.length() == 0) {
			UserInfo.feedback_dbId = getDBIdFromDB(getActivity().getResources()
					.getString(R.string.user_feedback));
			Log.e("test", "...................feedback dbid :"
					+ UserInfo.feedback_dbId);
		}

		// register broadcast
		if (cbr == null) {
			IntentFilter myIntentFilter = new IntentFilter();
			myIntentFilter.addAction(MessageInfo.MessageBroadCastName);
			cbr = new ChatBroadcastReceiver();
			getActivity().registerReceiver(cbr, myIntentFilter);
		}
		if (hbr == null) {
			IntentFilter headIntentFilter = new IntentFilter();
			headIntentFilter.addAction(MessageInfo.HeadModifieyBroadCastName);
			hbr = new HeadModifiedBroadcastReceiver();
			getActivity().registerReceiver(hbr, headIntentFilter);
		}
		readDataFromLocal();

		if (UserInfo.LocalphotoPath != null
				&& UserInfo.LocalphotoPath.length() > 0) {

			BitmapFactory.Options opts = new BitmapFactory.Options();

			opts.inJustDecodeBounds = true;

			BitmapFactory.decodeFile(UserInfo.LocalphotoPath, opts);

			opts.inSampleSize = computeSampleSize(opts, -1, 128 * 128);

			// 这里必然要将其设置回false，因为之前我们将其设置成了true

			opts.inJustDecodeBounds = false;

			try {

				Bitmap bmp = BitmapFactory.decodeFile(UserInfo.LocalphotoPath,
						opts);
				staff_photo.setBackgroundDrawable(new BitmapDrawable(bmp));
			} catch (OutOfMemoryError err) {
			}
		}
		instance = this;
	}

	public static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);
		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}
		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,

	int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;
		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));
		if (upperBound < lowerBound) {
			return lowerBound;
		}
		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	public void onResume() {
		Log.e("test", "menu onResume....................");

		if (MessageInfo.matchMessageEntityList.size() > 0) {
			list.get(COMPANY_FRIENDS).setNum(
					MessageInfo.matchMessageEntityList.size());
			list.get(6).setNumVisible(View.VISIBLE);

		} else {
			list.get(6).setNumVisible(View.GONE);
		}
		listAda.notifyDataSetChanged();
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.e("test", "menu onPause");
	}

	@Override
	public void onStop() {
		super.onStop();
		SharedPreferencesUtil shared = new SharedPreferencesUtil(getActivity());
		shared.saveUserInfo();

		instance = null;

		Log.e("test", "menu  onStop");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterBroadcast();
		if (hbr != null) {
			getActivity().unregisterReceiver(hbr);
			hbr = null;
		}
		if (cbr != null) {
			getActivity().unregisterReceiver(cbr);
			cbr = null;
		}

		if (menuShared == null)
			menuShared = new SharedPreferencesUtil(getActivity());

		menuShared.saveMenuDatatoShared(msgMap, MENUNAME + UserInfo.db_id);

		Log.i("test", "menu onDestroy");
	}

	// the meat of switching the above fragment
	private void switchFragment(Fragment fragment) {
		if (getActivity() == null)
			return;
		if (getActivity() instanceof ResponsiveUIActivity) {
			ResponsiveUIActivity ra = (ResponsiveUIActivity) getActivity();
			ra.switchContent(fragment);
		}
	}

	private void updataMenuList(int menuItemNum, int messageCount,
			ChatMsgEntity entity) {

		if (menuItemNum == 6) {
			list.get(COMPANY_FRIENDS).setNum(
					MessageInfo.matchMessageEntityList.size());
			list.get(menuItemNum).setNumVisible(View.VISIBLE);
			listAda.notifyDataSetChanged();
			return;
		}
		if (menuItemNum == -1)
			return;

		if (msgMap.containsKey(menuItemNum)) {
			if (entity != null) {
				if (MessageFragment.instance == null) {
					if (entity.getStatus() != MessageInfo.SEND_MESSAGE) {
						entity.setIsAdd(true);
					}
				} else {
					entity.setIsAdd(false);
				}
				if (menuItemNum == COMPANY_NEWS) {
					if (CompanyNewsFragment.instance == null)
						msgMap.get(menuItemNum).add(entity);
				} else if (menuItemNum == COMPANY_USER_FEEDBACK) {
					if (FeedBackFragment.instance == null)
						msgMap.get(menuItemNum).add(entity);
				} else
					msgMap.get(menuItemNum).add(entity);
				list.get(menuItemNum)
						.setNum(list.get(menuItemNum).getNum()
								+ messageCount
								+ (entity.getStatus() == MessageInfo.GROUP_MODIFY ? -1
										: 0));
				if (list.get(menuItemNum).getNum()
						+ messageCount
						+ (entity.getStatus() == MessageInfo.GROUP_MODIFY ? -1
								: 0) > 0)
					list.get(menuItemNum).setNumVisible(View.VISIBLE);
				else
					list.get(menuItemNum).setNumVisible(View.GONE);
			} else {
				if (messageCount == 0) {
					list.get(menuItemNum).setNumVisible(View.GONE);
				} else {
					list.get(menuItemNum).setNum(messageCount);
					list.get(menuItemNum).setNumVisible(View.VISIBLE);
				}
			}
		} else {
			List<ChatMsgEntity> msgList = new ArrayList<ChatMsgEntity>();
			if (entity != null) {
				if (MessageFragment.instance == null) {
					if (entity.getStatus() != MessageInfo.SEND_MESSAGE) {
						entity.setIsAdd(true);
					}
				} else {
					entity.setIsAdd(false);
				}
				msgList.add(entity);
				msgMap.put(menuItemNum, msgList);
				int count = 0;
				for (int i = 0; i < msgMap.get(menuItemNum).size(); i++) {
					if (msgMap.get(menuItemNum).get(i).getStatus() != MessageInfo.SEND_MESSAGE)
						count++;
				}
				if (count > 0/* msgMap.get(menuItemNum).size() > 0 */) {
					Log.e("test", "count is    + "
							+ msgMap.get(menuItemNum).size());
					list.get(menuItemNum)
							.setNum(list.get(menuItemNum).getNum()
									+ msgMap.get(menuItemNum).size()
									+ (entity.getStatus() == MessageInfo.GROUP_MODIFY ? -1
											: 0));
					if (list.get(menuItemNum).getNum()
							+ msgMap.get(menuItemNum).size()
							+ (entity.getStatus() == MessageInfo.GROUP_MODIFY ? -1
									: 0) > 0)
						list.get(menuItemNum).setNumVisible(View.VISIBLE);
					else
						list.get(menuItemNum).setNumVisible(View.GONE);
				} else
					list.get(menuItemNum).setNumVisible(View.GONE);
			}
		}
		listAda.notifyDataSetChanged();
	}

	public String getHeadUrlFromDB(String dbId) {
		if (dbId == null)
			return "";
		dbOpenHelper = new DBOpenHelper(getActivity());
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		String name = "";
		cursor = db.rawQuery("select * from contactsTable where dbid=?",
				new String[] { dbId });
		if (cursor.moveToFirst()) {
			name = cursor.getString(8);
		}
		dbOpenHelper.close();
		cursor.close();
		return name;
	}

	public String getSexFromDB(String dbId) {
		if (dbId == null)
			return "";
		dbOpenHelper = new DBOpenHelper(getActivity());
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		String name = "";
		cursor = db.rawQuery("select * from contactsTable where dbid=?",
				new String[] { dbId });
		if (cursor.moveToFirst()) {
			name = cursor.getString(13);
		}
		dbOpenHelper.close();
		cursor.close();
		return name;
	}

	private String getDBIdFromDB(String name) {
		if (name == null)
			return "";
		String dbid = "";
		dbOpenHelper = new DBOpenHelper(getActivity());
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		cursor = db.rawQuery("select * from contactsTable where name=?",
				new String[] { name });
		if (cursor.moveToFirst()) {
			dbid = cursor.getString(14);
		}
		cursor.close();
		dbOpenHelper.close();
		return dbid;
	}

	private class listAdapter extends BaseAdapter {
		private List<MenuItemInfo> mData;
		private LayoutInflater mInflater;

		public listAdapter(Context context, List<MenuItemInfo> list) {
			mInflater = LayoutInflater.from(context);
			mData = list;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mData.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return mData.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.menu_item1, null);
			holder.textView = (TextView) convertView
					.findViewById(R.id.item_function);
			holder.rl_num = (RelativeLayout) convertView
					.findViewById(R.id.rl_prompt_num);
			holder.tv_num = (TextView) convertView
					.findViewById(R.id.tv_prompt_num);
			holder.iv_icon = (ImageView) convertView
					.findViewById(R.id.iv_left_icon);

			holder.textView.setText(mData.get(position).getMenuName());
			holder.rl_num.setVisibility(mData.get(position).getNumVisible());
			holder.tv_num.setText(String.valueOf(mData.get(position).getNum()));
			holder.iv_icon.setBackgroundResource(mData.get(position).getIcon());

			if (position == currentPos) {
				convertView.setBackgroundColor(Color.BLACK);
				ImageView leftBar = (ImageView) convertView
						.findViewById(R.id.iv_left_bar);
				leftBar.setVisibility(View.VISIBLE);

				TextView tvItem = (TextView) convertView
						.findViewById(R.id.item_function);
				tvItem.setTextColor(Color.WHITE);

				ImageView rightBar = (ImageView) convertView
						.findViewById(R.id.iv_right_bar);
				rightBar.setBackgroundResource(R.drawable.mm_submenu_pressed);

				ImageView leftIcon = (ImageView) convertView
						.findViewById(R.id.iv_left_icon);

				if (position == 0) {
					leftIcon.setBackgroundResource(R.drawable.left_0_ico_highlighted);
				} else if (position == 1) {
					leftIcon.setBackgroundResource(R.drawable.left_1_ico_highlighted);
				} else if (position == 2) {
					leftIcon.setBackgroundResource(R.drawable.left_2_ico_highlighted);
				} else if (position == 3) {
					leftIcon.setBackgroundResource(R.drawable.left_3_ico_highlighted);
				} else if (position == 4) {
					leftIcon.setBackgroundResource(R.drawable.left_4_ico_highlighted);
				} else if (position == 5) {
					leftIcon.setBackgroundResource(R.drawable.ico_rss_pressed);
				}
				preView = convertView;
			}
			return convertView;
		}

		public class ViewHolder {
			public TextView textView;
			public RelativeLayout rl_num;
			public TextView tv_num;
			public ImageView iv_icon;
		}
	}

	@Override
	public void fillContentToPeopleList(ArrayList<AccountInfomation> list) {
		// TODO Auto-generated method stub

	}

	@Override
	public void likeMe(boolean arg0, boolean arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void fillPhotoSeek(String url) {
		// TODO Auto-generated method stub

	}
}
