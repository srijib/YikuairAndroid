package com.bestapp.yikuair.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

//import android.app.AlertDialog;
import android.widget.PopupWindow;
import android.widget.Button;
import android.widget.TextView;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.DialogInterface;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

import com.bestapp.yikuair.R;

public class MyDateTime extends PopupWindow {

	private static int START_YEAR = 2000, END_YEAR = 2020;
	// private final OnDateTimeSetListener mCallBack;
	private final Calendar mCalendar;
	// private Context context;
	private int curr_year, curr_month, curr_day, curr_hour, curr_minute;
	private Button button_cancel, button_ok;
	private static TextView title;
	String[] months_big = { "1", "3", "5", "7", "8", "10", "12" };
	String[] months_little = { "4", "6", "9", "11" };
	private static WheelView wv_year, wv_month, wv_day, wv_hours, wv_mins;
	final List<String> list_big, list_little;
	final int SCROLL_NUM = 5;
	final int POPWINDOW_HEIGHT = 260;
	final int TITLE_HEIGHT = 83;
	private int mScreenWidth;
	private int mScreenHeight;
	private String mSelectedTime;
	private String mComparedTime;
	private String mToday;
	private boolean isBeginTime;
	private int year, month, day, hour, minute;

	public MyDateTime(Context context, OnClickListener btnClick, String str,
			String selectedTime, String comparedTime, boolean isBTime) {
		super(context);
		// this.context = context;

		// mSelectedTime = selectedTime;
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		mScreenWidth = dm.widthPixels;
		mScreenHeight = dm.heightPixels;

		mCalendar = Calendar.getInstance();
		year = mCalendar.get(Calendar.YEAR);
		month = mCalendar.get(Calendar.MONTH);
		day = mCalendar.get(Calendar.DATE);
		hour = mCalendar.get(Calendar.HOUR_OF_DAY);
		minute = mCalendar.get(Calendar.MINUTE);
		mToday = String.valueOf(year) + "-" + String.valueOf(month) + "-"
				+ String.valueOf(day) + " " + String.valueOf(hour) + ":"
				+ String.valueOf(minute);

		isBeginTime = isBTime;
		if (isBeginTime)
			mComparedTime = getFormatedTime(mToday);
		else
			mComparedTime = getFormatedTime(comparedTime);

		Log.e("test", "selectedtime :" + selectedTime);
		String[] timeStr = selectedTime.split(" ");
		if (timeStr != null && timeStr.length > 2) {
			String[] dateStr = timeStr[0].split("-");
			if (dateStr != null && dateStr.length > 2) {
				year = Integer.valueOf(dateStr[0]);
				month = Integer.valueOf(dateStr[1]) - 1;
				Log.e("test", "month :" + month);
				day = Integer.valueOf(dateStr[2]);
			}
			String[] hmStr = timeStr[2].split(":");
			if (hmStr != null && hmStr.length > 1) {
				if (timeStr[1].equals("上午")) {
					hour = Integer.valueOf(hmStr[0]);
				} else {
					hour = Integer.valueOf(hmStr[0]) + 12;
				}
				minute = Integer.valueOf(hmStr[1]);
			}
		}

		curr_year = year;
		curr_month = month;
		curr_day = day;
		curr_hour = hour;
		curr_minute = minute;

		// this.END_YEAR = END_YEAR;
		// mCallBack = callBack;
		list_big = Arrays.asList(months_big);
		list_little = Arrays.asList(months_little);

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.time_layout, null);

		button_cancel = (Button) view.findViewById(R.id.button_cancel);
		button_ok = (Button) view.findViewById(R.id.button_ok);

		button_cancel.setOnClickListener(btnClick);
		button_ok.setOnClickListener(btnClick);
		title = (TextView) view.findViewById(R.id.TextDisplay);
		title.setText(str);
		// Log.i("test", "the button height is " + button_ok.getHeight());

		setContentView(view);
		this.setWidth(mScreenWidth);
		this.setHeight(LayoutParams.WRAP_CONTENT);

		int textSize = 0;
		textSize = adjustFontSize(mScreenWidth, mScreenHeight);
		title.setTextSize((float) (textSize * 0.4));
		button_ok.setWidth(textSize * 5);
		button_ok.setHeight(textSize);

		button_cancel.setWidth(textSize * 5);
		button_cancel.setHeight(textSize);

		wv_year = (WheelView) view.findViewById(R.id.year);
		wv_year.setAdapter(new NumericWheelAdapter(START_YEAR, END_YEAR));
		wv_year.setCyclic(true);
		wv_year.setLabel("年");
		// wv_year.setVisibleItems(SCROLL_NUM);
		wv_year.setCurrentItem(year - START_YEAR);

		wv_month = (WheelView) view.findViewById(R.id.month);
		wv_month.setAdapter(new NumericWheelAdapter(1, 12));
		wv_month.setCyclic(true);
		wv_month.setLabel("月");
		// wv_month.setVisibleItems(SCROLL_NUM);

		wv_month.setCurrentItem(month);

		wv_day = (WheelView) view.findViewById(R.id.day);
		wv_day.setCyclic(true);
		if (list_big.contains(String.valueOf(month + 1))) {
			wv_day.setAdapter(new NumericWheelAdapter(1, 31));
		} else if (list_little.contains(String.valueOf(month + 1))) {
			wv_day.setAdapter(new NumericWheelAdapter(1, 30));
		} else {
			if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
				wv_day.setAdapter(new NumericWheelAdapter(1, 29));
			else
				wv_day.setAdapter(new NumericWheelAdapter(1, 28));
		}
		wv_day.setLabel("日");
		// wv_day.setVisibleItems(SCROLL_NUM);
		wv_day.setCurrentItem(day - 1);

		wv_hours = (WheelView) view.findViewById(R.id.hour);
		wv_hours.setAdapter(new NumericWheelAdapter(0, 23));
		wv_hours.setCyclic(true);
		// wv_hours.setVisibleItems(SCROLL_NUM);
		wv_hours.setCurrentItem(hour);

		wv_mins = (WheelView) view.findViewById(R.id.mins);
		wv_mins.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));
		wv_mins.setCyclic(true);
		// wv_mins.setVisibleItems(SCROLL_NUM);
		wv_mins.setCurrentItem(minute);
		OnWheelChangedListener wheelListener_year = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				int year_num = newValue + START_YEAR;
				if (list_big
						.contains(String.valueOf(wv_month.getCurrentItem() + 1))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 31));
				} else if (list_little.contains(String.valueOf(wv_month
						.getCurrentItem() + 1))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 30));

					System.out.println(wv_month.getCurrentItem());

				} else {
					if ((year_num % 4 == 0 && year_num % 100 != 0)
							|| year_num % 400 == 0)
						wv_day.setAdapter(new NumericWheelAdapter(1, 29));
					else
						wv_day.setAdapter(new NumericWheelAdapter(1, 28));
				}

				// etHeight(350+TITLE_HEIGHT);
				Log.e("test", "the selected year is " + getYear());
				/*
				 * String tempDate = String.valueOf(getYear()) + "-" +
				 * String.valueOf(getMonth()) + "-" + String.valueOf(getDay()) +
				 * " " + String.valueOf(getHour()) + ":" +
				 * String.valueOf(getMin()); Log.e("test", "tempdate year:" +
				 * tempDate); if (compDate(tempDate) < 0) {
				 * wv_year.setCurrentItem(curr_year - START_YEAR); }
				 */}
		};
		OnWheelChangedListener wheelListener_month = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				int month_num = newValue + 1;
				if (list_big.contains(String.valueOf(month_num))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 31));
				} else if (list_little.contains(String.valueOf(month_num))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 30));
					if (getDay() == 31)
						wv_day.setCurrentItem(29);
				} else {

					if (((wv_year.getCurrentItem() + START_YEAR) % 4 == 0 && (wv_year
							.getCurrentItem() + START_YEAR) % 100 != 0)
							|| (wv_year.getCurrentItem() + START_YEAR) % 400 == 0) {

						wv_day.setAdapter(new NumericWheelAdapter(1, 29));
						if (getDay() == 31 || getDay() == 30) {
							wv_day.setCurrentItem(28);
						}
					} else {

						wv_day.setAdapter(new NumericWheelAdapter(1, 28));
						if (getDay() == 31 || getDay() == 30) {
							wv_day.setCurrentItem(27);
						}
					}
				}

				String tempDate = String.valueOf(getYear()) + "-"
						+ String.valueOf(getMonth()) + "-"
						+ String.valueOf(getDay()) + " "
						+ String.valueOf(getHour()) + ":"
						+ String.valueOf(getMin());
				/*
				 * Log.e("test", "tempdate month:" + tempDate); if
				 * (compDate(tempDate) < 0) {
				 * wv_month.setCurrentItem(curr_month); }
				 */Log.i("test", "the selected month is " + getMonth());
			}
		};

		OnWheelChangedListener wheelListener_day = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				String tempDate = String.valueOf(getYear()) + "-"
						+ String.valueOf(getMonth()) + "-"
						+ String.valueOf(getDay()) + " "
						+ String.valueOf(getHour()) + ":"
						+ String.valueOf(getMin());
				/*
				 * Log.e("test", "tempdate day:" + tempDate); if
				 * (compDate(tempDate) < 0) { wv_day.setCurrentItem(curr_day -
				 * 1); }
				 */Log.i("test", "the selected day is " + getDay());
			}
		};

		OnWheelChangedListener wheelListener_hour = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				String tempDate = String.valueOf(getYear()) + "-"
						+ String.valueOf(getMonth()) + "-"
						+ String.valueOf(getDay()) + " "
						+ String.valueOf(getHour()) + ":"
						+ String.valueOf(getMin());
				Log.e("test", "tempdate hour  :" + tempDate);
				// Log.e("test"," result :" + compDate(tempDate));

				Log.i("test", "the selected hour is " + getHour());
			}
		};

		OnWheelChangedListener wheelListener_min = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				String tempDate = String.valueOf(getYear()) + "-"
						+ String.valueOf(getMonth()) + "-"
						+ String.valueOf(getDay()) + " "
						+ String.valueOf(getHour()) + ":"
						+ String.valueOf(getMin());
				/*
				 * Log.e("test", "tempdate min:" + tempDate); if
				 * (compDate(tempDate) < 0) {
				 * wv_month.setCurrentItem(curr_minute); }
				 */
				Log.i("test", "the selected min is " + getMin());
			}
		};

		wv_year.addChangingListener(wheelListener_year);
		wv_month.addChangingListener(wheelListener_month);
		wv_day.addChangingListener(wheelListener_day);
		wv_hours.addChangingListener(wheelListener_hour);
		wv_mins.addChangingListener(wheelListener_min);
		wv_day.TEXT_SIZE = textSize;
		wv_hours.TEXT_SIZE = textSize;
		wv_mins.TEXT_SIZE = textSize;
		wv_month.TEXT_SIZE = textSize;
		wv_year.TEXT_SIZE = textSize;

	}

	public void onClick(DialogInterface dialog, int which) {
		curr_year = wv_year.getCurrentItem() + START_YEAR;
		curr_month = wv_month.getCurrentItem() + 1;
		curr_day = wv_day.getCurrentItem() + 1;
		curr_hour = wv_hours.getCurrentItem();
		curr_minute = wv_mins.getCurrentItem();
		/*
		 * if (mCallBack != null) { mCallBack.onDateTimeSet(curr_year,
		 * curr_month, curr_day, curr_hour, curr_minute); }
		 */
	}

	public void show() {
		// super.show();
	}

	public interface OnDateTimeSetListener {
		void onDateTimeSet(int year, int monthOfYear, int dayOfMonth, int hour,
				int minute);
	}

	public static int adjustFontSize(int mScreenWidth, int mScreenHeight) {
		if (mScreenWidth <= 240) { // 240X320 Â±èÂπï
			return 10;
		} else if (mScreenWidth <= 320) { // 320X480 Â±èÂπï
			return 14;
		} else if (mScreenWidth <= 480) { // 480X800 Êàñ 480X854 Â±èÂπï
			return 24;
		} else if (mScreenWidth <= 540) { // 540X960 Â±èÂπï
			return 26;
		} else if (mScreenWidth <= 800) { // 800X1280 Â±èÂπï
			return 35;
		} else { // Â§ß‰∫é 800X1280
			return 40;
		}
	}

	public String getFormatedTime(String time) {
		String[] timeStr = time.split(" ");
		String year = "";
		String month = "";
		String day = "";
		String hour = "";
		String min = "";
		if (timeStr != null && timeStr.length > 2) {
			String[] dateStr = timeStr[0].split("-");
			if (dateStr != null && dateStr.length > 2) {
				year = dateStr[0];
				month = dateStr[1];
				day = dateStr[2];
			}
			String[] hmStr = timeStr[2].split(":");
			if (hmStr != null && hmStr.length > 1) {
				if (timeStr[1].equals("上午")) {
					hour = hmStr[0];
				} else {
					hour = String.valueOf(Integer.valueOf(hmStr[0]) + 12);
				}
				min = hmStr[1];
			}
		}
		return year + "-" + month + "-" + day + " " + hour + ":" + min;
	}

	public int compDate(String date) {
		int day = 0;
		String s1 = " ";
		String s2 = " ";
		if (isBeginTime) {
			s1 = mToday;
			s2 = date;
		} else {
			s1 = mComparedTime;
			s2 = date;
		}
		Log.e("test", "s1 :" + s1);
		Log.e("test", "s2 :" + s2);
		try {
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
			Date date1 = new Date();
			Date date2 = new Date();
			date1 = sf.parse(s1);
			date2 = sf.parse(s2);
			day = (int) ((date2.getTime() - date1.getTime()) /*
															 * / 3600 / 24 /
															 * 1000
															 */);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.i("test", "dvalue :" + day);
		return day;
	}

	/*
	 * get the selected year
	 */
	public static int getYear() {
		return wv_year.getCurrentItem() + START_YEAR;
	}

	/*
	 * get the selected month
	 */
	public static int getMonth() {
		return wv_month.getCurrentItem() + 1;
	}

	/*
	 * get the selected day
	 */
	public static int getDay() {
		return wv_day.getCurrentItem() + 1;
	}

	/*
	 * get the selected hour
	 */
	public static int getHour() {
		return wv_hours.getCurrentItem();
	}

	/*
	 * get the selected minute
	 */
	public static int getMin() {
		return wv_mins.getCurrentItem();
	}

	public static String getTitle() {
		return title.getText().toString();
	}

	/*
	 * set the title
	 */
	public void setTitle(String str) {
		title.setText(str);
	}
}
