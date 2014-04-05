package com.bestapp.yikuair.fragments;

import java.io.IOException;

import com.bestapp.yikuair.R;
import com.bestapp.yikuair.utils.CropImage;
import com.bestapp.yikuair.utils.CropImageView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * Ë£ÅÂâ™ÁïåÈù¢
 * 
 */
public class CropImageActivity extends Activity implements OnClickListener {

	private CropImageView mImageView;
	private Bitmap mBitmap;

	private CropImage mCrop;

	private Button mSave;
	private Button mCancel, rotateLeft, rotateRight;
	private String mPath = "CropImageActivity";
	private String TAG = "";
	public int screenWidth = 0;
	public int screenHeight = 0;

	private ProgressBar mProgressBar;

	public static final int SHOW_PROGRESS = 2000;

	public static final int REMOVE_PROGRESS = 2001;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case SHOW_PROGRESS:
				mProgressBar.setVisibility(View.VISIBLE);
				break;
			case REMOVE_PROGRESS:
				mHandler.removeMessages(SHOW_PROGRESS);
				mProgressBar.setVisibility(View.INVISIBLE);
				break;
			}

		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gl_modify_avatar);

		init();
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mBitmap != null) {
			mBitmap = null;
		}
	}

	private void init() {
		getWindowWH();
		mPath = getIntent().getStringExtra("path");
		mImageView = (CropImageView) findViewById(R.id.gl_modify_avatar_image);
		mSave = (Button) this.findViewById(R.id.gl_modify_avatar_save);
		mCancel = (Button) this.findViewById(R.id.gl_modify_avatar_cancel);
		rotateLeft = (Button) this
				.findViewById(R.id.gl_modify_avatar_rotate_left);
		rotateRight = (Button) this
				.findViewById(R.id.gl_modify_avatar_rotate_right);
		mSave.setOnClickListener(this);
		mCancel.setOnClickListener(this);
		rotateLeft.setOnClickListener(this);
		rotateRight.setOnClickListener(this);
		try {
			mBitmap = createBitmap(mPath, screenWidth, screenHeight);
			if (mBitmap == null) {
				Toast.makeText(CropImageActivity.this, "Ê≤°ÊúâÊâæÂà∞ÂõæÁâá", 0)
						.show();
				finish();
			} else {
				resetImageView(mBitmap, mPath);
			}
		} catch (Exception e) {
			Toast.makeText(CropImageActivity.this, "Ê≤°ÊúâÊâæÂà∞ÂõæÁâá", 0)
					.show();
			finish();
		}
		addProgressbar();
	}

	/**
	 * Ëé∑ÂèñÂ±èÂπïÁöÑÈ´òÂíåÂÆΩ
	 */
	private void getWindowWH() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;
	}

	private void resetImageView(Bitmap b, String path) {
		mImageView.clear();
		mImageView.setImageBitmap(b);
		mImageView.setImageBitmapResetBase(b, true);
		mCrop = new CropImage(this, mImageView, mHandler);
		mCrop.crop(b);

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

		mCrop.startRotate(degree);

	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.gl_modify_avatar_cancel:
			// mCrop.cropCancel();
			finish();
			break;
		case R.id.gl_modify_avatar_save:
			String path = mCrop.saveToLocal(mCrop.cropAndSave());
			Log.i(TAG, "Êà™ÂèñÂêéÂõæÁâáÁöÑË∑ØÂæÑÊòØ = " + path);
			Intent intent = new Intent();
			intent.putExtra("path", path);
			setResult(RESULT_OK, intent);
			finish();
			break;
		case R.id.gl_modify_avatar_rotate_left:
			mCrop.startRotate(270.f);
			break;
		case R.id.gl_modify_avatar_rotate_right:
			mCrop.startRotate(90.f);
			break;

		}
	}

	protected void addProgressbar() {
		mProgressBar = new ProgressBar(this);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		addContentView(mProgressBar, params);
		mProgressBar.setVisibility(View.INVISIBLE);
	}

	public Bitmap createBitmap(String path, int w, int h) {
		try {
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			// ËøôÈáåÊòØÊï¥‰∏™ÊñπÊ≥ïÁöÑÂÖ≥ÈîÆÔºåinJustDecodeBoundsËÆæ‰∏∫trueÊó∂Â∞Ü‰∏ç‰∏∫ÂõæÁâáÂàÜÈÖçÂÜÖÂ≠ò„ÄÇ
			BitmapFactory.decodeFile(path, opts);
			int srcWidth = opts.outWidth;// Ëé∑ÂèñÂõæÁâáÁöÑÂéüÂßãÂÆΩÂ∫¶
			int srcHeight = opts.outHeight;// Ëé∑ÂèñÂõæÁâáÂéüÂßãÈ´òÂ∫¶
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
			// ËÆæÁΩÆÂ§ßÂ∞èÔºåËøô‰∏™‰∏ÄËà¨ÊòØ‰∏çÂáÜÁ°ÆÁöÑÔºåÊòØ‰ª•inSampleSizeÁöÑ‰∏∫ÂáÜÔºå‰ΩÜÊòØÂ¶ÇÊûú‰∏çËÆæÁΩÆÂç¥‰∏çËÉΩÁº©Êîæ
			newOpts.outHeight = destHeight;
			newOpts.outWidth = destWidth;
			// Ëé∑ÂèñÁº©ÊîæÂêéÂõæÁâá
			return BitmapFactory.decodeFile(path, newOpts);
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}

}