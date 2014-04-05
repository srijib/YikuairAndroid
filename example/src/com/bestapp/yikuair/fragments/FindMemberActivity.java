package com.bestapp.yikuair.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.bestapp.yikuair.R;
import com.bestapp.yikuair.database.DBOpenHelper;
import com.bestapp.yikuair.utils.FindMemberUtil;
import com.bestapp.yikuair.utils.FriendEntity;
import com.bestapp.yikuair.utils.MessageInfo;
import com.bestapp.yikuair.utils.StringWidthWeightRandom;

public class FindMemberActivity extends Activity {
	private Dialog mDialog;
	private EditText edittext;
	private DBOpenHelper dbOpenHelper;
	private Cursor cursor;
	private FriendEntity entity;
	private FindResultBroadcastReceiver fbr;
	private Toast toast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_friend_layout);
		dbOpenHelper = new DBOpenHelper(this);
		mDialog = new AlertDialog.Builder(this).create();
		edittext = (EditText) findViewById(R.id.et_memberId);
		
		// register broadcast
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(MessageInfo.FindResultBroadCast);
		fbr = new FindResultBroadcastReceiver();
		registerReceiver(fbr, intentFilter);
	}
	
	class FindResultBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			Log.i("test", "receive find result broadcast");
			closeLoadingDialog();	
			int code = arg1.getIntExtra("code", 0);
			FriendEntity friEntity = (FriendEntity) arg1
					.getSerializableExtra("friendEntity");
			if(code == 200 && friEntity != null){
				sendToPersonalActivity(friEntity, true);
			}else if(code == 201){
				showToast(getApplication().getString(R.string.no_member_error));
			}else if(code == 500){
				showToast(getApplication().getString(R.string.service_error));
			}else{
				showToast(getApplication().getString(R.string.find_error));
			}			
		}
	}
	
	public void showToast(String str){
		toast = Toast.makeText(getApplication(),
				str,
				Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}	
	
	public void sendToPersonalActivity(FriendEntity friendEntity, boolean isNewMember){
		Intent intent = new Intent(FindMemberActivity.this, PersonalProfileActivity.class);
		intent.putExtra("friendEntity", friendEntity);
		intent.putExtra("isNewMember", isNewMember);
		startActivity(intent);
		overridePendingTransition(R.anim.in_from_right, R.anim.out_of_left);
	}

	public void beginSearchMember(View view) {
		String memberId = edittext.getText().toString();
		if(memberId.length() == 0){
			showToast(getResources().getString(R.string.member_null_error));
			return;
		}
		if (getResultFromLocal(memberId)) {
			closeLoadingDialog();
			Log.i("test","get from local success......");
			if (entity == null) {
				showToast(getApplication().getString(R.string.find_error));
				return;
			}
			sendToPersonalActivity(entity, false);
		} else {
			Log.i("test","get from network.........");
			FindMemberUtil util = new FindMemberUtil(this);
			util.requestFindResult(memberId);		
		}
	}

	public void showRoundProcessDialog() {
		mDialog.show();
		mDialog.setCanceledOnTouchOutside(false);
		mDialog.setContentView(R.layout.loading_process_dialog);
	}

	public boolean getResultFromLocal(String memberId) {
		showRoundProcessDialog();
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		cursor = db.rawQuery("select * from contactsTable where userid=?",
				new String[] { memberId.toString() });
		int count = cursor.getCount();
		
		if (cursor.moveToFirst()) {
			String id = cursor.getString(cursor.getColumnIndex("userid"));
			String name = cursor.getString(cursor.getColumnIndex("name"));
			String phone = cursor.getString(cursor.getColumnIndex("phone"));
			String mobile = cursor.getString(cursor.getColumnIndex("mobile"));
			String signature = cursor.getString(cursor.getColumnIndex("signature"));
			String email = cursor.getString(cursor.getColumnIndex("email"));
			String duty = cursor.getString(cursor.getColumnIndex("duty"));
			String department = cursor.getString(cursor.getColumnIndex("department"));
			String headUrl = cursor.getString(cursor.getColumnIndex("headURL"));
			String companyId = cursor.getString(cursor.getColumnIndex("companyid"));	
			String alpha = cursor.getString(cursor.getColumnIndex("alpha"));
			String team = cursor.getString(cursor.getColumnIndex("team"));
			String sex  = cursor.getString(cursor.getColumnIndex("sex"));
			String dbId = cursor.getString(cursor.getColumnIndex("dbId"));
			String pinyin = cursor.getString(cursor.getColumnIndex("pinyin"));
			String searchIndex = cursor.getString(cursor.getColumnIndex("searchindex"));

			entity = new FriendEntity(id, name, phone,
					mobile, email, duty, department,
					headUrl, signature, companyId, alpha, team, sex, dbId, pinyin, searchIndex);
		}
		cursor.close();
		dbOpenHelper.close();
		if (count > 0) {
			return true;
		} else
			return false;
	}

	public void closeLoadingDialog() {
		if (mDialog.isShowing())
			mDialog.dismiss();
	}

	public void quitActivity(View view) {
		finish();
		unregisterReceiver(fbr);
		overridePendingTransition(R.anim.in_from_left, R.anim.out_of_right);
	}

	@Override
	public void onBackPressed() {
		quitActivity(null);
	}
}
