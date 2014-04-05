package com.bestapp.yikuair.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import android.annotation.SuppressLint;
import android.util.Log;

public class TaskListUtil {

	private static String requestURL;
	private static Thread getThread;
	private static String base64;

	public static void HttpRequest() {
		String userInfo = "id=" + UserInfo.id + "&username="
				+ UserInfo.user_email + "&password=" + UserInfo.cipher_password;
		String key = UserInfo.key;
		try {
			base64 = URLEncoder.encode(
					DataUtil.encodeECBAsBase64String(key, userInfo), "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		requestURL = UserInfo.getTaskListUrl + base64;

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
				//getContactFromJson(result);
				Log.i("test", "contact : " + result);
				content.close();
			}
		};
		getThread.start();
	}

	private static void getContactFromJson(String jsonStr) {

		try {
			JSONArray jsonObjs = new JSONObject(jsonStr).getJSONArray("data");
			for (int i = 0; i < jsonObjs.length(); i++) {
				JSONObject jsonObj = ((JSONObject) jsonObjs.opt(i));
				String phone = jsonObj.getString("phone");
				String id = jsonObj.getString("id");
				String signature = jsonObj.getString("signature");
				String duty = jsonObj.getString("duty");
				String cardId = jsonObj.getString("cardid");
				String realName = jsonObj.getString("realname");
				String mobile = jsonObj.getString("mobile");
				String headUrl = jsonObj.getString("headurl");
				String departmentName = jsonObj.getString("de_name");

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
