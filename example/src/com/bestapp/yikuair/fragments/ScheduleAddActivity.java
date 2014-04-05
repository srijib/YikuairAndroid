package com.bestapp.yikuair.fragments;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import android.os.Bundle;
import android.os.IBinder;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bestapp.yikuair.R;
import com.bestapp.yikuair.fragments.MessageFragment.MessageBroadcastReceiver;
import com.bestapp.yikuair.utils.CompareStrUtil;
import com.bestapp.yikuair.utils.MessageInfo;
import com.bestapp.yikuair.utils.MyDateTime;
import com.bestapp.yikuair.utils.SharedPreferencesUtil;
import com.bestapp.yikuair.utils.StringWidthWeightRandom;
import com.bestapp.yikuair.utils.UserInfo;

public class ScheduleAddActivity extends Activity {
	private int currentIndex;
	private GroupResultBroadcastReceiver gbr = null;
	private RelativeLayout rl_add_member;
	private ClientSocket client;
	private ViewPager mTabPager;
	private LinearLayout mTab1, mTab2, mTab3;
	private TextView scheduleType;
	private View view_task, view_meeting, view_other;
	private ImageButton sendBtn;
	private EditText scheduleTitle;
	private TextView scheduleBeginDate, scheduleBeginTime, scheduleEndDate,
			scheduleEndTime;
	private String receiveBeginDate, receiveEndDate, beginDate, endDate, bDate,
			eDate, beginTime, endTime, title, itemId;
	private EditText scheduleAddress;
	private ImageView addMember;
	private LinkedHashMap<String, String> nameIdMap = new LinkedHashMap<String, String>();
	private LinkedHashMap<String, String> nameIdTaskMap = new LinkedHashMap<String, String>();
	private LinkedHashMap<String, String> nameIdMeetingMap = new LinkedHashMap<String, String>();
	private LinkedHashMap<String, String> nameIdOtherMap = new LinkedHashMap<String, String>();
	private static final int CONTACT_REQUEST_CODE = 1;
	private static final int CHAT_RESULT_CODE = 2;

	private ArrayList<String> memberValueList = new ArrayList<String>();
	private ArrayList<String> memberIdList = new ArrayList<String>();
	private int type = 0;
	private String[] nameStr;
	private String[] idStr;
	private LinearLayout ll_begin, ll_end, ll_all_day, viewGroup, ll_tt_begin,
			ll_tt_end;
	private boolean isMeetingAllDayPressed = false;
	private boolean isOtherAllDayPressed = false;
	private MyDateTime mPopupWindow;
	private boolean isBeginPressed = false;
	private boolean isEndPressed = false;
	private Dialog mDialog;
	private String sendTitle, sendAddress, sendBeginDate, sendEndDate;
	private String ids, names;
	private boolean isExist; // for me
	private String groupId;
	public static ScheduleAddActivity instance = null;
	public boolean isFromChat;
	private ImageButton left_btn;
	private String currentDate;
	private MessageBroadcastReceiver mbr;
	private SharedPreferencesUtil shared;
	private String sendItemId;
	private String scheType;
	private String msgType;// group or individual
	private final static String YIKUAIR_GROUP = "yikuair_group";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		client = new ClientSocket(this);
		setContentView(R.layout.schedule_add);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		initView();
		currentDate = getIntent().getStringExtra("currentDate");
		Log.e("test", "currentDate is " + currentDate);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			nameStr = bundle.getStringArray("nameStr");
			idStr = bundle.getStringArray("idStr");
		}

		isFromChat = getIntent().getBooleanExtra("isFromChat", false);
		receiveBeginDate = bDate = getIntent().getStringExtra("beginDate");
		receiveEndDate = eDate = getIntent().getStringExtra("endDate");
		title = getIntent().getStringExtra("title");
		itemId = getIntent().getStringExtra("ItemId");
		type = getIntent().getIntExtra("type", 0);

		Log.i("test", "beginDate: " + bDate);
		Log.i("test", "endDate: " + eDate);
		Log.i("test", "title: " + title);
		Log.i("test", "type: " + type);
		Log.i("test", "itemId :" + itemId);

		if (bDate != null && type != MessageInfo.TASK) {
			beginTime = MessageInfo.formatTime(bDate);
			bDate = MessageInfo.formatDate(bDate, type);
			Log.i("test", "format bdate :" + bDate);
		}
		if (eDate != null) {
			endTime = MessageInfo.formatTime(eDate);
			eDate = MessageInfo.formatDate(eDate, type);
			Log.i("test", "format eDate :" + eDate);
		}
		currentIndex = type;
		if (type == 0)
			switchView();
		else
			mTabPager.setCurrentItem(type);
		if (nameStr != null)
			restoreScrollView();

		shared = new SharedPreferencesUtil(this);
	}

	class GroupResultBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			Log.i("test", "receive group result broadcast");
			int resultCode = arg1.getIntExtra("code", 0);
			int token = arg1.getIntExtra("token", 0);
			groupId = arg1.getStringExtra("group_id");
			if (resultCode == 200) {
				if (token == 15) {
					Log.i("test", "token == 15....................");
					if (MessageInfo.groupMap != null
							&& !MessageInfo.groupMap.containsKey(groupId)) {
						MessageInfo.groupMap.put(groupId, ids);

						if (shared == null)
							shared = new SharedPreferencesUtil( 
									ScheduleAddActivity.this);
						shared.saveGroupMaptoShared(MessageInfo.groupMap,
								UserInfo.db_id + "_" + YIKUAIR_GROUP);
					}

					String sendBDate = beginDate;
					String sendEDate = endDate;

					if (ll_tt_begin != null
							&& ll_tt_begin.getVisibility() == View.GONE) {
						String[] tempBegin = beginDate.split(" ");
						String[] tempEnd = endDate.split(" ");
						if (tempBegin != null && tempBegin.length > 1) {
							sendBDate = tempBegin[0];
							sendEDate = tempEnd[0];
						}
					}

					scheType = "";
					msgType = "2";// group
					if (currentIndex == MessageInfo.TASK) {
						scheType = "2";
					} else if (currentIndex == MessageInfo.MEETING) {
						scheType = "1";
					} else {
						scheType = "3";
					}

					sendItemId = StringWidthWeightRandom.getNextString();
					if (client == null)
						client = new ClientSocket(ScheduleAddActivity.this);

					client.sendMessage(sendTitle, 13, sendItemId,
							UserInfo.db_id, groupId,
							MessageInfo.StringToLong(sendBDate),
							MessageInfo.StringToLong(sendEDate), sendAddress,
							scheType, msgType, null, false);
				}
			}
		}
	}

	public class MessageBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.e("test", "scheduleaddactivity receive broadcast**************");
			ChatMsgEntity entity = new ChatMsgEntity();
			Bundle bundle = intent.getExtras();
			Serializable data = bundle.getSerializable("message");
			mDialog.dismiss();
			if (data == null) {
				return;
			}

			String taskId = ((ChatMsgEntity) data).getScheduleTaskId();

			String nameStr = "";
			if (memberValueList.size() == 1) {
				nameStr = memberValueList.get(0);
			} else if (memberValueList.size() > 1) {
				nameStr = names;
			}
			ChatMsgEntity msgEntity = new ChatMsgEntity();
			String content = "";
			if (currentIndex == MessageInfo.TASK) {
				content = getResources().getString(R.string.message_item_task);
			} else if (currentIndex == MessageInfo.MEETING) {
				content = getResources().getString(
						R.string.message_item_meeting);
			} else {
				content = getResources().getString(R.string.message_item_other);
			}
			msgEntity.setContent(content);
			msgEntity.setTime(MessageInfo.getChatTime());
			msgEntity.setFullTime(MessageInfo.getMessageFullTime());

			/**************************************/
			String[] tempName = nameStr.split("、");
			if (tempName.length == 2) {
				if (tempName[0].equals("我")) {
					nameStr = tempName[1];
				} else if (tempName[1].equals("我")) {
					nameStr = tempName[0];
				}
			}

			msgEntity.setName(nameStr);

			/*************************************/
			msgEntity.setScheduleItemId(sendItemId);
			msgEntity.setScheduleTaskId(taskId);// taskId..............

			ArrayList<String> idList = new ArrayList<String>();
			for (int i = 0; i < memberIdList.size(); i++) {
				if (memberIdList.get(i).equals(UserInfo.db_id))
					continue;
				idList.add(memberIdList.get(i));
			}
			if (idList.size() > 0) {
				if (idList.size() == 1) {
					msgEntity.setSenderId(UserInfo.db_id);
					msgEntity.setReceiverId(idList.get(0));
				} else {
					msgEntity.setChatType(MessageInfo.GROUP);
					msgEntity.setReceiverId(groupId);
				}
				Log.e("test", "receiver :" + msgEntity.getReceiverId());
				msgEntity.setType(MessageInfo.SCHEDULE);
				msgEntity.setIsAdd(false);
				msgEntity.setIsComing(false);
				msgEntity.setStatus(MessageInfo.SEND_MESSAGE);
				MessageInfo.messageEntityList.add(msgEntity);
			}

			if (isExist) {
				String[] memberId = new String[memberIdList.size()];
				String[] memberName = new String[memberIdList.size()];
				for (int i = 0; i < memberIdList.size(); i++) {
					memberId[i] = memberIdList.get(i);
					memberName[i] = memberValueList.get(i);
				}

				MessageInfo.scheduleList.add(new ScheduleItemInfo(
						UserInfo.realName/*
										 * getResources()
										 * .getString(R.string.me)
										 */, sendTitle, beginDate, endDate,
						memberName, memberId, UserInfo.db_id, currentIndex,
						sendItemId, false, sendAddress, taskId, groupId));
			}

			// save entity to shared...................
			List<ChatMsgEntity> mDataArrays = new ArrayList<ChatMsgEntity>();
			ChatMsgEntity messageEntity = new ChatMsgEntity();
			messageEntity.setTime(MessageInfo.getChatTime());
			messageEntity.setType(MessageInfo.SCHEDULE);
			messageEntity.setIsComing(false);
			messageEntity.setBackground(R.drawable.ima_sent);
			messageEntity.setChatState(getResources().getString(R.string.sent));
			messageEntity.setMsguuid(sendItemId);

			String sendBDate = beginDate;
			String sendEDate = endDate;
			if (ll_tt_begin != null && ll_tt_begin.getVisibility() == View.GONE) {
				String[] tempBegin = beginDate.split(" ");
				String[] tempEnd = endDate.split(" ");
				if (tempBegin != null && tempBegin.length > 1) {
					sendBDate = tempBegin[0];
					sendEDate = tempEnd[0];
				}
			}

			/************************/
			/****************************************/
			messageEntity.setGroupIds(ids);
			messageEntity.setGroupNames(names);

			/****************************************/
			messageEntity.setBeginDate(beginDate);
			messageEntity.setEndDate(endDate);

			messageEntity.setScheduleTitle(sendTitle);
			messageEntity.setScheduleBeginTime(sendBDate);
			messageEntity.setScheduleEndTime(sendEDate);
			messageEntity.setScheduleAddress(sendAddress);
			messageEntity.setScheduleType(currentIndex);

			/******************************************/
			messageEntity.setScheduleItemId(sendItemId);

			if (currentIndex == MessageInfo.TASK) {
				entity.setContent(getResources().getString(
						R.string.message_item_task));
			} else if (currentIndex == MessageInfo.MEETING) {
				entity.setContent(getResources().getString(
						R.string.message_item_meeting));
			} else {
				entity.setContent(getResources().getString(
						R.string.message_item_other));
			}

			/************************/

			if (shared.readDataFromShared(ids + "_" + UserInfo.db_id) != null) {
				mDataArrays = shared.readDataFromShared(ids + "_"
						+ UserInfo.db_id);
				mDataArrays.add(messageEntity);
			} else {
				mDataArrays.add(messageEntity);
			}
			Log.e("test",
					"*****************************************************");
			Log.e("test", "ids :::" + ids);
			Log.e("test", "count is " + mDataArrays.size());
			Log.e("test", "key :" + ids + "_" + UserInfo.db_id);
			shared.saveDatatoShared(ids + "_" + UserInfo.db_id, mDataArrays);
			shared.saveGroupInfo(ids + "_" + UserInfo.db_id, groupId);

			finish();
			overridePendingTransition(R.anim.in_from_left, R.anim.out_of_right);
		}
	}

	public void showRoundProcessDialog() {
		mDialog.show();
		mDialog.setCanceledOnTouchOutside(false);
		mDialog.setContentView(R.layout.schedule_loading_dialog);
	}

	public void sendSchedule(boolean isGroupChat) {
		String id = null;
		sendItemId = null;
		String[] memberId = new String[memberIdList.size()];
		String[] memberName = new String[memberIdList.size()];
		boolean isDel = false;
		if (nameStr == null) {
			nameStr = new String[] { UserInfo.realName /*
														 * getResources().getString
														 * (R.string.me)
														 */};
		}
		if (idStr == null) {
			idStr = new String[] { UserInfo.db_id };
		}
		for (int i = 0; i < memberIdList.size(); i++) {
			memberId[i] = memberIdList.get(i);
			memberName[i] = memberValueList.get(i);
		}
		if (memberId.length == 1 && memberId[0].equals(UserInfo.db_id))
			id = UserInfo.db_id;
		else
			id = ids;// StringWidthWeightRandom.getNextString();

		Log.i("test", "recevietitle:" + title);
		Log.i("test", "sendtitle:" + sendTitle);
		Log.i("test", "receivebegindate:" + receiveBeginDate);
		Log.i("test", "sendbeginDATE:" + beginDate);
		Log.i("test", "receviveendDATE:" + receiveEndDate);
		Log.i("test", "sendEndDate:" + endDate);
		Log.i("test", "recevietype:" + type);
		Log.i("test", "sendtype:" + currentIndex);
		Log.i("test", "isExist : " + isExist);
		Log.i("test",
				"compareName:"
						+ CompareStrUtil.romove(
								CompareStrUtil.toArrayList(memberId),
								CompareStrUtil.toArrayList(idStr)).size());
		Log.i("test",
				"compareId:"
						+ CompareStrUtil.romove(
								CompareStrUtil.toArrayList(memberName),
								CompareStrUtil.toArrayList(nameStr)).size());

		if (itemId != null) {
			Log.i("test", "isDel.............");
			Log.i("test", "itemid : " + itemId);
			sendItemId = itemId;
			isDel = true;
		} else {
			Log.i("test", "not isDel...........");
			sendItemId = StringWidthWeightRandom.getNextString();
			isDel = false;
		}
		if (isExist) {
			Log.e("test", "sendAddress...  ::" + sendAddress);
			Log.e("test", "schedulebegindate :" + beginDate);
			MessageInfo.scheduleList.add(new ScheduleItemInfo(
					UserInfo.realName/*
									 * getResources() .getString(R.string.me)
									 */, sendTitle, beginDate, endDate,
					memberName, memberId, UserInfo.db_id, currentIndex,
					sendItemId, isDel, sendAddress, null, groupId));
		}

		Log.e("test", "memberId :" + memberId);
		Log.e("test", "memberName :" + memberName);

		if (id != null && id.equals(UserInfo.db_id)) {
			finish();
			overridePendingTransition(R.anim.in_from_left, R.anim.out_of_right);
			return;
		}

		Log.i("test", "endDate :" + endDate);
		Log.i("test", "id is " + id);
		Log.i("test", "currentId" + currentIndex);
		Log.e("test", "beginDate :" + beginDate);

		String sendBDate = beginDate;
		String sendEDate = endDate;

		if (ll_tt_begin != null && ll_tt_begin.getVisibility() == View.GONE) {
			String[] tempBegin = beginDate.split(" ");
			String[] tempEnd = endDate.split(" ");
			if (tempBegin != null && tempBegin.length > 1) {
				sendBDate = tempBegin[0];
				sendEDate = tempEnd[0];
			}
		}

		ChatMsgEntity entity = new ChatMsgEntity();
		entity.setScheduleTitle(sendTitle);
		entity.setScheduleEndTime(sendEDate/* endDate */);
		entity.setScheduleBeginTime(sendBDate/* beginDate */);
		entity.setScheduleAddress(sendAddress);
		entity.setType(MessageInfo.SCHEDULE);
		entity.setmemberIdList(memberIdList);
		entity.setScheduleType(currentIndex);

		entity.setMsguuid(sendItemId);

		entity.setIsComing(false);

		/***************************************/
		entity.setBeginDate(beginDate);
		entity.setEndDate(endDate);

		/*************************************/
		entity.setScheduleItemId(sendItemId);

		Intent intent1 = new Intent(ScheduleAddActivity.this,
				ChatActivity.class);
		intent1.putExtra("name", names);
		intent1.putExtra("Id", id);
		if (!isGroupChat) {
			Log.i("test", "id :" + id);
			entity.setChatType(MessageInfo.INDIVIDUAL);
			entity.setReceiverId(id);
		} else if (isGroupChat) {
			Log.e("test", "groupId .......:" + groupId);
			entity.setChatType(MessageInfo.GROUP);
			entity.setReceiverId(groupId);
			intent1.putExtra("group_id", groupId);
		}

		/****************************************/
		if (isFromChat) {
			intent1.putExtra("ScheduleToChatMessage", (Serializable) entity);
			setResult(3, intent1);
			finish();
			overridePendingTransition(R.anim.in_from_up, R.anim.out_of_down);
		}
		/****************************************/
		else {
			intent1.putExtra("schedulemessage", (Serializable) entity);
			String nameStr = "";
			if (memberValueList.size() == 1) {
				nameStr = memberValueList.get(0);
			} else if (memberValueList.size() > 1) {
				nameStr = names;
			}
			ChatMsgEntity msgEntity = new ChatMsgEntity();
			String content = "";
			if (currentIndex == MessageInfo.TASK) {
				content = getResources().getString(R.string.message_item_task);
			} else if (currentIndex == MessageInfo.MEETING) {
				content = getResources().getString(
						R.string.message_item_meeting);
			} else {
				content = getResources().getString(R.string.message_item_other);
			}
			msgEntity.setContent(content);
			msgEntity.setTime(MessageInfo.getChatTime());
			msgEntity.setFullTime(MessageInfo.getMessageFullTime());
			msgEntity.setName(nameStr);
			/*************************************/
			msgEntity.setScheduleItemId(sendItemId);

			ArrayList<String> idList = new ArrayList<String>();
			for (int i = 0; i < memberIdList.size(); i++) {
				if (memberIdList.get(i).equals(UserInfo.db_id))
					continue;
				idList.add(memberIdList.get(i));
			}
			if (idList.size() > 0) {
				if (idList.size() == 1) {
					msgEntity.setSenderId(UserInfo.db_id);
					msgEntity.setReceiverId(idList.get(0));
				} else {
					msgEntity.setChatType(MessageInfo.GROUP);
					msgEntity.setReceiverId(groupId);
				}
				Log.e("test", "receiver :" + msgEntity.getReceiverId());
				msgEntity.setType(MessageInfo.SCHEDULE);
				msgEntity.setIsAdd(false);
				msgEntity.setIsComing(false);
				msgEntity.setStatus(MessageInfo.SEND_MESSAGE);
				MessageInfo.messageEntityList.add(msgEntity);
			} else {
				msgEntity.setSenderId(UserInfo.db_id);
			}
			// startActivity(intent1);
			// finish();
			// overridePendingTransition(R.anim.in_from_right,
			// R.anim.out_of_left);
		}
	}

	public void initView() {

		mDialog = new AlertDialog.Builder(this).create();
		left_btn = (ImageButton) findViewById(R.id.scheduleadd_left_btn);
		left_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!isFromChat) {
					backToSchedule();
				} else {
					Intent intent = new Intent();
					ChatMsgEntity entity = null;
					intent.putExtra("ScheduleToChatMessage",
							(Serializable) entity);
					setResult(3, intent);
					finish();
					overridePendingTransition(R.anim.in_from_up,
							R.anim.out_of_down);
				}
			}
		});
		mTabPager = (ViewPager) findViewById(R.id.scheduleadd_tabpager);
		mTabPager.setOnPageChangeListener(new MyOnPageChangeListener());

		scheduleType = (TextView) findViewById(R.id.tv_schedule_type);
		sendBtn = (ImageButton) findViewById(R.id.scheduleadd_right_btn);

		mTab1 = (LinearLayout) findViewById(R.id.schedule_add_task);
		mTab2 = (LinearLayout) findViewById(R.id.schedule_add_meeting);
		mTab3 = (LinearLayout) findViewById(R.id.schedule_add_other);

		mTab1.setOnClickListener(new MyOnClickListener(0));
		mTab2.setOnClickListener(new MyOnClickListener(1));
		mTab3.setOnClickListener(new MyOnClickListener(2));

		mTab1.setBackgroundResource(R.drawable.taskitem_background_pressed);

		nameIdTaskMap.put(UserInfo.db_id, UserInfo.realName/*
															 * getResources().
															 * getString
															 * (R.string.me)
															 */);
		nameIdMeetingMap.put(UserInfo.db_id, UserInfo.realName
		/* getResources().getString(R.string.me) */);
		nameIdOtherMap.put(UserInfo.db_id, UserInfo.realName/*
															 * getResources()
															 * .getString
															 * (R.string.me)
															 */);

		LayoutInflater mLi = LayoutInflater.from(this);
		view_task = mLi.inflate(R.layout.schedule_task, null);
		view_meeting = mLi.inflate(R.layout.schedule_meeting, null);
		view_other = mLi.inflate(R.layout.schedule_other, null);

		final ArrayList<View> views = new ArrayList<View>();
		views.add(view_task);
		views.add(view_meeting);
		views.add(view_other);

		sendBtn.setOnClickListener(btnClick);

		PagerAdapter mPagerAdapter = new PagerAdapter() {

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public int getCount() {
				return views.size();
			}

			@Override
			public void destroyItem(View container, int position, Object object) {
				((ViewPager) container).removeView(views.get(position));
			}

			@Override
			public Object instantiateItem(View container, int position) {
				((ViewPager) container).addView(views.get(position));
				return views.get(position);
			}
		};
		mTabPager.setAdapter(mPagerAdapter);
	}

	public void backToSchedule() {
		finish();
		overridePendingTransition(R.anim.in_from_left, R.anim.out_of_right);
	}

	@SuppressWarnings("unchecked")
	public void restoreScrollView() {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		for (int i = 0; i < nameStr.length; i++) {
			map.put(idStr[i], nameStr[i]);
		}
		if (isFromChat) {
			nameIdTaskMap = (LinkedHashMap<String, String>) map.clone();
			nameIdMeetingMap = (LinkedHashMap<String, String>) map.clone();
			nameIdOtherMap = (LinkedHashMap<String, String>) map.clone();
		} else {
			if (type == MessageInfo.TASK)
				nameIdTaskMap = (LinkedHashMap<String, String>) map.clone();
			else if (type == MessageInfo.MEETING)
				nameIdMeetingMap = (LinkedHashMap<String, String>) map.clone();
			else
				nameIdOtherMap = (LinkedHashMap<String, String>) map.clone();
		}
		setScrollView(map);
	}

	public void switchView() {
		View view = null;
		if (currentIndex == MessageInfo.TASK) {
			view = view_task;
			ll_end = (LinearLayout) view.findViewById(R.id.ll_end);
			scheduleEndDate = (TextView) view
					.findViewById(R.id.schedule_end_date);
			if (scheduleEndDate.getText().toString().length() == 0)
				scheduleEndDate.setText(currentDate/* MessageInfo.getTaskDate() */
						+ " " + MessageInfo.getTime());
		} else {
			if (currentIndex == MessageInfo.MEETING) {
				view = view_meeting;
			} else {
				view = view_other;
			}
			ll_tt_begin = (LinearLayout) view.findViewById(R.id.ll_tt_begin);

			ll_tt_end = (LinearLayout) view.findViewById(R.id.ll_tt_end);
			scheduleAddress = (EditText) view
					.findViewById(R.id.schedule_address);
			ll_begin = (LinearLayout) view.findViewById(R.id.ll_begin);
			ll_end = (LinearLayout) view.findViewById(R.id.ll_end);
			scheduleBeginDate = (TextView) view
					.findViewById(R.id.schedule_begin_date);
			scheduleBeginTime = (TextView) view
					.findViewById(R.id.schedule_begin_time);
			if (scheduleBeginDate.getText().toString().length() == 0
					|| scheduleBeginTime.getText().toString().length() == 0) {
				scheduleBeginDate.setText(MessageInfo.getScheduleDate(true,
						currentDate)/* MessageInfo.getScheduleDate(true) */);
				scheduleBeginTime.setText(MessageInfo.getTime());
			}
			scheduleEndDate = (TextView) view
					.findViewById(R.id.schedule_end_date);
			scheduleEndTime = (TextView) view
					.findViewById(R.id.schedule_end_time);
			if (scheduleEndDate.getText().toString().length() == 0
					|| scheduleEndTime.getText().toString().length() == 0) {
				scheduleEndDate.setText(MessageInfo.getScheduleDate(true,
						currentDate)/* MessageInfo.getScheduleDate(true) */);
				scheduleEndTime.setText(MessageInfo.getTime());
			}
			ll_all_day = (LinearLayout) view.findViewById(R.id.ll_all_day);

			ll_all_day.setOnClickListener(btnClick);
			ll_begin.setOnClickListener(btnClick);
		}
		scheduleTitle = (EditText) view.findViewById(R.id.schedule_title);

		if (!isFromChat) {
			rl_add_member = (RelativeLayout) view
					.findViewById(R.id.rl_add_member);
			rl_add_member.setOnClickListener(btnClick);
		}

		ll_end.setOnClickListener(btnClick);

		if (bDate == null) {
			beginDate = currentDate/* MessageInfo.getChatDate() */+ " "
					+ MessageInfo.getTime();
		} else {
			beginDate = receiveBeginDate;
		}
		if (eDate == null) {
			endDate = currentDate/* MessageInfo.getChatDate() */+ " "
					+ MessageInfo.getTime();
		} else {
			endDate = receiveEndDate;
		}

		if (currentIndex == type && title != null && title.length() > 0) {
			scheduleTitle.setText(title);
		}

		viewGroup = (LinearLayout) view.findViewById(R.id.ll_view_group);

		addMember = (ImageView) view.findViewById(R.id.iv_add_member);
		addMember.setOnClickListener(btnClick);
		if (isFromChat) {
			addMember.setVisibility(View.GONE);
		}

		if (currentIndex == type) {
			if (eDate != null)
				scheduleEndDate.setText(eDate);
			if (type != MessageInfo.TASK && bDate != null && beginTime != null
					&& endTime != null) {
				scheduleBeginDate.setText(bDate);
				scheduleBeginTime.setText(beginTime);
				scheduleEndTime.setText(endTime);
			}
		}

		if (currentIndex == 0)
			setScrollView(nameIdTaskMap);
		else if (currentIndex == 1)
			setScrollView(nameIdMeetingMap);
		else
			setScrollView(nameIdOtherMap);
	}

	@SuppressWarnings("unchecked")
	private void addMember() {
		Intent intent = new Intent();
		if (currentIndex == MessageInfo.TASK) {
			nameIdMap = (LinkedHashMap<String, String>) nameIdTaskMap.clone();
		} else if (currentIndex == MessageInfo.MEETING) {
			nameIdMap = (LinkedHashMap<String, String>) nameIdMeetingMap
					.clone();
		} else {
			nameIdMap = (LinkedHashMap<String, String>) nameIdOtherMap.clone();
		}

		MessageInfo.nameIdMap = (LinkedHashMap<String, String>) nameIdMap
				.clone();

		intent.setClass(ScheduleAddActivity.this, SelectMemberActivity.class);

		startActivityForResult(intent, CONTACT_REQUEST_CODE);
		overridePendingTransition(R.anim.in_from_down, R.anim.out_of_up);
	}

	public long compDate(String s1, String s2) {
		long d_value = 0;
		Log.e("test", "s1 :" + s1);
		Log.e("test", "s2 :" + s2);
		try {
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			Date date1 = new Date();
			Date date2 = new Date();
			date1 = sf.parse(s1);
			date2 = sf.parse(s2);
			d_value = ((date2.getTime() - date1.getTime()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Log.i("test", "dvalue :" + d_value);
		return d_value;
	}

	/*
	 * 获取PopupWindow实例
	 */
	private void getPopupWindowInstance(String str, String currentTime,
			String comparedTime, boolean isBtime) {
		if (null != mPopupWindow) {
			mPopupWindow = null;
		}

		initPopuptWindow(str, currentTime, comparedTime, isBtime);
	}

	/*
	 * 创建PopupWindow
	 */
	private void initPopuptWindow(String str, String currentTime,
			String comparedTime, boolean isBtime) {
		mPopupWindow = new MyDateTime(this, btnClick, str, currentTime,
				comparedTime, isBtime);
	}

	private OnClickListener btnClick = new OnClickListener() {
		@SuppressLint("NewApi")
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.ll_begin:
				ll_begin.setBackgroundResource(R.drawable.begin_time_selected);
				isBeginPressed = true;
				if (isEndPressed) {
					ll_end.setBackgroundResource(R.drawable.end_time_normal);
					isEndPressed = false;
				}
				getPopupWindowInstance("开始时间", beginDate, null, true);
				mPopupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
				break;
			case R.id.ll_end:
				if (currentIndex != MessageInfo.TASK) {
					ll_end.setBackgroundResource(R.drawable.end_time_selected);
					isEndPressed = true;
				}
				if (isBeginPressed) {
					ll_begin.setBackgroundResource(R.drawable.begin_time_normal);
					isBeginPressed = false;
				}
				getPopupWindowInstance("结束时间", endDate, beginDate, false);
				mPopupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
				break;

			case R.id.button_cancel:
				mPopupWindow.dismiss();
				break;
			case R.id.button_ok:
				int year = MyDateTime.getYear();
				int month = MyDateTime.getMonth();
				int day = MyDateTime.getDay();
				int hour = MyDateTime.getHour();
				int minute = MyDateTime.getMin();

				String time = "";
				String date = "";
				String min = "";
				String hou = "";
				String m = "";
				String d = "";
				if (month < 10) {
					m = String.valueOf(0) + String.valueOf(month);
				} else {
					m = String.valueOf(month);
				}

				if (day < 10) {
					d = String.valueOf(0) + String.valueOf(day);
				} else {
					d = String.valueOf(day);
				}

				if (minute < 10) {
					min = String.valueOf(0) + String.valueOf(minute);
				} else {
					min = String.valueOf(minute);
				}
				if (hour < 13) {
					hou = hour < 10 ? String.valueOf(0) + String.valueOf(hour)
							: String.valueOf(hour);
					time = getResources().getString(R.string.morning) + " "
							+ String.valueOf(hou) + ":" + min;
				} else {
					hou = hour - 12 < 10 ? String.valueOf(0)
							+ String.valueOf(hour - 12) : String
							.valueOf(hour - 12);
					time = getResources().getString(R.string.afternoon) + " "
							+ String.valueOf(hou) + ":" + min;
				}

				String selectedDate = String.valueOf(year) + "-" + m + "-" + d
						+ " " + time;

				Log.i("test", "selectedDate 2:" + selectedDate);
				String selectedTitle = MyDateTime.getTitle();
				String selDate = String.valueOf(year) + "-"
						+ String.valueOf(month) + "-" + String.valueOf(day)
						+ " " + String.valueOf(hour) + ":"
						+ String.valueOf(minute);

				Log.e("test", "beginDate :" + beginDate);
				Log.e("test", "endDate :" + endDate);
				Log.e("test", "selDate :" + selDate);

				if (selectedTitle != null && selectedTitle.equals("开始时间")) {
					String currentDate = MessageInfo.getScheduleToday();
					if (compDate(currentDate, selDate) < 0) {
						Toast.makeText(
								getApplication(),
								getApplication().getString(
										R.string.schedule_begintime_error),
								Toast.LENGTH_SHORT).show();
						return;
					}

					String[] resultEnd = endDate.split(" ");
					String reDate = "";
					if (resultEnd != null && resultEnd.length > 2) {
						if (resultEnd[1].equals("上午")) {
							reDate = resultEnd[0] + " " + resultEnd[2];
						} else {
							String[] strTime = resultEnd[2].split(":");
							if (strTime != null && strTime.length > 1) {
								reDate = resultEnd[0]
										+ " "
										+ String.valueOf(Integer
												.valueOf(strTime[0]) + 12)
										+ ":" + strTime[1];
							}
						}
					}
					Log.e("test", "reDate ::::::::::::" + reDate);

					if (compDate(selDate, reDate) < 0) {// *************************************
						scheduleEndTime.setText(time);
						scheduleEndDate.setText(MessageInfo.getWeek(String
								.valueOf(year)
								+ "-"
								+ String.valueOf(month)
								+ "-" + String.valueOf(day))
								+ " "
								+ m
								+ "-"
								+ d);
						endDate = selectedDate;
					}
				} else if (selectedTitle != null
						&& selectedTitle.equals("结束时间")) {
					Log.e("test", "test....beginDate::" + beginDate);
					String[] resultBegin = beginDate.split(" ");
					String resultDate = "";
					if (resultBegin != null && resultBegin.length > 2) {
						if (resultBegin[1].equals("上午")) {
							resultDate = resultBegin[0] + " " + resultBegin[2];
						} else {
							String[] strTime = resultBegin[2].split(":");
							if (strTime != null && strTime.length > 1) {
								resultDate = resultBegin[0]
										+ " "
										+ String.valueOf(Integer
												.valueOf(strTime[0]) + 12)
										+ ":" + strTime[1];
							}
						}
					}
					Log.e("test", "resultDate ::::" + resultDate);
					Log.e("test", "selDate :::::" + selDate);
					// Log.e("test", "d_value ::" + compDate(resultDate,
					// selDate));
					if (compDate(resultDate, selDate) < 0) {
						Toast.makeText(
								getApplication(),
								getApplication().getString(
										R.string.schedule_endtime_error),
								Toast.LENGTH_SHORT).show();
						return;
					}
				}

				String mon = "";
				String da = "";
				if (month < 10) {
					mon = String.valueOf(0) + String.valueOf(month);
				} else {
					mon = String.valueOf(month);
				}
				if (day < 10) {
					da = String.valueOf(0) + String.valueOf(day);
				} else {
					da = String.valueOf(day);
				}

				if (currentIndex == MessageInfo.TASK) {
					date = String.valueOf(year)
							+ getResources().getString(R.string.year) + mon
							+ getResources().getString(R.string.month) + da
							+ getResources().getString(R.string.seven) + " "
							+ time;
				} else {
					date = MessageInfo
							.getWeek(String.valueOf(year) + "-"
									+ String.valueOf(month) + "-"
									+ String.valueOf(day))
							+ " " + mon + "-" + da;
					if (isBeginPressed)
						scheduleBeginTime.setText(time);
					else
						scheduleEndTime.setText(time);
				}
				if (isBeginPressed)
					scheduleBeginDate.setText(date);
				else
					scheduleEndDate.setText(date);
				if (currentIndex == 0)
					scheduleEndDate.setTextColor(Color.BLACK);

				if (isBeginPressed) {
					beginDate = selectedDate;
				} else {
					endDate = selectedDate;
					Log.i("test", "selectedDate :" + endDate);
				}
				mPopupWindow.dismiss();
				break;
			case R.id.ll_all_day:
				if (currentIndex == MessageInfo.MEETING) {
					if (isMeetingAllDayPressed) {
						ll_all_day
								.setBackgroundResource(R.drawable.all_day_normal);
						ll_tt_begin.setVisibility(View.VISIBLE);
						ll_tt_end.setVisibility(View.VISIBLE);
						isMeetingAllDayPressed = false;
					} else {
						ll_all_day
								.setBackgroundResource(R.drawable.all_day_selected);
						ll_tt_begin.setVisibility(View.GONE);
						ll_tt_end.setVisibility(View.GONE);
						isMeetingAllDayPressed = true;
					}
				} else {
					if (isOtherAllDayPressed) {
						ll_all_day
								.setBackgroundResource(R.drawable.all_day_normal);
						ll_tt_begin.setVisibility(View.VISIBLE);
						ll_tt_end.setVisibility(View.VISIBLE);
						isOtherAllDayPressed = false;
					} else {
						ll_all_day
								.setBackgroundResource(R.drawable.all_day_selected);
						ll_tt_begin.setVisibility(View.GONE);
						ll_tt_end.setVisibility(View.GONE);
						isOtherAllDayPressed = true;
					}
				}
				break;
			case R.id.rl_add_member:
				addMember();
				break;
			case R.id.iv_add_member:
				addMember();
				break;
			case R.id.scheduleadd_right_btn:
				try {
					ids = "";
					names = "";
					sendTitle = scheduleTitle.getText().toString();
					String[] memberId = new String[memberIdList.size()];
					String[] memberName = new String[memberIdList.size()];
					String scheduleType = "";

					if (scheduleTitle.getText().toString().length() == 0) {
						Toast.makeText(ScheduleAddActivity.this,
								getResources().getString(R.string.title_error),
								Toast.LENGTH_LONG).show();
						return;
					} else if (memberIdList.size() == 0) {
						Toast.makeText(
								ScheduleAddActivity.this,
								getResources().getString(
										R.string.participant_error),
								Toast.LENGTH_LONG).show();
						return;
					} else if (beginDate.equals(receiveBeginDate)
							&& endDate.equals(receiveEndDate)
							&& sendTitle.equals(title)
							&& type == currentIndex
							&& CompareStrUtil.romove(
									CompareStrUtil.toArrayList(memberId),
									CompareStrUtil.toArrayList(idStr)).size() == 0
							&& CompareStrUtil.romove(
									CompareStrUtil.toArrayList(memberName),
									CompareStrUtil.toArrayList(nameStr)).size() == 0) {

						Toast.makeText(
								ScheduleAddActivity.this,
								getResources().getString(
										R.string.schedule_error),
								Toast.LENGTH_LONG).show();
						return;
					} else if (memberIdList.size() == 0
							|| memberValueList.size() == 0) {
						Toast.makeText(
								ScheduleAddActivity.this,
								getResources().getString(
										R.string.schedule_member_error),
								Toast.LENGTH_LONG).show();
						return;
					}

					for (int i = 0; i < memberIdList.size(); i++) {
						if (memberIdList.get(i).equals(UserInfo.db_id)) {
							isExist = true;
						}
						if (i == memberIdList.size() - 1) {
							ids += memberIdList.get(i);
							names += memberValueList.get(i);
						} else {
							ids += memberIdList.get(i) + "、";
							names += memberValueList.get(i) + "、";
						}
					}
					if (currentIndex != MessageInfo.TASK) {
						if (currentIndex == MessageInfo.MEETING)
							scheduleType = "1";
						else
							scheduleType = "3";
						if (scheduleAddress.getText() == null
								|| scheduleAddress.getText().toString()
										.length() > 0)
							sendAddress = scheduleAddress.getText().toString();
						else
							sendAddress = "";
					} else {
						scheduleType = "2";
					}
					Log.i("test", "ids :" + ids);
					Log.i("test", "names : " + names);
					Log.i("test", "begindate :" + beginDate);
					Log.i("test", "endDate :" + endDate);

					if (isFromChat) {
						String sendBDate = beginDate;
						String sendEDate = endDate;

						if (ll_tt_begin != null
								&& ll_tt_begin.getVisibility() == View.GONE) {
							String[] tempBegin = beginDate.split(" ");
							String[] tempEnd = endDate.split(" ");
							if (tempBegin != null && tempBegin.length > 1) {
								sendBDate = tempBegin[0];
								sendEDate = tempEnd[0];
							}
						}
						sendItemId = StringWidthWeightRandom.getNextString();

						Intent intent1 = new Intent(ScheduleAddActivity.this,
								ChatActivity.class);

						ChatMsgEntity entity = new ChatMsgEntity();
						entity.setScheduleTitle(sendTitle);
						entity.setScheduleEndTime(sendEDate/* endDate */);
						entity.setScheduleBeginTime(sendBDate/* beginDate */);
						entity.setScheduleAddress(sendAddress);
						entity.setType(MessageInfo.SCHEDULE);
						entity.setmemberIdList(memberIdList);
						entity.setScheduleType(currentIndex);

						entity.setMsguuid(sendItemId);

						entity.setIsComing(false);

						/***************************************/
						entity.setBeginDate(beginDate);
						entity.setEndDate(endDate);

						/*************************************/

						entity.setScheduleItemId(sendItemId);

						intent1.putExtra("ScheduleToChatMessage",
								(Serializable) entity);
						setResult(3, intent1);
						finish();
						overridePendingTransition(R.anim.in_from_up,
								R.anim.out_of_down);
						return;
					}

					sendBeginDate = MessageInfo.StringToLong(beginDate);
					sendEndDate = MessageInfo.StringToLong(endDate);

					if (isExist) {
						if (memberIdList.size() > 2) {
							showRoundProcessDialog();
							if (client == null)
								client = new ClientSocket(
										ScheduleAddActivity.this);
							client.sendMessage(null, 15,
									StringWidthWeightRandom.getNextString(),
									UserInfo.db_id, ids.replace("、", ","),
									sendBeginDate, sendEndDate, sendAddress,
									scheduleType, "2", null, false);
						} else {
							for (int i = 0; i < memberIdList.size(); i++) {
								if (!memberIdList.get(i).equals(UserInfo.db_id)) {
									ids = memberIdList.get(i);
									names = memberValueList.get(i);
								}
							}
							/**********************************/
							if (memberIdList.size() > 1) {

								String sendBDate = beginDate;
								String sendEDate = endDate;

								if (ll_tt_begin != null
										&& ll_tt_begin.getVisibility() == View.GONE) {
									String[] tempBegin = beginDate.split(" ");
									String[] tempEnd = endDate.split(" ");
									if (tempBegin != null
											&& tempBegin.length > 1) {
										sendBDate = tempBegin[0];
										sendEDate = tempEnd[0];
									}
								}

								scheType = "";
								msgType = "1";// group
								if (currentIndex == MessageInfo.TASK) {
									scheType = "2";
								} else if (currentIndex == MessageInfo.MEETING) {
									scheType = "1";
								} else {
									scheType = "3";
								}

								sendItemId = StringWidthWeightRandom
										.getNextString();
								if (client == null)
									client = new ClientSocket(
											ScheduleAddActivity.this);
								client.sendMessage(sendTitle, 13, sendItemId,
										UserInfo.db_id, ids,
										MessageInfo.StringToLong(sendBDate),
										MessageInfo.StringToLong(sendEDate),
										sendAddress, scheType, msgType, null,
										false);
								showRoundProcessDialog();
							} else
								sendSchedule(false);
						}
					} else {
						if (memberIdList.size() > 1) {
							showRoundProcessDialog();
							if (client == null)
								client = new ClientSocket(
										ScheduleAddActivity.this);
							client.sendMessage(null, 15,
									StringWidthWeightRandom.getNextString(),
									UserInfo.db_id, ids.replace("、", ","),
									sendBeginDate, sendEndDate, sendAddress,
									scheduleType, "2", null, false);
						} else {
							sendSchedule(false);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	};

	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case CONTACT_REQUEST_CODE:
			if (resultCode == RESULT_OK) {
				Log.i("test", "size :" + MessageInfo.nameIdMap.size());
				nameIdMap = (LinkedHashMap<String, String>) MessageInfo.nameIdMap
						.clone();
				if (nameIdMap != null) {
					if (currentIndex == 0) {
						nameIdTaskMap = (LinkedHashMap<String, String>) nameIdMap
								.clone();
					} else if (currentIndex == 1) {
						nameIdMeetingMap = (LinkedHashMap<String, String>) nameIdMap
								.clone();
					} else {
						nameIdOtherMap = (LinkedHashMap<String, String>) nameIdMap
								.clone();
					}
					if (nameIdMap != null) {
						setScrollView(nameIdMap);
					}
				}
			}
			break;
		case CHAT_RESULT_CODE:
			String nameStr = "";
			if (memberValueList.size() == 1) {
				nameStr = memberValueList.get(0);
			} else if (memberValueList.size() > 1) {
				nameStr = names;
			}

			ChatMsgEntity msgEntity = new ChatMsgEntity();
			String content = "";
			if (currentIndex == MessageInfo.TASK) {
				content = getResources().getString(R.string.message_item_task);
			} else if (currentIndex == MessageInfo.MEETING) {
				content = getResources().getString(
						R.string.message_item_meeting);
			} else {
				content = getResources().getString(R.string.message_item_other);
			}
			msgEntity.setContent(content);
			msgEntity.setTime(data.getExtras().getString("time"));
			msgEntity.setFullTime(data.getExtras().getString("fullTime"));
			Log.i("test", "format time :" + data.getExtras().getString("time"));
			Log.e("test", "member size :" + memberIdList.size());
			msgEntity.setName(nameStr);
			ArrayList<String> idList = new ArrayList<String>();
			for (int i = 0; i < memberIdList.size(); i++) {
				if (memberIdList.get(i).equals(UserInfo.db_id))
					continue;
				idList.add(memberIdList.get(i));
			}
			if (idList.size() > 0) {
				if (idList.size() == 1) {
					msgEntity.setSenderId(UserInfo.db_id);
					msgEntity.setReceiverId(idList.get(0));
				} else {
					msgEntity.setChatType(MessageInfo.GROUP);
					msgEntity.setReceiverId(groupId);
				}
				Log.e("test", "receiver :" + msgEntity.getReceiverId());
				msgEntity.setType(MessageInfo.SCHEDULE);
				msgEntity.setIsAdd(false);
				msgEntity.setIsComing(false);
				msgEntity.setStatus(MessageInfo.SEND_MESSAGE);
				MessageInfo.messageEntityList.add(msgEntity);
			} else {
				msgEntity.setSenderId(UserInfo.db_id);
			}

			break;
		}
	}

	public void setScrollView(LinkedHashMap<String, String> map) {
		memberValueList.clear();
		memberIdList.clear();
		viewGroup.removeAllViews();
		Set set = map.keySet();
		Iterator it = set.iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			String value = (String) map.get(key);
			memberValueList.add(value);
			memberIdList.add(key);
		}
		for (int i = 0; i < memberValueList.size(); i++) {
			View view = LayoutInflater.from(this).inflate(
					R.layout.schedule_member_item, null);
			TextView tv = (TextView) view.findViewById(R.id.member_name);
			tv.setText(memberValueList.get(i));
			viewGroup.addView(view);
		}
	}

	public void setTabStatus(int index) {
		if (index == 0) {
			scheduleType.setText(getResources().getString(R.string.task));
			mTab1.setBackgroundResource(R.drawable.taskitem_background_pressed);
			mTab2.setBackgroundResource(R.drawable.taskitem_background_normal);
			mTab3.setBackgroundResource(R.drawable.taskitem_background_normal);
		} else if (index == 1) {
			scheduleType.setText(getResources().getString(R.string.meeting));
			mTab1.setBackgroundResource(R.drawable.taskitem_background_normal);
			mTab2.setBackgroundResource(R.drawable.taskitem_background_pressed);
			mTab3.setBackgroundResource(R.drawable.taskitem_background_normal);
		} else {
			scheduleType.setText(getResources().getString(R.string.other));
			mTab1.setBackgroundResource(R.drawable.taskitem_background_normal);
			mTab2.setBackgroundResource(R.drawable.taskitem_background_normal);
			mTab3.setBackgroundResource(R.drawable.taskitem_background_pressed);
		}
	}

	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			currentIndex = index;
			setTabStatus(index);
			mTabPager.setCurrentItem(index);
		}
	};

	public class MyOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageSelected(int arg0) {
			currentIndex = arg0;
			switchView();
			setTabStatus(arg0);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}

	@Override
	public void onBackPressed() {
		if (!isFromChat)
			backToSchedule();
		else {
			Intent intent = new Intent();
			ChatMsgEntity entity = null;
			intent.putExtra("ScheduleToChatMessage", (Serializable) entity);
			setResult(3, intent);
			finish();
			overridePendingTransition(R.anim.in_from_up, R.anim.out_of_down);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		if (UserInfo.isHomePressed) {
			UserInfo.isSendBroadCast = false;
			if (client == null)
				client = new ClientSocket(this);

			client.sendMessage(null, 0,
					StringWidthWeightRandom.getNextString(), null, null, null,
					null, null, null, null, null, true);
		}
		Log.i("test", "scheduleaddactivity onStart");
		// register broadcast
		if (gbr == null) {
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(MessageInfo.GroupBroadCastName);
			gbr = new GroupResultBroadcastReceiver();
			registerReceiver(gbr, intentFilter);
		}
		if (mbr == null) {
			IntentFilter intentFilter2 = new IntentFilter();
			intentFilter2.addAction(MessageInfo.ScheduleResultBroadCast);
			mbr = new MessageBroadcastReceiver();
			registerReceiver(mbr, intentFilter2);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.i("test", "scheduleaddactivity onResume");
	}

	@Override
	public void onPause() {
		super.onPause();
		if (gbr != null) {
			unregisterReceiver(gbr);
			gbr = null;
		}
		if (mbr != null) {
			unregisterReceiver(mbr);
			mbr = null;
		}
		Log.i("test", "scheduleaddactivity onPause..................");
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.i("test", "scheduleaddactivity onStop");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// unregisterReceiver(gbr);
		// unregisterReceiver(mbr);
		Log.i("test", "scheduleaddactivity onDestroy");
	}

	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			if (mPopupWindow != null)
				mPopupWindow.dismiss();
			View v = getCurrentFocus();
			hideSoftInput(v.getWindowToken());
		}
		return super.dispatchTouchEvent(ev);
	}

	public void hideSoftInput(IBinder token) {
		if (token != null) {
			InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			im.hideSoftInputFromWindow(token,
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
}
