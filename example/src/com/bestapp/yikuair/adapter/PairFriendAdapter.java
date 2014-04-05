package com.bestapp.yikuair.adapter;

import java.util.ArrayList;

import com.bestapp.yikuair.officialaccount.SpeedFriendFragment;
import com.bestapp.yikuair.utils.AccountInfomation;
import com.bestapp.yikuair.R;


import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PairFriendAdapter extends BaseAdapter {

	private ArrayList<AccountInfomation> mInfos;
	private Context mContext;
	private static final int orange = Color.parseColor("#FF7F24");

	private int height;

	private void getHeight() {
		DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
		height = metrics.heightPixels;
	}

	public PairFriendAdapter(Context context, ArrayList<AccountInfomation> infos) {
		this.mInfos = infos;
		this.mContext = context;
		getHeight();
	}

	@Override
	public int getCount() {
		return mInfos.size();
	}

	@Override
	public Object getItem(int position) {
		return mInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	static class HoldView {
		ImageView ico;
		TextView name;
		TextView info;
		TextView time;
		TextView far;
		TextView info_num;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		HoldView mHoldView = new HoldView();
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = inflater.inflate(R.layout.view_pair_friend_notify, null);

		mHoldView.info_num = (TextView) convertView.findViewById(R.id.info_num);
		mHoldView.ico = (ImageView) convertView
				.findViewById(R.id.pair_ico_view);
		mHoldView.name = (TextView) convertView.findViewById(R.id.pair_name);
		mHoldView.info = (TextView) convertView.findViewById(R.id.pair_info);
		mHoldView.time = (TextView) convertView.findViewById(R.id.pair_time);
		mHoldView.far = (TextView) convertView.findViewById(R.id.pair_far);
		AccountInfomation subscripitionInfo = mInfos.get(position);
		mHoldView.name.setText(subscripitionInfo.getNickname());
		mHoldView.info.setText(subscripitionInfo.getInformation());
		mHoldView.time.setText(subscripitionInfo.getTime());
		mHoldView.far.setText(subscripitionInfo.getDistance());
		SpeedFriendFragment.instance.mAsyncImageLoader.loadBitmap(
				mHoldView.ico, subscripitionInfo.getHeadurl(), 0);
		if (subscripitionInfo.getInfor_num() != 0) {
			mHoldView.info_num.setText(String.valueOf(subscripitionInfo
					.getInfor_num()));
			mHoldView.info_num.setVisibility(View.VISIBLE);
		} else {
			mHoldView.info_num.setVisibility(View.GONE);
		}
		return convertView;
	}
}
