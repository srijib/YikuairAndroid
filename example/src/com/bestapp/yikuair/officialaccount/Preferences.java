package com.bestapp.yikuair.officialaccount;



import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Preferences {
	private SharedPreferences _settings = null;

	public Preferences(Context context) {
		super();
		_settings = context.getSharedPreferences("settings",
				Activity.MODE_PRIVATE);
	}

	public void setSettings(String key, String val) {
		Editor editor = _settings.edit();
		editor.putString(key, val);
		editor.commit();
	}

	public void setSettings(String key, int val) {
		Editor editor = _settings.edit();
		editor.putInt(key, val);
		editor.commit();
	}

	public void removeSetting(String key) {
		Editor editor = _settings.edit();
		editor.remove(key);
		editor.commit();
	}

	public void setSettings(String key, long val) {
		Editor editor = _settings.edit();
		editor.putLong(key, val);
		editor.commit();
	}

	public void setMoreSettings(String data, String split) {
		if (data == null || data.equals(""))
			return;
		String mSplit = (split == null) ? "," : split; // 預設�?,
		Editor editor = _settings.edit();
		String[] params = data.split(mSplit);
		for (String param : params) {
			if (param.equals(""))
				continue;
			String[] keyVal = param.split("=");
			if (keyVal[0].equals(""))
				continue;
			editor.putString(keyVal[0], (keyVal.length < 2) ? "" : keyVal[1]);
		}
		editor.commit();
	}

	public Map<String, String> getMoreSettings(String keys, String split) {
		Map<String, String> map = new HashMap<String, String>();
		String mSplit = (split == null) ? "," : split;
		String[] arrKeys = keys.split(mSplit);
		for (String key : arrKeys)
			map.put(key.trim(), getSettings(key, ""));
		return map;
	}

	public String getSettings(String key, String defaultVal) {
		String rv = defaultVal;
		try {
			rv = _settings.getString(key.trim(), defaultVal);
		} catch (ClassCastException e) {
		}
		return rv;
	}

	public int getSettings(String key, int defaultVal) {
		int rv = defaultVal;
		try {
			rv = _settings.getInt(key, defaultVal);
		} catch (ClassCastException e) {
		}
		return rv;
	}

	public long getSettings(String key, long defaultVal) {
		long rv = defaultVal;
		try {
			rv = _settings.getLong(key, defaultVal);
		} catch (ClassCastException e) {
		}
		return rv;
	}
}