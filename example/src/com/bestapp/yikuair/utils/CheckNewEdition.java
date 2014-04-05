package com.bestapp.yikuair.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.bestapp.yikuair.AppstartActivity;
import com.bestapp.yikuair.LoginActivity;
import com.bestapp.yikuair.fragments.ChatActivity;
import com.bestapp.yikuair.fragments.PersonalProfileActivity;
import com.bestapp.yikuair.fragments.ResponsiveUIActivity;
import com.bestapp.yikuair.fragments.ScheduleAddActivity;
import com.bestapp.yikuair.fragments.SelectMemberActivity;
import com.bestapp.yikuair.fragments.SettingActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class CheckNewEdition {

	private Context mContext;
	private static String requestURL;
	private static Thread getThread;

	public CheckNewEdition(Context context) {
		mContext = context;
	}

	public void checkEdition() {
		requestURL = "http://www.parng.com/checkV2.lc?v=1.24";
		getThread = new Thread() {
			public void run() {
				HttpClient client = new DefaultHttpClient();
				HttpGet getMethod = new HttpGet(requestURL);
				Log.e("test", "url :" + requestURL);
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
				Log.e("test", "check new edition :" + result);
				handleStr(result);
				content.close();
			}
		};
		getThread.start();
	}

	@SuppressLint("NewApi")
	private void handleStr(String Str) {
		if (Str == null)
			return;
		String newEditionUrl = "";
		String num = "";
		try {
			if (!Str.equals("0")) {
				String[] resultStr = Str.split(",");
				Log.e("test", "length :" + resultStr.length);
				if (resultStr != null && resultStr.length > 1) {
					num = resultStr[0];
					newEditionUrl = resultStr[1];
					closeAllActivity(newEditionUrl, num);
					sendLoginResultBroadCast(newEditionUrl, num);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void closeAllActivity(String url, String num) {
		Log.e("test", "checknewedition  close all activity...........");
		if (SettingActivity.instance != null)
			SettingActivity.instance.finish();
		if (PersonalProfileActivity.instance != null)
			PersonalProfileActivity.instance.finish();
		if (ResponsiveUIActivity.instance != null)
			ResponsiveUIActivity.instance.finish();
		if (ChatActivity.instance != null)
			ChatActivity.instance.finish();
		if (SelectMemberActivity.instance != null)
			SelectMemberActivity.instance.finish();
		if (ScheduleAddActivity.instance != null)
			ScheduleAddActivity.instance.finish();

		if (AppstartActivity.instance != null)
			AppstartActivity.instance.finish();

		if (LoginActivity.instance == null) {
			Log.e("test", "url:" + url );
			Log.e("test", "num:" + num );

			Intent intent = new Intent(mContext, LoginActivity.class);
			intent.putExtra("username", UserInfo.id);
			intent.putExtra("url", url);
			intent.putExtra("num", num);
			mContext.startActivity(intent);
		}
	}

	private void sendLoginResultBroadCast(String url, String num) {
		Intent intent = new Intent();
		intent.setAction(MessageInfo.LoginResultBroadCast);
		intent.putExtra("result", url);
		intent.putExtra("num", num);
		intent.putExtra("code", 200);
		intent.putExtra("token", 500);
		mContext.sendBroadcast(intent);
	}
}
