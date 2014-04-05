package com.bestapp.yikuair.utils;


import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.bestapp.yikuair.fragments.ChatActivity;
import com.bestapp.yikuair.fragments.ChatMsgEntity;
import com.bestapp.yikuair.fragments.ClientSocket;
import com.bestapp.yikuair.officialaccount.OfficialAccountFragment;
import com.bestapp.yikuair.officialaccount.PicTextList;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class OFFTimeInfomationReceiver extends BroadcastReceiver {
	private MenuHttp menuHttp;
	public static ArrayList<ChatMsgEntity> list = new ArrayList<ChatMsgEntity>();
	private String token;
	private Context mContext;
	private ClientSocket mClientSocket;

	@Override
	public void onReceive(Context context, Intent intent) {
		mContext = context;
		if (mClientSocket == null) {
			mClientSocket = new ClientSocket(context);
		}
		if (menuHttp == null) {
			menuHttp = new MenuHttp();
		}
		try {
			menuHttp.getOffTimeMessage(ResponseHandlers);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void getMenuBackInfo(String josn) {
		DBlog.e("dsds", josn);

		try {
			JSONObject jsonObject = new JSONObject(josn.toString());

			if (jsonObject.has("data")) {
				JSONArray jsonArray;

				jsonArray = jsonObject.getJSONArray("data");
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject2 = (JSONObject) jsonArray.opt(i);
					ChatMsgEntity entity = new ChatMsgEntity();
					String s1 = jsonObject2.getString("content");
					entity.setStatus(MessageInfo.RECEIVE_MESSAGE);
					entity.setTime(MessageInfo.getChatTime());
					entity.setFullTime(MessageInfo.getMessageFullTime());
					entity.setIsComing(true);
					entity.setSenderId(jsonObject2.getString("from"));
					entity.setReceiverId(UserInfo.db_id);
					entity.setMsguuid(jsonObject2.getString("msguuid"));
					JSONObject jsonObject3 = new JSONObject(s1.toString());
					String type = jsonObject3.getString("type");
					if (!type.equals("4")) {
						return;
					}
					if (jsonObject3.has("token")) {

						
						if (jsonObject3.has("content")) {

							token = jsonObject3.getString("token");
							if (token.equals("1")) {
								String content = new String(
										DataUtil.decodeBase64(jsonObject3
												.getString("content")));
								entity.setContent(content);
								entity.setType(MessageInfo.TEXT);

							} else if (token.equals("2")) {
								String smallImgPath = jsonObject3
										.getString("smallImgPath");
								String filePath = jsonObject3
										.getString("filePath");
								entity.setSmallPicUrl(smallImgPath);
								entity.setBigPicUrl(filePath);
								entity.setType(MessageInfo.PICTURE);
							} else if (token.equals("3")) {
								String filePath = jsonObject3
										.getString("filePath");
								entity.setType(MessageInfo.VOICE);
								entity.setVoiceUrl(filePath);
							} else if (token.equals("4")) {
								String imgpath = new String(
										DataUtil.decodeBase64(jsonObject3
												.getString("imgpath")));
								String title = new String(
										DataUtil.decodeBase64(jsonObject3
												.getString("title")));
								String content = new String(
										DataUtil.decodeBase64(jsonObject3
												.getString("content")));
								String details = new String(
										DataUtil.decodeBase64(jsonObject3
												.getString("detail")));
								String urls = new String(
										DataUtil.decodeBase64(jsonObject3
												.getString("url")));
								entity.setContent(content);
								entity.setType(MessageInfo.PIC_TEXT);
								entity.setSmallPicUrl(imgpath);
								entity.setTitle(title);
								entity.setDetail(details);
								entity.setUrl(urls);
								if (jsonObject3.has("list")) {
									JSONArray array = jsonObject3
											.getJSONArray("list");
									ArrayList<PicTextList> list_pc = new ArrayList<PicTextList>();
									for (int j = 0; j < array.length(); j++) {
										JSONObject pictxt = (JSONObject) array
												.opt(j);
										PicTextList ptL = new PicTextList();
										if (pictxt.has("title")) {
											ptL.setTitle(new String(
													DataUtil.decodeBase64(pictxt
															.getString("title"))));
										}

										if (pictxt.has("imgpath")) {
											String img_path = new String(
													DataUtil.decodeBase64(pictxt
															.getString("imgpath")));
											ptL.setImgpath(img_path);
										}

										if (pictxt.has("detail")) {
											ptL.setDetail(new String(
													DataUtil.decodeBase64(pictxt
															.getString("detail"))));
										}
										if (pictxt.has("url")) {
											ptL.setUrl(new String(DataUtil
													.decodeBase64(pictxt
															.getString("url"))));
										}
										list_pc.add(ptL);
									}

									entity.setList(list_pc);
								}
							}
							MessageInfo.OfficeAccountList.add(entity);
							DBlog.e("OfficeAccountList",
									MessageInfo.OfficeAccountList.size() + "");
							mClientSocket.sendMessage(null, 7,
									entity.getMsguuid(), entity.getSecond(),
									entity.getReceiverId(), null, null, null,
									null, entity.getType() + "", null, false);
							mClientSocket.sendMessage(null, 8,
									entity.getMsguuid(), entity.getSecond(),
									entity.getReceiverId(), null, null, null,
									null, entity.getType() + "", null, false);
						}
					}

					if (OfficialAccountFragment.instance != null
							&& ChatActivity.instance == null
							&& MessageInfo.OfficeAccountList.size() > 0) {
						OfficialAccountFragment.instance.getMessage();
						sendBroadcast();
					} else if (OfficialAccountFragment.instance != null
							&& ChatActivity.instance != null) {
						MessageInfo.OfficeAccountList.remove(entity);
						sendBroadcastToChat(entity);
					} else {
						sendBroadcast();
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();

		}

	}

	private void sendBroadcast() {
		Intent intent = new Intent();
		intent.putExtra("name", "office");
		DBlog.e("f-----------", MessageInfo.OfficeAccountList.size() + "");
		intent.putExtra("number", MessageInfo.OfficeAccountList.size());
		intent.setAction(MessageInfo.FriendBroadCastName);
		mContext.sendBroadcast(intent);

	}

	private void sendBroadcastToChat(ChatMsgEntity entity) {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putSerializable("message", entity);
		intent.setAction(MessageInfo.MessageBroadCastName);
		intent.putExtras(bundle);
		// Log.e("test", "conserver send messagebroadcast");
		mContext.sendBroadcast(intent);

	}

	AsyncHttpResponseHandler ResponseHandlers = new AsyncHttpResponseHandler() {
		@Override
		public void onSuccess(int arg0, String arg1) {
			getMenuBackInfo(arg1);
		};

		@Override
		public void onFailure(Throwable arg0, String arg1) {
		};
	};
}
