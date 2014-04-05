package com.bestapp.yikuair.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;

import android.widget.ImageView;

public class AsyncImageLoader implements ImageAsynTask.ImageDrawableTool {
	private String rootString = Environment.getExternalStorageDirectory()
			.getPath();
	private HashMap<String, SoftReference<Bitmap>> imageCache;
	private String imageName1;

	public AsyncImageLoader(Context mContext) {
		imageCache = new HashMap<String, SoftReference<Bitmap>>();
	}

	@SuppressWarnings("deprecation")
	public Bitmap loadBitmap(ImageView imageView, String imageNamePath,
			int type) {

		Bitmap drawable = null;
		String[] imageNames = imageNamePath.split("/");
		imageName1 = imageNames[imageNames.length - 1];
		if (imageCache.containsKey(imageName1)) {
			SoftReference<Bitmap> softReference = imageCache.get(imageName1);
			drawable = softReference.get();
		}
		if (drawable == null) {
			String bitmapName = imageName1 + "";
			File cacheDir = new File(rootString + "/sys_init/");
			File[] cacheFiles = cacheDir.listFiles();
			int i = 0;
			if (null != cacheFiles) {

				for (; i < cacheFiles.length; i++) {

					if (bitmapName.equals(cacheFiles[i].getName())) {

						break;
					}

				}

				if (i < cacheFiles.length) {
					try {
						InputStream in = new FileInputStream(cacheFiles[i]);
						drawable = BitmapFactory.decodeStream(in);
						// drawable = BitmapFactory.createFromStream(in, "src");
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			}
		}

		if (drawable == null) {

			new ImageAsynTask(imageNamePath, imageView, type, this).execute();

		}
		if (drawable != null) {
			if (type == 0) {
				// imageView.setImageDrawable(drawable);
				imageView.setImageBitmap(drawable);
			} else {
//				imageView.setBackgroundDrawable(drawable);
				imageView.setBackgroundDrawable(new BitmapDrawable(drawable));
			}

		}
		return drawable;
	}

	@Override
	public void addDrawableToCache(byte[] arg) {
	}
}