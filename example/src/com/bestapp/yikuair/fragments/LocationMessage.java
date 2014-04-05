package com.bestapp.yikuair.fragments;


import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.GraphicsOverlay;
import com.baidu.mapapi.map.GroundOverlay;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mapapi.map.MyLocationOverlay.LocationMode;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.bestapp.yikuair.R;
import com.bestapp.yikuair.location.LocationEngine;
import com.bestapp.yikuair.utils.LocationChangeListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class LocationMessage extends Activity implements OnClickListener{

	private MapView mMapView = null;
	private MapController mMapController = null;

	public static final String strKey = "O9L1OLfQzzf3CG13iqiRiNzV";
	private LocationClient mLocClient;
	private LocationData locData;
	double longtitude;
	double latitude;
	private MKSearch mSearch = null;
	public MyLocationListenner myListener = new MyLocationListenner();
	// 定位图层
	MyLocationOverlay myLocationOverlay = null;

	OnCheckedChangeListener radioButtonListener = null;

	private TextView currentLocationTextView = null;
	private ImageButton backButton = null;
	private ImageButton sendButton = null;
	private GeoPoint locationPoint = null;
	private String poiString = null;
	private MKMapViewListener mMapListener = null;
	private Bitmap mapImage = null;
	boolean isRequest = false;//是否手动触发请求定位
	boolean isFirstLoc = true;//是否首次定位
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		BMapManager mBMapManager = new BMapManager(this.getApplicationContext());
		mBMapManager.init(strKey, new LocationEngine.MyGeneralListener());
		setContentView(R.layout.location_message);

		/*
		 * map init
		 */
		mMapView = (MapView) findViewById(R.id.location_message_map);

		mMapController = mMapView.getController();
		mMapView.getController().setZoom(14);
		mMapView.getController().enableClick(true);
		mMapView.setBuiltInZoomControls(false);
		backButton = (ImageButton) findViewById(R.id.location_message_back);
		backButton.setOnClickListener(this);
		sendButton = (ImageButton) findViewById(R.id.location_message_send);
		sendButton.setOnClickListener(this);

		/*
		 * loc init
		 */



		myLocationOverlay = new MyLocationOverlay(mMapView);
		myLocationOverlay.setData(locData);
		myLocationOverlay.setMarker(this.getResources().getDrawable(R.drawable.icon_gcoding));
		mMapView.getOverlays().add(myLocationOverlay);
		mMapView.refresh();
		currentLocationTextView = (TextView) findViewById(R.id.location_message_curlocation);

		Intent intent = getIntent();

		if (intent.hasExtra("location")) {
			double latitude = intent.getDoubleExtra("latitude", 116.484442);
			double longitude = intent.getDoubleExtra("longitude", 39.917007);
			String poi = intent.getStringExtra("location");
			locationPoint = new GeoPoint((int) (latitude * 1e6),
					(int) (longitude * 1e6));
			ItemizedOverlay itemOverlay = new ItemizedOverlay(null, mMapView);
			OverlayItem item = new OverlayItem(locationPoint, "", "");
			item.setMarker(this.getResources().getDrawable(R.drawable.icon_gcoding));
			itemOverlay.addItem(item);
			mMapView.getOverlays().add(itemOverlay);
			mMapView.getController().animateTo(locationPoint);
			mMapView.getController().enableClick(false);
			currentLocationTextView.setText(poi);

		} else {
			mLocClient = new LocationClient(this);
			locData = new LocationData();
			mLocClient.registerLocationListener(myListener);

			LocationClientOption option = new LocationClientOption();
			option.setOpenGps(true);// 打开gps
			option.setCoorType("bd09ll"); // 设置坐标类型
			option.setScanSpan(1000);
			option.setTimeOut(TRIM_MEMORY_COMPLETE);
			mLocClient.setLocOption(option);
			mLocClient.start();
			mSearch = new MKSearch();
			mSearch.init(mBMapManager, new MKSearchListener() {
				@Override
				public void onGetPoiDetailSearchResult(int type, int error) {
				}

				public void onGetAddrResult(MKAddrInfo res, int error) {
					if (error != 0) {
						String str = String.format("错误号：%d", error);
						// Toast.makeText(LocationMessage.this, str,
						// Toast.LENGTH_LONG).show();
						return;
					}
					// 地图移动到该点
					mMapView.getController().animateTo(res.geoPt);

					if (res.type == MKAddrInfo.MK_GEOCODE) {
						// 地理编码：通过地址检索坐标点
						String strInfo = String.format("纬度：%f 经度：%f",
								res.geoPt.getLatitudeE6() / 1e6,
								res.geoPt.getLongitudeE6() / 1e6);
						// Toast.makeText(LocationMessage.this, strInfo,
						// Toast.LENGTH_LONG).show();
						currentLocationTextView.setText("strInfo");
						
						
					}
					if (res.type == MKAddrInfo.MK_REVERSEGEOCODE) {
						// 反地理编码：通过坐标点检索详细地址及周边poi
						poiString = res.strAddr;
						// Toast.makeText(LocationMessage.this, strInfo,
						// Toast.LENGTH_LONG).show();
						currentLocationTextView.setText(poiString);

					}
					// 生成ItemizedOverlay图层用来标注结果点
					// ItemizedOverlay<OverlayItem> itemOverlay = new
					// ItemizedOverlay<OverlayItem>(null, mMapView);
					// //生成Item
					// OverlayItem item = new OverlayItem(res.geoPt, "", null);
					// //为maker定义位置和边界
					// //得到需要标在地图上的资源
					// // Drawable marker =
					// getResources().getDrawable(R.drawable.icon_markf);
					// // marker.setBounds(0, 0, marker.getIntrinsicWidth(),
					// marker.getIntrinsicHeight());
					// //给item设置marker
					// // item.setMarker(marker);
					// //在图层上添加item
					// itemOverlay.addItem(item);
					//
					// //清除地图其他图层
					// // mMapView.getOverlays().clear();
					// //添加一个标注ItemizedOverlay图层
					// mMapView.getOverlays().add(itemOverlay);
					// //执行刷新使生效
					// mMapView.refresh();
				}

				public void onGetPoiResult(MKPoiResult res, int type, int error) {

				}

				public void onGetDrivingRouteResult(MKDrivingRouteResult res,
						int error) {
				}

				public void onGetTransitRouteResult(MKTransitRouteResult res,
						int error) {
				}

				public void onGetWalkingRouteResult(MKWalkingRouteResult res,
						int error) {
				}

				public void onGetBusDetailResult(MKBusLineResult result,
						int iError) {
				}

				@Override
				public void onGetSuggestionResult(MKSuggestionResult res,
						int arg1) {
				}

				@Override
				public void onGetShareUrlResult(MKShareUrlResult result,
						int type, int error) {
					// TODO Auto-generated method stub

				}

			});

			mMapListener = new MKMapViewListener() {
				@Override
				public void onMapMoveFinish() {
					/**
					 * 在此处理地图移动完成回调 缩放，平移等操作完成后，此回调被触发
					 */
				}

				@Override
				public void onClickMapPoi(MapPoi mapPoiInfo) {
					/**
					 * 在此处理底图poi点击事件 显示底图poi名称并移动至该点 设置过：
					 * mMapController.enableClick(true); 时，此回调才能被触发
					 * 
					 */
					String title = "";
					if (mapPoiInfo != null) {
						title = mapPoiInfo.strText;
						mMapController.animateTo(mapPoiInfo.geoPt);
					}
				}

				@Override
				public void onGetCurrentMap(Bitmap b) {
					/**
					 * 当调用过 mMapView.getCurrentMap()后，此回调会被触发 可在此保存截图至存储设备
					 */
					mapImage = b;

				}

				@Override
				public void onMapAnimationFinish() {
					/**
					 * 地图完成带动画的操作（如: animationTo()）后，此回调被触发
					 */
				}

				/**
				 * 在此处理地图载完成事件
				 */
				@Override
				public void onMapLoadFinish() {

				}
			};
			mMapView.regMapViewListener(mBMapManager, mMapListener);
			mMapView.refresh();
		}

	}

	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
//			locData.latitude = location.getLatitude();
//			locData.longitude = location.getLongitude();
//			// 如果不显示定位精度圈，将accuracy赋值为0即可
//			locData.accuracy = location.getRadius();
//			// 此处可以设置 locData的方向信息, 如果定位 SDK
//			// 未返回方向信息，用户可以自己实现罗盘功能添加方向信息。
//			locData.direction = location.getDerect();
//			// 更新定位数据
//			myLocationOverlay.setData(locData);
//			// 更新图层数据执行刷新后生效
//			mMapView.refresh();
//			// 是手动触发请求或首次定位时，移动到定位点
//			// if (isFirstLoc){
//			// 移动地图到定位点
//			Log.d("LocationOverlay", "receive location, animate to it");
//			locationPoint = new GeoPoint((int) (locData.latitude * 1e6),
//					(int) (locData.longitude * 1e6));
//			mMapView.getController().animateTo(locationPoint);
//			mSearch.reverseGeocode(locationPoint);
			
            locData.latitude = location.getLatitude();
            locData.longitude = location.getLongitude();
            //如果不显示定位精度圈，将accuracy赋值为0即可
            locData.accuracy = location.getRadius();
            // 此处可以设置 locData的方向信息, 如果定位 SDK 未返回方向信息，用户可以自己实现罗盘功能添加方向信息。
            locData.direction = location.getDerect();
            double tmpLongtitude = location.getLongitude();
            double tmpLatitude = location.getLatitude();
            longtitude = tmpLongtitude;
            latitude = tmpLatitude;
            //更新定位数据
            myLocationOverlay.setData(locData);
            //更新图层数据执行刷新后生效
            mMapView.refresh();
            //是手动触发请求或首次定位时，移动到定位点

            locationPoint = new GeoPoint((int)(locData.latitude* 1e6), (int)(locData.longitude *  1e6));
            if (isRequest || isFirstLoc){
            	//移动地图到定位点
            	Log.d("LocationOverlay", "receive location, animate to it");
                mMapController.animateTo(locationPoint);
                isRequest = false;
                myLocationOverlay.setLocationMode(LocationMode.FOLLOWING);
//                myLocationOverlay.setData(locData);
                mSearch.reverseGeocode(locationPoint);
            }
            mSearch.reverseGeocode(locationPoint);
            //首次定位完成
            isFirstLoc = false;
            

			// }
			// 首次定位完成
			// isFirstLoc = false;
		}

		public void onReceivePoi(BDLocation poiLocation) {
			Log.e("yikuair", "onReceivePoi");
			if (poiLocation == null) {
				return;
			}
		}
	}

	public class MyLocationMapView extends MapView {
		PopupOverlay pop = null;// 弹出泡泡图层，点击图标使用

		public MyLocationMapView(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

		public MyLocationMapView(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		public MyLocationMapView(Context context, AttributeSet attrs,
				int defStyle) {
			super(context, attrs, defStyle);
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			if (!super.onTouchEvent(event)) {
				// 消隐泡泡
				if (pop != null && event.getAction() == MotionEvent.ACTION_UP)
					pop.hidePop();
			}
			return true;
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.location_message_back:
			Log.e("yikuair", "location message back");

			LocationMessage.this.finish();
			break;
		case R.id.location_message_send:
			/*
			 * send a picture mapImage send a poi poiString
			 */

			Intent intent = new Intent();
			if (poiString == null) {
				Toast.makeText(this, "正在读取位置信息， 请稍候", Toast.LENGTH_LONG);
				break;
			} else {
				intent.putExtra("location", poiString);
				intent.putExtra("longitude", longtitude);
				intent.putExtra("latitude", latitude);
				Log.e("FM", "logitude"+longtitude+";latitude"+latitude);
				setResult(RESULT_OK, intent);
				// mMapView.getCurrentMap();
				LocationMessage.this.finish();
				break;
			}

		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mMapView.destroyDrawingCache();
		mMapView.destroy();
	}

}
