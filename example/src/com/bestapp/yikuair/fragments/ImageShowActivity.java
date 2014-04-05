package com.bestapp.yikuair.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.bestapp.yikuair.R;
import com.slidingmenu.lib.SlidingMenu;

public class ImageShowActivity extends Activity {
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_show);
		String path = null;
		if(getIntent().getExtras() != null){
			path = getIntent().getExtras().getString("path");
		}
	}

}
