package com.bestapp.yikuair.adapter;

import java.util.ArrayList;

import com.bestapp.yikuair.utils.MenuHttp.MenuData;

import android.R.integer;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MenuListAdapter extends BaseAdapter {

	private ArrayList<MenuData> menuDatas = new ArrayList<MenuData>();
	private Context context;
	private int color = Color.parseColor("#777777");

	public MenuListAdapter(Context context, ArrayList<MenuData> menuDatas) {
		this.context = context;
		this.menuDatas = menuDatas;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return menuDatas.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return menuDatas.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		MenuData data = menuDatas.get(arg0);
		TextView button = new TextView(context);
		button.setText(data.buttonText);
		button.setGravity(Gravity.CENTER);
		button.setTextSize(15);
		int margin = dip2px(32);
		button.setHeight(margin);
		button.setTextColor(color);
		button.setGravity(Gravity.CENTER);
		return button;
	}

	public int dip2px(float dpValue) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
}
