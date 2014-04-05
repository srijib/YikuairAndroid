package com.bestapp.yikuair.fragments;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.bestapp.yikuair.R;
import com.bestapp.yikuair.database.DBOpenHelper;
import com.bestapp.yikuair.utils.FriendEntity;
import com.bestapp.yikuair.utils.MessageInfo;
import com.bestapp.yikuair.utils.StringWidthWeightRandom;
import com.bestapp.yikuair.utils.UserInfo;

public class TaskMemberActivity extends Activity {
	private GridView gridView;
	private BottomAdapter adapter;
	private int COLUMN_WIDTH = 360;
	private static final int HORIZONTAL_SPACE = 0;
	private List<MemberItemInfo> memberList = new ArrayList<MemberItemInfo>();
	private LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
	private String memberId;
	private String memberName;
	private String groupId;
	private int listId;
	private DBOpenHelper dbOpenHelper;
	private Cursor cursor;
	private TextView groupName;
	private Button addBtn;
	private boolean isPressed = false;
	private ClientSocket client;
	private Dialog mDialog;
	private GroupBroadcastReceiver gbr;
	private boolean isDelMember = false;
	private boolean isAddMember = false;
	private int mScreenHeight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_member);

		mDialog = new AlertDialog.Builder(this).create();

		groupId = getIntent().getStringExtra("groupId");
		memberId = getIntent().getStringExtra("ids");
		Log.e("FM", memberId);
		memberName = getIntent().getStringExtra("names");
//		memberId.
		listId = getIntent().getIntExtra("listId", -1);
		String[] ids = memberId.split("、");
		String[] names = memberName.split("、");
//		String[] ids = memberId.split("„ÄÅ");
//		String[] names = memberName.split("„ÄÅ");

		client = new ClientSocket(this);

		map.clear();
		memberList.add(new MemberItemInfo(null, null, null, null, 0));
		Log.e("test", "ids .size :" + ids.length);
		Log.e("test", "names size:" + names.length);
		for (int i = 0; i < ids.length; i++) {
			memberList.add(new MemberItemInfo(ids[i], names[i],
					getSexFromDB(ids[i]), getHeadUrlFromDB(ids[i]), 1));
			map.put(ids[i], names[i]);
		}
		// register broadcastreceiver
		if (gbr == null) {
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(MessageInfo.GroupInfoResultBroadCast);
			gbr = new GroupBroadcastReceiver();
			registerReceiver(gbr, intentFilter);
		}
		initView();
	}

	public class GroupBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			int resultCode = intent.getIntExtra("code", 0);
			mDialog.dismiss();
			if (isDelMember) {
				updatePhoneBook();
				isDelMember = false;
				return;
			}
			if (isAddMember) {
				updatePhoneBook();
				isAddMember = false;
				return;
			}
			if (ChatActivity.instance != null)
				ChatActivity.instance.finish();
			if (memberId != null) {
				for (int i = 0; i < MessageFragment.messageList.size(); i++) {
					if (MessageFragment.messageList.get(i).getId()
							.equals(memberId)) {
						MessageFragment.messageList.remove(i);
						MessageFragment.userList.remove(i);
						MessageFragment.boolList.remove(i);
					}
				}
				MessageFragment.lstAdapter.notifyDataSetChanged();

				if (PhoneBookFragment.instance != null) {
					for (int i = 0; i < PhoneBookFragment.contactList.size(); i++) {
						if (PhoneBookFragment.contactList.get(i).getGroupId() != null
								&& PhoneBookFragment.contactList.get(i)
										.getGroupId().equals(groupId)) {
							PhoneBookFragment.contactList.remove(i);
							PhoneBookFragment.listAdapter
									.notifyDataSetChanged();
							break;
						}
					}
				} else {
					if (MessageInfo.groupList != null
							&& MessageInfo.groupList.size() > 0) {
						for (int i = 0; i < MessageInfo.groupList.size(); i++) {
							if (MessageInfo.groupList.get(i).getGroupId()
									.equals(groupId)) {
								MessageInfo.groupList.remove(i);
							}
						}
					}
				}
			} else {
				for (int i = 0; i < MessageInfo.messageEntityList.size(); i++) {
					if (MessageInfo.messageEntityList.get(i).getChatType() == MessageInfo.GROUP
							&& groupId != null
							&& MessageInfo.messageEntityList.get(i)
									.getReceiverId().equals(groupId)) {
						MessageInfo.messageEntityList.remove(i);
					}
				}
			}
			finish();
			overridePendingTransition(R.anim.in_from_left, R.anim.out_of_right);
		}
	}

	public void updatePhoneBook() {
		if (PhoneBookFragment.instance != null) {
			for (int j = 0; j < PhoneBookFragment.contactList.size(); j++) {
				if (PhoneBookFragment.contactList.get(j).getGroupId() != null
						&& PhoneBookFragment.contactList.get(j).getGroupId()
								.equals(groupId)) {
					PhoneBookFragment.contactList.get(j)
							.setRealName(memberName);
					PhoneBookFragment.contactList.get(j).setDbId(memberId);
					PhoneBookFragment.listAdapter.notifyDataSetChanged();
				}
			}
		}
	}

	public String getSexFromDB(String dbId) {
		if (dbId == null)
			return "";
		dbOpenHelper = new DBOpenHelper(this);
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		String sex = "";
		cursor = db.rawQuery("select * from contactsTable where dbid=?",
				new String[] { dbId });
		if (cursor.moveToFirst()) {
			sex = cursor.getString(13);
		}
		dbOpenHelper.close();
		cursor.close();
		return sex;
	}

	public String getHeadUrlFromDB(String dbId) {
		if (dbId == null)
			return "";
		dbOpenHelper = new DBOpenHelper(this);
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

	public void initView() {
		mScreenHeight = getWindowManager().getDefaultDisplay().getHeight();
		if (mScreenHeight < 1920) {
			COLUMN_WIDTH = 240;
		}

		gridView = (GridView) findViewById(R.id.member_grid);
		addBtn = (Button) findViewById(R.id.add_to_maillist_btn);
		for (int i = 0; i < MessageInfo.groupList.size(); i++) {
			if (MessageInfo.groupList.get(i).getGroupId().equals(groupId)) {
				isPressed = true;
				addBtn.setText(getResources().getString(
						R.string.remove_from_maillist));
			}
		}

		addBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (isPressed) {
					addBtn.setText(getResources().getString(
							R.string.add_to_maillist));
					for (int i = 0; i < MessageInfo.groupList.size(); i++) {
						if (MessageInfo.groupList.get(i).getGroupId()
								.equals(groupId))
							MessageInfo.groupList.remove(i);
					}
					if (PhoneBookFragment.instance != null) {
						for (int i = 0; i < PhoneBookFragment.contactList
								.size(); i++) {
							if (PhoneBookFragment.contactList.get(i)
									.getGroupId() != null
									&& PhoneBookFragment.contactList.get(i)
											.getGroupId().equals(groupId)) {
								PhoneBookFragment.contactList.remove(i);
								PhoneBookFragment.listAdapter
										.notifyDataSetChanged();
								break;
							}
						}
					}
					isPressed = false;
				} else {
					addBtn.setText(getResources().getString(
							R.string.remove_from_maillist));
					isPressed = true;
					FriendEntity entity = new FriendEntity();
					entity.setRealName(memberName);
					entity.setSex("3");
					entity.setAlpher(getResources().getString(R.string.group));
					entity.setGroupId(groupId);
					entity.setDbId(memberId);
					// entity.setGroupId(MessageInfo.groupList.size());
					MessageInfo.groupList.add(entity);
				}
			}
		});
		groupName = (TextView) findViewById(R.id.group_name);
		groupName.setText(memberName);

		adapter = new BottomAdapter(this, memberList);
		gridView.setAdapter(adapter);

		LayoutParams params = new LayoutParams(adapter.getCount()
				* (COLUMN_WIDTH + HORIZONTAL_SPACE),
				android.view.ViewGroup.LayoutParams.FILL_PARENT);
		gridView.setLayoutParams(params);
		gridView.setColumnWidth(COLUMN_WIDTH);
		gridView.setHorizontalSpacing(HORIZONTAL_SPACE);
		gridView.setStretchMode(GridView.NO_STRETCH);
		gridView.setNumColumns(adapter.getCount());

		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (arg2 == 0) {
					Intent intent = new Intent();
					/*
					 * nameIdMap = (LinkedHashMap<String, String>)
					 * nameIdMeetingMap.clone(); MessageInfo.nameIdMap =
					 * (LinkedHashMap<String, String>) nameIdMap .clone();
					 */
					intent.setClass(TaskMemberActivity.this,
							SelectMemberActivity.class);
					MessageInfo.nameIdMap.clear();
					/*
					 * for (int i = 0; i < memberList.size(); i++) {
					 * MessageInfo.nameIdMap.put(memberList.get(i).getDbId(),
					 * memberList.get(i).getName()); }
					 */
					MessageInfo.nameIdMap = (LinkedHashMap<String, String>) map
							.clone();

					startActivityForResult(intent, 1);
					overridePendingTransition(R.anim.in_from_down,
							R.anim.out_of_up);
				}
			}
		});

		if (MessageInfo.groupList != null && !MessageInfo.groupList.isEmpty()) {
			for (int i = 0; i < MessageInfo.groupList.size(); i++) {
				if (MessageInfo.groupList.get(i).getDbId().equals(groupId)) {
					addBtn.setText(getResources().getString(
							R.string.remove_from_maillist));
					isPressed = true;
				}
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null)
			return;
		if (MessageInfo.nameIdMap != null && !MessageInfo.nameIdMap.isEmpty()) {

			List<String> nameList = new ArrayList<String>();
			List<String> idList = new ArrayList<String>();
			List<String> senderIdList = new ArrayList<String>();
			List<String> delIdList = new ArrayList<String>();

			Set set = MessageInfo.nameIdMap.keySet();
			Iterator it = set.iterator();
			String ids = "";
			String delIds = "";
			while (it.hasNext()) {
				String key = (String) it.next();
				String value = (String) MessageInfo.nameIdMap.get(key);
				nameList.add(value);
				idList.add(key);
			}

			for (int j = 0; j < memberList.size(); j++) {
				if (memberList.get(j).getType() == 0)
					continue;
				if (!MessageInfo.nameIdMap.containsKey(memberList.get(j)
						.getDbId())) {
					delIdList.add(memberList.get(j).getDbId());
					// memberList.remove(j);
				}
			}

			if (delIdList.size() > 0) {
				memberList.clear();
				map.clear();
				memberList.add(new MemberItemInfo(null, null, null, null, 0));
				for (int j = 0; j < idList.size(); j++) {
					memberList.add(new MemberItemInfo(idList.get(j), nameList
							.get(j), getSexFromDB(idList.get(j)),
							getHeadUrlFromDB(idList.get(j)), 1));
					map.put(idList.get(j), nameList.get(j));
				}

				setGridView();

				for (int k = 0; k < delIdList.size(); k++) {
					if (k == delIdList.size() - 1) {
						delIds += delIdList.get(k);
					} else {
						delIds += delIdList.get(k) + ",";
					}
				}
				memberId = "";
				memberName = "";
				for (int i = 0; i < idList.size(); i++) {
					if (i == idList.size() - 1) {
						memberId += idList.get(i);
						memberName += nameList.get(i);
					} else {
						memberId += idList.get(i) + "、";
						memberName += nameList.get(i) + "、";
					}
				}
				groupName.setText(memberName);

				if (MessageInfo.groupMap != null) {
					if (MessageInfo.groupMap.containsKey(groupId)) {
						MessageInfo.groupMap.remove(groupId);
						MessageInfo.groupMap.put(groupId, memberId);
					}
				}

				if (client == null)
					client = new ClientSocket(this);
				MessageInfo.nameIdMap.clear();
				showRoundProcessDialog();
				if (client == null)
					client = new ClientSocket(this);
				client.sendMessage(delIds, 17,
						StringWidthWeightRandom.getNextString(),
						UserInfo.db_id, groupId, null, null, null, null, null,
						null, false);

				isDelMember = true;
				return;
			}

			for (int i = 0; i < idList.size(); i++) {
				if (!map.containsKey(idList.get(i))) {
					memberList.add(new MemberItemInfo(idList.get(i), nameList
							.get(i), getSexFromDB(idList.get(i)),
							getHeadUrlFromDB(idList.get(i)), 1));

					senderIdList.add(idList.get(i));
					memberName = memberName + "、" + nameList.get(i);
					memberId = memberId + "、" + idList.get(i);
				}
			}
			groupName.setText(memberName);

			if (MessageInfo.groupMap != null) {
				if (MessageInfo.groupMap.containsKey(groupId)) {
					MessageInfo.groupMap.remove(groupId);
					MessageInfo.groupMap.put(groupId, memberId);
				}
			}

			memberList.clear();
			map.clear();
			memberList.add(new MemberItemInfo(null, null, null, null, 0));
			for (int j = 0; j < idList.size(); j++) {
				memberList.add(new MemberItemInfo(idList.get(j), nameList
						.get(j), getSexFromDB(idList.get(j)),
						getHeadUrlFromDB(idList.get(j)), 1));
				map.put(idList.get(j), nameList.get(j));
			}

			LayoutParams params = new LayoutParams(memberList.size()
					* (COLUMN_WIDTH + HORIZONTAL_SPACE),
					android.view.ViewGroup.LayoutParams.FILL_PARENT);
			gridView.setLayoutParams(params);
			gridView.setColumnWidth(COLUMN_WIDTH);
			gridView.setHorizontalSpacing(HORIZONTAL_SPACE);
			gridView.setStretchMode(GridView.NO_STRETCH);
			gridView.setNumColumns(memberList.size());

			adapter.notifyDataSetChanged();
			MessageInfo.nameIdMap.clear();

			if (senderIdList != null && !senderIdList.isEmpty()) {
				for (int i = 0; i < senderIdList.size(); i++) {
					if (i == senderIdList.size() - 1) {
						ids += senderIdList.get(i);
					} else {
						ids += senderIdList.get(i) + "、";
					}
				}
				showRoundProcessDialog();
				if (client == null)
					client = new ClientSocket(this);
				client.sendMessage(ids.replace("、", ","), 16,
						StringWidthWeightRandom.getNextString(),
						UserInfo.db_id, groupId, null, null, null, null, null,
						null, false);
				isAddMember = true;
			}
		}
	}

	public void setGridView() {
		LayoutParams params = new LayoutParams(memberList.size()
				* (COLUMN_WIDTH + HORIZONTAL_SPACE),
				android.view.ViewGroup.LayoutParams.FILL_PARENT);
		gridView.setLayoutParams(params);
		gridView.setColumnWidth(COLUMN_WIDTH);
		gridView.setHorizontalSpacing(HORIZONTAL_SPACE);
		gridView.setStretchMode(GridView.NO_STRETCH);
		gridView.setNumColumns(memberList.size());

		adapter.notifyDataSetChanged();
	}

	public void showRoundProcessDialog() {
		mDialog.show();
		mDialog.setCanceledOnTouchOutside(false);
		mDialog.setContentView(R.layout.quit_group_loading_dialog);
	}

	public void quitFromGroup(View view) {
		if (client == null)
			client = new ClientSocket(this);
		client.sendMessage(UserInfo.db_id, 17,
				StringWidthWeightRandom.getNextString(), UserInfo.db_id,
				groupId, null, null, null, null, null, null, false);
		showRoundProcessDialog();
	}

	public void backtoChat(View view) {
		Intent data = new Intent();
		memberName = groupName.getText().toString();
		data.putExtra("titleNames", memberName);
		data.putExtra("titleIds", memberId);
		setResult(5, data);

		if (MessageInfo.groupList != null && MessageInfo.groupList.size() > 0) {
			for (int i = 0; i < MessageInfo.groupList.size(); i++) {
				if (MessageInfo.groupList.get(i).getGroupId().equals(groupId)) {
					MessageInfo.groupList.get(i).setDbId(memberId);
					MessageInfo.groupList.get(i).setRealName(memberName);
				}
			}
		}

		finish();
		overridePendingTransition(R.anim.in_from_left, R.anim.out_of_right);
	}

	public boolean onTouchEvent(android.view.MotionEvent event) {
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		return imm.hideSoftInputFromWindow(this.getCurrentFocus()
				.getWindowToken(), 0);
	}

	class BottomAdapter extends BaseAdapter {
		private Context context;
		private List<MemberItemInfo> mData;
		private LayoutInflater mInflater;
		public ImageLoader imgLoader;

		public BottomAdapter(Context context, List<MemberItemInfo> list) {
			this.context = context;
			mInflater = LayoutInflater.from(context);
			mData = list;
			this.imgLoader = new ImageLoader(context);
		}

		@Override
		public int getCount() {
			return mData.size();
		}

		@Override
		public Object getItem(int position) {
			return mData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public int getItemViewType(int position) {
			return mData.get(position).getType();
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			ViewHolder holder = null;
			holder = new ViewHolder();
			if (mData.get(position).getType() == 0) {
				convertView = mInflater.inflate(
						R.layout.grid_add_group_member_item, null);
				return convertView;
			} else {
				convertView = mInflater.inflate(
						R.layout.grid_group_member_item, null);
				holder.name = (TextView) convertView
						.findViewById(R.id.tv_staff_name);
				holder.photo = (ImageView) convertView
						.findViewById(R.id.iv_staff_photo);
				holder.delete = (ImageView) convertView
						.findViewById(R.id.iv_del_member);
				holder.name.setText(mData.get(position).getName());

				holder.delete.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						if (mData.get(position).getDbId()
								.equals(UserInfo.db_id)) {
							quitFromGroup(arg0);
							return;
						}

						if (client == null)
							client = new ClientSocket(context);
						client.sendMessage(mData.get(position).getDbId(), 17,
								StringWidthWeightRandom.getNextString(),
								UserInfo.db_id, groupId, null, null, null,
								null, null, null, false);
						if (map.containsKey(mData.get(position).getDbId()))
							map.remove(mData.get(position).getDbId());
						showRoundProcessDialog();
						isDelMember = true;
						mData.remove(position);
						memberName = "";
						memberId = "";
						for (int i = 0; i < mData.size(); i++) {
							if (i == 0)
								continue;
							if (i == mData.size() - 1) {
								memberName += mData.get(i).getName();
								memberId += mData.get(i).getDbId();
							} else {
								memberName += mData.get(i).getName() + "、";
								memberId += mData.get(i).getDbId() + "、";
							}
						}
						groupName.setText(memberName);

						if (MessageInfo.groupMap != null) {
							if (MessageInfo.groupMap.containsKey(groupId)) {
								MessageInfo.groupMap.remove(groupId);
								MessageInfo.groupMap.put(groupId, memberId);
							}
						}

						LayoutParams params = new LayoutParams(mData.size()
								* (COLUMN_WIDTH + HORIZONTAL_SPACE),
								android.view.ViewGroup.LayoutParams.FILL_PARENT);
						gridView.setLayoutParams(params);
						gridView.setColumnWidth(COLUMN_WIDTH);
						gridView.setHorizontalSpacing(HORIZONTAL_SPACE);
						gridView.setStretchMode(GridView.NO_STRETCH);
						gridView.setNumColumns(mData.size());

						notifyDataSetChanged();
					}
				});

				if (mData.get(position).getHeadUrl() != null
						&& mData.get(position).getHeadUrl().length() > 0) {
					String headUrl = "http://" + UserInfo.downloadImgUrl
							+ mData.get(position).getHeadUrl();
					imgLoader.DisplayImage(headUrl, (Activity) context,
							holder.photo);
				} else {
					if (mData.get(position).getSex().equals("0")) {
						holder.photo.setBackgroundDrawable(context
								.getResources().getDrawable(
										R.drawable.ico_girl_big));

					} else {
						holder.photo.setBackgroundDrawable(context
								.getResources().getDrawable(
										R.drawable.ico_boy_big));
					}
				}
				return convertView;
			}
		}

		public class ViewHolder {
			public TextView name;
			public ImageView photo;
			public ImageView delete;
		}
	}

	protected void onStart() {
		super.onStart();
		if (UserInfo.isHomePressed) {
			ClientSocket client = new ClientSocket(this);
			UserInfo.isSendBroadCast = false;
			client.sendMessage(null, 0,
					StringWidthWeightRandom.getNextString(), null, null, null,
					null, null, null, null, null, true);
		}
	}

	protected void onDestroy() {
		super.onDestroy();
		if (gbr != null) {
			unregisterReceiver(gbr);
			gbr = null;
		}
		Log.e("test", "task member onDestroy.....");
	}
}
