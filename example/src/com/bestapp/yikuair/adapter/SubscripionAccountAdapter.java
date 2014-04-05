package com.bestapp.yikuair.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.bestapp.yikuair.officialaccount.OfficialAccountFragment;
import com.bestapp.yikuair.utils.AccountInfomation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bestapp.yikuair.R;

public class SubscripionAccountAdapter extends BaseAdapter {

	private ArrayList<AccountInfomation> mInfos;
	private Context mContext;

	public SubscripionAccountAdapter(Context context,
			ArrayList<AccountInfomation> infos) {
		this.mInfos = infos;
		this.mContext = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mInfos.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		HoldView mHoldView;
		mHoldView = new HoldView();
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = inflater.inflate(R.layout.view_notify, null);
		mHoldView.ico = (ImageView) convertView
				.findViewById(R.id.notify_ico_view);

		mHoldView.badgeView = (TextView) convertView
				.findViewById(R.id.notify_num);
		mHoldView.name = (TextView) convertView.findViewById(R.id.notify_name);
		mHoldView.info = (TextView) convertView.findViewById(R.id.notify_info);
		mHoldView.time = (TextView) convertView
				.findViewById(R.id.notify_f_time);
		mHoldView.delete = (Button) convertView.findViewById(R.id.delete_info);
		mHoldView.delete.setTag(position);
		AccountInfomation subscripitionInfo = (AccountInfomation) getItem(position);
		String time = getShowTime(subscripitionInfo.getTime()).toString();
		mHoldView.time.setText(time);
		mHoldView.name.setText(subscripitionInfo.getRealname());
		mHoldView.info.setText(subscripitionInfo.getInformation());
		mHoldView.delete.setVisibility(View.GONE);

		if (subscripitionInfo.getHeadurl() == null
				|| subscripitionInfo.getHeadurl().trim().equals("")) {
			mHoldView.ico.setImageResource(R.drawable.ico_boy);
		} else {
			OfficialAccountFragment.instance.asyncImageLoader.loadBitmap(
					mHoldView.ico, subscripitionInfo.getHeadurl(), 0);
		}
		if (subscripitionInfo.getInfor_num() != 0) {
			mHoldView.badgeView.setText(String.valueOf(subscripitionInfo
					.getInfor_num()));
			mHoldView.badgeView.setVisibility(View.VISIBLE);
		} else {
			mHoldView.badgeView.setVisibility(View.GONE);
		}
		return convertView;
	}

	@SuppressLint("SimpleDateFormat")
	private String getShowTime(String time) {
		if (time == null || time.equals(""))
			return "";
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date now = null;
		Date date = null;

		long l = 0L;
		try {
			now = new Date();
			date = df.parse(time);
			l = now.getTime() - date.getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int day = (int) (l / (24 * 60 * 60 * 1000));
		int hour = (int) (l / (60 * 60 * 1000) - day * 24);
		int min = (int) ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
		int s = (int) (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
		StringBuffer timer = new StringBuffer();
		if (day != 0) {
			timer.append(day + "天");
			timer.append("前");
			return timer.toString();
		}
		if (hour != 0) {
			timer.append(min + "小时");
		}
		if (min != 0) {
			timer.append(min + "分钟");
			if (hour != 0) {
				timer.append("前");
				return timer.toString();
			}
		}
		if (s != 0) {
			timer.append(s + "秒");
		}
		if (timer.toString().equals("")) {
			return "当前";
		}
		timer.append("前");
		return timer.toString();
	}

	static class HoldView {
		ImageView ico;
		TextView name;
		TextView info;
		TextView time;
		Button delete;
		TextView badgeView;
	}
}
