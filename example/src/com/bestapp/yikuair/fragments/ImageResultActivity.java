package com.bestapp.yikuair.fragments;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bestapp.yikuair.R;
import com.bestapp.yikuair.utils.BitmapCompressUtil;
import com.bestapp.yikuair.utils.UserInfo;

public class ImageResultActivity extends Activity implements OnClickListener {

	private Button mSend;
	private Button mCancel;
	private ImageView mImageView;
	private String path;
	private Bitmap mBitmap;
	private Bitmap resultBitmap;
	public int screenWidth = 0;
	public int screenHeight = 0;
	private final static int UUID_LENGTH = 15;

	public static final File FILE_SDCARD = Environment
			.getExternalStorageDirectory();

	public static final File FILE_LOCAL = new File(FILE_SDCARD,
			"yikuairPicture");

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_show);
		path = getIntent().getStringExtra("path");

		init();
	}

	private void init() { 
		getWindowWH();

		mSend = (Button) this.findViewById(R.id.btn_confirm);
		mCancel = (Button) this.findViewById(R.id.btn_cancel);
		mImageView = (ImageView) this.findViewById(R.id.img_show);

		try {
			mBitmap = createBitmap(path, screenWidth, screenHeight);
			Log.e("test", "path :" + path);
			if (mBitmap == null) {
				Toast.makeText(this, "没有找到图片", 0).show();
				finish();
			} else {
				resetImageView(mBitmap, path);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, "没有图片", 0).show();
			finish();
		}

		/*
		 * int degree = BitmapCompressUtil.readPictureDegree(path); Bitmap bmp =
		 * BitmapCompressUtil
		 * .getResizeBitmap(BitmapCompressUtil.decodeFileFromPath(path)); Bitmap
		 * bitmap = BitmapCompressUtil.rotateImageView(degree, bmp);
		 * mImageView.setImageBitmap(bitmap);
		 * 
		 * if (bitmap != null && !bitmap.isRecycled()) { bitmap = null; }
		 */
		mSend.setOnClickListener(this);
		mCancel.setOnClickListener(this);
	}

	/*
	 * public void saveBitmap(Bitmap bm) {
	 * 
	 * File f = new File(path); if (f.exists()) { f.delete(); } try {
	 * FileOutputStream out = new FileOutputStream(f);
	 * bm.compress(Bitmap.CompressFormat.PNG, 90, out); out.flush();
	 * out.close();
	 * 
	 * } catch (FileNotFoundException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } catch (IOException e) { // TODO Auto-generated
	 * catch block e.printStackTrace(); } }
	 */

	public String saveToLocal(Bitmap bm) {
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

	private void resetImageView(Bitmap b, String path) {
		// mImageView.clear();
		// mImageView.setImageBitmap(b);
		// mImageView.setImageBitmapResetBase(b, true);
		// mCrop = new CropImage(this, mImageView, mHandler);
		// mCrop.crop(b);

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
			default:
				degree = 0;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.e("test", "degree :" + degree);
		resultBitmap = rotateImageView(degree, b);
		if (resultBitmap != null)
			mImageView.setImageBitmap(resultBitmap);
	}

	public static Bitmap rotateImageView(int angle, Bitmap bitmap) {

		if (bitmap == null)
			return null;
		// 旋转图片 动作
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		if (bitmap != null && !bitmap.isRecycled()) {
			bitmap = null;
		}
		// bitmap.recycle();
		return resizedBitmap;
	}

	private void getWindowWH() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;
	}

	public Bitmap createBitmap(String path, int w, int h) {
		try {
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, opts);
			int srcWidth = opts.outWidth;
			int srcHeight = opts.outHeight;
			int destWidth = 0;
			int destHeight = 0;
			// Áº©ÊîæÁöÑÊØî‰æã
			double ratio = 0.0;
			if (srcWidth < w || srcHeight < h) {
				ratio = 0.0;
				destWidth = srcWidth;
				destHeight = srcHeight;
			} else if (srcWidth > srcHeight) {// ÊåâÊØî‰æãËÆ°ÁÆóÁº©ÊîæÂêéÁöÑÂõæÁâáÂ§ßÂ∞èÔºåmaxLengthÊòØÈïøÊàñÂÆΩÂÖÅËÆ∏ÁöÑÊúÄÂ§ßÈïøÂ∫¶
				ratio = (double) srcWidth / w;
				destWidth = w;
				destHeight = (int) (srcHeight / ratio);
			} else {
				ratio = (double) srcHeight / h;
				destHeight = h;
				destWidth = (int) (srcWidth / ratio);
			}
			BitmapFactory.Options newOpts = new BitmapFactory.Options();
			// Áº©ÊîæÁöÑÊØî‰æãÔºåÁº©ÊîæÊòØÂæàÈöæÊåâÂáÜÂ§áÁöÑÊØî‰æãËøõË°åÁº©ÊîæÁöÑÔºåÁõÆÂâçÊàëÂè™ÂèëÁé∞Âè™ËÉΩÈÄöËøáinSampleSizeÊù•ËøõË°åÁº©ÊîæÔºåÂÖ∂ÂÄºË°®ÊòéÁº©ÊîæÁöÑÂÄçÊï∞ÔºåSDK‰∏≠Âª∫ËÆÆÂÖ∂ÂÄºÊòØ2ÁöÑÊåáÊï∞ÂÄº
			newOpts.inSampleSize = (int) ratio + 1;
			// inJustDecodeBoundsËÆæ‰∏∫falseË°®Á§∫ÊääÂõæÁâáËØªËøõÂÜÖÂ≠ò‰∏≠
			newOpts.inJustDecodeBounds = false;
			newOpts.outHeight = destHeight;
			newOpts.outWidth = destWidth;
			// Ëé∑ÂèñÁº©ÊîæÂêéÂõæÁâá
			return BitmapFactory.decodeFile(path, newOpts);
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_cancel:
			finish();
			break;
		case R.id.btn_confirm:
			Intent intent = new Intent();
			if(resultBitmap == null)
				break;
			String smallImgPath = saveToLocal(BitmapCompressUtil
					.getResizeImage(resultBitmap));
			String bigImgPath = saveToLocal(BitmapCompressUtil.compressImage(resultBitmap));

			intent.putExtra("smallImgPath", smallImgPath);

			intent.putExtra("bigImgPath", bigImgPath);
			
			Log.e("test","smallimgPath :::::" + smallImgPath);

			Log.e("test","bigimgPath :::::" + bigImgPath);
			
			setResult(RESULT_OK, intent);
			finish();
			break;
		}
	}
}