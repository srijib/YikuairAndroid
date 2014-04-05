package com.bestapp.yikuair.officialaccount;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bestapp.yikuair.R;
import com.bestapp.yikuair.fragments.ChatActivity;
import com.bestapp.yikuair.utils.AccountInfomation;

public class FriendSuccessView extends Activity implements OnClickListener {

	private int width;
	private AccountInfomation accountInfomation;

	private void getWidtht() {
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		width = metrics.widthPixels;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_speed_success);
		getWindow().setLayout(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		getWidtht();
		initView();
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
				WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

	}

	private void initView() {
		Intent intent = getIntent();

		accountInfomation = intent.getParcelableExtra("info");
		ImageView manView = (ImageView) findViewById(R.id.man);
		ImageView womanView = (ImageView) findViewById(R.id.woman);
		manView.setLayoutParams(new LinearLayout.LayoutParams(
				(int) (width / 40.0 * 13), (int) (width / 40.0 * 13)));
		if (SpeedFriendFragment.instance.drawable != null)
			manView.setImageDrawable(SpeedFriendFragment.instance.drawable);

		womanView.setLayoutParams(new LinearLayout.LayoutParams(
				(int) (width / 40.0 * 13), (int) (width / 40.0 * 13)));
		if (accountInfomation.getHeadurl() != null)
			SpeedFriendFragment.instance.mAsyncImageLoader.loadBitmap(
					womanView, accountInfomation.getHeadurl(), 0);
		Button continueButton = (Button) findViewById(R.id.continue_bt);
		Button chatButton = (Button) findViewById(R.id.chat);
		continueButton.setOnClickListener(this);

		chatButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.continue_bt:
			finish();
			break;
		case R.id.chat:
			finish();
			Intent intent = new Intent(this, ChatActivity.class);
			String id = accountInfomation.getId();
			String name = accountInfomation.getNickname();
			intent.putExtra("Id", id);
			intent.putExtra("name", name);
			startActivityForResult(intent, 1);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_of_left);
			break;
		default:
			break;
		}
	}
}
