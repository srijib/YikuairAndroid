package com.bestapp.yikuair.service;

import com.bestapp.yikuair.fragments.ClientSocket;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class NetworkService extends Service {
	private final static String TAG = "networkservice";
	private ClientSocket client = new ClientSocket(getApplicationContext());

	@Override
	public IBinder onBind(Intent intent) {
		Log.v(TAG, "networkService onBind");
		return null;
	}

	@Override
	public void onCreate() {
		Log.v(TAG, "networkService onCreate");
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.v(TAG, "networkService onStart");

		super.onStart(intent, startId);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v(TAG, "networkService onstartCommand");
		flags = START_STICKY;
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		Intent localIntent = new Intent();
		localIntent.setClass(this, NetworkService.class);
		this.startService(localIntent);
	}
}
