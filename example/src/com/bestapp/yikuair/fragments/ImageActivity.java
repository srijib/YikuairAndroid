package com.bestapp.yikuair.fragments;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import com.bestapp.yikuair.R;
import com.bestapp.yikuair.utils.BitmapCompressUtil;
import com.bestapp.yikuair.utils.ImageLoaderOriginal;
import com.bestapp.yikuair.utils.MessageInfo;

public class ImageActivity extends Activity {

	private Dialog mDialog;
	private ImageBroadcastReceiver ibr;
	private static final String LOCATION_DIR = "/mnt/sdcard/yikuair/";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String path = null;
		boolean isDownload = false;
		ImageLoaderOriginal imageLoader = new ImageLoaderOriginal(this);;

		if (getIntent().getExtras() != null) {
			path = getIntent().getExtras().getString("imagePath");
			isDownload = getIntent().getExtras()
					.getBoolean("isDownload", false);
		}
		
		Log.e("test","image oncreate.................");
		// register broadcast
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(MessageInfo.ImageBroadcast);
		ibr = new ImageBroadcastReceiver();
		registerReceiver(ibr, intentFilter);
		
		if (path != null) {
			if (!isDownload) {
				setImageView(path);
			} else {
				String str[] = path.split("\\/");
				if(str != null && str.length > 4){
					File file = new File(LOCATION_DIR + str[3] + str[4]);
					Log.i("test","image activity path :" + LOCATION_DIR + str[3] + str[4]);
					if(file.exists())
						setImageView(LOCATION_DIR + str[3] + str[4]);
					else{
						showWaitingProcessDialog();
						imageLoader.loadImage(path, 0, null);
					}						
				}			
			}
		}
	}

	public void setImageView(String path){
		ImageView imageView = new ImageView(this);
		imageView.setScaleType(ScaleType.CENTER_INSIDE);
		imageView.setImageBitmap(BitmapCompressUtil.rotateImageView(BitmapCompressUtil.readPictureDegree(path),BitmapFactory.decodeFile(path))/*BitmapCompressUtil
				.compressImage(BitmapFactory.decodeFile(path))*/);
	
		imageView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
/*				if(ibr != null)
					unregisterReceiver(ibr);
*/				finish();
			}
		});

		setContentView(imageView);
		getWindow().setBackgroundDrawableResource(android.R.color.black);
	
	}
	
	public void showWaitingProcessDialog() {
		mDialog = new AlertDialog.Builder(this).create();
		mDialog.show();
		mDialog.setContentView(R.layout.loading_process_dialog);
	}
	
	@Override
	protected void onDestroy() {
		unregisterReceiver(ibr);
		Log.e("test","image  destroy");
		super.onDestroy();
	}


	public class ImageBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i("test", "receive image broadcast");
			 String action = intent.getAction();
			 String imgPath = null;
			 if(action.equals(MessageInfo.ImageBroadcast)) {
				    imgPath = intent.getStringExtra("imgPath");
			 }
			 setImageView(imgPath);
			 mDialog.dismiss();
		}
	}
}
