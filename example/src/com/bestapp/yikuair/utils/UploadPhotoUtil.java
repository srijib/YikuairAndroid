package com.bestapp.yikuair.utils;

import java.io.File;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.bestapp.yikuair.fragments.ChatMsgEntity;
import com.bestapp.yikuair.fragments.ClientSocket;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class UploadPhotoUtil {

	public String uploadPath;
	public String userName;
	public String msguuid;
	public Context mContext;
	public ClientSocket client;

	public UploadPhotoUtil(Context context) {
		mContext = context;
		client = new ClientSocket(mContext);
	}

	public void uploadFile(String path, String id) {
		uploadPath = path;
		msguuid = id;

		new Thread(uploadThread).start();
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			String result = (String) msg.obj;
			Log.i("test", "upload header result :" + result);
			try {
				JSONObject jsonObj = new JSONObject(result);
				if (jsonObj.has("code")) {
					int code = jsonObj.getInt("code");
					if (code == 200) {

						JSONObject jsonObject = new JSONObject(result)
								.getJSONObject("data");
						String headurl = jsonObject.getString("headurl");

						if (client == null)
							client = new ClientSocket(mContext);
						client.sendMessage(headurl, 12, msguuid,
								UserInfo.db_id, null, null, null, null, null,
								"headUrl", null, false);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	};

	public Runnable uploadThread = new Runnable() {
		@Override
		public void run() {
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(UserInfo.uploadHeaderUrl);
				MultipartEntity mpEntity = new MultipartEntity();
				FileBody file = new FileBody(new File(uploadPath),
						"application/octet-stream", uploadPath);

				Log.i("test", "name :" + UserInfo.id);
				Log.i("test", "msguuid : " + msguuid);
				Log.i("test", "path :" + uploadPath);

				mpEntity.addPart("upload", file);
				mpEntity.addPart("username", new StringBody(UserInfo.id));

				mpEntity.addPart("from", new StringBody(UserInfo.db_id));

				mpEntity.addPart("msguuid", new StringBody(msguuid));
				mpEntity.addPart("password", new StringBody(
						UserInfo.cipher_password));

				httppost.setEntity(mpEntity);

				HttpResponse response = httpclient.execute(httppost);
				String content = EntityUtils.toString(response.getEntity());

				Message msg = mHandler.obtainMessage();
				msg.obj = content;
				msg.sendToTarget();

				httpclient.getConnectionManager().shutdown();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	private void sendMessageBroadcast(ChatMsgEntity entity) {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putSerializable("message", entity);
		intent.setAction(MessageInfo.MessageBroadCastName);
		intent.putExtras(bundle);
		mContext.sendBroadcast(intent);
	}
}
