package com.bestapp.yikuair.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Base64;
import android.util.Log;

import com.bestapp.yikuair.fragments.ChatMsgEntity;
import com.bestapp.yikuair.fragments.MessageItemInfo;
import com.bestapp.yikuair.fragments.ScheduleItemInfo;

public class SharedPreferencesUtil {

	public Context mContext;
	private SharedPreferences chatSharedPre;

	public SharedPreferencesUtil(Context context) {
		mContext = context;
	}

	public void saveChatDate(String key, String date) {
		SharedPreferences shared = mContext.getSharedPreferences(key,
				Context.MODE_PRIVATE);
		Editor editor = shared.edit();
		editor.putString("date", date);
		editor.commit();
	}

	public void saveLoginInfo(String userName, String cipherPwd) {
		SharedPreferences shared = mContext.getSharedPreferences("loginInfo",
				Context.MODE_PRIVATE);
		Editor editor = shared.edit();
		editor.putString("name", userName);
		editor.putString("password", cipherPwd);
		editor.commit();
	}

	public String[] getLoginInfo() {
		String[] infoStr;
		SharedPreferences shared = mContext.getSharedPreferences("loginInfo",
				Context.MODE_PRIVATE);
		String name = shared.getString("name", "");
		String pwd = shared.getString("password", "");
		/*
		 * if (name != null && name.length() > 0 && pwd != null && pwd.length()
		 * > 0) {
		 */
		infoStr = new String[2];
		infoStr[0] = name;
		infoStr[1] = pwd;
		/*
		 * return infoStr; }
		 */
		return infoStr;
	}

	public String getScheduleCurrentDate(String key) {
		SharedPreferences shared = mContext.getSharedPreferences(key,
				Context.MODE_PRIVATE);
		String groupId = shared.getString("scheduleCurrentDate", "");
		return groupId;
	}

	public void saveScheduleCurrentDate(String key, String currentDate) {
		SharedPreferences shared = mContext.getSharedPreferences(key,
				Context.MODE_PRIVATE);
		Editor editor = shared.edit();
		editor.putString("scheduleCurrentDate", currentDate);
		editor.commit();
	}

	public String getScheduleToday(String key) {
		SharedPreferences shared = mContext.getSharedPreferences(key,
				Context.MODE_PRIVATE);
		String groupId = shared.getString("scheduleToday", "");
		return groupId;
	}

	public void saveScheduleToday(String key, String today) {
		SharedPreferences shared = mContext.getSharedPreferences(key,
				Context.MODE_PRIVATE);
		Editor editor = shared.edit();
		editor.putString("scheduleToday", today);
		editor.commit();
	}

	public String getChatDate(String key) {
		SharedPreferences shared = mContext.getSharedPreferences(key,
				Context.MODE_PRIVATE);
		String chatDate = shared.getString("date", null);
		return chatDate;
	}

	public void saveGroupInfo(String key, String groupId) {
		SharedPreferences shared = mContext.getSharedPreferences(key,
				Context.MODE_PRIVATE);
		Editor editor = shared.edit();
		editor.putString("groupId", groupId);
		editor.commit();
	}

	public String getGroupInfo(String key) {
		SharedPreferences shared = mContext.getSharedPreferences(key,
				Context.MODE_PRIVATE);
		String groupId = shared.getString("groupId", null);
		return groupId;
	}

	public void savePhotoUrl() {
		SharedPreferences shared = mContext.getSharedPreferences(
				"individual_photo", Context.MODE_PRIVATE);
		Editor editor = shared.edit();
		editor.putString("localphotopath", UserInfo.LocalphotoPath);
		editor.putString("headurl", UserInfo.headUrl);
		editor.commit();
	}

	public String[] getPhotoUrl() {
		SharedPreferences shared = mContext.getSharedPreferences(
				"individual_photo", Context.MODE_PRIVATE);
		String localPhotoPath = shared.getString("localphotopath", "");
		String headUrl = shared.getString("headurl", "");
		UserInfo.LocalphotoPath = localPhotoPath;
		UserInfo.headUrl = headUrl;
		String[] photoPath = new String[2];
		photoPath[0] = localPhotoPath;
		photoPath[1] = headUrl;
		return photoPath;
	}

	public void saveUserInfo() {
		SharedPreferences shared = mContext.getSharedPreferences("yikuair",
				Context.MODE_PRIVATE);
		Editor editor = shared.edit();

		editor.putString("userName", UserInfo.userName);
		editor.putBoolean("isHomePressed", UserInfo.isHomePressed);
		editor.putString("user_email", UserInfo.user_email);
		editor.putString("push_userId", UserInfo.push_userId);
		editor.putString("user_password", UserInfo.user_password);
		editor.putString("cipher_password", UserInfo.cipher_password);
		editor.putString("departmentName", UserInfo.departmentName);
		editor.putString("team", UserInfo.team);
		editor.putString("signature", UserInfo.signature);
		editor.putString("mobile", UserInfo.mobile);
		editor.putString("db_id", UserInfo.db_id);
		editor.putString("sex", UserInfo.sex);
		editor.putString("realName", UserInfo.realName);
		editor.putString("duty", UserInfo.duty);
		editor.putString("id", UserInfo.id);
		editor.putString("push_channelId", UserInfo.push_channelId);
		// editor.putString("photo_path", UserInfo.LocalphotoPath);
		// editor.putString("feedback_dbId", UserInfo.feedback_dbId);
		editor.putBoolean("isFirstLogin", UserInfo.isFirstLogin);
		editor.putBoolean("isDefaultCompanyNewsForMenu",
				UserInfo.isDefaultCompanyNewsForMenu);
		editor.putBoolean("isDefaultUserFeedbackForMenu",
				UserInfo.isDefaultUserFeedbackForMenu);
		editor.commit();
	}

	public void getUserInfo() {
		SharedPreferences shared = mContext.getSharedPreferences("yikuair",
				Context.MODE_WORLD_READABLE);
		UserInfo.userName = shared.getString("userName", "");
		UserInfo.user_email = shared.getString("user_email", "");
		UserInfo.push_userId = shared.getString("push_userId", "");
		UserInfo.user_password = shared.getString("user_password", "");
		UserInfo.cipher_password = shared.getString("cipher_password", "");
		UserInfo.departmentName = shared.getString("departmentName", "");
		UserInfo.team = shared.getString("team", "");
		UserInfo.signature = shared.getString("signature", "");
		UserInfo.mobile = shared.getString("mobile", "");
		UserInfo.db_id = shared.getString("db_id", "");
		UserInfo.push_channelId = shared.getString("push_channelId", "");
		UserInfo.sex = shared.getString("sex", "");
		UserInfo.realName = shared.getString("realName", "");
		UserInfo.duty = shared.getString("duty", "");
		UserInfo.id = shared.getString("id", "");
		// UserInfo.LocalphotoPath = shared.getString("photo_path", "");
		// UserInfo.feedback_dbId = shared.getString("feedback_dbId", "");
		// UserInfo.companyNews_dbId = shared.getString("company_news_dbId",
		// "");
		UserInfo.isFirstLogin = shared.getBoolean("isFirstLogin", true);
		UserInfo.isDefaultCompanyNewsForMenu = shared.getBoolean(
				"isDefaultCompanyNewsForMenu", true);
		UserInfo.isDefaultUserFeedbackForMenu = shared.getBoolean(
				"isDefaultUserFeedbackForMenu", true);
		UserInfo.isHomePressed = shared.getBoolean("isHomePressed", false);
	}

	@SuppressLint("NewApi")
	public List<ChatMsgEntity> readDataFromShared(String name) {
		List<ChatMsgEntity> mDataArrays = new ArrayList<ChatMsgEntity>();
		// if (chatSharedPre == null)
		chatSharedPre = mContext.getSharedPreferences(name,
				Context.MODE_PRIVATE);
		String listBase64 = chatSharedPre.getString("chatList", null);
		if (listBase64 != null && !listBase64.isEmpty()) {
			byte[] base64Bytes = Base64.decode(listBase64.getBytes(),
					Base64.DEFAULT);
			ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);
			ObjectInputStream ois = null;
			try {
				ois = new ObjectInputStream(bais);
				mDataArrays = (List<ChatMsgEntity>) ois.readObject();
				Log.e("test", "shared :" + mDataArrays.size());
				return mDataArrays;
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@SuppressLint("NewApi")
	public void saveDatatoShared(String name, List<ChatMsgEntity> mDataArrays) {
		if (mDataArrays == null)
			return;
		Log.e("test", "savedatatoshared................");
		Log.e("test", "size :::::::::" + mDataArrays.size());
		chatSharedPre = mContext.getSharedPreferences(name,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = chatSharedPre.edit();
		editor.clear();
		ByteArrayOutputStream baos = new ByteArrayOutputStream(3000);
		List<ChatMsgEntity> chatList = mDataArrays;
		if (chatList != null)
			if (chatList != null) {
				ObjectOutputStream oos = null;
				try {
					oos = new ObjectOutputStream(baos);
					oos.writeObject(chatList);
				} catch (IOException e) {
					e.printStackTrace();
				}
				String strList = new String(Base64.encode(baos.toByteArray(),
						Base64.DEFAULT));
				editor.putString("chatList", strList);
				editor.commit();
			}
	}

	@SuppressLint("NewApi")
	public List<MessageItemInfo> readMessageItemFromShared(String userId) {
		List<MessageItemInfo> mDataArrays = new ArrayList<MessageItemInfo>();
		// if (chatSharedPre == null)
		chatSharedPre = mContext.getSharedPreferences(userId,
				Context.MODE_PRIVATE);
		String listBase64 = chatSharedPre.getString("messageList", null);
		if (listBase64 != null && !listBase64.isEmpty()) {
			byte[] base64Bytes = Base64.decode(listBase64.getBytes(),
					Base64.DEFAULT);
			ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);
			ObjectInputStream ois = null;
			try {
				ois = new ObjectInputStream(bais);
				mDataArrays = (List<MessageItemInfo>) ois.readObject();
				return mDataArrays;
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@SuppressLint("NewApi")
	public LinkedList<HashMap<String, Boolean>> readBoolListFromShared(
			String userId) {
		LinkedList<HashMap<String, Boolean>> mDataArrays = new LinkedList<HashMap<String, Boolean>>();
		// if (chatSharedPre == null)
		chatSharedPre = mContext.getSharedPreferences(userId,
				Context.MODE_PRIVATE);
		String listBase64 = chatSharedPre.getString("boolList", null);
		if (listBase64 != null && !listBase64.isEmpty()) {
			byte[] base64Bytes = Base64.decode(listBase64.getBytes(),
					Base64.DEFAULT);
			ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);
			ObjectInputStream ois = null;
			try {
				ois = new ObjectInputStream(bais);
				mDataArrays = (LinkedList<HashMap<String, Boolean>>) ois
						.readObject();
				return mDataArrays;
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@SuppressLint("NewApi")
	public LinkedList<HashMap<String, List<ChatMsgEntity>>> readUserListFromShared(
			String userId) {
		LinkedList<HashMap<String, List<ChatMsgEntity>>> mDataArrays = new LinkedList<HashMap<String, List<ChatMsgEntity>>>();
		// if (chatSharedPre == null)
		chatSharedPre = mContext.getSharedPreferences(userId,
				Context.MODE_PRIVATE);
		String listBase64 = chatSharedPre.getString("userList", null);
		if (listBase64 != null && !listBase64.isEmpty()) {
			byte[] base64Bytes = Base64.decode(listBase64.getBytes(),
					Base64.DEFAULT);
			ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);
			ObjectInputStream ois = null;
			try {
				ois = new ObjectInputStream(bais);
				mDataArrays = (LinkedList<HashMap<String, List<ChatMsgEntity>>>) ois
						.readObject();
				return mDataArrays;
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@SuppressLint("NewApi")
	public void saveMessagetoShared(String userId,
			List<MessageItemInfo> messageList,
			List<HashMap<String, List<ChatMsgEntity>>> userList,
			List<HashMap<String, Boolean>> boolList) {
		if (messageList == null || userList == null || boolList == null)
			return;
		chatSharedPre = mContext.getSharedPreferences(userId,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = chatSharedPre.edit();
		editor.clear();
		List<MessageItemInfo> mMessageList = messageList;
		List<HashMap<String, List<ChatMsgEntity>>> mUserList = userList;
		List<HashMap<String, Boolean>> mBoolList = boolList;
		if (mMessageList != null && !mMessageList.isEmpty()) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream(3000);
			ObjectOutputStream oos = null;
			try {
				oos = new ObjectOutputStream(baos);
				oos.writeObject(mMessageList);
			} catch (IOException e) {
				e.printStackTrace();
			}
			String strList = new String(Base64.encode(baos.toByteArray(),
					Base64.DEFAULT));
			editor.putString("messageList", strList);
		}
		if (mUserList != null && !mUserList.isEmpty()) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream(3000);
			ObjectOutputStream oos = null;
			try {
				oos = new ObjectOutputStream(baos);
				oos.writeObject(mUserList);
			} catch (IOException e) {
				e.printStackTrace();
			}
			String strList = new String(Base64.encode(baos.toByteArray(),
					Base64.DEFAULT));
			editor.putString("userList", strList);
		}
		if (mBoolList != null && !mBoolList.isEmpty()) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream(3000);
			ObjectOutputStream oos = null;
			try {
				oos = new ObjectOutputStream(baos);
				oos.writeObject(mBoolList);
			} catch (IOException e) {
				e.printStackTrace();
			}
			String strList = new String(Base64.encode(baos.toByteArray(),
					Base64.DEFAULT));
			editor.putString("boolList", strList);
		}
		editor.commit();
	}

	@SuppressLint("NewApi")
	public void saveGroupMaptoShared(HashMap<String, String> map, String name) {
		if (map == null || map.size() == 0)
			return;
		chatSharedPre = mContext.getSharedPreferences(name,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = chatSharedPre.edit();
		editor.clear();
		ByteArrayOutputStream baos = new ByteArrayOutputStream(3000);
		HashMap<String, String> hashMap = map;
		if (hashMap != null && !hashMap.isEmpty()) {
			ObjectOutputStream oos = null;
			try {
				oos = new ObjectOutputStream(baos);
				oos.writeObject(hashMap);
			} catch (IOException e) { 
				e.printStackTrace();
			}
			String strMap = new String(Base64.encode(baos.toByteArray(),
					Base64.DEFAULT));
			editor.putString("groupMap", strMap);
			editor.commit();
		}
	}

	@SuppressLint("NewApi")
	public HashMap<String, String> readGroupMapFromShared(String name) {
		HashMap<String, String> mDataArrays = new HashMap<String, String>();
		chatSharedPre = mContext.getSharedPreferences(name,
				Context.MODE_PRIVATE);
		String mapBase64 = chatSharedPre.getString("groupMap", null);
		if (mapBase64 != null && !mapBase64.isEmpty()) {
			byte[] base64Bytes = Base64.decode(mapBase64.getBytes(),
					Base64.DEFAULT);
			ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);
			ObjectInputStream ois = null;
			try {
				ois = new ObjectInputStream(bais);
				mDataArrays = (HashMap<String, String>) ois.readObject();
				return mDataArrays;
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return mDataArrays;
	}

	@SuppressLint("NewApi")
	public void saveScheduletoShared(
			HashMap<String, List<ScheduleItemInfo>> map, String name) {
		if (map == null)
			return;
		chatSharedPre = mContext.getSharedPreferences(name,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = chatSharedPre.edit();
		editor.clear();
		ByteArrayOutputStream baos = new ByteArrayOutputStream(3000);
		HashMap<String, List<ScheduleItemInfo>> hashMap = map;
		if (hashMap != null && !hashMap.isEmpty()) {
			ObjectOutputStream oos = null;
			try {
				oos = new ObjectOutputStream(baos);
				oos.writeObject(hashMap);
			} catch (IOException e) {
				e.printStackTrace();
			}
			String strMap = new String(Base64.encode(baos.toByteArray(),
					Base64.DEFAULT));
			editor.putString("scheduleMap", strMap);
			editor.commit();
		}
	}

	@SuppressLint("NewApi")
	public HashMap<String, List<ScheduleItemInfo>> readScheduleFromShared(
			String name) {
		HashMap<String, List<ScheduleItemInfo>> mDataArrays = new HashMap<String, List<ScheduleItemInfo>>();
		// if (chatSharedPre == null)
		chatSharedPre = mContext.getSharedPreferences(name,
				Context.MODE_PRIVATE);
		String mapBase64 = chatSharedPre.getString("scheduleMap", null);
		if (mapBase64 != null && !mapBase64.isEmpty()) {
			byte[] base64Bytes = Base64.decode(mapBase64.getBytes(),
					Base64.DEFAULT);
			ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);
			ObjectInputStream ois = null;
			try {
				ois = new ObjectInputStream(bais);
				mDataArrays = (HashMap<String, List<ScheduleItemInfo>>) ois
						.readObject();
				return mDataArrays;
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@SuppressLint("NewApi")
	public void saveMenuDatatoShared(HashMap<Integer, List<ChatMsgEntity>> map,
			String name) {
		if (map == null)
			return;
		chatSharedPre = mContext.getSharedPreferences(name,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = chatSharedPre.edit();
		editor.clear();
		ByteArrayOutputStream baos = new ByteArrayOutputStream(3000);
		HashMap<Integer, List<ChatMsgEntity>> hashMap = map;
		if (hashMap != null && !hashMap.isEmpty()) {
			ObjectOutputStream oos = null;
			try {
				oos = new ObjectOutputStream(baos);
				oos.writeObject(hashMap);
			} catch (IOException e) {
				e.printStackTrace();
			}
			String strMap = new String(Base64.encode(baos.toByteArray(),
					Base64.DEFAULT));
			editor.putString("menuMap", strMap);
			editor.commit();
		}
	}

	@SuppressLint("NewApi")
	public HashMap<Integer, List<ChatMsgEntity>> readMenuDataFromShared(
			String name) {
		HashMap<Integer, List<ChatMsgEntity>> mDataArrays = new HashMap<Integer, List<ChatMsgEntity>>();
		// if (chatSharedPre == null)
		chatSharedPre = mContext.getSharedPreferences(name,
				Context.MODE_PRIVATE);
		String mapBase64 = chatSharedPre.getString("menuMap", null);
		if (mapBase64 != null && !mapBase64.isEmpty()) {
			byte[] base64Bytes = Base64.decode(mapBase64.getBytes(),
					Base64.DEFAULT);
			ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);
			ObjectInputStream ois = null;
			try {
				ois = new ObjectInputStream(bais);
				mDataArrays = (HashMap<Integer, List<ChatMsgEntity>>) ois
						.readObject();
				return mDataArrays;
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
