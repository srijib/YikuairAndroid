package com.bestapp.yikuair.fragments;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bestapp.yikuair.R;
import com.bestapp.yikuair.database.DBOpenHelper;
import com.bestapp.yikuair.fragments.ScheduleAdapter.ViewHolder;
import com.bestapp.yikuair.utils.MessageInfo;
import com.bestapp.yikuair.utils.SharedPreferencesUtil;
import com.bestapp.yikuair.utils.UserInfo;

public class MessageAdapter extends BaseAdapter {

	private List<MessageItemInfo> arrays = null;
	private Context mContext;
	public ImageLoader imageLoader;
	public SharedPreferencesUtil shared;
	public DBOpenHelper dbOpenHelper;
	public Cursor cursor;

	public MessageAdapter(Context mContext, List<MessageItemInfo> arrays) {
		this.mContext = mContext;
		this.arrays = arrays;
		this.imageLoader = new ImageLoader(mContext);
		shared = new SharedPreferencesUtil(mContext);
	}

	public int getCount() {
		return this.arrays.size();
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		// if (view == null) {
		viewHolder = new ViewHolder();
		view = LayoutInflater.from(mContext).inflate(R.layout.message_item,
				null);
		viewHolder.name = (TextView) view.findViewById(R.id.friend_name);
		viewHolder.content = (TextView) view.findViewById(R.id.chat_content);
		viewHolder.time = (TextView) view.findViewById(R.id.chat_time);
		viewHolder.num = (TextView) view.findViewById(R.id.tv_message_num);
		viewHolder.photo = (ImageView) view.findViewById(R.id.head);
		viewHolder.btnDel = (Button) view.findViewById(R.id.del);

		view.setTag(viewHolder);

		/*
		 * } else { viewHolder = (ViewHolder) view.getTag(); }
		 */
		// Log.e("test", "url == " + this.arrays.get(position).getHeadUrl());

		if (this.arrays.get(position).getHeadUrl() != null
				&& this.arrays.get(position).getHeadUrl().length() > 0) {

			String headUrl = "http://" + UserInfo.downloadImgUrl
					+ getHeadUrlFromDB(this.arrays.get(position).getId());
			imageLoader.DisplayImage(headUrl, (Activity) mContext,
					viewHolder.photo);
		} else {
			if (this.arrays.get(position).getSex() != null
					&& this.arrays.get(position).getSex().equals("0")) {
				viewHolder.photo.setBackgroundDrawable(mContext.getResources()
						.getDrawable(R.drawable.ico_girl)); 
			} else if (this.arrays.get(position).getSex() != null
					&& this.arrays.get(position).getSex().equals("1")) {
				viewHolder.photo.setBackgroundDrawable(mContext.getResources()
						.getDrawable(R.drawable.ico_boy));
			} else {
				viewHolder.photo.setBackgroundDrawable(mContext.getResources()
						.getDrawable(R.drawable.ico_group));
			}
		}

		viewHolder.name.setText(this.arrays.get(position).getName());
		viewHolder.content.setText(this.arrays.get(position).getContent());
		viewHolder.num.setVisibility(this.arrays.get(position)
				.getIsMessageNumVisible());
		viewHolder.num.setText(String.valueOf(this.arrays.get(position)
				.getMessageNum()));
		viewHolder.btnDel.setVisibility(this.arrays.get(position)
				.getIsDelShow());

		viewHolder.btnDel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				LinkedList<ChatMsgEntity> mDataArrays = new LinkedList<ChatMsgEntity>();
				mDataArrays.clear();
				//Log.e("test","id ::::::" + arrays.get(position).getId());
				shared.saveDatatoShared(arrays.get(position).getId() + "_" + UserInfo.db_id,
						mDataArrays);
				arrays.remove(position);
				MessageFragment.userList.remove(position);
				MessageFragment.boolList.remove(position);
				if (arrays.isEmpty()) {
					MessageFragment.rl_message_list.setVisibility(View.GONE);
					MessageFragment.iv_message_default
							.setVisibility(View.VISIBLE);
				}

				notifyDataSetChanged();
			}
		});

		if (this.arrays.get(position).getTime() != null) {
			String[] str = this.arrays.get(position).getTime().split(" ");
			if (str != null && str.length > 2) {
				String time = str[2];
				viewHolder.time.setText(time);
			} else
				viewHolder.time.setText(this.arrays.get(position).getTime());
		}

		return view;
	}

	public String getHeadUrlFromDB(String dbId) {
		if (dbId == null)
			return "";
		dbOpenHelper = new DBOpenHelper(mContext);
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		String name = "";
		cursor = db.rawQuery("select * from contactsTable where dbid=?",
				new String[] { dbId });
		if (cursor.moveToFirst()) {
			name = cursor.getString(8);
		}
		// Log.e("test", "headurl :: " + name);
		dbOpenHelper.close();
		cursor.close();
		return name;
	}

	
	final static class ViewHolder {
		TextView name;
		TextView content;
		TextView time;
		TextView num;
		ImageView photo;
		Button btnDel;
	}
}