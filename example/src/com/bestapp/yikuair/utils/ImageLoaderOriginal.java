package com.bestapp.yikuair.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class ImageLoaderOriginal {

	private ExecutorService executorService = Executors.newFixedThreadPool(2);
	private Handler handler = new Handler();
	private Context mContext;
	private BitmapFactory.Options mOptions;
	public static String URL_PREFIX = "http://";
	public static String SDCARDPATH = Environment.getExternalStorageDirectory()
			+ "/";

	public ImageLoaderOriginal(Context context) {
		mContext = context;
		mOptions = new BitmapFactory.Options();
		mOptions.inPurgeable = true;
		mOptions.inInputShareable = true;
	}

	// introduce thread pool to manage the multi-threaded
	public void loadImage(final String url, final int id, final View view) {
		executorService.submit(new Runnable() {
			public void run() {
				/*
				 * if ((view == null) || (url == null) || (url.equals("") ||
				 * url.equals("null"))) return;
				 */
				// Log.i("test","path : " + URL_PREFIX + url);
				if (url.contains(URL_PREFIX))
					loadPic(url, id, view);
				else
					loadPic(URL_PREFIX + url, id, view);
			}
		});
	}

	private void loadPic(final String url, final int id, final View view) {
		InputStream pictureInputStream = null;

		try {
			if (url == null)
				return;

			String str[] = url.split("\\/");

			if (str != null && str.length > 5) {
				try {
					String fileName = SDCARDPATH + "yikuair/" + str[5] + str[6];
					File dirFile = new File(fileName);
					if (dirFile.exists()) {
						Log.e("Test", "filename :" + fileName);
						Bitmap b = BitmapFactory.decodeFile(fileName);
						handler.post(new ImageBitmapSetting(b, view, id,
								fileName));
						return;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			pictureInputStream = new URL(url).openStream();
			Bitmap bitmap = BitmapFactory.decodeStream(pictureInputStream,
					null, mOptions);
			if (bitmap == null)
				return;

			handler.post(new ImageBitmapSetting(bitmap, view, id, url));

			return;

		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (pictureInputStream != null)
					pictureInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private class ImageBitmapSetting implements Runnable {
		private Bitmap mDrawable;
		private View mView;
		int mId;
		String imgPath;

		public ImageBitmapSetting(Bitmap imageBitmap, View view, int id,
				String url) {
			mDrawable = imageBitmap;
			mView = view;
			mId = id;
			imgPath = url;
		}

		@Override
		public void run() {
			if (mDrawable != null && mView != null) {
				((ImageView) mView.findViewById(mId))
						.setImageBitmap(BitmapCompressUtil
								.getResizeImage(mDrawable));
			} else if (mView == null && mId == 0 && mDrawable != null) {
				saveBitmap(imgPath, mDrawable);
			}
		}
	}

	public void saveBitmap(String bitmapName, Bitmap mBitmap) {
		// Log.i("test", "bitmapName :" + bitmapName);

		String fileName = null;

		String str[] = bitmapName.split("\\/");

		String sdStatus = Environment.getExternalStorageState();
		if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
			Log.e("TestFile", "SD card is not avaiable/writeable right now.");
			sendImageBroadcast(null);
			return;
		}
		File dirFile = new File(SDCARDPATH + "yikuair/");
		if (!dirFile.exists())
			dirFile.mkdirs();
		if (str != null && str.length > 5) {

			fileName = SDCARDPATH + "yikuair/" + str[5] + str[6];
			// File subdir = new File(fileName);
			// Log.i("test","local file path :" + fileName);
		}
		
		Log.e("test","bitmapname :" + bitmapName);
		Log.e("test","fileName ::" + fileName);
		FileOutputStream bos = null;
		try {
			bos = new FileOutputStream(fileName);
			mBitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
		} catch (Exception e) {
			e.printStackTrace();
			sendImageBroadcast(null);
		} finally {
			try {
				if (bos != null) {
					bos.flush();
					bos.close();
				}

			} catch (IOException e) {
				e.printStackTrace();
				sendImageBroadcast(null);

			}
		}
		// saveLocalUrl(bitmapName, fileName);
		sendImageBroadcast(fileName);
	}

	private void sendImageBroadcast(String path) {
		Intent intent = new Intent();
		intent.setAction(MessageInfo.ImageBroadcast);
		intent.putExtra("imgPath", path);
		mContext.sendBroadcast(intent);
	}
}