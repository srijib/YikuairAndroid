package com.bestapp.yikuair.fragments;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bestapp.yikuair.R;
import com.bestapp.yikuair.database.DBOpenHelper;
import com.bestapp.yikuair.utils.MessageInfo;
import com.bestapp.yikuair.utils.SharedPreferencesUtil;
import com.bestapp.yikuair.utils.StringWidthWeightRandom;
import com.bestapp.yikuair.utils.UserInfo;

public class MessageFragment extends Fragment {

	private ListView listView;
	public static MessageAdapter lstAdapter;
	private SharedPreferencesUtil messageSharedPre;
	private MessageBroadcastReceiver mbr;
	public static LinkedList<MessageItemInfo> messageList = new LinkedList<MessageItemInfo>();
	public static LinkedList<HashMap<String, List<ChatMsgEntity>>> userList = new LinkedList<HashMap<String, List<ChatMsgEntity>>>();
	public static LinkedList<HashMap<String, Boolean>> boolList = new LinkedList<HashMap<String, Boolean>>();
	private DBOpenHelper dbOpenHelper;
	private Cursor cursor;
	private ImageButton rightBtn;
	private ImageButton leftBtn;
	public static RelativeLayout iv_message_default;
	public static RelativeLayout rl_message_list;
	public static MessageFragment instance = null;
	public RelativeLayout rl_message_layout;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		instance = this;
		return inflater.inflate(R.layout.main_message, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.e("test", "messageFragment onActivityCreated...........");

		messageSharedPre = new SharedPreferencesUtil(getActivity());
		if (UserInfo.db_id == null)
			messageSharedPre.getUserInfo();

		// register broadcast
		if (mbr == null) {
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(MessageInfo.MessageBroadCastName);
			mbr = new MessageBroadcastReceiver();
			getActivity().registerReceiver(mbr, intentFilter);
		}
		initView();
		getDataFromShared();
	}

	public String getNameFromDB(String dbId) {
		if (dbId == null)
			return "";
		dbOpenHelper = new DBOpenHelper(getActivity());
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		String name = "";
		cursor = db.rawQuery("select * from contactsTable where dbid=?",
				new String[] { dbId });
		if (cursor.moveToFirst()) {
			name = cursor.getString(2);
		}
		dbOpenHelper.close();
		cursor.close();
		return name;
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
		Log.e("test", "headurl :: " + name);
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

	public void getDataFromLocal() {
		Log.i("test", "getDataFromLocal");
		if (MessageInfo.messageEntityList != null
				&& !MessageInfo.messageEntityList.isEmpty()) {
			Log.e("test", "messageentitylist..size : "
					+ MessageInfo.messageEntityList.size());
			boolean isAdd = false;
			String idKey = "";
			for (int i = 0; i < MessageInfo.messageEntityList.size(); i++) {
				ChatMsgEntity entity = MessageInfo.messageEntityList.get(i);
				isAdd = entity.getIsAdd();
				int type = entity.getChatType();

				Log.e("test", "get status :" + entity.getStatus());

				if (entity.getIsChangeGroupInfo()) {
					Log.e("test", "i == " + i);
					Log.e("test", "****************************************");
					Log.e("test", "new groupInfo: " + entity.getReceiverId());
					Log.e("test",
							"userId :"
									+ MessageInfo.groupMap.get(entity
											.getReceiverId()));
					updateGroupInfo(entity.getReceiverId());
					continue;
				}

				// notice : filter message
				if (entity.getStatus() == MessageInfo.SEND_ARRIVAL
						|| entity.getStatus() == MessageInfo.SEND_READED
						|| (entity.getSenderId() != null && entity
								.getSenderId()
								.equals(UserInfo.companyNews_dbId))
						|| (entity.getSenderId() != null && entity
								.getSenderId().equals(UserInfo.feedback_dbId)))
					continue;
				String groupId = "";
				if (type == MessageInfo.GROUP) {
					if (MessageInfo.groupMap
							.containsKey(entity.getReceiverId())) {
						Log.e("test",
								"userid 1 ::"
										+ MessageInfo.groupMap.get(entity
												.getReceiverId()));
						Log.e("test", "userid 2 ::" + entity.getUserId());

						/*
						 * if (!MessageInfo.groupMap.get(entity.getReceiverId())
						 * .equals(entity.getUserId())) { if (messageSharedPre
						 * == null) messageSharedPre = new
						 * SharedPreferencesUtil( getActivity());
						 * messageSharedPre.saveDatatoShared(
						 * MessageInfo.groupMap.get(entity .getReceiverId()) +
						 * "_" + UserInfo.db_id, ChatActivity.mDataArrays); }
						 */

						idKey = MessageInfo.groupMap
								.get(entity.getReceiverId());
						groupId = entity.getReceiverId();

						boolean isExist = false;
						for (int j = 0; j < messageList.size(); j++) {
							if (messageList.get(j).getGroupId().equals(groupId)
									&& !messageList.get(j).getId()
											.equals(idKey)) {
								isExist = true;
								break;
							}
						}
						if (isExist)
							continue;
					}
				} else {
					if (entity.getStatus() == MessageInfo.RECEIVE_MESSAGE)
						idKey = entity.getSenderId();
					else if (entity.getStatus() == MessageInfo.SEND_MESSAGE)
						idKey = entity.getReceiverId();

					Log.i("test", "senderId :" + entity.getSenderId());
					Log.i("test", "ReceiverId :" + entity.getReceiverId());
				}
				Log.i("test", "idKey :" + idKey);
				Log.i("test", "isAdd :" + isAdd);

				/********************/
				boolean iscoming = false;
				if (entity.getType() == MessageInfo.SCHEDULE)
					iscoming = true;
				else
					iscoming = entity.getIsComing();
				/********************/

				if (isNameShow(idKey, entity, isAdd, entity.getIsComing())) {
					MessageItemInfo msgItem = new MessageItemInfo();
					String names = "";
					if (type == MessageInfo.GROUP) {
						/*
						 * if (entity.getName() != null) { names =
						 * entity.getName(); } else {
						 */
						String[] strId = idKey.split("、");
						for (int j = 0; j < strId.length; j++) {
							if (j == strId.length - 1)
								names += getNameFromDB(strId[j]);
							else
								names += getNameFromDB(strId[j]) + "、";
						}
						// }
					} else {
						if (entity.getName() == null
								|| entity.getName().length() == 0) {
							names = getNameFromDB(idKey);
						} else {
							names = entity.getName();
						}
					}
					msgItem.setContent(entity.getContent());
					msgItem.setTime(MessageInfo.formatMessageItemTime(entity
							.getTime()));
					msgItem.setFullTime(entity.getFullTime());
					if (type == MessageInfo.GROUP) {
						msgItem.setSex("2");// represent group
						msgItem.setHeadUrl("");
					} else {
						msgItem.setSex(getSexFromDB(idKey));
						msgItem.setHeadUrl(getHeadUrlFromDB(idKey));
					}

					Log.e("test", "idkey is == " + idKey);
					Log.e("test", "names is " + names);
					msgItem.setId(idKey);
					msgItem.setName(names);
					msgItem.setGroupId(groupId);

					updateMessageView(msgItem, -1, null, null,
							entity.getIsComing());
				}
			}
			MessageInfo.messageEntityList.clear();
		}
	}

	public void getDataFromShared() {
		List<MessageItemInfo> lstMessage = new ArrayList<MessageItemInfo>();
		Log.e("test", "message getDataFromShared....................");
		if (UserInfo.db_id == null)
			messageSharedPre.getUserInfo();

		if (messageSharedPre.readBoolListFromShared(UserInfo.db_id) != null) {
			boolList = messageSharedPre.readBoolListFromShared(UserInfo.db_id);
			Log.i("test", "boolList size : " + boolList.size());
		}

		if (messageSharedPre.readUserListFromShared(UserInfo.db_id) != null) {
			userList = messageSharedPre.readUserListFromShared(UserInfo.db_id);
			Log.i("test", "usList size : " + userList.size());
		}

		if (messageSharedPre.readMessageItemFromShared(UserInfo.db_id) != null) {
			lstMessage = messageSharedPre
					.readMessageItemFromShared(UserInfo.db_id);
			Log.e("test", "message.size :" + lstMessage.size());
			for (int i = 0; i < lstMessage.size(); i++) {
				updateView(lstMessage.get(i), 0, null, null, true);
			}
		}

		for (int i = 0; i < boolList.size(); i++) {
			boolList.get(i).put(messageList.get(i).getId(), false);
		}
	}

	/************************/
	// for getdatafromshared
	public void updateView(MessageItemInfo messageItem, int id,
			ChatMsgEntity entity, String userID, boolean isNumShow) {
		if (messageItem == null)
			Log.e("test", "messageitem is null");
		else
			Log.e("test", "messageitme :" + messageItem.getContent());
		Log.e("test", "isNumShow ::" + isNumShow);
		if (messageItem != null) {
			messageList.add(messageItem);
			Log.e("test", "messagelist size :" + messageList.size());
			Log.e("test",
					"id is :"
							+ ((messageList.get(messageList.size() - 1).getId())));
			Log.e("test",
					"size :"
							+ userList
									.get(messageList.size() - 1)
									.get((messageList.get(messageList.size() - 1)
											.getId())).size());

			if (isNumShow
					&& userList
							.get(messageList.size() - 1)
							.get((messageList.get(messageList.size() - 1)
									.getId())).size() > 0) {
				messageList.get(messageList.size() - 1).setIsMessageNumVisible(
						View.VISIBLE);
				messageList.get(messageList.size() - 1).setMessageNum(
						userList.get(messageList.size() - 1)
								.get((messageList.get(messageList.size() - 1)
										.getId())).size());
			} else {
				messageList.get(messageList.size() - 1).setIsMessageNumVisible(
						View.INVISIBLE);
			}
			if (messageList != null && !messageList.isEmpty()) {
				iv_message_default.setVisibility(View.GONE);
				rl_message_list.setVisibility(View.VISIBLE);
			}
		} else {
			messageList.get(id).setContent(entity.getContent());
			messageList.get(id).setTime(
					MessageInfo.formatMessageItemTime(entity.getTime()));
			messageList.get(id).setFullTime(entity.getFullTime());
			if (isNumShow
					&& userList.get(id).get((messageList.get(id).getId()))
							.size() > 0) {
				Log.i("test",
						"sizej  :"
								+ userList.get(id)
										.get((messageList.get(id).getId()))
										.size());
				messageList.get(id).setIsMessageNumVisible(View.VISIBLE);
				messageList.get(id).setMessageNum(
						userList.get(id).get((messageList.get(id).getId()))
								.size());
			} else {
				messageList.get(id).setIsMessageNumVisible(View.INVISIBLE);
			}
		}
		lstAdapter.notifyDataSetChanged();
	}

	/************************/

	public void updateMessageView(MessageItemInfo messageItem, int id,
			ChatMsgEntity entity, String userID, boolean isNumShow) {
		if (messageItem == null)
			Log.e("test", "messageitem is null");
		else
			Log.e("test", "messageitme :" + messageItem.getContent());
		Log.e("test", "isNumShow ::" + isNumShow);
		if (messageItem != null) {
			messageList.addFirst(messageItem);
			Log.e("test", "messagelist size :" + messageList.size());
			Log.e("test",
					"id is :"
							+ ((messageList.get(messageList.size() - 1).getId())));
			/*
			 * Log.e("test", "size :" + userList .get(messageList.size() - 1)
			 * .get((messageList.get(messageList.size() - 1) .getId())).size());
			 */if (isNumShow
					&& userList.get(0).get((messageList.get(0).getId())).size() > 0) {
				messageList.get(0).setIsMessageNumVisible(View.VISIBLE);
				messageList.get(0).setMessageNum(
						userList.get(0).get((messageList.get(0).getId()))
								.size());
			} else {
				messageList.get(0).setIsMessageNumVisible(View.INVISIBLE);
			}
			if (messageList != null && !messageList.isEmpty()) {
				iv_message_default.setVisibility(View.GONE);
				rl_message_list.setVisibility(View.VISIBLE);
			}
		} else {
			messageList.get(id).setContent(entity.getContent());
			messageList.get(id).setTime(
					MessageInfo.formatMessageItemTime(entity.getTime()));
			messageList.get(id).setFullTime(entity.getFullTime());
			if (isNumShow
					&& userList.get(id).get((messageList.get(id).getId()))
							.size() > 0) {
				Log.i("test",
						"sizej  :"
								+ userList.get(id)
										.get((messageList.get(id).getId()))
										.size());
				messageList.get(id).setIsMessageNumVisible(View.VISIBLE);
				messageList.get(id).setMessageNum(
						userList.get(id).get((messageList.get(id).getId()))
								.size());
			} else {
				messageList.get(id).setIsMessageNumVisible(View.INVISIBLE);
			}
			MessageItemInfo item = new MessageItemInfo();
			item = messageList.get(id);
			messageList.remove(id);
			messageList.addFirst(item);
			HashMap<String, List<ChatMsgEntity>> map = new HashMap<String, List<ChatMsgEntity>>();
			HashMap<String, Boolean> mapbool = new HashMap<String, Boolean>();
			map = userList.get(id);
			mapbool = boolList.get(id);
			userList.remove(id);
			boolList.remove(id);
			userList.addFirst(map);
			boolList.addFirst(mapbool);
		}
		lstAdapter.notifyDataSetChanged();
	}

	public void initView() {
		rl_message_layout = (RelativeLayout) getActivity().findViewById(
				R.id.rl_message_layout);
		listView = (ListView) getActivity().findViewById(R.id.message_list);

		rl_message_layout.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				for (int i = 0; i < messageList.size(); i++) {
					if (messageList.get(i).getIsDelShow() == View.VISIBLE)
						messageList.get(i).setIsDelShow(View.GONE);
				}
				lstAdapter.notifyDataSetChanged();
				return false;
			}
		});

		leftBtn = (ImageButton) getActivity().findViewById(
				R.id.message_left_btn);
		rl_message_list = (RelativeLayout) getActivity().findViewById(
				R.id.rl_message_list);
		iv_message_default = (RelativeLayout) getActivity().findViewById(
				R.id.rl_message_default);
		if (messageList != null && messageList.isEmpty()) {
			iv_message_default.setVisibility(View.VISIBLE);
			rl_message_list.setVisibility(View.GONE);
		}

		leftBtn.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				int unReadMessageCount = 0;
				for (int i = 0; i < userList.size(); i++) {
					unReadMessageCount += userList.get(i)
							.get((messageList.get(i).getId())).size();
				}
				Log.i("test", "messagecount is " + unReadMessageCount);
				MessageInfo.unReadedMessageCount = unReadMessageCount;
				sendMessageBroadcast();
				return false;
			}
		});

		rightBtn = (ImageButton) getActivity().findViewById(
				R.id.message_right_btn);
		rightBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),
						SelectMemberActivity.class);
				intent.putExtra("type", "startChat");
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.in_from_down,
						R.anim.out_of_up);
			}
		});
		lstAdapter = new MessageAdapter(getActivity(), messageList);
		listView.setAdapter(lstAdapter);

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				/*
				 * for (int i = 0; i < messageList.size(); i++) { if
				 * (messageList.get(i).getIsDelShow() == View.VISIBLE)
				 * messageList.get(i).setIsDelShow(View.GONE); }
				 */

				if (messageList.get(arg2).getIsDelShow() == View.VISIBLE) {
					messageList.get(arg2).setIsDelShow(View.GONE);
					lstAdapter.notifyDataSetChanged();
					return;
				}

				Intent intent = new Intent(getActivity(), ChatActivity.class);
				intent.putExtra("Id", messageList.get(arg2).getId());
				intent.putExtra("name", messageList.get(arg2).getName());
				// intent.putExtra("listId", arg2);
				intent.putExtra(
						"chatmsgList",
						(Serializable) (userList.get(arg2).get(messageList.get(
								arg2).getId())));

				startActivityForResult(intent, 1);
				getActivity().overridePendingTransition(R.anim.in_from_right,
						R.anim.out_of_left);

				messageList.get(arg2).setIsMessageNumVisible(View.INVISIBLE);
				lstAdapter.notifyDataSetChanged();

				userList.get(arg2).get(messageList.get(arg2).getId()).clear();
				boolList.get(arg2).put(messageList.get(arg2).getId(), true);
			}
		});

		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				if (messageList.get(arg2).getIsDelShow() == View.GONE)
					messageList.get(arg2).setIsDelShow(View.VISIBLE);
				else
					messageList.get(arg2).setIsDelShow(View.GONE);
				lstAdapter.notifyDataSetChanged();

				// Button delBtn = (Button) arg1.findViewById(R.id.del);
				/*
				 * if(delBtn.getVisibility() == View.GONE){
				 * delBtn.setVisibility(View.VISIBLE); }
				 */
				return true;
			}
		});
	}

	public class MessageBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.e("test", "message receive broadcast**************");
			ChatMsgEntity entity = new ChatMsgEntity();
			Bundle bundle = intent.getExtras();
			Serializable data = bundle.getSerializable("message");
			if (data != null) {
				entity = (ChatMsgEntity) data;

//				if (entity.getType() == 4) {
//					return;
//				}
				Log.i("test", "getchatType :" + entity.getChatType());
				if (entity.getFromname() != null
						&& !entity.getFromname().equals("")) {
					return;
				}
				if (entity.getIsChangeGroupInfo()) {
					Log.e("test", "****************************************");
					Log.e("test", "new groupInfo: " + entity.getReceiverId());
					Log.e("test",
							"userId :"
									+ MessageInfo.groupMap.get(entity
											.getReceiverId()));
					updateGroupInfo(entity.getReceiverId());
					return;
				}

				if (entity.getStatus() != MessageInfo.RECEIVE_MESSAGE
						|| entity.getSenderId().equals(
								UserInfo.companyNews_dbId)
						|| entity.getSenderId().equals(UserInfo.feedback_dbId))
					return;

				int type = entity.getChatType();
				String idKey = "";
				String groupId = "";
				if (type == MessageInfo.GROUP) {
					Log.i("test", "group.............");

					if (MessageInfo.groupMap != null
							&& MessageInfo.groupMap.containsKey(entity
									.getReceiverId()))
						idKey = MessageInfo.groupMap
								.get(entity.getReceiverId());
					groupId = entity.getReceiverId();
					entity.setGroupId(groupId);
				} else {
					Log.i("test", "individual");
					// if (entity.getSenderId().equals(UserInfo.db_id)) {
					idKey = entity.getSenderId();
					// } else {
					// idKey = entity.getSenderId();
					// }
				}
				// String id = entity.getSenderId();
				String content = entity.getContent();
				String time = entity.getTime();

				if (type == MessageInfo.INDIVIDUAL
						&& idKey.equals(UserInfo.db_id))
					return;

				if (isNameShow(idKey, entity, true, true)) {
					try {

						String names = "";
						if (type == MessageInfo.GROUP) {
							String[] strId = idKey.split("、");
							for (int j = 0; j < strId.length; j++) {
								if (j == strId.length - 1)
									names += getNameFromDB(strId[j]);
								else
									names += getNameFromDB(strId[j]) + "、";
							}
						} else {
							names = getNameFromDB(idKey);
						}

						String sex = "";
						String headurl = "";
						if (type == MessageInfo.GROUP) {
							sex = "2";
							headurl = "";
						} else {
							sex = getSexFromDB(idKey);
							headurl = getHeadUrlFromDB(idKey);
						}
						updateMessageView(new MessageItemInfo(idKey, content,
								MessageInfo.formatMessageItemTime(time), names,
								sex/* getSexFromDB(idKey) */, headurl/*
																	 * getHeadUrlFromDB
																	 * (idKey)
																	 */,
								View.GONE, entity.getFullTime(), groupId), -1,
								null, idKey, true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public void updateGroupInfo(String groupId) {
		String newId;
		String oldId;
		String names = "";
		int i = 0;
		for (i = 0; i < messageList.size(); i++) {
			Log.e("test", "groupId :::" + groupId);
			Log.e("test", "messageList.get(i).getGroupId() :"
					+ messageList.get(i).getGroupId());
			if (messageList.get(i).getGroupId() != null
					&& messageList.get(i).getGroupId().equals(groupId)) {
				newId = MessageInfo.groupMap.get(groupId);
				oldId = messageList.get(i).getId();

				if (newId.equals(oldId))
					continue;

				Log.e("test", "update group's user id............");
				if (!newId.contains(UserInfo.db_id)) {
					messageList.remove(i);
					userList.remove(i);
					boolList.remove(i);
					if (ChatActivity.instance != null
							&& ChatActivity.userId.equals(oldId)) {
						Toast.makeText(getActivity(),
								getActivity().getString(R.string.quit_group),
								Toast.LENGTH_SHORT).show();
						ChatActivity.instance.finish();
					}
					lstAdapter.notifyDataSetChanged();
					return;
				}

				Log.e("test", "newiD ......... :::::" + newId);
				Log.e("test", "oldiD ......... :::::" + oldId);

				messageList.get(i).setId(newId);
				List<ChatMsgEntity> map = new ArrayList<ChatMsgEntity>();
				Boolean bool;
				map = userList.get(i).get(oldId);
				bool = boolList.get(i).get(oldId);
				userList.get(i).remove(oldId);
				boolList.get(i).remove(oldId);
				userList.get(i).put(newId, map);
				boolList.get(i).put(newId, bool);
				String[] strId = newId.split("、");
				for (int j = 0; j < strId.length; j++) {
					if (j == strId.length - 1)
						names += getNameFromDB(strId[j]);
					else
						names += getNameFromDB(strId[j]) + "、";
				}
				Log.e("test", "names :" + names);
				messageList.get(i).setName(names);
				lstAdapter.notifyDataSetChanged();
				if (ChatActivity.instance == null) {
					if (messageSharedPre == null)
						messageSharedPre = new SharedPreferencesUtil(
								getActivity());
					List<ChatMsgEntity> chatMsgList = new ArrayList<ChatMsgEntity>();
					chatMsgList = messageSharedPre.readDataFromShared(oldId
							+ "_" + UserInfo.db_id);
					if (chatMsgList != null && chatMsgList.size() > 0)
						messageSharedPre.saveDatatoShared(newId + "_"
								+ UserInfo.db_id, chatMsgList);
					if (chatMsgList != null)
						chatMsgList.clear();
					messageSharedPre.saveDatatoShared(oldId + "_"
							+ UserInfo.db_id, chatMsgList);
				} else {
					Log.e("test", "idD ......... :::::" + ChatActivity.userId);

					if (ChatActivity.userId.equals(oldId)) {
						/*
						 * if (messageSharedPre == null) messageSharedPre = new
						 * SharedPreferencesUtil( getActivity());
						 * List<ChatMsgEntity> chatMsgList =
						 * (List<ChatMsgEntity>)
						 * ChatActivity.mDataArrays.clone();
						 * Log.e("test","size :::::::::::" +
						 * chatMsgList.size());
						 * messageSharedPre.saveDatatoShared(newId + "_" +
						 * UserInfo.db_id, chatMsgList);
						 */
					} else {

						if (messageSharedPre == null)
							messageSharedPre = new SharedPreferencesUtil(
									getActivity());
						List<ChatMsgEntity> chatMsgList = new ArrayList<ChatMsgEntity>();
						chatMsgList = messageSharedPre.readDataFromShared(oldId
								+ "_" + UserInfo.db_id);
						if (chatMsgList != null && chatMsgList.size() > 0)
							messageSharedPre.saveDatatoShared(newId + "_"
									+ UserInfo.db_id, chatMsgList);
						if (chatMsgList != null)
							chatMsgList.clear();
						messageSharedPre.saveDatatoShared(oldId + "_"
								+ UserInfo.db_id, chatMsgList);
					}
				}
				break;
			}
		}

		if (messageSharedPre == null)
			messageSharedPre = new SharedPreferencesUtil(getActivity());
		messageSharedPre.saveMessagetoShared(UserInfo.db_id, messageList,
				userList, boolList);
	}

	public boolean isNameShow(String id, ChatMsgEntity entity, boolean isSave,
			boolean isNumShow) {
		Log.i("test", "id.... :  " + id);
		Log.e("test", "issave :" + isSave);
		for (int i = 0; i < userList.size(); i++) {
			if (userList.get(i).containsKey(id)) {
				Log.e("test", "boolean: "
						+ boolList.get(i).get(id).booleanValue());
				if (boolList.get(i).get(id).booleanValue() == false && isSave) {
					Log.i("test", "entity contet :" + entity.getContent());
					userList.get(i).get(id).add(entity);
				}
				Log.i("test", "entity.content 1:" + entity.getContent());
				updateMessageView(null, i, entity, id, isNumShow);
				return false;
			}
		}
		HashMap<String, List<ChatMsgEntity>> map = new HashMap<String, List<ChatMsgEntity>>();
		HashMap<String, Boolean> mapbool = new HashMap<String, Boolean>();
		map.put(id, new ArrayList<ChatMsgEntity>());

		if (isSave) {
			map.get(id).add(entity);
		}
		userList.addFirst(map);
		mapbool.put(id, false);
		boolList.addFirst(mapbool);
		return true;
	}

	// for deliver unreaded message count
	private void sendMessageBroadcast() {

		Intent intent = new Intent();
		// Bundle bundle = new Bundle();
		// bundle.putSerializable("message", entity);
		intent.setAction(MessageInfo.MessageBroadCastName);
		// intent.putExtras(bundle);
		intent.putExtra("name",
				getActivity().getResources()
						.getString(R.string.company_message));

		getActivity().sendBroadcast(intent);
	}

	@SuppressLint("NewApi")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.e("test", "onActivityResult..........................");
		if (data == null) {
			Log.e("test", "messagefragment null...............");
			return;
		}

		String content = data.getExtras().getString("content");
		String time = data.getExtras().getString("time");
		String userId = data.getExtras().getString("userId");
		String fullTime = data.getExtras().getString("fullTime");
		String name = data.getExtras().getString("names");
		String newUserId = data.getExtras().getString("newUserId");
		String groupId = data.getExtras().getString("groupId");
		Log.e("test", "userId :" + userId);
		Log.e("test", "names :" + name);
		Log.e("test", "fulltime :" + fullTime);
		if (fullTime == null || fullTime.isEmpty())
			return;

		if (newUserId != null) {
			Log.e("test", "newuserId :" + newUserId);
		}

		// if (content != null && time != null) {
		for (int i = 0; i < messageList.size(); i++) {
			if (messageList.get(i).getGroupId() != null
					&& messageList.get(i).getGroupId().equals(groupId)) {
				if (!messageList.get(i).getId().equals(userId)) {
					if (messageSharedPre == null)
						messageSharedPre = new SharedPreferencesUtil(
								getActivity());
					List<ChatMsgEntity> chatMsgList = (List<ChatMsgEntity>) ChatActivity.mDataArrays
							.clone();
					/*
					 * chatMsgList = messageSharedPre.readDataFromShared(userId
					 * + "_" + UserInfo.db_id);
					 */Log.e("test",
							"chatmsglist.size........." + chatMsgList.size());
					Log.e("test", "id........................ :"
							+ messageList.get(i).getId());
					Log.e("test", "userId........................ :" + userId);
					Log.e("test,", "key is " + messageList.get(i).getId() + "_"
							+ UserInfo.db_id);
					messageSharedPre.saveDatatoShared(messageList.get(i)
							.getId() + "_" + UserInfo.db_id, chatMsgList);
					if (chatMsgList != null)
						chatMsgList.clear();
					messageSharedPre.saveDatatoShared(userId + "_"
							+ UserInfo.db_id, chatMsgList);
				}
			}

			if (messageList.get(i).getId().equals(userId)
					|| (messageList.get(i).getGroupId() != null && messageList
							.get(i).getGroupId().equals(groupId))) {
				if (content != null)
					messageList.get(i).setContent(content);
				/*
				 * messageList.get(i).setName(name); Log.e("test", "name :" +
				 * name);
				 */
				if (time != null)
					messageList.get(i).setTime(
							MessageInfo.formatMessageItemTime(time));
				if (fullTime != null)
					messageList.get(i).setFullTime(fullTime);
				boolList.get(i).put(messageList.get(i).getId(), false);

				break;
			}
		}

		Collections.sort(messageList, new Comparator<MessageItemInfo>() {
			@Override
			public int compare(MessageItemInfo lhs, MessageItemInfo rhs) {
				Date date1 = MessageInfo.stringToDate(lhs.getFullTime());
				Date date2 = MessageInfo.stringToDate(rhs.getFullTime());
				if (date1.before(date2)) {
					return 1;
				}
				return -1;
			}
		});

		LinkedList<HashMap<String, List<ChatMsgEntity>>> tempUserList = new LinkedList<HashMap<String, List<ChatMsgEntity>>>();
		LinkedList<HashMap<String, Boolean>> tempBoolList = new LinkedList<HashMap<String, Boolean>>();
		for (int i = 0; i < messageList.size(); i++) {
			String id = messageList.get(i).getId();

			for (int j = 0; j < userList.size(); j++) {
				if (userList.get(j).containsKey(id)) {
					HashMap<String, List<ChatMsgEntity>> map = new HashMap<String, List<ChatMsgEntity>>();
					HashMap<String, Boolean> mapbool = new HashMap<String, Boolean>();
					/*
					 * if (messageList.get(i).getId().equals(userId) &&
					 * newUserId != null) { messageList.get(i).setId(newUserId);
					 * map.put(newUserId, userList.get(j).get(id));
					 * mapbool.put(newUserId, boolList.get(j).get(id)); } else {
					 */map.put(id, userList.get(j).get(id));
					mapbool.put(id, boolList.get(j).get(id));
					// }
					tempUserList.add(map);
					tempBoolList.add(mapbool);

					break;
				}
			}
		}
		userList = tempUserList;
		boolList = tempBoolList;

		Log.e("test", "u.size ::" + userList.size());
		Log.e("test", "b.size ::" + boolList.size());

		/*
		 * messageList.get(resultCode).setContent(content);
		 * messageList.get(resultCode).setTime(
		 * MessageInfo.formatMessageItemTime(time));
		 * boolList.get(resultCode).put(messageList.get(resultCode).getId(),
		 * false);
		 */lstAdapter.notifyDataSetChanged();
		// }
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.i("test", "message onStart");
		if (UserInfo.isHomePressed) {
			for (int i = 0; i < boolList.size(); i++) {
				if (boolList.get(i) != null && messageList.get(i) != null)
					boolList.get(i).put(messageList.get(i).getId(), false);
			}
			ClientSocket client = new ClientSocket(getActivity());
			UserInfo.isSendBroadCast = false;
			client.sendMessage(null, 0,
					StringWidthWeightRandom.getNextString(), null, null, null,
					null, null, null, null, null, true);
			UserInfo.isHomePressed = false;
		}
		instance = this;
		getDataFromLocal();
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.i("test", "message onResume");
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.i("test", "message onPause");
	}

	@Override
	public void onStop() {
		super.onStop();
		instance = null;
		if (messageSharedPre == null)
			messageSharedPre = new SharedPreferencesUtil(getActivity());
		messageSharedPre.saveMessagetoShared(UserInfo.db_id, messageList,
				userList, boolList);

		Log.e("test", "messageList....size :" + messageList.size());
		Log.e("test", "userList....size :" + userList.size());
		Log.e("test", "boolList....size :" + boolList.size());

		Log.i("test", "message onStop");
	}

	public void onDestroy() {
		super.onDestroy();
		if (mbr != null) {
			getActivity().unregisterReceiver(mbr);
			mbr = null;
		}
		/*
		 * if (gbr != null) { getActivity().unregisterReceiver(gbr); gbr = null;
		 * }
		 */
		/********************************************/
		messageList.clear();
		userList.clear();
		boolList.clear();
		Log.i("test", "message onDestroy");
	}
}
