package com.bestapp.yikuair.fragments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
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

public class PhoneBookFragment extends Fragment {

	public ContactMenuView menuListView = null;
	private ImageButton menuBtn;
	private TextView allStaff;
	private EditText editText;
	private int menuWidth;
	private int marginLeftWidth;
	private boolean hasMeasured = false;
	private RelativeLayout titleBar;
	private LetterListView letterListView;
	private ArrayList<String> popList = new ArrayList<String>();
	private HashMap<String, Integer> alphaIndexer;
	private ListView contactListView;
	public static ListAdapter listAdapter;
	private TextView overlay;
	private Handler handler;
	private String[] sections;
	private OverlayThread overlayThread;
	public static List<FriendEntity> contactList = new ArrayList<FriendEntity>();
	private List<FriendEntity> allStaffList = new ArrayList<FriendEntity>();
	private List<FriendEntity> searchResultList = new ArrayList<FriendEntity>();
	private Cursor cursor;
	private WindowManager windowManager;
	private DBOpenHelper dbOpenHelper;
	private ImageButton findBtn;
	private RelativeLayout layout;
	private LinearLayout menuTitle;
	private LinearLayout letterList;
	private ImageView iv_clear;
	public static PhoneBookFragment instance = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		instance = this;
		return inflater.inflate(R.layout.main_address_list, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		dbOpenHelper = new DBOpenHelper(getActivity());

		layout = (RelativeLayout) getActivity()
				.findViewById(R.id.rl_phone_book);
		layout.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				hideSoftInput(v.getWindowToken());
				return false;
			}
		});

		iv_clear = (ImageView) getActivity()
				.findViewById(R.id.iv_clear_content);
		iv_clear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				editText.setText("");
				letterList.setVisibility(View.VISIBLE);
				iv_clear.setVisibility(View.GONE);
			}
		});

		letterList = (LinearLayout) getActivity().findViewById(
				R.id.ll_letterlist);

		editText = (EditText) getActivity().findViewById(R.id.et_search);
		editText.addTextChangedListener(mTextWatcher);
		letterListView = (LetterListView) getActivity().findViewById(
				R.id.phoneBookLetterList);
		letterListView
				.setOnTouchingLetterChangedListener(new LetterListViewListener());
		alphaIndexer = new HashMap<String, Integer>();
		handler = new Handler();
		overlayThread = new OverlayThread();
		contactListView = (ListView) getActivity().findViewById(
				R.id.phone_list_view);

		contactListView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				hideSoftInput(v.getWindowToken());
				return false;
			}
		});

		menuTitle = (LinearLayout) getActivity().findViewById(
				R.id.phonebook_title);
		menuTitle.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				showMenu();
				return false;
			}
		});

		menuBtn = (ImageButton) getActivity().findViewById(R.id.menu_btn);
		allStaff = (TextView) getActivity().findViewById(R.id.all_staff);

		titleBar = (RelativeLayout) getActivity().findViewById(
				R.id.addresslist_title);
		findBtn = (ImageButton) getActivity().findViewById(
				R.id.addresslist_right_btn);

		findBtn.setVisibility(View.GONE);

		// initPersonalInfo();
		initOverlay();
		initContactList();
		initPopMenu();

		findBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),
						FindMemberActivity.class);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.in_from_right,
						R.anim.out_of_left);
			}
		});

		contactListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (contactList.get(arg2).getDbId().equals(UserInfo.db_id)) {
					skipToPersonal(true, 0);
				} else {
					skipToPersonal(false, arg2);
				}
			}
		});
	}

	private void initView() {
		// update staff's information
		/*
		 * if (UserInfo.LocalphotoPath != null &&
		 * UserInfo.LocalphotoPath.length() != 0) { Log.e("test", "localpath :"
		 * + UserInfo.LocalphotoPath); Bitmap b =
		 * BitmapFactory.decodeFile(UserInfo.LocalphotoPath);
		 * individual_photo.setImageBitmap(b); } else { Log.e("test",
		 * "localpath 2 :" + UserInfo.LocalphotoPath);
		 * 
		 * if (UserInfo.sex.equals("0"))
		 * individual_photo.setImageDrawable(getResources().getDrawable(
		 * R.drawable.girl)); else
		 * individual_photo.setImageDrawable(getResources().getDrawable(
		 * R.drawable.boy)); }
		 */
	}

	private void skipToPersonal(boolean isIndividual, int arg2) {
		Intent intent = new Intent(getActivity(), PersonalProfileActivity.class);
		if (!isIndividual) {
			if (contactList.get(arg2).getSex().equals("3")) {
				Intent intent1 = new Intent(getActivity(), ChatActivity.class);
				intent1.putExtra("Id", contactList.get(arg2).getDbId());
				intent1.putExtra("name", contactList.get(arg2).getRealName());
				startActivityForResult(intent1, 1);
				getActivity().overridePendingTransition(R.anim.in_from_right,
						R.anim.out_of_left);
				return;
			} else {
				Bundle bundle = new Bundle();
				bundle.putSerializable("friendEntity", contactList.get(arg2));
				intent.putExtras(bundle);
			}
		} else {
			intent.putExtra("individualInfo", " ");
		}

		startActivity(intent);
		getActivity().overridePendingTransition(R.anim.in_from_right,
				R.anim.out_of_left);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null)
			return;
		Log.e("test", "phonebook activity result.....................");
		String content = data.getExtras().getString("content");
		String time = data.getExtras().getString("time");
		String userId = data.getExtras().getString("userId");
		String fullTime = data.getExtras().getString("fullTime");
		String name = data.getExtras().getString("names");

		LinkedList<MessageItemInfo> messageList = new LinkedList<MessageItemInfo>();
		LinkedList<HashMap<String, List<ChatMsgEntity>>> userList = new LinkedList<HashMap<String, List<ChatMsgEntity>>>();
		LinkedList<HashMap<String, Boolean>> boolList = new LinkedList<HashMap<String, Boolean>>();

		SharedPreferencesUtil shared = new SharedPreferencesUtil(getActivity());
		if (shared.readBoolListFromShared(UserInfo.db_id) != null) {
			boolList = shared.readBoolListFromShared(UserInfo.db_id);
		}

		if (shared.readUserListFromShared(UserInfo.db_id) != null) {
			userList = shared.readUserListFromShared(UserInfo.db_id);
		}

		if (shared.readMessageItemFromShared(UserInfo.db_id) != null) {
			messageList = (LinkedList<MessageItemInfo>) shared
					.readMessageItemFromShared(UserInfo.db_id);
		}

		for (int i = 0; i < messageList.size(); i++) {
			if (messageList.get(i).getId().equals(userId)) {
				if (content != null)
					messageList.get(i).setContent(content);
				messageList.get(i).setName(name);
				Log.e("test", "name :" + name);
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
					map.put(id, userList.get(j).get(id));
					mapbool.put(id, boolList.get(j).get(id));
					tempUserList.add(map);
					tempBoolList.add(mapbool);
					break;
				}
			}
		}
		userList = tempUserList;
		boolList = tempBoolList;

		shared.saveMessagetoShared(UserInfo.db_id, messageList, userList,
				boolList);
	}

	/*
	 * private OnClickListener btnClick = new OnClickListener() { public void
	 * onClick(View v) { switch (v.getId()) { case R.id.rl_individual:
	 * skipToPersonal(true, 0); break; } } };
	 */
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

	TextWatcher mTextWatcher = new TextWatcher() {
		private CharSequence inputContent;

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			inputContent = s;
			if (inputContent.length() == 0) {
				contactList.clear();
				contactList.addAll(allStaffList);
				iv_clear.setVisibility(View.GONE);
				letterList.setVisibility(View.VISIBLE);
				listAdapter.notifyDataSetChanged();
				return;
			}
			searchResultList.clear();
			for (int i = 0; i < allStaffList.size(); i++) {
				if (allStaffList.get(i).getSearchIndex().contains(inputContent)) {
					searchResultList.add(allStaffList.get(i));
				}
			}
			contactList.clear();
			contactList.addAll(searchResultList);
			letterList.setVisibility(View.GONE);
			iv_clear.setVisibility(View.VISIBLE);
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

	private void initPopMenu() {
		if (menuListView == null) {
			menuListView = new ContactMenuView(getActivity(), menuBtn);
		}
		menuListView.listview.setOnItemClickListener(listClickListener);
		menuListView.clear();

		for (int i = 0; i < popList.size(); i++) {
			if (popList.get(i).length() == 0)
				continue;
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
		try {
			SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
			cursor = db.rawQuery("select * from contactsTable where dbid=?",
					new String[] { UserInfo.db_id });
		}catch (Exception e){
			Toast.makeText(this.getActivity(), "通讯录正在更新，请稍候再试", Toast.LENGTH_LONG);
			this.onDetach();
		}

		Log.e("test", "userinfo.db_id:" + UserInfo.db_id);
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
		if (MessageInfo.groupList != null && !MessageInfo.groupList.isEmpty()) {
			for (int i = 0; i < MessageInfo.groupList.size(); i++)
				contactList.add(MessageInfo.groupList.get(i));
		}

		cursor = dbOpenHelper.select(0, null);
		int count = cursor.getCount();
		if (count > 0) {
			for (int i = 0; i < count; i++) {
				cursor.moveToPosition(i);
				if (cursor.getString(14).equals(UserInfo.db_id))
					continue;
				contactList.add(new FriendEntity(cursor.getString(1), cursor
						.getString(2), cursor.getString(3),
						cursor.getString(4), cursor.getString(5), cursor
								.getString(6), cursor.getString(7), cursor
								.getString(8), cursor.getString(9), cursor
								.getString(10), cursor.getString(11), cursor
								.getString(12), cursor.getString(13), cursor
								.getString(14), cursor.getString(15), cursor
								.getString(16)));
			}
		}

		cursor = dbOpenHelper.select(1, null);
		count = cursor.getCount();
		if (count > 0) {
			popList.add(getResources().getString(R.string.all_staff));
			for (int j = 0; j < count; j++) {
				cursor.moveToPosition(j);
				if (cursor.getString(1) == null
						|| cursor.getString(1).length() == 1)
					continue;
				popList.add(cursor.getString(1));
			}
		}

		cursor.close();
		dbOpenHelper.close();

		// Collections.sort(contactList, new PinyinComparator());

		/*
		 * for (int j = 0; j < contactList.size(); j++) { Log.e("test",
		 * "pinyin :" + contactList.get(j).getPinyin()); }
		 */
		allStaffList.addAll(contactList);
		switchalphaIndex(contactList);

		listAdapter = new ListAdapter(getActivity(), contactList);
		contactListView.setAdapter(listAdapter);
	}

	private void switchContactByDep(String department) {
		contactList.clear();
		contactList.add(getPersonalProfile());
		if (!department.equals(getActivity().getResources().getString(
				R.string.all_staff))
				&& MessageInfo.groupList != null
				&& !MessageInfo.groupList.isEmpty()) {
			for (int i = 0; i < MessageInfo.groupList.size(); i++)
				contactList.add(MessageInfo.groupList.get(i));
		}

		if (department.equals(getResources().getString(R.string.all_staff))) {
			for (int i = 0; i < allStaffList.size(); i++) {
				if (allStaffList.get(i).getDbId().equals(UserInfo.db_id))
					allStaffList.remove(i);
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
				contactList.add(new FriendEntity(cursor.getString(1), cursor
						.getString(2), cursor.getString(3),
						cursor.getString(4), cursor.getString(5), cursor
								.getString(6), cursor.getString(7), cursor
								.getString(8), cursor.getString(9), cursor
								.getString(10), cursor.getString(11), cursor
								.getString(12), cursor.getString(13), cursor
								.getString(14), cursor.getString(15), cursor
								.getString(16)));
			}
		// Collections.sort(contactList, new PinyinComparator());

		switchalphaIndex(contactList);
		listAdapter.notifyDataSetChanged();
		contactListView.setSelection(0);
		cursor.close();
		dbOpenHelper.close();
	}

	public void switchalphaIndex(List<FriendEntity> list) {
		alphaIndexer = new HashMap<String, Integer>();
		sections = new String[list.size()];

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

			allStaff.setText(popMenuItem);
			switchContactByDep(popMenuItem);
			menuListView.close();
		}
	};

	public class ListAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private List<FriendEntity> list;
		// private ImageLoaderOriginal imageLoader = null;
		private Context mContext;

		public ImageLoader imageLoader;

		public ListAdapter(Context context, List<FriendEntity> list) {
			this.mContext = context;
			this.inflater = LayoutInflater.from(context);
			this.list = list;
			imageLoader = new ImageLoader(getActivity());

			/*
			 * alphaIndexer = new HashMap<String, Integer>(); sections = new
			 * String[list.size()];
			 * 
			 * if (imageLoader == null) { imageLoader = new
			 * ImageLoaderOriginal(mContext); }
			 * 
			 * 
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
			ViewHolder holder = new ViewHolder();

			convertView = inflater.inflate(R.layout.phone_list_item, null);
			holder.alpha = (TextView) convertView.findViewById(R.id.alpha);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.department = (TextView) convertView
					.findViewById(R.id.department);
			holder.duty = (TextView) convertView.findViewById(R.id.duty);

			holder.duty.setVisibility(View.INVISIBLE);

			holder.photo = (ImageView) convertView.findViewById(R.id.img_photo);
			convertView.setTag(holder);

			if (list.get(position) == null)
				return convertView;

			holder.name.setText(list.get(position).getRealName());
			if (list.get(position).getDepartmentName() != null
					&& list.get(position).getDepartmentName().length() > 0) {
				holder.department.setText(list.get(position).getTeam());
			} else {
				holder.department.setText(list.get(position)
						.getDepartmentName());
			}

			holder.duty.setText(list.get(position).getDuty());

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
					imageLoader.DisplayImage(headUrl, getActivity(),
							holder.photo);
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
			if (position == 0) {
				holder.alpha.setText(getResources().getString(
						R.string.individual));
				holder.alpha.setVisibility(View.VISIBLE);
			}
			if (!previewStr.equals(currentStr)) {
				holder.alpha.setVisibility(View.VISIBLE);
				holder.alpha.setText(currentStr);
			} else {
				holder.alpha.setVisibility(View.GONE);
			}
			return convertView;
		}

		private class ViewHolder {
			TextView alpha;
			TextView name;
			TextView department;
			TextView duty;
			ImageView photo;
		}
	}

	private void initOverlay() {
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		overlay = (TextView) inflater.inflate(R.layout.overlay, null);
		overlay.setVisibility(View.INVISIBLE);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
						| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
				PixelFormat.TRANSLUCENT);
		windowManager = (WindowManager) getActivity().getSystemService(
				Context.WINDOW_SERVICE);
		windowManager.addView(overlay, lp);
	}

	private class LetterListViewListener implements
			OnTouchingLetterChangedListener {
		public void onTouchingLetterChanged(final String s) {
			if (alphaIndexer.get(s) != null) {
				int position = alphaIndexer.get(s);
				Log.e("test", "position:" + position);
				contactListView.setSelection(position);
				overlay.setText(sections[position]);
				overlay.getBackground().setAlpha(0);
				overlay.setVisibility(View.VISIBLE);
				handler.removeCallbacks(overlayThread);
				handler.postDelayed(overlayThread, 1500);
			}
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

	@Override
	public void onStart() {
		Log.i("test", "phonebook onstart....................");
		if (UserInfo.isHomePressed) {
			ClientSocket client = new ClientSocket(getActivity());
			UserInfo.isSendBroadCast = false;

			UserInfo.isHomePressed = false;

			client.sendMessage(null, 0,
					StringWidthWeightRandom.getNextString(), null, null, null,
					null, null, null, null, null, true);
		}
		initView();
		super.onStart();
	}

	@Override
	public void onResume() {
		Log.i("test", "phone onResume....................");
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.i("test", "phonebook onPause");
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.i("test", "phonebook onStop");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		instance = null;
		contactList.clear();
		windowManager.removeView(overlay);
		Log.i("test", "phonebook onDestroy");
	}

	private class OverlayThread implements Runnable {
		@Override
		public void run() {
			overlay.setVisibility(View.GONE);
		}
	}
}
