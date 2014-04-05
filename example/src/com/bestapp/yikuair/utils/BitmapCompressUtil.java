package com.bestapp.yikuair.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Random;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Log;

public class BitmapCompressUtil {
	private final static int UUID_LENGTH = 15;

	public static final File FILE_SDCARD = Environment
			.getExternalStorageDirectory();

	public static Bitmap compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 70, baos);
		int options = 100;
		while (baos.toByteArray().length / 1024 > 100) {
			baos.reset();
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);
			options -= 10;
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
		return bitmap;
	}

	/*
	 * public static Bitmap getResizeImage(String srcPath) { float width = 85f;
	 * Bitmap bitmap = BitmapFactory.decodeFile(srcPath); Matrix matrix = new
	 * Matrix(); matrix.postScale(((float) width) / bitmap.getWidth(), ((float)
	 * width) / bitmap.getWidth()); Bitmap B = null; B =
	 * Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
	 * matrix, true); bitmap.recycle();
	 * 
	 * return compressImage(B); }
	 */

	public static Bitmap getResizeBitmap(Bitmap bitmap) {
		float width = UserInfo.screenWidth;
		Matrix matrix = new Matrix();
		matrix.postScale(((float) width) / bitmap.getWidth(), ((float) width)
				/ bitmap.getWidth());
		Bitmap B = null;
		B = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
		if (bitmap != null && !bitmap.isRecycled()) {
			bitmap = null;
		}

		return B;//compressImage(B);
	}

	public static Bitmap getResizeImage(Bitmap bitmap) {
		float width = 150f;
		Matrix matrix = new Matrix();
		matrix.postScale(((float) width) / bitmap.getWidth(), ((float) width)
				/ bitmap.getWidth());
		Bitmap B = null;
		B = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
		// bitmap.recycle();
		if (bitmap != null && !bitmap.isRecycled()) {
			bitmap = null;
		}
		return compressImage(B);
	}

	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	public static Bitmap rotateImageView(int angle, Bitmap bitmap) {

		if (bitmap == null)
			return null;
		// 旋转图片 动作
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
		if (bitmap != null && !bitmap.isRecycled()) {
			bitmap = null;
		}
		// bitmap.recycle();
		return resizedBitmap;
	}

	public static Bitmap decodeFileFromPath(String path) {
		BitmapFactory.Options bfOptions = new BitmapFactory.Options();
		bfOptions.inDither = false;
		bfOptions.inPurgeable = true;
		bfOptions.inInputShareable = true;
		bfOptions.inTempStorage = new byte[12 * 1024];
		// bfOptions.inJustDecodeBounds = true;  
		File file = new File(path);
		FileInputStream fs = null;
		try {
			fs = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		Bitmap bmp = null;
		if (fs != null)
			try {
				bmp = BitmapFactory.decodeFileDescriptor(fs.getFD(), null,
						bfOptions);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (fs != null) {
					try {
						fs.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		return compressImage(bmp);
	}
	
	public static String saveToLocal(Bitmap bm) {
		//String picPath = FILE_LOCAL + getNextString() + "photo.jpg";
		String picPath = FILE_SDCARD.toString() + File.separator + "yikuair"
				+ File.separator + getNextString() + "photo.jpg";
		
		File picFileDir = new File(FILE_SDCARD.toString() + File.separator + "yikuair");
		if (!picFileDir.exists()) {
			picFileDir.mkdir();
		}
		
		try {
			FileOutputStream fos = new FileOutputStream(picPath);
			bm.compress(CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return picPath;
	}

	public static String getNextString() {
		SimpleDateFormat sDateFormat = new SimpleDateFormat("hh:mm:ss");
		String date = sDateFormat.format(new java.util.Date());
		Random random = new Random();
		char[] chars = (UserInfo.mac_address + date).replace(":", "-").toCharArray();
		char[] data = new char[UUID_LENGTH];

		for (int i = 0; i < UUID_LENGTH; i++) {
			int index = random.nextInt(chars.length);
			data[i] = chars[index];
		}
		String s = new String(data);
		return s;
	}

	
}