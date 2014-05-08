package com.bestapp.yikuair.officialaccount;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.bestapp.yikuair.utils.AccountInfomation;
//import com.bestapp.yikuair.utils.Client;
import com.bestapp.yikuair.utils.DBlog;
import com.bestapp.yikuair.utils.UserInfo;
import com.loopj.android.http.AsyncHttpResponseHandler;


public abstract class MyPhotoManager implements PhotoManager {
	public static final String strKey = "MDKO5Tv8eO36jXtElcIuZ99s";
	private int TYPE = 0;
	private Context mContext;
	private double lan = -1;
	private double lon = -1;
	private BackInfo iBackInfo;

	public MyPhotoManager(Context context, BackInfo backInfo) {
		mContext = context;
		iBackInfo = backInfo;
	}

//	@Override
//	public void uploadingPhoto(final Bitmap bitmap) {
//
//		new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//
//				// ByteArrayOutputStream baos = new ByteArrayOutputStream();
//				// bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//				try {
//					Client.test(imageZoom(bitmap));
//				} catch (ClientProtocolException e) {
//					e.printStackTrace();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}).start();
//
//	}

	private byte[] imageZoom(Bitmap bitMap) {
		double maxSize = 400.00;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitMap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		byte[] b = baos.toByteArray();
		double mid = b.length / 1024;
		if (mid > maxSize) {
			double i = mid / maxSize;
			bitMap = zoomImage(bitMap, bitMap.getWidth() / Math.sqrt(i),
					bitMap.getHeight() / Math.sqrt(i));
		}
		try {
			baos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		bitMap.compress(Bitmap.CompressFormat.JPEG, 100, bs);
		return bs.toByteArray();
	}

	public static Bitmap zoomImage(Bitmap bgimage, double newWidth,
			double newHeight) {
		float width = bgimage.getWidth();
		float height = bgimage.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
				(int) height, matrix, true);
		return bitmap;
	}

	@Override
	public void uploadingLikeFriend(AccountInfomation friendInfo) {

	}

	private ArrayList<AccountInfomation> getAroundJson(String content) {
		JSONObject object;
		ArrayList<AccountInfomation> list = new ArrayList<AccountInfomation>();
		try {
			object = new JSONObject(content);

			if (object.has("message")) {
				if (object.getString("message").trim().equals("success")) {
					JSONArray array = object.getJSONArray("data");
					for (int i = 0; i < array.length(); i++) {
						AccountInfomation accountInfomation = new AccountInfomation();
						JSONObject object2 = array.getJSONObject(i);
						String id = object2.getString("id");
						String _lon = object2.getString("lon");
						String _lan = object2.getString("lan");
						String headurl = object2.getString("headurl");
						String nickname = object2.getString("nickname");
						String sex = object2.getString("sex");
						BigDecimal b = new BigDecimal(getDistatce(lan,
								Double.parseDouble(_lan), lon,
								Double.parseDouble(_lon)));
						double distance = b.setScale(1,
								BigDecimal.ROUND_HALF_UP).doubleValue();
						String distanceString = "";
						if (distance == 0) {
							distanceString = "附近";
						} else {
							distanceString = distance + "公里";
						}
						String uploadTime = getShowTime(object2
								.getString("uploadTime"));
						accountInfomation.setId(id);
						accountInfomation.setHeadurl(headurl);
						accountInfomation.setTime(uploadTime);
						accountInfomation.setDistance(distanceString);
						accountInfomation.setNickname(nickname);
						accountInfomation.setSex(sex);
						list.add(accountInfomation);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}

	private boolean getLikeOrNotJson(String content) {
		JSONObject object;
		try {
			object = new JSONObject(content);
			if (object.has("message")) {
				if (object.getString("message").trim().equals("success")) {
					if (object.getString("data") == null
							|| "0".equals(object.getString("data"))) {
						return false;
					} else {
						return true;
					}
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

//	private boolean upLoadingLoactionBackJson(String content) {
//		try {
//
//			JSONObject object = new JSONObject(content);
//			if (object.has("message")) {
//				if (object.getString("message").trim().equals("success")) {
//					Client.loadingPeople(lon + "", lan + "", ResponseHandler);
//					TYPE = 3;
//					return true;
//				}
//			}
//
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		return false;
//	}

	private String getMyHeadJson(String content) {

		String headurl = null;
		try {
			JSONObject object = new JSONObject(content);
			if (object.has("message")) {
				if (object.getString("message").trim().equals("success")) {
					JSONArray jsonArray = object.getJSONArray("data");
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject object1 = jsonArray.optJSONObject(i);
						if (object1.has("headurl")) {
							UserInfo.nick_url = object1.getString("headurl");
						}
						if (object1.has("nickname")) {
							UserInfo.nick_name = object1.getString("nickname");
							DBlog.e("NICKNAME", UserInfo.nick_name);
						}
					}

				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return headurl;
	}

	private boolean like;

//	public void sendLikeOrNot(boolean tag, String id) {
//		String like_type;
//		like = tag;
//		if (tag) {
//			like_type = "1";
//		} else {
//			like_type = "0";
//		}
//		TYPE = 4;
//		Client.likeOrNot(id, like_type, ResponseHandler);
//
//	}

	public String getDistatce(double lat1, double lat2, double lon1, double lon2) {
		double R = 6371;
		double distance = 0.0;
		double dLat = (lat2 - lat1) * Math.PI / 180;
		double dLon = (lon2 - lon1) * Math.PI / 180;
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(lat1 * Math.PI / 180)
				* Math.cos(lat2 * Math.PI / 180) * Math.sin(dLon / 2)
				* Math.sin(dLon / 2);
		distance = (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))) * R;

		return distance + "";
	}

//	@Override
//	public void loadingMyPhotoUrl() {
//		TYPE = 1;
//		Client.getPhoto(ResponseHandler);
//	}

	LocationClient locationClient;

//	@Override
//	public void uploadingLocation() {
//
//		locationClient = new LocationClient(mContext);
//		// 设置定位条件
//		LocationClientOption option = new LocationClientOption();
//		option.setOpenGps(true); // 是否打开GPS
//		option.setCoorType("bd09ll"); // 设置返回值的坐标类型。
//		option.setPriority(LocationClientOption.NetWorkFirst); // 设置定位优先级
//		option.setProdName("LocationDemo"); // 设置产品线名称。强烈建议您使用自定义的产品线名称，方便我们以后为您提供更高效准确的定位服务。
//		option.setScanSpan(1000); // 设置定时定位的时间间隔。单位毫秒
//		locationClient.setLocOption(option);
//		locationClient.start();
//		locationClient.requestLocation();
//		// 注册位置监听器
//		locationClient.registerLocationListener(new BDLocationListener() {
//
//			@Override
//			public void onReceiveLocation(BDLocation location) {
//				// TODO Auto-generated method stub
//				if (location == null) {
//					return;
//				}
//
//				if (TYPE != 2) {
//					TYPE = 2;
//					if (locationClient != null && locationClient.isStarted()) {
//						locationClient.stop();
//						locationClient = null;
//					}
//					lon = location.getLongitude();
//					lan = location.getLatitude();
//					UserInfo.lon = lon;
//					UserInfo.lan = lan;
//					Client.upLoadingLocation(lon + "", lan + "",
//							ResponseHandler);
//				}
//			}
//
//			@Override
//			public void onReceivePoi(BDLocation location) {
//			}
//
//		});
//	}

	final AsyncHttpResponseHandler ResponseHandler = new AsyncHttpResponseHandler() {
		@Override
		public void onSuccess(int arg0, String arg1) {
			DBlog.e("onSuccess", arg1);
			switch (TYPE) {
			case 1:
				String MyheadUrl = getMyHeadJson(arg1);
				iBackInfo.fillPhotoSeek(MyheadUrl);
				break;

			case 2:
//				upLoadingLoactionBackJson(arg1);
				break;
			case 3:
				iBackInfo.fillContentToPeopleList(getAroundJson(arg1));
				break;
			case 4:
				// like or not
				iBackInfo.likeMe(getLikeOrNotJson(arg1), like);
				break;
			}
		};

		@Override
		public void onFailure(Throwable arg0, String arg1) {
			DBlog.e("onFailure", arg1);
		};
	};

	@SuppressLint("SimpleDateFormat")
	private String getShowTime(String time) {
		if (time.equals(""))
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
		StringBuffer timer = new StringBuffer();
		if (day != 0) {
			timer.append(day + "天");
			timer.append("前");
			return timer.toString();
		}
		if (hour != 0) {
			timer.append(hour + "小时");
			timer.append("前");
			return timer.toString();
		}
		if (min != 0) {
			timer.append(min + "分钟");
		}
		if (timer.toString().equals("")) {
			return "当前";
		}
		timer.append("前");
		return timer.toString();
	}

	public interface BackInfo {

		public void fillContentToPeopleList(ArrayList<AccountInfomation> list);

		public void likeMe(boolean arg0, boolean arg1);

		public void fillPhotoSeek(String url);
	}

	@Override
	public void loadingMyPhotoUrl() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void uploadingLocation() {
		// TODO Auto-generated method stub
		
	}

}
