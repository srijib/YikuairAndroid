package com.bestapp.yikuair.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bestapp.yikuair.R;
import com.bestapp.yikuair.officialaccount.PicTextList;
import com.bestapp.yikuair.utils.ImageLoaderOriginal;

public class ItemAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<PicTextList> list;
	private static ImageLoaderOriginal imageLoader = null;

	public ItemAdapter(Context context, ArrayList<PicTextList> list) {

		this.context = context;
		this.list = list;
		imageLoader = new ImageLoaderOriginal(context);
	}

	@Override
	public int getCount() {
		if (list == null) {
			list = new ArrayList<PicTextList>();
		}
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = inflater.inflate(R.layout.pic_txt_item, null);
		TextView txt = (TextView) convertView.findViewById(R.id.item_title);
		getImage(convertView, list.get(position).getImgpath(),
				R.id.item_image_path);
		txt.setText(list.get(position).getTitle());
		return convertView;
	}

	private void getImage(View convertView, String imgUrl, int id) {
		Log.i("test", "smallimg url :" + imgUrl);
		imageLoader.loadImage(imgUrl, id, convertView);
	}
}
