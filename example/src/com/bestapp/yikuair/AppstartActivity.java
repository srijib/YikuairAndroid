package com.bestapp.yikuair;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.baidu.android.pushservice.PushSettings;
import com.bestapp.yikuair.LoginActivity.ImageBroadcastReceiver;
import com.bestapp.yikuair.fragments.ChatActivity;
import com.bestapp.yikuair.fragments.ChatMsgEntity;
import com.bestapp.yikuair.fragments.ClientSocket;
import com.bestapp.yikuair.fragments.MessageFragment;
import com.bestapp.yikuair.fragments.ResponsiveUIActivity;
import com.bestapp.yikuair.fragments.ScheduleItemInfo;
import com.bestapp.yikuair.utils.DataUtil;
import com.bestapp.yikuair.utils.ImageLoaderOriginal;
import com.bestapp.yikuair.utils.MessageInfo;
import com.bestapp.yikuair.utils.PushUtils;
import com.bestapp.yikuair.utils.SharedPreferencesUtil;
import com.bestapp.yikuair.utils.StringWidthWeightRandom;
import com.bestapp.yikuair.utils.UserInfo;
import com.umeng.analytics.MobclickAgent;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;

public class AppstartActivity extends Activity {

	public class ImageBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.e("test", "AppStart receive image broadcast");
			String action = intent.getAction();
			String imgPath = null;
			if (action.equals(MessageInfo.ImageBroadcast)) {
				imgPath = intent.getStringExtra("imgPath");
			}
			if (imgPath != null)
				UserInfo.LocalphotoPath = imgPath;
			else
				Toast.makeText(getApplication(),
						getApplication().getString(R.string.sdcard_error),
						Toast.LENGTH_SHORT).show();
			isDownloadPhotoFinished = true;
		}
	}
	class LoginResultBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			Log.i("test", "receive appstart result broadcast");
			String result = arg1.getStringExtra("result");
			int resultCode = arg1.getIntExtra("code", 0);
			int token = arg1.getIntExtra("token", 0);
			String num = arg1.getStringExtra("num");
			Log.e("test", "resultCode  :" + resultCode);
			Log.e("test", "token :" + token);
			Log.e("test", "result ::" + result);
			if (resultCode == 200) {
				if (token == 100) {
					Log.i("test", "token == 100....................");
					readDataFromJson(result);
					Log.e("test", "token 0...........");
					isFirstLogin();
					client.sendMessage(null, 0,
							StringWidthWeightRandom.getNextString(), null,
							null, null, null, null, null, null, null, false);
				} else if (token == 200) {
					Log.e("test", "token == 200....................");
					client.createClient(userName, password);
				} else if (token == 0) {
					Log.e("test", "not first login in ");
					UserInfo.isRecreateConnection = true;
					enterMainActivity();
				} else {
					Log.e("test", "default.......");
					enterLoginActivity();
				}
			} else if (resultCode == 403) {
				/*
				 * Toast.makeText( getApplication(), getApplication().getString(
				 * R.string.login_safe_exception), Toast.LENGTH_SHORT).show();
				 */client.socket = null;
				UserInfo.isSendBroadCast = true;// avoid to not send lonin
												// broadcast
				UserInfo.isRecreateConnection = false;
			} else if (resultCode == 404) {
				/*
				 * Toast.makeText( getApplication(), getApplication().getString(
				 * R.string.login_pram_exception), Toast.LENGTH_SHORT).show();
				 */
				client.socket = null;
			} else if (resultCode == 405) {
				/*
				 * Toast.makeText(getApplication(),
				 * getApplication().getString(R.string.service_error),
				 * Toast.LENGTH_SHORT).show();
				 */
				client.socket = null;
				intent = new Intent(AppstartActivity.this, LoginActivity.class);
				startActivity(intent);
				finish();
			} else {
				/*
				 * Toast.makeText(getApplication(),
				 * getApplication().getString(R.string.network_error),
				 * Toast.LENGTH_SHORT).show();
				 */

				intent = new Intent(AppstartActivity.this, LoginActivity.class);
				startActivity(intent);
				finish();
			}
		}
	}
	public class ScheduleResultBroadReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.e("test", "appstart schedule del info receive image broadcast");
			String type = intent.getStringExtra("type");
			String from = intent.getStringExtra("sender");
			String to = intent.getStringExtra("to");
			String taskId = intent.getStringExtra("taskId");
			String sender = "";
			if (type.equals("1")) {// for individual
				sender = from;
			} else if (type.equals("2")) {
				if (MessageInfo.groupMap.containsKey(to)) {
					sender = MessageInfo.groupMap.get(to);
				}
			}
			// delete item from scheduleFragment

			MessageInfo.scheduleList.add(new ScheduleItemInfo(
					UserInfo.realName, null, null, null, null, null,
					UserInfo.db_id, 0, null, true, null, taskId, null));

			Log.e("test", "delete taskid :" + taskId);

			// if (type.equals("1")) {// for individual
			if (ChatActivity.instance != null
					&& ChatActivity.userId.equals(sender)) {
				boolean isExist = false;
				for (int j = 0; j < ChatActivity.mDataArrays.size(); j++) {
					if (ChatActivity.mDataArrays.get(j).getIsComing() == true
							&& ChatActivity.mDataArrays.get(j).getType() == MessageInfo.SCHEDULE
							&& ChatActivity.mDataArrays.get(j)
									.getScheduleTaskId().equals(taskId)) {
						Log.e("test", "find success.................");
						isExist = true;
						ChatActivity.mDataArrays.get(j).setCheckBackground(
								R.drawable.bt_confirm_pressed);
						ChatActivity.mDataArrays.get(j).setCheckStatus(
								getResources().getString(R.string.canceled));
						ChatActivity.mAdapter.notifyDataSetChanged();
						return;
					}
				}
				if (!isExist) {
					for (int j = 0; j < ChatActivity.lstMessage.size(); j++) {
						if (ChatActivity.lstMessage.get(j).getIsComing() == true
								&& ChatActivity.lstMessage.get(j).getType() == MessageInfo.SCHEDULE
								&& ChatActivity.lstMessage.get(j)
										.getScheduleTaskId().equals(taskId)) {
							isExist = true;
							ChatActivity.lstMessage.get(j).setCheckBackground(
									R.drawable.bt_confirm_pressed);
							ChatActivity.lstMessage.get(j)
									.setCheckStatus(
											getResources().getString(
													R.string.canceled));
							break;
						}
					}
				}
			} else {
				boolean isExist = false;
				for (int i = 0; i < MessageFragment.userList.size(); i++) {
					if (MessageFragment.userList.get(i).containsKey(sender)) {
						for (int j = 0; j < MessageFragment.userList.get(i)
								.get(sender).size(); j++) {
							if (MessageFragment.userList.get(i).get(sender)
									.get(j).getScheduleTaskId().equals(taskId)) {
								MessageFragment.userList
										.get(i)
										.get(sender)
										.get(j)
										.setCheckBackground(
												R.drawable.bt_confirm_pressed);
								MessageFragment.userList
										.get(i)
										.get(sender)
										.get(j)
										.setCheckStatus(
												getResources().getString(
														R.string.canceled));
								isExist = true;
								break;
							}
						}
						if (isExist) {
							break;
						}
					}
				}
				if (isExist)
					return;

				if (shared.readDataFromShared(sender + "_" + UserInfo.db_id) != null) {
					List<ChatMsgEntity> lstMessage = new ArrayList<ChatMsgEntity>();
					lstMessage = shared.readDataFromShared(sender + "_"
							+ UserInfo.db_id);
					for (int i = 0; i < lstMessage.size(); i++) {
						if (lstMessage.get(i).getType() == MessageInfo.SCHEDULE
								&& lstMessage.get(i).getScheduleTaskId() != null
								&& lstMessage.get(i).getScheduleTaskId()
										.equals(taskId)) {
							lstMessage.get(i).setCheckBackground(
									R.drawable.bt_confirm_pressed);
							lstMessage.get(i)
									.setCheckStatus(
											getResources().getString(
													R.string.canceled));
							shared.saveDatatoShared(sender + "_"
									+ UserInfo.db_id, lstMessage);
							break;
						}
					}
				}
			}
		}
	}
	public static AppstartActivity instance = null;
	private ClientSocket client;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				startEnterMainActivity();
				break;
			}
		};
	};
	private ImageBroadcastReceiver ibr = null;
	private Intent intent = null;
	private boolean isDownloadPhotoFinished = false;
	private boolean isEnterLogin = false;
	private LoginResultBroadcastReceiver lbr;

	private String password;

	private ScheduleResultBroadReceiver sbr = null;

	private SharedPreferencesUtil shared;

	private String userName;

	private void enterLoginActivity() {
		Intent intent = new Intent(this, LoginActivity.class);

		startActivity(intent);
		finish();
	}

	private void enterMainActivity() {
		if (!isDownloadPhotoFinished) {
			new Thread() {
				@Override
				public void run() {
					try {
						while (!isDownloadPhotoFinished) {
							// loading
						}
						Message message = new Message();
						message.what = 0;
						handler.sendMessage(message);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}.start();
		} else {
			startEnterMainActivity();
		}
	}

	private boolean isFirstLogin() {
		SharedPreferences setting = getSharedPreferences("yikuair", 0);
		Boolean user_first = setting.getBoolean("FIRST", true);
		if (user_first) {
			setting.edit().putBoolean("FIRST", false).commit();
			UserInfo.isFirstLogin = true;
			return true;
		} else {
			UserInfo.isFirstLogin = false;
			return false;
		}
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.appstart);
		instance = AppstartActivity.this;
		shared = new SharedPreferencesUtil(this);
		String[] loginStr = shared.getLoginInfo();
		client = new ClientSocket(this);
		if (loginStr != null)
			Log.e("test", "Loginstr.length :" + loginStr.length);
		if (loginStr != null && loginStr.length == 2 && loginStr[0] != null
				&& !loginStr[0].isEmpty() && loginStr[1] != null
				&& !loginStr[1].isEmpty()) {
			Log.e("test", "back.......................");
			Log.e("test", "login[0] :" + loginStr[0]);
			Log.e("test", "login[1] :" + loginStr[1]);

			if (LoginActivity.instance != null)
				LoginActivity.instance.finish();

			userName = loginStr[0];
			password = loginStr[1];
			if (lbr == null) {
				IntentFilter intentFilter = new IntentFilter();
				intentFilter.addAction(MessageInfo.LoginResultBroadCast);
				lbr = new LoginResultBroadcastReceiver();
				registerReceiver(lbr, intentFilter);
			}
			if (sbr == null) {
				IntentFilter intentFilter3 = new IntentFilter();
				intentFilter3.addAction(MessageInfo.ScheduleDelResultBroadCast);
				sbr = new ScheduleResultBroadReceiver();
				registerReceiver(sbr, intentFilter3);
			}
			if (ibr == null) {
				IntentFilter intentFilter2 = new IntentFilter();
				intentFilter2.addAction(MessageInfo.ImageBroadcast);
				ibr = new ImageBroadcastReceiver();
				registerReceiver(ibr, intentFilter2);
			}

			registerPushService();
		} else {
			if (loginStr != null && loginStr[0] != null) {
				userName = loginStr[0];
				UserInfo.id = userName;
			}
			Log.e("test", "enter loginactivity");
			isEnterLogin = true;
			/*
			 * AlphaAnimation aa = new AlphaAnimation(1.0f, 1.0f);
			 * aa.setDuration(4000); layout.startAnimation(aa);
			 */}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (lbr != null) {
			unregisterReceiver(lbr);
			lbr = null;
		}
		if (sbr != null) {
			unregisterReceiver(sbr);
			sbr = null;
		}
		if (ibr != null) {
			unregisterReceiver(ibr);
			ibr = null;
		}

		Log.e("test", "appstart onDestroy.....");
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				if (isEnterLogin) {
					intent = new Intent(AppstartActivity.this,
							LoginActivity.class);
					if (userName != null) {
						intent.putExtra("username", userName);
					}
					startActivity(intent);
					finish();
				}
			}
		}, 1500);
	}

	private void readDataFromJson(String jsonResult) {
		try {
			JSONObject jsonObj = new JSONObject(jsonResult)
					.getJSONObject("data");
			DisplayMetrics dm = new DisplayMetrics();
			dm = getResources().getDisplayMetrics();
			UserInfo.screenWidth = dm.widthPixels;
			UserInfo.screenHeight = dm.heightPixels;

			UserInfo.cipher_password = jsonObj.getString("password");
			UserInfo.clientsocket = client;
			UserInfo.realName = jsonObj.getString("realname");
			UserInfo.duty = jsonObj.getString("duty");
			UserInfo.companyId = jsonObj.getString("com_id");
			UserInfo.id = jsonObj.getString("username");
			UserInfo.sex = jsonObj.getString("sex");
			UserInfo.signature = new String(DataUtil.decodeBase64(jsonObj
					.getString("signature")));
			UserInfo.db_id = jsonObj.getString("id");
			UserInfo.mobile = jsonObj.getString("mobile");

			SharedPreferencesUtil shared = new SharedPreferencesUtil(this);
			String[] photoPath = shared.getPhotoUrl();
			if (photoPath != null && photoPath.length > 1
					&& photoPath[1].equals(jsonObj.getString("headurl"))) {
				Log.e("test", "photoPath[0] :" + photoPath[0]);
				UserInfo.LocalphotoPath = photoPath[0];
				isDownloadPhotoFinished = true;
			} else {
				UserInfo.headUrl = jsonObj.getString("headurl");
				ImageLoaderOriginal imageLoader = new ImageLoaderOriginal(this);
				if (UserInfo.headUrl != null && UserInfo.headUrl.length() > 0) {
					String imageUrl = UserInfo.downloadImgUrl
							+ jsonObj.getString("headurl");
					imageLoader.loadImage(imageUrl, 0, null);
					Log.e("test", "download photoimg .....................");
					isDownloadPhotoFinished = false;
				} else {
					Log.e("test", "photo image is null.....................");
					UserInfo.LocalphotoPath = null;
					UserInfo.headUrl = null;
					isDownloadPhotoFinished = true;
				}
			}

			String dep = jsonObj.getString("de_name");
			String[] str = dep.split("-");
			if (str != null && str.length > 0) {
				UserInfo.departmentName = str[0];
				if (str.length > 1)
					UserInfo.team = str[1];
				else {
					UserInfo.team = str[0];
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void registerPushService() {
		PushManager.startWork(this, PushConstants.LOGIN_TYPE_API_KEY,
				PushUtils.getMetaValue(this, "api_key"));
//		PushSettings.enableDebugMode(this, true);
	}

	private void startEnterMainActivity() {
		UserInfo.isLogin = true;
		shared.saveUserInfo();
		shared.savePhotoUrl();
		Intent intent = new Intent(this, ResponsiveUIActivity.class);
		if (client.socket == null)
			Log.e("test", "socket is null.....");
		else
			Log.e("test", "socket is not null.......");
		startActivity(intent);
		// finish();
	}

}