package com.bestapp.yikuair.fragments;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bestapp.yikuair.R;

public class CalGridViewAdapter extends BaseAdapter {

	private Context mContext;
	private List<String> mLists = new ArrayList<String>();

	public CalGridViewAdapter(Context pContext, List<String> pStrs) {
		this.mContext = pContext;
		mLists = pStrs;

	}

	@Override
	public int getCount() {
		return mLists.size();
	}
 
	@Override
	public Object getItem(int position) {
		return mLists.get(position);
	}  

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		Holder _Holder = null;
		if (null == convertView) {
			_Holder = new Holder();
			LayoutInflater mInflater = LayoutInflater.from(mContext);
			convertView = mInflater.inflate(R.layout.cal_gridview_item, null);
			_Holder.cal_gv_item = (TextView) convertView
					.findViewById(R.id.tv_gv_item);
			_Holder.cal_gv_item.setFocusable(false);
			convertView.setTag(_Holder);
		} else {
			_Holder = (Holder) convertView.getTag();
		}
		_Holder.cal_gv_item.setText(mLists.get(position));

		return convertView;
	}

	private static class Holder {
		TextView cal_gv_item;
	}
}
