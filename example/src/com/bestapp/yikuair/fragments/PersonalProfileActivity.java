package com.bestapp.yikuair.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bestapp.yikuair.R;
import com.bestapp.yikuair.utils.DataUtil;
import com.bestapp.yikuair.utils.FriendEntity;
import com.bestapp.yikuair.utils.MessageInfo;
import com.bestapp.yikuair.utils.StringWidthWeightRandom;
import com.bestapp.yikuair.utils.UserInfo;

public class PersonalProfileActivity extends Activity {

	private FriendEntity entity;
	private TextView name;
	private TextView duty;
	private TextView department;
	private TextView signature;
	private String phoneNum;
	private Button shieldBtn;
	private Button callBtn;
	private Button sendMsgBtn;
	private ImageButton setBtn;
	private ImageView photo;
	private boolean isShield;
	private boolean isNewMember;
	private boolean isSelf = false;
	private int mScreenHeight;
	private RelativeLayout info;
	public static PersonalProfileActivity instance = null;
	public ImageLoader imageLoader;
	public boolean isFromChat;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		setContentView(R.layout.personal_profile);
		isNewMember = getIntent().getBooleanExtra("isNewMember", false);
		entity = (FriendEntity) getIntent()
				.getSerializableExtra("friendEntity");
		isFromChat = getIntent().getBooleanExtra("isFromChat", false);

		if (getIntent().getStringExtra("individualInfo") != null) {
			Log.i("test", "individual info");
			isSelf = true;
			setBtn = (ImageButton) findViewById(R.id.personal_right_btn);
			setBtn.setVisibility(View.VISIBLE);
		}

		imageLoader = new ImageLoader(this);

		name = (TextView) findViewById(R.id.tv_staff_name);
		duty = (TextView) findViewById(R.id.tv_staff_duty);
		duty.setVisibility(View.GONE);

		department = (TextView) findViewById(R.id.tv_staff_department);
		signature = (TextView) findViewById(R.id.tv_staff_signature);
		shieldBtn = (Button) findViewById(R.id.shield_btn);

		shieldBtn.setVisibility(View.GONE);

		callBtn = (Button) findViewById(R.id.call_btn);
		sendMsgBtn = (Button) findViewById(R.id.sendmsg_btn);
		photo = (ImageView) findViewById(R.id.iv_staff_photo);

		mScreenHeight = getWindowManager().getDefaultDisplay().getHeight();
		info = (RelativeLayout) findViewById(R.id.iv_info);
		Log.e("test", "mScreenHeight  :" + mScreenHeight);
		if (mScreenHeight <= 960) {
			// info.g
			// RelativeLayout.LayoutParams param = new
			// RelativeLayout.LayoutParams(info.getWidth(),150);
			// info.setLayoutParams(param);
			info.getLayoutParams().height = 360;
			info.invalidate();
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					230, 230);
			params.addRule(RelativeLayout.CENTER_HORIZONTAL);
			params.topMargin = 12;
			photo.setLayoutParams(params);

		}

		shieldBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!isShield) {
					shieldBtn.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.btn_delete_normal));
					shieldBtn.setText(getResources().getString(
							R.string.cancel_shield));
					shieldBtn.setTextColor(Color.WHITE);
					isShield = true;
				} else {
					shieldBtn.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.personal_profile_other_btn_background));
					shieldBtn.setText(getResources().getString(
							R.string.shield_colleague));
					shieldBtn.setTextColor(Color.BLACK);
					isShield = false;
				}
			}
		});

		initView();
	}

	public void showDialog() {

		new AlertDialog.Builder(this).setMessage("添加好友请求已发送")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// do something
						callBtn.setBackgroundDrawable(getResources()
								.getDrawable(
										R.drawable.personal_profile_btn_normal));
						callBtn.setText(getResources().getString(
								R.string.wait_check));
					}
				}).show();
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

	@SuppressLint("NewApi")
	public void initView() {
		if (isNewMember) {
			callBtn.setText(getResources().getString(R.string.add_mem));
			shieldBtn.setVisibility(View.GONE);
			sendMsgBtn.setVisibility(View.GONE);
			callBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					showDialog();
				}
			});
		}
		if (!isSelf && entity != null) {
			if (entity.getRealName() != null
					&& entity.getRealName().equals(
							getResources().getString(R.string.company_news))) {
				callBtn.setVisibility(View.GONE);
				sendMsgBtn.setVisibility(View.GONE);
				shieldBtn.setVisibility(View.GONE);
				TextView iv_rightBtn = (TextView) findViewById(R.id.tv_right_btn);
				iv_rightBtn.setVisibility(View.VISIBLE);
				iv_rightBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						sendMessage(v);
					}
				});
			}
			if (entity.getHeadUrl() != null
					&& entity.getHeadUrl().length() != 0) {
				Log.e("test", "headurl :" + entity.getHeadUrl());
				String headUrl = "http://" + UserInfo.downloadImgUrl
						+ entity.getHeadUrl();
				imageLoader.DisplayImage(headUrl, this, photo);
			} else {
				if (entity.getSex().equals("0"))
					photo.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.ico_girl));
				else
					photo.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.ico_boy));
			}

			name.setText(entity.getRealName());
			duty.setText(entity.getDuty());
			String dep = "";
			/*
			 * if (UserInfo.departmentName.equals(UserInfo.team)) dep =
			 * UserInfo.departmentName; else dep = UserInfo.departmentName + "-"
			 * + UserInfo.team;
			 */

			Log.e("test", "team :" + entity.getTeam());
			Log.e("test", "department :" + entity.getDepartmentName());
			if (entity.getDepartmentName().equals(entity.getTeam()))
				dep = entity.getDepartmentName();
			else {
				if (entity.getTeam() != null && entity.getTeam().length() > 0)
					dep = entity.getDepartmentName() + "-" + entity.getTeam();
				else
					dep = entity.getDepartmentName();
			}
			department.setText(dep);

			Log.e("test", "signature :" + entity.getSignature());

			if (entity.getSignature() != null
					&& !entity.getSignature().isEmpty()) {
				signature.setText(new String(DataUtil.decodeBase64(entity
						.getSignature())));
			} else {
				if (entity.getSex().equals("0"))
					signature.setText(getResources().getString(
							R.string.signature_woman_default));
				else
					signature.setText(getResources().getString(
							R.string.signature_man_default));
			}
			phoneNum = entity.getMobile();
		} else if (isSelf) {
			callBtn.setVisibility(View.GONE);
			sendMsgBtn.setVisibility(View.GONE);
			shieldBtn.setVisibility(View.GONE);

			Log.i("test", "UserInfo.realName :" + UserInfo.realName);
			Log.i("test", "UserInfo.duty :" + UserInfo.duty);
			name.setText(UserInfo.realName);
			duty.setText(UserInfo.duty);
			department.setText(UserInfo.departmentName);

			if (UserInfo.LocalphotoPath != null
					&& UserInfo.LocalphotoPath.length() > 0) {
				BitmapFactory.Options opts = new BitmapFactory.Options();

				opts.inJustDecodeBounds = true;

				BitmapFactory.decodeFile(UserInfo.LocalphotoPath, opts);

				opts.inSampleSize = computeSampleSize(opts, -1, 128 * 128);

				// 这里必然要将其设置回false，因为之前我们将其设置成了true

				opts.inJustDecodeBounds = false;

				try {

					Bitmap bmp = BitmapFactory.decodeFile(
							UserInfo.LocalphotoPath, opts);
					photo.setBackgroundDrawable(new BitmapDrawable(bmp));
				} catch (OutOfMemoryError err) {
				}
			} else {
				if (UserInfo.sex.equals("0"))
					photo.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.girl));
				else
					photo.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.boy));
			}

			if (UserInfo.signature != null && UserInfo.signature.length() > 0) {
				signature.setText(UserInfo.signature);
			} else {
				signature.setText(getResources().getString(
						R.string.signature_me_default));
			}
		}
	}

	public void callUp(View view) {
		if (phoneNum == null || "".equals(phoneNum.trim())) {
			if (entity.getSex().equals("1"))
				Toast.makeText(getApplicationContext(), "他没有公开电话",
						Toast.LENGTH_SHORT).show();
			else
				Toast.makeText(getApplicationContext(), "她没有公开电话",
						Toast.LENGTH_SHORT).show();
		} else {
			Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
					+ phoneNum));
			startActivity(intent);
		}
	}

	public void sendMessage(View view) {
		Intent intent = new Intent(this, ChatActivity.class);
		if (isFromChat) {
			finish();
			overridePendingTransition(R.anim.in_from_left, R.anim.out_of_right);
		} else {
			intent.putExtra("Id", entity.getDbId());
			intent.putExtra("name", entity.getRealName());
			startActivityForResult(intent, 1);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_of_left);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null
				|| (data.getExtras().getString("content") == null || data
						.getExtras().getString("content").length() == 0))
			return;

		if (entity != null) {
			ChatMsgEntity msgEntity = new ChatMsgEntity();
			msgEntity.setContent(data.getExtras().getString("content"));
			msgEntity.setTime(data.getExtras().getString("time"));
			msgEntity.setFullTime(data.getExtras().getString("fullTime"));
			msgEntity.setName(entity.getRealName());
			msgEntity.setReceiverId(entity.getDbId());
			msgEntity.setSenderId(UserInfo.db_id);// individual db id
			msgEntity.setStatus(MessageInfo.SEND_MESSAGE);
			msgEntity.setIsAdd(false);
			msgEntity.setIsComing(false);

			MessageInfo.messageEntityList.add(msgEntity);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		if (UserInfo.signature != null && UserInfo.signature.length() > 0) {
			signature.setText(UserInfo.signature);
		}
		if (UserInfo.isHomePressed) {
			ClientSocket client = new ClientSocket(this);
			UserInfo.isSendBroadCast = false;
			client.sendMessage(null, 0,
					StringWidthWeightRandom.getNextString(), null, null, null,
					null, null, null, null, null, true);
		}
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
				photo.setBackgroundDrawable(new BitmapDrawable(bmp));
			} catch (OutOfMemoryError err) {
			}
		}
		Log.i("test", "personalProfile onStart");
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.i("test", "personalprofile onResume");
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.i("test", "personalProfile onPause");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i("test", "personalProfile onDestroy");
	}

	public void backtoChat(View view) {
		finish();
		overridePendingTransition(R.anim.in_from_left, R.anim.out_of_right);
	}

	public void setPersonalInfo(View view) {
		Intent intent = new Intent(this, SettingActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.in_from_right, R.anim.out_of_left);
	}

	@Override
	public void onBackPressed() {
		backtoChat(null);
	}
}
