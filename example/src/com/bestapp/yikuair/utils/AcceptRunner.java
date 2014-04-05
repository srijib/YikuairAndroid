package com.bestapp.yikuair.utils;

import java.io.InputStream;
import org.json.JSONObject;

public class AcceptRunner extends Thread {
	public static String WRIETEFLAG = "_!@#$%^&*_";

	private InputStream in;
	private CallBacker mCallBacker;

	public AcceptRunner(InputStream in, CallBacker callBacker) {
		this.in = in;
		this.mCallBacker = callBacker;
	}

	@Override
	public void run() {
		try {

			String receiveString;
			byte[] content = new byte[240];
			byte[] arrayByte = null;
			int len = 0;
			while ((len = in.read(content)) != -1) {
				arrayByte = DataUtil.byteArray(arrayByte,
						DataUtil.subBytes(content, 0, len));
				int ablen = arrayByte.length;
				int flaglen = WRIETEFLAG.getBytes().length;
				byte[] byteCode = DataUtil.subBytes(arrayByte, ablen - flaglen,
						flaglen);
				boolean isEnd = DataUtil.isBytesEquals(byteCode,
						WRIETEFLAG.getBytes());
				if (isEnd) {
					receiveString = new String(DataUtil.subBytes(arrayByte, 0,
							ablen - flaglen));
					arrayByte = null;
					DBlog.e("�յ��������ظ�", "receive message" + receiveString);
					JSONObject object = new JSONObject(receiveString);
					if (object.has("message")) {
						String message = object.getString("message");
						if ("success".equals(message)) {
//							mCallBacker.sendLoginSure();
							DBlog.e("test", "sendLoginSure");
						}
					}
					if (object.has("token") && object.has("type")) {
						String token = object.getString("token");
						String type = object.getString("type");
						if ("6".equals(token) && "4".equals(type)) {
							mCallBacker.addOrCancel(true);
							DBlog.e("��ӻ�ɾ��ɹ�", "ok");
						} else if ("4".equals(type)) {
							mCallBacker.addOrCancel(false);
							DBlog.e("��ӻ�ɾ��ʧ��", "fair");
						}
					}

				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			DBlog.e("IOException", e.toString());
			mCallBacker.addOrCancel(false);
		}
	}

public	interface CallBacker {
/*		public void sendLoginSure();
*/
		public void addOrCancel(boolean isSuccess);
	}
}