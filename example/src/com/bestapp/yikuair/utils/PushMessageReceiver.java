package com.bestapp.yikuair.utils;


import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.baidu.android.pushservice.PushConstants;
import com.bestapp.yikuair.database.DBOpenHelper;
import com.bestapp.yikuair.fragments.ChatMsgEntity;
import com.bestapp.yikuair.fragments.ResponsiveUIActivity;

/**
 * PushMessageReceiver
 */
public class PushMessageReceiver extends BroadcastReceiver {
	/** TAG to Log */
	public static final String TAG = PushMessageReceiver.class.getSimpleName();

	AlertDialog.Builder builder;
	Context mContext;

	/**
	 * @param context
	 *            Context
	 * @param intent
	 * 
	 */
	@Override
	public void onReceive(final Context context, Intent intent) {
		mContext = context;

		if (intent.getAction().equals(PushConstants.ACTION_MESSAGE)) {
			Log.i("FM10", "ACTION_MESSAGE");
			String message = intent.getExtras().getString(
					PushConstants.EXTRA_PUSH_MESSAGE_STRING);

			Log.i("test", "onMessage: " + message);
			Log.d("test",
					"EXTRA_EXTRA = "
							+ intent.getStringExtra(PushConstants.EXTRA_EXTRA));

			/*
			 * Intent responseIntent = null; responseIntent = new
			 * Intent(PushUtils.ACTION_MESSAGE);
			 * responseIntent.putExtra(PushUtils.EXTRA_MESSAGE, message);
			 * responseIntent.setClass(context, ResponsiveUIActivity.class);
			 * responseIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			 * context.startActivity(responseIntent);
			 */

		} else if (intent.getAction().equals(PushConstants.ACTION_RECEIVE)) {
			Log.i("FM10", "ACTION_RECEIVE");
			final String method = intent
					.getStringExtra(PushConstants.EXTRA_METHOD);

			int errorCode = intent.getIntExtra(PushConstants.EXTRA_ERROR_CODE,
					PushConstants.ERROR_SUCCESS);
			String content = "";
			if (intent.getByteArrayExtra(PushConstants.EXTRA_CONTENT) != null) {

				content = new String(
						intent.getByteArrayExtra(PushConstants.EXTRA_CONTENT));
			}
			

			/*
			 * Log.d(TAG, "onMessage: method : " + method); Log.d(TAG,
			 * "onMessage: result : " + errorCode); Log.d(TAG,
			 * "onMessage: content : " + content); Toast.makeText( context,
			 * "method : " + method + "\n result: " + errorCode +
			 * "\n content = " + content, Toast.LENGTH_SHORT) .show();
			 */

			Log.e("test", "push receiver content : " + content);

			try {
				JSONObject jsonContent = new JSONObject(content);
				JSONObject params = jsonContent
						.getJSONObject("response_params");
				String userid = params.getString("user_id");
				String channelid = params.getString("channel_id");
				Log.i("test", "userid: " + userid);
				Log.i("test", "channelid :" + channelid);
				UserInfo.push_userId = userid;
				UserInfo.push_channelId = channelid;
				sendLoginResultBroadCast(null, 200, 200);
			} catch (Exception e) {
				sendLoginResultBroadCast(null, 405, 0);
				e.printStackTrace();
			}

			/*
			 * Intent responseIntent = null; responseIntent = new
			 * Intent(PushUtils.ACTION_RESPONSE);
			 * responseIntent.putExtra(PushUtils.RESPONSE_METHOD, method);
			 * responseIntent.putExtra(PushUtils.RESPONSE_ERRCODE, errorCode);
			 * responseIntent.putExtra(PushUtils.RESPONSE_CONTENT, content);
			 * responseIntent.setClass(context,ResponsiveUIActivity.class);
			 * responseIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			 * context.startActivity(responseIntent);
			 */

		} else if (intent.getAction().equals(
				PushConstants.ACTION_RECEIVER_NOTIFICATION_CLICK)) {
			/*
			 * Log.d("test", "intent=" + intent.toUri(0)); Log.d("test",
			 * "EXTRA_EXTRA = " +
			 * intent.getStringExtra(PushConstants.EXTRA_EXTRA));
			 */

			DBOpenHelper dbOpenHelper = new DBOpenHelper(context);
			Cursor cursor;
			SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
			//
			Intent aIntent = new Intent();
			aIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			aIntent.setClass(context, ResponsiveUIActivity.class);
			aIntent.putExtra("isFromPush", true);
			String title = intent
					.getStringExtra(PushConstants.EXTRA_NOTIFICATION_TITLE);
			aIntent.putExtra(PushConstants.EXTRA_NOTIFICATION_TITLE, "消息");
			String content = intent
					.getStringExtra(PushConstants.EXTRA_NOTIFICATION_CONTENT);
			String[] str = content.split(":");
			if (str != null && str.length > 1) {
				content = str[1];
			}
			aIntent.putExtra(PushConstants.EXTRA_NOTIFICATION_CONTENT, content);

			Log.i("test", "content :" + content);

			String extraContents = intent.getExtras().getString(
					PushConstants.EXTRA_EXTRA);

			String token = "";
			String type = "";
			String from = "";
			String name = "";
			try {
				JSONObject contentJson = new JSONObject(extraContents);
				token = contentJson.getString("token");
				type = contentJson.getString("type");
				from = contentJson.getString("from");

				Log.i("test", "token :" + token);
				Log.i("test", "type :" + type);
				Log.i("test", "from :" + from);
			} catch (Exception e) {
				Log.d(TAG, "parse message as json exception " + e);
			}

			cursor = db.rawQuery("select * from contactsTable where dbid=?",
					new String[] { from });

			Log.i("test", "count is " + cursor.getCount());
			if (cursor.moveToFirst())
				name = cursor.getString(cursor.getColumnIndex("name"));
			// List<ChatMsgEntity>
			ChatMsgEntity entity = new ChatMsgEntity();
			entity.setType(Integer.valueOf(token));
			entity.setContent(content);
			entity.setSenderId(from);
			entity.setStatus(MessageInfo.RECEIVE_MESSAGE);
			entity.setName(name);

			dbOpenHelper.close();
			cursor.close();
			aIntent.putExtra("pushmessage", content);
			aIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			context.startActivity(aIntent);
		}
	}

	private void sendLoginResultBroadCast(String result, int code, int token) {
		Intent intent = new Intent();
		intent.setAction(MessageInfo.LoginResultBroadCast);
		intent.putExtra("code", code);
		intent.putExtra("token", token);
		mContext.sendBroadcast(intent);
	}
}
