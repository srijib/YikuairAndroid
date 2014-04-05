package com.bestapp.yikuair.fragments;

import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bestapp.yikuair.R;

public class gridViewAdapter extends BaseAdapter {
	private Context mContext;
	private List<gridItemInfo> lstMenuItem = null;	
    private LayoutInflater mInflater;
	
	public gridViewAdapter(Context context, List<gridItemInfo> list) {
		mContext = context;
		lstMenuItem = list;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {	
		return lstMenuItem.size();
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
		//Log.i(TAG,"position " + position + " " + convertView + " " + parent.getChildCount());
		/*if(position == 0 && convertView != null)
			return convertView;*/
		ViewHolder holder = new ViewHolder();
		convertView = mInflater.inflate(R.layout.menu_item, null);
		holder.menuItemImage = (ImageView) convertView.findViewById(R.id.grid_item_pic);
		holder.menuItemName = (TextView) convertView.findViewById(R.id.grid_item_name);
		holder.menuItemName.setText(lstMenuItem.get(position).getMenuName());				
		holder.menuItemImage.setImageResource(lstMenuItem.get(position).getMenuImg());
		return convertView;
	}
	
	public static final class ViewHolder {
		public ImageView menuItemImage;
		public TextView menuItemName;
	}
}
