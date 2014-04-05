package com.bestapp.yikuair.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.actionbarsherlock.view.MenuItem;
import com.bestapp.yikuair.R;
import com.bestapp.yikuair.utils.MessageInfo;
import com.bestapp.yikuair.utils.SharedPreferencesUtil;
import com.bestapp.yikuair.utils.StringWidthWeightRandom;
import com.bestapp.yikuair.utils.UserInfo;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class ResponsiveUIActivity extends SlidingFragmentActivity {

	public static ResponsiveUIActivity instance = null;
	private Fragment mContent;
	private HomeKeyEventBroadCastReceiver receiver = null;
	private boolean isHomePressed = false;
	private int backPressedNum = 0;
	private SharedPreferencesUtil shared;
	private final static String YIKUAIR_GROUP = "yikuair_group";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e("test", "responsiveUIActivity on create...........");
		instance = this;
		setContentView(R.layout.responsive_content_frame);
		if (getSupportActionBar() != null)
			getSupportActionBar().hide();

		ClientSocket client = new ClientSocket(this);
		if (client.socket != null)
			Log.e("test", "client.socket != null...................");
		else
			Log.e("test", "client.socket == null...................");

		// check if the content frame contains the menu frame
		if (findViewById(R.id.menu_frame) == null) {
			setBehindContentView(R.layout.menu_frame);
			getSlidingMenu().setSlidingEnabled(true);
			getSlidingMenu()
					.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE/* TOUCHMODE_FULLSCREEN */);
			// show home as up so we can toggle
			if (getSupportActionBar() != null)
				getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		} else {
			// add a dummy view
			View v = new View(this);
			setBehindContentView(v);
			getSlidingMenu().setSlidingEnabled(false);
			getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		}
		// set the Above View Fragment
		if (savedInstanceState != null)
			mContent = getSupportFragmentManager().getFragment(
					savedInstanceState, "mContent");
		if (mContent == null)
			mContent = new MessageFragment()/* BirdGridFragment(0) */;
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, mContent).commit();

		// set the Behind View Fragment
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame, new MenuFragment()).commit();

		// customize the SlidingMenu
		SlidingMenu sm = getSlidingMenu();
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setBehindScrollScale(0.25f);
		sm.setFadeDegree(0.25f);

		shared = new SharedPreferencesUtil(this);
		Log.e("test", "userinfo.dbid ::" + UserInfo.db_id);
		MessageInfo.groupMap = shared.readGroupMapFromShared(UserInfo.db_id
				+ "_" + YIKUAIR_GROUP);
	}

	@Override
	public void onBackPressed() {
		// super.onBackPressed();

		boolean isDel = false;
		for (int i = 0; i < MessageFragment.messageList.size(); i++) {
			if (MessageFragment.messageList.get(i).getIsDelShow() == View.VISIBLE) {
				MessageFragment.messageList.get(i).setIsDelShow(View.GONE);
				isDel = true;
			}
		}
		MessageFragment.lstAdapter.notifyDataSetChanged();
		if (isDel == true) {
			return;
		}

		if (backPressedNum == 0) {
			backPressedNum++;
			Toast.makeText(getApplication(),
					getApplication().getString(R.string.back_press_hint),
					Toast.LENGTH_SHORT).show();
		} else {
			/*
			 * backPressedNum = 0; Intent intent = new Intent();
			 * intent.setClass(ResponsiveUIActivity.this, ExitActivity.class);
			 * startActivity(intent);
			 */
			UserInfo.isHomePressed = true;
			ClientSocket client = new ClientSocket(this);
			client.sendMessage(null, 102,
					StringWidthWeightRandom.getNextString(), null, null, null,
					null, null, null, null, null, false);

			Intent i = new Intent(Intent.ACTION_MAIN);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.addCategory(Intent.CATEGORY_HOME);
			startActivity(i);
		}

		/*
		 * Intent i = new Intent(Intent.ACTION_MAIN);
		 * i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		 * i.addCategory(Intent.CATEGORY_HOME); startActivity(i);
		 */}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			toggle();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		getSupportFragmentManager().putFragment(outState, "mContent", mContent);
	}

	public void switchContent(final Fragment fragment) {
		mContent = fragment;
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, fragment).commit();
		Handler h = new Handler();
		h.postDelayed(new Runnable() {
			public void run() {
				getSlidingMenu().showContent();
			}
		}, 100);
	}

	public void openMainMenu(View v) {
		toggle();
	}

	@Override
	public void onStart() {
		super.onStart();

		boolean isFromPush = getIntent().getBooleanExtra("isFromPush", false);
		if (!isFromPush)
			isHomePressed = false;

		if (isHomePressed) {
			mContent = new MessageFragment() /* BirdGridFragment(0) */;
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.content_frame, mContent).commit();

			isHomePressed = false;
		}

		// 监听home键广播
		if (receiver == null) {
			receiver = new HomeKeyEventBroadCastReceiver();
			registerReceiver(receiver, new IntentFilter(
					Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
		}
		Log.i("test", "ResponsiveUIActivity onStart");
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.i("test", "ResponsiveUIActivity onResume");
//		if (UserInfo.isHomePressed) {
//			ClientSocket client = new ClientSocket(
//					ResponsiveUIActivity.this);
//			client.sendMessage(null, 0,
//					StringWidthWeightRandom.getNextString(), null,
//					null, null, null, null, null, null, null, false);
//			isHomePressed = true;			
//		}
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.i("test", "ResponsiveUIActivity onPause");
	}

	@Override
	public void onStop() {
		super.onStop();
		SharedPreferencesUtil shared = new SharedPreferencesUtil(this);
		shared.saveUserInfo();
		Log.i("test", "ResponsiveUIActivity onStop");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i("test", "ResponsiveUIActivity ondestroy");
		if (receiver != null) {
			unregisterReceiver(receiver);
			receiver = null;
		}
		if (shared == null)
			shared = new SharedPreferencesUtil(this);
		shared.saveGroupMaptoShared(MessageInfo.groupMap, UserInfo.db_id + "_"
				+ YIKUAIR_GROUP);
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
						ClientSocket client = new ClientSocket(
								ResponsiveUIActivity.this);
						client.sendMessage(null, 102,
								StringWidthWeightRandom.getNextString(), null,
								null, null, null, null, null, null, null, false);
						isHomePressed = true;
						Log.e("test", "home键被点击");

						SharedPreferencesUtil shared = new SharedPreferencesUtil(
								ResponsiveUIActivity.this);
						shared.saveLoginInfo(UserInfo.id,
								UserInfo.cipher_password);

						shared.saveUserInfo();
					}
				}
			}
		}
	}

	public void onBirdPressed(int pos) {
		/*
		 * Intent intent = BirdActivity.newInstance(this, pos);
		 * startActivity(intent);
		 */
	}
}
