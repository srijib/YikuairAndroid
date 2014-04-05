package com.bestapp.yikuair.fragments;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bestapp.yikuair.utils.AcceptRunner;
import com.bestapp.yikuair.utils.ClientConServerThread;
import com.bestapp.yikuair.utils.DBlog;
import com.bestapp.yikuair.utils.DataUtil;
import com.bestapp.yikuair.utils.MessageInfo;
import com.bestapp.yikuair.utils.StringWidthWeightRandom;
import com.bestapp.yikuair.utils.UserInfo;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
//
//public class ClientSocket {
//	public static Socket socket = null;
//	public OutputStream out = null;
//	public String sendContent = null;
//	private Context mContext;
//	private static ExecutorService es;// threadpool
//	private static final int THREAD_COUNT = 5;
//	private static Handler handler;
//
//	public Handler getHandler() {
//		return handler;
//	}
//
//	public void setHandler(Handler handler) {
//		this.handler = handler;
//	}
//
//	public ClientSocket(Context context) {
//		mContext = context;
//		WifiManager wifi = (WifiManager) mContext
//				.getSystemService(Context.WIFI_SERVICE);
//		WifiInfo info = wifi.getConnectionInfo();
//		UserInfo.mac_address = info.getMacAddress();
//	}
//
//	public ExecutorService instanceES() {
//		if (es == null)
//			es = Executors.newFixedThreadPool(THREAD_COUNT);
//		return es;
//	}
//
//	public Socket instanceSocket() {
//		
//		if (socket == null /* || !isConnected() */) {
//			try {
//				Log.e("test", "new socket........................");
//				socket = new Socket(SocketConfig.IPADDRESS, SocketConfig.PORT);
//				socket.setKeepAlive(true);
//			} catch (Exception ex) {
//				ex.printStackTrace();
//				sendLoginResultBroadCast(null, 405, 0);
//				return null;
//			}
//		}
//		return socket;
//	}
//
//	public boolean isConnected() {
//		try {
//			Log.e("test", "isConnected....................");
//			socket.sendUrgentData(0xFF);
//		} catch (Exception ex) {
//			return false;
//		}
//		return true;
//	}
//
//	private void sendLoginResultBroadCast(String result, int code, int token) {
//		Intent intent = new Intent();
//		intent.setAction(MessageInfo.LoginResultBroadCast);
//		intent.putExtra("result", result);
//		intent.putExtra("code", code);
//		intent.putExtra("token", token);
//		mContext.sendBroadcast(intent);
//	}
//
//	public void closeSocket() {
//		try {
//			if (out != null)
//				out.close();
//			if (socket != null) {
//				socket.close();
//				socket = null;
//			}
//
//			if (UserInfo.timer != null) {
//				UserInfo.timer.cancel();
//				UserInfo.timer = null;
//			}
//
//			sendLoginResultBroadCast("", 407, 0);
//
//			/*
//			 * UserInfo.isSendBroadCast = false;
//			 * 
//			 * UserInfo.isRecreateConnection = true;
//			 * Log.e("test","ClientSocket create.........");
//			 * 
//			 * sendMessage(null, 0, StringWidthWeightRandom.getNextString(),
//			 * null, null, null, null, null, null, null, null, true);
//			 */
//		} catch (Exception e) {
//			e.printStackTrace();
//			socket = null;
//		}
//	}
//
//	public void createClient(String userEmail, String cipherPassword) {
//		final String email = userEmail;
//		final String cipherPwd = cipherPassword;
////		UserInfo.isHomePressed = false;
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				try {
//					sendContent = "{\"token\":\"100\",\"uid\":{\"username\":\""
//							+ email + "\",\"password\":\"" + cipherPwd
//							+ "\",\"device\":\"0\", \"devicetoken\":\""
//							+ UserInfo.push_userId + "_"
//							+ UserInfo.push_channelId + "\"}}";
//
//					Log.i("test", "send token 100 : " + sendContent);
//					socket = instanceSocket();
//
//					if (socket == null)
//						return;
//					out = socket.getOutputStream();
//					String string = sendContent;
//					out.write(string.getBytes());
//					out.write(SocketConfig.WRIETEFLAGBYTES);
//					out.flush();
//					Log.e("test", "send over..............");
//
//					ClientConServerThread ccst = new ClientConServerThread(
//							mContext, socket);
//					ccst.start();
//
//				} catch (Exception ex) {
//					closeSocket();
//					ex.printStackTrace();
//				}
//			}
//		}).start();
//	}
//	public void sendMessage(String msgString, int msgToken, String msgId,
//			String msgSender, String msgReceiver, String scheduleBeginTime,
//			String scheduleEndTime, String scheduleAddress,
//			String scheduleType, String msgType, String taskid, boolean isRecre,String fromname) {
//	}
//	public void sendMessage(String msgString, int msgToken, String msgId,
//			String msgSender, String msgReceiver, String scheduleBeginTime,
//			String scheduleEndTime, String scheduleAddress,
//			String scheduleType, String msgType, String taskid, boolean isRecre) {
//
//		es = instanceES();
//		Runnable runner = new ExecutorThread(msgString, msgToken, msgId,
//				msgSender, msgReceiver, scheduleBeginTime, scheduleEndTime,
//				scheduleAddress, scheduleType, msgType, taskid, isRecre);
//		es.execute(runner);
//
//
//	}
//
//	public static String replaceBlank(String str) {
//		String dest = "";
//		if (str != null) {
//			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
//			Matcher m = p.matcher(str);
//			dest = m.replaceAll("");
//		}
//		return dest;
//	}
//
//	class ExecutorThread implements Runnable {
//		private int token;
//		private String sender;
//		private String receiver;
//		private String messageString;
//		private String msguuid;
//		private String msgstr;
//		private boolean isRecreate;
//		private String type;// individual or group /*mobile or
//							// signature*/
//		private String tasktype;// task, meeting, other
//		private String beginTime;
//		private String endTime;
//		private String address;
//		private String taskId;
//
//		public ExecutorThread(String msgString, int msgToken, String msgId,
//				String msgSender, String msgReceiver, String scheduleBeginTime,
//				String scheduleEndTime, String scheduleAddress,
//				String scheduleType, String msgType, String taskid,
//				boolean isRecre) {
//			this.token = msgToken;
//			this.sender = msgSender;
//			this.receiver = msgReceiver;
//			this.messageString = msgString;
//			this.msguuid = msgId;
//			this.msgstr = msgString;
//			this.isRecreate = isRecre;
//			this.type = msgType;// individual or group /*mobile or
//			// signature*/
//			this.tasktype = scheduleType;// task, meeting, other
//			this.beginTime = scheduleBeginTime;
//			this.endTime = scheduleEndTime;
//			this.address = scheduleAddress;
//			this.taskId = taskid;
//		}
//
//		public void run() {
//			try {
//				if (token == 0) {
//					sendContent = "{\"token\":\"0\",\"uid\":{\"username\":\""
//							+ UserInfo.id + "\",\"password\":\""
//							+ UserInfo.cipher_password
//							+ "\",\"device\":\"0\", \"devicetoken\":\""
//							+ UserInfo.push_userId + "_"
//							+ UserInfo.push_channelId/* UserInfo.token */
//							+ "\"}}";
//				} else if (token == 1) {
//					sendContent = "{\"token\":\""
//							+ token
//							+ "\",\"type\":\""
//							+ type
//							+ "\",\"from\":\""
//							+ sender
//							+ "\",\"to\":\""
//							+ receiver
//							+ "\",\"content\":\""
//							+ replaceBlank(DataUtil.ecodeBase64(messageString
//									.getBytes())) + "\",\"msguuid\":\""
//							+ msguuid + "\"}";
//				} else if (token == 2) {
//					sendContent = msgstr;
//				} else if (token == 7 || token == 8 || token == 9
//						|| token == 10) {
//					sendContent = "{\"token\":\"" + token + "\",\"type\":\""
//							+ type + "\",\"from\":\"" + sender + "\",\"to\":\""
//							+ receiver + "\",\"msguuid\":\"" + msguuid + "\"}";
//				} else if (token == 12) {
//					if (type != null && type.equals("signature")) {
//						sendContent = "{\"token\":\""
//								+ token
//								+ "\",\"signature\":\""
//								+ DataUtil
//										.ecodeBase64(messageString.getBytes())
//								+ "\",\"com_id\":\"" + UserInfo.companyId
//								+ "\",\"from\":\"" + sender
//								+ "\",\"msguuid\":\"" + msguuid + "\"}";
//					} else if (type != null && type.equals("mobile")) {
//						sendContent = "{\"token\":\"" + token
//								+ "\",\"mobile\":\"" + messageString
//								+ "\",\"com_id\":\"" + UserInfo.companyId
//								+ "\",\"from\":\"" + sender
//								+ "\",\"msguuid\":\"" + msguuid + "\"}";
//					} else if (type != null && type.equals("headUrl")) {
//						sendContent = "{\"token\":\"" + token
//								+ "\",\"headurl\":\"" + messageString
//								+ "\",\"com_id\":\"" + UserInfo.companyId
//								+ "\",\"from\":\"" + sender
//								+ "\",\"msguuid\":\"" + msguuid + "\"}";
//					}
//				} else if (token == 13) {
//					if (tasktype != null) {
//						if (taskId == null)
//							sendContent = "{\"token\":\""
//									+ token
//									+ "\",\"tasktype\":\""
//									+ tasktype
//									+ "\",\"title\":\""
//									+ DataUtil.ecodeBase64(messageString
//											.getBytes())
//									+ "\",\"address\":\""
//									+ ((address == null || address.length() == 0) ? ""
//											: DataUtil.ecodeBase64(address
//													.getBytes()))
//									+ "\",\"type\":\"" + type
//									+ "\",\"from\":\"" + sender
//									+ "\",\"to\":\"" + receiver
//									+ "\",\"btime\":\"" + beginTime
//									+ "\",\"etime\":\"" + endTime
//									+ "\",\"msguuid\":\"" + msguuid + "\"}";
//						else
//							sendContent = "{\"token\":\""
//									+ token
//									+ "\",\"tasktype\":\""
//									+ tasktype
//									+ "\",\"title\":\""
//									+ DataUtil.ecodeBase64(messageString
//											.getBytes())
//									+ "\",\"address\":\""
//									+ ((address == null || address.length() == 0) ? ""
//											: DataUtil.ecodeBase64(address
//													.getBytes()))
//									+ "\",\"type\":\"" + type
//									+ "\",\"from\":\"" + sender
//									+ "\",\"to\":\"" + receiver
//									+ "\",\"task_id\":\"" + taskId
//									+ "\",\"btime\":\"" + beginTime
//									+ "\",\"etime\":\"" + endTime
//									+ "\",\"msguuid\":\"" + msguuid + "\"}";
//					}// for schedule
//					else
//						// for group
//						sendContent = "{\"token\":\"" + token
//								+ "\",\"type\":\"" + type + "\",\"from\":\""
//								+ sender + "\",\"to\":\"" + receiver
//								+ "\",\"content\":\"" + messageString
//								+ "\",\"msguuid\":\"" + msguuid + "\"}";
//				} else if (token == 14) {
//					sendContent = "{\"token\":\"" + token + "\",\"type\":\""
//							+ type + "\",\"from\":\"" + sender + "\",\"to\":\""
//							+ receiver + "\",\"task_id\":\"" + tasktype// represent
//																		// taskID
//							+ "\",\"msguuid\":\"" + msguuid + "\"}";
//				} else if (token == 15) {
//					sendContent = "{\"from\":\"" + sender + "\",\"ids\":\""
//							+ receiver + "\",\"group_name\":\"" + ""
//							+ "\",\"msguuid\":\"" + msguuid
//							+ "\",\"status\":\"" + "0" + "\",\"token\":\""
//							+ token + "\"}";
//				} else if (token == 16) {
//					sendContent = "{\"token\":\"" + token + "\",\"from\":\""
//							+ sender + "\",\"to\":\"" + receiver
//							+ "\",\"ids\":\"" + messageString
//							+ "\",\"msguuid\":\"" + msguuid + "\"}";
//				} else if (token == 17) {
//					sendContent = "{\"token\":\"" + token + "\",\"from\":\""
//							+ sender + "\",\"to\":\"" + receiver
//							+ "\",\"ids\":\"" + messageString
//							+ "\",\"msguuid\":\"" + msguuid + "\"}";
//				} else if (token == 22) {
//					String locationContent = "{\"longitude\":" + beginTime
//							+ ",\"latitude\":" + endTime + ",\"location\":\""
//							+ messageString + "\"}";
//					String longdate = Long.toString(System.currentTimeMillis());
//					sendContent = "{\"content\":\"" + DataUtil.ecodeBase64(locationContent.getBytes())
//							+ "\",\"from\":\"" + sender + "\",\"type\":\""
//							+ type + "\",\"to\":\"" + receiver
//							+ "\",\"offline\":\"" + "0" + "\",\"msguuid\":\""
//							+ msguuid + "\",\"token\":\"" + token
//							+ "\",\"longDate\":\""+ longdate + "\",smallImgPath\":\"" + "\"}";
//
//				} else if (token == 23) {
//					sendContent = "{\"token\":\"" + token + "\",\"from\":\""
//							+ sender + "\",\"type\":\"" + type + "\",\"to\":\""
//							+ receiver + "\",\"task_id\":\"" + messageString
//							+ "\",\"msguuid\":\"" + msguuid + "\"}";
//				} else if (token == 102) {
//					sendContent = "{\"token\":\"102\",\"user_id\":\""
//							+ UserInfo.db_id + "\"}";
//				} else if (token == 101) {
//					sendContent = "{\"token\":\"101\",\"user_id\":\""
//							+ UserInfo.db_id + "\"}";
//				}
//
//				Log.i("test", "sendContent : " + sendContent);
//				socket = instanceSocket();
//				if (socket == null)
//					return;
//				out = socket.getOutputStream();
//				out.write(sendContent.getBytes());
//				out.write(SocketConfig.WRIETEFLAGBYTES);
//				out.flush();
//
//				Log.e("test", "send over..............");
//
//				if (token == 102 || token == 101) {
//					socket = null;
//					if (UserInfo.timer != null) {
//						UserInfo.timer.cancel();
//						UserInfo.timer = null;
//					}
//				}
//				if (isRecreate) {// home√à√Æ√Ü√ã√∏√Æ√Ç√µ√ª√î¬∫√•√à√°√ß√ä√±‚àû√Ç¬™‚à´√Å¬¥√£√ã√∏√ª√ä√©‚Ä¢
//					// thread for receiving from server
//					Log.i("test", "recreate............");
//					UserInfo.isHomePressed = false;
//					ClientConServerThread ccst = new ClientConServerThread(
//							mContext, socket);
//					ccst.start();
//				}
//			} catch (Exception ex) {
//				closeSocket();
//				ex.printStackTrace();
//			}
//		}
//	}
//
//	public void SendAddOfficialAccount(String fromId, String toId) {
//		final String from_Id = fromId;
//		final String to_Id = toId;
//		UUID uuid = UUID.randomUUID();
//		if (socket == null) {
//			instanceSocket();
//		}
//		String contentString = "{\"from\":\"" + from_Id
//				+ "\",\"type\":\"4\",\"to\":\"" + to_Id + "\",\"msguuid\":\""
//				+ uuid + "\",\"token\":\"18\"}";
//
//		if (isConnected()) {
//			try {
//				out = socket.getOutputStream();
//
//				out.write(contentString.getBytes());
//				out.write(SocketConfig.WRIETEFLAGBYTES);
//				out.flush();
//				DBlog.e("ÂèëÈÄÅÊ∑ªÂä†Ê∂àÊÅØ", contentString);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//
//		}
//
//	}
//
//	public void SendCancelOfficialAccount(String fromId, String toId) {
//		final String from_Id = fromId;
//		final String to_Id = toId;
//		UUID uuid = UUID.randomUUID();
//		if (socket == null) {
//			instanceSocket();
//		}
//		String contentString = "{\"from\":\"" + from_Id
//				+ "\",\"type\":\"4\",\"to\":\"" + to_Id + "\",\"msguuid\":\""
//				+ uuid + "\",\"token\":\"24\"}";
//
//		if (isConnected()) {
//			try {
//				out = socket.getOutputStream();
//				out.write(contentString.getBytes());
//				out.write(SocketConfig.WRIETEFLAGBYTES);
//				out.flush();
//				DBlog.e("ÂèëÈÄÅÂèñÊ∂àÊ∂àÊÅØ", contentString);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//
//		}
//	}
//
//	public void sendLoginSure() {
//		sendContent = "{\"token\":\"0\",\"uid\":{\"username\":\"" + "YY001"
//				+ "\",\"password\":\"" + "C4CA4238A0B923820DCC509A6F75849B"
//				+ "\",\"device\":\"0\", \"devicetoken\":\"" + "" + "_" + ""
//				+ "\"}}";
//		try {
//			out = socket.getOutputStream();
//			out.write(sendContent.getBytes());
//			out.write(SocketConfig.WRIETEFLAGBYTES);
//			out.flush();
//			DBlog.e("sendLoginSure", sendContent);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//	public void addOrCancel(boolean isSuccess) {
//		Message message = Message.obtain();
//		Bundle bundle = new Bundle();
//		bundle.putBoolean("opt", isSuccess);
//		message.setData(bundle);
//		getHandler().sendMessage(message);
//	}
//}

public class ClientSocket {
	public static Socket socket = null;
	public OutputStream out = null;
	public String sendContent = null;
	private Context mContext;
	private static ExecutorService es;// threadpool
	private static final int THREAD_COUNT = 5;
	private static Handler handler;

	public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public ClientSocket(Context context) {
		mContext = context;
		WifiManager wifi = (WifiManager) mContext
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		UserInfo.mac_address = info.getMacAddress();
	}

	public ExecutorService instanceES() {
		if (es == null)
			es = Executors.newFixedThreadPool(THREAD_COUNT);
		return es;
	}

	public Socket instanceSocket() {
		if (socket == null /* || !isConnected() */) {
			try {
				Log.e("test", "new socket........................");
				socket = new Socket(SocketConfig.IPADDRESS, SocketConfig.PORT);
				socket.setKeepAlive(true);
			} catch (Exception ex) {
				ex.printStackTrace();
				sendLoginResultBroadCast(null, 405, 0);
				return null;
			}
		}
		return socket;
	}

	public boolean isConnected() {
		try {
			Log.e("test", "isConnected....................");
			socket.sendUrgentData(0xFF);
		} catch (Exception ex) {
			return false;
		}
		return true;
	}

	private void sendLoginResultBroadCast(String result, int code, int token) {
		Intent intent = new Intent();
		intent.setAction(MessageInfo.LoginResultBroadCast);
		intent.putExtra("result", result);
		intent.putExtra("code", code);
		intent.putExtra("token", token);
		mContext.sendBroadcast(intent);
	}

	public void closeSocket() {
		try {
			if (out != null)
				out.close();
			if (socket != null) {
				socket.close();
				socket = null;
			}

			if (UserInfo.timer != null) {
				UserInfo.timer.cancel();
				UserInfo.timer = null;
			}

			sendLoginResultBroadCast("", 407, 0);

			/*
			 * UserInfo.isSendBroadCast = false;
			 * 
			 * UserInfo.isRecreateConnection = true;
			 * Log.e("test","ClientSocket create.........");
			 * 
			 * sendMessage(null, 0, StringWidthWeightRandom.getNextString(),
			 * null, null, null, null, null, null, null, null, true);
			 */
		} catch (Exception e) {
			e.printStackTrace();
			socket = null;
		}
		
	}

	public void createClient(String userEmail, String cipherPassword) {
		final String email = userEmail;
		final String cipherPwd = cipherPassword;
		UserInfo.isHomePressed = false;
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					sendContent = "{\"token\":\"100\",\"uid\":{\"username\":\""
							+ email + "\",\"password\":\"" + cipherPwd
							+ "\",\"device\":\"0\", \"devicetoken\":\""
							+ UserInfo.push_userId + "_"
							+ UserInfo.push_channelId + "\"}}";

					Log.i("test", "send token 100 : " + sendContent);
					socket = instanceSocket();

					if (socket == null)
						socket = instanceSocket();
					if (socket == null)
						Log.e("FM", "socket is null");
					out = socket.getOutputStream();
					String string = sendContent;
					out.write(string.getBytes());
					out.write(SocketConfig.WRIETEFLAGBYTES);
					out.flush();
					Log.e("test", "send over..............");

					// thread for receiving from server
					ClientConServerThread ccst = new ClientConServerThread(
							mContext, socket);
					ccst.start();

				} catch (Exception ex) {
					ex.printStackTrace();
					createClient(email, cipherPwd);

				}
			}
		}).start();
	}

	public void sendMessage(String msgString, int msgToken, String msgId,
			String msgSender, String msgReceiver, String scheduleBeginTime,
			String scheduleEndTime, String scheduleAddress,
			String scheduleType, String msgType, String taskid,
			boolean isRecre, String fromname) {
		/*
		 * token = msgToken; sender = msgSender; receiver = msgReceiver;
		 * messageString = msgString; msguuid = msgId; msgstr = msgString;
		 * isRecreate = isRecre; type = msgType; tasktype = scheduleType;//
		 * task, meeting, other beginTime = scheduleBeginTime; endTime =
		 * scheduleEndTime; address = scheduleAddress; taskId = taskid;
		 */

		es = instanceES();
		Runnable runner = new ExecutorThread(msgString, msgToken, msgId,
				msgSender, msgReceiver, scheduleBeginTime, scheduleEndTime,
				scheduleAddress, scheduleType, msgType, taskid, isRecre,
				fromname);
		es.execute(runner);

	}

	public void sendMessage(String msgString, int msgToken, String msgId,
			String msgSender, String msgReceiver, String scheduleBeginTime,
			String scheduleEndTime, String scheduleAddress,
			String scheduleType, String msgType, String taskid, boolean isRecre) {
		/*
		 * new Thread(new Runnable() {
		 * 
		 * @Override public void run() { try { if (token == 0) { sendContent =
		 * "{\"token\":\"0\",\"uid\":{\"username\":\"" + UserInfo.id +
		 * "\",\"password\":\"" + UserInfo.cipher_password +
		 * "\",\"device\":\"0\", \"devicetoken\":\"" + UserInfo.push_userId +
		 * "_" + UserInfo.push_channelId + "\"}}"; } else if (token == 1) {
		 * sendContent = "{\"token\":\"" + token + "\",\"type\":\"" + type +
		 * "\",\"from\":\"" + sender + "\",\"to\":\"" + receiver +
		 * "\",\"content\":\"" + DataUtil.ecodeBase64(messageString.getBytes())
		 * + "\",\"msguuid\":\"" + msguuid + "\"}"; } else if (token == 2) {
		 * sendContent = msgstr; } else if (token == 7 || token == 8 || token ==
		 * 9 || token == 10) { sendContent = "{\"token\":\"" + token +
		 * "\",\"type\":\"" + type + "\",\"from\":\"" + sender + "\",\"to\":\""
		 * + receiver + "\",\"msguuid\":\"" + msguuid + "\"}"; } else if (token
		 * == 12) { if (type != null && type.equals("signature")) { sendContent
		 * = "{\"token\":\"" + token + "\",\"signature\":\"" +
		 * DataUtil.ecodeBase64(messageString .getBytes()) + "\",\"com_id\":\""
		 * + UserInfo.companyId + "\",\"from\":\"" + sender +
		 * "\",\"msguuid\":\"" + msguuid + "\"}"; } else if (type != null &&
		 * type.equals("mobile")) { sendContent = "{\"token\":\"" + token +
		 * "\",\"mobile\":\"" + messageString + "\",\"com_id\":\"" +
		 * UserInfo.companyId + "\",\"from\":\"" + sender + "\",\"msguuid\":\""
		 * + msguuid + "\"}"; } else if (type != null && type.equals("headUrl"))
		 * { sendContent = "{\"token\":\"" + token + "\",\"headurl\":\"" +
		 * messageString + "\",\"com_id\":\"" + UserInfo.companyId +
		 * "\",\"from\":\"" + sender + "\",\"msguuid\":\"" + msguuid + "\"}"; }
		 * } else if (token == 13) { if (tasktype != null)// for schedule
		 * sendContent = "{\"token\":\"" + token + "\",\"tasktype\":\"" +
		 * tasktype + "\",\"title\":\"" + DataUtil.ecodeBase64(messageString
		 * .getBytes()) + "\",\"address\":\"" + ((address == null ||
		 * address.length() == 0) ? "" : DataUtil.ecodeBase64(address
		 * .getBytes())) + "\",\"type\":\"" + type + "\",\"from\":\"" + sender +
		 * "\",\"to\":\"" + receiver + "\",\"btime\":\"" + beginTime +
		 * "\",\"etime\":\"" + endTime + "\",\"msguuid\":\"" + msguuid + "\"}";
		 * else // for group sendContent = "{\"token\":\"" + token +
		 * "\",\"type\":\"" + type + "\",\"from\":\"" + sender + "\",\"to\":\""
		 * + receiver + "\",\"content\":\"" + messageString +
		 * "\",\"msguuid\":\"" + msguuid + "\"}"; } else if (token == 14) {
		 * sendContent = "{\"token\":\"" + token + "\",\"type\":\"" + type +
		 * "\",\"from\":\"" + sender + "\",\"to\":\"" + receiver +
		 * "\",\"task_id\":\"" + tasktype// represent // taskID +
		 * "\",\"msguuid\":\"" + msguuid + "\"}"; } else if (token == 15) {
		 * sendContent = "{\"from\":\"" + sender + "\",\"ids\":\"" + receiver +
		 * "\",\"group_name\":\"" + "" + "\",\"msguuid\":\"" + msguuid +
		 * "\",\"status\":\"" + "0" + "\",\"token\":\"" + token + "\"}"; } else
		 * if (token == 16) { sendContent = "{\"token\":\"" + token +
		 * "\",\"from\":\"" + sender + "\",\"to\":\"" + receiver +
		 * "\",\"ids\":\"" + messageString + "\",\"msguuid\":\"" + msguuid +
		 * "\"}"; } else if (token == 17) { sendContent = "{\"token\":\"" +
		 * token + "\",\"from\":\"" + sender + "\",\"to\":\"" + receiver +
		 * "\",\"ids\":\"" + messageString + "\",\"msguuid\":\"" + msguuid +
		 * "\"}"; } else if (token == 23) { sendContent = "{\"token\":\"" +
		 * token + "\",\"from\":\"" + sender + "\",\"type\":\"" + type +
		 * "\",\"to\":\"" + receiver + "\",\"task_id\":\"" + messageString +
		 * "\",\"msguuid\":\"" + msguuid + "\"}"; } else if (token == 102) {
		 * sendContent = "{\"token\":\"102\",\"user_id\":\"" + UserInfo.db_id +
		 * "\"}"; } else if (token == 101) { sendContent =
		 * "{\"token\":\"101\",\"user_id\":\"" + UserInfo.db_id + "\"}"; }
		 * 
		 * Log.i("test", "sendContent : " + sendContent); socket =
		 * instanceSocket(); out = socket.getOutputStream();
		 * out.write(sendContent.getBytes());
		 * out.write(SocketConfig.WRIETEFLAGBYTES); out.flush(); if (token ==
		 * 102 || token == 101) { socket = null; if (UserInfo.timer != null) {
		 * UserInfo.timer.cancel(); UserInfo.timer = null; } } if (isRecreate)
		 * {// homeÈîÆËøîÂõûÔºåÈáçÊñ∞Âª∫Á´ãËøûÊé• // thread for receiving from
		 * server Log.i("test", "recreate............"); UserInfo.isHomePressed
		 * = false; ClientConServerThread ccst = new ClientConServerThread(
		 * mContext, socket); ccst.start(); }
		 * 
		 * } catch (Exception ex) { closeSocket(); ex.printStackTrace(); } }
		 * }).start();
		 */

		es = instanceES();
		Runnable runner = new ExecutorThread(msgString, msgToken, msgId,
				msgSender, msgReceiver, scheduleBeginTime, scheduleEndTime,
				scheduleAddress, scheduleType, msgType, taskid, isRecre, null);
		es.execute(runner);

	}

	public static String replaceBlank(String str) {
		String dest = "";
		if (str != null) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}

	class ExecutorThread implements Runnable {
		private int token;
		private String sender;
		private String receiver;
		private String messageString;
		private String msguuid;
		private String msgstr;
		private boolean isRecreate;
		private String type;// individual or group /*mobile or
							// signature*/
		private String tasktype;// task, meeting, other
		private String beginTime;
		private String endTime;
		private String address;
		private String taskId;
		private String fromname;

		public ExecutorThread(String msgString, int msgToken, String msgId,
				String msgSender, String msgReceiver, String scheduleBeginTime,
				String scheduleEndTime, String scheduleAddress,
				String scheduleType, String msgType, String taskid,
				boolean isRecre, String fromname) {
			this.token = msgToken;
			this.sender = msgSender;
			this.receiver = msgReceiver;
			this.messageString = msgString;
			this.msguuid = msgId;
			this.msgstr = msgString;
			this.isRecreate = isRecre;
			this.type = msgType;// individual or group /*mobile or
			// signature*/
			this.tasktype = scheduleType;// task, meeting, other
			this.beginTime = scheduleBeginTime;
			this.endTime = scheduleEndTime;
			this.address = scheduleAddress;
			this.taskId = taskid;
			this.fromname = fromname;
		}

		public void run() {
			try {
				String addMessage = "";
				if (fromname != null && !fromname.trim().equals("")) {
					addMessage = "\",\"fromName\":\""
							+ DataUtil.ecodeBase64(("附近的好友-" + fromname)
									.getBytes()) + "";
					DBlog.e("-------", addMessage);
				}
				if (token == 0) {
					sendContent = "{\"token\":\"0\",\"uid\":{\"username\":\""
							+ UserInfo.id + "\",\"password\":\""
							+ UserInfo.cipher_password
							+ "\",\"device\":\"0\", \"devicetoken\":\""
							+ UserInfo.push_userId + "_"
							+ UserInfo.push_channelId/* UserInfo.token */
							+ "\"}}";
				} else if (token == 1) {
					sendContent = "{\"token\":\""
							+ token
							+ addMessage
							+ "\",\"type\":\""
							+ type
							+ "\",\"from\":\""
							+ sender
							+ "\",\"to\":\""
							+ receiver
							+ "\",\"content\":\""
							+ replaceBlank(DataUtil.ecodeBase64(messageString
									.getBytes())) + "\",\"msguuid\":\""
							+ msguuid + "\"}";
					DBlog.e("ddd", sendContent);
				} else if (token == 2) {
					sendContent = msgstr;
				} else if (token == 7 || token == 8 || token == 9
						|| token == 10) {
					sendContent = "{\"token\":\"" + token + "\",\"type\":\""
							+ type + "\",\"from\":\"" + sender + "\",\"to\":\""
							+ receiver + "\",\"msguuid\":\"" + msguuid + "\"}";
				} else if (token == 12) {
					if (type != null && type.equals("signature")) {
						sendContent = "{\"token\":\""
								+ token
								+ "\",\"signature\":\""
								+ DataUtil
										.ecodeBase64(messageString.getBytes())
								+ "\",\"com_id\":\"" + UserInfo.companyId
								+ "\",\"from\":\"" + sender
								+ "\",\"msguuid\":\"" + msguuid + "\"}";
					} else if (type != null && type.equals("mobile")) {
						sendContent = "{\"token\":\"" + token
								+ "\",\"mobile\":\"" + messageString
								+ "\",\"com_id\":\"" + UserInfo.companyId
								+ "\",\"from\":\"" + sender
								+ "\",\"msguuid\":\"" + msguuid + "\"}";
					} else if (type != null && type.equals("headUrl")) {
						sendContent = "{\"token\":\"" + token
								+ "\",\"headurl\":\"" + messageString
								+ "\",\"com_id\":\"" + UserInfo.companyId
								+ "\",\"from\":\"" + sender
								+ "\",\"msguuid\":\"" + msguuid + "\"}";
					}
				} else if (token == 13) {
					if (tasktype != null) {
						if (taskId == null)
							sendContent = "{\"token\":\""
									+ token
									+ "\",\"tasktype\":\""
									+ tasktype
									+ "\",\"title\":\""
									+ DataUtil.ecodeBase64(messageString
											.getBytes())
									+ "\",\"address\":\""
									+ ((address == null || address.length() == 0) ? ""
											: DataUtil.ecodeBase64(address
													.getBytes()))
									+ "\",\"type\":\"" + type
									+ "\",\"from\":\"" + sender
									+ "\",\"to\":\"" + receiver
									+ "\",\"btime\":\"" + beginTime
									+ "\",\"etime\":\"" + endTime
									+ "\",\"msguuid\":\"" + msguuid + "\"}";
						else
							sendContent = "{\"token\":\""
									+ token
									+ "\",\"tasktype\":\""
									+ tasktype
									+ "\",\"title\":\""
									+ DataUtil.ecodeBase64(messageString
											.getBytes())
									+ "\",\"address\":\""
									+ ((address == null || address.length() == 0) ? ""
											: DataUtil.ecodeBase64(address
													.getBytes()))
									+ "\",\"type\":\"" + type
									+ "\",\"from\":\"" + sender
									+ "\",\"to\":\"" + receiver
									+ "\",\"task_id\":\"" + taskId
									+ "\",\"btime\":\"" + beginTime
									+ "\",\"etime\":\"" + endTime
									+ "\",\"msguuid\":\"" + msguuid + "\"}";
					}// for schedule
					else
						// for group
						sendContent = "{\"token\":\"" + token
								+ "\",\"type\":\"" + type + "\",\"from\":\""
								+ sender + "\",\"to\":\"" + receiver
								+ "\",\"content\":\"" + messageString
								+ "\",\"msguuid\":\"" + msguuid + "\"}";
				} else if (token == 14) {
					sendContent = "{\"token\":\"" + token + "\",\"type\":\""
							+ type + "\",\"from\":\"" + sender + "\",\"to\":\""
							+ receiver + "\",\"task_id\":\"" + tasktype// represent
																		// taskID
							+ "\",\"msguuid\":\"" + msguuid + "\"}";
				} else if (token == 15) {
					sendContent = "{\"from\":\"" + sender + "\",\"ids\":\""
							+ receiver + "\",\"group_name\":\"" + ""
							+ "\",\"msguuid\":\"" + msguuid
							+ "\",\"status\":\"" + "0" + "\",\"token\":\""
							+ token + "\"}";
				} else if (token == 16) {
					sendContent = "{\"token\":\"" + token + "\",\"from\":\""
							+ sender + "\",\"to\":\"" + receiver
							+ "\",\"ids\":\"" + messageString
							+ "\",\"msguuid\":\"" + msguuid + "\"}";
				} else if (token == 17) {
					sendContent = "{\"token\":\"" + token + "\",\"from\":\""
							+ sender + "\",\"to\":\"" + receiver
							+ "\",\"ids\":\"" + messageString
							+ "\",\"msguuid\":\"" + msguuid + "\"}";
				} else if (token == 22) {
					String locationContent = "{\"longitude\":" + beginTime
							+ ",\"latitude\":" + endTime + ",\"location\":\""
							+ messageString + "\"}";
					Log.e("FM", locationContent);
					sendContent = "{\"content\":\""
							+ replaceBlank(DataUtil.ecodeBase64(locationContent
									.getBytes())) + "\",\"from\":\"" + sender
							+ "\",\"type\":\"" + type + "\",\"to\":\""
							+ receiver + "\",\"msguuid\":\"" + msguuid
							+ "\",\"token\":\"" + token + "\"}";

				} else if (token == 23) {
					sendContent = "{\"token\":\"" + token + "\",\"from\":\""
							+ sender + "\",\"type\":\"" + type + "\",\"to\":\""
							+ receiver + "\",\"task_id\":\"" + messageString
							+ "\",\"msguuid\":\"" + msguuid + "\"}";
				} else if (token == 102) {
					sendContent = "{\"token\":\"102\",\"user_id\":\""
							+ UserInfo.db_id + "\"}";
				} else if (token == 101) {
					sendContent = "{\"token\":\"101\",\"user_id\":\""
							+ UserInfo.db_id + "\"}";
				}

				Log.i("test", "sendContent : " + sendContent);
				socket = instanceSocket();
				if (socket == null)
					return;
				out = socket.getOutputStream();
				out.write(sendContent.getBytes());
				out.write(SocketConfig.WRIETEFLAGBYTES);
				out.flush();

				Log.e("test", "send over..............");

				if (token == 102 || token == 101) {
					socket = null;
					if (UserInfo.timer != null) {
						UserInfo.timer.cancel();
						UserInfo.timer = null;
					}
				}
				if (isRecreate) {// homeÈîÆËøîÂõûÔºåÈáçÊñ∞Âª∫Á´ãËøûÊé•
					// thread for receiving from server
					Log.i("test", "recreate............");
					UserInfo.isHomePressed = false;
					ClientConServerThread ccst = new ClientConServerThread(
							mContext, socket);
					ccst.start();
				}
			} catch (Exception ex) {
				closeSocket();
				ex.printStackTrace();
			}
		}
	}

	public void SendAddOfficialAccount(String fromId, String toId) {
		final String from_Id = fromId;
		final String to_Id = toId;
		UUID uuid = UUID.randomUUID();
		if (socket == null) {
			instanceSocket();
		}
		String contentString = "{\"from\":\"" + from_Id
				+ "\",\"type\":\"4\",\"to\":\"" + to_Id + "\",\"msguuid\":\""
				+ uuid + "\",\"token\":\"18\"}";

		if (isConnected()) {
			try {
				out = socket.getOutputStream();

				out.write(contentString.getBytes());
				out.write(SocketConfig.WRIETEFLAGBYTES);
				out.flush();
				DBlog.e("发送添加消息", contentString);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	public void SendCancelOfficialAccount(String fromId, String toId) {
		final String from_Id = fromId;
		final String to_Id = toId;
		UUID uuid = UUID.randomUUID();
		if (socket == null) {
			instanceSocket();
		}
		String contentString = "{\"from\":\"" + from_Id
				+ "\",\"type\":\"4\",\"to\":\"" + to_Id + "\",\"msguuid\":\""
				+ uuid + "\",\"token\":\"24\"}";

		if (isConnected()) {
			try {
				out = socket.getOutputStream();
				out.write(contentString.getBytes());
				out.write(SocketConfig.WRIETEFLAGBYTES);
				out.flush();
				DBlog.e("发送取消消息", contentString);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	public void sendLoginSure() {
		sendContent = "{\"token\":\"0\",\"uid\":{\"username\":\"" + "YY001"
				+ "\",\"password\":\"" + "C4CA4238A0B923820DCC509A6F75849B"
				+ "\",\"device\":\"0\", \"devicetoken\":\"" + "" + "_" + ""
				+ "\"}}";
		try {
			out = socket.getOutputStream();
			out.write(sendContent.getBytes());
			out.write(SocketConfig.WRIETEFLAGBYTES);
			out.flush();
			DBlog.e("sendLoginSure", sendContent);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addOrCancel(boolean isSuccess) {
		Message message = Message.obtain();
		Bundle bundle = new Bundle();
		bundle.putBoolean("opt", isSuccess);
		message.setData(bundle);
		getHandler().sendMessage(message);
	}
}