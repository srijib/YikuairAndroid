package com.bestapp.yikuair.fragments;

import java.util.ArrayList;
import java.util.HashMap;
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bestapp.yikuair.R;
import com.bestapp.yikuair.database.DBOpenHelper;
import com.bestapp.yikuair.fragments.LetterListView.OnTouchingLetterChangedListener;
import com.bestapp.yikuair.utils.FriendEntity;
import com.bestapp.yikuair.utils.MessageInfo;
import com.bestapp.yikuair.utils.SharedPreferencesUtil;
import com.bestapp.yikuair.utils.StringWidthWeightRandom;
import com.bestapp.yikuair.utils.UserInfo;

public class SelectMemberActivity extends Activity {

	public ContactMenuView menuListView = null;
	private ImageButton menuBtn;
	private GroupResultBroadcastReceiver gbr = null;
	private TextView allStaff;
	private int menuWidth;
	private int marginLeftWidth;
	private boolean hasMeasured = false;
	private RelativeLayout titleBar;
	private LetterListView letterListView;
	private ArrayList<String> popList = new ArrayList<String>();
	private HashMap<String, Integer> alphaIndexer;
	private ListView contactListView;
	private ListAdapter listAdapter;
	private EditText editText;
	private TextView overlay;
	private Handler handler;
	private Button addBtn;
	private ImageButton closeBtn;
	private RelativeLayout rl_layout;
	private String[] sections;
	private OverlayThread overlayThread;
	private List<FriendEntity> contactList = new ArrayList<FriendEntity>();
	private List<FriendEntity> allStaffList = new ArrayList<FriendEntity>();
	private List<FriendEntity> searchResultList = new ArrayList<FriendEntity>();
	private LinkedHashMap<String, String> nameIdMap = new LinkedHashMap<String, String>();
	private Cursor cursor;
	private DBOpenHelper dbOpenHelper;
	protected String nameStr = "";
	private WindowManager mWindowManager;
	private int count;
	private boolean isStartChat;
	private Dialog mDialog;
	private List<String> idLst = new ArrayList<String>();
	private List<String> nameLst = new ArrayList<String>();
	private String ids = null;
	private String names = null;
	private String groupId;
	private LinearLayout menuTitle;
	public static SelectMemberActivity instance = null;
	public SharedPreferencesUtil shared;
	private final static String YIKUAIR_GROUP = "yikuair_group";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		setContentView(R.layout.select_member_list);

		String type = getIntent().getStringExtra("type");
		if (type != null && type.equals("startChat")) {
			// LinearLayout ll_checkbox = (LinearLayout)
			// findViewById(R.id.ll_individual_checkbox);
			// ll_checkbox.setVisibility(View.GONE);
			isStartChat = true;
			MessageInfo.nameIdMap.clear();
			String idStr = getIntent().getStringExtra("ids");
			if (idStr != null) {
				String[] idS = idStr.split("、");
				for (int i = 0; i < idS.length; i++) {
					MessageInfo.nameIdMap.put(idS[i], getNameFromDB(idS[i]));
				}
			}
		} else {
			isStartChat = false;
		}

		nameIdMap = (LinkedHashMap<String, String>) MessageInfo.nameIdMap
				.clone();

		/******** added 2013 11.6 ********/

		MessageInfo.nameIdMap.clear();

		/****************************/

		count = nameIdMap.size();

		shared = new SharedPreferencesUtil(this);
		dbOpenHelper = new DBOpenHelper(this);
		mDialog = new AlertDialog.Builder(this).create();

		letterListView = (LetterListView) findViewById(R.id.phoneBookLetterList);
		letterListView
				.setOnTouchingLetterChangedListener(new LetterListViewListener());

		alphaIndexer = new HashMap<String, Integer>();
		handler = new Handler();
		overlayThread = new OverlayThread();
		contactListView = (ListView) findViewById(R.id.phone_list_view);

		rl_layout = (RelativeLayout) findViewById(R.id.rl_layout);
		menuBtn = (ImageButton) findViewById(R.id.menu_btn);
		allStaff = (TextView) findViewById(R.id.all_staff);
		addBtn = (Button) findViewById(R.id.btn_add);
		closeBtn = (ImageButton) findViewById(R.id.addresslist_left_btn);
		menuTitle = (LinearLayout) findViewById(R.id.phonebook_title);
		menuTitle.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				showMenu();
				return false;
			}
		});

		titleBar = (RelativeLayout) findViewById(R.id.addresslist_title);
		editText = (EditText) findViewById(R.id.et_search);
		editText.addTextChangedListener(mTextWatcher);

		/*
		 * individual_photo = (ImageView) findViewById(R.id.img_photo); if
		 * (UserInfo.LocalphotoPath != null && UserInfo.LocalphotoPath.length()
		 * != 0) { Bitmap b = BitmapFactory.decodeFile(UserInfo.LocalphotoPath);
		 * individual_photo.setImageBitmap(b); } else { if (UserInfo.sex != null
		 * && UserInfo.sex.equals("0"))
		 * individual_photo.setImageDrawable(getResources().getDrawable(
		 * R.drawable.girl)); else
		 * individual_photo.setImageDrawable(getResources().getDrawable(
		 * R.drawable.boy)); }
		 */

		initOverlay();
		initContactList();
		initPopMenu();

		contactListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (contactList.get(arg2).getDbId().equals(UserInfo.db_id)
						|| contactList.get(arg2).getSex().equals("3"))
					return;

				RelativeLayout rl = (RelativeLayout) arg1;
				CheckBox cb = (CheckBox) rl.findViewById(R.id.cb_check);

				if (cb.isChecked()) {
					contactList.get(arg2).setIsChecked(false);
					nameIdMap.remove(contactList.get(arg2).getDbId());
				} else {
					contactList.get(arg2).setIsChecked(true);
					nameIdMap.put(contactList.get(arg2).getDbId(), contactList
							.get(arg2).getRealName());
				}
				listAdapter.notifyDataSetChanged();

				count = nameIdMap.size();
				addBtn.setText(getResources().getString(R.string.btn_add) + "("
						+ String.valueOf(count) + ")");
			}
		});
		rl_layout.setOnClickListener(btnClick);
		addBtn.setOnClickListener(btnClick);

		// register broadcast
		if (gbr == null) {
			Log.e("test","selectmemberactivity register groupbroadcast................");
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(MessageInfo.GroupBroadCastName);
			gbr = new GroupResultBroadcastReceiver();
			registerReceiver(gbr, intentFilter);
		}
	}

	public String getNameFromDB(String dbId) {
		dbOpenHelper = new DBOpenHelper(this);
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		String name = "";
		Cursor cursor = db.rawQuery("select * from contactsTable where dbid=?",
				new String[] { dbId });
		if (cursor.moveToFirst()) {
			name = cursor.getString(2);
		}
		dbOpenHelper.close();
		cursor.close();
		return name;
	}

	class GroupResultBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			Log.i("test", "receive group result broadcast");
			int resultCode = arg1.getIntExtra("code", 0);
			int token = arg1.getIntExtra("token", 0);
			groupId = arg1.getStringExtra("group_id");
			mDialog.dismiss();
			if (resultCode == 200) {
				if (token == 15) {
					Log.i("test", "token == 15....................");
					if (MessageInfo.groupMap != null
							&& !MessageInfo.groupMap.containsKey(groupId))
						MessageInfo.groupMap.put(groupId, ids);
					if (shared == null)
						shared = new SharedPreferencesUtil(
								SelectMemberActivity.this);
					shared.saveGroupMaptoShared(MessageInfo.groupMap,
							UserInfo.db_id + "_" + YIKUAIR_GROUP);
					startGroupChat();
				}
			} else
				Toast.makeText(
						getApplication(),
						getApplication().getString(R.string.create_group_error),
						Toast.LENGTH_LONG).show();
		}
	}

	public void showRoundProcessDialog() {
		mDialog.show();
		mDialog.setCanceledOnTouchOutside(false);
		mDialog.setContentView(R.layout.schedule_loading_dialog);
	}

	public void startGroupChat() {
		Intent intent = new Intent(SelectMemberActivity.this,
				ChatActivity.class);
		intent.putExtra("Id", ids);
		Log.e("test", "select ids :" + ids);
		intent.putExtra("name", names);
		intent.putExtra("group_id", groupId);
		intent.putExtra("isFromSelectMember", true);
		startActivity(intent);
		if (isStartChat) {
			if (ChatActivity.instance != null)
				ChatActivity.instance.finish();
			finish();
		}
		overridePendingTransition(R.anim.in_from_right, R.anim.out_of_left);
	}

	TextWatcher mTextWatcher = new TextWatcher() {
		private CharSequence inputContent;

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			inputContent = s;
			Log.i("test", "str : " + inputContent);

			if (inputContent.length() == 0) {
				contactList.clear();
				contactList.addAll(allStaffList);
				listAdapter.notifyDataSetChanged();
				return;
			}
			searchResultList.clear();
			for (int i = 0; i < allStaffList.size(); i++) {
				if (allStaffList.get(i).getRealName().contains(inputContent)) {
					searchResultList.add(allStaffList.get(i));
				}
			}
			contactList.clear();
			contactList.addAll(searchResultList);
			listAdapter.notifyDataSetChanged();
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
		}
	};

	private void showMenu() {
		if (!hasMeasured) {
			menuWidth = menuBtn.getWidth() + allStaff.getWidth();
			marginLeftWidth = allStaff.getLeft();
			hasMeasured = true;
		}
		switchPopMenuShow(titleBar, menuWidth, marginLeftWidth);
		if (popList.size() > 0)
			menuBtn.setBackgroundResource(R.drawable.ico_close);
	}

	private OnClickListener btnClick = new OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_add:
				Intent intent = getIntent();
				if (!isStartChat) {
					MessageInfo.nameIdMap = (LinkedHashMap<String, String>) nameIdMap
							.clone();
					setResult(RESULT_OK, intent);
					closeActivity();
				} else {
					if (nameIdMap != null && nameIdMap.size() == 0) {
						Toast.makeText(
								SelectMemberActivity.this,
								getResources().getString(
										R.string.chat_member_error),
								Toast.LENGTH_LONG).show();
						return;
					}
					Intent intent1 = new Intent(SelectMemberActivity.this,
							ChatActivity.class);
					Set set = nameIdMap.keySet();
					Iterator it = set.iterator();
					idLst.clear();
					nameLst.clear();
					while (it.hasNext()) {
						String key = (String) it.next();
						String value = (String) nameIdMap.get(key);
						nameLst.add(value);
						idLst.add(key);
					}

					ids = "";
					names = "";
					for (int i = 0; i < idLst.size(); i++) {
						if (i == idLst.size() - 1) {
							ids += idLst.get(i);
							names += nameLst.get(i);
						} else {
							ids += idLst.get(i) + "、";
							names += nameLst.get(i) + "、";
						}
					}

					if (nameIdMap.size() > 1) {
						showRoundProcessDialog();
						ClientSocket client = new ClientSocket(
								SelectMemberActivity.this);
						/**************************/
						if (isStartChat) {
							ids = UserInfo.db_id + "、" + ids;
							names = UserInfo.realName + "、" + names;
						}
						/**************************/

						client.sendMessage(null, 15,
								StringWidthWeightRandom.getNextString(),
								UserInfo.db_id, ids.replace("、", ","), null,
								null, null, null, "2", null, false);
						return;
					}

					intent1.putExtra("Id", ids);
					intent1.putExtra("name", names);
					intent1.putExtra("isFromSelectMember", true);
					if (isStartChat) {
						if (ChatActivity.instance != null)
							ChatActivity.instance.finish();
						finish();
					}
					startActivity(intent1);
					overridePendingTransition(R.anim.in_from_right,
							R.anim.out_of_left);
				}
				break;
			case R.id.all_staff:
				showMenu();
				break;
			case R.id.menu_btn:
				showMenu();
				break;
			case R.id.rl_layout:
				closeActivity();
				break;
			}
		}
	};

	/*
	 * @Override public void onActivityResult(int requestCode, int resultCode,
	 * Intent data) { String content = data.getExtras().getString("content"); //
	 * String time = data.getExtras().getString("time"); // String idStr = "";
	 * String nameStr = ""; if (content != null && content.length() > 0) { if
	 * (idLst.size() == 1) { // idStr = idLst.get(0); nameStr = nameLst.get(0);
	 * } else if (idLst.size() > 1) { // idStr = ids; nameStr = names; }
	 * 
	 * ChatMsgEntity msgEntity = new ChatMsgEntity();
	 * msgEntity.setContent(data.getExtras().getString("content"));
	 * msgEntity.setTime(data.getExtras().getString("time")); Log.i("test",
	 * "format time :" + data.getExtras().getString("time"));
	 * msgEntity.setName(nameStr); msgEntity.setIsComing(false); if
	 * (idLst.size() > 1) { msgEntity.setChatType(MessageInfo.GROUP);
	 * msgEntity.setReceiverId(groupId); } else {
	 * msgEntity.setSenderId(UserInfo.db_id);
	 * msgEntity.setReceiverId(idLst.get(0)); }
	 * 
	 * msgEntity.setIsAdd(false); msgEntity.setStatus(MessageInfo.SEND_MESSAGE);
	 * MessageInfo.messageEntityList.add(msgEntity); } }
	 */
	private void initPopMenu() {
		if (menuListView == null) {
			menuListView = new ContactMenuView(this, menuBtn);
		}
		menuListView.listview.setOnItemClickListener(listClickListener);
		menuListView.clear();

		for (int i = 0; i < popList.size(); i++) {
			menuListView.add(i, popList.get(i));
		}
	}

	protected void switchPopMenuShow(View view, int menuWidth,
			int marginLeftWidth) {
		if (!menuListView.getIsShow()) {
			menuListView.show(view, menuWidth, marginLeftWidth);
		} else {
			menuListView.close();
		}
	}

	public FriendEntity getPersonalProfile() {
		if (UserInfo.db_id == null) {
			SharedPreferencesUtil shared = new SharedPreferencesUtil(this);
			shared.getUserInfo();
		}
		if (UserInfo.db_id == null)
			return null;
		try {
			SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
			cursor = db.rawQuery("select * from contactsTable where dbid=?",
					new String[] { UserInfo.db_id });
		} catch (Exception e){
			Toast.makeText(this, "通讯录正在更新，请稍候再试", Toast.LENGTH_LONG);
			this.finish();
		}


		FriendEntity entity = null;
		if (cursor.moveToFirst()) {
			String id = cursor.getString(cursor.getColumnIndex("userid"));
			String name = cursor.getString(cursor.getColumnIndex("name"));
			String phone = cursor.getString(cursor.getColumnIndex("phone"));
			String mobile = cursor.getString(cursor.getColumnIndex("mobile"));
			String signature = cursor.getString(cursor
					.getColumnIndex("signature"));
			String email = cursor.getString(cursor.getColumnIndex("email"));
			String duty = cursor.getString(cursor.getColumnIndex("duty"));
			String department = cursor.getString(cursor
					.getColumnIndex("department"));
			String headUrl = cursor.getString(cursor.getColumnIndex("headURL"));
			String companyId = cursor.getString(cursor
					.getColumnIndex("companyid"));
			String alpha = getResources().getString(R.string.individual);
			String team = cursor.getString(cursor.getColumnIndex("team"));
			String sex = cursor.getString(cursor.getColumnIndex("sex"));
			String dbId = UserInfo.db_id;
			entity = new FriendEntity(id, name, phone, mobile, email, duty,
					department, headUrl, signature, companyId, alpha, team,
					sex, dbId, "", "");
		}
		if (entity == null) {
			entity = new FriendEntity(UserInfo.id, UserInfo.realName, null,
					UserInfo.mobile, null, UserInfo.duty,
					UserInfo.departmentName, null, UserInfo.signature,
					UserInfo.companyId, getResources().getString(
							R.string.individual), UserInfo.team, UserInfo.sex,
					UserInfo.db_id, "", "");
		}

		cursor.close();
		dbOpenHelper.close();
		return entity;
	}

	private void initContactList() {
		contactList.add(getPersonalProfile());
		/*
		 * if (MessageInfo.groupList != null &&
		 * !MessageInfo.groupList.isEmpty()) { for (int i = 0; i <
		 * MessageInfo.groupList.size(); i++)
		 * contactList.add(MessageInfo.groupList.get(i)); }
		 */
		cursor = dbOpenHelper.select(0, null);
		int count = cursor.getCount();

		if (count > 0) {
			for (int i = 0; i < count; i++) {
				cursor.moveToPosition(i);
				if (cursor.getString(14).equals(UserInfo.db_id))
					continue;
				FriendEntity entity = new FriendEntity(cursor.getString(1),
						cursor.getString(2), cursor.getString(3),
						cursor.getString(4), cursor.getString(5),
						cursor.getString(6), cursor.getString(7),
						cursor.getString(8), cursor.getString(9),
						cursor.getString(10), cursor.getString(11),
						cursor.getString(12), cursor.getString(13),
						cursor.getString(14), cursor.getString(15),
						cursor.getString(16));

				if (nameIdMap != null && nameIdMap.size() > 0) {
					if (nameIdMap.containsKey(entity.getDbId())) {
						entity.setIsChecked(true);
					}
				}
				contactList.add(entity);
			}
		}

		cursor = dbOpenHelper.select(1, null);
		count = cursor.getCount();
		if (count > 0) {
			popList.add(getResources().getString(R.string.all_staff));
			for (int j = 0; j < count; j++) {
				cursor.moveToPosition(j);
				if (cursor.getString(1) == null
						|| cursor.getString(1).length() <= 1)
					continue;
				popList.add(cursor.getString(1));
			}
		}
		cursor.close();
		dbOpenHelper.close();
		allStaffList.addAll(contactList);
		switchalphaIndex(contactList);

		listAdapter = new ListAdapter(this, contactList);
		contactListView.setAdapter(listAdapter);
	}

	private void switchContactByDep(String department) {
		contactList.clear();
		contactList.add(getPersonalProfile());
		if (department.equals(getResources().getString(R.string.all_staff))) {
			for (int i = 0; i < allStaffList.size(); i++) {
				if (allStaffList.get(i).getDbId().equals(UserInfo.db_id))
					allStaffList.remove(i);
				if (nameIdMap != null && nameIdMap.size() > 0) {
					if (nameIdMap.containsKey(allStaffList.get(i).getDbId())) {
						allStaffList.get(i).setIsChecked(true);
					}
				}
			}
			contactList.addAll(allStaffList);
			switchalphaIndex(contactList);
			listAdapter.notifyDataSetChanged();
			contactListView.setSelection(0);
	  		
			return;
		}

		cursor = dbOpenHelper.select(2, department);
		int count = cursor.getCount();
		if (count > 0)
			for (int i = 0; i < count; i++) {
				cursor.moveToPosition(i);
				if (cursor.getString(14).equals(UserInfo.db_id))
					continue;
				FriendEntity entity = new FriendEntity(cursor.getString(1),
						cursor.getString(2), cursor.getString(3),
						cursor.getString(4), cursor.getString(5),
						cursor.getString(6), cursor.getString(7),
						cursor.getString(8), cursor.getString(9),
						cursor.getString(10), cursor.getString(11),
						cursor.getString(12), cursor.getString(13),
						cursor.getString(14), cursor.getString(15),
						cursor.getString(16));

				if (nameIdMap != null && nameIdMap.size() > 0) {
					if (nameIdMap.containsKey(entity.getDbId())) {
						entity.setIsChecked(true);
					}
				}
				contactList.add(entity);
			}
		switchalphaIndex(contactList);
		listAdapter.notifyDataSetChanged();
		contactListView.setSelection(0);

		cursor.close();
		dbOpenHelper.close();
	}

	public void switchalphaIndex(List<FriendEntity> list) {
		alphaIndexer = new HashMap<String, Integer>();
		sections = new String[list.size()];
		/*
		 * if (imageLoader == null) { imageLoader = new
		 * ImageLoaderOriginal(mContext); }
		 */
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) == null)
				continue;
			String currentStr = list.get(i).getAlpha();
			String previewStr = (i - 1) >= 0 ? list.get(i - 1).getAlpha() : " ";
			if (!previewStr.equals(currentStr)) {
				String name = list.get(i).getAlpha();
				alphaIndexer.put(name, i);
				sections[i] = name;
			}
		}
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

	OnItemClickListener listClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int arg2,
				long arg3) {

			int key = Integer.parseInt(view.getTag().toString());

			String[] str = popList.get(key).split(" ");
			String popMenuItem = str[0];
			/*
			 * Toast.makeText(SelectMemberActivity.this, popMenuItem,
			 * Toast.LENGTH_SHORT).show();
			 */
			allStaff.setText(popMenuItem);
			switchContactByDep(popMenuItem);
			menuListView.close();
		}
	};

	private class ListAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private List<FriendEntity> list;
		public ImageLoader imageLoader;
		public Context mContext;

		public ListAdapter(Context context, List<FriendEntity> list) {
			this.inflater = LayoutInflater.from(context);
			this.list = list;
			this.mContext = context;
			imageLoader = new ImageLoader(mContext);

			/*
			 * alphaIndexer = new HashMap<String, Integer>(); sections = new
			 * String[list.size()];
			 * 
			 * for (int i = 0; i < list.size(); i++) { String currentStr =
			 * list.get(i).getAlpha(); String previewStr = (i - 1) >= 0 ?
			 * list.get(i - 1).getAlpha() : " "; if
			 * (!previewStr.equals(currentStr)) { String name =
			 * list.get(i).getAlpha(); alphaIndexer.put(name, i); sections[i] =
			 * name; } }
			 */}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			// if (convertView == null) {
			convertView = inflater.inflate(
					R.layout.select_member_adapter_layout, null);
			holder = new ViewHolder();
			holder.alpha = (TextView) convertView.findViewById(R.id.alpha);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.department = (TextView) convertView
					.findViewById(R.id.department);
			holder.duty = (TextView) convertView.findViewById(R.id.duty);
			holder.check = (CheckBox) convertView.findViewById(R.id.cb_check);
			holder.photo = (ImageView) convertView.findViewById(R.id.img_photo);

			convertView.setTag(holder);
			/*
			 * } else { holder = (ViewHolder) convertView.getTag(); }
			 */
			if (list.get(position) == null)
				return convertView;
			holder.name.setText(list.get(position).getRealName());
			holder.department.setText(list.get(position).getDepartmentName());
			holder.department.setVisibility(View.INVISIBLE);
			holder.duty.setText(list.get(position).getDuty());
			holder.duty.setVisibility(View.INVISIBLE);
			holder.check.setChecked(list.get(position).getIsChecked());

			if (list.get(position).getDbId().equals(UserInfo.db_id)) {
				if (UserInfo.LocalphotoPath != null
						&& UserInfo.LocalphotoPath.length() != 0) {
					Log.e("test", "localpath :" + UserInfo.LocalphotoPath);

					BitmapFactory.Options opts = new BitmapFactory.Options();
					opts.inJustDecodeBounds = true;
					BitmapFactory.decodeFile(UserInfo.LocalphotoPath, opts);
					opts.inSampleSize = computeSampleSize(opts, -1, 128 * 128);
					opts.inJustDecodeBounds = false;
					try {
						Bitmap bmp = BitmapFactory.decodeFile(
								UserInfo.LocalphotoPath, opts);

						holder.photo.setBackgroundDrawable(new BitmapDrawable(
								bmp));

					} catch (OutOfMemoryError err) {

					}

				} else {
					Log.e("test", "localpath 2 :" + UserInfo.LocalphotoPath);

					if (UserInfo.sex.equals("0"))
						holder.photo.setImageDrawable(getResources()
								.getDrawable(R.drawable.ico_girl));
					else
						holder.photo.setImageDrawable(getResources()
								.getDrawable(R.drawable.ico_boy));
				}
			} else {
				if (list.get(position).getHeadUrl() != null
						&& list.get(position).getHeadUrl().length() > 0) {
					String headUrl = "http://" + UserInfo.downloadImgUrl
							+ list.get(position).getHeadUrl();
					imageLoader.DisplayImage(headUrl,
							SelectMemberActivity.instance, holder.photo);
				} else {
					if (list.get(position).getSex().equals("0"))
						holder.photo.setImageDrawable(getResources()
								.getDrawable(R.drawable.ico_girl));
					else if (list.get(position).getSex().equals("1"))
						holder.photo.setImageDrawable(getResources()
								.getDrawable(R.drawable.ico_boy));
					else
						holder.photo.setImageDrawable(getResources()
								.getDrawable(R.drawable.ico_group));
				}
			}

			String currentStr = list.get(position).getAlpha();
			String previewStr = (position - 1) >= 0 ? list.get(position - 1)
					.getAlpha() : " ";
			if (!previewStr.equals(currentStr)) {
				holder.alpha.setVisibility(View.VISIBLE);
				holder.alpha.setText(currentStr);
			} else {
				holder.alpha.setVisibility(View.GONE);
			}
			if (list.get(position).getSex().equals("3")
					|| list.get(position).getDbId().equals(UserInfo.db_id)) {
				holder.check.setVisibility(View.GONE);
			}
			return convertView;
		}

		private class ViewHolder {
			TextView alpha;
			TextView name;
			TextView department;
			TextView duty;
			ImageView photo;
			CheckBox check;
		}
	}

	private void initOverlay() {
		LayoutInflater inflater = LayoutInflater.from(this);
		overlay = (TextView) inflater.inflate(R.layout.overlay, null);
		overlay.setVisibility(View.INVISIBLE);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
						| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
				PixelFormat.TRANSLUCENT);
		mWindowManager = (WindowManager) this
				.getSystemService(Context.WINDOW_SERVICE);
		mWindowManager.addView(overlay, lp);
	}

	private class LetterListViewListener implements
			OnTouchingLetterChangedListener {
		@Override
		public void onTouchingLetterChanged(final String s) {
			if (alphaIndexer.get(s) != null) {
				int position = alphaIndexer.get(s);
				contactListView.setSelection(position);
				overlay.setText(sections[position]);
				overlay.getBackground().setAlpha(0);
				overlay.setVisibility(View.VISIBLE);
				handler.removeCallbacks(overlayThread);
				handler.postDelayed(overlayThread, 1500);
			}
		}
	}

	private class OverlayThread implements Runnable {
		@Override
		public void run() {
			overlay.setVisibility(View.GONE);
		}
	}

	public void closeActivity() {
		finish();
		overridePendingTransition(R.anim.in_from_up, R.anim.out_of_down);
	}

	@Override
	public void onStart() {
		super.onStart();
		if (UserInfo.isHomePressed) {
			ClientSocket client = new ClientSocket(this);
			UserInfo.isSendBroadCast = false;
			client.sendMessage(null, 0,
					StringWidthWeightRandom.getNextString(), null, null, null,
					null, null, null, null, null, true);
		}
		Log.i("test", "selectMemberActivity onStart");
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.i("test", "selectMemberActivity onResume");
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.i("test", "selectMemberActivity onPause");
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.i("test", "selectMemberActivity onStop");
	}

	@Override
	public void onDestroy() {
		mWindowManager.removeView(overlay);
		if (gbr != null) {
			unregisterReceiver(gbr);
			gbr = null;
		}
		super.onDestroy();
		Log.i("test", "selectMemberActivity onDestroy");
	}

	@Override
	public void onBackPressed() {
		closeActivity();
	}

	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
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
