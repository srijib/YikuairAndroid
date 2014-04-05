package com.bestapp.yikuair.fragments;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.ClipboardManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bestapp.yikuair.R;
import com.bestapp.yikuair.database.DBOpenHelper;
import com.bestapp.yikuair.officialaccount.PicTextList;
import com.bestapp.yikuair.officialaccount.ShowHtmlOrUrlView;
import com.bestapp.yikuair.utils.DBlog;
import com.bestapp.yikuair.utils.FriendEntity;
import com.bestapp.yikuair.utils.HttpUtils;
import com.bestapp.yikuair.utils.ImageLoaderOriginal;
import com.bestapp.yikuair.utils.MessageInfo;
import com.bestapp.yikuair.utils.UserInfo;
import com.bestapp.yikuair.utils.VoiceLoader;

public class ChatMsgViewAdapter extends BaseAdapter {
	public static interface IMsgViewType {
		final static int IM_COM_MSG = 0;
		final static int IM_TO_MSG = 1;
		final static int IM_COM_VOICE = 2;
		final static int IM_TO_VOICE = 3;
		final static int IM_COM_PICTURE = 4;
		final static int IM_TO_PICTURE = 5;
		final static int IM_COM_SCHEDULE = 6;
		final static int IM_TO_SCHEDULE = 7;
		final static int IM_COM_LOCATION = 8;
		final static int IM_TO_LOCATION = 9;
		final static int IM_COM_PIC_TEXT = 10;
		final static int IM_COUNT = 11;
		final static int IM_COM_WEN_INFO = 12;
	}

	private List<ChatMsgEntity> coll;
	private Context ctx;
	private LayoutInflater mInflater;
	private DBOpenHelper dbOpenHelper;
	private Cursor cursor;
	private static ImageLoaderOriginal imageLoader = null;
	private static VoiceLoader voiceLoader = null;
	public ImageLoader imgLoader;

	private static String SDCARDPATH = Environment
			.getExternalStorageDirectory() + "/" + "yikuair/";
	private ShowHtmlOrUrlView mShowHtmlOrUrlView;

	public ChatMsgViewAdapter(Context context, List<ChatMsgEntity> coll) {
		ctx = context;
		this.coll = coll;
		mInflater = LayoutInflater.from(context);
		mShowHtmlOrUrlView = new ShowHtmlOrUrlView(context);
		this.imgLoader = new ImageLoader(context);
		if (imageLoader == null) {
			imageLoader = new ImageLoaderOriginal(ctx);
		}
		if (voiceLoader == null) {
			voiceLoader = new VoiceLoader(ctx);
		}
	}

	public int getCount() {
		return coll.size();
	}

	public Object getItem(int position) {
		return coll.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		ChatMsgEntity entity = coll.get(position);

		if (entity.getIsComing()) {
			if (entity.getType() == MessageInfo.TEXT) {
				return IMsgViewType.IM_COM_MSG;
			} else if (entity.getType() == MessageInfo.VOICE) {
				return IMsgViewType.IM_COM_VOICE;
			} else if (entity.getType() == MessageInfo.PICTURE) {
				return IMsgViewType.IM_COM_PICTURE;
			} else if (entity.getType() == MessageInfo.LOCATION) {
				return IMsgViewType.IM_COM_LOCATION;
			} else if (entity.getType() == MessageInfo.PIC_TEXT) {
				return IMsgViewType.IM_COM_PIC_TEXT;
			} else if (entity.getType() == MessageInfo.INFO_WEB) {
				return IMsgViewType.IM_COM_WEN_INFO;
			} else {
				return IMsgViewType.IM_COM_SCHEDULE;
			}
		} else {
			if (entity.getType() == MessageInfo.TEXT) {
				return IMsgViewType.IM_TO_MSG;
			} else if (entity.getType() == MessageInfo.VOICE) {
				return IMsgViewType.IM_TO_VOICE;
			} else if (entity.getType() == MessageInfo.PICTURE) {
				return IMsgViewType.IM_TO_PICTURE;
			} else if (entity.getType() == MessageInfo.LOCATION) {
				return IMsgViewType.IM_TO_LOCATION;
			} else {
				return IMsgViewType.IM_TO_SCHEDULE;
			}
		}
	}

	public int getViewTypeCount() {
		return IMsgViewType.IM_COUNT;
	}

	@SuppressLint("NewApi")
	public View getView(final int position, View convertView, ViewGroup parent) {

		final ChatMsgEntity entity = coll.get(position);
		final boolean isComing = entity.getIsComing();
		int type = entity.getType();

		ViewHolder viewHolder = null;
		// if (convertView == null) {
		viewHolder = new ViewHolder();
		if (isComing) {
			if (type == MessageInfo.TEXT || type == MessageInfo.COMPANY_NEWS) {
				convertView = mInflater.inflate(
						R.layout.chatting_item_msg_text_left, null);
			} else if (type == MessageInfo.VOICE) {
				convertView = mInflater.inflate(
						R.layout.chatting_item_msg_voice_left, null);
			} else if (type == MessageInfo.PICTURE) {
				convertView = mInflater.inflate(
						R.layout.chatting_item_msg_picture_left, null);
			} else if (type == MessageInfo.LOCATION) {
				convertView = mInflater.inflate(
						R.layout.chatting_item_msg_location_left, null);
			} else if (type == MessageInfo.PIC_TEXT) {
				convertView = mInflater.inflate(
						R.layout.chatting_item_msg_pic_text_left, null);
			} else if (type == MessageInfo.INFO_WEB) {
				convertView = mInflater.inflate(R.layout.chatting_item_msg_web,
						null);
				viewHolder.web_info = (WebView) convertView
						.findViewById(R.id.web_info);
			} else {
				convertView = mInflater.inflate(
						R.layout.chatting_item_msg_schedule_left, null);
			}
		} else {
			if (type == MessageInfo.TEXT) {
				convertView = mInflater.inflate(
						R.layout.chatting_item_msg_text_right, null);
			} else if (type == MessageInfo.VOICE) {
				convertView = mInflater.inflate(
						R.layout.chatting_item_msg_voice_right, null);
			} else if (type == MessageInfo.PICTURE) {
				convertView = mInflater.inflate(
						R.layout.chatting_item_msg_picture_right, null);
			} else if (type == MessageInfo.LOCATION) {
				convertView = mInflater.inflate(
						R.layout.chatting_item_msg_location_right, null);
			} else {
				convertView = mInflater.inflate(
						R.layout.chatting_item_msg_schedule_right, null);
			}
		}
		viewHolder.tvSendTime = (TextView) convertView
				.findViewById(R.id.tv_sendtime);

		viewHolder.tvDate = (TextView) convertView.findViewById(R.id.tv_date);

		if (type != MessageInfo.PIC_TEXT)
			viewHolder.photo = (ImageView) convertView
					.findViewById(R.id.iv_userhead);
		viewHolder.ll_tv_show = (LinearLayout) convertView
				.findViewById(R.id.iv_show);

		if (!isComing) {
			viewHolder.tvState = (TextView) convertView
					.findViewById(R.id.tv_state);
			viewHolder.ll_progressBar = (RelativeLayout) convertView
					.findViewById(R.id.ll_progressbar);
			viewHolder.rl_state = (RelativeLayout) convertView
					.findViewById(R.id.rl_state);
		}
		if (type == MessageInfo.SCHEDULE) {
			viewHolder.scheduleTitle = (TextView) convertView
					.findViewById(R.id.tv_title_content);
			viewHolder.scheduleBeginTime = (TextView) convertView
					.findViewById(R.id.tv_beginTime_content);
			viewHolder.scheduleEndTime = (TextView) convertView
					.findViewById(R.id.tv_endTime_content);
			viewHolder.scheduleAddress = (TextView) convertView
					.findViewById(R.id.tv_address_content);
			viewHolder.ll_beginTime = (LinearLayout) convertView
					.findViewById(R.id.ll_beginTime);
			viewHolder.ll_endTime = (LinearLayout) convertView
					.findViewById(R.id.ll_endTime);
			viewHolder.ll_address = (LinearLayout) convertView
					.findViewById(R.id.ll_address);
			viewHolder.ll_schedule_content = (LinearLayout) convertView
					.findViewById(R.id.tv_chatcontent);
			if (isComing == true)
				viewHolder.tv_check = (TextView) convertView
						.findViewById(R.id.tv_check);
		}

		if (type == MessageInfo.PICTURE) {
			viewHolder.ivPicture = (ImageView) convertView
					.findViewById(R.id.iv_chatpicture);
		} else if (type == MessageInfo.TEXT || type == MessageInfo.COMPANY_NEWS
				|| type == MessageInfo.LOCATION || type == MessageInfo.PIC_TEXT) {
			viewHolder.tvContent = (TextView) convertView
					.findViewById(R.id.tv_chatcontent);
			if (type == MessageInfo.LOCATION) {
				viewHolder.ll_tv_content = (LinearLayout) convertView
						.findViewById(R.id.ll_chatcontent);
				viewHolder.ll_tv_content.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(ctx, LocationMessage.class);
						if (entity.getlongitude() != null
								&& entity.getlatitude() != null
								&& entity.getContent() != null) {
							Double longitude = Double.parseDouble(entity
									.getlongitude());
							Double latitude = Double.parseDouble(entity
									.getlatitude());
							String location = entity.getContent();
							intent.putExtra("location", location);
							intent.putExtra("longitude", longitude);
							intent.putExtra("latitude", latitude);

							ctx.startActivity(intent);
							((Activity) ctx).overridePendingTransition(
									R.anim.in_from_right, R.anim.out_of_left);
						}

					}
				}

				);
			}

			if (type == MessageInfo.PIC_TEXT) {
				viewHolder.title = (TextView) convertView
						.findViewById(R.id.tv_title);
				viewHolder.ivPicture = (ImageView) convertView
						.findViewById(R.id.iv_content);
				viewHolder.ll_tv_content = (LinearLayout) convertView
						.findViewById(R.id.ll_content);
				// viewHolder.pic_txt_list = (ListView) convertView
				// .findViewById(R.id.pic_txt_list);
			}

			if (type == MessageInfo.PIC_TEXT) {
				viewHolder.web_info = (WebView) convertView
						.findViewById(R.id.web_info);
			}
		} else if (type == MessageInfo.VOICE) {
			viewHolder.chatSec = (TextView) convertView
					.findViewById(R.id.tv_second);
			viewHolder.voiceContent = (RelativeLayout) convertView
					.findViewById(R.id.voice_content);
			viewHolder.voiceAnim = (ImageView) convertView
					.findViewById(R.id.voice_anim);
		}
		convertView.setTag(viewHolder);
		/*
		 * } else { viewHolder = (ViewHolder) convertView.getTag(); }
		 */
		if (type != MessageInfo.PIC_TEXT && type != MessageInfo.INFO_WEB) {
			if (!isComing) {
				try {
					if (UserInfo.LocalphotoPath != null
							&& UserInfo.LocalphotoPath.length() > 0) {
						BitmapFactory.Options opts = new BitmapFactory.Options();
						opts.inJustDecodeBounds = true;
						BitmapFactory.decodeFile(UserInfo.LocalphotoPath, opts);
						opts.inSampleSize = computeSampleSize(opts, -1,
								128 * 128);
						opts.inJustDecodeBounds = false;
						try {
							Bitmap bmp = BitmapFactory.decodeFile(
									UserInfo.LocalphotoPath, opts);

							viewHolder.photo
									.setBackgroundDrawable(new BitmapDrawable(
											bmp));

						} catch (OutOfMemoryError err) {

						}

					} else {
						if (UserInfo.sex != null && UserInfo.sex.equals("0"))
							viewHolder.photo.setBackgroundDrawable(ctx
									.getResources()
									.getDrawable(R.drawable.girl));
						else
							viewHolder.photo
									.setBackgroundDrawable(ctx.getResources()
											.getDrawable(R.drawable.boy));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				if (getHeadUrlFromDB(entity.getSenderId()) != null
						&& getHeadUrlFromDB(entity.getSenderId()).length() > 0) {
					String headUrl = "http://" + UserInfo.downloadImgUrl
							+ getHeadUrlFromDB(entity.getSenderId());
					imgLoader.DisplayImage(headUrl, (Activity) ctx,
							viewHolder.photo);
				} else {
					if (getSexFromDB(entity.getSenderId()).equals("0"))
						viewHolder.photo.setImageDrawable(ctx.getResources()
								.getDrawable(R.drawable.ico_girl));
					else
						viewHolder.photo.setImageDrawable(ctx.getResources()
								.getDrawable(R.drawable.ico_boy));
				}
			}

			viewHolder.photo.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(ctx,
							PersonalProfileActivity.class);
					if (isComing) {
						FriendEntity friendEntity = getPersonalProfile(entity
								.getSenderId());
						Bundle bundle = new Bundle();
						bundle.putSerializable("friendEntity", friendEntity);
						intent.putExtras(bundle);
					} else {
						intent.putExtra("individualInfo", " ");
					}
					intent.putExtra("isFromChat", true);
					ctx.startActivity(intent);
					((Activity) ctx).overridePendingTransition(
							R.anim.in_from_right, R.anim.out_of_left);
				}
			});
		}
		viewHolder.tvSendTime.setText(entity.getTime());
		if ((entity.getDate() != null) && (!entity.getDate().equals(""))) {
			viewHolder.tvDate.setVisibility(View.VISIBLE);
			viewHolder.tvDate.setText(entity.getDate());
		} else {
			viewHolder.tvDate.setVisibility(View.GONE);
		}
		if (isComing == false) {
			viewHolder.tvState.setBackgroundResource(entity.getBackground());
			viewHolder.tvState.setText(entity.getChatState());
			viewHolder.ll_progressBar.setVisibility(View.GONE/*
															 * entity.getAnimVisible
															 * ()
															 */);
			viewHolder.rl_state.setVisibility(View.VISIBLE/*
														 * entity.getStateVisible
														 * ()
														 */);
		}
		if (type == MessageInfo.SCHEDULE) {
			viewHolder.scheduleTitle.setText(entity.getScheduleTitle());
			if (entity.getScheduleBeginTime() != null
					&& entity.getScheduleType() != MessageInfo.TASK) {
				viewHolder.scheduleBeginTime.setText(entity
						.getScheduleBeginTime());
				viewHolder.ll_beginTime.setVisibility(View.VISIBLE);
			}

			DisplayMetrics dm = ctx.getResources().getDisplayMetrics();
			int mScreenWidth = dm.widthPixels;

			int maxWidth = 0;

			maxWidth = mScreenWidth - 40 - (int) (0.55 * mScreenWidth);

			viewHolder.scheduleTitle.setMaxWidth(maxWidth);
			viewHolder.scheduleAddress.setMaxWidth(maxWidth);

			if (entity.getScheduleAddress() != null
					&& entity.getScheduleAddress().length() > 0) {
				Log.e("test", "address :" + entity.getScheduleAddress());
				viewHolder.scheduleAddress.setText(entity.getScheduleAddress());
				viewHolder.ll_address.setVisibility(View.VISIBLE);
			}
			viewHolder.scheduleEndTime.setText(entity.getScheduleEndTime());

			viewHolder.ll_schedule_content
					.setOnLongClickListener(new OnLongClickListener() {
						@Override
						public boolean onLongClick(View arg0) {
							// TODO Auto-generated method stub
							String title;
							if (isComing) {
								if (entity.getSenderId() == null)
									return false;
								title = getNameFromDB(entity.getSenderId());
								if (entity.getSenderId() == UserInfo.companyNews_dbId)
									title = "公司动态";
								else if (entity.getSenderId() == UserInfo.feedback_dbId)
									title = "用户反馈";
								else
									title = entity.getChatName();
								String[] groupStr = entity.getSenderId().split(
										"、");
								if (groupStr.length > 1)
									title = "群聊" + "("
											+ Integer.toString(groupStr.length)
											+ "人" + ")";
							} else {
								if (entity.getReceiverId() == null)
									return false;
								title = getNameFromDB(entity.getReceiverId());
								if (entity.getReceiverId() == UserInfo.companyNews_dbId)
									title = "公司动态";
								else if (entity.getReceiverId() == UserInfo.feedback_dbId)
									title = "用户反馈";
								else
									title = entity.getChatName();

								String[] groupStr = entity.getReceiverId()
										.split("、");
								if (groupStr.length > 1)
									title = "群聊" + "("
											+ Integer.toString(groupStr.length)
											+ "人" + ")";
							}

							AlertDialog dialog = new AlertDialog.Builder(ctx)
									.setTitle(title)
									// 对话框标题
									.setItems(
											new String[] { "查看", "删除" },
											new DialogInterface.OnClickListener() {// 每一条的名称
												@SuppressWarnings("deprecation")
												public void onClick(
														DialogInterface dialog,
														int which) {// 响应点击事件
													if (which == 0) {

														String[] names = null;
														String[] ids = null;
														String[] tempNames = null;
														String[] tempIds = null;
														if (entity
																.getGroupNames() != null)
															tempNames = entity
																	.getGroupNames()
																	.split("、");
														if (entity
																.getGroupIds() != null)
															tempIds = entity
																	.getGroupIds()
																	.split("、");
														if (tempIds.length == 1) {
															ids = new String[2];
															names = new String[2];
															ids[0] = UserInfo.db_id;
															ids[1] = tempIds[0];
															names[0] = UserInfo.realName;
															names[1] = tempNames[0];
														} else {
															ids = tempIds;
															names = tempNames;
														}
														if (entity
																.getScheduleType() == MessageInfo.TASK) {
															Log.e("test",
																	"task........");
															Intent intent = new Intent(
																	ctx,
																	ScheduleTaskActivity.class);
															Bundle bundle = new Bundle();
															bundle.putStringArray(
																	"nameStr",
																	names);
															bundle.putStringArray(
																	"idStr",
																	ids);
															intent.putExtra(
																	"endDate",
																	entity.getScheduleEndTime());
															intent.putExtra(
																	"type",
																	MessageInfo.TASK);
															if (isComing) {
																intent.putExtra(
																		"ItemId",
																		"");
																intent.putExtra(
																		"isFromChat",
																		true);
															} else {
																intent.putExtra(
																		"ItemId",
																		entity.getScheduleItemId());
																intent.putExtra(
																		"isFromChat",
																		false);
															}
															intent.putExtra(
																	"taskId",
																	entity.getScheduleTaskId());
															intent.putExtra(
																	"isFromChatToModify",
																	true);
															intent.putExtra(
																	"title",
																	entity.getScheduleTitle());
															intent.putExtras(bundle);
															((Activity) ctx)
																	.startActivityForResult(
																			intent,
																			4);
															((Activity) ctx)
																	.overridePendingTransition(
																			R.anim.in_from_down,
																			R.anim.out_of_up);
														} else if (entity
																.getScheduleType() == MessageInfo.MEETING) {
															Log.e("test",
																	"meeting........");
															Intent intent = new Intent(
																	ctx,
																	ScheduleMeetingActivity.class);
															Bundle bundle = new Bundle();
															bundle.putStringArray(
																	"nameStr",
																	names);
															bundle.putStringArray(
																	"idStr",
																	ids);
															intent.putExtra(
																	"beginDate",
																	entity.getScheduleBeginTime());
															intent.putExtra(
																	"endDate",
																	entity.getScheduleEndTime());
															intent.putExtra(
																	"type",
																	MessageInfo.MEETING);
															intent.putExtra(
																	"address",
																	entity.getScheduleAddress());
															if (isComing) {
																intent.putExtra(
																		"ItemId",
																		"");
																intent.putExtra(
																		"isFromChat",
																		true);
															} else {
																intent.putExtra(
																		"ItemId",
																		entity.getScheduleItemId());
																intent.putExtra(
																		"isFromChat",
																		false);
															}
															intent.putExtra(
																	"isFromChatToModify",
																	true);
															intent.putExtra(
																	"taskId",
																	entity.getScheduleTaskId());

															intent.putExtra(
																	"title",
																	entity.getScheduleTitle());
															intent.putExtras(bundle);
															((Activity) ctx)
																	.startActivityForResult(
																			intent,
																			4);
															((Activity) ctx)
																	.overridePendingTransition(
																			R.anim.in_from_down,
																			R.anim.out_of_up);
														} else {
															Log.e("test",
																	"other.........");
															Intent intent = new Intent(
																	ctx,
																	ScheduleOtherActivity.class);
															Bundle bundle = new Bundle();
															bundle.putStringArray(
																	"nameStr",
																	names);
															bundle.putStringArray(
																	"idStr",
																	ids);
															intent.putExtra(
																	"beginDate",
																	entity.getScheduleBeginTime());
															intent.putExtra(
																	"endDate",
																	entity.getScheduleEndTime());
															intent.putExtra(
																	"type",
																	MessageInfo.OTHER);
															intent.putExtra(
																	"address",
																	entity.getScheduleAddress());
															if (isComing) {
																intent.putExtra(
																		"ItemId",
																		"");
																intent.putExtra(
																		"isFromChat",
																		true);
															} else {
																intent.putExtra(
																		"ItemId",
																		entity.getScheduleItemId());
																intent.putExtra(
																		"isFromChat",
																		false);
															}
															intent.putExtra(
																	"taskId",
																	entity.getScheduleTaskId());
															intent.putExtra(
																	"isFromChatToModify",
																	true);
															intent.putExtra(
																	"title",
																	entity.getScheduleTitle());
															intent.putExtras(bundle);
															((Activity) ctx)
																	.startActivityForResult(
																			intent,
																			4);
															((Activity) ctx)
																	.overridePendingTransition(
																			R.anim.in_from_down,
																			R.anim.out_of_up);
														}
													} else {
														if (entity
																.getMessageType() == MessageInfo.COMPANY_NEWS) {
															CompanyNewsFragment.mDataArrays
																	.remove(position);
															notifyDataSetChanged();
														} else if (entity
																.getMessageType() == MessageInfo.USER_FEEDBACK) {
															FeedBackFragment.mDataArrays
																	.remove(position);
															notifyDataSetChanged();
														} else {
															ChatActivity.mDataArrays
																	.remove(position);
															notifyDataSetChanged();
														}
													}
												}
											}).create();
							dialog.show();
							dialog.setCanceledOnTouchOutside(true);

							return false;
						}
					});

			/******************************************/
			viewHolder.ll_schedule_content
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {

							String[] names = null;
							String[] ids = null;
							String[] tempNames = null;
							String[] tempIds = null;
							if (entity.getGroupNames() != null)
								tempNames = entity.getGroupNames().split("、");
							if (entity.getGroupIds() != null)
								tempIds = entity.getGroupIds().split("、");
							if (tempIds.length == 1) {
								ids = new String[2];
								names = new String[2];
								ids[0] = UserInfo.db_id;
								ids[1] = tempIds[0];
								names[0] = UserInfo.realName;
								names[1] = tempNames[0];
							} else {
								ids = tempIds;
								names = tempNames;
							}
							if (entity.getScheduleType() == MessageInfo.TASK) {
								Log.e("test", "task........");
								Intent intent = new Intent(ctx,
										ScheduleTaskActivity.class);
								Bundle bundle = new Bundle();
								bundle.putStringArray("nameStr", names);
								bundle.putStringArray("idStr", ids);
								intent.putExtra("endDate",
										entity.getScheduleEndTime());
								intent.putExtra("type", MessageInfo.TASK);
								if (isComing) {
									intent.putExtra("ItemId", "");
									intent.putExtra("isFromChat", true);
								} else {
									intent.putExtra("ItemId",
											entity.getScheduleItemId());
									intent.putExtra("isFromChat", false);
								}
								intent.putExtra("taskId",
										entity.getScheduleTaskId());
								intent.putExtra("isFromChatToModify", true);
								intent.putExtra("title",
										entity.getScheduleTitle());
								intent.putExtras(bundle);
								((Activity) ctx).startActivityForResult(intent,
										4);
								((Activity) ctx).overridePendingTransition(
										R.anim.in_from_down, R.anim.out_of_up);
							} else if (entity.getScheduleType() == MessageInfo.MEETING) {
								Log.e("test", "meeting........");
								Intent intent = new Intent(ctx,
										ScheduleMeetingActivity.class);
								Bundle bundle = new Bundle();
								bundle.putStringArray("nameStr", names);
								bundle.putStringArray("idStr", ids);
								intent.putExtra("beginDate",
										entity.getScheduleBeginTime());
								intent.putExtra("endDate",
										entity.getScheduleEndTime());
								intent.putExtra("type", MessageInfo.MEETING);
								intent.putExtra("address",
										entity.getScheduleAddress());
								if (isComing) {
									intent.putExtra("ItemId", "");
									intent.putExtra("isFromChat", true);
								} else {
									intent.putExtra("ItemId",
											entity.getScheduleItemId());
									intent.putExtra("isFromChat", false);
								}
								intent.putExtra("isFromChatToModify", true);
								intent.putExtra("taskId",
										entity.getScheduleTaskId());

								intent.putExtra("title",
										entity.getScheduleTitle());
								intent.putExtras(bundle);
								((Activity) ctx).startActivityForResult(intent,
										4);
								((Activity) ctx).overridePendingTransition(
										R.anim.in_from_down, R.anim.out_of_up);
							} else {
								Log.e("test", "other.........");
								Intent intent = new Intent(ctx,
										ScheduleOtherActivity.class);
								Bundle bundle = new Bundle();
								bundle.putStringArray("nameStr", names);
								bundle.putStringArray("idStr", ids);
								intent.putExtra("beginDate",
										entity.getScheduleBeginTime());
								intent.putExtra("endDate",
										entity.getScheduleEndTime());
								intent.putExtra("type", MessageInfo.OTHER);
								intent.putExtra("address",
										entity.getScheduleAddress());
								if (isComing) {
									intent.putExtra("ItemId", "");
									intent.putExtra("isFromChat", true);
								} else {
									intent.putExtra("ItemId",
											entity.getScheduleItemId());
									intent.putExtra("isFromChat", false);
								}
								intent.putExtra("taskId",
										entity.getScheduleTaskId());

								intent.putExtra("isFromChatToModify", true);
								intent.putExtra("title",
										entity.getScheduleTitle());
								intent.putExtras(bundle);
								((Activity) ctx).startActivityForResult(intent,
										4);
								((Activity) ctx).overridePendingTransition(
										R.anim.in_from_down, R.anim.out_of_up);
							}
						}
					});

			if (isComing == true) {
				viewHolder.tv_check.setText(entity.getCheckStatus());
				viewHolder.tv_check.setBackgroundResource(entity
						.getCheckBackground());

				viewHolder.tv_check.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						if (entity.getCheckStatus()
								.equals(ctx.getResources().getString(
										R.string.canceled)))
							return;
						ClientSocket client = new ClientSocket(ctx);
						client.sendMessage(null, 14, entity.getMsguuid(),
								entity.getSenderId(), UserInfo.db_id, null,
								null, null, entity.getScheduleTaskId(),
								String.valueOf(entity.getChatType()), null,
								false);

						sendMessageBroadcast(entity.getMsguuid());// modify
																	// check
																	// status

						/*
						 * Log.e("test", "entity scheduelBeginTime :" +
						 * entity.getScheduleBeginTime()); Log.e("test",
						 * "entity scheduelEndTime :" +
						 * entity.getScheduleEndTime());
						 */
						ScheduleItemInfo item = new ScheduleItemInfo();
						item.setScheduleContent(entity.getScheduleTitle());
						item.setScheduleBeginTime(getScheduleDate(entity
								.getScheduleBeginTime()));
						item.setScheduleEndTime(getScheduleDate(entity
								.getScheduleEndTime()));
						item.setType(entity.getScheduleType());
						item.setSponsorName(getNameFromDB(entity.getSenderId()));
						item.setId(entity.getSenderId());// sender dbid
						item.setTaskId(entity.getScheduleTaskId());
						if (entity.getScheduleAddress() != null
								&& entity.getScheduleAddress().length() > 0)
							item.setAddress(entity.getScheduleAddress());

						String idStr = entity.getGroupIds();
						String nameStr = entity.getGroupNames();
						if (idStr != null && nameStr != null) {
							String[] ids;
							String[] names;
							String[] tempIds = idStr.split("、");
							String[] tempNames = nameStr.split("、");
							if (tempIds.length == 1) {
								ids = new String[2];
								names = new String[2];
								for (int i = 0; i < 2; i++) {
									if (i == 0) {
										ids[i] = UserInfo.db_id;
										names[i] = UserInfo.realName;
									} else {
										ids[i] = tempIds[0];
										names[i] = tempNames[0];
									}
								}
							} else {
								ids = tempIds;
								names = tempNames;
							}
							item.setMemberId(ids);
							item.setMemberName(names);
						}
						item.setIsFromChat(true);

						/*
						 * String[] str = entity.getReceiverId().split(",");
						 * String[] idStr = new String[str.length]; if (str !=
						 * null) for (int i = 0; i < str.length; i++) { idStr[i]
						 * = str[i]; Log.e("test","id:" + idStr[i]); }
						 * item.setMemberId(idStr);
						 */
						MessageInfo.scheduleList.add(item);
					}
				});
			}
		} else if (type == MessageInfo.PICTURE) {
			if (!isComing) {
				/*
				 * Bitmap bitmap = BitmapCompressUtil
				 * .getResizeImage(BitmapCompressUtil.rotateImageView(
				 * BitmapCompressUtil.readPictureDegree(entity .getBigPicUrl()),
				 * BitmapCompressUtil .decodeFileFromPath(entity
				 * .getBigPicUrl())));
				 */

				Bitmap bitmap = BitmapFactory.decodeFile(entity
						.getSmallPicUrl()/* entity.getBigPicUrl() */);
				viewHolder.ivPicture.setImageBitmap(bitmap);
			} else {
				getImage(convertView, entity.getSmallPicUrl(),
						R.id.iv_chatpicture);
			}

			viewHolder.ivPicture
					.setOnLongClickListener(new OnLongClickListener() {
						@Override
						public boolean onLongClick(View arg0) {
							// TODO Auto-generated method stub
							String title;
							if (isComing) {
								if (entity.getSenderId() == null)
									return false;
								title = getNameFromDB(entity.getSenderId());
								if (entity.getSenderId() == UserInfo.companyNews_dbId)
									title = "公司动态";
								else if (entity.getSenderId() == UserInfo.feedback_dbId)
									title = "用户反馈";
								else
									title = entity.getChatName();

								String[] groupStr = entity.getSenderId().split(
										"、");
								if (groupStr.length > 1)
									title = "群聊" + "("
											+ Integer.toString(groupStr.length)
											+ "人" + ")";
							} else {
								if (entity.getReceiverId() == null)
									return false;
								title = getNameFromDB(entity.getReceiverId());
								if (entity.getReceiverId() == UserInfo.companyNews_dbId)
									title = "公司动态";
								else if (entity.getReceiverId() == UserInfo.feedback_dbId)
									title = "用户反馈";
								else
									title = entity.getChatName();

								String[] groupStr = entity.getReceiverId()
										.split("、");
								if (groupStr.length > 1)
									title = "群聊" + "("
											+ Integer.toString(groupStr.length)
											+ "人" + ")";
							}

							AlertDialog dialog = new AlertDialog.Builder(ctx)
									.setTitle(title)
									// 对话框标题
									.setItems(
											new String[] { "查看", "删除" },
											new DialogInterface.OnClickListener() {// 每一条的名称
												@SuppressWarnings("deprecation")
												public void onClick(
														DialogInterface dialog,
														int which) {// 响应点击事件
													if (which == 0) {
														String sdStatus = Environment
																.getExternalStorageState();
														if (!sdStatus
																.equals(Environment.MEDIA_MOUNTED)) {
															Toast.makeText(
																	ctx,
																	ctx.getString(R.string.sdcard_error),
																	Toast.LENGTH_SHORT)
																	.show();
															return;
														}

														final String imagePath = coll
																.get(position)
																.getBigPicUrl();
														final Boolean isDownload = coll
																.get(position)
																.getIsComing();
														Intent intent = new Intent();
														Log.i("test",
																"bigimagePath :"
																		+ imagePath);
														intent.putExtra(
																"imagePath",
																imagePath);
														intent.putExtra(
																"isDownload",
																isDownload);
														intent.setClass(
																ctx,
																ImageActivity.class);
														ctx.startActivity(intent);
													} else {
														if (entity
																.getMessageType() == MessageInfo.COMPANY_NEWS) {
															CompanyNewsFragment.mDataArrays
																	.remove(position);
															notifyDataSetChanged();
														} else if (entity
																.getMessageType() == MessageInfo.USER_FEEDBACK) {
															FeedBackFragment.mDataArrays
																	.remove(position);
															notifyDataSetChanged();
														} else {
															ChatActivity.mDataArrays
																	.remove(position);
															notifyDataSetChanged();
														}
													}
												}
											}).create();
							dialog.show();
							dialog.setCanceledOnTouchOutside(true);

							return false;
						}
					});

			viewHolder.ivPicture.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {

					String sdStatus = Environment.getExternalStorageState();
					if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
						Toast.makeText(ctx,
								ctx.getString(R.string.sdcard_error),
								Toast.LENGTH_SHORT).show();
						return;
					}

					if (position > coll.size() - 1)
						return;
					final String imagePath = coll.get(position).getBigPicUrl();
					final Boolean isDownload = coll.get(position).getIsComing();
					Intent intent = new Intent();
					Log.i("test", "bigimagePath :" + imagePath);
					intent.putExtra("imagePath", imagePath);
					intent.putExtra("isDownload", isDownload);
					intent.setClass(ctx, ImageActivity.class);
					ctx.startActivity(intent);
				}
			});
		} else if (type == MessageInfo.INFO_WEB) {
			if (entity.getDetail() != null
					&& !entity.getDetail().trim().equals("")) {
				try {
					viewHolder.web_info.loadData(
							URLEncoder.encode(entity.getDetail(), "utf-8"),
							"text/html", "utf-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
				}
			} else if (entity.getUrl() != null
					&& !entity.getUrl().trim().equals("")) {
			}
		} else if (type == MessageInfo.TEXT || type == MessageInfo.COMPANY_NEWS
				|| type == MessageInfo.LOCATION || type == MessageInfo.PIC_TEXT) {
			DisplayMetrics dm = ctx.getResources().getDisplayMetrics();
			int mScreenWidth = dm.widthPixels;

			int maxWidth = 0;
			// if (isComing) {
			// maxWidth = mScreenWidth - 40 - (int) (0.3 * mScreenWidth);
			// } else {
			maxWidth = mScreenWidth - 40 - (int) (0.25 * mScreenWidth);
			// }

			if (type != MessageInfo.LOCATION && type != MessageInfo.PIC_TEXT)
				viewHolder.tvContent.setMaxWidth(maxWidth);

			viewHolder.tvContent.setText(entity.getContent());

			if (type == MessageInfo.PIC_TEXT) {
				viewHolder.title.setText(entity.getTitle());

				viewHolder.ll_tv_show.setTag(entity.getParentPicText());
				viewHolder.ll_tv_show.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						mShowHtmlOrUrlView.ShowWindow((PicTextList) v.getTag());
					}
				});
				getImage(convertView, entity.getSmallPicUrl(), R.id.iv_content);
				for (int i = 0; i < entity.getList().size(); i++) {
					View myView = mInflater
							.inflate(R.layout.pic_txt_item, null);
					ImageView img = (ImageView) myView
							.findViewById(R.id.item_image_path);
					DBlog.e("---------------------", entity.getList().get(i)
							.getImgpath());
					new ImageAsynTask(entity.getList().get(i).getImgpath(), img)
							.execute();
					myView.setLayoutParams(new LinearLayout.LayoutParams(
							LayoutParams.MATCH_PARENT, 200));
					// imgLoader.DisplayImage(
					// entity.getList().get(i).getImgpath(),
					// (Activity) ctx, img);
					TextView textView = (TextView) myView
							.findViewById(R.id.item_title);
					textView.setText(entity.getList().get(i).getTitle());
					viewHolder.ll_tv_content.addView(myView);
					myView.setTag(entity.getList().get(i));
					myView.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							mShowHtmlOrUrlView.ShowWindow((PicTextList) v
									.getTag());
						}
					});
				}
			}
			if (type == MessageInfo.LOCATION || type == MessageInfo.PIC_TEXT)
				viewHolder.ll_tv_content
						.setOnLongClickListener(new OnLongClickListener() {
							@Override
							public boolean onLongClick(View arg0) {
								String title;
								if (isComing) {
									if (entity.getSenderId() == null)
										return false;
									title = getNameFromDB(entity.getSenderId());
									if (entity.getSenderId() == UserInfo.companyNews_dbId)
										title = "公司动态";
									else if (entity.getSenderId() == UserInfo.feedback_dbId)
										title = "用户反馈";
									else
										title = entity.getChatName();

									String[] groupStr = entity.getSenderId()
											.split("、");
									if (groupStr.length > 1)
										title = "群聊"
												+ "("
												+ Integer
														.toString(groupStr.length)
												+ "人" + ")";
								} else {
									if (entity.getReceiverId() == null)
										return false;
									title = getNameFromDB(entity
											.getReceiverId());
									if (entity.getReceiverId() == UserInfo.companyNews_dbId)
										title = "公司动态";
									else if (entity.getReceiverId() == UserInfo.feedback_dbId)
										title = "用户反馈";
									else
										title = entity.getChatName();

									String[] groupStr = entity.getReceiverId()
											.split("、");
									if (groupStr.length > 1)
										title = "群聊"
												+ "("
												+ Integer
														.toString(groupStr.length)
												+ "人" + ")";
								}

								AlertDialog dialog = new AlertDialog.Builder(
										ctx)
										.setTitle(title)
										// 对话框标题
										.setItems(
												new String[] { "复制", "删除" },
												new DialogInterface.OnClickListener() {// 每一条的名称
													@SuppressWarnings("deprecation")
													public void onClick(
															DialogInterface dialog,
															int which) {// 响应点击事件
														if (which == 0) {
															ClipboardManager clip = (ClipboardManager) ctx
																	.getSystemService(Context.CLIPBOARD_SERVICE);
															clip.setText(entity
																	.getContent());
														} else {
															if (entity
																	.getMessageType() == MessageInfo.COMPANY_NEWS) {
																CompanyNewsFragment.mDataArrays
																		.remove(position);
																notifyDataSetChanged();
															} else if (entity
																	.getMessageType() == MessageInfo.USER_FEEDBACK) {
																FeedBackFragment.mDataArrays
																		.remove(position);
																notifyDataSetChanged();
															} else {
																ChatActivity.mDataArrays
																		.remove(position);
																notifyDataSetChanged();
															}
														}
													}
												}).create();
								dialog.show();
								dialog.setCanceledOnTouchOutside(true);
								return false;
							}
						});
			else
				viewHolder.tvContent
						.setOnLongClickListener(new OnLongClickListener() {
							@Override
							public boolean onLongClick(View arg0) {
								String title;
								if (isComing) {
									if (entity.getSenderId() == null)
										return false;
									title = getNameFromDB(entity.getSenderId());
									if (entity.getSenderId() == UserInfo.companyNews_dbId)
										title = "公司动态";
									else if (entity.getSenderId() == UserInfo.feedback_dbId)
										title = "用户反馈";
									else
										title = entity.getChatName();

									String[] groupStr = entity.getSenderId()
											.split("、");
									if (groupStr.length > 1)
										title = "群聊"
												+ "("
												+ Integer
														.toString(groupStr.length)
												+ "人" + ")";
								} else {
									if (entity.getReceiverId() == null)
										return false;
									title = getNameFromDB(entity
											.getReceiverId());
									if (entity.getReceiverId() == UserInfo.companyNews_dbId)
										title = "公司动态";
									else if (entity.getReceiverId() == UserInfo.feedback_dbId)
										title = "用户反馈";
									else
										title = entity.getChatName();

									String[] groupStr = entity.getReceiverId()
											.split("、");
									if (groupStr.length > 1)
										title = "群聊"
												+ "("
												+ Integer
														.toString(groupStr.length)
												+ "人" + ")";
								}

								AlertDialog dialog = new AlertDialog.Builder(
										ctx)
										.setTitle(title)
										// 对话框标题
										.setItems(
												new String[] { "复制", "删除" },
												new DialogInterface.OnClickListener() {// 每一条的名称
													@SuppressWarnings("deprecation")
													public void onClick(
															DialogInterface dialog,
															int which) {// 响应点击事件
														if (which == 0) {
															ClipboardManager clip = (ClipboardManager) ctx
																	.getSystemService(Context.CLIPBOARD_SERVICE);
															clip.setText(entity
																	.getContent());
														} else {
															if (entity
																	.getMessageType() == MessageInfo.COMPANY_NEWS) {
																CompanyNewsFragment.mDataArrays
																		.remove(position);
																notifyDataSetChanged();
															} else if (entity
																	.getMessageType() == MessageInfo.USER_FEEDBACK) {
																FeedBackFragment.mDataArrays
																		.remove(position);
																notifyDataSetChanged();
															} else {
																ChatActivity.mDataArrays
																		.remove(position);
																notifyDataSetChanged();
															}
														}
													}
												}).create();
								dialog.show();
								dialog.setCanceledOnTouchOutside(true);
								return false;
							}
						});
		} else if (type == MessageInfo.VOICE) {
			if (isComing)
				getVoice(convertView, entity.getVoiceUrl(), R.id.tv_second,
						entity.getSecond());
			else
				viewHolder.chatSec.setText(entity.getSecond());

			viewHolder.voiceContent
					.setOnLongClickListener(new OnLongClickListener() {
						@Override
						public boolean onLongClick(View arg0) {
							// TODO Auto-generated method stub
							String title;
							if (isComing) {
								if (entity.getSenderId() == null)
									return false;
								title = getNameFromDB(entity.getSenderId());
								if (entity.getSenderId() == UserInfo.companyNews_dbId)
									title = "公司动态";
								else if (entity.getSenderId() == UserInfo.feedback_dbId)
									title = "用户反馈";
								else
									title = entity.getChatName();

								String[] groupStr = entity.getSenderId().split(
										"、");
								if (groupStr.length > 1)
									title = "群聊" + "("
											+ Integer.toString(groupStr.length)
											+ "人" + ")";
							} else {
								if (entity.getReceiverId() == null)
									return false;
								title = getNameFromDB(entity.getReceiverId());
								if (entity.getReceiverId() == UserInfo.companyNews_dbId)
									title = "公司动态";
								else if (entity.getReceiverId() == UserInfo.feedback_dbId)
									title = "用户反馈";
								else
									title = entity.getChatName();

								String[] groupStr = entity.getReceiverId()
										.split("、");
								if (groupStr.length > 1)
									title = "群聊" + "("
											+ Integer.toString(groupStr.length)
											+ "人" + ")";
							}

							final ImageView view = (ImageView) arg0
									.findViewById(R.id.voice_anim);

							AlertDialog dialog = new AlertDialog.Builder(ctx)
									.setTitle(title)
									// 对话框标题
									.setItems(
											new String[] { "查看", "删除" },
											new DialogInterface.OnClickListener() {// 每一条的名称
												@SuppressWarnings("deprecation")
												public void onClick(
														DialogInterface dialog,
														int which) {// 响应点击事件
													if (which == 0) {
														String sdStatus = Environment
																.getExternalStorageState();
														if (!sdStatus
																.equals(Environment.MEDIA_MOUNTED)) {
															Toast.makeText(
																	ctx,
																	ctx.getString(R.string.sdcard_error),
																	Toast.LENGTH_SHORT)
																	.show();
															return;
														}

														view.setBackgroundResource(0);

														if (isComing) {
															view.setImageResource(R.drawable.voice_animation_left);
														} else {
															view.setImageResource(R.drawable.voice_animation_right);
														}

														String voicePath = null;
														final AnimationDrawable animationDrawable = (AnimationDrawable) view
																.getDrawable();
														animationDrawable
																.setOneShot(false);
														animationDrawable
																.start();
														if (!isComing) {
															voicePath = coll
																	.get(position)
																	.getVoiceUrl();
														} else {
															String[] str = coll
																	.get(position)
																	.getVoiceUrl()
																	.split("\\/");
															if (str != null
																	&& str.length > 4)
																voicePath = SDCARDPATH
																		+ str[4];
														}
														Log.i("test",
																"voicePath : "
																		+ voicePath);
														try {
															MediaPlayer player = new MediaPlayer();
															try {
																// player.setDataSource(voicePath);
																// player.prepare();

																File file = new File(
																		voicePath);
																FileInputStream fis = new FileInputStream(
																		file);
																player.setDataSource(fis
																		.getFD());
																player.prepare();

																player.start();
																player.setOnCompletionListener(new OnCompletionListener() {
																	public void onCompletion(
																			MediaPlayer mp) {
																		animationDrawable
																				.stop();
																		animationDrawable
																				.setVisible(
																						false,
																						false);
																		if (isComing) {
																			view.setBackgroundDrawable(ctx
																					.getResources()
																					.getDrawable(
																							R.drawable.ico_left_wave_3));
																		} else {
																			view.setBackgroundDrawable(ctx
																					.getResources()
																					.getDrawable(
																							R.drawable.ico_right_wave_3));
																		}
																	}
																});
															} catch (Exception e) {
																e.printStackTrace();
															}
														} catch (IllegalArgumentException e) {
															e.printStackTrace();
														}

													} else {
														if (entity
																.getMessageType() == MessageInfo.COMPANY_NEWS) {
															CompanyNewsFragment.mDataArrays
																	.remove(position);
															notifyDataSetChanged();
														} else if (entity
																.getMessageType() == MessageInfo.USER_FEEDBACK) {
															FeedBackFragment.mDataArrays
																	.remove(position);
															notifyDataSetChanged();
														} else {
															ChatActivity.mDataArrays
																	.remove(position);
															notifyDataSetChanged();
														}
													}
												}
											}).create();
							dialog.show();
							dialog.setCanceledOnTouchOutside(true);

							return false;
						}
					});

			viewHolder.voiceContent.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					String sdStatus = Environment.getExternalStorageState();
					if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
						Toast.makeText(ctx,
								ctx.getString(R.string.sdcard_error),
								Toast.LENGTH_SHORT).show();
						return;
					}

					final ImageView view = (ImageView) v
							.findViewById(R.id.voice_anim);

					view.setBackgroundResource(0);

					if (isComing) {
						view.setImageResource(R.drawable.voice_animation_left);
					} else {
						view.setImageResource(R.drawable.voice_animation_right);
					}

					String voicePath = null;
					final AnimationDrawable animationDrawable = (AnimationDrawable) view
							.getDrawable();
					animationDrawable.setOneShot(false);
					animationDrawable.start();
					if (!isComing) {
						voicePath = coll.get(position).getVoiceUrl();
					} else {
						String[] str = coll.get(position).getVoiceUrl()
								.split("\\/");
						if (str != null && str.length > 4)
							voicePath = SDCARDPATH + str[4];
					}
					Log.i("test", "voicePath : " + voicePath);
					try {
						MediaPlayer player = new MediaPlayer();
						try {
							// player.setDataSource(voicePath);
							// player.prepare();

							File file = new File(voicePath);
							FileInputStream fis = new FileInputStream(file);
							player.setDataSource(fis.getFD());
							player.prepare();

							player.start();
							player.setOnCompletionListener(new OnCompletionListener() {
								public void onCompletion(MediaPlayer mp) {
									animationDrawable.stop();
									animationDrawable.setVisible(false, false);
									if (isComing) {
										view.setBackgroundDrawable(ctx
												.getResources()
												.getDrawable(
														R.drawable.ico_left_wave_3));
									} else {
										view.setBackgroundDrawable(ctx
												.getResources()
												.getDrawable(
														R.drawable.ico_right_wave_3));
									}
								}
							});
						} catch (Exception e) {
							e.printStackTrace();
						}
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					}
				}
			});
		}
		return convertView;
	}

	static class ViewHolder {
		public TextView title;
		public TextView tvSendTime;
		public TextView tvDate;
		public TextView tvContent;
		public TextView tvState;
		public TextView scheduleTitle;
		public TextView scheduleBeginTime;
		public TextView scheduleEndTime;
		public TextView scheduleAddress;
		public ImageView ivPicture;
		public ImageView voiceAnim;
		public TextView chatSec;
		public RelativeLayout voiceContent;
		public boolean isComing = true;
		public RelativeLayout ll_progressBar;
		public RelativeLayout rl_state;
		public LinearLayout ll_beginTime;
		public LinearLayout ll_endTime;
		public LinearLayout ll_address;
		public LinearLayout ll_schedule_content;
		public LinearLayout ll_tv_content;
		public LinearLayout ll_tv_show;
		public ImageView photo;
		public TextView tv_check;
		public WebView web_info;
		// public ListView pic_txt_list;
	}

	public String getNameFromDB(String dbId) {
		dbOpenHelper = new DBOpenHelper(ctx);
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		String name = "";
		Cursor cursor = db.rawQuery("select * from contactsTable where dbid=?",
				new String[] { dbId });
		if (cursor.moveToFirst()) {
			name = cursor.getString(2);
		}
		dbOpenHelper.close();
		cursor.close();
		return name;
	}

	public String getHeadUrlFromDB(String dbId) {
		if (dbId == null)
			return "";
		dbOpenHelper = new DBOpenHelper(ctx);
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

	public String getSexFromDB(String dbId) {
		if (dbId == null)
			return "";
		dbOpenHelper = new DBOpenHelper(ctx);
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

	private String getScheduleDate(String date) {
		if (date == null)
			return null;
		String[] resultDate = date.split(" ");
		String noon = "";
		String[] time;
		if (resultDate != null && resultDate.length > 1) {
			time = resultDate[1].split(":");
			if (time != null && time.length > 1) {
				if (Integer.valueOf(time[0]) < 12) {
					noon = "上午";
				} else {
					noon = "下午";
					int tempHour = Integer.valueOf(time[0]) - 12;
					if (tempHour < 10) {
						time[0] = "0" + time[0];
					}
				}
			}
			return resultDate[0] + " " + noon + " " + time[0] + ":" + time[1];
		}
		return "";
	}

	public FriendEntity getPersonalProfile(String dbId) {
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		cursor = db.rawQuery("select * from contactsTable where dbid=?",
				new String[] { dbId });
		FriendEntity entity = null;
		if (cursor.moveToFirst()) {
			String id = cursor.getString(cursor.getColumnIndex("userid"));
			String name = cursor.getString(cursor.getColumnIndex("name"));
			String phone = cursor.getString(cursor.getColumnIndex("phone"));
			String mobile = cursor.getString(cursor.getColumnIndex("mobile"));
			String signature = cursor.getString(cursor
					.getColumnIndex("signature"));
			String email = cursor.getString(cursor.getColumnIndex("email"));
			String duty = cursor.getString(cursor.getColumnIndex("duty"));
			String department = cursor.getString(cursor
					.getColumnIndex("department"));
			String headUrl = cursor.getString(cursor.getColumnIndex("headURL"));
			String companyId = cursor.getString(cursor
					.getColumnIndex("companyid"));
			String alpha = cursor.getString(cursor.getColumnIndex("alpha"));
			String team = cursor.getString(cursor.getColumnIndex("team"));
			String sex = cursor.getString(cursor.getColumnIndex("sex"));
			String dbid = cursor.getString(cursor.getColumnIndex("dbid"));
			String pinyin = cursor.getString(cursor.getColumnIndex("pinyin"));
			String searchIndex = cursor.getString(cursor
					.getColumnIndex("searchindex"));

			entity = new FriendEntity(id, name, phone, mobile, email, duty,
					department, headUrl, signature, companyId, alpha, team,
					sex, dbid, pinyin, searchIndex);
		}
		cursor.close();
		dbOpenHelper.close();
		return entity;
	}

	private void sendMessageBroadcast(String msguuid) {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		ChatMsgEntity entity = new ChatMsgEntity();
		entity.setStatus(MessageInfo.SCHEDULE_CHECK);
		entity.setMsguuid(msguuid);
		entity.setSenderId("");
		entity.setReceiverId("");
		bundle.putSerializable("message", entity);
		intent.setAction(MessageInfo.MessageBroadCastName);
		intent.putExtras(bundle);
		Log.e("test", "conserver send messagebroadcast");
		ctx.sendBroadcast(intent);
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

	private void getImage(View convertView, String imgUrl, int id) {
		Log.i("test", "smallimg url :" + imgUrl);
		imageLoader.loadImage(imgUrl, id, convertView);
	}

	private void getVoice(View convertView, String voiceUrl, int id, String sec) {
		voiceLoader.loadVoice(voiceUrl, id, convertView, sec);
	}

	class ImageAsynTask extends AsyncTask<Void, Void, byte[]>

	{

		public String ImageName = "";
		private ImageView view = null;

		public ImageAsynTask(String name, ImageView view) {
			this.ImageName = name;
			this.view = view;
		}

		@Override
		protected byte[] doInBackground(Void... params) {

			return loadImages(this.ImageName);
		}

		protected void onPostExecute(byte[] result) {
			super.onPostExecute(result);
			if (result == null) {
				return;
			}

			InputStream ins = new ByteArrayInputStream(result);
			view.setImageBitmap(centerSquareScaleBitmap(BitmapFactory
					.decodeStream(ins)));
			result = null;
			view = null;
			System.gc();
			try {
				ins.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		public byte[] loadImages(String name) {

			String url = name;
			try {
				URL ul = new URL(url);
				if (ul != null) {
					InputStream in = HttpUtils.getStreamFromURL(url);

					if (in != null) {
						ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
						byte[] buff = new byte[100];
						int rc = 0;
						while ((rc = in.read(buff, 0, 100)) > 0) {
							swapStream.write(buff, 0, rc);
						}
						byte[] in2b = swapStream.toByteArray();
						in.close();
						in = null;
						buff = null;
						swapStream.close();
						swapStream = null;
						System.gc();
						return in2b;

					} else {
						return null;

					}
				}

			} catch (IOException e) {

				e.printStackTrace();
			}

			return null;

		}

		public Bitmap centerSquareScaleBitmap(Bitmap bitmap) {
			if (null == bitmap) {
				return null;
			}

			Bitmap result = bitmap;

			int widthOrg = bitmap.getWidth();
			int heightOrg = bitmap.getHeight();
			int edgeLength = widthOrg - 10;
			if (widthOrg > edgeLength && heightOrg > edgeLength) {
				int longerEdge = (int) (edgeLength
						* Math.max(widthOrg, heightOrg) / Math.min(widthOrg,
						heightOrg));
				int scaledWidth = widthOrg > heightOrg ? longerEdge
						: edgeLength;
				int scaledHeight = widthOrg > heightOrg ? edgeLength
						: longerEdge;
				Bitmap scaledBitmap;

				try {
					scaledBitmap = Bitmap.createScaledBitmap(bitmap,
							scaledWidth, scaledHeight, true);
				} catch (Exception e) {
					return null;
				}

				int xTopLeft = (scaledWidth - edgeLength) / 2;
				int yTopLeft = (scaledHeight - edgeLength) / 2;

				try {
					result = Bitmap.createBitmap(scaledBitmap, xTopLeft,
							yTopLeft, edgeLength, edgeLength);
					scaledBitmap.recycle();
					bitmap.recycle();
				} catch (Exception e) {
					return null;
				}
			}

			return result;
		}
	}

}
