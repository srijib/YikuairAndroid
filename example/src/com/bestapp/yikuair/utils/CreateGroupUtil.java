package com.bestapp.yikuair.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bestapp.yikuair.database.DBOpenHelper;
import com.bestapp.yikuair.fragments.ChatActivity;
import com.bestapp.yikuair.fragments.ChatMsgEntity;
import com.bestapp.yikuair.fragments.MenuFragment;
import com.bestapp.yikuair.fragments.MessageFragment;
import com.bestapp.yikuair.fragments.PhoneBookFragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

public class CreateGroupUtil {

	private Context mContext;
	private static String requestURL;
	private static Thread getThread;
	private static String base64;
	private ChatMsgEntity entity;
	private String ids = "";
	private DBOpenHelper dbOpenHelper;
	private Cursor cursor;
	private SharedPreferencesUtil shared;
	private final static String YIKUAIR_GROUP = "yikuair_group";


	public CreateGroupUtil(Context context, ChatMsgEntity ent) {
		mContext = context;
		entity = ent;
		shared = new SharedPreferencesUtil(mContext);
	}

	public void getGroupMember() {

		String userInfo = "username=" + UserInfo.id + "&password="
				+ UserInfo.cipher_password + "&id=" + entity.getReceiverId();

		String key = UserInfo.key;
		try {
			base64 = URLEncoder.encode(
					DataUtil.encodeECBAsBase64String(key, userInfo), "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		requestURL = UserInfo.getGroupMemberUrl + base64;

		getThread = new Thread() {
			public void run() {
				HttpClient client = new DefaultHttpClient();
				HttpGet getMethod = new HttpGet(requestURL);
				Log.i("test", "url :" + requestURL);
				HttpResponse response = null;
				try {
					response = client.execute(getMethod);
					if (response != null) {
						StatusLine statusLine = response.getStatusLine();
						if (statusLine != null) {
							if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
								HttpEntity entity = response.getEntity();
								if (entity != null) {
									InputStream content = entity.getContent();
									handleEntity(content);
								}
							}
						}
					}
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					client.getConnectionManager().shutdown();
				}
			}

			@SuppressLint("NewApi")
			private void handleEntity(InputStream content) throws IOException {

				String result = "";
				BufferedReader in = null;
				in = new BufferedReader(new InputStreamReader(content));
				StringBuffer sb = new StringBuffer("");
				String line = "";
				String NL = System.getProperty("line.separator");

				while ((line = in.readLine()) != null) {
					sb.append(line + NL);
				}
				in.close();
				result = sb.toString();
				result = JsonFilter(result);
				getDataFromJson(result);
				content.close();
			}
		};
		getThread.start();
	}

	public static String JsonFilter(String jsonstr) {
		return jsonstr.substring(jsonstr.indexOf("{")).replace("\r\n", "\n");
	}

	private void getDataFromJson(String jsonStr) {
		Log.e("test", "jsonStr group:" + jsonStr);

		try {
			JSONArray jsonObjs = new JSONObject(jsonStr).getJSONArray("data");
			Log.i("test,", "member count is " + jsonObjs.length());
			int memberCount = jsonObjs.length();
			if (jsonObjs.length() > 0) {
				for (int i = 0; i < memberCount; i++) {
					JSONObject jsonObj = ((JSONObject) jsonObjs.opt(i));
					if (i == memberCount - 1) {
						ids += jsonObj.getString("user_id");
					} else {
						ids += jsonObj.getString("user_id") + "、";
					}
				} 
				Log.e("test", "creategroup ids :" + ids);

				if (MessageInfo.groupMap.containsKey(entity.getReceiverId())) {
					entity.setIsChangeGroupInfo(true);
				}
				MessageInfo.groupMap.put(entity.getReceiverId(), ids);
				
				if (shared == null)
					shared = new SharedPreferencesUtil(mContext);
				shared.saveGroupMaptoShared(MessageInfo.groupMap, UserInfo.db_id + "_"
						+ YIKUAIR_GROUP);
				
				sendMessageBroadcast(entity);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public String getNameFromDB(String dbId) {
		if (dbId == null)
			return "";
		dbOpenHelper = new DBOpenHelper(mContext);
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		String name = "";
		cursor = db.rawQuery("select * from contactsTable where dbid=?",
				new String[] { dbId });
		if (cursor.moveToFirst()) {
			name = cursor.getString(2);
		}
		dbOpenHelper.close();
		cursor.close();
		return name;
	}

	private void sendMessageBroadcast(ChatMsgEntity entity) {
		if (MenuFragment.instance == null && MessageFragment.instance == null
				&& ChatActivity.instance == null) {
			Log.e("test","save to group list ");
			entity.setIsAdd(true);
			MessageInfo.messageEntityList.add(entity);
		} else {
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putSerializable("message", entity);
			intent.setAction(MessageInfo.MessageBroadCastName);
			intent.putExtras(bundle);
			Log.e("test", "group send messagebroadcast");
			mContext.sendBroadcast(intent);
		}

		String names = "";
		String[] strId = ids.split("、");
		for (int j = 0; j < strId.length; j++) {
			if (j == strId.length - 1)
				names += getNameFromDB(strId[j]);
			else
				names += getNameFromDB(strId[j]) + "、";
		}
	//	if (PhoneBookFragment.instance == null) {
			if (MessageInfo.groupList != null
					&& MessageInfo.groupList.size() > 0) {
				for (int i = 0; i < MessageInfo.groupList.size(); i++) {
					if (MessageInfo.groupList.get(i).getGroupId()
							.equals(entity.getReceiverId())) {
						MessageInfo.groupList.get(i)
								.setDbId(
										MessageInfo.groupMap.get(entity
												.getReceiverId()));
						MessageInfo.groupList.get(i).setRealName(names);
					}
				}
			}
		//} 
			/*else {
			if (PhoneBookFragment.contactList != null
					&& PhoneBookFragment.contactList.size() > 0) {
				for (int i = 0; i < PhoneBookFragment.contactList.size(); i++) {
					if (PhoneBookFragment.contactList.get(i).getGroupId() != null
							&& PhoneBookFragment.contactList.get(i)
									.getGroupId()
									.equals(entity.getReceiverId())) {
						PhoneBookFragment.contactList.remove(i);
						PhoneBookFragment.listAdapter.notifyDataSetChanged();
						break;
					}
				}
			}
		}
*/	}
}
