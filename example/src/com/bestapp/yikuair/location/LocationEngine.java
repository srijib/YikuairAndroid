package com.bestapp.yikuair.location;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.PopupOverlay;

public class LocationEngine {
	private static LocationEngine mInstance = null;
	public static final String strKey = "MDKO5Tv8eO36jXtElcIuZ99s";
	BMapManager mBMapManager = null;

	public LocationEngine(Context context) {
		init(context);
		mInstance = this;
		boolean m_bKeyRight = true;
	}

	public void init(Context context) {
		if (mBMapManager == null) {
			mBMapManager = new BMapManager(context);
		}

		if (!mBMapManager.init(strKey, new MyGeneralListener())) {
			Toast.makeText(context, "BMapManager  初始化错误!", Toast.LENGTH_LONG)
					.show();
		}
	}

	public static class MyGeneralListener implements MKGeneralListener {

		@Override
		public void onGetNetworkState(int iError) {
			if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
				Log.e("yikuair", "BaiduLocation says network error"
						+ "error number is: " + iError);
			} else if (iError == MKEvent.ERROR_NETWORK_DATA) {
				Log.e("yikuair", "BaiduLocation says network data error"
						+ "error number is: " + iError);
			}
			// ...
		}

		@Override
		public void onGetPermissionState(int iError) {
			// 非零值表示key验证未通过
			if (iError != 0) {
				// 授权Key错误：
				Log.e("yikuair", "BaiduLocation API key wrong");
				Log.e("yikuair", "Error code is " + iError);
			} else {
				// DemoApplication.getInstance().m_bKeyRight = true;
				// Toast.makeText(DemoApplication.getInstance().getApplicationContext(),
				// "key认证成功", Toast.LENGTH_LONG).show();
			}
		}
	}

	public static Object getInstance() {
		// TODO Auto-generated method stub
		return mInstance;
	}


}
