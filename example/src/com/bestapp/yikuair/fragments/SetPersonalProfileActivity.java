package com.bestapp.yikuair.fragments;

import java.io.File;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
//import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bestapp.yikuair.R;
import com.bestapp.yikuair.fragments.ImageActivity.ImageBroadcastReceiver;
import com.bestapp.yikuair.utils.ImageLoaderOriginal;
import com.bestapp.yikuair.utils.MessageInfo;
import com.bestapp.yikuair.utils.SharedPreferencesUtil;
import com.bestapp.yikuair.utils.StringWidthWeightRandom;
import com.bestapp.yikuair.utils.UploadPhotoUtil;
import com.bestapp.yikuair.utils.UserInfo;
import com.umeng.analytics.MobclickAgent;

public class SetPersonalProfileActivity extends Activity {

	private EditText setSignature;
	private TextView name;
	private TextView duty;
	private TextView department;
	private ImageView photo;
	private SelectPicPopupWindow menuWindow;
	private static final int FLAG_CHOOSE_IMG = 5;
	private static final int FLAG_CHOOSE_PHONE = 6;
	private static final int FLAG_MODIFY_FINISH = 7;
	private static String localTempImageFileName = "";
	public static final String IMAGE_PATH = "yikuair";
	public static final File FILE_SDCARD = Environment
			.getExternalStorageDirectory();
	public static final File FILE_LOCAL = new File(FILE_SDCARD, IMAGE_PATH);
	public static final File FILE_PIC_SCREENSHOT = new File(FILE_LOCAL,
			"images/screenshots");
	private UploadPhotoUtil util;
	private String imagePath;
	private Dialog mDialog;
	private ClientSocket client;
	private LoginResultBroadcastReceiver lbr;
	private ImageBroadcastReceiver ibr;
	// private LocalBroadcastManager localBroadcastManager;
	private SharedPreferencesUtil shared;
	private String sig;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_personal_profile);
		mDialog = new AlertDialog.Builder(this).create();
		client = new ClientSocket(this);
		lbr = new LoginResultBroadcastReceiver();
		shared = new SharedPreferencesUtil(this);

		// register broadcast
		// localBroadcastManager = LocalBroadcastManager.getInstance(this);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(MessageInfo.LoginResultBroadCast);
		lbr = new LoginResultBroadcastReceiver();
		// getActivity().registerReceiver(lbr, intentFilter);
		registerReceiver(lbr, intentFilter);

		/*
		 * IntentFilter intentFilter2 = new IntentFilter();
		 * intentFilter2.addAction(MessageInfo.ImageBroadcast); ibr = new
		 * ImageBroadcastReceiver(); registerReceiver(ibr, intentFilter2);
		 */
		initView();
	}

	public void initView() {

		setSignature = (EditText) findViewById(R.id.et_set_signature);
		name = (TextView) findViewById(R.id.tv_staff_name);
		department = (TextView) findViewById(R.id.tv_staff_department);
		duty = (TextView) findViewById(R.id.tv_staff_duty);
		photo = (ImageView) findViewById(R.id.iv_staff_photo);
		photo.setOnClickListener(photoClick);
		name.setText(UserInfo.realName);
		duty.setText(UserInfo.duty);

		String dep = "";
		if (UserInfo.departmentName.equals(UserInfo.team))
			dep = UserInfo.departmentName;
		else
			dep = UserInfo.departmentName + "-" + UserInfo.team;
		department.setText(dep);
		if (UserInfo.LocalphotoPath != null
				&& UserInfo.LocalphotoPath.length() > 0) {
			Bitmap b = BitmapFactory.decodeFile(UserInfo.LocalphotoPath);
			photo.setBackgroundDrawable(new BitmapDrawable(b));
			// photo.setImageBitmap(b);
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				client.sendMessage(null, 0,
						StringWidthWeightRandom.getNextString(), null, null,
						null, null, null, null, null, null, false);
				break;
			default:
				break;
			}
		};
	};

	private OnClickListener photoClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.iv_staff_photo:
				String sdStatus = Environment.getExternalStorageState();
				if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
					Toast.makeText(SetPersonalProfileActivity.this,
							getString(R.string.sdcard_error),
							Toast.LENGTH_SHORT).show();
					return;
				}

				menuWindow = new SelectPicPopupWindow(
						SetPersonalProfileActivity.this, itemsOnClick);
				menuWindow.showAtLocation(findViewById(R.id.setting),
						Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == FLAG_CHOOSE_IMG && resultCode == RESULT_OK) {
			if (data != null) {
				Uri uri = data.getData();
				if (!TextUtils.isEmpty(uri.getAuthority())) {
					Cursor cursor = getContentResolver().query(uri,
							new String[] { MediaStore.Images.Media.DATA },
							null, null, null);
					if (null == cursor) {
						Toast.makeText(this, "图片没找到", 0).show();
						return;
					}
					cursor.moveToFirst();
					String path = cursor.getString(cursor
							.getColumnIndex(MediaStore.Images.Media.DATA));
					cursor.close();
					Log.i("test", "path=" + path);
					Intent intent = new Intent(this, CropImageActivity.class);
					intent.putExtra("path", path);
					startActivityForResult(intent, FLAG_MODIFY_FINISH);
				} else {
					Log.i("test", "path=" + uri.getPath());
					Intent intent = new Intent(this, CropImageActivity.class);
					intent.putExtra("path", uri.getPath());
					startActivityForResult(intent, FLAG_MODIFY_FINISH);
				}
			}
		} else if (requestCode == FLAG_CHOOSE_PHONE && resultCode == RESULT_OK) {
			File f = new File(FILE_PIC_SCREENSHOT, localTempImageFileName);
			Intent intent = new Intent(this, CropImageActivity.class);
			intent.putExtra("path", f.getAbsolutePath());
			startActivityForResult(intent, FLAG_MODIFY_FINISH);
		} else if (requestCode == FLAG_MODIFY_FINISH && resultCode == RESULT_OK) {
			if (data != null) {
				imagePath = data.getStringExtra("path");
				Log.i("test", "created bitmap path = " + imagePath);
				Bitmap b = BitmapFactory.decodeFile(imagePath);
				// photo.setBackgroundDrawable(new BitmapDrawable(b));
				UserInfo.LocalphotoPath = imagePath;
				util = new UploadPhotoUtil(this);
				new UploadPhotoTask().execute();
				shared.savePhotoUrl();
				photo.setImageBitmap(b);
			}
		}
	}

	private class UploadPhotoTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... arg0) {
			util.uploadFile(imagePath, StringWidthWeightRandom.getNextString());
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (!result) {
				util.uploadFile(imagePath,
						StringWidthWeightRandom.getNextString());
			} else {
				// maybe refresh
			}
		}

	}

	public void showRoundProcessDialog() {
		mDialog.show();
		mDialog.setCanceledOnTouchOutside(false);
		mDialog.setContentView(R.layout.loading_process_dialog);
	}

	public boolean checkHeadUrl() {
		String sdStatus = Environment.getExternalStorageState();
		if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
			Toast.makeText(SetPersonalProfileActivity.this,
					getString(R.string.sdcard_error), Toast.LENGTH_SHORT)
					.show();
			return true;
		} else if (UserInfo.LocalphotoPath == null
				|| UserInfo.LocalphotoPath.length() == 0) {
			return false;
		} else
			return true;
	}

	public void showToast() {
		Toast.makeText(getApplication(),
				getApplication().getString(R.string.prompt_set_head),
				Toast.LENGTH_SHORT).show();
	}

	public void toSkip(View view) {
		// if (!checkHeadUrl()) {
		// showToast();
		// return;
		// }

		Log.i("FM", "sure pressed");
		sig = setSignature.getText().toString();
		UserInfo.signature = sig;
		// new ToSkipTask().execute();
		client.sendMessage(sig, 12, StringWidthWeightRandom.getNextString(),
				UserInfo.db_id, null, null, null, null, null, "signature",
				null, false);
		if (!UserInfo.isFinishProcess) {
			client.sendMessage(null, 0,
					StringWidthWeightRandom.getNextString(), null, null, null,
					null, null, null, null, null, false);
		} else {
			client.sendMessage(null, 0,
					StringWidthWeightRandom.getNextString(), null, null, null,
					null, null, null, null, null, false);
		}
		enterMainActivity();

	}

	private class ToSkipTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... arg0) {
			client.sendMessage(sig, 12,
					StringWidthWeightRandom.getNextString(), UserInfo.db_id,
					null, null, null, null, null, "signature", null, false);
			if (!UserInfo.isFinishProcess) {
				client.sendMessage(null, 0,
						StringWidthWeightRandom.getNextString(), null, null,
						null, null, null, null, null, null, false);
			} else {
				client.sendMessage(null, 0,
						StringWidthWeightRandom.getNextString(), null, null,
						null, null, null, null, null, null, false);
			}
			return true;
		}

	}

	public void enterMainActivity() {
		Log.i("test", "enter.........");
		mDialog.dismiss();
		UserInfo.isLogin = true;
		shared.saveUserInfo();
		Intent intent = new Intent(this, ResponsiveUIActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.in_from_right, R.anim.out_of_left);
		finish();
	}

	class LoginResultBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			Log.i("test", "receive create connection result broadcast");
			int resultCode = arg1.getIntExtra("code", 0);
			int token = arg1.getIntExtra("token", 0);
			if (resultCode == 200) {
				if (token == 0) {
					Log.i("FM", "LoginResultBroadcastReceiver received");

					// enterMainActivity();
				}
			}
		}
	}

	@Override
	protected void onDestroy() {

		// unregisterReceiver(ibr);
		super.onDestroy();
		unregisterReceiver(lbr);
	}

	@Override
	public void onBackPressed() {
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
