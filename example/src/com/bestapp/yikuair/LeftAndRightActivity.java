package com.bestapp.yikuair;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import com.bestapp.yikuair.R;
import com.bestapp.yikuair.fragments.ColorFragment;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.SlidingMenu.OnClosedListener;
import com.slidingmenu.lib.SlidingMenu.OnOpenedListener;

public class LeftAndRightActivity extends BaseActivity {

	public LeftAndRightActivity() {
		super(R.string.left_and_right);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSlidingMenu().setMode(SlidingMenu.LEFT_RIGHT);
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

		setContentView(R.layout.content_frame);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, new SampleListFragment()).commit();

		getSlidingMenu().setSecondaryMenu(R.layout.properties);
		getSlidingMenu().setSecondaryShadowDrawable(R.drawable.shadowright);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame_two, new SampleListFragment())
				.commit();
	}

}
