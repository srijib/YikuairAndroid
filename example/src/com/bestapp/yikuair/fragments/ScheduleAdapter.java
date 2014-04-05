package com.bestapp.yikuair.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bestapp.yikuair.R;
import com.bestapp.yikuair.database.DBOpenHelper;
import com.bestapp.yikuair.utils.MessageInfo;
import com.bestapp.yikuair.utils.SharedPreferencesUtil;
import com.bestapp.yikuair.utils.StringWidthWeightRandom;
import com.bestapp.yikuair.utils.UserInfo;

public class ScheduleAdapter extends BaseAdapter {

	private List<ScheduleItemInfo> arrays = null;
	private Context mContext;
	private String beginDate, endDate, title, address, itemId, taskId, groupId;
	private boolean isFromChat = false;
	private String[] nameStr;
	private String[] idStr;
	private int type;
	public ImageLoader imageLoader;
	private DBOpenHelper dbOpenHelper;
	private Cursor cursor;
	private SharedPreferencesUtil shared;
	private ClientSocket client;

	public ScheduleAdapter(Context mContext, List<ScheduleItemInfo> arrays) {
		this.mContext = mContext;
		this.arrays = arrays;
		this.imageLoader = new ImageLoader(mContext);
		shared = new SharedPreferencesUtil(mContext);
		client = new ClientSocket(mContext);
	}

	public int getViewTypeCount() {
		return 2;
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

		viewHolder = new ViewHolder();
		if (arrays.get(position).getType() == MessageInfo.TODOITEM) {
			view = LayoutInflater.from(mContext).inflate(
					R.layout.schedule_task_layout, null);
			viewHolder.itemTitle = (TextView) view
					.findViewById(R.id.tv_item_title);
			return view;
		} else {
			view = LayoutInflater.from(mContext).inflate(
					R.layout.schedule_item, null);
		}
		viewHolder.sponsorHead = (ImageView) view
				.findViewById(R.id.sponsor_head);
		viewHolder.sponsorName = (TextView) view
				.findViewById(R.id.sponsor_name);
		viewHolder.scheduleContent = (TextView) view
				.findViewById(R.id.schedule_content);
		viewHolder.scheduleTime = (TextView) view
				.findViewById(R.id.schedule_time);
		viewHolder.btnDel = (Button) view.findViewById(R.id.del);
		viewHolder.ivResume = (ImageView) view.findViewById(R.id.iv_resume);
		viewHolder.cancelLine = (LinearLayout) view
				.findViewById(R.id.ll_cancel_line);
		viewHolder.editItem = (ImageView) view.findViewById(R.id.iv_edit_item);

		viewHolder.nameStr = arrays.get(position).getMemberName();
		viewHolder.idStr = arrays.get(position).getmemberId();
		viewHolder.beginDate = arrays.get(position).getScheduleBeginTime();
		viewHolder.endDate = arrays.get(position).getScheduleEndTime();
		viewHolder.type = arrays.get(position).getType();
		viewHolder.title = arrays.get(position).getScheduleContent();
		viewHolder.itemId = arrays.get(position).getItemId();
		viewHolder.address = arrays.get(position).getAddress();
		viewHolder.isFromChat = arrays.get(position).getIsFromChat();
		viewHolder.taskId = arrays.get(position).getTaskId();
		viewHolder.groupId = arrays.get(position).getGroupId();
		view.setTag(viewHolder);

		view.setOnClickListener(new OnClickListener() {

			@SuppressLint("ResourceAsColor")
			public void onClick(View view) {
				final ViewHolder holder = (ViewHolder) view.getTag();
				beginDate = holder.beginDate;
				endDate = holder.endDate;
				nameStr = holder.nameStr;
				idStr = holder.idStr;
				type = holder.type;
				title = holder.title;
				itemId = holder.itemId;
				address = holder.address;
				isFromChat = holder.isFromChat;
				taskId = holder.taskId;
				groupId = holder.groupId;

				Log.e("test", "title: " + title);
				Log.e("test", "type :" + type);
				Log.e("test", "itmeId :" + itemId);

				if (type == MessageInfo.TASK) {
					Intent intent = new Intent(mContext,
							ScheduleTaskActivity.class);
					Bundle bundle = new Bundle();
					bundle.putStringArray("nameStr", nameStr);
					bundle.putStringArray("idStr", idStr);
					// intent.putExtra("beginDate", beginDate);
					intent.putExtra("endDate", endDate);
					intent.putExtra("type", type);
					intent.putExtra("ItemId", itemId);
					intent.putExtra("title", title);
					intent.putExtra("isFromChat", isFromChat);
					intent.putExtra("taskId", taskId);
					intent.putExtra("groupId", groupId);
					intent.putExtras(bundle);

					mContext.startActivity(intent);
					((Activity) mContext).overridePendingTransition(
							R.anim.in_from_down, R.anim.out_of_up);

				} else if (type == MessageInfo.MEETING) {
					Intent intent = new Intent(mContext,
							ScheduleMeetingActivity.class);
					Bundle bundle = new Bundle();
					bundle.putStringArray("nameStr", nameStr);
					bundle.putStringArray("idStr", idStr);
					intent.putExtra("beginDate", beginDate);
					intent.putExtra("endDate", endDate);
					intent.putExtra("type", type);
					intent.putExtra("address", address);
					intent.putExtra("isFromChat", isFromChat);
					intent.putExtra("ItemId", itemId);
					intent.putExtra("title", title);
					intent.putExtra("taskId", taskId);
					intent.putExtra("groupId", groupId);

					intent.putExtras(bundle);

					Log.e("test", "title :" + title);
					Log.e("test", "address :" + address);

					mContext.startActivity(intent);
					((Activity) mContext).overridePendingTransition(
							R.anim.in_from_down, R.anim.out_of_up);

				} else if (type == MessageInfo.OTHER) {
					Intent intent = new Intent(mContext,
							ScheduleOtherActivity.class);
					Bundle bundle = new Bundle();
					bundle.putStringArray("nameStr", nameStr);
					bundle.putStringArray("idStr", idStr);
					intent.putExtra("beginDate", beginDate);
					intent.putExtra("endDate", endDate);
					intent.putExtra("type", type);
					intent.putExtra("address", address);
					intent.putExtra("isFromChat", isFromChat);
					intent.putExtra("ItemId", itemId);
					intent.putExtra("title", title);
					intent.putExtra("taskId", taskId);
					intent.putExtra("groupId", groupId);

					intent.putExtras(bundle);

					mContext.startActivity(intent);
					((Activity) mContext).overridePendingTransition(
							R.anim.in_from_down, R.anim.out_of_up);
				}
			}
		});

		view.setOnLongClickListener(new OnLongClickListener() {
			public boolean onLongClick(View v) {
				final ViewHolder holder = (ViewHolder) v.getTag();
				if (holder.btnDel.getVisibility() == View.VISIBLE) {
					holder.btnDel.setVisibility(View.GONE);
					holder.ivResume.setVisibility(View.GONE);
					holder.scheduleTime.setVisibility(View.VISIBLE);
				} else {
					holder.scheduleTime.setVisibility(View.GONE);
					holder.btnDel.setVisibility(View.VISIBLE);
					holder.ivResume.setVisibility(View.VISIBLE);
				}
				return true;
			}
		});

		viewHolder.sponsorName.setText(this.arrays.get(position)
				.getSponsorName());
		viewHolder.scheduleContent.setText(this.arrays.get(position)
				.getScheduleContent());

		int type = arrays.get(position).getType();

		if (type == MessageInfo.TASK) {
			viewHolder.scheduleTime.setText(getFormatedTime(arrays
					.get(position).getScheduleEndTime(), true));
		} else {
			viewHolder.scheduleTime.setText(getFormatedTime(arrays
					.get(position).getScheduleBeginTime(), false));
		}

		if (viewHolder.sponsorName.getText() != null
				&& viewHolder.sponsorName.getText().toString()
						.equals(UserInfo.realName)) {
			if (UserInfo.LocalphotoPath != null
					&& UserInfo.LocalphotoPath.length() != 0) {
				Log.e("test", "localpath :" + UserInfo.LocalphotoPath);
				BitmapFactory.Options opts = new BitmapFactory.Options();
				opts.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(UserInfo.LocalphotoPath, opts);
				opts.inSampleSize = computeSampleSize(opts, -1, 128 * 128);
				opts.inJustDecodeBounds = false;
				try {
					Bitmap bmp = BitmapFactory.decodeFile(
							UserInfo.LocalphotoPath, opts);

					viewHolder.sponsorHead.setImageBitmap(bmp);

				} catch (OutOfMemoryError err) {

				}

			} else {
				if (UserInfo.sex.equals("0"))
					viewHolder.sponsorHead.setImageDrawable(mContext
							.getResources().getDrawable(R.drawable.ico_girl));
				else
					viewHolder.sponsorHead.setImageDrawable(mContext
							.getResources().getDrawable(R.drawable.ico_boy));
			}
		} else {
			if (getHeadUrlFromDB(this.arrays.get(position).getId()) != null
					&& getHeadUrlFromDB(this.arrays.get(position).getId())
							.length() > 0) {

				String headUrl = "http://" + UserInfo.downloadImgUrl
						+ getHeadUrlFromDB(this.arrays.get(position).getId());
				imageLoader.DisplayImage(headUrl, (Activity) mContext,
						viewHolder.sponsorHead);
			} else {
				if (getSexFromDB(this.arrays.get(position).getId()).equals("0"))
					viewHolder.sponsorHead.setImageDrawable(mContext
							.getResources().getDrawable(R.drawable.ico_girl));
				else
					viewHolder.sponsorHead.setImageDrawable(mContext
							.getResources().getDrawable(R.drawable.ico_boy));
			}
		}

		/*
		 * if (str != null && str.length > 2)
		 * viewHolder.scheduleEndTime.setText(str[1] + " " + str[2]); else
		 * viewHolder.scheduleEndTime.setText(mContext.getResources()
		 * .getString(R.string.today));// handle exception to avoid // null
		 */
		viewHolder.btnDel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String ids = "";
				boolean isGroup = false;
				for (int i = 0; i < arrays.get(position).getmemberId().length; i++) {

					if (arrays.get(position).getmemberId().length == 2) {
						if (arrays.get(position).getmemberId()[0]
								.equals(UserInfo.db_id))
							ids = arrays.get(position).getmemberId()[1];
						else if (arrays.get(position).getmemberId()[1]
								.equals(UserInfo.db_id))
							ids = arrays.get(position).getmemberId()[0];
						break;
					} else {
						isGroup = true;
						if (i == arrays.get(position).getmemberId().length - 1)
							ids += arrays.get(position).getmemberId()[i];
						else
							ids += arrays.get(position).getmemberId()[i] + "、";
					}
				}
				if (shared == null)
					shared = new SharedPreferencesUtil(mContext);

				List<ChatMsgEntity> mDataArrays = new ArrayList<ChatMsgEntity>();

				Log.e("test", "ids ::" + ids);
				if (shared.readDataFromShared(ids + "_" + UserInfo.db_id) != null) {
					mDataArrays = shared.readDataFromShared(ids + "_"
							+ UserInfo.db_id);
					for (int i = 0; i < mDataArrays.size(); i++) {
						Log.e("test", "itemId :"
								+ mDataArrays.get(i).getScheduleItemId());
						if (arrays.get(position).getItemId()
								.equals(mDataArrays.get(i).getScheduleItemId())) {
							mDataArrays.remove(i);
							shared.saveDatatoShared(ids + "_" + UserInfo.db_id,
									mDataArrays);
							break;
						}
					}
				}

				String msguuid = StringWidthWeightRandom.getNextString();
				if (isGroup) {
					String groupId = "";
					HashMap<String, String> groupMap = (HashMap<String, String>) MessageInfo.groupMap
							.clone();
					Set set = groupMap.keySet();
					Iterator it = set.iterator();
					while (it.hasNext()) {
						String key = (String) it.next();
						String value = (String) groupMap.get(key);
						if (value.equals(ids))
							groupId = key;
					}
					client.sendMessage(arrays.get(position).getTaskId(), 23,
							msguuid, UserInfo.db_id, groupId, null, null, null,
							null, "2", null, false);
				} else {
					client.sendMessage(arrays.get(position).getTaskId(), 23,
							msguuid, UserInfo.db_id, ids, null, null, null,
							null, "1", null, false);
				}

				String begintime = arrays.get(position).getScheduleBeginTime();
				String[] str = begintime.split(" ");
				if (str != null && str.length > 2)
					begintime = str[0];
				String id = arrays.get(position).getItemId();
				int taskId = 0, taskCount = 0;
				boolean isDel = false;

				if (MessageInfo.map.containsKey(begintime)) {
					List<ScheduleItemInfo> list = MessageInfo.map
							.get(begintime);
					taskId = list.size();
					for (int j = 0; j < list.size(); j++) {
						if (list.get(j).getType() == MessageInfo.TASK) {
							taskId = j;
							break;
						} else if (list.get(j).getType() != MessageInfo.TODOITEM
								&& list.get(j).getItemId().equals(id)
								&& list.get(j).getType() != MessageInfo.TASK)
							MessageInfo.map.get(begintime).remove(j);
					}
					taskCount = list.size() - taskId;

					// Log.i("test", "teskCount :" + taskCount);
					for (int i = taskId; i < list.size(); i++) {
						if (list.get(i).getItemId().equals(id)) {
							if (taskCount == 1) {
								MessageInfo.map.get(begintime).remove(i - 1);
								MessageInfo.map.get(begintime).remove(i - 1);
								isDel = true;
							} else
								MessageInfo.map.get(begintime).remove(i);
						}
					}
				}
				arrays.remove(position);
				if (isDel)
					arrays.remove(position - 1);
				notifyDataSetChanged();
			}
		});
		return view;
	}

	final static class ViewHolder {
		TextView itemTitle;
		TextView sponsorName;
		TextView scheduleContent;
		TextView scheduleTime;
		Button btnDel;
		LinearLayout cancelLine;
		RelativeLayout rlBottom;
		ImageView editItem;
		ImageView ivResume;
		ImageView sponsorHead;
		String[] nameStr;
		String[] idStr;
		String beginDate;
		String endDate;
		String title;
		String address;
		String itemId;
		String taskId;
		String groupId;
		boolean isFromChat;
		int type;
	}

	private String getFormatedTime(String scheduleTime, boolean isTask) {
		String date = "";
		String time = "";
		String noon = "";
		Log.e("test", "scheduletime :::" + scheduleTime);
		String[] str = scheduleTime.split(" ");
		if (str != null && str.length > 2) {
			String[] tempDate = str[0].split("-");
			if (tempDate != null && tempDate.length > 2) {
				date = tempDate[1] + "-" + tempDate[2];
			}
			if (str[1].equals(mContext.getResources().getString(
					R.string.morning))) {
				time = str[2];
			} else {
				String[] tempTime = str[2].split(":");
				if (tempTime != null && tempTime.length > 1) {
					time = String.valueOf(Integer.valueOf(tempTime[0]) + 12)
							+ ":" + tempTime[1];
				}
			}
		}
		if (isTask) {
			noon = mContext.getResources().getString(R.string.schedule_endTime);
		} else {
			noon = mContext.getResources().getString(
					R.string.schedule_begintime);
		}
		return date + " " + time + " " + noon;
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
		Log.e("test", "headurl :: " + name);
		dbOpenHelper.close();
		cursor.close();
		return name;
	}

	public String getSexFromDB(String dbId) {
		if (dbId == null)
			return "";
		dbOpenHelper = new DBOpenHelper(mContext);
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		String name = "";
		cursor = db.rawQuery("select * from contactsTable where dbid=?",
				new String[] { dbId });
		if (cursor.moveToFirst()) {
			name = cursor.getString(13);
		}
		dbOpenHelper.close();
		cursor.close();
		return name;
	}

	public static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);
		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}
		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,

	int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;
		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));
		if (upperBound < lowerBound) {
			return lowerBound;
		}
		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	private void setAppearAnim(View view) {
		Animation alphaAnimation = new AlphaAnimation(0.1f, 1.0f);
		alphaAnimation.setDuration(500);
		view.startAnimation(alphaAnimation);
	}

	private void setDisappearAnim(View view) {
		Animation alphaAnimation = new AlphaAnimation(1.0f, 0.1f);
		alphaAnimation.setDuration(500);
		view.startAnimation(alphaAnimation);
	}
}