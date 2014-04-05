package com.bestapp.yikuair;

import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;

public class OfficialAccountBaseActivity extends Activity {

	public int netTextSize;
	public int youTextSize;
	public int contentTextSize;
	public int noTextSize;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
	@Override
	protected void onRestart() {
		super.onRestart();
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	protected int getHeight() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int height = dm.heightPixels;
		return height;
	}

	public int getItemHeight() {

		return getHeight() / 400 * 41;
	}

	public int getWidth() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;
		return width;
	}

}
