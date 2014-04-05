package com.bestapp.yikuair.fragments;

import java.io.File;
import java.io.IOException;
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
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.bestapp.yikuair.R;
import com.bestapp.yikuair.database.DBOpenHelper;
import com.bestapp.yikuair.utils.FriendEntity;
import com.bestapp.yikuair.utils.MessageInfo;
import com.bestapp.yikuair.utils.PullToRefreshListView;
import com.bestapp.yikuair.utils.SharedPreferencesUtil;
import com.bestapp.yikuair.utils.StringWidthWeightRandom;
import com.bestapp.yikuair.utils.UploadFileUtil;
import com.bestapp.yikuair.utils.UserInfo;
import com.bestapp.yikuair.utils.PullToRefreshBase.OnRefreshListener;

public class FeedBackFragment extends Fragment implements OnClickListener {

	private Button mBtnSend;
	private Button pressTalkBtn;
	private EditText mEditTextContent;
	private ListView mListView;
	private ChatMsgViewAdapter mAdapter;
	private GridView menuGridView;
	private ImageButton voiceBtn;
	private ImageButton keyboardBtn;
	private GridView topGridView;
	private ImageButton plusBtn;
	private ImageButton menuBtn;
	private boolean isGridShow = false;
	private boolean isTopGridShow = false;
	private RelativeLayout ll_fasong;
	private RelativeLayout ll_yuyin;
	private List<ChatMsgEntity> chatMsgList = new ArrayList<ChatMsgEntity>();
	public static LinkedList<ChatMsgEntity> mDataArrays = new LinkedList<ChatMsgEntity>();
	private List<ChatMsgEntity> lstMessage = new ArrayList<ChatMsgEntity>();
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
	private TextView talkLabel;
	private FriendEntity friendEntity;

	private static int RESULT_LOAD_IMAGE = 1;
	private static int RESULT_SHOW_IMAGE = 2;
	private static int SHOW_MAX_COUNT = 5;
	public static final int RESULT_OK = -1;
	private int itemCount;
	private String userId;
	private SharedPreferencesUtil chatSharedPre;
	private UploadFileUtil uploadFileInstance;
	private PullToRefreshListView mPullRefreshListView;
	private ImageButton leftBtn;
	private String prevDate;
	public static FeedBackFragment instance = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		instance = this;
		return inflater.inflate(R.layout.user_feedback_chat_message, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		getActivity().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		if (UserInfo.feedback_dbId == null
				|| UserInfo.feedback_dbId.length() == 0) {
			String dbid = "";
			DBOpenHelper dbOpenHelper = new DBOpenHelper(getActivity());
			SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
			Cursor cursor = db.rawQuery(
					"select * from contactsTable where name=?",
					new String[] { getActivity().getResources().getString(
							R.string.user_feedback) });
			if (cursor.moveToFirst()) {
				dbid = cursor.getString(14);
			}
			UserInfo.feedback_dbId = dbid;
			userId = dbid;
			cursor.close();
			dbOpenHelper.close();
		} else
			userId = UserInfo.feedback_dbId;

		Log.e("test", "fedback dbid :" + userId);

		initChatView();
		updateChatListView();
	}

	public class ChatBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i("test", "feedback broadcast receive...................");
			ChatMsgEntity entity = new ChatMsgEntity();
			Bundle bundle = intent.getExtras();
			Serializable data = bundle.getSerializable("message");
			if (data != null) {
				entity = (ChatMsgEntity) data;
				if (entity.getSenderId().equals(userId)
						|| entity.getReceiverId().equals(userId)) {
					Log.i("test", "feedback new ...........");
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
					lstMessage.get(j).setSenderId(userId);
					updateChatView(lstMessage.get(j), null, false);
				}
			}
		}

		if (chatInfoList != null) {
			Log.i("test", "chatInfoList.size : " + chatInfoList.size());
			for (int i = 0; i < chatInfoList.size(); i++) {
				Log.i("test", "content :" + chatInfoList.get(i).getContent());

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
				hideSoftInput(v.getWindowToken());
				return false;
			}
		});

		leftBtn = (ImageButton) getActivity().findViewById(R.id.btn_back);
		leftBtn.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				hideSoftInput(arg0.getWindowToken());
				sendMessageBroadcast();
				return false;
			}
		});

		mBtnSend = (Button) getActivity().findViewById(R.id.btn_send);
		mBtnSend.setOnClickListener(this);
		mEditTextContent = (EditText) getActivity().findViewById(
				R.id.et_sendmessage);
		mEditTextContent.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				setGridViewVisibility(false);
			}
		});

		mAdapter = new ChatMsgViewAdapter(getActivity(), mDataArrays);
		mListView.setAdapter(mAdapter);

		Log.i("test", "userID: " + userId);
		chatMsgList.addAll(MessageInfo.menuFeedbackList);
		MessageInfo.menuFeedbackList.clear();

		uploadFileInstance = new UploadFileUtil(getActivity());
		chatSharedPre = new SharedPreferencesUtil(getActivity());

		getLocalMessage(chatMsgList);

		menuGridView = (GridView) getActivity().findViewById(
				R.id.message_gridView);
		menuBtn = (ImageButton) getActivity().findViewById(R.id.ib_open_menu);
		menuBtn.setOnClickListener(this);
		topGridView = (GridView) getActivity().findViewById(R.id.top_gridView);

		ll_fasong = (RelativeLayout) getActivity().findViewById(R.id.ll_fasong);
		ll_yuyin = (RelativeLayout) getActivity().findViewById(R.id.ll_yuyin);
		pressTalkBtn = (Button) getActivity().findViewById(R.id.btn_yuyin);
		voiceBtn = (ImageButton) getActivity().findViewById(
				R.id.chatting_voice_btn);
		voiceBtn.setOnClickListener(this);
		keyboardBtn = (ImageButton) getActivity().findViewById(
				R.id.chatting_keyboard_btn);
		keyboardBtn.setOnClickListener(this);
		plusBtn = (ImageButton) getActivity().findViewById(
				R.id.chatting_plus_btn);
		plusBtn.setOnClickListener(this);
		menuBtn = (ImageButton) getActivity().findViewById(R.id.ib_open_menu);
		menuBtn.setOnClickListener(this);

		topMenuItem.add(new gridItemInfo(getResources().getString(
				R.string.menu_phone), R.drawable.ico_phone));
		topMenuItem.add(new gridItemInfo(getResources().getString(
				R.string.menu_data), R.drawable.ico_data));
		topMenuItem.add(new gridItemInfo(getResources().getString(
				R.string.menu_invite), R.drawable.ico_invite));

		topGridView.setAdapter(new TopGridViewAdapter(getActivity(),
				topMenuItem));

		topGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				switch (arg2) {
				case 0:
					String phoneNum = friendEntity.getMobile();
					if (phoneNum == null || "".equals(phoneNum.trim())) {
						Toast.makeText(getActivity().getApplicationContext(),
								getResources().getString(R.string.dial_error),
								Toast.LENGTH_SHORT).show();
					} else {
						Intent intent = new Intent(Intent.ACTION_CALL, Uri
								.parse("tel:" + phoneNum));
						startActivity(intent);
					}
					break;
				case 1:
					Intent intent = new Intent(getActivity(),
							PersonalProfileActivity.class);
					Bundle bundle = new Bundle();
					friendEntity = getEntityFromUserID(userId);// for individual
																// chat
					bundle.putSerializable("friendEntity", friendEntity);
					intent.putExtras(bundle);
					startActivity(intent);
					getActivity().overridePendingTransition(
							R.anim.in_from_right, R.anim.out_of_left);
					break;
				case 2:
					Intent intent_1 = new Intent(getActivity(),
							SelectMemberActivity.class);
					startActivity(intent_1);
					getActivity().overridePendingTransition(
							R.anim.in_from_right, R.anim.out_of_left);
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
		/*
		 * lstMenuItem.add(new gridItemInfo(getResources().getString(
		 * R.string.location), R.drawable.ico_location)); lstMenuItem.add(new
		 * gridItemInfo(getResources().getString( R.string.video),
		 * R.drawable.ico_video));
		 */
		lstMenuItem.add(new gridItemInfo(getResources()
				.getString(R.string.task), R.drawable.ico_task));
		lstMenuItem.add(new gridItemInfo(getResources().getString(
				R.string.meeting), R.drawable.ico_meeting));
		/*
		 * lstMenuItem.add(new gridItemInfo(getResources().getString(
		 * R.string.white_board), R.drawable.ico_whiteboard));
		 */
		menuGridView
				.setAdapter(new gridViewAdapter(getActivity(), lstMenuItem));
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
							Intent intent1 = new Intent(
									Intent.ACTION_PICK,
									android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
							startActivityForResult(intent1, RESULT_LOAD_IMAGE);
							break;
						case 2:
							Intent intent4 = new Intent(getActivity(),
									ScheduleAddActivity.class);
							intent4.putExtra("type", 0);
							startActivity(intent4);
							break;
						case 3:
							Intent intent5 = new Intent(getActivity(),
									ScheduleAddActivity.class);
							intent5.putExtra("type", 1);
							startActivity(intent5);
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
					upTime = System.currentTimeMillis();
					if (mRecorder != null) {
						mRecorder.stop();
						mRecorder.release();
						mRecorder = null;
					}
					if ((upTime - downTime) < HIGHLIMITTIME
							&& (upTime - downTime) > LOWLIMITTIME) {
						tempFile.delete();
						Toast toast = Toast.makeText(getActivity(),
								"too short", Toast.LENGTH_SHORT);
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
								null, null, null, 0);

						uploadFileInstance.uploadFile(
								tempFile.getAbsolutePath(), userId,
								MessageInfo.VOICE, msguuid,
								MessageInfo.INDIVIDUAL);
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
						Toast toast = Toast.makeText(getActivity(),
								"Please Insert SdCard", Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.BOTTOM, 0, 150);
						toast.show();
						break;
					}
					downTime = System.currentTimeMillis();

					View view = LayoutInflater.from(getActivity()).inflate(
							R.layout.audio_recorder_ring, null);

					menuWindow = new PopupWindow(view, 200, 200);
					view.findViewById(R.id.recorder_ring).setVisibility(
							View.VISIBLE);
					talkLabel = (TextView) view.findViewById(R.id.talk_label);

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
							.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
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
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK
				&& null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };
			Cursor cursor = getActivity().getContentResolver().query(
					selectedImage, filePathColumn, null, null, null);
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();

			Intent intent = new Intent(getActivity(), ImageResultActivity.class);
			intent.putExtra("path", picturePath);
			startActivityForResult(intent, RESULT_SHOW_IMAGE);

		} else if (requestCode == RESULT_SHOW_IMAGE && resultCode == RESULT_OK
				&& null != data) {
			String picturePath = data.getStringExtra("path");
			String msguuid = StringWidthWeightRandom.getNextString();
			createLocalmsgEntity(MessageInfo.PICTURE, picturePath, null,
					userId, msguuid, null, null, null, null, 0);
			Log.i("test", "userId 2 :" + userId);
			uploadFileInstance.uploadFile(picturePath, userId,
					MessageInfo.PICTURE, msguuid, MessageInfo.INDIVIDUAL);
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
			int vuSize = 10 * mRecorder.getMaxAmplitude() / 32768;
			// talkLabel.setText(vuSize + "");
			mHandler.postDelayed(mUpdateMicStatusTimer, 300);
		}
	}

	public boolean checkSDCard() {
		return Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState());
	}

	public void updateChatStatus(ChatMsgEntity entity) {
		int status = entity.getStatus();
		entity.setSenderId(userId);
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
			if (status == MessageInfo.SEND_ARRIVAL
					&& mDataArrays.get(listId) != null) {
				Log.i("test", "listId :" + listId);
				Log.i("test", "TYPE :" + mDataArrays.get(listId).getType());
				mDataArrays.get(listId).setAnimVisibile(View.GONE);
				mDataArrays.get(listId).setStateVisible(View.VISIBLE);
			} else if (status == MessageInfo.SEND_READED
					&& mDataArrays.get(listId) != null) {
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
		entity.setMessageType(MessageInfo.USER_FEEDBACK);
		
		mDataArrays.add(entity);
		mAdapter.notifyDataSetChanged();
		if (isSend) {
			msguidMap.put(msguuid, mAdapter.getCount() - 1);
		}
		mEditTextContent.setText("");
		mListView.setSelection(mListView.getCount() - 1);

	}

	public void openTaskMember() {
		Intent intent = new Intent(getActivity(), TaskMemberActivity.class);
		startActivity(intent);
		getActivity().overridePendingTransition(R.anim.in_from_right,
				R.anim.out_of_left);
	}

	private void createLocalmsgEntity(int type, String content,
			String voiceSec, String receiverId, String msguuid, String title,
			String beginTime, String endTime, String address, int scheduleType) {
		ChatMsgEntity entity = new ChatMsgEntity();
		entity.setTime(MessageInfo.getChatTime());
		entity.setType(type);
		entity.setIsComing(false);
		entity.setReceiverId(userId);
		entity.setBackground(R.drawable.ima_sent);
		entity.setChatState(getResources().getString(R.string.sent));
		entity.setMsguuid(msguuid);
		entity.setAnimVisibile(View.VISIBLE);
		entity.setStateVisible(View.GONE);

		if (prevDate == null) {
			if (chatSharedPre.getChatDate(userId + "_" + UserInfo.db_id) != null
					&& chatSharedPre.getChatDate(userId + "_" + UserInfo.db_id)
							.length() > 0) {
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
			UserInfo.clientsocket.sendMessage(content, 1, msguuid,
					UserInfo.db_id, userId, null, null, null, null, "1", null,
					false);
		} else if (type == MessageInfo.PICTURE) {
			entity.setBigPicUrl(content);
			entity.setContent(getResources().getString(R.string.picture));
			entity.setReceiverId(receiverId);
		} else if (type == MessageInfo.VOICE) {
			entity.setSecond(voiceSec);
			entity.setVoiceUrl(content);
			entity.setContent(getResources().getString(R.string.voice));
			entity.setReceiverId(receiverId);
		} else {
			entity.setScheduleTitle(title);
			entity.setScheduleBeginTime(beginTime);
			entity.setScheduleEndTime(endTime);
			entity.setScheduleAddress(address);
			entity.setScheduleType(scheduleType);
			String[] str = receiverId.split(",");
			if (str.length == 1 && str[0].equals(UserInfo.db_id)) {
				entity.setAnimVisibile(View.GONE);
				entity.setStateVisible(View.VISIBLE);
				entity.setBackground(R.drawable.ima_readed);
				entity.setChatState(getResources().getString(R.string.readed));
			}
		}
		updateChatView(entity, msguuid, true);
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

		friendEntity = getEntityFromUserID(userId);// for individual chat

		// register broadcast
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(MessageInfo.MessageBroadCastName);
		cbr = new ChatBroadcastReceiver();
		getActivity().registerReceiver(cbr, myIntentFilter);
		Log.i("test", "chatactivity onStart");
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.i("test", "feedbackfragment onResume");
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.i("test", "feedbackfragment onPause");
	}

	@Override
	public void onStop() {
		super.onStop();
		instance = null;
		chatSharedPre.saveDatatoShared(userId + "_" + UserInfo.db_id,
				mDataArrays);

		if (prevDate == null)
			prevDate = MessageInfo.getChattingDate();

		chatSharedPre.saveChatDate(userId + "_" + UserInfo.db_id, prevDate);

		getActivity().unregisterReceiver(cbr);
		Log.i("test", "feedbackfragment onStop");
	}

	@Override
	public void onDestroy() {
		Log.i("test", "feedbackfragment onDestroy");
		mDataArrays.clear();
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_send:
			String messageContent = mEditTextContent.getText().toString();
			if (messageContent.length() > 0) {
				createLocalmsgEntity(MessageInfo.TEXT, messageContent, null,
						null, StringWidthWeightRandom.getNextString(), null,
						null, null, null, 0);
			}
			break;
		case R.id.chatting_voice_btn:
			setGridViewVisibility(false);
			voiceBtn.setVisibility(View.GONE);
			keyboardBtn.setVisibility(View.VISIBLE);
			ll_fasong.setVisibility(View.GONE);
			ll_yuyin.setVisibility(View.VISIBLE);
			break;
		case R.id.chatting_keyboard_btn:
			setGridViewVisibility(false);
			voiceBtn.setVisibility(View.VISIBLE);
			keyboardBtn.setVisibility(View.GONE);
			ll_fasong.setVisibility(View.VISIBLE);
			ll_yuyin.setVisibility(View.GONE);
			break;
		case R.id.chatting_plus_btn:
			hideSoftInput(getActivity().getCurrentFocus().getWindowToken());
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
		DBOpenHelper dbOpenHelper = new DBOpenHelper(getActivity());
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

	// for deliver unreaded message count
	private void sendMessageBroadcast() {
		Intent intent = new Intent();
		intent.setAction(MessageInfo.MessageBroadCastName);
		intent.putExtra("name",
				getActivity().getResources().getString(R.string.user_feedback));
		getActivity().sendBroadcast(intent);
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
			InputMethodManager im = (InputMethodManager) getActivity()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			im.hideSoftInputFromWindow(token,
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
}
