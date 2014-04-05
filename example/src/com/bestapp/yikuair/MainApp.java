package com.bestapp.yikuair;

import java.io.File;

import android.app.Application;

import com.baidu.android.pushservice.PushManager;
import com.bestapp.yikuair.officialaccount.Preferences;
import com.bestapp.yikuair.utils.DBlog;
import com.bestapp.yikuair.utils.OperationFileHelper;

public class MainApp extends Application {
	public static Preferences preference;
	public static MainApp mInstance;

	@Override
	public void onCreate() {
		super.onCreate();
		DBlog.e("sdsd", "sadasd");
		initPreference();
		mInstance = this;
		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(getApplicationContext());
	}

	private void initPreference() {
		preference = new Preferences(getApplicationContext());
	}

	public static MainApp getInstance() {

		return mInstance;
	}

	public void exitApp() {
		PushManager.stopWork(this);
		File cacheFile = new File("/data/data/com.bestapp.yikuair/cache");
		File sharedPreference = new File("/data/data/com.bestapp.yikuair/shared_prefs");
		OperationFileHelper.RecursionDeleteFile(cacheFile);
		OperationFileHelper.RecursionDeleteFile(sharedPreference);
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(0);
	}
}
