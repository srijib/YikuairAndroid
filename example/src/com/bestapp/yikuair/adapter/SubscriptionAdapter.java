package com.bestapp.yikuair.adapter;

import java.util.ArrayList;

import com.bestapp.yikuair.R;
import com.bestapp.yikuair.officialaccount.OfficialAccountFragment;
import com.bestapp.yikuair.officialaccount.SubActivity;
import com.bestapp.yikuair.utils.AccountInfomation;
import com.bestapp.yikuair.utils.UserInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SubscriptionAdapter extends BaseAdapter {

	private ArrayList<AccountInfomation> mInfos;
	private SubActivity mContext;
	private int changeId;

	public int getChangeId() {
		return changeId;
	}

	public void setChangeId(int changeId) {
		this.changeId = changeId;
	}

	public SubscriptionAdapter(SubActivity context,
			ArrayList<AccountInfomation> infos, int itemHeight) {
		this.mInfos = infos;
		this.mContext = context;
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		HoldView mHoldView;
		mHoldView = new HoldView();
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = inflater.inflate(R.layout.view_subscription_item, null);
		mHoldView.ico = (ImageView) convertView.findViewById(R.id.sub_ico_img);
		mHoldView.name = (TextView) convertView.findViewById(R.id.sub_name);
		mHoldView.sub = (ImageView) convertView.findViewById(R.id.sub_img);
		convertView.setTag(mHoldView);
		mHoldView.sub.setTag(position);
		final AccountInfomation subscripitionInfo = (AccountInfomation) getItem(position);

		if (subscripitionInfo.getHeadurl() != null
				&& !subscripitionInfo.getHeadurl().equals("")) {
			OfficialAccountFragment.instance.asyncImageLoader.loadBitmap(
					mHoldView.ico, subscripitionInfo.getHeadurl(), 0);
		} else {
			mHoldView.ico.setImageResource(R.drawable.ico_boy);
		}
		mHoldView.name.setText(subscripitionInfo.getRealname());
		if (subscripitionInfo.isSub()) {
			mHoldView.sub.setImageResource(R.drawable.ico_add_pressed);
		} else {
			mHoldView.sub.setImageResource(R.drawable.ico_add_normal);
		}
		mHoldView.sub.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setChangeId((Integer) v.getTag());
				mContext.showRoundProcessDialog();

				if (subscripitionInfo.isSub()) {
					mContext.sendCancelOfficiaAccount(UserInfo.db_id,
							subscripitionInfo.getId());
				} else {
					mContext.SendAddOfficialAccount(UserInfo.db_id,
							subscripitionInfo.getId());
				}
			}
		});
		return convertView;
	}

	static class HoldView {
		ImageView ico;
		TextView name;
		ImageView sub;

	}

}
