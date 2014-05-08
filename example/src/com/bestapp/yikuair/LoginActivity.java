package com.bestapp.yikuair;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.bestapp.yikuair.R;
import com.bestapp.yikuair.fragments.ChatActivity;
import com.bestapp.yikuair.fragments.ChatMsgEntity;
import com.bestapp.yikuair.fragments.CheckActivity;
import com.bestapp.yikuair.fragments.ClientSocket;
import com.bestapp.yikuair.fragments.FirstSetPasswordActivity;
import com.bestapp.yikuair.fragments.MessageFragment;
import com.bestapp.yikuair.fragments.ResponsiveUIActivity;
import com.bestapp.yikuair.fragments.ScheduleItemInfo;
import com.bestapp.yikuair.utils.ContactUtil;
import com.bestapp.yikuair.utils.DataUtil;
import com.bestapp.yikuair.utils.ImageLoaderOriginal;
import com.bestapp.yikuair.utils.Md5Util;
import com.bestapp.yikuair.utils.MessageInfo;
import com.bestapp.yikuair.utils.NetworkDetectUtil;
import com.bestapp.yikuair.utils.PushUtils;
import com.bestapp.yikuair.utils.SharedPreferencesUtil;
import com.bestapp.yikuair.utils.StringWidthWeightRandom;
import com.bestapp.yikuair.utils.UserInfo;
import com.umeng.analytics.MobclickAgent;

public class LoginActivity extends Activity {

	private EditText email;
	private EditText password;
	private Button enterBtn;
	private ClientSocket client;
	private LoginResultBroadcastReceiver lbr = null;
	private String userEmail;
	private String userPassword;
	private String cipherPassword;
	private Dialog mDialog;
	private ContactUtil contactUtil;
	private String tempEmail;
	private String tempPassword;
	private ImageBroadcastReceiver ibr = null;
	private boolean isDownloadPhotoFinished = true;
	private SharedPreferencesUtil shared;
	private String newUrl;
	public static LoginActivity instance = null;
	// private int loginCount;
	private static final int maxLoginCount = 3;
	private ScheduleResultBroadReceiver sbr = null;
	private HomeKeyEventBroadCastReceiver receiver = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e("test", "login onCreate.............");
		setContentView(R.layout.login);
		String userName = getIntent().getStringExtra("username");

		String url = getIntent().getStringExtra("url");
		String num = getIntent().getStringExtra("editionNum");
		if (url != null && num != null) {
			Log.e("test", "url :" + url);
			Log.e("test", "num :" + num);
			newUrl = url;
			dialog(num);
		}

		instance = this;
		shared = new SharedPreferencesUtil(this);
		email = (EditText) findViewById(R.id.login_email);
		password = (EditText) findViewById(R.id.login_password);

		if (userName != null && userName.length() > 0) {
			email.setText(userName);
			password.requestFocus();
		}

		mDialog = new AlertDialog.Builder(this).create();
		client = new ClientSocket(this);
		contactUtil = new ContactUtil(this);
		enterBtn = (Button) findViewById(R.id.enterBtn);
		enterBtn.setOnClickListener(new android.view.View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				beginRequest();
			}

		});
	}

	public void registerPushService() {
		PushManager.startWork(this, PushConstants.LOGIN_TYPE_API_KEY,
				PushUtils.getMetaValue(this, "api_key"));
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				startFirstModifyPassword();
				break;
			case 2:
				startEnterMainActivity();
				break;
			case 3:
				dialog((String) msg.obj);
				break;
			}
		};
	};

	public void showRoundProcessDialog() {
		mDialog.show();
		mDialog.setCanceledOnTouchOutside(false);
		mDialog.setContentView(R.layout.loading_dialog);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null)
			return;
		if (data.getBooleanExtra("isRight", false)) {
			beginRequest();
		} else {
			showCheckDialog();
		}
	}

	public void showCheckDialog() {
		Intent intent = new Intent();
		intent.setClass(LoginActivity.this, CheckActivity.class);
		startActivityForResult(intent, 1);
	}

	public void beginRequest() {
		userEmail = email.getText().toString();
		userPassword = password.getText().toString();
		UserInfo.password = userPassword;

		if ((userEmail == null || userEmail.length() == 0)
				|| (userPassword == null || userPassword.length() == 0)) {
			Toast.makeText(getApplication(),
					getApplication().getString(R.string.login_null_error),
					Toast.LENGTH_SHORT).show();
			return;

		}
		showRoundProcessDialog();
		registerPushService();

		cipherPassword = Md5Util.MD5(userPassword);

		tempEmail = new String(userEmail);
		tempPassword = new String(cipherPassword);

		boolean networkState = NetworkDetectUtil.detect(this);
		if (!networkState) {
			Toast.makeText(getApplication(),
					getApplication().getString(R.string.network_unavailable),
					Toast.LENGTH_SHORT).show();
			closeLoadingDialog();
		}
	}

	// public void loginRequest(View view) {
	//
	// beginRequest();
	// }

	public void closeLoadingDialog() {
		// Log.i("test","close.............");
		if (mDialog.isShowing())
			mDialog.dismiss();
	}

	public class ImageBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.e("test", "Login receive image broadcast");
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

	public class ScheduleResultBroadReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.e("test", "schedule del info receive image broadcast");
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

	class LoginResultBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			Log.i("test", "receive login result broadcast");
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
					// loginCount = 0;
					readDataFromJson(result);
					// if (isFirstLogin()) {
					// Log.i("test", "first login");
					if (contactUtil != null) {
						contactUtil.requestContact();
					} else {
						contactUtil = new ContactUtil(LoginActivity.this);
						contactUtil.requestContact();
					}
					// setPassword();
					// } else {
					Log.e("test", "token 0...........");
					enterMainActivity();
					client.sendMessage(null, 0,
							StringWidthWeightRandom.getNextString(), null,
							null, null, null, null, null, null, null, false);
					// }
				} else if (token == 200) {
					Log.e("test", "token == 200....................");
					client.socket = null;
					client.createClient(tempEmail, tempPassword);
				} else if (token == 0 /*
									 * && !UserInfo.isFirstLogin &&
									 * !UserInfo.isRecreateConnection
									 */) {
					Log.e("test", "not first login in ");
					UserInfo.isRecreateConnection = true;
					enterMainActivity();
				} else if (token == 500) {// new edition broadcast
					newUrl = result;
					Message message = new Message();
					message.what = 3;
					message.obj = num;
					handler.sendMessage(message);
				}
			} else if (resultCode == 403) {
				Toast.makeText(
						getApplication(),
						getApplication().getString(
								R.string.login_safe_exception),
						Toast.LENGTH_SHORT).show();
				client.socket = null;
				UserInfo.isSendBroadCast = true;// avoid to not send lonin
												// broadcast
				UserInfo.isRecreateConnection = false;
				closeLoadingDialog();
			} else if (resultCode == 404) {
				Toast.makeText(
						getApplication(),
						getApplication().getString(
								R.string.login_pram_exception),
						Toast.LENGTH_SHORT).show();

				client.socket = null;
				closeLoadingDialog();
			} else if (resultCode == 405) {
				Toast.makeText(getApplication(),
						getApplication().getString(R.string.service_error),
						Toast.LENGTH_SHORT).show();

				client.socket = null;
				closeLoadingDialog();
			} else if (resultCode == 407) {
				UserInfo.isSendBroadCast = false;
				UserInfo.isRecreateConnection = true;

				Log.e("test", "recreate connection.........");
				if (client == null)
					client = new ClientSocket(LoginActivity.this);
				client.sendMessage(null, 0,
						StringWidthWeightRandom.getNextString(), null, null,
						null, null, null, null, null, null, true);

			} else {
				Log.e("test", "token is " + token);
				Toast.makeText(getApplication(),
						getApplication().getString(R.string.network_error),
						Toast.LENGTH_SHORT).show();

				client.socket = null;
				closeLoadingDialog();
			}
		}
	}

	private void setPassword() {
		if (!isDownloadPhotoFinished) {
			new Thread() {
				@Override
				public void run() {
					try {
						while (!isDownloadPhotoFinished) {
							// loading
						}
						Message message = new Message();
						message.what = 1;
						handler.sendMessage(message);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}.start();
		} else {
			startFirstModifyPassword();
		}
	}

	protected void dialog(String num) {
		AlertDialog.Builder builder = new Builder(this);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setMessage("检测到新版本" + num + " ，请点击升级");

		builder.setTitle(getResources().getString(R.string.notice));

		builder.setPositiveButton(getResources().getString(R.string.btn_add),
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Log.e("test", "newUrl :" + newUrl);
						Uri uri = Uri.parse("http://" + newUrl);
						Intent intent = new Intent(Intent.ACTION_VIEW, uri);
						startActivity(intent);
					}
				});

		builder.create().show();
	}

	private void startFirstModifyPassword() {
		closeLoadingDialog();
		shared.saveUserInfo();
		shared.savePhotoUrl();
		Intent intent = new Intent(this, FirstSetPasswordActivity.class);
		startActivity(intent);
		this.overridePendingTransition(R.anim.in_from_right, R.anim.out_of_left);
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

	private void enterMainActivity() {
		// if (!isDownloadPhotoFinished) {
		// new Thread() {
		// @Override
		// public void run() {
		// try {
		// while (!isDownloadPhotoFinished) {
		// // loading
		// }
		// Message message = new Message();
		// message.what = 2;
		// handler.sendMessage(message);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		// }.start();
		// } else {
		startEnterMainActivity();
		// }
	}

	private void startEnterMainActivity() {
		closeLoadingDialog();
		UserInfo.isLogin = true;
		shared.saveUserInfo();
		shared.savePhotoUrl();
		Intent intent = new Intent(this, ResponsiveUIActivity.class);
		if (client.socket == null)
			Log.e("test", "333333333333333333333333");
		else
			Log.e("test", "555555555555555555555555");
		startActivity(intent);
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
				Log.i("test", "photoPath[0] :" + photoPath[0]);
				UserInfo.LocalphotoPath = photoPath[0];
				isDownloadPhotoFinished = true;
			} else {
				UserInfo.headUrl = jsonObj.getString("headurl");
				ImageLoaderOriginal imageLoader = new ImageLoaderOriginal(this);
				if (UserInfo.headUrl != null && UserInfo.headUrl.length() > 0) {
					String imageUrl = UserInfo.downloadImgUrl
							+ jsonObj.getString("headurl");
					imageLoader.loadImage(imageUrl, 0, null);
					isDownloadPhotoFinished = false;
				} else {
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

	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			View v = getCurrentFocus();
			if (v != null)
				hideSoftInput(v.getWindowToken());
		}
		return super.dispatchTouchEvent(ev);
	}

	public void hideSoftInput(IBinder token) {
		if (token != null) {
			InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			im.hideSoftInputFromWindow(token,
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	public void unregisterBroadcast() {
		if (ibr != null) {
			unregisterReceiver(ibr);
			ibr = null;
		}
		if (receiver != null) {
			unregisterReceiver(receiver);
			receiver = null;
		}
		/*
		 * if (lbr != null) { unregisterReceiver(lbr); lbr = null; }
		 */
	}

	@Override
	public void onBackPressed() {
		UserInfo.cipher_password = null;
		Intent i = new Intent(Intent.ACTION_MAIN);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.addCategory(Intent.CATEGORY_HOME);
		startActivity(i);
	}

	class HomeKeyEventBroadCastReceiver extends BroadcastReceiver {
		static final String SYSTEM_REASON = "reason";
		static final String SYSTEM_HOME_KEY = "homekey";// home key
		static final String SYSTEM_RECENT_APPS = "recentapps";// long home key

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
				String reason = intent.getStringExtra(SYSTEM_REASON);
				if (reason != null) {
					if (reason.equals(SYSTEM_HOME_KEY)) {
						UserInfo.isHomePressed = true;
						Log.e("test", "login activity home键被点击");
						UserInfo.cipher_password = null;
					}
				}
			}
		}
	}

	public void onStart() {
		super.onStart();
		Log.e("test", "login onStart.....");

		if (email != null && email.getText() != null
				&& email.getText().toString().length() > 0) {
			password.requestFocus();
		}

		// register broadcast
		if (lbr == null) {
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(MessageInfo.LoginResultBroadCast);
			lbr = new LoginResultBroadcastReceiver();
			registerReceiver(lbr, intentFilter);
		}
		if (ibr == null) {
			IntentFilter intentFilter2 = new IntentFilter();
			intentFilter2.addAction(MessageInfo.ImageBroadcast);
			ibr = new ImageBroadcastReceiver();
			registerReceiver(ibr, intentFilter2);
		}
		if (sbr == null) {
			IntentFilter intentFilter3 = new IntentFilter();
			intentFilter3.addAction(MessageInfo.ScheduleDelResultBroadCast);
			sbr = new ScheduleResultBroadReceiver();
			registerReceiver(sbr, intentFilter3);
		}
		// 监听home键广播
		if (receiver == null) {
			receiver = new HomeKeyEventBroadCastReceiver();
			registerReceiver(receiver, new IntentFilter(
					Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
		}
	}

	protected void onStop() {
		super.onStop();
		Log.e("test", "login onStop.....");
		Log.e("test", "dbid :" + UserInfo.id);
		Log.e("test", "password :" + UserInfo.cipher_password);

		if (shared == null)
			shared = new SharedPreferencesUtil(this);
		shared.saveLoginInfo(UserInfo.id, UserInfo.cipher_password);

		unregisterBroadcast();
		password.setText("");
	}

	protected void onDestroy() {
		// unregisterBroadcast();
		super.onDestroy();
		if (sbr != null) {
			unregisterReceiver(sbr);
			sbr = null;
		}
		if (lbr != null) {
			unregisterReceiver(lbr);
			lbr = null;
		}

		instance = null;
		Log.e("test", "login onDestroy.....");
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}