package com.bestapp.yikuair.anim;

import android.os.Bundle;
import android.view.Menu;

import com.bestapp.yikuair.BaseActivity;
import com.bestapp.yikuair.R;
import com.bestapp.yikuair.SampleListFragment;
import com.bestapp.yikuair.R.id;
import com.bestapp.yikuair.R.layout;
import com.bestapp.yikuair.R.menu;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.SlidingMenu.CanvasTransformer;

public abstract class CustomAnimation extends BaseActivity {

	private CanvasTransformer mTransformer;

	public CustomAnimation(int titleRes, CanvasTransformer transformer) {
		super(titleRes);
		mTransformer = transformer;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// set the Above View
		setContentView(R.layout.content_frame);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, new SampleListFragment()).commit();

		SlidingMenu sm = getSlidingMenu();
		setSlidingActionBarEnabled(true);
		sm.setBehindScrollScale(0.0f);
		sm.setBehindCanvasTransformer(mTransformer);
	}

}
