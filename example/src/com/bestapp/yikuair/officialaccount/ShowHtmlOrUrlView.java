package com.bestapp.yikuair.officialaccount;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;

import com.bestapp.yikuair.R;

public class ShowHtmlOrUrlView {

	private PopupWindow htmlWindow;
	private Context context;

	public ShowHtmlOrUrlView(Context context) {
		this.context = context;
	}

	public void ShowWindow(PicTextList picText) {

		View html_view = ((Activity) context).getLayoutInflater().inflate(
				R.layout.view_pop_html, null);
		ImageView back = (ImageView) html_view.findViewById(R.id.html_back);
		WebView content = (WebView) html_view.findViewById(R.id.html_content);
		String detail = picText.getDetail();
		String url = picText.getUrl();
		if (detail != null && !detail.trim().equals("")) {
			try {
				content.loadData(URLEncoder.encode(detail, "utf-8"),
						"text/html", "utf-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
			}
		} else if (url != null && !url.trim().equals("")) {
			content.loadUrl(url);
		}
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (htmlWindow != null && htmlWindow.isShowing()) {
					htmlWindow.dismiss();
					htmlWindow = null;
					System.gc();
				}
			}
		});
		htmlWindow = new PopupWindow(html_view, LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT, true);
		htmlWindow.showAtLocation(html_view, Gravity.NO_GRAVITY, 0, 0);

	}
}
