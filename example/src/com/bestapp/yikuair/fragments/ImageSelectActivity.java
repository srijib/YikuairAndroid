package com.bestapp.yikuair.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.bestapp.yikuair.R;
import com.bestapp.yikuair.utils.BitmapCompressUtil;

public class ImageSelectActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.image_select);
		String path = null;
		if (getIntent().getExtras() != null) {
			path = getIntent().getExtras().getString("imagePath");
		}
		
		ImageView imageView = new ImageView(this);
		imageView.setScaleType(ScaleType.CENTER_INSIDE);
		if (path != null)
			imageView.setImageBitmap( BitmapCompressUtil.compressImage(BitmapFactory.decodeFile(path)));
		
		imageView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		this.getWindow().setBackgroundDrawableResource(android.R.color.black);
	}

}
