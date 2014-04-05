package com.bestapp.yikuair.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.bestapp.yikuair.database.DBOpenHelper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

public class FindMemberUtil {

	private Context mContext;
	private static String requestURL;
	private static Thread getThread;
	private static String base64;
	public static List<FriendEntity> contactList = new ArrayList<FriendEntity>();
	public static HashMap<String, List<FriendEntity>> departmentContactMap = new HashMap<String, List<FriendEntity>>();
	public FriendEntity entity;

	public FindMemberUtil(Context context) {
		mContext = context;
		entity = new FriendEntity();
	}

	public void requestFindResult(String id) {

		String userInfo = "username=" + UserInfo.id + "&password="
				+ UserInfo.cipher_password + "&id=" + id;
		String key = UserInfo.key;
		try {
			base64 = URLEncoder.encode(
					DataUtil.encodeECBAsBase64String(key, userInfo), "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		requestURL = UserInfo.getSearchResultUrl + base64;

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
				byte[] buffer = new byte[1024];
				String result = "";
				int length = -1;
				StringBuilder sb = new StringBuilder();
				while ((length = content.read(buffer)) != -1) {
					String tempStr = new String(buffer, 0, length,
							Charset.forName("UTF-8"));
					sb.append(tempStr);
				}
				result = sb.toString();
				result = result.equals("") ? "nothing" : result;
				getResultFromJson(result);
				content.close();
			}
		};
		getThread.start();
	}

	private void getResultFromJson(String jsonStr) {

		try {
			Log.i("test", "jsonStr : " + jsonStr);
			JSONObject resultCode = new JSONObject(jsonStr);
			int code = resultCode.getInt("code");
			if(code == 200){			
				JSONArray jsonContacts = new JSONObject(jsonStr)
						.getJSONArray("data");			
				Log.e("test", "length : " + jsonContacts.length());				
				for (int i = 0; i < jsonContacts.length(); i++) {
					JSONObject jsonObj = ((JSONObject) jsonContacts.opt(i));
					String phone = jsonObj.getString("phone");
					String id = jsonObj.getString("id");
					String signature = jsonObj.getString("signature");
					String duty = jsonObj.getString("duty");
					String realName = jsonObj.getString("realname");
					String mobile = jsonObj.getString("mobile");
					String headUrl = jsonObj.getString("headurl");
					String departmentName = jsonObj.getString("de_name");
					String email = jsonObj.getString("email");
					String companyId = jsonObj.getString("com_id");
					String alphaName = jsonObj.getString("pinyin");

					String[] strArray = new String[] { id, realName, phone, mobile,
							email, duty, departmentName, headUrl, signature,
							companyId, alphaName };
					entity.setPhone(phone);
					entity.setID(id);
					entity.setSignature(signature);
					entity.setDuty(duty);
					entity.setDepartmentName(departmentName);
					entity.setRealName(realName);
					entity.setMobile(mobile);
					entity.setHeadUrl(headUrl);
					entity.setEmail(email);
					entity.setCompanyId(companyId);
					entity.setAlpher(alphaName);
				}	
			}

			sendFindResultBroadCast(entity, code, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void sendFindResultBroadCast(FriendEntity entity, int code,
			int token) {
		Intent intent = new Intent();
		intent.setAction(MessageInfo.FindResultBroadCast);
		Bundle bundle = new Bundle();
		bundle.putSerializable("friendEntity", entity);
		intent.putExtra("code", code);
		intent.putExtras(bundle);
		mContext.sendBroadcast(intent);
	}
}
