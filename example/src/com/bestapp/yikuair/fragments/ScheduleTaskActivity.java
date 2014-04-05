package com.bestapp.yikuair.fragments;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
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
import com.bestapp.yikuair.fragments.ScheduleAddActivity.MessageBroadcastReceiver;
import com.bestapp.yikuair.utils.CompareStrUtil;
import com.bestapp.yikuair.utils.MessageInfo;
import com.bestapp.yikuair.utils.MyDateTime;
import com.bestapp.yikuair.utils.ScheduleEditText;
import com.bestapp.yikuair.utils.SharedPreferencesUtil;
import com.bestapp.yikuair.utils.StringWidthWeightRandom;
import com.bestapp.yikuair.utils.UserInfo;

public class ScheduleTaskActivity extends Activity {
	private int currentIndex = MessageInfo.TASK;
	private ClientSocket client;
	private RelativeLayout rl_add_member;
	private TextView edit;
	private TextView finish;
	private EditText scheduleTitle;
	private TextView scheduleEndDate, scheduleEndTime;
	private String receiveEndDate, beginDate, endDate, eDate, endTime, title,
			itemId;
	private ImageView addMember;
	private LinkedHashMap<String, String> nameIdMap = new LinkedHashMap<String, String>();
	private LinkedHashMap<String, String> nameIdTaskMap = new LinkedHashMap<String, String>();
	private static final int CONTACT_REQUEST_CODE = 1;
	private static final int CHAT_RESULT_CODE = 2;

	private ArrayList<String> memberValueList = new ArrayList<String>();
	private ArrayList<String> memberIdList = new ArrayList<String>();
	private String[] nameStr;
	private String[] idStr;
	private LinearLayout ll_end, viewGroup, ll_tt_end;
	private MyDateTime mPopupWindow;
	private boolean isEndPressed = false;
	private Dialog mDialog;
	private String sendTitle, sendAddress, sendBeginDate, sendEndDate;
	private String ids, names;
	private boolean isExist = true; // for me
	private String groupId;
	public static ScheduleTaskActivity instance = null;
	private ImageButton left_btn;
	private boolean isFromChat;
	private boolean isFromChatToModify;
	private MessageBroadcastReceiver mbr;
	private SharedPreferencesUtil shared;
	private String sendItemId;
	private String scheType;
	private String msgType;// group or individual
	private String taskId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		setContentView(R.layout.schedule_edit_task);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			nameStr = bundle.getStringArray("nameStr");
			idStr = bundle.getStringArray("idStr");
		}

		taskId = getIntent().getStringExtra("taskId");
		
		groupId = getIntent().getStringExtra("groupId");

		isFromChat = getIntent().getBooleanExtra("isFromChat", false);

		/* =============================== */
		isFromChatToModify = getIntent().getBooleanExtra("isFromChatToModify",
				false);
		/* =============================== */

		receiveEndDate = eDate = getIntent().getStringExtra("endDate");
		title = getIntent().getStringExtra("title");
		itemId = getIntent().getStringExtra("ItemId");

		Log.i("test", "endDate: " + eDate);
		Log.i("test", "title: " + title);
		Log.i("test", "itemId :" + itemId);
		Log.e("test", "groupid ::" + groupId);

		if (eDate != null) {
			endTime = MessageInfo.formatTime(eDate);
			eDate = MessageInfo.formatDate(eDate, MessageInfo.TASK);
			Log.i("test", "format eDate :" + eDate);
		}

		initView();

		// register broadcast
		IntentFilter intentFilter2 = new IntentFilter();
		intentFilter2.addAction(MessageInfo.ScheduleResultBroadCast);
		mbr = new MessageBroadcastReceiver();
		registerReceiver(mbr, intentFilter2);

		shared = new SharedPreferencesUtil(this);

	}

	public class MessageBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.e("test",
					"scheduletaskactivity receive broadcast**************");
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

				boolean isDel = false;

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
				MessageInfo.scheduleList.add(new ScheduleItemInfo(
						UserInfo.realName/*
										 * getResources()
										 * .getString(R.string.me)
										 */, sendTitle, beginDate, endDate,
						memberName, memberId, UserInfo.db_id, currentIndex,
						sendItemId, isDel, sendAddress, taskId, groupId));
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

			String sendEDate = endDate;

			/************************/
			/****************************************/
			messageEntity.setGroupIds(ids);
			messageEntity.setGroupNames(names);

			/****************************************/
			messageEntity.setBeginDate(beginDate);
			messageEntity.setEndDate(endDate);

			messageEntity.setScheduleTitle(sendTitle);
			messageEntity.setScheduleBeginTime(beginDate);
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
			Log.e("test", "ids::::::::::::: " + ids);
			Log.e("test", "itemid :" + itemId);

			if (shared.readDataFromShared(ids + "_" + UserInfo.db_id) != null) {
				mDataArrays = shared.readDataFromShared(ids + "_"
						+ UserInfo.db_id);
				for (int i = 0; i < mDataArrays.size(); i++) {

					if (mDataArrays.get(i).getScheduleItemId() != null
							&& mDataArrays.get(i).getScheduleItemId()
									.equals(itemId)) {
						mDataArrays.remove(i);
						mDataArrays.add(messageEntity);
					}
				}
			} else {
				mDataArrays.add(messageEntity);
			}
			shared.saveDatatoShared(ids + "_" + UserInfo.db_id, mDataArrays);
			
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
		Log.i("test", "receviveendDATE:" + receiveEndDate);
		Log.i("test", "sendEndDate:" + endDate);
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
			MessageInfo.scheduleList.add(new ScheduleItemInfo(
					UserInfo.realName/*
									 * getResources() .getString(R.string.me)
									 */, sendTitle, beginDate, endDate,
					memberName, memberId, UserInfo.db_id, currentIndex,
					sendItemId, isDel, null, null, groupId));
		}
		/* =============================== */
		if (isFromChatToModify) {
			Intent data = new Intent();
			data.putExtra("isDel", isDel);
			data.putExtra("itemId", sendItemId);

			ChatMsgEntity entity = new ChatMsgEntity();
			entity.setScheduleTitle(sendTitle);
			entity.setScheduleBeginTime(beginDate);
			entity.setScheduleEndTime(endDate);
			entity.setScheduleAddress(sendAddress);
			entity.setType(MessageInfo.SCHEDULE);
			entity.setmemberIdList(memberIdList);
			entity.setScheduleType(currentIndex);
			entity.setScheduleTaskId(taskId);
			entity.setIsComing(false);

			data.putExtra("schedulemessage", (Serializable) entity);

			setResult(4, data);
			finish();
			overridePendingTransition(R.anim.in_from_down, R.anim.out_of_up);
			return;
		}
		/* =============================== */

		if (id != null && id.equals(UserInfo.db_id)) {
			finish();
			overridePendingTransition(R.anim.in_from_up, R.anim.out_of_down);
			return;
		}

		Log.i("test", "endDate :" + endDate);
		Log.i("test", "id is " + id);
		Log.i("test", "currentId" + currentIndex);

		String sendEDate = endDate;

		/*
		 * String[] tempEnd = endDate.split(" "); if (tempEnd != null &&
		 * tempEnd.length > 1) { sendEDate = tempEnd[0]; }
		 */
		ChatMsgEntity entity = new ChatMsgEntity();
		entity.setScheduleTitle(sendTitle);
		entity.setScheduleEndTime(sendEDate/* endDate */);
		// entity.setScheduleBeginTime(sendBDate/* beginDate */);
		entity.setScheduleAddress(sendAddress);
		entity.setType(MessageInfo.SCHEDULE);
		entity.setmemberIdList(memberIdList);
		entity.setScheduleType(currentIndex);
		entity.setIsComing(false);

		Intent intent1 = new Intent(ScheduleTaskActivity.this,
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

		/*********************************************************/
		intent1.putExtra("schedulemessage", (Serializable) entity);
		String nameStr = "";
		if (memberValueList.size() == 1) {
			nameStr = memberValueList.get(0);
		} else if (memberValueList.size() > 1) {
			nameStr = names;
		}
		ChatMsgEntity msgEntity = new ChatMsgEntity();
		String content = "";
		content = getResources().getString(R.string.message_item_task);
		msgEntity.setContent(content);
		msgEntity.setTime(MessageInfo.getChatTime());
		msgEntity.setFullTime(MessageInfo.getMessageFullTime());
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
		/*
		 * startActivity(intent1); finish();
		 * overridePendingTransition(R.anim.in_from_right, R.anim.out_of_left);
		 */}

	public void initView() {

		client = new ClientSocket(this);
		mDialog = new AlertDialog.Builder(this).create();
		left_btn = (ImageButton) findViewById(R.id.scheduleadd_left_btn);
		left_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				/*
				 * Intent intent = new Intent(); ChatMsgEntity entity = null;
				 * intent.putExtra("ScheduleToChatMessage", (Serializable)
				 * entity); setResult(3, intent);
				 */finish();
				overridePendingTransition(R.anim.in_from_up, R.anim.out_of_down);
			}
		});
		edit = (TextView) findViewById(R.id.tv_edit_right);
		finish = (TextView) findViewById(R.id.tv_finish_right);

		if (isFromChat) {
			edit.setVisibility(View.GONE);
			finish.setVisibility(View.GONE);
		}

		ll_end = (LinearLayout) findViewById(R.id.ll_end);
		scheduleEndDate = (TextView) findViewById(R.id.schedule_end_date);
		if (scheduleEndDate.getText().toString().length() == 0)
			scheduleEndDate.setText(MessageInfo.getTaskDate() + " "
					+ MessageInfo.getTime());
		scheduleTitle = (EditText) findViewById(R.id.schedule_title);

		rl_add_member = (RelativeLayout) findViewById(R.id.rl_add_member);
		/*
		 * rl_add_member.setOnClickListener(btnClick);
		 * 
		 * ll_end.setOnClickListener(btnClick);
		 */

		beginDate = MessageInfo.getChatDate() + " " + MessageInfo.getTime();

		if (eDate == null) {
			endDate = MessageInfo.getChatDate() + " " + MessageInfo.getTime();
		} else {
			endDate = receiveEndDate;
		}

		if (title != null && title.length() > 0) {
			scheduleTitle.setText(title);
		}

		viewGroup = (LinearLayout) findViewById(R.id.ll_view_group);

		addMember = (ImageView) findViewById(R.id.iv_add_member);
		// addMember.setOnClickListener(btnClick);

		if (eDate != null && eDate.length() > 0)
			scheduleEndDate.setText(eDate);

		nameIdTaskMap.put(UserInfo.db_id, UserInfo.realName/*
															 * getResources().
															 * getString
															 * (R.string.me)
															 */);
		restoreScrollView();
		edit.setOnClickListener(btnClick);
		finish.setOnClickListener(btnClick);
	}

	public void backToSchedule() {
		finish();
		overridePendingTransition(R.anim.in_from_up, R.anim.out_of_down);
	}

	@SuppressWarnings("unchecked")
	public void restoreScrollView() {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		for (int i = 0; i < nameStr.length; i++) {
			map.put(idStr[i], nameStr[i]);
		}
		nameIdTaskMap = (LinkedHashMap<String, String>) map.clone();
		setScrollView(map);
	}

	@SuppressWarnings("unchecked")
	private void addMember() {
		Intent intent = new Intent();
		nameIdMap = (LinkedHashMap<String, String>) nameIdTaskMap.clone();

		MessageInfo.nameIdMap = (LinkedHashMap<String, String>) nameIdMap
				.clone();

		intent.setClass(ScheduleTaskActivity.this, SelectMemberActivity.class);

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
			case R.id.tv_edit_right:
				edit.setVisibility(View.GONE);
				finish.setVisibility(View.VISIBLE);
				scheduleTitle.setFocusable(true);
				scheduleTitle.setFocusableInTouchMode(true);
				ll_end.setClickable(true);
				scheduleTitle.requestFocus();
				scheduleTitle.requestFocusFromTouch();
				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
						.showSoftInput(scheduleTitle, 0);
				/*
				 * if(!isFromChatToModify){ rl_add_member.setClickable(true);
				 * rl_add_member.setOnClickListener(btnClick); }
				 */ll_end.setOnClickListener(btnClick);
				break;
			case R.id.ll_end:
				getPopupWindowInstance("结束时间", endDate, beginDate, false);
				mPopupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
				break;
			case R.id.scheduleadd_left_btn:
				finish();
				overridePendingTransition(R.anim.in_from_up, R.anim.out_of_down);
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

				date = String.valueOf(year)
						+ getResources().getString(R.string.year) + mon
						+ getResources().getString(R.string.month) + da
						+ getResources().getString(R.string.seven) + " " + time;

				scheduleEndDate.setText(date);
				scheduleEndDate.setTextColor(Color.BLACK);

				endDate = selectedDate;

				mPopupWindow.dismiss();
				break;

			case R.id.rl_add_member:
				addMember();
				break;
			/*
			 * case R.id.iv_add_member: addMember(); break;
			 */
			case R.id.tv_finish_right:
				try {
					ids = "";
					names = "";
					sendTitle = scheduleTitle.getText().toString();
					String[] memberId = new String[memberIdList.size()];
					String[] memberName = new String[memberIdList.size()];
					String scheduleType = "";
					// boolean isGroup = false;

					for (int i = 0; i < memberIdList.size(); i++) {
						memberId[i] = memberIdList.get(i);
						memberName[i] = memberValueList.get(i);
						if (i == memberIdList.size() - 1) {
							ids += memberIdList.get(i);
							names += memberValueList.get(i);
						} else {
							ids += memberIdList.get(i) + "、";
							names += memberValueList.get(i) + "、";
						}
					}
					
					if (scheduleTitle.getText().toString().length() == 0) {
						Toast.makeText(ScheduleTaskActivity.this,
								getResources().getString(R.string.title_error),
								Toast.LENGTH_SHORT).show();
						return;
					} else if (memberIdList.size() == 0) {
						Toast.makeText(
								ScheduleTaskActivity.this,
								getResources().getString(
										R.string.participant_error),
								Toast.LENGTH_SHORT).show();
						return;
					} else if (endDate.equals(receiveEndDate)
							&& sendTitle.equals(title)
							&& CompareStrUtil.romove(
									CompareStrUtil.toArrayList(memberId),
									CompareStrUtil.toArrayList(idStr)).size() == 0
							&& CompareStrUtil.romove(
									CompareStrUtil.toArrayList(memberName),
									CompareStrUtil.toArrayList(nameStr)).size() == 0) {
						Toast.makeText(
								ScheduleTaskActivity.this,
								getResources().getString(
										R.string.schedule_error),
								Toast.LENGTH_SHORT).show();
						return;
					} else if (memberIdList.size() == 0
							|| memberValueList.size() == 0) {
						Toast.makeText(
								ScheduleTaskActivity.this,
								getResources().getString(
										R.string.schedule_member_error),
								Toast.LENGTH_SHORT).show();
						return;
					}

					Log.i("test", "ids :" + ids);
					Log.i("test", "names : " + names);
					Log.i("test", "endDate :" + endDate);

					if (memberIdList.size() == 1) {
						sendSchedule(false);
						return;
					}


					if (isFromChatToModify) {
						Intent data = new Intent();
						data.putExtra("isDel", true);
						data.putExtra("itemId", itemId);

						ChatMsgEntity entity = new ChatMsgEntity();
						entity.setScheduleTitle(sendTitle);
						entity.setScheduleBeginTime(beginDate);
						entity.setScheduleEndTime(endDate);
						entity.setScheduleAddress(sendAddress);
						entity.setType(MessageInfo.SCHEDULE);
						entity.setmemberIdList(memberIdList);
						entity.setScheduleType(currentIndex);
						entity.setScheduleTaskId(taskId);
						entity.setIsComing(false);

						data.putExtra("schedulemessage", (Serializable) entity);

						setResult(4, data);
						finish();
						overridePendingTransition(R.anim.in_from_down,
								R.anim.out_of_up);
						return;
					}

					String sendBDate = beginDate;
					String sendEDate = endDate;
					String to = "";

					scheType = ""; 
					if (groupId == null){
						for (int i = 0; i < memberIdList.size(); i++) {
							if (!memberIdList.get(i).equals(UserInfo.db_id)) {
								ids = memberIdList.get(i);
								names = memberValueList.get(i);
							}
						} 
						to = ids;
						msgType = "1";// individual
					}
					else {
						msgType = "2";// group
						to = groupId;
					}

					if (currentIndex == MessageInfo.TASK) {
						scheType = "2";
					} else if (currentIndex == MessageInfo.MEETING) {
						scheType = "1";
					} else {
						scheType = "3";
					}

					sendItemId = itemId;

					if (client == null)
						client = new ClientSocket(ScheduleTaskActivity.this);
					client.sendMessage(sendTitle, 13, 	StringWidthWeightRandom.getNextString(),//sendItemId,
							UserInfo.db_id, to,
							MessageInfo.StringToLong(sendBDate),
							MessageInfo.StringToLong(sendEDate), sendAddress,
							scheType, msgType, taskId, false);
					showRoundProcessDialog();

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
					nameIdTaskMap = (LinkedHashMap<String, String>) nameIdMap
							.clone();
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
			content = getResources().getString(R.string.message_item_task);

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

	@Override
	public void onBackPressed() {
		backToSchedule();
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
		Log.i("test", "scheduletaskactivity onStart");
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.i("test", "scheduletaskactivity onResume");
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.i("test", "scheduletaskactivity onPause");
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.i("test", "scheduletaskactivity onStop");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mbr);
		Log.i("test", "scheduletaskactivity onDestroy");
	}

	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			if (mPopupWindow != null)
				mPopupWindow.dismiss();
			View v = getCurrentFocus();
			if (v != null)
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
