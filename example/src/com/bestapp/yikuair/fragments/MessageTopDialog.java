package com.bestapp.yikuair.fragments;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import com.bestapp.yikuair.R;
import com.bestapp.yikuair.database.DBOpenHelper;
import com.bestapp.yikuair.utils.FriendEntity;

public class MessageTopDialog extends Activity {

	public static MessageTopDialog instance = null;
	private GridView gridView;
	private List<gridItemInfo> lstMenuItem = new ArrayList<gridItemInfo>();
	private DBOpenHelper dbOpenHelper;
	private Cursor cursor;
	private String userId;
	private FriendEntity entity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message_top_dialog);
		instance = this;
		Intent intent = getIntent();
		userId = intent.getStringExtra("userId");

		initFriendEntity();
		initView();
	}

	public void initFriendEntity() {
		entity = new FriendEntity();
		dbOpenHelper = new DBOpenHelper(this);
		cursor = dbOpenHelper.get(userId);
		if (cursor.moveToFirst()) {

			cursor.moveToFirst();
			String name = cursor.getString(2);
			String duty = cursor.getString(6);
			String signature = cursor.getString(9);
			String department = cursor.getString(7);
			String team = cursor.getString(12);
			String sex = cursor.getString(13);
			String mobile = cursor.getString(4);

			entity.setID(userId);
			entity.setRealName(name);
			entity.setDepartmentName(department);
			entity.setDuty(duty);
			entity.setSignature(signature);
			entity.setTeam(team);
			entity.setSex(sex);
			entity.setMobile(mobile);
		}
		dbOpenHelper.close();
		cursor.close();
	}

	public void initView() {
		gridView = (GridView) findViewById(R.id.menu_gridView);

		lstMenuItem.add(new gridItemInfo(getResources().getString(
				R.string.menu_phone), R.drawable.ico_phone));
		lstMenuItem.add(new gridItemInfo(getResources().getString(
				R.string.menu_data), R.drawable.ico_data));
		lstMenuItem.add(new gridItemInfo(getResources().getString(
				R.string.menu_shield), R.drawable.ico_forbiden));
		lstMenuItem.add(new gridItemInfo(getResources().getString(
				R.string.menu_invite), R.drawable.ico_invite));
		gridView.setAdapter(new gridViewAdapter(this, lstMenuItem));
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				switch (arg2) {
				case 0:
					String phoneNum = entity.getMobile();
					if (phoneNum == null || "".equals(phoneNum.trim())) {
						Toast.makeText(getApplicationContext(),
								getResources().getString(R.string.dial_error),
								Toast.LENGTH_SHORT).show();
					} else {
						Intent intent = new Intent(Intent.ACTION_CALL, Uri
								.parse("tel:" + phoneNum));
						startActivity(intent);
					}
					break;
				case 1:
					Intent intent = new Intent(MessageTopDialog.this,
							PersonalProfileActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("friendEntity", entity);
					intent.putExtras(bundle);
					startActivity(intent);
					overridePendingTransition(R.anim.in_from_right,
							R.anim.out_of_left);

					break;
				case 2:
					break;
				case 3:
					Intent intent_1 = new Intent(MessageTopDialog.this,
							TaskMemberActivity.class);
					startActivity(intent_1);
					overridePendingTransition(R.anim.in_from_right,
							R.anim.out_of_left);
					break;
				default:
					break;
				}
			}
		});

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return true;
	}
}
