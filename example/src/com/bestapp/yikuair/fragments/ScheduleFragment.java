package com.bestapp.yikuair.fragments;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.bestapp.yikuair.R;
import com.bestapp.yikuair.utils.CalendarGridViewAdapter;
import com.bestapp.yikuair.utils.MessageInfo;
import com.bestapp.yikuair.utils.SharedPreferencesUtil;
import com.bestapp.yikuair.utils.StringWidthWeightRandom;
import com.bestapp.yikuair.utils.UserInfo;
import com.bestapp.yikuair.utils.Utils;

public class ScheduleFragment extends Fragment {

	public static ScheduleFragment instance = null;
	private ViewPager mCalPager;
	private static ImageView mTabImg1, mTabImg2, mTabImg3, mTabImg4, mTabImg5,
			mTabImg6, mTabImg7;
	private static LinearLayout ll_default_schedule, schedule_list;
	private static TextView today, day;
	private static String strToday;
	private static int mWay;
	private ImageButton rightBtn;
	private static Calendar currentCal;
	private static String currentDate;
	private static ListView listView;
	private static ScheduleAdapter lstAdapter;
	private static List<ScheduleItemInfo> scheduleList = new ArrayList<ScheduleItemInfo>();
	private SharedPreferencesUtil scheduleSharedPre;
	private final static String ScheduleSharedPreName = "yikuair_schedule";
	private static int num = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.schedule, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.i("test", "scheudlefragment onActivityCreated....................");

		// TaskListUtil.HttpRequest();// request task list from server
		scheduleSharedPre = new SharedPreferencesUtil(getActivity());
		getLocalSchedule();

		initView();
		updateListView(currentDate);
	}

	public void onStart() {
		Log.i("test", "scheudlefragment onstart....................");
		if (UserInfo.isHomePressed) {
			ClientSocket client = new ClientSocket(getActivity());
			UserInfo.isSendBroadCast = false;

			UserInfo.isHomePressed = false;

			client.sendMessage(null, 0,
					StringWidthWeightRandom.getNextString(), null, null, null,
					null, null, null, null, null, true);
		}
		updateSchedule(MessageInfo.scheduleList);
		MessageInfo.scheduleList.clear();
		super.onStart();
	}

	public void onResume() {
		Log.i("test", "schedulefragment onResume....................");
		super.onResume();
	}

	public void getLocalSchedule() {
		MessageInfo.map.clear();
		if (scheduleSharedPre.readScheduleFromShared(UserInfo.db_id
				+ ScheduleSharedPreName) != null)
			MessageInfo.map = scheduleSharedPre
					.readScheduleFromShared(UserInfo.db_id
							+ ScheduleSharedPreName);
	}

	public void initView() {
		Log.i("test", "scheudlefragment initView().......................");
		rightBtn = (ImageButton) getActivity().findViewById(
				R.id.schedule_right_btn);
		rightBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),
						ScheduleAddActivity.class);
				// Log.e("test","currentDate :" + currentDate);
				intent.putExtra("currentDate", currentDate);
				startActivityForResult(intent, 1);
				getActivity().overridePendingTransition(R.anim.in_from_right,
						R.anim.out_of_left);
			}
		});

		schedule_list = (LinearLayout) getActivity().findViewById(
				R.id.ll_schedule_list);
		ll_default_schedule = (LinearLayout) getActivity().findViewById(
				R.id.ll_default_schedule);
		listView = (ListView) getActivity().findViewById(R.id.schedule_list);
		lstAdapter = new ScheduleAdapter(getActivity(), scheduleList);
		listView.setAdapter(lstAdapter);

		mCalPager = (ViewPager) getActivity().findViewById(R.id.cal_viewpager);

		today = (TextView) getActivity().findViewById(R.id.schedule_date);
		day = (TextView) getActivity().findViewById(R.id.schedule_day);

		mTabImg1 = (ImageView) getActivity().findViewById(R.id.img_tab_monday);
		mTabImg2 = (ImageView) getActivity().findViewById(R.id.img_tab_tuesday);
		mTabImg3 = (ImageView) getActivity().findViewById(
				R.id.img_tab_wednesday);
		mTabImg4 = (ImageView) getActivity()
				.findViewById(R.id.img_tab_thursday);
		mTabImg5 = (ImageView) getActivity().findViewById(R.id.img_tab_friday);
		mTabImg6 = (ImageView) getActivity()
				.findViewById(R.id.img_tab_saturday);
		mTabImg7 = (ImageView) getActivity().findViewById(R.id.img_tab_sunday);

		today.setText(getCurrentDate());

		setTabStatus(Integer.valueOf(getWeek()));

		Log.e("test", "today : " + strToday);
		currentDate = strToday;

		final ScreenSlidePagerAdapter screenSlidePagerAdapter = new ScreenSlidePagerAdapter(
				getActivity().getSupportFragmentManager());
		mCalPager.setAdapter(screenSlidePagerAdapter);
		mCalPager.setCurrentItem(500);
		mCalPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	public void updateSchedule(List<ScheduleItemInfo> list) {
		Log.i("test", "count:" + list.size());
		// Log.i("test", "map.size :" + MessageInfo.map.size());
		String btime = strToday;
		String etime = strToday;
		SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
      
		for (int k = 0; k < list.size(); k++) {
			if (list.get(k).getIsDel()) {
				// delete item by taskId
				Set set = MessageInfo.map.keySet();
				Iterator it = set.iterator();
				while (it.hasNext()) {
					String key = (String) it.next();
					List<ScheduleItemInfo> lst = (List<ScheduleItemInfo>) MessageInfo.map
							.get(key);
					for (int j = 0; j < lst.size(); j++) {
						if (lst.get(j).getTaskId() != null
								&& lst.get(j).getTaskId()
										.equals(list.get(k).getTaskId())) {
							MessageInfo.map.get(key).remove(j); 
							break;
						}
					}
				}
			}
		}
		
		for (int i = 0; i < list.size(); i++) {
			if(list.get(i).getScheduleBeginTime() == null || list.get(i).getScheduleEndTime() == null)
				continue;
			String[] bStr = list.get(i).getScheduleBeginTime().split(" ");
			String[] eStr = list.get(i).getScheduleEndTime().split(" ");
			Log.i("test", "bstr :" + list.get(i).getScheduleBeginTime());
			Log.i("test", "estr :" + list.get(i).getScheduleEndTime());
			int dValue = 0;
			if (bStr != null && bStr.length > 1) {
				btime = bStr[0];
			}
			if (eStr != null && eStr.length > 1) {
				etime = eStr[0];
			}
			try {
				dValue = compDate(btime, etime);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.i("test", "btime :" + btime);
			Log.i("test", "etime :" + etime);
			Log.i("test", " dvalue :" + dValue);

			String[] tempDate = new String[Math.abs(dValue) + 1];
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			ParsePosition pos = new ParsePosition(0);
			Date strtodate = formatter.parse(btime, pos);
			Calendar cal = Calendar.getInstance();
			cal.setTime(strtodate);
			int dayCount = Math.abs(dValue) + 1;

			for (int l = 0; l < dayCount; l++) {
				tempDate[l] = date.format(cal.getTime());
				cal.roll(Calendar.DAY_OF_YEAR, true);
			}

			for (int k = 0; k < dayCount; k++) {
				String tempBeginTime = tempDate[k];
				Log.i("test", "tempbeginTime :" + tempBeginTime);
				if (MessageInfo.map.containsKey(tempBeginTime)) {
					if (list.get(i).getIsDel()) {
						Log.i("test", "delete........................");
						for (int j = 0; j < MessageInfo.map.get(tempBeginTime)
								.size(); j++) {
							if (MessageInfo.map.get(tempBeginTime).get(j)
									.getType() == MessageInfo.TODOITEM)
								continue;
							Log.i("test", "map.getitemid :"
									+ MessageInfo.map.get(tempBeginTime).get(j)
											.getItemId());
							Log.i("test", "map.gettitle :"
									+ MessageInfo.map.get(tempBeginTime).get(j)
											.getScheduleContent());
							Log.i("test", "list.getitemid :"
									+ list.get(i).getItemId());
							if (MessageInfo.map.get(tempBeginTime).get(j)
									.getItemId()
									.equals(list.get(i).getItemId())) {
								MessageInfo.map.get(tempBeginTime).remove(j);
							}
						}
					}
					MessageInfo.map.get(tempBeginTime).add(list.get(i));
				} else {
					List<ScheduleItemInfo> lst = new ArrayList<ScheduleItemInfo>();
					lst.add(list.get(i));
					MessageInfo.map.put(tempBeginTime, lst);
				}
			}
		}
		updateListView(currentDate);
	}

	public static void updateListView(String date) {
		Log.i("test", "update date :" + date);
		scheduleList.clear();
		List<ScheduleItemInfo> taskList = new ArrayList<ScheduleItemInfo>();
		if (MessageInfo.map != null && MessageInfo.map.get(date) != null) {

			schedule_list.setVisibility(View.VISIBLE);
			ll_default_schedule.setVisibility(View.GONE);
			for (int i = 0; i < MessageInfo.map.get(date).size(); i++) {
				if (MessageInfo.map.get(date).get(i).getType() == MessageInfo.MEETING
						|| MessageInfo.map.get(date).get(i).getType() == MessageInfo.OTHER) {
					scheduleList.add(MessageInfo.map.get(date).get(i));
				} else if (MessageInfo.map.get(date).get(i).getType() == MessageInfo.TASK) {
					taskList.add(MessageInfo.map.get(date).get(i));
				}
			}
			if (taskList.size() > 0) {
				ScheduleItemInfo item = new ScheduleItemInfo();
				item.setType(MessageInfo.TODOITEM);
				scheduleList.add(item);
			}
			for (int j = 0; j < taskList.size(); j++) {
				scheduleList.add(taskList.get(j));
			}
			MessageInfo.map.get(date).clear();
			MessageInfo.map.get(date).addAll(scheduleList);

			lstAdapter.notifyDataSetChanged();
		} else {
			schedule_list.setVisibility(View.GONE);
			ll_default_schedule.setVisibility(View.VISIBLE);

		}
	}

	public static int getWeek() {
		final Calendar c = Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		/*
		 * mYear = String.valueOf(c.get(Calendar.YEAR)); // mMonth =
		 * String.valueOf(c.get(Calendar.MONTH) + 1); mDay =
		 * String.valueOf(c.get(Calendar.DAY_OF_MONTH));
		 */
		mWay = c.get(Calendar.DAY_OF_WEEK);

		if (mWay == 1)
			return 6;
		else
			return mWay - 2;
	}

	public class MyOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageSelected(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			setTabStatus(0);
		}
	}

	private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

		public ScreenSlidePagerAdapter(FragmentManager fm) {
			super(getChildFragmentManager());
		}

		@Override
		public Fragment getItem(int position) {
			Log.e("test", "position :" + position);
			return CalendarFragment.create(position);
		}

		@Override
		public int getCount() {
			return 1000;
		}
	}

	private String getCurrentDate() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月");
		SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");

		Date curDate = new Date(System.currentTimeMillis());
		String str = formatter.format(curDate);
		strToday = date.format(curDate);
		Log.i("test", "today date is :" + strToday);
		return str;
	}

	public static void setTabStatus(int index) {
		switch (index) {
		case 0:
			mTabImg1.setVisibility(View.VISIBLE);
			mTabImg2.setVisibility(View.INVISIBLE);
			mTabImg3.setVisibility(View.INVISIBLE);
			mTabImg4.setVisibility(View.INVISIBLE);
			mTabImg5.setVisibility(View.INVISIBLE);
			mTabImg6.setVisibility(View.INVISIBLE);
			mTabImg7.setVisibility(View.INVISIBLE);
			break;
		case 1:
			mTabImg1.setVisibility(View.INVISIBLE);
			mTabImg2.setVisibility(View.VISIBLE);
			mTabImg3.setVisibility(View.INVISIBLE);
			mTabImg4.setVisibility(View.INVISIBLE);
			mTabImg5.setVisibility(View.INVISIBLE);
			mTabImg6.setVisibility(View.INVISIBLE);
			mTabImg7.setVisibility(View.INVISIBLE);
			break;
		case 2:
			mTabImg1.setVisibility(View.INVISIBLE);
			mTabImg2.setVisibility(View.INVISIBLE);
			mTabImg3.setVisibility(View.VISIBLE);
			mTabImg4.setVisibility(View.INVISIBLE);
			mTabImg5.setVisibility(View.INVISIBLE);
			mTabImg6.setVisibility(View.INVISIBLE);
			mTabImg7.setVisibility(View.INVISIBLE);
			break;
		case 3:
			mTabImg1.setVisibility(View.INVISIBLE);
			mTabImg2.setVisibility(View.INVISIBLE);
			mTabImg3.setVisibility(View.INVISIBLE);
			mTabImg4.setVisibility(View.VISIBLE);
			mTabImg5.setVisibility(View.INVISIBLE);
			mTabImg6.setVisibility(View.INVISIBLE);
			mTabImg7.setVisibility(View.INVISIBLE);
			break;
		case 4:
			mTabImg1.setVisibility(View.INVISIBLE);
			mTabImg2.setVisibility(View.INVISIBLE);
			mTabImg3.setVisibility(View.INVISIBLE);
			mTabImg4.setVisibility(View.INVISIBLE);
			mTabImg5.setVisibility(View.VISIBLE);
			mTabImg6.setVisibility(View.INVISIBLE);
			mTabImg7.setVisibility(View.INVISIBLE);
			break;
		case 5:
			mTabImg1.setVisibility(View.INVISIBLE);
			mTabImg2.setVisibility(View.INVISIBLE);
			mTabImg3.setVisibility(View.INVISIBLE);
			mTabImg4.setVisibility(View.INVISIBLE);
			mTabImg5.setVisibility(View.INVISIBLE);
			mTabImg6.setVisibility(View.VISIBLE);
			mTabImg7.setVisibility(View.INVISIBLE);
			break;
		case 6:
			mTabImg1.setVisibility(View.INVISIBLE);
			mTabImg2.setVisibility(View.INVISIBLE);
			mTabImg3.setVisibility(View.INVISIBLE);
			mTabImg4.setVisibility(View.INVISIBLE);
			mTabImg5.setVisibility(View.INVISIBLE);
			mTabImg6.setVisibility(View.INVISIBLE);
			mTabImg7.setVisibility(View.VISIBLE);
			break;
		}
	}

	public static int compDate(String s1, String s2) {
		try {
			int day;
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
			Date date1 = new Date();
			Date date2 = new Date();

			date1 = sf.parse(s1);
			date2 = sf.parse(s2);
			day = (int) ((date2.getTime() - date1.getTime()) / 3600 / 24 / 1000);
			Log.i("test", "day :" + day);
			return day;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public static class CalendarFragment extends Fragment {
		public static final String ARG_PAGE = "page";
		private int mPageNumber;
		private Calendar mCalendar;
		private CalendarGridViewAdapter calendarGridViewAdapter;
		private static final int count = 3;
		private static String prevDate = strToday;

		public static Fragment create(int pageNumber) {
			Log.e("test", "pageNumber 1:" + pageNumber);
			CalendarFragment fragment = new CalendarFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_PAGE, pageNumber);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			mPageNumber = getArguments().getInt(ARG_PAGE);
			if (mPageNumber < 2)
				return;// test for bug..............................
			mCalendar = Utils.getSelectCalendar(mPageNumber);
			SharedPreferencesUtil shared = new SharedPreferencesUtil(
					getActivity());
			/***********************************/
			shared.getScheduleToday("schedule_today" + UserInfo.db_id);
			shared.getScheduleCurrentDate("schedule_current" + UserInfo.db_id);

			List<String> mStrs = new ArrayList<String>();
			Log.e("test", "pager number  : " + mPageNumber);
			mStrs = getWeekDate(mCalendar);
			calendarGridViewAdapter = new CalendarGridViewAdapter(
					getActivity(), mStrs);
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			GridView calendarView = new GridView(getActivity());
			initGridView(calendarView, calendarGridViewAdapter);
			calendarView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					TextView tv = (TextView) view.findViewById(R.id.tv_gv_date);
					currentDate = tv.getText().toString();
					setDate(strToday, currentDate);
					setTabStatus(position);
					updateListView(currentDate);
				}
			});
			return calendarView;
		}

		private void initGridView(GridView gridView, BaseAdapter adapter) {
			gridView = setGirdView(gridView);
			gridView.setAdapter(adapter);
		}

		public List<String> getWeekDate(Calendar cal) {
			List<String> list = new ArrayList<String>();
			SimpleDateFormat day = new SimpleDateFormat("dd");
			SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
			String dateFormat = date.format(cal.getTime());
			String currentDate = null;
			Calendar tempCal = null;
			// Log.i("test","Date : " + date.format(cal.getTime()));
			Log.e("test", "num :" + num);
			if (++num > count) {
				try {
					Log.i("test", "strtoday :" + strToday);
					Log.i("test", "prevDate :" + prevDate);
					Log.i("test", "dateFormat : " + dateFormat);
					if (compDate(prevDate, dateFormat) < 0) {
						tempCal = Utils.setNextViewItem(cal);
					} else {
						tempCal = Utils.setPrevViewItem(cal);
					}
					currentDate = date.format(tempCal.getTime());
					prevDate = currentDate;
					currentCal = tempCal;
					updateListView(currentDate);
					setDate(strToday, currentDate);
					Log.i("test", "current Date : " + currentDate);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			for (int i = 0; i < 7; i++) {
				list.add(day.format(cal.getTime()) + " "
						+ date.format(cal.getTime()));
				cal.roll(Calendar.DAY_OF_YEAR, true);
			}
			return list;
		}

		public void setDate(String fromDate, String toDate) {
			int value = 0;
			String date;
			try {
				value = compDate(fromDate, toDate);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Log.i("test", "value: " + value);
			if (value == 0) {
				date = getResources().getString(R.string.today);
			} else if (value < 0) {
				int abs = Math.abs(value);
				if (abs > 0 && abs < 7) {
					date = String.valueOf(abs % 7)
							+ getResources().getString(R.string.before_day);
				} else if (abs > 6 && abs < 30) {
					date = String.valueOf(abs / 7 == 0 ? 1 : (int) Math
							.floor((double) abs / 7))
							+ getResources().getString(R.string.before_week);
				} else if (abs >= 30 && abs < 360) {
					date = String.valueOf((int) Math.floor((double) abs / 30))
							+ getResources().getString(R.string.before_month);
				} else {
					date = String.valueOf((int) Math
							.floor((double) value / 360))
							+ getResources().getString(R.string.before_year);
				}
			} else {
				if (value <= 6 - getWeek()) {
					date = String.valueOf(value % 7)
							+ getResources().getString(R.string.after_day);
				} else if (value > 6 - getWeek() && value < 30) {
					date = String.valueOf(value / 7 == 0 ? 1 : (int) Math
							.ceil((double) value / 7))
							+ getResources().getString(R.string.after_week);
				} else if (value >= 30 && value < 360) {
					date = String
							.valueOf((int) Math.floor((double) value / 30))
							+ getResources().getString(R.string.after_month);
				} else {
					date = String.valueOf((int) Math
							.floor((double) value / 360))
							+ getResources().getString(R.string.after_year);
				}
			}
			day.setText(date);
			String[] str = toDate.split("-");
			String newDate = str[0] + getResources().getString(R.string.year)
					+ str[1] + getResources().getString(R.string.month);
			today.setText(newDate);
		}

		@SuppressWarnings("deprecation")
		private GridView setGirdView(GridView gridView) {
			gridView.setNumColumns(7);
			gridView.setGravity(Gravity.CENTER_VERTICAL);
			// gridView.setVerticalSpacing(1);
			// gridView.setHorizontalSpacing(1);
			gridView.setFocusable(true);
			gridView.setFocusableInTouchMode(true);
			gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));

			WindowManager windowManager = getActivity().getWindowManager();
			Display display = windowManager.getDefaultDisplay();
			int i = display.getWidth() / 7;
			int j = display.getWidth() - (i * 7);
			int x = j / 2;
			gridView.setPadding(x, 0, 0, 0);
			return gridView;
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.i("test", "schedule onPause");
	}

	@Override
	public void onStop() {
		super.onStop();
		num = 0;
		SharedPreferencesUtil shared = new SharedPreferencesUtil(getActivity());
		shared.saveScheduleToday("schedule_today" + UserInfo.db_id, strToday);
		shared.saveScheduleCurrentDate("schedule_current" + UserInfo.db_id,
				currentDate);
		Log.i("test", "schedulefragment onStop");
	}

	@Override
	public void onDestroy() {
		Log.i("test", "schedulefragment onDestroy");
		scheduleSharedPre.saveScheduletoShared(MessageInfo.map, UserInfo.db_id
				+ ScheduleSharedPreName);
		/*
		 * Log.i("test", "map size: " + MessageInfo.map.size()); Set set =
		 * MessageInfo.map.keySet(); Iterator it = set.iterator(); while
		 * (it.hasNext()) { String key = (String) it.next();
		 * List<ScheduleItemInfo> value = (List<ScheduleItemInfo>)
		 * MessageInfo.map .get(key); for (int j = 0; j < value.size(); j++) {
		 * String content = value.get(j).getScheduleContent(); String time =
		 * value.get(j).getScheduleBeginTime(); String name =
		 * value.get(j).getSponsorName(); String[] memberId =
		 * value.get(j).getMemberName(); String[] memberName =
		 * value.get(j).getMemberName(); String id = value.get(j).getId();
		 * 
		 * Log.i("test","time :" + time); String[] strTime = time.split(" ");
		 * if(strTime != null && strTime.length > 2){ time = strTime[2]; }
		 * 
		 * MessageItemInfo messageItem = new MessageItemInfo();
		 * messageItem.setContent(content); messageItem.setName(name);
		 * messageItem.setTime(time); messageItem.setMemberId(memberId);
		 * messageItem.setMemberName(memberName); messageItem.setId(id);
		 * MessageInfo.MessageList.add(messageItem); } Log.i("test",
		 * "messagelist size :" + MessageInfo.MessageList.size()); }
		 */

		super.onDestroy();
	}
}
