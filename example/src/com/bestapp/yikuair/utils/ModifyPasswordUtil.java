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
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ModifyPasswordUtil {

	private Context mContext;
	private static String requestURL;
	private static Thread getThread;
	private static String base64;
	private String newPassword;

	public ModifyPasswordUtil(Context context, String new_password) {
		mContext = context;
		newPassword = new_password;
	}

	public void ModifyPassword() {

		Log.i("test","username :" + UserInfo.id);
		Log.i("test","newpassword :" + newPassword);
		String userInfo = "username=" + UserInfo.id + "&password="
				+ UserInfo.cipher_password + "&newpassword=" + Md5Util.MD5(newPassword);

		String key = UserInfo.key;
		try {
			base64 = URLEncoder.encode(
					DataUtil.encodeECBAsBase64String(key, userInfo), "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		requestURL = UserInfo.modifyPasswordUrl + base64;

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
		Log.i("test","jsonStr :" + jsonStr);
		JSONObject resultCode;
		int code;
		try {
			resultCode = new JSONObject(jsonStr);
			code = resultCode.getInt("code");
			sendModifyPasswordResultBroadCast(code);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void sendModifyPasswordResultBroadCast(int code) {
		Log.e("test","sendmodifypasswordbraodcast............");
		Intent intent = new Intent();
		intent.setAction(MessageInfo.ModifyPasswordBroadcast);
		intent.putExtra("code", code);
		mContext.sendBroadcast(intent);
	}
}
