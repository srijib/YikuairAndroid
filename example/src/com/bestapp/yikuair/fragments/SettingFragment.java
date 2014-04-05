package com.bestapp.yikuair.fragments;

import java.io.File;
import java.util.Date;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bestapp.yikuair.R;
import com.bestapp.yikuair.utils.MessageInfo;
import com.bestapp.yikuair.utils.SharedPreferencesUtil;
import com.bestapp.yikuair.utils.StringWidthWeightRandom;
import com.bestapp.yikuair.utils.UploadPhotoUtil;
import com.bestapp.yikuair.utils.UserInfo;

public class SettingFragment extends Fragment {

	private RelativeLayout rl_photo;
	private RelativeLayout rl_signature;
	private RelativeLayout rl_reset_password;
	private RelativeLayout rl_change_mobile;
	private SelectPicPopupWindow menuWindow;
	private static final int FLAG_CHOOSE_IMG = 5;
	private static final int FLAG_CHOOSE_PHONE = 6;
	private static final int FLAG_MODIFY_FINISH = 7;
	private static final int FLAG_SET_SIGNATURE = 8;
	private static final int FLAG_SET_MOBILE = 9;
	private static String localTempImageFileName = "";
	private ImageView head;
	private ExitSettingsPW exitPW;
	private TextView tv_signature;
	private TextView tv_mobile;
	private Button exitBtn;
	private SharedPreferencesUtil shared;
	private ClientSocket client;

	public static final String IMAGE_PATH = "yikuair";
	public static final File FILE_SDCARD = Environment
			.getExternalStorageDirectory();
	public static final File FILE_LOCAL = new File(FILE_SDCARD, IMAGE_PATH);
	public static final File FILE_PIC_SCREENSHOT = new File(FILE_LOCAL,
			"images/screenshots");

	public static SettingFragment instance = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_settings, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		shared = new SharedPreferencesUtil(getActivity());
		instance = this;
		initView();
	}

	public static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);
		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}
		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,

	int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;
		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));
		if (upperBound < lowerBound) {
			return lowerBound;
		}
		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	public void initView() {
		Log.i("test", "initview");
		shared = new SharedPreferencesUtil(getActivity());
		client = new ClientSocket(getActivity());

		exitBtn = (Button) getActivity().findViewById(R.id.exit_btn);
		rl_photo = (RelativeLayout) getActivity().findViewById(
				R.id.rl_setting_photo);
		rl_signature = (RelativeLayout) getActivity().findViewById(
				R.id.rl_setting_signature);
		rl_reset_password = (RelativeLayout) getActivity().findViewById(
				R.id.rl_setting_reset_password);
		rl_change_mobile = (RelativeLayout) getActivity().findViewById(
				R.id.rl_setting_change_mobile);
		head = (ImageView) getActivity().findViewById(R.id.iv_individual_photo);
		if (UserInfo.LocalphotoPath != null
				&& UserInfo.LocalphotoPath.length() > 0) {
			BitmapFactory.Options opts = new BitmapFactory.Options();

			opts.inJustDecodeBounds = true;

			BitmapFactory.decodeFile(UserInfo.LocalphotoPath, opts);

			opts.inSampleSize = computeSampleSize(opts, -1, 128 * 128);

			// 这里必然要将其设置回false，因为之前我们将其设置成了true

			opts.inJustDecodeBounds = false;

			try {

				Bitmap bmp = BitmapFactory.decodeFile(UserInfo.LocalphotoPath,
						opts);
				head.setImageBitmap(bmp);
			} catch (OutOfMemoryError err) {
			}
		} else {
			Log.i("test", "sex :" + UserInfo.sex);
			if (UserInfo.sex.equals("0")) {
				head.setImageDrawable(getResources().getDrawable(
						R.drawable.girl));
			} else {
				head.setImageDrawable(getResources()
						.getDrawable(R.drawable.boy));
			}
		}

		tv_signature = (TextView) getActivity().findViewById(
				R.id.tv_setting_signature);
		tv_mobile = (TextView) getActivity().findViewById(
				R.id.tv_setting_mobile);
		if (UserInfo.signature != null && UserInfo.signature.length() > 0)
			tv_signature.setText(UserInfo.signature);
		if (UserInfo.mobile != null && UserInfo.mobile.length() > 0)
			tv_mobile.setText(UserInfo.mobile);
		rl_photo.setOnClickListener(rlClick);
		rl_signature.setOnClickListener(rlClick);
		rl_reset_password.setOnClickListener(rlClick);
		rl_change_mobile.setOnClickListener(rlClick);
		exitBtn.setOnClickListener(rlClick);
	}

	private OnClickListener rlClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.rl_setting_photo:
				String sdStatus = Environment.getExternalStorageState();
				if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
					Toast.makeText(getActivity(),
							getString(R.string.sdcard_error),
							Toast.LENGTH_SHORT).show();
					return;
				}

				menuWindow = new SelectPicPopupWindow(getActivity(),
						itemsOnClick);
				menuWindow.showAtLocation(
						getActivity().findViewById(R.id.setting),
						Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
				break;
			case R.id.rl_setting_signature:
				Intent intent2 = new Intent(getActivity(),
						SetSignatureActivity.class);
				startActivityForResult(intent2, FLAG_SET_SIGNATURE);
				getActivity().overridePendingTransition(R.anim.in_from_right,
						R.anim.out_of_left);
				break;
			case R.id.rl_setting_reset_password:
				Intent intent3 = new Intent(getActivity(),
						SetPasswordActivity.class);
				startActivity(intent3);
				getActivity().overridePendingTransition(R.anim.in_from_right,
						R.anim.out_of_left);
				break;
			case R.id.rl_setting_change_mobile:
				Intent intent4 = new Intent(getActivity(),
						ChangeMobileActivity.class);
				startActivityForResult(intent4, FLAG_SET_MOBILE);
				getActivity().overridePendingTransition(R.anim.in_from_right,
						R.anim.out_of_left);
				break;
			case R.id.exit_btn:
				exit_settings();
				break;
			}
		}
	};

	private OnClickListener itemsOnClick = new OnClickListener() {

		public void onClick(View v) {
			menuWindow.dismiss();
			switch (v.getId()) {
			case R.id.btn_pick_photo:
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_PICK);
				intent.setType("image/*");
				startActivityForResult(intent, FLAG_CHOOSE_IMG);
				break;
			case R.id.btn_take_photo:
				String status = Environment.getExternalStorageState();
				if (status.equals(Environment.MEDIA_MOUNTED)) {
					try {
						localTempImageFileName = "";
						localTempImageFileName = String.valueOf((new Date())
								.getTime()) + ".png";
						File filePath = FILE_PIC_SCREENSHOT;
						if (!filePath.exists()) {
							filePath.mkdirs();
						}
						Intent intent1 = new Intent(
								android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
						File f = new File(filePath, localTempImageFileName);
						Uri u = Uri.fromFile(f);
						intent1.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
						intent1.putExtra(MediaStore.EXTRA_OUTPUT, u);
						startActivityForResult(intent1, FLAG_CHOOSE_PHONE);
					} catch (ActivityNotFoundException e) {

					}
				}
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == FLAG_CHOOSE_IMG && resultCode == -1) {
			if (data != null) {
				Uri uri = data.getData();
				if (!TextUtils.isEmpty(uri.getAuthority())) {
					Cursor cursor = getActivity().getContentResolver().query(
							uri, new String[] { MediaStore.Images.Media.DATA },
							null, null, null);
					if (null == cursor) {
						Toast.makeText(getActivity(), "图片没找到", 0).show();
						return;
					}
					cursor.moveToFirst();
					String path = cursor.getString(cursor
							.getColumnIndex(MediaStore.Images.Media.DATA));
					cursor.close();
					Log.i("test", "path=" + path);
					Intent intent = new Intent(getActivity(),
							CropImageActivity.class);
					intent.putExtra("path", path);
					startActivityForResult(intent, FLAG_MODIFY_FINISH);
				} else {
					Log.i("test", "path=" + uri.getPath());
					Intent intent = new Intent(getActivity(),
							CropImageActivity.class);
					intent.putExtra("path", uri.getPath());
					startActivityForResult(intent, FLAG_MODIFY_FINISH);
				}
			}
		} else if (requestCode == FLAG_CHOOSE_PHONE && resultCode == -1) {
			File f = new File(FILE_PIC_SCREENSHOT, localTempImageFileName);
			Intent intent = new Intent(getActivity(), CropImageActivity.class);
			intent.putExtra("path", f.getAbsolutePath());
			startActivityForResult(intent, FLAG_MODIFY_FINISH);
		} else if (requestCode == FLAG_MODIFY_FINISH && resultCode == -1) {
			if (data != null) {
				final String path = data.getStringExtra("path");
				Log.i("test", "path = " + path);
				Bitmap b = BitmapFactory.decodeFile(path);
				head.setImageBitmap(b);
				UserInfo.LocalphotoPath = path;
				sendHeadModifiedBroadcast(UserInfo.LocalphotoPath);

				UploadPhotoUtil util = new UploadPhotoUtil(getActivity());
				util.uploadFile(path, StringWidthWeightRandom.getNextString());
				shared.savePhotoUrl();
			}
		} else if (requestCode == FLAG_SET_SIGNATURE) {
			if (data == null)
				return;
			String signature = data.getExtras().getString("signature");
			if (signature != null && signature.length() > 0) {
				tv_signature.setText(signature);
			} else {
				tv_signature.setText("未填写");
			}
		} else if (requestCode == FLAG_SET_MOBILE) {
			if (data == null)
				return;
			String mobile = data.getExtras().getString("mobile");
			if (mobile != null && mobile.length() > 0) {
				tv_mobile.setText(mobile);
			} else {
				tv_mobile.setText("未设置");
			}
		}
	}

	private void sendHeadModifiedBroadcast(String headurl) {
		Log.e("test", "send head broadcast.........");
		Intent intent = new Intent();
		intent.setAction(MessageInfo.HeadModifieyBroadCastName);
		getActivity().sendBroadcast(intent);
	}

	public void onStart() {
		Log.i("test", "setting onstart....................");
		super.onStart();
		if (UserInfo.isHomePressed) {
			UserInfo.isSendBroadCast = false;
			if (client == null)
				client = new ClientSocket(getActivity());

			client.sendMessage(null, 0,
					StringWidthWeightRandom.getNextString(), null, null, null,
					null, null, null, null, null, true);
			UserInfo.isHomePressed = false;
		}
	}

	public void onResume() {
		Log.i("test", "setting onResume....................");
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.i("test", "setting onPause");
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.i("test", "setting onStop");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i("test", "setting onDestroy");
	}

	public void exit_settings() {
		exitPW = new ExitSettingsPW(getActivity());
		exitPW.showAtLocation(getActivity().findViewById(R.id.setting),
				Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
	}

}
