package com.bestapp.yikuair.fragments;

import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.TimerTask;

import com.bestapp.yikuair.utils.UserInfo;

import android.util.Log;

public class SocketTimer extends TimerTask {
	private Socket socket = null;

	public SocketTimer(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			// if (!UserInfo.isHomePressed) {
			Log.i("test", "send pulse packet ");
			String message = "{\"token\":\"5\",\"user_id\":\"" + UserInfo.db_id
					+ "\"}";

			/*Selector selector = Selector.open();
			SocketChannel client = socket.getChannel();
			client.configureBlocking(false);
			ByteBuffer sendbuffer = ByteBuffer.allocate(40);
			sendbuffer.put(message.getBytes());
			sendbuffer.flip();
			client.write(sendbuffer);*/

			OutputStream out = socket.getOutputStream();

			Log.i("test", "pulse packet : " + message);
			UserInfo.isLogin = true;
			out.write(message.getBytes());
			out.write(SocketConfig.WRIETEFLAGBYTES);
			out.flush();
			// }
		} catch (Exception ex) {
			this.closeSocket();
			ex.printStackTrace();
		}
	}

	public void closeSocket() {
		try {
			// socket = null;

			this.cancel();

			// socket.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
