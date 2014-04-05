package com.bestapp.yikuair.fragments;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bestapp.yikuair.R;
import com.bestapp.yikuair.adapter.MenuListAdapter;
import com.bestapp.yikuair.database.DBOpenHelper;
import com.bestapp.yikuair.utils.BitmapCompressUtil;
import com.bestapp.yikuair.utils.DBlog;
import com.bestapp.yikuair.utils.DataUtil;
import com.bestapp.yikuair.utils.FriendEntity;
import com.bestapp.yikuair.utils.MenuHttp;
import com.bestapp.yikuair.utils.MenuHttp.MenuData;
import com.bestapp.yikuair.utils.MessageInfo;
import com.bestapp.yikuair.utils.PullToRefreshBase.OnRefreshListener;
import com.bestapp.yikuair.utils.PullToRefreshListView;
import com.bestapp.yikuair.utils.SharedPreferencesUtil;
import com.bestapp.yikuair.utils.StringWidthWeightRandom;
import com.bestapp.yikuair.utils.UploadFileUtil;
import com.bestapp.yikuair.utils.UserInfo;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class ChatActivity extends Activity implements OnClickListener,
		OnItemClickListener, Serializable {

	private static final long serialVersionUID = -4663986401966063219L;
	private Button mBtnSend;
	private ImageButton menuBtn;
	private GridView topGridView;
	private Button pressTalkBtn;
	private EditText mEditTextContent;
	private TextView chatName;
	private ListView mListView;
	public static ChatMsgViewAdapter mAdapter;
	private String names;
	private GridView menuGridView;
	private ImageButton voiceBtn;
	private ImageButton keyboardBtn;
	private ImageButton plusBtn;
	private boolean isGridShow = false;
	private boolean isTopGridShow = false;
	private FriendEntity friendEntity;
	private RelativeLayout ll_fasong;
	private RelativeLayout ll_yuyin;
	private List<ChatMsgEntity> chatMsgList = new ArrayList<ChatMsgEntity>();
	public static LinkedList<ChatMsgEntity> mDataArrays = new LinkedList<ChatMsgEntity>();
	public static LinkedList<ChatMsgEntity> mDataArrays_no_show = new LinkedList<ChatMsgEntity>();
	public static List<ChatMsgEntity> lstMessage = new ArrayList<ChatMsgEntity>();
	private List<ChatMsgEntity> tempList = new ArrayList<ChatMsgEntity>();
	private List<gridItemInfo> lstMenuItem = new ArrayList<gridItemInfo>();
	private List<gridItemInfo> topMenuItem = new ArrayList<gridItemInfo>();
	private HashMap<String, Integer> msguidMap = new HashMap<String, Integer>();
	private ChatBroadcastReceiver cbr;
	private long downTime;
	private long upTime;
	private long LOWLIMITTIME = 400;
	private long HIGHLIMITTIME = 1000;
	private MediaRecorder mRecorder;
	private File tempFile;
	private PopupWindow menuWindow = null;
	private File mRecAudioPath;
	private static final String STORE_RECORDS_PATH = "STORE_RECORDS_PATH";
	private static int RESULT_LOAD_IMAGE = 1;
	private static int RESULT_SHOW_IMAGE = 2;
	private static int RESULT_SCHEDULE = 3; // from schedule
	private static int RESULT_MODIFY_SCHEDULE = 4;
	private static int RESULT_TASK_MEMBER = 5;
	private static int RESULT_LOCATION = 6;

	private static int SHOW_MAX_COUNT = 10;
	private int listId;
	private int itemCount;
	public static String userId;
	private String lastContent = null;
	private String lastTime = null;
	private String lastFullTime = null;
	private SharedPreferencesUtil chatSharedPre;
	private UploadFileUtil uploadFileInstance;
	private PullToRefreshListView mPullRefreshListView;
	public static String groupId;
	private int memberCount;
	private String prevDate = null;
	public static ChatActivity instance = null;
	public Chronometer timer;
	public boolean isFromSelectMember = false;
	public TextView tv_left_title;
	public String newUserId = null;
	public ClientSocket client;
	private ArrayList<MenuData> menuDatas = new ArrayList<MenuData>();
	private MenuHttp menuHttp = new MenuHttp();
	private int Type;
	private int MenuType = 0;
	private String token;
	private int MenuId;
	private String fromname = "";

	public void getBackInfo(String josn) {
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(josn);
			ChatMsgEntity entity = new ChatMsgEntity();
			entity.setStatus(MessageInfo.RECEIVE_MESSAGE);
			entity.setTime(MessageInfo.getChatTime());
			entity.setFullTime(MessageInfo.getMessageFullTime());
			entity.setIsComing(true);
			entity.setSenderId(userId);
			entity.setReceiverId(UserInfo.db_id);
			entity.setMsguuid(menuDatas.get(MenuId).msguuid);

			if (jsonObject.has("token")) {
				token = jsonObject.getString("token");
				if (token.equals("1")) {
					String content = new String(
							DataUtil.decodeBase64(jsonObject
									.getString("content")));
					entity.setContent(content);
					entity.setType(MessageInfo.TEXT);

				} else if (token.equals("2")) {
					String smallImgPath = jsonObject.getString("smallImgPath");
					String filePath = jsonObject.getString("filePath");
					entity.setSmallPicUrl(smallImgPath);
					entity.setBigPicUrl(filePath);
					entity.setType(MessageInfo.PICTURE);

				} else if (token.equals("3")) {
					String filePath = jsonObject.getString("filePath");
					entity.setType(MessageInfo.VOICE);
					entity.setVoiceUrl(filePath);

				} else if (token.equals("4")) {
					String imgpath = jsonObject.getString("imgpath");
					String title = new String(DataUtil.decodeBase64(jsonObject
							.getString("title")));
					String content = new String(
							DataUtil.decodeBase64(jsonObject
									.getString("content")));
					entity.setType(MessageInfo.PIC_TEXT);
					entity.setContent(content);
					entity.setTitle(title);
					entity.setSmallPicUrl(imgpath);
				} else if (token.equals("5")) {
					String detail = new String(DataUtil.decodeBase64(jsonObject
							.getString("detail")));
					String url = new String(DataUtil.decodeBase64(jsonObject
							.getString("url")));
					entity.setType(MessageInfo.INFO_WEB);
					entity.setUrl(url);
					entity.setDetail(detail);
				}
			}
			updateChatStatus(entity);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void getMenuBackInfo(String josn) throws JSONException {
		DBlog.e("dsds", new String(DataUtil.decodeBase64(josn)));
		DBlog.e("dsds", josn);

		JSONObject jsonObject = new JSONObject(josn.toString());

		if (jsonObject.has("token")) {
			getBackInfo(josn);
			return;
		} else if (jsonObject.has("data")) {
			JSONArray jsonArray = jsonObject.getJSONArray("data");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject2 = (JSONObject) jsonArray.opt(i);
				JSONObject jsonObject3 = new JSONObject(
						jsonObject2.getString("content"));

				if (jsonObject3.has("token")) {
					if (jsonObject3.has("content")) {

						ChatMsgEntity entity = new ChatMsgEntity();
						entity.setStatus(MessageInfo.RECEIVE_MESSAGE);
						entity.setTime(MessageInfo.getChatTime());
						entity.setFullTime(MessageInfo.getMessageFullTime());
						entity.setIsComing(true);
						entity.setSenderId(userId);
						entity.setReceiverId(UserInfo.db_id);
						entity.setMsguuid(jsonObject3.getString("msguuid"));

						token = jsonObject3.getString("token");
						HashMap<String, String> map = new HashMap<String, String>();
						if (token.equals("1")) {
							String content = new String(
									DataUtil.decodeBase64(jsonObject3
											.getString("content")));
							map.put("content", content);
							entity.setContent(content);
							entity.setType(MessageInfo.TEXT);

						} else if (token.equals("2")) {
							String smallImgPath = jsonObject3
									.getString("smallImgPath");
							String filePath = jsonObject3.getString("filePath");
							map.put("smallImgPath", smallImgPath);
							map.put("filePath", filePath);
							entity.setSmallPicUrl(smallImgPath);
							entity.setBigPicUrl(filePath);
							entity.setType(MessageInfo.PICTURE);
						} else if (token.equals("3")) {
							String filePath = jsonObject3.getString("filePath");
							map.put("filePath", filePath);
							entity.setType(MessageInfo.VOICE);
							entity.setVoiceUrl(filePath);
						} else if (token.equals("4")) {
							String imgpath = jsonObject3.getString("imgpath");
							String title = new String(
									DataUtil.decodeBase64(jsonObject3
											.getString("title")));
							String content = new String(
									DataUtil.decodeBase64(jsonObject3
											.getString("content")));
							map.put("content", content);
							map.put("title", title);
							map.put("imgpath", imgpath);
							entity.setType(MessageInfo.PICTURE);
							entity.setSmallPicUrl(imgpath);
						}

						updateChatStatus(entity);
					}
				}
			}
		} else if (jsonObject.has("message")) {

		}

	}

	final AsyncHttpResponseHandler ResponseHandler = new AsyncHttpResponseHandler() {
		@Override
		public void onSuccess(int arg0, String arg1) {
			try {
				if (MenuType == 0) {
					menuDatas = menuHttp.getMenuList(arg1);
				} else if (MenuType == 1) {
					getBackInfo(arg1);
				} else if (MenuType == 2) {
					getMenuBackInfo(arg1);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		};

		@Override
		public void onFailure(Throwable arg0, String arg1) {
		};
	};

	public void onClickMenu(int arg0) {
		if (menuDatas.size() > arg0) {
			try {
				MenuId = arg0;
				menuHttp.useMenu(newUserId, menuDatas.get(arg0),
						ResponseHandler);
				MenuType = 1;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private PopupWindow mPopupWindow;
	private ListView menuListView;
	private MenuListAdapter menuListAdapter;

	public void show(View view) {

		if (menuDatas.size() > 0) {
			if (mPopupWindow == null) {
				LayoutInflater layoutInflater = LayoutInflater.from(this);
				View popupWindow = layoutInflater.inflate(
						R.layout.view_menu_list, null);
				menuListView = (ListView) popupWindow
						.findViewById(R.id.menu_list_view);
				menuListView.setOnItemClickListener(this);
				menuListAdapter = new MenuListAdapter(this, menuDatas);
				menuListView.setAdapter(menuListAdapter);
				mPopupWindow = new PopupWindow(popupWindow,
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				mPopupWindow.setFocusable(true);
				mPopupWindow.setOutsideTouchable(true);
				mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
			}
			int[] location = new int[2];
			view.getLocationOnScreen(location);

			mPopupWindow.showAtLocation(view, Gravity.NO_GRAVITY, location[0],

			location[1] - mPopupWindow.getHeight() - view.getHeight());

		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		setContentView(R.layout.chat_message);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		client = new ClientSocket(this);

		Intent intent = getIntent();

		if (intent.hasExtra("type")) {
			Type = intent.getIntExtra("type", 1);
			String id = intent.getStringExtra("Id");
			FrameLayout fl = (FrameLayout) findViewById(R.id.rl);
			fl.setVisibility(View.VISIBLE);
			try {
				menuHttp.getMenuList(id, ResponseHandler);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		names = intent.getStringExtra("name");
		fromname = intent.getStringExtra("fromname");
		if (fromname == null) {
			fromname = "";
		}
		isFromSelectMember = intent
				.getBooleanExtra("isFromSelectMember", false);

		Log.e("test", "isFromSelectMember :" + isFromSelectMember);

		listId = intent.getIntExtra("listId", -1);
		userId = intent.getStringExtra("Id");
		groupId = intent.getStringExtra("group_id");

		chatSharedPre = new SharedPreferencesUtil(this);
		if (UserInfo.db_id == null)
			chatSharedPre.getUserInfo();

		Log.e("test", "userID.......: " + userId);
		Log.e("test", "names :" + names);
		Log.e("test", "groupId :" + groupId);
		chatMsgList = (List<ChatMsgEntity>) getIntent().getSerializableExtra(
				"chatmsgList");

		ChatMsgEntity entity = (ChatMsgEntity) getIntent()
				.getSerializableExtra("schedulemessage");

		uploadFileInstance = new UploadFileUtil(this);
		chatSharedPre = new SharedPreferencesUtil(this);

		getGroupId();
		initChatView(names);
		getLocalMessage(chatMsgList);

		if (groupId != null) {
			setViewVisible();
			memberCount = getGroupMemberCount();
		}

		if (groupId == null)
			friendEntity = getEntityFromUserID(userId);// for individual chat

		if (entity != null) {
			if (entity.getChatType() == MessageInfo.GROUP) {
				groupId = entity.getReceiverId();
				memberCount = getGroupMemberCount();
				setViewVisible();
			}
			tv_left_title = (TextView) findViewById(R.id.tv_left_title);
			tv_left_title.setText(getResources().getString(
					R.string.chat_schedule));
			/*
			 * List<String> receiverList = entity.getMemberIdList(); String
			 * receiverStr = "";
			 * 
			 * for (int i = 0; i < receiverList.size(); i++) { if (i ==
			 * receiverList.size() - 1) receiverStr += receiverList.get(i); else
			 * receiverStr += receiverList.get(i) + ","; }
			 */createLocalmsgEntity(
					entity.getType(),
					entity.getBeginDate(),
					entity.getEndDate(),
					entity.getReceiverId(),// receiverStr,
					StringWidthWeightRandom.getNextString(),
					entity.getScheduleTitle(), entity.getScheduleBeginTime(),
					entity.getScheduleEndTime(), entity.getScheduleAddress(),
					entity.getScheduleType(), null, fromname);

		}
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(MessageInfo.MessageBroadCastName);
		instance = this;
		cbr = new ChatBroadcastReceiver();
		registerReceiver(cbr, myIntentFilter);
	}

	public void getGroupId() {
		if (MessageInfo.groupMap != null) {
			Set set = MessageInfo.groupMap.keySet();
			Iterator it = set.iterator();
			while (it.hasNext()) {
				String key = (String) it.next();
				String value = (String) MessageInfo.groupMap.get(key);
				Log.e("test", "value :" + value);
				if (value.equals(userId)) {
					Log.e("test", "key :" + key);
					groupId = key;
				}
			}
		}
		if (groupId == null) {
			groupId = chatSharedPre.getGroupInfo(userId + "_" + UserInfo.db_id);
		}
	}

	public int getGroupMemberCount() {
		String[] str = userId.split("、");
		if (str != null)
			return str.length;
		return 0;
	}

	public void setViewVisible() {
		ImageButton menuBtn = (ImageButton) findViewById(R.id.ib_open_menu);
		ImageButton menuGroup = (ImageButton) findViewById(R.id.ib_open_group);
		menuBtn.setVisibility(View.GONE);
		menuGroup.setVisibility(View.VISIBLE);
	}

	public class ChatBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			ChatMsgEntity entity = new ChatMsgEntity();
			Bundle bundle = intent.getExtras();
			Serializable data = bundle.getSerializable("message");
			Log.e("test", "chatactivity receive broadcast");
			if (data != null) {
				entity = (ChatMsgEntity) data;
				Log.e("test", "type is " + entity.getChatType());
				Log.e("test", "groupID is " + groupId);
				Log.e("test", "entity.reciverid  :" + entity.getReceiverId());
				Log.e("test", "msguuid :" + entity.getMsguuid());
				Log.e("test", "size:::: :" + msguidMap.size());

				if ((entity.getChatType() == MessageInfo.INDIVIDUAL && entity
						.getSenderId().equals(userId))
						|| (entity.getChatType() == MessageInfo.INDIVIDUAL && entity
								.getReceiverId().equals(userId))
						|| (entity.getChatType() == MessageInfo.GROUP
								&& groupId != null && groupId.equals(entity
								.getReceiverId()))
						|| (entity.getChatType() == MessageInfo.GROUP
								&& groupId != null && userId.contains(entity
								.getReceiverId()))
						|| msguidMap.containsKey(entity.getMsguuid())) {
					Log.e("test", "gourpid :" + groupId);
					Log.e("test", "sender :" + entity.getSenderId());
					Log.e("test", "receiver :" + entity.getReceiverId());
					Log.i("test", "userId" + userId);
					Log.i("test", "55555555555555");
					if (prevDate == null) {
						if (chatSharedPre.getChatDate(userId + "_"
								+ UserInfo.db_id) != null
								&& chatSharedPre.getChatDate(
										userId + "_" + UserInfo.db_id).length() > 0) {
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
				} else if (entity.getIsComing() == true
						&& entity.getStatus() == MessageInfo.RECEIVE_MESSAGE) {
					MessageInfo.messageEntityList.add(entity);
				}
			}
		}
	}

	public void getLocalMessage(List<ChatMsgEntity> chatInfoList) {
		Log.e("test", "getlocalmessage :");

		List<ChatMsgEntity> tempList = new ArrayList<ChatMsgEntity>();
		String getMessageKey = "";

		if (fromname.equals("")) {
			getMessageKey = userId + "_" + UserInfo.db_id;
		} else {
			getMessageKey = userId + "_" + UserInfo.db_id + "_" + "f";
		}
		if (chatSharedPre.readDataFromShared(getMessageKey) != null) {
			Log.e("test", "key is " + getMessageKey);
			lstMessage = chatSharedPre.readDataFromShared(getMessageKey);
			// List<ChatMsgEntity> newList = new ArrayList<ChatMsgEntity>();
			// if (fromname.equals("")) {
			// for (ChatMsgEntity chatMsgEntity : lstMessage) {
			// if (chatMsgEntity.getFromname() == null
			// || chatMsgEntity.getFromname().equals("")) {
			// newList.add(chatMsgEntity);
			// } else {
			// mDataArrays_no_show.add(chatMsgEntity);
			// }
			// }
			// } else {
			// for (ChatMsgEntity chatMsgEntity : lstMessage) {
			//
			// if (chatMsgEntity.getFromname() != null
			// && !chatMsgEntity.getFromname().equals("")) {
			// newList.add(chatMsgEntity);
			// } else {
			// mDataArrays_no_show.add(chatMsgEntity);
			// }
			// }
			// }
			// lstMessage.clear();
			// lstMessage.addAll(newList);
			// for (ChatMsgEntity chatMsgEntity : lstMessage) {
			//
			// if (!fromname.equals(chatMsgEntity.getFromname())) {
			// continue;
			// }
			// }

			itemCount = lstMessage.size();
			Log.e("test", "count is " + itemCount);
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
			Log.i("test", "chatInfoList.size : " + chatInfoList.size());
			for (int i = 0; i < chatInfoList.size(); i++) {
				if (chatInfoList.get(i).getChatType() == MessageInfo.GROUP) {
					Log.e("test", "group..................................");
					groupId = chatInfoList.get(i).getReceiverId();
					setViewVisible();
				}

				Log.e("test", "receive localnew message");
				String tempReceiver = UserInfo.db_id;
				String tempSender = userId;
				int tempType = 1;
				if (groupId != null && groupId.length() > 0) {
					tempReceiver = UserInfo.db_id;
					tempSender = chatInfoList.get(i).getSenderId();
					tempType = 2;
				}
				if (client == null)
					client = new ClientSocket(this);

				client.sendMessage(null, 8, chatInfoList.get(i).getMsguuid(),
						tempSender, tempReceiver, null, null, null, null,
						String.valueOf(tempType), null, false, fromname);

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

				if (chatInfoList.get(i).getType() == MessageInfo.SCHEDULE) {
					Log.e("test", "entity.msguuid  ::::"
							+ chatInfoList.get(i).getMsguuid());

					if (chatInfoList.get(i).getScheduleTaskId() != null
							&& chatInfoList.get(i).getScheduleTaskId().length() > 0) {
						for (int j = 0; j < mDataArrays.size(); j++) {
							if (mDataArrays.get(j).getScheduleTaskId() != null
									&& mDataArrays
											.get(j)
											.getScheduleTaskId()
											.equals(chatInfoList.get(i)
													.getScheduleTaskId())) {
								mDataArrays.get(j).setCheckStatus(
										getResources().getString(
												R.string.canceled));
								mDataArrays.get(j).setCheckBackground(
										R.drawable.bt_confirm_pressed);
								int k = 0;
								for (k = 0; k < MessageInfo.scheduleList.size(); k++) {
									if (MessageInfo.scheduleList
											.get(k)
											.getTaskId()
											.equals(chatInfoList.get(i)
													.getScheduleTaskId())) {
										MessageInfo.scheduleList.remove(k);
										break;
									}
								}
								if (k == MessageInfo.scheduleList.size())
									MessageInfo.scheduleList
											.add(new ScheduleItemInfo(
													UserInfo.realName,
													null,
													null,
													null,
													null,
													null,
													UserInfo.db_id,
													0,
													null,
													true,
													null,
													chatInfoList
															.get(i)
															.getScheduleTaskId(),
													groupId));
							}
						}
					}

					if (chatInfoList.get(i).getCheckStatus() == null
							|| (chatInfoList.get(i).getCheckStatus() != null && !chatInfoList
									.get(i)
									.getCheckStatus()
									.equals(getResources().getString(
											R.string.canceled)))) {
						chatInfoList.get(i).setCheckStatus(
								getResources().getString(R.string.click_check)
										+ " ");
						chatInfoList.get(i).setCheckBackground(
								R.drawable.bt_confirm_normal);

					} else {
						Log.e("test", "********************************");
						Log.e("test", "checkstatus :"
								+ chatInfoList.get(i).getCheckStatus());
					}
					/*******************************************/
					Log.e("test", "groupnames :" + names);
					Log.e("test", "ids :" + userId);
					chatInfoList.get(i).setGroupNames(names);
					chatInfoList.get(i).setGroupIds(userId);
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

	public void initChatView(String name) {

		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);

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
				hideSoftInput(v.getWindowToken());
				setTopGridViewVisibility(false);

				setGridViewVisibility(false);

				return false;
			}
		});

		mBtnSend = (Button) findViewById(R.id.btn_send);
		mBtnSend.setOnClickListener(this);
		mEditTextContent = (EditText) findViewById(R.id.et_sendmessage);
		mEditTextContent.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				setGridViewVisibility(false);
			}
		});

		menuBtn = (ImageButton) findViewById(R.id.ib_open_menu);
		menuBtn.setOnClickListener(this);
		topGridView = (GridView) findViewById(R.id.top_gridView);
		chatName = (TextView) findViewById(R.id.chat_name);
		menuGridView = (GridView) findViewById(R.id.message_gridView);
		ll_fasong = (RelativeLayout) findViewById(R.id.ll_fasong);
		ll_yuyin = (RelativeLayout) findViewById(R.id.ll_yuyin);
		pressTalkBtn = (Button) findViewById(R.id.btn_yuyin);
		voiceBtn = (ImageButton) findViewById(R.id.chatting_voice_btn);
		voiceBtn.setOnClickListener(this);
		keyboardBtn = (ImageButton) findViewById(R.id.chatting_keyboard_btn);
		keyboardBtn.setOnClickListener(this);
		plusBtn = (ImageButton) findViewById(R.id.chatting_plus_btn);
		plusBtn.setOnClickListener(this);

		chatName.setText(name);

		topMenuItem.add(new gridItemInfo(getResources().getString(
				R.string.menu_phone), R.drawable.ico_phone));
		topMenuItem.add(new gridItemInfo(getResources().getString(
				R.string.menu_data), R.drawable.ico_data));
		/*
		 * topMenuItem.add(new gridItemInfo(getResources().getString(
		 * R.string.menu_shield), R.drawable.ico_forbiden));
		 */
		topMenuItem.add(new gridItemInfo(getResources().getString(
				R.string.menu_invite), R.drawable.ico_invite));
		topGridView.setAdapter(new TopGridViewAdapter(this, topMenuItem));

		topGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				switch (arg2) {
				case 0:
					String phoneNum = friendEntity.getMobile();
					if (phoneNum == null || "".equals(phoneNum.trim())) {
						Toast.makeText(getApplicationContext(),
								getResources().getString(R.string.dial_error),
								Toast.LENGTH_SHORT).show();
					} else {
						Intent intent = new Intent(Intent.ACTION_CALL, Uri
								.parse("tel:" + phoneNum));
						startActivity(intent);
					}
					break;
				case 1:
					Intent intent = new Intent(ChatActivity.this,
							PersonalProfileActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("friendEntity", friendEntity);
					intent.putExtras(bundle);
					intent.putExtra("isFromChat", true);
					startActivity(intent);
					overridePendingTransition(R.anim.in_from_right,
							R.anim.out_of_left);
					break;
				case 2:
					Intent intent_1 = new Intent(ChatActivity.this,
							SelectMemberActivity.class);
					intent_1.putExtra("type", "startChat");
					String idStr = userId;
					if (groupId == null) {
						idStr = idStr;
					}
					if (mDataArrays != null && mDataArrays.size() > 0) {
						ChatMsgEntity msgEntity = new ChatMsgEntity();
						msgEntity.setContent(lastContent);
						msgEntity.setTime(lastTime);
						msgEntity.setFullTime(MessageInfo.getMessageFullTime());

						msgEntity.setName(names);
						msgEntity.setIsComing(false);
						msgEntity.setSenderId(UserInfo.db_id);
						String id = userId;
						if (newUserId != null)
							id = newUserId;
						msgEntity.setReceiverId(id);

						msgEntity.setIsAdd(false);
						msgEntity.setStatus(MessageInfo.SEND_MESSAGE);
						MessageInfo.messageEntityList.add(msgEntity);
					}
					intent_1.putExtra("ids", idStr);
					startActivity(intent_1);
					overridePendingTransition(R.anim.in_from_right,
							R.anim.out_of_left);
					break;
				default:
					break;
				}
			}
		});

		lstMenuItem.add(new gridItemInfo(getResources().getString(
				R.string.take_photo), R.drawable.ico_photo));
		lstMenuItem.add(new gridItemInfo(getResources().getString(
				R.string.photo), R.drawable.ico_take_photo));

		lstMenuItem.add(new gridItemInfo(getResources().getString(
				R.string.location), R.drawable.ico_location));
		/*
		 * lstMenuItem.add(new gridItemInfo(getResources().getString(
		 * R.string.video), R.drawable.ico_video));
		 */
		lstMenuItem.add(new gridItemInfo(getResources()
				.getString(R.string.task), R.drawable.ico_task));
		lstMenuItem.add(new gridItemInfo(getResources().getString(
				R.string.meeting), R.drawable.ico_meeting));

		lstMenuItem.add(new gridItemInfo(getResources().getString(
				R.string.white_board), R.drawable.ico_whiteboard));
		menuGridView.setAdapter(new gridViewAdapter(this, lstMenuItem));
		menuGridView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						switch (arg2) {
						case 0:
							Intent intent0 = new Intent(
									"android.media.action.IMAGE_CAPTURE");
							startActivityForResult(intent0,
									Activity.DEFAULT_KEYS_DIALER);
							break;
						case 1:
							Intent intent1 = new Intent(Intent.ACTION_PICK
							/*
							 * android.provider.MediaStore.Images.Media.
							 * EXTERNAL_CONTENT_URI
							 */);
							intent1.setType("image/*");
							startActivityForResult(intent1, RESULT_LOAD_IMAGE);
							break;

						case 2:
							Intent intent2 = new Intent(ChatActivity.this,
									LocationMessage.class);
							startActivityForResult(intent2, RESULT_LOCATION);
							break;
						case 3:
							Intent intent4 = new Intent(ChatActivity.this,
									ScheduleAddActivity.class);
							Bundle bundle = new Bundle();
							String[] idStr = getScheduleMember(userId, true);
							String[] nameStr = getScheduleMember(names, false);
							bundle.putStringArray("nameStr", nameStr);
							bundle.putStringArray("idStr", idStr);
							intent4.putExtra("type", 0);
							intent4.putExtra("isFromChat", true);
							intent4.putExtra("currentDate",
									MessageInfo.getChatDate());
							intent4.putExtras(bundle);
							startActivityForResult(intent4, RESULT_SCHEDULE);
							overridePendingTransition(R.anim.in_from_down,
									R.anim.out_of_up);
							break;
						case 4:
							Intent intent5 = new Intent(ChatActivity.this,
									ScheduleAddActivity.class);
							Bundle bundle2 = new Bundle();
							String[] idStr2 = getScheduleMember(userId, true);
							String[] nameStr2 = getScheduleMember(names, false);
							bundle2.putStringArray("nameStr", nameStr2);
							bundle2.putStringArray("idStr", idStr2);
							intent5.putExtra("isFromChat", true);
							intent5.putExtras(bundle2);
							intent5.putExtra("type", 1);
							intent5.putExtra("currentDate",
									MessageInfo.getChatDate());
							startActivityForResult(intent5, RESULT_SCHEDULE);
							overridePendingTransition(R.anim.in_from_down,
									R.anim.out_of_up);
							break;
						case 5:
							Intent intentWhiteBoard = new Intent(
									ChatActivity.this, WhiteBoardActivity.class);
							startActivityForResult(intentWhiteBoard,
									RESULT_SHOW_IMAGE);
							break;
						default:
							break;
						}
					}
				});

		pressTalkBtn.setOnTouchListener(new View.OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();
				switch (action) {
				case MotionEvent.ACTION_UP:
					if (tempFile == null)
						return true;
					upTime = System.currentTimeMillis();
					if (mRecorder != null) {
						mRecorder.stop();
						mRecorder.release();
						mRecorder = null;
					}
					if ((upTime - downTime) < HIGHLIMITTIME
							&& (upTime - downTime) > LOWLIMITTIME) {
						tempFile.delete();
						Toast toast = Toast.makeText(ChatActivity.this,
								"录音时间太短", Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.BOTTOM, 0, 150);
						toast.show();

					} else if ((upTime - downTime) <= LOWLIMITTIME) {
						tempFile.delete();
					} else {

						String msguuid = StringWidthWeightRandom
								.getNextString();
						createLocalmsgEntity(MessageInfo.VOICE,
								tempFile.getAbsolutePath(), (upTime - downTime)
										/ 1000 + "''", userId, msguuid, null,
								null, null, null, 0, null, fromname);

						int chatType = 0;
						String to;
						if (groupId != null && groupId.length() > 0) {
							to = groupId;
							chatType = MessageInfo.GROUP;
						} else {
							to = userId;
							chatType = MessageInfo.INDIVIDUAL;
						}

						uploadFileInstance.uploadFile(
								tempFile.getAbsolutePath(), to/* userId */,
								MessageInfo.VOICE, msguuid, chatType);
					}
					if (menuWindow != null)
						menuWindow.dismiss();
					break;
				case MotionEvent.ACTION_DOWN:
					if (checkSDCard()) {
						mRecAudioPath = new File(Environment
								.getExternalStorageDirectory().getPath()
								+ File.separator + STORE_RECORDS_PATH);
						mRecAudioPath.mkdirs();
					} else {
						Toast toast = Toast.makeText(ChatActivity.this,
								getString(R.string.sdcard_error),
								Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.BOTTOM, 0, 150);
						toast.show();
						break;
					}
					downTime = System.currentTimeMillis();

					View view = LayoutInflater.from(ChatActivity.this).inflate(
							R.layout.audio_recorder_ring, null);

					menuWindow = new PopupWindow(view, 200, 200);
					view.findViewById(R.id.recorder_ring).setVisibility(
							View.VISIBLE);
					// talkLabel = (TextView)
					// view.findViewById(R.id.talk_label);

					/**************************************/
					timer = (Chronometer) view.findViewById(R.id.chronometer);
					/**************************************/

					view.setBackgroundResource(R.drawable.pls_talk);

					menuWindow
							.showAtLocation(mListView, Gravity.CENTER_VERTICAL
									| Gravity.CENTER_HORIZONTAL, 0, 0);
					try {
						if (mRecAudioPath.isDirectory()) {
							mRecAudioPath.mkdir();
						}
						tempFile = File.createTempFile("tmp_record", ".aac",
								mRecAudioPath);
					} catch (IOException e) {

					}
					mRecorder = new MediaRecorder();
					mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
					mRecorder
							.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB/* THREE_GPP */);
					mRecorder
							.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
					mRecorder.setOutputFile(tempFile.getAbsolutePath());

					try {
						mRecorder.prepare();
						mRecorder.start();
						new Thread(mUpdateMicStatusTimer).start();
					} catch (IllegalStateException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					break;
				}
				return true;
			}
		});

		mAdapter = new ChatMsgViewAdapter(this, mDataArrays);
		mListView.setAdapter(mAdapter);
	}

	private String[] getScheduleMember(String userStr, boolean isUserId) {
		String[] str = userStr.split("、");
		String[] result = new String[str.length + 1];
		if (str != null) {
			if (isUserId) {
				result[0] = UserInfo.db_id;
			} else {
				result[0] = getResources().getString(R.string.me);
			}
			for (int i = 0; i < str.length; i++) {
				result[i + 1] = str[i];
			}
			return result;
		} else
			return null;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		Log.e("test", "chat onactivityresult.............");

		if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK
				&& null != data) {

			Uri selectedImage = data.getData();
			if (selectedImage == null) {
				Bundle bundle = data.getExtras();
				if (bundle != null) {
					Bitmap photo = (Bitmap) bundle.get("data");
					String picPath = BitmapCompressUtil.saveToLocal(photo);
					Intent intent = new Intent(this, ImageResultActivity.class);
					intent.putExtra("path", picPath);
					startActivityForResult(intent, RESULT_SHOW_IMAGE);
				} else
					return;
			} else {
				if (!TextUtils.isEmpty(selectedImage.getAuthority())) {
					String[] filePathColumn = { MediaStore.Images.Media.DATA };
					Cursor cursor = getContentResolver().query(selectedImage,
							filePathColumn, null, null, null);
					if (cursor == null) {
						Toast.makeText(getApplicationContext(), "找不到图片",
								Toast.LENGTH_SHORT).show();
						return;
					}
					cursor.moveToFirst();
					int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
					String picturePath = cursor.getString(columnIndex);
					cursor.close();

					Intent intent = new Intent(this, ImageResultActivity.class);
					intent.putExtra("path", picturePath);
					startActivityForResult(intent, RESULT_SHOW_IMAGE);
				} else {
					Log.i("test", "path=" + selectedImage.getPath());
					Intent intent = new Intent(this, ImageResultActivity.class/*
																			 * CropImageActivity
																			 * .
																			 * class
																			 */);
					intent.putExtra("path", selectedImage.getPath());
					startActivityForResult(intent, RESULT_SHOW_IMAGE);
				}
			}
		} else if (requestCode == RESULT_SHOW_IMAGE && resultCode == RESULT_OK
				&& null != data) {
			String smallPath = data.getStringExtra("smallImgPath");
			String bigPath = data.getStringExtra("bigImgPath");

			if (smallPath == null || bigPath == null) {
				Toast.makeText(getApplication(),
						getApplication().getString(R.string.sdcard_error),
						Toast.LENGTH_SHORT).show();
				return;
			}
			String msguuid = StringWidthWeightRandom.getNextString();
			createLocalmsgEntity(MessageInfo.PICTURE, smallPath, null, userId,
					msguuid, bigPath, null, null, null, 0, null, fromname);
			Log.i("test", "userId 2 :" + userId);

			int chatType = 0;
			String to;
			if (groupId != null && groupId.length() > 0) {
				to = groupId;
				chatType = MessageInfo.GROUP;
			} else {
				to = userId;
				chatType = MessageInfo.INDIVIDUAL;
			}
			uploadFileInstance.uploadFile(bigPath, to/* userId */,
					MessageInfo.PICTURE, msguuid, chatType);
		} else if (requestCode == RESULT_SCHEDULE) {
			if (data == null)
				return;
			ChatMsgEntity entity = (ChatMsgEntity) data
					.getSerializableExtra("ScheduleToChatMessage");
			if (entity == null)
				return;
			String receiverStr = "";
			if (groupId != null)
				receiverStr = groupId;
			else
				receiverStr = userId;

			createLocalmsgEntity(entity.getType(), entity.getBeginDate(),
					entity.getEndDate(), receiverStr, entity.getMsguuid(),
					entity.getScheduleTitle(), entity.getScheduleBeginTime(),
					entity.getScheduleEndTime(), entity.getScheduleAddress(),
					entity.getScheduleType(), null, fromname);
		} else if (requestCode == RESULT_MODIFY_SCHEDULE) {
			if (data == null)
				return;

			boolean isDel = data.getBooleanExtra("isDel", false);
			if (isDel) {
				String itemId = data.getStringExtra("itemId");
				Log.e("test", "itemid.............: " + itemId);

				for (int i = 0; i < mDataArrays.size(); i++) {
					if (mDataArrays.get(i).getMsguuid().equals(itemId)) {
						mDataArrays.remove(i);
						mAdapter.notifyDataSetChanged();
						ChatMsgEntity entity = (ChatMsgEntity) data
								.getSerializableExtra("schedulemessage");
						String receiver = null;
						if (groupId != null)
							receiver = groupId;
						else
							receiver = userId;

						Log.e("test", "receiver ::" + receiver);

						createLocalmsgEntity(
								entity.getType(),
								entity.getBeginDate(),
								entity.getEndDate(),
								receiver,
								StringWidthWeightRandom.getNextString(),// itemId,
								entity.getScheduleTitle(),
								entity.getScheduleBeginTime(),
								entity.getScheduleEndTime(),
								entity.getScheduleAddress(),
								entity.getScheduleType(),
								entity.getScheduleTaskId(), fromname);
						break;
					}
				}
			}
		} else if (requestCode == RESULT_TASK_MEMBER) {
			if (data == null)
				return;
			String title = data.getStringExtra("titleNames");
			String titleIds = data.getStringExtra("titleIds");
			names = title;
			Log.e("test", "names:" + title);
			Log.e("test", "ids :" + titleIds);
			chatName.setText(names);

			 {
				if (chatSharedPre == null)
					chatSharedPre = new SharedPreferencesUtil(this);
				if (chatSharedPre.readDataFromShared(userId + "_"
						+ UserInfo.db_id) != null) {

					List<ChatMsgEntity> tempLst = new ArrayList<ChatMsgEntity>();
					tempLst = chatSharedPre.readDataFromShared(userId + "_"
							+ UserInfo.db_id);
					chatSharedPre.saveDatatoShared(titleIds + "_"
							+ UserInfo.db_id, tempLst);
					tempList.clear();
					chatSharedPre.saveDatatoShared(userId + "_"
							+ UserInfo.db_id, tempList);
				}
				if (chatSharedPre.getGroupInfo(userId + "_" + UserInfo.db_id) != null) {
					String groupID = chatSharedPre.getGroupInfo(userId + "_"
							+ UserInfo.db_id);
					chatSharedPre.saveGroupInfo(userId + "_" + UserInfo.db_id,
							"");
					chatSharedPre.saveGroupInfo(
							titleIds + "_" + UserInfo.db_id, groupID);
				}
				if (chatSharedPre.getChatDate(userId + "_" + UserInfo.db_id) != null) {
					String date = chatSharedPre.getChatDate(userId + "_"
							+ UserInfo.db_id);
					chatSharedPre.saveChatDate(titleIds + "_" + UserInfo.db_id,
							date);
					chatSharedPre.saveChatDate(userId + "_" + UserInfo.db_id,
							"");
				}
				newUserId = titleIds;
				Log.e("test", "new user id :" + newUserId);
				chatName.setText(names);
			}
		} else if (requestCode == RESULT_LOCATION && resultCode == RESULT_OK) {
			if (data == null)
				return;
			String location = data.getStringExtra("location");
			double longitude = data.getDoubleExtra("longitude", 116.484442);
			double latitude = data.getDoubleExtra("latitude", 39.917007);

			String receiver = null;
			if (groupId != null)
				receiver = groupId;
			else
				receiver = userId;

			createLocalmsgEntity(MessageInfo.LOCATION, location, null,
					receiver, StringWidthWeightRandom.getNextString(), null, ""
							+ longitude, "" + latitude, null, 0, null, fromname);
		}
	}

	private final Handler mHandler = new Handler();
	private Runnable mUpdateMicStatusTimer = new Runnable() {
		public void run() {
			updateMicStatus();
		}
	};

	private void updateMicStatus() {
		if (mRecorder != null) {
			// int vuSize = 10 * mRecorder.getMaxAmplitude() / 32768;
			// talkLabel.setText(vuSize + "");
			timer.setBase(SystemClock.elapsedRealtime());
			timer.start();
			// mHandler.postDelayed(mUpdateMicStatusTimer, 300);
		}
	}

	public boolean checkSDCard() {
		return Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState());
	}

	@SuppressLint("NewApi")
	public void updateChatStatus(ChatMsgEntity entity) {
		Log.e("test", "updatechatstatus............");
		if (entity == null)
			return;

		Log.e("test", "taskid ::" + entity.getScheduleTaskId());
		int status = entity.getStatus();
		if (status == MessageInfo.RECEIVE_MESSAGE) {
			String senderId = entity.getSenderId();
			DBOpenHelper dbOpenHelper = new DBOpenHelper(this);
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

			String tempReceiver = UserInfo.db_id;
			String tempSender = userId;
			int tempType = 1;
			if (groupId != null && groupId.length() > 0) {
				tempReceiver = UserInfo.db_id;
				tempSender = entity.getSenderId();
				tempType = 2;
			}
			if (client == null)
				client = new ClientSocket(this);
			client.sendMessage(null, 8, entity.getMsguuid(), tempSender,
					tempReceiver, null, null, null, null,
					String.valueOf(tempType), null, false, fromname);

			if (entity.getType() == MessageInfo.SCHEDULE) {
				if (entity.getScheduleTaskId() != null
						&& entity.getScheduleTaskId().length() > 0) {
					Log.e("test",
							"entity.gettaskid :" + entity.getScheduleTaskId());
					for (int j = 0; j < mDataArrays.size(); j++) {
						Log.e("test", "mdataarrays :"
								+ mDataArrays.get(j).getScheduleTaskId());
						if (mDataArrays.get(j).getScheduleTaskId() != null
								&& mDataArrays.get(j).getScheduleTaskId()
										.equals(entity.getScheduleTaskId())) {
							Log.e("test", "***********************************");
							mDataArrays.get(j)
									.setCheckStatus(
											getResources().getString(
													R.string.canceled));
							mDataArrays.get(j).setCheckBackground(
									R.drawable.bt_confirm_pressed);

							int k = 0;
							for (k = 0; k < MessageInfo.scheduleList.size(); k++) {
								if (MessageInfo.scheduleList.get(k).getTaskId()
										.equals(entity.getScheduleTaskId())) {
									MessageInfo.scheduleList.remove(k);
									break;
								}
							}
							if (k == MessageInfo.scheduleList.size())
								MessageInfo.scheduleList
										.add(new ScheduleItemInfo(
												UserInfo.realName, null, null,
												null, null, null,
												UserInfo.db_id, 0, null, true,
												null, entity
														.getScheduleTaskId(),
												groupId));
						}
					}
				}
				Log.e("test", "entity.msguuid  ::::" + entity.getMsguuid());
				// msguidMap.put(entity.getMsguuid(), mAdapter.getCount() - 1);
				entity.setCheckStatus(getResources().getString(
						R.string.click_check));
				entity.setCheckBackground(R.drawable.bt_confirm_normal);

				/*******************************************/
				Log.e("test", "**********************************************");
				Log.e("test", "groupnames :" + names);
				Log.e("test", "ids :" + userId);
				entity.setGroupNames(names);
				entity.setGroupIds(userId);
			}

			updateChatView(entity, null, false);

			return;
		} else {
			if (status == MessageInfo.GROUP_MODIFY
					&& MessageFragment.instance == null) {
				Log.e("test", "group modified...........");
				MessageInfo.messageEntityList.add(entity);
			}

			if (entity.getMsguuid() == null
					|| !msguidMap.containsKey(entity.getMsguuid()))
				return;
			if (msguidMap == null)
				Log.e("test", "msguidMap is null");

			int listId = msguidMap.get(entity.getMsguuid());
			Log.i("test", "memberCount is " + memberCount);
			Log.i("test", "listId :" + msguidMap.get(entity.getMsguuid()));

			if (status == MessageInfo.SEND_ARRIVAL) {
				Log.i("test", "listId :" + listId);
				Log.i("test", "TYPE :" + mDataArrays.get(listId).getType());
				mDataArrays.get(listId).setAnimVisibile(View.GONE);
				mDataArrays.get(listId).setStateVisible(View.VISIBLE);
				mDataArrays.get(listId).setChatState(
						getResources().getString(R.string.sent));

				/**********************************************************/
				if (entity.getScheduleTaskId() != null
						&& !entity.getScheduleTaskId().isEmpty()) {
					String[] tempIds = null;
					String[] tempNames = null;
					if (groupId == null) {
						tempIds = new String[2];
						tempNames = new String[2];
						tempIds[0] = UserInfo.db_id;
						tempIds[1] = userId;
						tempNames[0] = UserInfo.realName;
						tempNames[1] = names;
					} else {
						tempIds = userId.split("、");
						tempNames = names.split("、");
					}

					mDataArrays.get(listId).setScheduleTaskId(
							entity.getScheduleTaskId());

					MessageInfo.scheduleList.add(new ScheduleItemInfo(
							UserInfo.realName, mDataArrays.get(listId)
									.getScheduleTitle(), mDataArrays
									.get(listId).getScheduleBeginTime(),
							mDataArrays.get(listId).getScheduleEndTime(),
							tempNames, tempIds, UserInfo.db_id, mDataArrays
									.get(listId).getScheduleType(), entity
									.getMsguuid(), true/* isDel */,
							mDataArrays.get(listId).getScheduleAddress(),
							entity.getScheduleTaskId(), groupId));
				}

			} else if (status == MessageInfo.SEND_READED
					&& mDataArrays.get(listId) != null) {
				mDataArrays.get(listId).setBackground(R.drawable.ima_readed);
				mDataArrays.get(listId).setChatState(
						getResources().getString(R.string.readed));
			} else if (status == MessageInfo.SCHEDULE_CHECK
					&& mDataArrays.get(listId) != null) {
				mDataArrays.get(listId).setCheckStatus(
						getResources().getString(R.string.checked));
				mDataArrays.get(listId).setCheckBackground(
						R.drawable.bt_confirm_pressed);
			}
		}
		mAdapter.notifyDataSetChanged();
	}

	public void updateChatView(ChatMsgEntity entity, String msguuid,
			boolean isSend) {
		if (entity == null)
			return;
		lastContent = entity.getContent();
		lastTime = entity.getTime();
		lastFullTime = entity.getFullTime();

		Log.i("test", "content :" + entity.getContent());
		Log.i("test", "iscoming :" + entity.getIsComing());
		Log.i("test", "senderid :" + entity.getSenderId());
		Log.i("test", "receiverId : " + entity.getReceiverId());
		Log.i("test", "title :" + entity.getScheduleTitle());
		Log.i("test", "btime :" + entity.getScheduleBeginTime());
		Log.i("test", "etime :" + entity.getScheduleEndTime());
		Log.i("test", "type :" + entity.getType());
		Log.i("test", "scheduleType :" + entity.getScheduleType());
		Log.i("test", "checkstatus :" + entity.getCheckStatus());

		entity.setChatName(names);

		mDataArrays.add(entity);
		mAdapter.notifyDataSetChanged();
		if (isSend) {
			msguidMap.put(msguuid, mAdapter.getCount() - 1);
		}
		// for set check button unuseable
		if (entity.getType() == MessageInfo.SCHEDULE) {
			Log.e("test", "entity.msguuid  ::::" + entity.getMsguuid());
			msguidMap.put(entity.getMsguuid(), mAdapter.getCount() - 1);
		}

		mEditTextContent.setText("");
		mListView.setSelection(mListView.getCount() - 1);
	}

	public void openTaskMember(View view) {
		Intent intent = new Intent(this, TaskMemberActivity.class);
		intent.putExtra("groupId", groupId);
		String id = userId;
		if (newUserId != null)
			id = newUserId;
		intent.putExtra("ids", id);
		intent.putExtra("names", names);
		intent.putExtra("listId", listId);
		startActivityForResult(intent, RESULT_TASK_MEMBER);
		overridePendingTransition(R.anim.in_from_right, R.anim.out_of_left);
	}

	private void createLocalmsgEntity(int type, String content,
			String voiceSec, String receiverId, String msguuid, String title,
			String beginTime, String endTime, String address, int scheduleType,
			String taskId, String fromname) {

		ChatMsgEntity entity = new ChatMsgEntity();
		entity.setTime(MessageInfo.getChatTime());
		entity.setFullTime(MessageInfo.getMessageFullTime());
		entity.setType(type);
		entity.setIsComing(false);
		entity.setBackground(R.drawable.ima_sent);
		entity.setReceiverId(userId);
		entity.setChatState(getResources().getString(R.string.waiting));
		entity.setMsguuid(msguuid);
		entity.setAnimVisibile(View.VISIBLE);
		entity.setStateVisible(View.GONE);
		entity.setFromname(fromname);
		Log.e("test", "key  :" + (userId + "_" + UserInfo.db_id));

		if (prevDate == null) {
			if (chatSharedPre.getChatDate(userId + "_" + UserInfo.db_id) != null
					&& chatSharedPre.getChatDate(userId + "_" + UserInfo.db_id)
							.length() > 0) {
				Log.e("test",
						"prevdate :::::::"
								+ chatSharedPre.getChatDate(userId + "_"
										+ UserInfo.db_id));
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

		if (type == MessageInfo.TEXT) {
			entity.setContent(content);
			// if (UserInfo.clientsocket != null)
			if (client == null)
				client = new ClientSocket(this);
			if (Type == 4)
				try {
					MenuType = 2;
					menuHttp.sendMessageBefore(userId, content, ResponseHandler);
					client.sendMessage(content, 1, msguuid, UserInfo.db_id,
							userId, null, null, null, null, "1", null, false,
							fromname);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			else {
				if (groupId == null)
					client.sendMessage(content, 1, msguuid, UserInfo.db_id,
							userId, null, null, null, null, "1", null, false,
							fromname);
				else
					client.sendMessage(content, 1, msguuid, UserInfo.db_id,
							groupId, null, null, null, null, "2", null, false,
							fromname);
			}
		} else if (type == MessageInfo.PICTURE) {
			entity.setSmallPicUrl(content);
			entity.setBigPicUrl(title);
			entity.setContent(getResources().getString(R.string.picture));
			entity.setReceiverId(receiverId);
		} else if (type == MessageInfo.VOICE) {
			entity.setSecond(voiceSec);
			entity.setVoiceUrl(content);
			entity.setContent(getResources().getString(R.string.voice));
			entity.setReceiverId(receiverId);
		} else if (type == MessageInfo.LOCATION) {
			entity.setlongitude(beginTime);
			entity.setlatitude(endTime);
			entity.setContent(content);
			entity.setReceiverId(receiverId);
			if (groupId == null)
				client.sendMessage(content, 22, msguuid, UserInfo.db_id,
						userId, "" + beginTime, "" + endTime, null, null, "1",
						null, false, fromname);
			else
				client.sendMessage(content, 22, msguuid, UserInfo.db_id,
						groupId, "" + beginTime, "" + endTime, null, null, "2",
						null, false, fromname);

		} else {
			/****************************************/
			entity.setGroupIds(userId);
			entity.setGroupNames(names);

			/****************************************/
			entity.setBeginDate(content);
			entity.setEndDate(voiceSec);

			entity.setScheduleTitle(title);
			entity.setScheduleBeginTime(beginTime);
			entity.setScheduleEndTime(endTime);
			entity.setScheduleAddress(address);
			entity.setScheduleType(scheduleType);

			/******************************************/
			entity.setScheduleItemId(msguuid);

			Log.e("test", "itemid :::::::" + entity.getScheduleItemId());

			if (scheduleType == MessageInfo.TASK) {
				entity.setContent(getResources().getString(
						R.string.message_item_task));
			} else if (scheduleType == MessageInfo.MEETING) {
				entity.setContent(getResources().getString(
						R.string.message_item_meeting));
			} else {
				entity.setContent(getResources().getString(
						R.string.message_item_other));
			}

			String[] str = receiverId.split("、");
			if (str.length == 1 && str[0].equals(UserInfo.db_id)) {
				entity.setAnimVisibile(View.GONE);
				entity.setStateVisible(View.VISIBLE);
				entity.setBackground(R.drawable.ima_readed);
				entity.setChatState(getResources().getString(R.string.readed));
			} else {
				String scheType = "";
				String msgType = "";// individual or group
				if (scheduleType == MessageInfo.TASK) {
					scheType = "2";
				} else if (scheduleType == MessageInfo.MEETING) {
					scheType = "1";
				} else {
					scheType = "3";
				}
				if (groupId != null) {
					msgType = "2";
				} else {
					msgType = "1";
				}
				if (client == null)
					client = new ClientSocket(this);

				client.sendMessage(title, 13, msguuid, UserInfo.db_id,
						receiverId, MessageInfo.StringToLong(beginTime),
						MessageInfo.StringToLong(endTime), address, scheType,
						msgType, taskId, false, fromname);
			}
		}
		updateChatView(entity, msguuid, true);
	}

	@Override
	public void onBackPressed() {
		chatBack(null);
	}

	public void chatBack(View view) {
		Log.e("test", "chatback...........");
		Intent data = new Intent();
		data.putExtra("content", lastContent);
		data.putExtra("time", lastTime);
		data.putExtra("userId", userId);
		data.putExtra("newUserId", newUserId);
		data.putExtra("fullTime", lastFullTime);
		data.putExtra("groupId", groupId);
		data.putExtra("names", chatName.getText().toString());
		setResult(listId, data);
		// chatSharedPre.saveDatatoShared(userId, mDataArrays);

		Log.e("test", "isFromselectMember :" + isFromSelectMember);
		Log.e("test", "groupId :" + groupId);
		if ((isFromSelectMember && lastTime != null && lastContent != null)
				|| (isFromSelectMember && groupId != null)) {
			ChatMsgEntity msgEntity = new ChatMsgEntity();
			msgEntity.setContent(lastContent);
			msgEntity.setTime(lastTime);
			msgEntity.setFullTime(MessageInfo.getMessageFullTime());
			Log.i("test", "format time :" + data.getExtras().getString("time"));

			msgEntity.setName(names);
			msgEntity.setIsComing(false);
			if (groupId != null && groupId.length() > 0) {
				msgEntity.setChatType(MessageInfo.GROUP);
				msgEntity.setReceiverId(groupId);
				msgEntity.setUserId(userId);
			} else {
				msgEntity.setSenderId(UserInfo.db_id);
				String id = userId;
				if (newUserId != null)
					id = newUserId;
				msgEntity.setReceiverId(id);
			}

			msgEntity.setIsAdd(false);
			msgEntity.setStatus(MessageInfo.SEND_MESSAGE);
			MessageInfo.messageEntityList.add(msgEntity);
		}

		finish();
		overridePendingTransition(R.anim.in_from_left, R.anim.out_of_right);
	}

	public void updateChatListView() {
		if (MessageInfo.messageEntityList != null
				&& MessageInfo.messageEntityList.size() > 0) {
			for (int i = 0; i < MessageInfo.messageEntityList.size(); i++) {
				ChatMsgEntity entity = MessageInfo.messageEntityList.get(i);
				if ((entity.getSenderId() != null && entity.getSenderId()
						.equals(userId))
						|| (entity.getReceiverId() != null && entity
								.getReceiverId().equals(userId))
						|| (groupId != null && groupId.equals(entity
								.getReceiverId()))
						|| (groupId != null && groupId.equals(entity
								.getSenderId()))) {
					updateChatStatus(entity);
					MessageInfo.messageEntityList.remove(i);
				}
			}
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		chatSharedPre.getUserInfo();
		if (UserInfo.isHomePressed) {
			Log.e("test", "chatactivity recreate connection...................");
			if (client == null)
				client = new ClientSocket(this);
			UserInfo.isSendBroadCast = false;
			client.sendMessage(null, 0,
					StringWidthWeightRandom.getNextString(), null, null, null,
					null, null, null, null, null, true, fromname);
		}
		// updateChatListView();

		// register broadcast
		/*
		 * IntentFilter myIntentFilter = new IntentFilter();
		 * myIntentFilter.addAction(MessageInfo.MessageBroadCastName); instance
		 * = this; cbr = new ChatBroadcastReceiver(); registerReceiver(cbr,
		 * myIntentFilter);
		 */Log.i("test", "chatactivity onStart");
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.i("test", "chatactivity onResume");
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.i("test", "chatactivity onPause");
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.i("test", "chatactivity onStop");
		String id = userId;
		if (newUserId != null)
			id = newUserId;

		String getMessageKey = "";

		if (fromname.equals("")) {
			getMessageKey = id + "_" + UserInfo.db_id;
		} else {
			getMessageKey = id + "_" + UserInfo.db_id + "_" + "f";
		}
		Log.e("test", "key is  :" + id + "_" + UserInfo.db_id);
		Log.e("test", "groupid :" + groupId);

		chatSharedPre.saveGroupInfo(getMessageKey, groupId);
		chatSharedPre.saveDatatoShared(getMessageKey, mDataArrays);
		if (prevDate == null)
			prevDate = MessageInfo.getChattingDate();

		chatSharedPre.saveChatDate(getMessageKey, prevDate);
		fromname = "";
	}

	@Override
	protected void onDestroy() {
		Log.i("test", "chatactivity onDestroy");
		super.onDestroy();

		mDataArrays.clear();
		groupId = null;
		unregisterReceiver(cbr);
		instance = null;// set for quit
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_send:
			String messageContent = mEditTextContent.getText().toString();
			if (messageContent.length() > 0) {
				createLocalmsgEntity(MessageInfo.TEXT, messageContent, null,
						null, StringWidthWeightRandom.getNextString(), null,
						null, null, null, 0, null, fromname);
			}
			break;
		case R.id.chatting_voice_btn:
			setGridViewVisibility(false);
			voiceBtn.setVisibility(View.GONE);
			keyboardBtn.setVisibility(View.VISIBLE);
			ll_fasong.setVisibility(View.GONE);
			ll_yuyin.setVisibility(View.VISIBLE);
			hideSoftInput(v.getWindowToken());
			break;
		case R.id.chatting_keyboard_btn:
			setGridViewVisibility(false);
			voiceBtn.setVisibility(View.VISIBLE);
			keyboardBtn.setVisibility(View.GONE);
			ll_fasong.setVisibility(View.VISIBLE);
			ll_yuyin.setVisibility(View.GONE);

			mEditTextContent.requestFocus();
			((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
					.showSoftInput(mEditTextContent, 0);

			break;
		case R.id.chatting_plus_btn:
			hideSoftInput(getCurrentFocus().getWindowToken());
			if (!isGridShow) {
				setGridViewVisibility(true);
			} else {
				setGridViewVisibility(false);
			}
			break;
		case R.id.ib_open_menu:
			if (!isTopGridShow) {
				setTopGridViewVisibility(true);
				menuBtn.setImageDrawable(getResources().getDrawable(
						R.drawable.ico_close));
			} else {
				setTopGridViewVisibility(false);
				menuBtn.setImageDrawable(getResources().getDrawable(
						R.drawable.ico_open));
			}
			break;
		}
	}

	public FriendEntity getEntityFromUserID(String userId) {
		FriendEntity entity = new FriendEntity();
		DBOpenHelper dbOpenHelper = new DBOpenHelper(this);
		Cursor cursor = dbOpenHelper.get(userId);

		if (cursor.moveToFirst()) {
			cursor.moveToFirst();
			String name = cursor.getString(2);
			String duty = cursor.getString(6);
			String signature = cursor.getString(9);
			String department = cursor.getString(7);
			String team = cursor.getString(12);
			String sex = cursor.getString(13);
			String mobile = cursor.getString(4);
			String headUrl = cursor.getString(8);

			entity.setDbId(userId);
			entity.setRealName(name);
			entity.setDepartmentName(department);
			entity.setDuty(duty);
			entity.setSignature(signature);
			entity.setTeam(team);
			entity.setSex(sex);
			entity.setMobile(mobile);
			entity.setHeadUrl(headUrl);
		}
		dbOpenHelper.close();
		cursor.close();
		return entity;
	}

	public void setTopGridViewVisibility(boolean isVisible) {
		if (isVisible == true) {
			topGridView.setVisibility(View.VISIBLE);
			isTopGridShow = true;
		} else {
			topGridView.setVisibility(View.GONE);
			isTopGridShow = false;
		}
	}

	public void setGridViewVisibility(boolean isVisible) {
		if (isVisible == true) {
			menuGridView.setVisibility(View.VISIBLE);
			isGridShow = true;
		} else {
			menuGridView.setVisibility(View.GONE);
			isGridShow = false;
		}
	}

	public void hideSoftInput(IBinder token) {
		if (token != null) {
			InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			im.hideSoftInputFromWindow(token,
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		onClickMenu(arg2);
		mPopupWindow.dismiss();
		mPopupWindow = null;

	}
}
