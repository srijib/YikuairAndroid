package com.bestapp.yikuair.fragments;

import java.util.LinkedList;

import com.bestapp.yikuair.utils.CustomedViewPager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.widget.GridView;

public class CalViewPagerAdapter extends PagerAdapter{
	private LinkedList<GridView> mLists;
	public CalViewPagerAdapter(Context context, LinkedList<GridView> array) {
		this.mLists=array;
	}
	@Override
	public int getCount() {
		return mLists.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		
		return arg0 == arg1;
	}
	@Override
	public Object instantiateItem(View arg0, int arg1)
	{
		try{
			((CustomedViewPager) arg0).addView(mLists.get(arg1));	
		}catch(Exception e){
			e.printStackTrace();
		}		
		return mLists.get(arg1);
	}

	@Override
	public void destroyItem(View arg0, int arg1, Object arg2)
	{
		((CustomedViewPager) arg0).removeView(mLists.get(arg1));
	}

}
