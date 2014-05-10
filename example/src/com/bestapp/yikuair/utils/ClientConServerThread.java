package com.bestapp.yikuair.utils;

import java.io.InputStream;
import java.net.Socket;
import java.util.Timer;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import com.bestapp.yikuair.AppstartActivity;
import com.bestapp.yikuair.LoginActivity;
import com.bestapp.yikuair.R;
import com.bestapp.yikuair.database.DBOpenHelper;
import com.bestapp.yikuair.fragments.ChatActivity;
import com.bestapp.yikuair.fragments.ChatMsgEntity;
import com.bestapp.yikuair.fragments.ClientSocket;
import com.bestapp.yikuair.fragments.MenuFragment;
import com.bestapp.yikuair.fragments.MessageFragment;
import com.bestapp.yikuair.fragments.PersonalProfileActivity;
import com.bestapp.yikuair.fragments.ResponsiveUIActivity;
import com.bestapp.yikuair.fragments.ScheduleAddActivity;
import com.bestapp.yikuair.fragments.SelectMemberActivity;
import com.bestapp.yikuair.fragments.SettingActivity;
import com.bestapp.yikuair.fragments.SocketConfig;
import com.bestapp.yikuair.fragments.SocketTimer;
import com.bestapp.yikuair.officialaccount.OfficialAccountFragment;
import com.bestapp.yikuair.officialaccount.SubActivity;

public class ClientConServerThread extends Thread {
	private Context mContext;
	private Socket socket;
	private InputStream in = null;
	// private BufferedInputStream buffer=null;
	private ClientSocket client;
	private String receiveString;

	// private CheckNewEdition checkUtil;

	public ClientConServerThread(Context context, Socket socket) {
		this.mContext = context;
		this.socket = socket;
		this.client = new ClientSocket(mContext);
		// this.checkUtil = new CheckNewEdition(mContext);
	}

	@Override
	public void run() {
		try {
			Log.e("test", "clientconserverpthread create........");
			in = socket.getInputStream();
			if (socket == null)
				Log.i("test", "socket is null.....");
			if (in == null)
				Log.i("test", "in is null........");
			socket.setKeepAlive(true);
			socket.setSoTimeout(0);
			// while (true) {
			byte[] buf = new byte[240000];
			int len = 0;
			byte[] arrayByte = null;
			// buffer = new BufferedInputStream(in);

			while ((len = in.read(buf)) != -1) {
				if (!UserInfo.isHomePressed) {
					arrayByte = DataUtil.byteArray(arrayByte,
							DataUtil.subBytes(buf, 0, len));
					int ablen = arrayByte.length;
					int flaglen = SocketConfig.WRIETEFLAGBYTES.length;
					byte[] byteCode = DataUtil.subBytes(arrayByte, ablen
							- flaglen, flaglen);
					boolean isEnd = DataUtil.isBytesEquals(byteCode,
							SocketConfig.WRIETEFLAGBYTES);
					if (isEnd) {
						receiveString = new String(DataUtil.subBytes(arrayByte,
								0, ablen - flaglen));
						arrayByte = null;
						Log.i("test", "receive message" + receiveString);
						getJsonArray(receiveString);
					}
				}
			}
			// }
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("FM", "");
			try {
				if (socket != null) {
					Log.e("test", "clientconserverthread : closesocket");
					// socket.close();
					socket = null;
					Log.e("test", "clientconserverpthread create........");
					in = socket.getInputStream();
					if (socket == null)
						Log.i("test", "socket is null.....");
					if (in == null)
						Log.i("test", "in is null........");
					socket.setKeepAlive(true);
					socket.setSoTimeout(0);
					// while (true) {
					byte[] buf = new byte[2400];
					int len = 0;
					byte[] arrayByte = null;
					while ((len = in.read(buf)) != -1) {
						if (!UserInfo.isHomePressed) {
							arrayByte = DataUtil.byteArray(arrayByte,
									DataUtil.subBytes(buf, 0, len));
							int ablen = arrayByte.length;
							int flaglen = SocketConfig.WRIETEFLAGBYTES.length;
							byte[] byteCode = DataUtil.subBytes(arrayByte,
									ablen - flaglen, flaglen);
							boolean isEnd = DataUtil.isBytesEquals(byteCode,
									SocketConfig.WRIETEFLAGBYTES);
							if (isEnd) {
								receiveString = new String(DataUtil.subBytes(
										arrayByte, 0, ablen - flaglen));
								arrayByte = null;
								Log.i("test", "receive message" + receiveString);
								getJsonArray(receiveString);
							}
						}
					}
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	private void getJsonArray(String str) {
		String[] st = str.split("_!@#\\$%\\^&\\*_");
		for (int i = 0; i < st.length; i++) {
			String jsondata = st[i];
			Log.i("test", "data :" + jsondata);
			if (jsondata != null && jsondata.length() > 0
					&& new JsonValidator().validate(jsondata) == true) {
				parseJson(jsondata);
			} else {
				if (ResponsiveUIActivity.instance == null)
					sendLoginResultBroadCast("", 406, 0);
			}
		}
	}

	private void parseJson(String strResult) {
		try {
			JSONObject jsonObj = new JSONObject(strResult);
			if (jsonObj.has("token")) {
				int token = jsonObj.getInt("token");
				if (token == 0) {
					// checkUtil.checkEdition();
					if (jsonObj.has("code")) {
						int code = jsonObj.getInt("code");
						if (code == 200) {
							if (UserInfo.isSendBroadCast) {
								sendLoginResultBroadCast(strResult, code, token);
							} else {
								UserInfo.isSendBroadCast = true;
							}

							/*
							 * if(UserInfo.isRecreateConnection){
							 * sendRecreateBroadcast(code);
							 * UserInfo.isRecreateConnection = false; }
							 */

							Log.e("test",
									"begin timer.................................");
							/*
							 * Timer timer = new Timer(); timer.schedule(new
							 * SocketTimer(socket), 1000, 250000);
							 */
							if (UserInfo.timer == null) {
								UserInfo.timer = new Timer();
							}
							UserInfo.timer.schedule(new SocketTimer(socket),
									1000, 230000);
						} else {
							Log.e("FM", "token not 200");
							closeAllAcitivity();
							sendLoginResultBroadCast(strResult, code, token);
						}
					}
				} else if (token == 1 || token == 2 || token == 3
						|| token == 22) {
					if (jsonObj.has("content") == false) {
						return;
					} else {
						String content = new String(
								DataUtil.decodeBase64(jsonObj
										.getString("content")));
						String latitude;
						double tmpLatitude;
						String longitude;
						double tmpLongitude;

						String time = MessageInfo.parseTime(jsonObj
								.getString("longDate"));
						String fullTime = MessageInfo
								.parseMessageFullTime(jsonObj
										.getString("longDate"));
						String sender = jsonObj.getString("from");
						String receiver = jsonObj.getString("to");
						String msguuid = jsonObj.getString("msguuid");
						String date = MessageInfo.parseDate(jsonObj
								.getString("longDate"));
						Log.e("test", "receive message date :" + date);
						String type = jsonObj.getString("type");
						/*
						 * if (date.equals(MessageInfo.date)) { date = ""; }
						 * else { MessageInfo.date = date; }
						 */
						String smallPicUrl = null;
						String bigPicUrl = null;
						String voiceUrl = null;
						if (token == 2) {
							String smallPicPath = UserInfo.downloadImgUrl
									+ jsonObj.getString("smallImgPath");
							String bigPicPath = UserInfo.downloadImgUrl
									+ jsonObj.getString("filePath");
							content = mContext.getResources().getString(
									R.string.picture);
							smallPicUrl = smallPicPath;
							bigPicUrl = bigPicPath;
						} else if (token == 3) {
							voiceUrl = UserInfo.downloadVoiceUrl
									+ jsonObj.getString("filePath");
							content = mContext.getResources().getString(
									R.string.voice);
						}

						int status = MessageInfo.RECEIVE_MESSAGE;
						ChatMsgEntity entity = new ChatMsgEntity(true, sender,
								receiver, content, time, date, smallPicUrl,
								bigPicUrl, token, status, voiceUrl);
						entity.setMsguuid(msguuid);// add msguuid;
						entity.setFullTime(fullTime);// add fulltime to sort
						if (token == 22) {
							String strContent = content;
							JSONObject jsonContent = new JSONObject(content);
							content = jsonContent.getString("location");
							tmpLatitude = jsonContent.getDouble("latitude");
							tmpLongitude = jsonContent.getDouble("longitude");
							latitude = "" + tmpLatitude;
							longitude = "" + tmpLongitude;
							entity.setlongitude(longitude);
							entity.setlatitude(latitude);
							entity.setContent(content);
						}

						String fromname;
						fromname = jsonObj.getString("fromName").trim();
						DBlog.e("fromane", fromname);
						if (fromname != null && !"".equals(fromname)) {
							String getFromname = new String(
									DataUtil.decodeBase64(fromname)).trim();

							DBlog.e("I want", jsonObj.toString());
							entity.setFromname(getFromname.replace("����ĺ���-",
									""));
						}
						entity.setChatType(MessageInfo.INDIVIDUAL);
						if (type.equals("2")) {
							entity.setChatType(MessageInfo.GROUP);
							createLocalGroup(entity);
						}

						String tempReceiver = receiver;
						client.sendMessage(null, 7, msguuid, sender,
								tempReceiver, null, null, null, null, type,
								null, false, null);

						sendMessageBroadcast(entity);
						return;
					}
				} else if (token == 6) {
					String msguuid = jsonObj.getString("msguuid");
					String sender = jsonObj.getString("from");
					String receiver = jsonObj.getString("to");
					String type = jsonObj.getString("type");
					int chatType = 0;
					if (type.equals("2"))
						chatType = MessageInfo.GROUP;
					if (type.equals("4")) {
						chatType = MessageInfo.OFFICEACCOUNT;
					}

					ChatMsgEntity entity = new ChatMsgEntity();
					entity.setStatus(MessageInfo.SEND_ARRIVAL);
					entity.setSenderId(sender);
					if (jsonObj.has("task_id")) {
						entity.setScheduleTaskId(jsonObj.getString("task_id"));
					}
					entity.setMsguuid(msguuid);
					entity.setReceiverId(receiver);
					entity.setChatType(chatType);
					if (jsonObj.has("task_id"))
						sendScheduleBroadcast(entity);

					sendMessageBroadcast(entity);
				} else if (token == 7) {

				} else if (token == 8) {
					String sender = jsonObj.getString("from");
					String receiver = jsonObj.getString("to");
					String msguuid = jsonObj.getString("msguuid");
					String type = jsonObj.getString("type");
					int chatType = 0;
					if (type.equals("2"))
						chatType = MessageInfo.GROUP;
					client.sendMessage(null, 9, msguuid, sender, receiver,
							null, null, null, null, type, null, false, null);
					client.sendMessage(null, 10, msguuid, sender, receiver,
							null, null, null, null, type, null, false, null);
					ChatMsgEntity entity = new ChatMsgEntity();
					entity.setSenderId(sender);
					entity.setChatType(chatType);
					entity.setMsguuid(msguuid);
					entity.setStatus(MessageInfo.SEND_READED);
					entity.setReceiverId(receiver);
					sendMessageBroadcast(entity);
				} else if (token == 12) {
					JSONObject Obj = jsonObj.getJSONObject("data");
					String dbId = Obj.getString("from");
					String msguuid = jsonObj.getString("msguuid");
					String headUrl = null;
					String signature = null;
					client.sendMessage(null, 8, msguuid, dbId, UserInfo.db_id,
							null, null, null, null, "3", null, false, null);
					client.sendMessage(null, 8, msguuid, dbId, UserInfo.db_id,
							null, null, null, null, "3", null, false, null);

					if (Obj.has("headurl"))
						headUrl = Obj.getString("headurl");
					if (Obj.has("signature"))
						signature = Obj.getString("signature");
					updateDB(dbId, headUrl, signature);
				} else if (token == 15) {
					int code = 200;
					if (!jsonObj.has("code")) {
						String group_id = jsonObj.getString("from");
						String msguuid = jsonObj.getString("msguuid");

						sendGroupBroadcast(msguuid, code, token, group_id);
					} else {
						JSONObject Obj = jsonObj.getJSONObject("data");
						String msguuid = Obj.getString("msguuid");
						String group_id = Obj.getString("group_id");
						// String dbId = jsonObj.getString("from");
						// client.sendMessage(null, 8, msguuid, group_id,
						// UserInfo.db_id,
						// null, null, null, null, null, null, false, null);
						sendGroupBroadcast(msguuid, code, token, group_id);
					}

				} else if (token == 100) {
					if (jsonObj.has("code")) {
						int code = jsonObj.getInt("code");
						String result = null;
						if (code == 200) {
							result = receiveString;
						}
						sendLoginResultBroadCast(result, code, token);
					}
				} else if (token == 13) {
					Log.i("test", "token 13.............");
					String sender = jsonObj.getString("from");
					String receiver = jsonObj.getString("to");
					int type = jsonObj.getInt("type");
					String msguuid = jsonObj.getString("msguuid");
					String btime = jsonObj.getString("btime");
					String etime = jsonObj.getString("etime");
					String title = new String(DataUtil.decodeBase64(jsonObj
							.getString("title")));
					String taskId = jsonObj.getString("task_id");
					int taskType = jsonObj.getInt("tasktype");
					String longDate = jsonObj.getString("longDate");
					String address = "";
					if (jsonObj.getString("address") != null
							&& jsonObj.getString("address").length() > 0) {
						address = new String(DataUtil.decodeBase64(jsonObj
								.getString("address")));
					}

					int tempTaskType;
					if (taskType == 1)
						tempTaskType = 1;
					else if (taskType == 2)
						tempTaskType = 0;
					else
						tempTaskType = 2;

					ChatMsgEntity entity = new ChatMsgEntity();
					entity.setType(MessageInfo.SCHEDULE);
					entity.setScheduleType(tempTaskType);
					entity.setStatus(MessageInfo.RECEIVE_MESSAGE);
					entity.setSenderId(sender);
					entity.setReceiverId(receiver);
					entity.setMsguuid(msguuid);
					entity.setScheduleTitle(title);
					entity.setScheduleAddress(address);
					entity.setScheduleTaskId(taskId);
					entity.setTime(MessageInfo.parseTime(longDate));
					entity.setFullTime(MessageInfo.parseMessageFullTime(jsonObj
							.getString("longDate")));
					entity.setScheduleBeginTime(MessageInfo
							.parseScheduleDate(btime));
					entity.setScheduleEndTime(MessageInfo
							.parseScheduleDate(etime));
					entity.setIsComing(true);
					if (tempTaskType == MessageInfo.TASK) {
						entity.setContent(mContext.getResources().getString(
								R.string.message_item_task));
					} else if (tempTaskType == MessageInfo.MEETING) {
						entity.setContent(mContext.getResources().getString(
								R.string.message_item_meeting));
					} else {
						entity.setContent(mContext.getResources().getString(
								R.string.message_item_other));
					}

					String tempReceiver = receiver;
					if (type == 2) {
						tempReceiver = UserInfo.db_id;
						entity.setChatType(MessageInfo.GROUP);
					} else
						entity.setChatType(MessageInfo.INDIVIDUAL);

					if (!sender.equals(UserInfo.db_id)) {
						client.sendMessage(null, 7, msguuid, sender,
								tempReceiver, null, null, null, null,
								String.valueOf(type), null, false, null);
						client.sendMessage(null, 8, msguuid, sender,
								tempReceiver, null, null, null, null,
								String.valueOf(type), null, false, null);
						/*
						 * client.sendMessage(null, 8, msguuid, sender,
						 * tempReceiver, null, null, null, null,
						 * String.valueOf(type), false);
						 */
						if (type == 2
						/* && !MessageInfo.groupMap.containsKey(receiver) */)
							createLocalGroup(entity);
						else
							sendScheduleBroadcast(entity);
						sendMessageBroadcast(entity);
					}
				} else if (token == 14) {
					Log.i("test", "token 14......................");
					// JSONObject Obj = jsonObj.getJSONObject("data");
					if (jsonObj.getInt("code") == 200)
						return;
					String msguuid = jsonObj.getString("msguuid");
					String sender = jsonObj.getString("from");
					String receiver = jsonObj.getString("to");
					String taskId = jsonObj.getString("task_id");
					int scheduleType = jsonObj.getInt("type");
					int formatedType;
					if (scheduleType == 1)
						formatedType = MessageInfo.MEETING;
					else if (scheduleType == 2)
						formatedType = MessageInfo.TASK;
					else
						formatedType = MessageInfo.OTHER;
					ChatMsgEntity entity = new ChatMsgEntity();
					entity.setSenderId(sender);
					entity.setReceiverId(receiver);
					entity.setScheduleTaskId(taskId);
					entity.setScheduleType(formatedType);
					entity.setMsguuid(msguuid);
					entity.setStatus(MessageInfo.SEND_READED);
					sendMessageBroadcast(entity);
				} else if (token == 16) {
					if (jsonObj.has("code"))
						sendGroupInfoBroadcast(200);
					else {
						String groupId = jsonObj.getString("to");
						String sender = jsonObj.getString("from");
						String msguuid = jsonObj.getString("msguuid");
						changeGroupInfo(groupId, sender, msguuid);
					}
				} else if (token == 17) {
					if (jsonObj.has("code"))
						sendGroupInfoBroadcast(200);
					else {
						String groupId = jsonObj.getString("to");
						String sender = jsonObj.getString("from");
						String msguuid = jsonObj.getString("msguuid");
						changeGroupInfo(groupId, sender, msguuid);
					}
				} else if (token == 23) {
					Log.e("test", "token 23.....................");
					String from = jsonObj.getString("from");
					String msguuid = jsonObj.getString("msguuid");
					String type = jsonObj.getString("type");
					String to = jsonObj.getString("to");

					if (from.equals(UserInfo.db_id))
						return;
					client.sendMessage(null, 7, msguuid, from, to, null, null,
							null, null, type, null, false, null);
					client.sendMessage(null, 8, msguuid, from, to, null, null,
							null, null, type, null, false, null);

					if (jsonObj.has("task_id")) {
						String sender = jsonObj.getString("from");
						String taskId = jsonObj.getString("task_id");
						// sendScheduleDelBroadcast(sender, to, taskId, type);
					}
				}
			} else {
				sendLoginResultBroadCast("", 404, 0);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void updateDB(String dbId, String headUrl, String signature) {
		DBOpenHelper dbOpenHelper = new DBOpenHelper(mContext);
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		ContentValues value = new ContentValues();
		if (headUrl != null) {
			value.put("headURL", headUrl);
			db.update("contactsTable", value, "dbid=?", new String[] { dbId });
		}
		if (signature != null) {
			value.put("signature", signature);
			db.update("contactsTable", value, "dbid=?", new String[] { dbId });
		}
		dbOpenHelper.close();
		db.close();
	}

	private void changeGroupInfo(String groupId, String sender, String msguuid) {
		client.sendMessage(null, 8, msguuid, sender, UserInfo.db_id, null,
				null, null, null, "2", null, false, null);
		ChatMsgEntity entity = new ChatMsgEntity();
		entity.setReceiverId(groupId);
		entity.setSenderId(sender);
		entity.setStatus(MessageInfo.GROUP_MODIFY);
		entity.setChatType(MessageInfo.GROUP);
		createLocalGroup(entity);
	}

	private void sendScheduleDelBroadcast(String sender, String to,
			String taskId, String type) {
		Log.e("test", "sendScheduledelbroadcast............................");
		Intent intent = new Intent();
		intent.setAction(MessageInfo.ScheduleDelResultBroadCast);
		intent.putExtra("sender", sender);
		intent.putExtra("to", to);
		intent.putExtra("type", type);
		intent.putExtra("taskId", taskId);
		mContext.sendBroadcast(intent);
	}

	private void closeAllAcitivity() {
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
			Intent intent = new Intent(mContext, LoginActivity.class);
			intent.putExtra("username", UserInfo.id);
			mContext.startActivity(intent);
		}

		/*
		 * if (ClientSocket.rbr != null){
		 * mContext.unregisterReceiver(ClientSocket.rbr); ClientSocket.rbr =
		 * null; }
		 */

		UserInfo.isRecreateConnection = false;
		ClientSocket.socket = null;
	}

	private void createLocalGroup(ChatMsgEntity entity) {
		CreateGroupUtil util = new CreateGroupUtil(mContext, entity);
		util.getGroupMember();
	}

	private void sendLoginResultBroadCast(String result, int code, int token) {
		Log.e("test", "send login result broadcast........................");
		Log.e("test", "result :" + result);
		Intent intent = new Intent();
		intent.setAction(MessageInfo.LoginResultBroadCast);
		intent.putExtra("result", result);
		intent.putExtra("code", code);
		intent.putExtra("token", token);
		mContext.sendBroadcast(intent);
	}

	private void sendGroupInfoBroadcast(int code) {
		Intent intent = new Intent();
		intent.setAction(MessageInfo.GroupInfoResultBroadCast);
		intent.putExtra("code", code);
		mContext.sendBroadcast(intent);
	}

	private void sendRecreateBroadcast(int code) {
		Intent intent = new Intent();
		intent.setAction(MessageInfo.RecreateResultBroadCast);
		intent.putExtra("code", code);
		mContext.sendBroadcast(intent);
	}

	private void sendScheduleBroadcast(ChatMsgEntity entity) {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putSerializable("message", entity);
		intent.setAction(MessageInfo.ScheduleResultBroadCast);
		intent.putExtras(bundle);
		mContext.sendBroadcast(intent);
	}

	private void sendMessageBroadcast(ChatMsgEntity entity) {
		if (MenuFragment.instance == null)
			Log.e("test", " menu is null..");
		else
			Log.e("test", " menu is not null..");

		if (MessageFragment.instance == null)
			Log.e("test", " messagefragment is null..");
		else
			Log.e("test", " messagefragment is not null..");

		if (ChatActivity.instance == null)
			Log.e("test", " chatactivity is null..");
		else
			Log.e("test", " chatacitvity is not null..");

		if (MenuFragment.instance == null && MessageFragment.instance == null
				&& ChatActivity.instance == null) {
			entity.setIsAdd(true);
			// if (entity.getFromname() != null
			// && !entity.getFromname().trim().equals("")) {
			MessageInfo.matchMessageEntityList.add(entity);
			// sendBroadcastToFrind();
			// } else {

			DBlog.e("tadiao", "------messageEntityList");
			MessageInfo.messageEntityList.add(entity);
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putSerializable("message", entity);
			intent.setAction(MessageInfo.MessageBroadCastName);
			intent.putExtras(bundle);
			// Log.e("test", "conserver send messagebroadcast");
			mContext.sendBroadcast(intent);
			// }
		} else {
			// if (entity.getFromname() != null
			// && !entity.getFromname().trim().equals("")) {
			MessageInfo.matchMessageEntityList.add(entity);
			// sendBroadcastToFrind();
			// } else {
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putSerializable("message", entity);
			intent.setAction(MessageInfo.MessageBroadCastName);
			intent.putExtras(bundle);
			// Log.e("test", "conserver send messagebroadcast");
			mContext.sendBroadcast(intent);
			Log.e("FM", "" + mContext);
			// }
		}
	}

	// private void sendBroadcastToFrind() {
	// Intent intent = new Intent();
	// intent.setAction(MessageInfo.FriendMessageBroadCastName);
	// mContext.sendBroadcast(intent);
	// }

	private void sendGroupBroadcast(String msguuid, int code, int token,
			String groupId) {
		Intent intent = new Intent();
		intent.setAction(MessageInfo.GroupBroadCastName);
		intent.putExtra("msguuid", msguuid);
		intent.putExtra("code", code);
		intent.putExtra("token", token);
		intent.putExtra("group_id", groupId);
		mContext.sendBroadcast(intent);
	}
}
