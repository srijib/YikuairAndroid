package com.bestapp.yikuair.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.R.integer;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.ImageView;

class ImageAsynTask extends AsyncTask<Void, Void, byte[]>

{

	private String rootString = Environment.getExternalStorageDirectory()
			.getPath();
	public String ImageName = "";
	private ImageView view = null;
	private int type;
	private ImageDrawableTool drawableTool;

	public ImageAsynTask(String name, ImageView view, int type,
			ImageDrawableTool drawableTool) {
		this.ImageName = name;
		this.view = view;
		this.type = type;
		this.drawableTool = drawableTool;
	}

	@Override
	protected byte[] doInBackground(Void... params) {

		return loadImages(this.ImageName);
	}

	@SuppressWarnings("deprecation")
	protected void onPostExecute(byte[] result) {
		super.onPostExecute(result);
		if (result == null) {

			return;
		}

		File dir = new File(rootString + "/sys_init/");
		if (!dir.exists()) {
			dir.mkdirs();
		}
		String[] imagNames = ImageName.split("/");

		File bitmapFile = new File(rootString + "/sys_init/"
				+ imagNames[imagNames.length - 1]);
		if (!bitmapFile.exists()) {
			try {
				bitmapFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(bitmapFile);
			fos.write(result);
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		InputStream ins = new ByteArrayInputStream(result);

		if (type == 0) {
			view.setImageDrawable(Drawable.createFromStream(ins, null));
		} else {
			view.setBackgroundDrawable(Drawable.createFromStream(ins, null));
		}
		drawableTool.addDrawableToCache(result);
		result = null;
		view = null;
		System.gc();
		try {
			ins.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	public byte[] loadImages(String name) {
		String url = "http://192.168.1.2:8080/static" + name;
		try {
			URL ul = new URL(url);
			if (ul != null) {
				InputStream in = HttpUtils.getStreamFromURL(url);

				if (in != null) {
					ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
					byte[] buff = new byte[100];
					int rc = 0;
					while ((rc = in.read(buff, 0, 100)) > 0) {
						swapStream.write(buff, 0, rc);
					}
					byte[] in2b = swapStream.toByteArray();
					in.close();
					in = null;
					buff = null;
					swapStream.close();
					swapStream = null;
					System.gc();
					return in2b;

				} else {
					return null;

				}
			}

		} catch (IOException e) {

			e.printStackTrace();
		}

		return null;

	}

	interface ImageDrawableTool {
		public void addDrawableToCache(byte[] arg);
	}

}