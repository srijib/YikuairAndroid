package com.bestapp.yikuair.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bestapp.yikuair.R;


public class ImgButton extends LinearLayout {

	private ImageView iv;
	private TextView tv;

	public ImgButton(Context context) {
		this(context, null);
	}

	public ImgButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// 导入布局
		LayoutInflater.from(context).inflate(R.layout.customed_imagebutton, this, true);
		iv = (ImageView) findViewById(R.id.iv);
		tv = (TextView) findViewById(R.id.tv);

	}

	/**
	 * 设置图片资源
	 */
	public void setImageResource(int resId) {
		iv.setImageResource(resId);
	}

	/**
	 * 设置显示的文字
	 */
	public void setTextViewText(String text) {
		tv.setText(text);
	}

}