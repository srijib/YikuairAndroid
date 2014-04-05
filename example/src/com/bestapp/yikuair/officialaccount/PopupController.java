package com.bestapp.yikuair.officialaccount;

import java.io.File;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.bestapp.yikuair.R;

public class PopupController {

	private final int REQUEST_CODE_TAKE_PHOTO;
	private final int REQUEST_CODE_CHOOSE_PIC;
	private Fragment activity;
	private PopupWindow popupWindow;
	private Button takePhotoBtn;
	private Button choosePicBtn;
	private Button cancelBtn;

	public PopupController(Fragment activity, int take_photo_code,
			int choose_pic_code) {

		this.activity = activity;
		this.REQUEST_CODE_TAKE_PHOTO = take_photo_code;
		this.REQUEST_CODE_CHOOSE_PIC = choose_pic_code;
	}

	public PopupWindow getPopupWindow() {
		return popupWindow;
	}

	/***
	 * 获取PopupWindow实例
	 */
	public void checkPopupWindow() {
		if (null != popupWindow) {
			popupWindow.dismiss();
			return;
		} else {
			initPopuptWindow();
		}
	}

	@SuppressWarnings("deprecation")
	private void initPopuptWindow() {
		View popupWindow_view = activity.getActivity().getLayoutInflater()
				.inflate(R.layout.pop, null, false);
		popupWindow = new PopupWindow(popupWindow_view,
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, true);
		// popupWindow.setAnimationStyle(R.style.animation_fade);
		popupWindow_view.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (popupWindow != null && popupWindow.isShowing()) {
					popupWindow.dismiss();
					popupWindow = null;
				}
				return false;
			}
		});
		takePhotoBtn = (Button) popupWindow_view.findViewById(R.id.take_photo);
		choosePicBtn = (Button) popupWindow_view.findViewById(R.id.choose_pic);
		cancelBtn = (Button) popupWindow_view.findViewById(R.id.cancel);
		takePhotoBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				popupWindow.dismiss();
				popupWindow = null;
				String state = Environment.getExternalStorageState(); // 获取系统的存储状态
				if (state.equals(Environment.MEDIA_MOUNTED)) { // 被分区,有读和写的权限
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // 设置
																					// intent
					// 的行为为拍照
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri
							.fromFile(new File(Environment
									.getExternalStorageDirectory(), "pic.png")));
					activity.startActivityForResult(intent,
							REQUEST_CODE_TAKE_PHOTO);
				} else {
					Toast.makeText(activity.getActivity(), "SD卡无读写权限",
							Toast.LENGTH_LONG).show();
				}
			}
		});
		choosePicBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				popupWindow.dismiss();
				popupWindow = null;
				Intent intent = new Intent(Intent.ACTION_PICK, null);
				intent.setDataAndType(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
				activity.startActivityForResult(intent, REQUEST_CODE_CHOOSE_PIC);
			}
		});
		cancelBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				popupWindow.dismiss();
				popupWindow = null;
			}
		});
	}
}
