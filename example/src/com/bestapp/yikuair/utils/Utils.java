package com.bestapp.yikuair.utils;

import java.util.Calendar;
import java.util.Date;

public class Utils {
	public static String LeftPad_Tow_Zero(int str) {
		java.text.DecimalFormat format = new java.text.DecimalFormat("00");
		return format.format(str);
	}

	public static Calendar getSelectCalendar(int mPageNumber) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());

		if (mPageNumber > 500) {
			for (int i = 0; i < mPageNumber - 500; i++) {
				calendar = setNextViewItem(calendar);
			}
		} else if (mPageNumber < 500) {
			for (int i = 0; i < 500 - mPageNumber; i++) {
				calendar = setPrevViewItem(calendar);
			}
		} else {
			calendar = setLocalViewItem(calendar);
		}
		return calendar;
	}

	// local week
	public static Calendar setLocalViewItem(Calendar calendar) {
		int count = 0;

		if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
			count = 8;
		else
			count = calendar.get(Calendar.DAY_OF_WEEK);
		calendar.add(Calendar.DAY_OF_WEEK, (2 - count));

		return calendar;
	}

	// last week
	public static Calendar setPrevViewItem(Calendar calendar) {

		Calendar cal = (Calendar) calendar.clone();
		int minuend = 0;
		if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
			minuend = -12;
		else
			minuend = -5;
		cal.add(Calendar.DAY_OF_WEEK,
				(minuend - calendar.get(Calendar.DAY_OF_WEEK)));

		return cal;
	}

	// next week
	public static Calendar setNextViewItem(Calendar calendar) {
		Calendar cal = (Calendar) calendar.clone();
		int count = 0;
		if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
			count = 8;
		else
			count = calendar.get(Calendar.DAY_OF_WEEK);
		cal.add(Calendar.DAY_OF_WEEK, (9 - count));
		return cal;
	}

}
