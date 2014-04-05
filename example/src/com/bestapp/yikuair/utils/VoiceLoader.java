package com.bestapp.yikuair.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class VoiceLoader extends Thread {

	private Handler handler = new Handler();
	private static String SDCARDPATH = Environment
			.getExternalStorageDirectory() + "/";
	private Context mContext;
	private ExecutorService executorService = Executors.newFixedThreadPool(2);
	public static String URL_PREFIX = "http://";

	public VoiceLoader(Context context) {
		mContext = context;
	}

	public void loadVoice(final String url, final int id, final View view,
			final String sec) {
		executorService.submit(new Runnable() {
			public void run() {

				if ((view == null) || (url == null)
						|| (url.equals("") || url.equals("null")))
					return;

				loadVoi(URL_PREFIX + url, id, view, sec);
			}
		});
	}

	private class VoiceSetting implements Runnable {
		private View mView;
		int mId;
		String second;

		public VoiceSetting(View view, int id, String sec) {
			mView = view;
			mId = id;
			second = sec;
		}

		@Override
		public void run() {

			if (mView != null) {
				mView.setVisibility(View.VISIBLE);
				((TextView) mView.findViewById(mId)).setText(second);
			}
		}
	}

	private void loadVoi(final String urlPath, final int id, final View view,
			final String sec) {
		OutputStream output = null;
		String fileName = null;
		try {
			Log.i("test", "urlPath :" + urlPath);
			
			String str[] = urlPath.split("\\/");
			
			if(str != null && str.length > 5){
				fileName = str[6];
			}
			
			URL url = new URL(urlPath);
			URLConnection conn = url.openConnection();
			conn.connect();
			File dirFile = new File(SDCARDPATH + "yikuair/");
			if (!dirFile.exists())
				dirFile.mkdirs();
			File f_voice = new File(SDCARDPATH + "yikuair/" + fileName);
			Log.i("voice path", urlPath);
			InputStream input = conn.getInputStream();
			if (!f_voice.exists()) {

				f_voice.createNewFile();
				output = new FileOutputStream(f_voice);

				byte[] voice_bytes = new byte[1024];
				int len1 = -1;
				while ((len1 = input.read(voice_bytes)) != -1) {
					output.write(voice_bytes, 0, len1);
					output.flush();
				}

				Log.i("test", "success path: " + f_voice.getAbsolutePath());
				output.close();
			}

			handler.post(new VoiceSetting(view, id, sec));
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
	}

}
