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

public class UploadFileUtil {

	public String uploadPath;
	public String userName;
	public String receiver;
	public String msguuid;
	public int fileType;
	public Context mContext;
	public int chatType; // individual : 0  group : 1
	public ClientSocket client;
	public UploadFileUtil(Context context) {
		mContext = context;
		client = new ClientSocket(mContext);
	}

	public void uploadFile(String path, String to, int type, String id, int tp) {

		uploadPath = path;
		receiver = to;
		fileType = type;
		msguuid = id;
		chatType = tp;

		new Thread(uploadThread).start();
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			String result = (String) msg.obj;
			Log.i("test", "upload file result :" + result);
			try {
				JSONObject jsonObj = new JSONObject(result);
				if (jsonObj.has("code")) {
					int code = jsonObj.getInt("code");
					if (code == 200) {
						JSONObject jsonObjArray = new JSONObject(result)
								.getJSONObject("data");
						if(client == null)
							client = new ClientSocket(mContext);
						client.sendMessage(
								jsonObjArray.toString(), 2,
								StringWidthWeightRandom.getNextString(), null,
								null, null, null, null, null, null, null, false);
						ChatMsgEntity entity = new ChatMsgEntity();
						entity.setStatus(MessageInfo.SEND_ARRIVAL);
						entity.setMsguuid(msguuid);
						entity.setChatType(chatType);
						entity.setSenderId(UserInfo.id);
						entity.setReceiverId(receiver);
						sendMessageBroadcast(entity);
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
				HttpPost httppost = new HttpPost(UserInfo.uploadRequestUrl);
				MultipartEntity mpEntity = new MultipartEntity();
				Log.e("test","uploadPath :" + uploadPath);
				FileBody file = new FileBody(new File(uploadPath),
						"application/octet-stream", uploadPath);

				Log.i("test", "name :" + UserInfo.id/* UserInfo.email_name*/);
				Log.i("test", "receiver : " + receiver);
				Log.i("test", "msguuid : " + msguuid);
				Log.i("test", "type :" + String.valueOf(fileType));
				Log.i("test", "path :" + uploadPath);

				mpEntity.addPart("upload", file);
				mpEntity.addPart("username",
						new StringBody(UserInfo.id));
				mpEntity.addPart("token",
						new StringBody(String.valueOf(fileType)));
				mpEntity.addPart("from", new StringBody(UserInfo.db_id));
				mpEntity.addPart("to", new StringBody(receiver));
				
				int type = 0;
				if(chatType == MessageInfo.INDIVIDUAL)
					type = 1;
				else if (chatType == MessageInfo.GROUP)
					type = 2;
				mpEntity.addPart("type", new StringBody(String.valueOf(type)));
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
