package com.bestapp.yikuair.utils;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.bestapp.yikuair.R;

public class CalendarGridViewAdapter extends BaseAdapter {

	List<String> mStrs = new ArrayList<String>();
	private Activity activity;
	Resources resources;

	// construct
	public CalendarGridViewAdapter(Activity a, List<String> strs) {
		mStrs = strs;
		activity = a;
		resources = activity.getResources();
		// titles = getDates();
	}

	public CalendarGridViewAdapter(Activity a) {
		activity = a;
		resources = activity.getResources();
	}

	@Override
	public int getCount() {
		return 7;
	}

	@Override
	public Object getItem(int position) {
		return mStrs.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder _Holder = null;
		if (null == convertView) {
			_Holder = new Holder();
			LayoutInflater mInflater = LayoutInflater.from(activity);
			convertView = mInflater.inflate(R.layout.cal_gridview_item, null);
			_Holder.cal_gv_item = (TextView) convertView
					.findViewById(R.id.tv_gv_item);
			_Holder.tv_gv_date = (TextView) convertView
					.findViewById(R.id.tv_gv_date);
			convertView.setTag(_Holder);
		} else {
			_Holder = (Holder) convertView.getTag();
		}
		_Holder.cal_gv_item.setText(mStrs.get(position).split(" ")[0]);
		_Holder.tv_gv_date.setText(mStrs.get(position).split(" ")[1]);
		return convertView;

	}

	private static class Holder {
		TextView cal_gv_item;
		TextView tv_gv_date;
	}
}
