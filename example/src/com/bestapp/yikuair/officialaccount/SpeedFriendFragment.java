//package com.bestapp.yikuair.officialaccount;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Timer;
//import java.util.TimerTask;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.graphics.Bitmap;
//import android.graphics.drawable.BitmapDrawable;
//import android.graphics.drawable.ClipDrawable;
//import android.graphics.drawable.Drawable;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Environment;
//import android.os.Handler;
//import android.os.Message;
//import android.support.v4.app.Fragment;
//import android.util.DisplayMetrics;
//import android.util.Log;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.view.animation.Animation;
//import android.view.animation.Animation.AnimationListener;
//import android.view.animation.AnimationUtils;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.FrameLayout;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.LinearLayout.LayoutParams;
//import android.widget.TextView;
//import android.widget.ViewFlipper;
//
//import com.bestapp.yikuair.R;
//import com.bestapp.yikuair.fragments.ChatMsgEntity;
//import com.bestapp.yikuair.officialaccount.MyPhotoManager.BackInfo;
//import com.bestapp.yikuair.utils.AccountInfomation;
//import com.bestapp.yikuair.utils.AsyncImageLoader;
//import com.bestapp.yikuair.utils.Client;
//import com.bestapp.yikuair.utils.CustomToast;
//import com.bestapp.yikuair.utils.DBlog;
//import com.bestapp.yikuair.utils.MessageInfo;
//import com.bestapp.yikuair.utils.SharedPreferencesUtil;
//import com.bestapp.yikuair.utils.UserInfo;
//import com.loopj.android.http.AsyncHttpResponseHandler;
////
////import com.bestapp.yikuair.R;
////import com.bestapp.yikuair.officialaccount.MyPhotoManager.BackInfo;
////import com.bestapp.yikuair.utils.AccountInfomation;
////import com.bestapp.yikuair.utils.AsyncImageLoader;
//
//public class SpeedFriendFragment extends Fragment implements OnClickListener,
//		BackInfo, AnimationListener {
//
//	private FrameLayout frameLayout;
//	private ViewFlipper vf;
//	private Button like;
//	private Button not_like;
//	private View message;
//	private TextView friend_distance;
//	private TextView friend_time;
//	private TextView friend_nick_name;
//	private TextView friend_info_num;
//	private View titleView;
//	private LayoutInflater mInflater;
//	private final int REQUEST_CODE_ZOOM_PHOTO = 1;
//	private final int REQUEST_CODE_TAKE_PHOTO = 2;
//	private final int REQUEST_CODE_CHOOSE_PIC = 3;
//	private final int REQUEST_LIKE_BACK = 4;
//	private ImageView resultImg;
//	private Bitmap photo;
//	private int width;
//	private int height;
//	private SharedPreferencesUtil chatSharedPre;
//	private ImageView photoView;
//	private ImageView edit;
//	public MyPhotoManager mPhotoManager;
//	private View DataView;// 交友引导页
//	private View HeadView;// 头像编辑页面
//	private View SeekView;// 搜索朋友页面
//	private View ShowFriendView;// 朋友的展示页面
//	public ImageView[] list_img = new ImageView[10];
//	private Handler handler;
//	private ArrayList<AccountInfomation> friendList = new ArrayList<AccountInfomation>();
//	private ArrayList<AccountInfomation> list = new ArrayList<AccountInfomation>();
//	private FriendChatBroadcastReceiver fcb = new FriendChatBroadcastReceiver();
//	public AsyncImageLoader mAsyncImageLoader;
//	private int Friend_Type = 3;
//	public static SpeedFriendFragment instance;
//	private String getMessageKey;
//	private List<ChatMsgEntity> lstMessage;
//	private ImageView newfriend;
//
//	private void getHeightAndWidth() {
//		DisplayMetrics metrics = getResources().getDisplayMetrics();
//		width = metrics.widthPixels;
//		height = metrics.heightPixels;
//	}
//
//	private void initImageList(FrameLayout view) {
//		for (int i = 0; i < list_img.length; i++) {
//
//			ImageView img = (ImageView) mInflater.inflate(
//					R.layout.friend_photo, null);
//			list_img[i] = img;
//			img.setPadding(2, 2, 2, 0);
//			view.addView(list_img[i]);
//
//		}
//	}
//
//	@Override
//	public void onDestroy() {
//		if (photo != null && !photo.isRecycled()) {
//			photo.recycle();
//			photo = null;
//			System.gc();
//
//		}
//		super.onDestroy();
//	};
//
//	@Override
//	public void onPause() {
//		unregisterBroadcast();
//		super.onPause();
//	};
//
//	@Override
//	public void onResume() {
//		registerBroadcast();
//		if (MessageInfo.matchMessageEntityList.size() > 0) {
//			friend_info_num.setText(MessageInfo.matchMessageEntityList.size()
//					+ "");
//			friend_info_num.setVisibility(View.VISIBLE);
//		} else {
//			friend_info_num.setVisibility(View.GONE);
//		}
//		super.onResume();
//	};
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		mInflater = inflater;
//		getHeightAndWidth();
//		instance = this;
//		return inflater.inflate(R.layout.activity_speed_friend, null);
//	}
//
//	final AsyncHttpResponseHandler ResponseHandler = new AsyncHttpResponseHandler() {
//		@Override
//		public void onSuccess(int arg0, String arg1) {
//			DBlog.e("onSuccess", arg1);
//			// loading friends
//			if (getFriendJson(arg1)) {
//				newfriend.setVisibility(View.VISIBLE);
//			} else {
//				newfriend.setVisibility(View.GONE);
//			}
//		}
//	};
//
//	private boolean getFriendJson(String content) {
//		JSONObject object;
//
//		try {
//			object = new JSONObject(content);
//
//			if (object.has("message")) {
//				if (object.getString("message").trim().equals("success")) {
//					JSONArray array = object.getJSONArray("data");
//					for (int i = 0; i < array.length(); i++) {
//						ChatMsgEntity chat = new ChatMsgEntity();
//						JSONObject object2 = (JSONObject) array.opt(i);
//						String id = object2.getString("id");
//						chat.setUserId(id);
//						if (lstMessage == null) {
//							return true;
//						}
//						if (lstMessage != null && !lstMessage.contains(chat)) {
//							return true;
//						}
//
//					}
//				}
//			}
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		return false;
//
//	}
//
//	@Override
//	public void onActivityCreated(Bundle savedInstanceState) {
//		super.onActivityCreated(savedInstanceState);
//		mPhotoManager = new MyPhotoManager(getActivity(), this);
//		handler = new Handler();
//		mAsyncImageLoader = new AsyncImageLoader(getActivity());
//
//		initMainView();
//		getMessageKey = UserInfo.db_id + "_friend_list";
//		chatSharedPre = new SharedPreferencesUtil(getActivity());
//		lstMessage = chatSharedPre.readDataFromShared(getMessageKey);
//		new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				Client.loadingMatchFriend(ResponseHandler);
//			}
//		}).start();
//
//	}
//
//	private void initMainView() {
//		friend_info_num = (TextView) getActivity().findViewById(
//				R.id.friend_info_num);
//		find_sex_title = (TextView) getActivity().findViewById(
//				R.id.speed_friend_title);
//		titleView = getActivity().findViewById(R.id.head_title);
//		titleView.setVisibility(View.GONE);
//		newfriend = (ImageView) getActivity().findViewById(R.id.new_friend);
//		vf = (ViewFlipper) getActivity().findViewById(R.id.friend_flipper);
//		message = (View) getActivity().findViewById(R.id.speed_friend_message);
//		message.setOnClickListener(this);
//		if (UserInfo.nick_name == null || "".equals(UserInfo.nick_name)) {
//			initViewDataFragment();
//		} else {
//			initFinddingView();
//		}
//		if (MessageInfo.matchMessageEntityList.size() > 0) {
//			friend_info_num.setText(MessageInfo.matchMessageEntityList.size()
//					+ "");
//			friend_info_num.setVisibility(View.VISIBLE);
//		} else {
//			friend_info_num.setVisibility(View.GONE);
//		}
//		select_control = (ImageView) getActivity().findViewById(
//				R.id.select_control);
//		select_context = getActivity().findViewById(R.id.select_context);
//		select_m = (Button) getActivity().findViewById(R.id.select_man);
//		select_m.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				v.setBackgroundResource(R.drawable.ima_friend_set_m_pressed);
//				find_sex_title.setText(R.string.seek_friend_m);
//				select_f.setBackgroundResource(R.drawable.ima_friend_set_f_normal);
//				select_a.setBackgroundResource(R.drawable.ima_friend_set_all_normal);
//				Friend_Type = 0;
//				backFindingView();
//			}
//		});
//		select_f = (Button) getActivity().findViewById(R.id.select_woman);
//		select_f.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				v.setBackgroundResource(R.drawable.ima_friend_set_f_pressed);
//				find_sex_title.setText(R.string.seek_friend_f);
//				select_m.setBackgroundResource(R.drawable.ima_friend_set_m_normal);
//				select_a.setBackgroundResource(R.drawable.ima_friend_set_all_normal);
//				Friend_Type = 1;
//				backFindingView();
//			}
//		});
//		select_a = (Button) getActivity().findViewById(R.id.select_all);
//		select_a.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				v.setBackgroundResource(R.drawable.ima_friend_set_all_pressed);
//				find_sex_title.setText(R.string.seek_friend);
//				select_f.setBackgroundResource(R.drawable.ima_friend_set_f_normal);
//				select_m.setBackgroundResource(R.drawable.ima_friend_set_m_normal);
//				Friend_Type = 3;
//				backFindingView();
//			}
//		});
//		select_control.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				if (select_context.getVisibility() == View.GONE) {
//					select_context.setVisibility(View.VISIBLE);
//					select_control.setImageResource(R.drawable.ico_close);
//					switch (Friend_Type) {
//					case 0:
//						select_m.setBackgroundResource(R.drawable.ima_friend_set_m_pressed);
//						break;
//					case 1:
//						select_f.setBackgroundResource(R.drawable.ima_friend_set_f_pressed);
//						break;
//					case 3:
//						select_a.setBackgroundResource(R.drawable.ima_friend_set_all_pressed);
//						break;
//					}
//				} else {
//					select_context.setVisibility(View.GONE);
//					select_control.setImageResource(R.drawable.ico_open);
//				}
//			}
//		});
//	}
//
//	private void initViewDataFragment() {
//
//		DataView = mInflater.inflate(R.layout.activity_speed_data, null);
//		vf.addView(DataView);
//		ImageView view = (ImageView) DataView
//				.findViewById(R.id.speed_login_img);
//		view.setLayoutParams(new LinearLayout.LayoutParams(
//				(int) (width / 12.0 * 11), (int) (width / 48.0 * 57)));
//		Button startButton = (Button) DataView.findViewById(R.id.speed_login);
//		startButton.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				initViewHeadFragment();
//				vf.showNext();
//				vf.removeView(DataView);
//				DataView = null;
//			}
//		});
//	}
//
//	private EditText edit_nick_name;
//
//	private void initViewHeadFragment() {
//		HeadView = mInflater.inflate(R.layout.activity_head, null);
//		vf.addView(HeadView);
//		titleView.setVisibility(View.GONE);
//		resultImg = (ImageView) HeadView.findViewById(R.id.get_phone);
//		edit_nick_name = (EditText) HeadView.findViewById(R.id.edit_nick_name);
//		TextView head_skip = (TextView) HeadView.findViewById(R.id.head_skip);
//		head_skip.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				gotoFindingView();
//			}
//		});
//		if (UserInfo.nick_name != null && !UserInfo.nick_name.trim().equals("")) {
//			edit_nick_name.setText(UserInfo.nick_name);
//		}
//
//		edit_nick_name.setLayoutParams(new LinearLayout.LayoutParams(
//				(int) (height / 800.0 * 282), LayoutParams.WRAP_CONTENT));
//		resultImg.setLayoutParams(new LinearLayout.LayoutParams(
//				(int) (height / 800.0 * 282), (int) (height / 800.0 * 282)));
//
//		if (drawable != null) {
//			resultImg.setBackgroundDrawable(drawable);
//		} else {
//			resultImg.setBackgroundResource(R.drawable.bt_friend_head_boy);
//		}
//		resultImg.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				startGetPic(v);
//			}
//		});
//	}
//
//	Animation anim;
//	private TextView find_notity;
//	private TextView find_sex_title;
//
//	private void initFinddingView() {
//		mPhotoManager.uploadingLocation();
//		SeekView = mInflater.inflate(R.layout.view_seek_friend, null);
//		find_notity = (TextView) SeekView.findViewById(R.id.find_notify);
//		vf.addView(SeekView);
//		anim = AnimationUtils.loadAnimation(getActivity()
//				.getApplicationContext(), R.anim.rotate);
//
//		getActivity().findViewById(R.id.seek_friend_waitting)
//				.setAnimation(anim);
//		titleView.setVisibility(View.VISIBLE);
//		photoView = (ImageView) SeekView.findViewById(R.id.photo);
//		if (photo != null) {
//			photoView.setBackgroundDrawable(new BitmapDrawable(photo));
//		} else if (UserInfo.nick_url != null) {
//			mAsyncImageLoader.loadBitmap(photoView, UserInfo.nick_url, 1);
//		}
//		frameLayout = (FrameLayout) SeekView.findViewById(R.id.waitting_seek);
//		frameLayout.setLayoutParams(new LinearLayout.LayoutParams(
//				(int) (width / 480.0 * 200), (int) (width / 480.0 * 200)));
//
//	}
//
//	private ImageView select_control;
//	private View select_context;
//	private Button select_f;
//	private Button select_m;
//	private Button select_a;
//
//	@SuppressWarnings("deprecation")
//	private void initShowFriendView() {
//		againEdit = true;
//		titleView.setVisibility(View.VISIBLE);
//
//		// if (ShowFriendView != null) {
//		// edit.setBackgroundDrawable(drawable);
//		// return;
//		// }
//		try {
//			animation_left = AnimationUtils.loadAnimation(getActivity(),
//					R.anim.friend_out_of_left);
//			animation_right = AnimationUtils.loadAnimation(getActivity(),
//					R.anim.friend_out_of_right);
//
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//
//		animation_left.setAnimationListener(this);
//		animation_right.setAnimationListener(this);
//		ShowFriendView = mInflater.inflate(R.layout.view_pop_friend, null);
//
//		ImageView heart = (ImageView) ShowFriendView
//				.findViewById(R.id.show_head);
//
//		clipDrawable = (ClipDrawable) heart.getDrawable();
//		FrameLayout friend_head = (FrameLayout) ShowFriendView
//				.findViewById(R.id.head_pop_friend);
//		initImageList(friend_head);
//		friend_distance = (TextView) ShowFriendView.findViewById(R.id.juli);
//		friend_time = (TextView) ShowFriendView.findViewById(R.id.pop_time);
//		friend_nick_name = (TextView) ShowFriendView
//				.findViewById(R.id.nick_name);
//		crruentId = list_img.length - 1;
//		loadingImageView();
//		if (friendList.size() > 0) {
//			friend_time.setText(friendList.get(0).getTime());
//			friend_nick_name.setText(friendList.get(0).getNickname());
//			friend_distance.setText(friendList.get(0).getDistance());
//		}
//		like = (Button) ShowFriendView.findViewById(R.id.like);
//		not_like = (Button) ShowFriendView.findViewById(R.id.not_like);
//		edit = (ImageView) ShowFriendView.findViewById(R.id.photo_edit);
//		// edit.setLayoutParams(new LinearLayout.LayoutParams(
//		// (int) (height / 800.0 * 90), (int) (height / 800.0 * 90)));
//		edit.setBackgroundDrawable(drawable);
//		edit.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				initViewHeadFragment();
//				vf.showNext();
//			}
//		});
//		like.setOnClickListener(this);
//		not_like.setOnClickListener(this);
//		vf.addView(ShowFriendView);
//
//		handlers = new Handler() {
//			@Override
//			public void handleMessage(Message msg) {
//				if (msg.what == 0x1233) {
//					clipDrawable.setLevel(clipDrawable.getLevel() + 200);
//
//				}
//			}
//		};
//
//		timer = new Timer();
//		timer.schedule(new TimerTask() {
//			@Override
//			public void run() {
//				Message msg = new Message();
//				msg.what = 0x1233;
//				handlers.sendMessage(msg);
//				if (clipDrawable.getLevel() >= 10000) {
//					timer.cancel();
//					handlers = null;
//					timer = null;
//				}
//			}
//		}, 200, 10);
//	}
//
//	Handler handlers;
//	ClipDrawable clipDrawable;
//	Timer timer;
//	boolean againEdit = false;
//
//	private void gotoFindingView() {
//		UserInfo.nick_name = edit_nick_name.getText().toString().trim();
//		if (!UserInfo.nick_name.equals("")
//				&& (photo != null || drawable != null)) {
//
//			if (againEdit) {
//				initShowFriendView();
//			} else {
//				initFinddingView();
//			}
//			vf.showNext();
//			vf.removeView(HeadView);
//			HeadView = null;
//			System.gc();
//			BitmapDrawable bd = (BitmapDrawable) drawable;
//			mPhotoManager.uploadingPhoto(bd.getBitmap());
//		} else {
//			CustomToast.showToast(getActivity(), "昵称或头像不能为空", 1000);
//		}
//	}
//
//	private void startGetPic(View v) {
//		PopupController popupController = new PopupController(this,
//				REQUEST_CODE_TAKE_PHOTO, REQUEST_CODE_CHOOSE_PIC);
//		popupController.checkPopupWindow();
//		popupController.getPopupWindow()
//				.showAtLocation(v, Gravity.BOTTOM, 0, 0);
//	}
//
//	@SuppressWarnings("deprecation")
//	@Override
//	public void onActivityResult(int requestCode, int resultCode, Intent data) {
//		Log.e("onActivityResult", resultCode + "");
//		switch (requestCode) {
//		case REQUEST_CODE_ZOOM_PHOTO:
//			if (data != null && data.getExtras() != null) {
//				Bundle extras = data.getExtras();
//				if (extras != null) {
//					photo = extras.getParcelable("data");
//					if (resultImg != null) {
//						resultImg.setBackgroundDrawable(new BitmapDrawable(
//								photo));
//					}
//					if (edit != null) {
//						edit.setBackgroundDrawable(new BitmapDrawable(photo));
//					}
//					if (SeekView != null && photo != null) {
//						mPhotoManager.uploadingPhoto(photo);
//					} else {
//						drawable = new BitmapDrawable(photo);
//					}
//					File pic = new File(
//							Environment.getExternalStorageDirectory()
//									+ "/pic.png");
//					pic.delete();
//				}
//			}
//			break;
//		case REQUEST_CODE_TAKE_PHOTO:
//			File pic = new File(Environment.getExternalStorageDirectory()
//					+ "/pic.png");
//			startPhotoZoom(Uri.fromFile(pic));
//			break;
//		case REQUEST_CODE_CHOOSE_PIC:
//			if (data != null && data.getData() != null) {
//				startPhotoZoom(data.getData());
//			}
//			break;
//
//		case REQUEST_LIKE_BACK:
//			updataView(true);
//			break;
//		default:
//			break;
//		}
//	}
//
//	private void startPhotoZoom(Uri uri) {
//		Intent intent = new Intent("com.android.camera.action.CROP");
//		intent.setDataAndType(uri, "image/*");
//		intent.putExtra("crop", "true");
//		intent.putExtra("aspectX", 1);
//		intent.putExtra("aspectY", 1);
//		intent.putExtra("outputX", 200);
//		intent.putExtra("outputY", 200);
//		intent.putExtra("scale", true);
//		intent.putExtra("scaleUpIfNeeded", true);
//		intent.putExtra("return-data", true);
//		startActivityForResult(intent, REQUEST_CODE_ZOOM_PHOTO);
//	}
//
//	public void backFindingView() {
//		select_context.setVisibility(View.GONE);
//		select_control.setImageResource(R.drawable.ico_open);
//		if (ShowFriendView != null) {
//			initData();
//			initFinddingView();
//			vf.removeView(ShowFriendView);
//			ShowFriendView = null;
//			vf.showNext();
//		} else if (frameLayout != null) {
//			frameLayout.setClickable(false);
//			frameLayout.findViewById(R.id.seek_friend_waitting).startAnimation(
//					anim);
//			find_notity.setText(R.string.seek);
//			mPhotoManager.uploadingLocation();
//			Log.e("asasasas", "click end");
//		}
//	}
//
//	@Override
//	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.like:
//			if (friendList_crrent_num < 10
//					&& friendList.size() > friendList_crrent_num) {
//				AccountInfomation accountInfomation = friendList
//						.get(friendList_crrent_num);
//				mPhotoManager.sendLikeOrNot(true, accountInfomation.getId());
//
//				DBlog.e("like", friendList.get(friendList_crrent_num).getId()
//						+ ":"
//						+ friendList.get(friendList_crrent_num).getNickname());
//			} else {
//				backFindingView();
//			}
//			break;
//		case R.id.not_like:
//			if (friendList_crrent_num < 10
//					&& friendList.size() > friendList_crrent_num) {
//				AccountInfomation accountInfomation = friendList
//						.get(friendList_crrent_num);
//				mPhotoManager.sendLikeOrNot(false, accountInfomation.getId());
//
//				DBlog.e("not like", friendList.get(friendList_crrent_num)
//						.getId()
//						+ ":"
//						+ friendList.get(friendList_crrent_num).getNickname());
//			} else {
//				backFindingView();
//			}
//			break;
//		case R.id.speed_friend_message:
//			Intent intent = new Intent(getActivity(), PairFriendActivity.class);
//			startActivity(intent);
//			friend_info_num.setVisibility(View.GONE);
//			newfriend.setVisibility(View.GONE);
//			break;
//		case R.id.get_phone:
//			startGetPic(v);
//			break;
//		}
//	}
//
//	public Drawable drawable;
//
//	@Override
//	public void fillContentToPeopleList(ArrayList<AccountInfomation> list) {
//
//		if (Friend_Type != 3) {
//			ArrayList<AccountInfomation> newlist = new ArrayList<AccountInfomation>();
//			for (AccountInfomation accountInfomation : list) {
//				if (accountInfomation.getSex().equals(Friend_Type + "")) {
//					newlist.add(accountInfomation);
//				}
//			}
//			friendList.addAll(newlist);
//		} else {
//			friendList.addAll(list);
//		}
//
//		handler.postDelayed(new Runnable() {
//
//			@Override
//			public void run() {
//				drawable = photoView.getBackground();
//
//				if (friendList.size() > 0) {
//					initShowFriendView();
//					vf.showNext();
//					vf.removeView(SeekView);
//					SeekView = null;
//				} else {
//					frameLayout.findViewById(R.id.seek_friend_waitting)
//							.clearAnimation();
//					find_notity.setText("附近的人已经看完了");
//					frameLayout.setClickable(true);
//					frameLayout.setOnClickListener(new OnClickListener() {
//
//						@Override
//						public void onClick(View v) {
//							v.setClickable(false);
//							v.findViewById(R.id.seek_friend_waitting)
//									.startAnimation(anim);
//							find_notity.setText(R.string.seek);
//							Friend_Type = 3;
//							mPhotoManager.uploadingLocation();
//							Log.e("asasasas", "click end");
//						}
//					});
//				}
//
//			}
//		}, 1500);
//
//	}
//
//	private void updataView(boolean arg) {
//		friendList_crrent_num++;
//		if (10 > friendList_crrent_num
//				&& friendList.size() > friendList_crrent_num) {
//			animFriendView(arg);
//			AccountInfomation accountInfomation = friendList
//					.get(friendList_crrent_num);
//			friend_distance.setText(accountInfomation.getDistance());
//			friend_time.setText(accountInfomation.getTime());
//			friend_nick_name.setText(accountInfomation.getNickname());
//			// if (friendList_crrent_num == LoadNum) {
//			// loadingImageView();
//			// }
//		} else {
//			backFindingView();
//		}
//
//	}
//
//	private int LoadNum = 9;
//	private int LoadFriend = 0;
//	public int crruentId = 0;// 显示到第几张
//	private int friendList_crrent_num = 0;//
//
//	private void initData() {
//		LoadNum = 9;
//		LoadFriend = 0;
//		crruentId = 0;
//		friendList_crrent_num = 0;
//		friendList.clear();
//	}
//
//	private void loadingImageView() {
//
//		for (int i = 0; i < 10; i++) {
//			if (friendList.size() > i) {
//				AccountInfomation accountInfomation = friendList.get(i);
//				mAsyncImageLoader.loadBitmap(list_img[LoadNum],
//						accountInfomation.getHeadurl(), 0);
//
//				DBlog.e("Not_SHOW_VIEW", LoadNum + "");
//				DBlog.e("list  Number", i + LoadFriend * 5 + "");
//				DBlog.e("friendList_crrent_num", friendList_crrent_num + "");
//				LoadNum--;
//			}
//		}
//	}
//
//	Animation animation_left;
//	Animation animation_right;
//
//	private void animFriendView(boolean arg) {
//		if (arg) {
//
//			if (crruentId > 0)
//				list_img[crruentId].startAnimation(animation_right);
//
//		} else {
//			if (crruentId > 0)
//				list_img[crruentId].startAnimation(animation_left);
//
//		}
//	}
//
//	@Override
//	public void fillPhotoSeek(String url) {
//
//	}
//
//	@Override
//	public void onAnimationEnd(Animation animation) {
//		list_img[crruentId].setVisibility(View.GONE);
//		list_img[crruentId].setImageBitmap(null);
//		crruentId--;
//	}
//
//	@Override
//	public void onAnimationRepeat(Animation animation) {
//
//	}
//
//	@Override
//	public void onAnimationStart(Animation animation) {
//
//	}
//
//	@Override
//	public void likeMe(boolean arg, boolean arg1) {
//
//		if (arg && arg1) {
//			Intent intents = new Intent(getActivity(), FriendSuccessView.class);
//			intents.putExtra("info", friendList.get(friendList_crrent_num));
//			startActivityForResult(intents, REQUEST_LIKE_BACK);
//		} else {
//			updataView(arg1);
//		}
//	}
//
//	public class FriendChatBroadcastReceiver extends BroadcastReceiver {
//
//		@Override
//		public void onReceive(Context arg0, Intent arg1) {
//			if (arg1 == null)
//				return;
//			if (friend_info_num != null) {
//
//				if (MessageInfo.matchMessageEntityList.size() > 0) {
//					friend_info_num.setText(MessageInfo.matchMessageEntityList
//							.size() + "");
//					friend_info_num.setVisibility(View.VISIBLE);
//				} else {
//					friend_info_num.setVisibility(View.GONE);
//				}
//			}
//		}
//	};
//
//	private void registerBroadcast() {
//		IntentFilter intentFilter = new IntentFilter();
//		intentFilter.addAction(MessageInfo.FriendMessageBroadCastName);
//		getActivity().registerReceiver(fcb, intentFilter);
//	}
//
//	private void unregisterBroadcast() {
//		getActivity().unregisterReceiver(fcb);
//	}
//
//}
