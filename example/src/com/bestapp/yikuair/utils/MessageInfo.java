package com.bestapp.yikuair.utils;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import android.os.Handler;
import android.text.format.Time;
import android.util.Log;

import com.bestapp.yikuair.fragments.ChatMsgEntity;
import com.bestapp.yikuair.fragments.MessageItemInfo;
import com.bestapp.yikuair.fragments.ScheduleItemInfo;
import com.bestapp.yikuair.utils.FriendEntity;
import com.bestapp.yikuair.utils.MessageInfo;

public class MessageInfo {
	public static List<Map<String, Object>> userMap = new ArrayList<Map<String, Object>>();
	public static List<MessageItemInfo> MessageList = new ArrayList<MessageItemInfo>();
	public static List<ChatMsgEntity> messageEntityList = new ArrayList<ChatMsgEntity>();
	public static List<ChatMsgEntity> matchMessageEntityList = new ArrayList<ChatMsgEntity>();
	public static HashMap<String, String> groupMap = new HashMap<String, String>();
	public static List<ChatMsgEntity> menuCompanyNewsList = new ArrayList<ChatMsgEntity>();
	public static List<ChatMsgEntity> menuFeedbackList = new ArrayList<ChatMsgEntity>();
	public static List<FriendEntity> groupList = new ArrayList<FriendEntity>();
	public static List<ChatMsgEntity> OfficeAccountList = new ArrayList<ChatMsgEntity>();
	public static List<AccountInfomation> OfficeAccountList_MINE = new ArrayList<AccountInfomation>();
	public static final String LoginResultBroadCast = "com.yikuai.login.result";
	public static final String HeadModifieyBroadCastName = "com.yikuair.head.modifiey";
	public static final String MessageBroadCastName = "com.yikuair.fragments.message";
	public static final String ScheduleBroadCastName = "com.yikuair.fragments.schedule";
	public static final String ContactBroadCastName = "com.yikuair.fragments.phonebook";
	public static final String BarginBroadCastName = "com.yikuair.fragments.bargin";
	public static final String FriendBroadCastName = "com.yikuair.fragments.friends";
	public static final String MessageItemBroadcast = "com.yikuair.message.Item";
	public static final String ModifyPasswordBroadcast = "com.yikuair.modifypassword";
	public static final String ImageBroadcast = "com.yikuair.image";
	public static final String FindResultBroadCast = "com.yikuai.find.result";
	public static final String GroupBroadCastName = "com.yikuair.group";
	public static final String RecreateResultBroadCast = "com.yikuair.recreate";
	public static final String GroupInfoResultBroadCast = "com.yikuair.group.info";
	public static final String ScheduleResultBroadCast = "com.yikuair.schedule.info";
	public static final String FriendMessageBroadCastName = "com.yikuair.fragments.friend.message";
	public static final String ScheduleDelResultBroadCast = "com.yikuair.schedule.del.info";

	public static final int TEXT = 1;
	public static final int PICTURE = 2;
	public static final int VOICE = 3;
	public static final int SCHEDULE = 4;
	public static final int PIC_TEXT = 7;
	public static final int INFO_WEB = 8;
	public static final int LOCATION = 22;

	public static final int COMPANY_NEWS = 5;
	public static final int USER_FEEDBACK = 6;
	public static final int SEND_ARRIVAL = 0;
	public static final int SEND_READED = 1;
	public static final int RECEIVE_MESSAGE = 2;
	public static final int SEND_MESSAGE = 3;
	public static final int SCHEDULE_CHECK = 4;
	public static final int GROUP_MODIFY = 5;

	public static final int TASK = 0;
	public static final int MEETING = 1;
	public static final int OTHER = 2;
	public static final int TODOITEM = 3;
	public static final int TASKTYPE_TASK = 2;
	public static final int TASKTYPE_MEETING = 1;
	public static final int TASKTYPE_OTHER = 3;
	public static boolean isChatting = false;
	public static boolean isNewMember = false;
	public static final int INDIVIDUAL = 0;
	public static final int GROUP = 1;
	public static final int OFFICEACCOUNT = 4;
	
	public static int unReadedMessageCount = 0;
	public static String strToday;

	public static LinkedHashMap<String, String> nameIdMap = new LinkedHashMap<String, String>();// for
	// schedule
	public static List<ScheduleItemInfo> scheduleList = new ArrayList<ScheduleItemInfo>();
	public static HashMap<String, List<ScheduleItemInfo>> map = new HashMap<String, List<ScheduleItemInfo>>();

	private static String mYear;
	private static String mMonth;
	private static String mDay;
	private static String mWay;

	public static String getTime() {
		Time t = new Time();
		String min = null, noon;
		t.setToNow();
		int hour = t.hour;
		String tempHour = "";
		int minute = t.minute;
		if (minute < 10) {
			min = "0" + String.valueOf(minute);
		} else
			min = String.valueOf(minute);
		if (hour < 13) {
			tempHour = String.valueOf(hour);
			noon = "上午";
		} else {
			tempHour = String.valueOf(hour - 12);
			noon = "下午";
		}
		if (Integer.valueOf(tempHour) < 10) {
			tempHour = String.valueOf(0) + tempHour;
		}
		String time = noon + " " + tempHour + ":" + min;
		return time;
	}

	public static String getChatTime() {
		Time t = new Time();
		String min = "";
		t.setToNow();
		int hour = t.hour;
		String tempHour = String.valueOf(hour);
		int minute = t.minute;
		if (minute < 10) {
			min = "0" + String.valueOf(minute);
		} else
			min = String.valueOf(minute);

		String time = tempHour + ":" + min;
		return time;
	}

	public static String getScheduleToday() {
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return sDateFormat.format(new java.util.Date());
	}

	public static String getTaskDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
		return sdf.format(new Date());
	}

	public static String getDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日");
		return sdf.format(new Date());
	}

	public static String getChattingDate() {
		final Calendar c = Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		mYear = String.valueOf(c.get(Calendar.YEAR)); // 获取当前年份
		mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
		mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
		mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
		if ("1".equals(mWay)) {
			mWay = "日";
		} else if ("2".equals(mWay)) {
			mWay = "一";
		} else if ("3".equals(mWay)) {
			mWay = "二";
		} else if ("4".equals(mWay)) {
			mWay = "三";
		} else if ("5".equals(mWay)) {
			mWay = "四";
		} else if ("6".equals(mWay)) {
			mWay = "五";
		} else if ("7".equals(mWay)) {
			mWay = "六";
		}
		return mYear + "-" + mMonth + "-" + mDay + " " + "周" + mWay;
	}

	public static String getChatDate() {
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return sDateFormat.format(new java.util.Date());
	}

	public static String getScheduleDate() {
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy年MM月dd日 hh:mm");
		return sDateFormat.format(new java.util.Date());
	}

	public static String StringToLong(String str) {
		if (str == null)
			return "";
		String resultStr = str;
		String[] temp = str.split(" ");
		if (temp != null && temp.length > 2) {
			if (temp[1].equals("上午")) {
				resultStr = temp[0] + " " + temp[2];
			} else if (temp[1].equals("下午")) {
				String[] tempTime = temp[2].split(":");
				if (tempTime.length > 1) {
					tempTime[0] = String
							.valueOf(Integer.valueOf(tempTime[0]) + 12);
				}
				resultStr = temp[0] + " " + tempTime[0] + ":" + tempTime[1];
			}
		}
		// Log.i("test", "result time  :" + resultStr);

		Log.e("test", "resultStr........ :" + resultStr);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");

		Date dt2 = null;
		try {
			if (temp.length > 1)
				dt2 = sdf.parse(resultStr);
			else
				dt2 = sdf2.parse(resultStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		long lTime = dt2.getTime();
		return String.valueOf(lTime);
	}

	public static String getScheduleDate(boolean isDefault, String date) {
		if (date == null)
			return null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		final Calendar c = Calendar.getInstance();
		String str = "";
		try {
			c.setTime(format.parse(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		// c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		mYear = String.valueOf(c.get(Calendar.YEAR));
		mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);
		mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
		mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
		if ("1".equals(mWay)) {
			mWay = "天";
		} else if ("2".equals(mWay)) {
			mWay = "一";
		} else if ("3".equals(mWay)) {
			mWay = "二";
		} else if ("4".equals(mWay)) {
			mWay = "三";
		} else if ("5".equals(mWay)) {
			mWay = "四";
		} else if ("6".equals(mWay)) {
			mWay = "五";
		} else if ("7".equals(mWay)) {
			mWay = "六";
		}
		int day = Integer.valueOf(mDay);
		String resultDay = "";
		String[] resultDate = date.split("-");
		if (day < 10) {
			resultDay = "0" + String.valueOf(day);
		} else {
			resultDay = String.valueOf(day);
		}
		if (isDefault)
			str = "星期" + mWay + " " + resultDate[1] + "-" + resultDate[2]/*
																		 * mMonth
																		 * + "-"
																		 * +
																		 * resultDay
																		 */;
		else
			str = "星期" + mWay + " " + mYear + "-" + mMonth + "-" + resultDay;
		return str;
	}

	public static String formatTime(String date) {
		String forTime = MessageInfo.getTime();
		if (date != null) {
			String[] tStr = date.split(" ");
			if (tStr != null && tStr.length > 2) {
				return tStr[1] + tStr[2];
			}
		}
		return forTime;
	}

	public static String formatDate(String date, int type) {
		String forDate = MessageInfo.getTaskDate() + " "
				+ MessageInfo.getTime();

		if (date != null) {
			String[] bStr = date.split(" ");
			if (bStr != null && bStr.length > 2) {
				String[] str = bStr[0].split("-");
				if (type == MessageInfo.TASK) {
					if (str != null && str.length > 2) {
						bStr[0] = str[0] + "年" + str[1] + "月" + str[2] + "日";
						date = bStr[0] + " " + bStr[1] + " " + bStr[2];
						Log.i("test", "date : " + date);
						return date;
					}
				} else {
					String weekDay = getWeek(bStr[0]);
					if (str != null && str.length > 2) {
						String monthDay = str[1] + "-" + str[2];
						date = weekDay + " " + monthDay;
						return date;
					}
				}
			}
		}
		return forDate;
	}

	public static String getWeek(String pTime) {
		String Week = "星期";
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(format.parse(pTime));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (c.get(Calendar.DAY_OF_WEEK) == 1) {
			Week += "日";
		}
		if (c.get(Calendar.DAY_OF_WEEK) == 2) {
			Week += "一";
		}
		if (c.get(Calendar.DAY_OF_WEEK) == 3) {
			Week += "二";
		}
		if (c.get(Calendar.DAY_OF_WEEK) == 4) {
			Week += "三";
		}
		if (c.get(Calendar.DAY_OF_WEEK) == 5) {
			Week += "四";
		}
		if (c.get(Calendar.DAY_OF_WEEK) == 6) {
			Week += "五";
		}
		if (c.get(Calendar.DAY_OF_WEEK) == 7) {
			Week += "六";
		}
		return Week;
	}

	public static String formatMessageItemTime(String str) {

		String formatStr = "";
		if (str != null) {
			String[] tempStr = str.split(":");
			if (tempStr != null && tempStr.length > 1) {
				if (Integer.valueOf(tempStr[0]) < 12) {
					formatStr = "上午" + str;
				} else {
					String tempHour = tempStr[0];
					tempStr[0] = String.valueOf(Integer.valueOf(tempHour) - 12);
					formatStr = "下午" + tempStr[0] + ":" + tempStr[1];
				}
			}
			return formatStr;
		}
		return "";
	}

	public static String parseTime(String time) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		String tmptime = sdf.format(new Date(Long.parseLong(time)));
		String[] str = tmptime.split(":");
		if (str != null && str.length > 1) {
			if (Integer.valueOf(str[0]) < 10) {
				str[0] = String.valueOf(Integer.valueOf(str[0]));
			}
		}
		return str[0] + ":" + str[1];
	}

	public static String parseDate(String date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat result = new SimpleDateFormat("yyyy年MM月dd日");
		String week = getWeek(sdf.format(new Date(Long.parseLong(date))));
		return result.format(new Date(Long.parseLong(date))) + week;
	}

	public static String parseScheduleDate(String longDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
		String tmptime = sdf.format(new Date(Long.parseLong(longDate)));
		Log.i("test", "schedule parsed date :" + tmptime);
		return tmptime;
	}

	public static String parseMessageFullTime(String longDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String tmptime = sdf.format(new Date(Long.parseLong(longDate)));
		return tmptime;
	}

	public static String getMessageFullTime() {
		SimpleDateFormat sDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		return sDateFormat.format(new java.util.Date());
	}

	public static Date stringToDate(String dateString) {
		if (dateString == null || dateString.equals(""))
			dateString = getMessageFullTime();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		Date dateValue = null;
		Log.e("test", "dateString :" + dateString);
		try {
			dateValue = simpleDateFormat.parse(dateString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dateValue;
	}

	public static String getScheduleAllDayDate(String date) {
		if (date == null)
			return null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
		final Calendar c = Calendar.getInstance();
		String str = "";
		try {
			c.setTime(format.parse(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		mYear = String.valueOf(c.get(Calendar.YEAR));
		mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);
		mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
		mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
		if ("1".equals(mWay)) {
			mWay = "天";
		} else if ("2".equals(mWay)) {
			mWay = "一";
		} else if ("3".equals(mWay)) {
			mWay = "二";
		} else if ("4".equals(mWay)) {
			mWay = "三";
		} else if ("5".equals(mWay)) {
			mWay = "四";
		} else if ("6".equals(mWay)) {
			mWay = "五";
		} else if ("7".equals(mWay)) {
			mWay = "六";
		}
		int day = Integer.valueOf(mDay);
		String resultDay = "";
		String[] resultDate = date.split("-");
		if (day < 10) {
			resultDay = "0" + String.valueOf(day);
		} else {
			resultDay = String.valueOf(day);
		}

		str = "星期" + mWay + " " + resultDay + "日" + mMonth + "月";
		return str;
	}

}