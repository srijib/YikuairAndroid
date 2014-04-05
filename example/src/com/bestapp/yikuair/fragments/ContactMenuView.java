package com.bestapp.yikuair.fragments;

import java.util.ArrayList;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.TranslateAnimation;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import com.bestapp.yikuair.R;
import com.bestapp.yikuair.utils.UserInfo;

public class ContactMenuView extends View {

	private Context mContext;
	private PopupWindow popWindow;
	private View popview;
	public ListView listview;
	public ArrayList<MenuItem> mitems = null;
	RelativeLayout layout;
	private TranslateAnimation myMenuOpen;
	private TranslateAnimation myMenuClose;
	private int menuOpenMillis = 500;
	private int menuCloseMillis = 500;
	private final int MENU_OPEN_ANIM = 1;
	private final int MENU_CLOSE_ANIM = 2;
	private MyHandler myHandler = new MyHandler();
	private boolean isDismissing = false;
	private View imgBtn;

	class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			if (msg == null) {
				return;
			}
			super.handleMessage(msg);
			switch (msg.what) {
			case MENU_OPEN_ANIM:
				startMenuOpenAnimation();
				break;
			case MENU_CLOSE_ANIM:
				if (popWindow != null) {
					popWindow.dismiss();
				}
				isDismissing = false;
				break;
			}
		}
	}

	public ContactMenuView(Context context, View btn) {
		super(context);
		mContext = context;
		imgBtn = btn;
		mitems = new ArrayList<MenuItem>();
		LayoutInflater layoutInflater = (LayoutInflater) (mContext)
				.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);

		popview = layoutInflater.inflate(R.layout.hotalk_menu_view, null);
		listview = (ListView) popview.findViewById(R.id.hotalk_menu_listview);
		layout = (RelativeLayout) popview
				.findViewById(R.id.hotalk_menu_view_layout);
		adapter = new ItemTextListAdapter(mContext);
		layout.setOnClickListener(onclick);
		ViewGroup.LayoutParams params = listview.getLayoutParams();

		listview.setFocusable(true);
		listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listview.setAdapter(adapter);
		listview.setFocusableInTouchMode(true);
		listview.setMinimumHeight(200);
		listview.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (!isDismissing) {
					isDismissing = true;
					if ((keyCode == KeyEvent.KEYCODE_MENU)
							&& (popWindow.isShowing())) {
						close();
					} else if (((keyCode == KeyEvent.KEYCODE_BACK) && (popWindow
							.isShowing()))) {
						close();
					}
				}

				return false;
			}
		});
	}

	public void add(int key, String value) {
		remove(key);
		MenuItem item = new MenuItem(key, value);
		mitems.add(item);
		adapter.notifyDataSetChanged();
	}

	public void remover(int position) {
		if (mitems.size() > position) {
			mitems.remove(position);
		}
	}

	public void remove(int key) {
		MenuItem item = null;
		for (int i = 0; i < mitems.size(); i++) {
			item = mitems.get(i);
			if (item.MenuKey == key) {
				mitems.remove(i);
				break;
			}
		}
	}

	public void clear() {
		mitems.clear();
	}

	ItemTextListAdapter adapter;

	private OnClickListener onclick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.hotalk_menu_view_layout:
				close();
				break;
			default:
				break;
			}
		}
	};

	public void setItems(ArrayList<String> items) {
		mitems.clear();
		if (items != null && items.size() > 0) {
			for (int i = 0; i < items.size(); i++) {
				MenuItem item = new MenuItem(0, items.get(i));
				mitems.add(item);
			}
		}
	}

	public void show(View view, int menuWidth, int menuMarginLeft) {
		try {
			if (popWindow == null) {
				popWindow = new PopupWindow(popview, UserInfo.screenWidth / 2, UserInfo.screenHeight / 2 - 150);
			}
			if (popview != null) {
				if (popWindow.isShowing()) {
					startMenuCloseAnimation();
				} else {
					if (mitems != null && mitems.size() > 0) {

						popWindow.setFocusable(true);
						popWindow.setOutsideTouchable(true);
						popWindow.setBackgroundDrawable(new BitmapDrawable());
						popWindow
								.setOnDismissListener(new PopupWindow.OnDismissListener() {
									public void onDismiss() {
										// TODO Auto-generated method stub
										imgBtn.setBackgroundResource(R.drawable.ico_open);
									}
								});
						popWindow.update();
						menuMarginLeft = (UserInfo.screenWidth - UserInfo.screenWidth / 2 ) / 2 ;
						popWindow.showAsDropDown(view, menuMarginLeft, 3);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			close();
		}
	}

	public boolean getIsShow() {
		if (popWindow != null) {
			return popWindow.isShowing();
		} else
			return false;
	}

	public void close() {
		if (popWindow != null && popWindow.isShowing()) {
			startMenuCloseAnimation();
		}
	}

	private void startMenuOpenAnimation() {

		menuOpenMillis = (mitems.size() * 100) + 100;
		if (menuOpenMillis > 500) {
			menuOpenMillis = 500;
		}
		myMenuOpen = new TranslateAnimation(0f, 0f,
				-(listview.getHeight() + 1), 0f);
		myMenuOpen.setDuration(menuOpenMillis);
		listview.startAnimation(myMenuOpen);
	}

	private void startMenuCloseAnimation() {
		myMenuClose = new TranslateAnimation(0f, 0f, 0f,
				-(listview.getHeight() + 1));
		myMenuClose.setDuration(menuCloseMillis);
		layout.startAnimation(myMenuClose);
		myHandler.sendEmptyMessageDelayed(MENU_CLOSE_ANIM, menuCloseMillis);
	}

	public class ItemTextListAdapter extends SimpleAdapter {

		public ItemTextListAdapter(Context context) {
			super(context, null, 0, null, null);
		}

		@Override
		public int getCount() {
			return mitems.size();
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ItemHolder holder;
			if (convertView == null || convertView.getTag() == null
					|| !(convertView.getTag() instanceof ItemHolder)) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.hotalk_menu_item_view, null, true);
				holder = new ItemHolder();
				holder.menuName = (TextView) convertView
						.findViewById(R.id.textview);
			} else {
				holder = (ItemHolder) convertView
						.getTag(R.layout.hotalk_menu_item_view);
			}
			convertView.setTag(holder);
			convertView.setTag(R.layout.hotalk_menu_item_view, holder);
			MenuItem item = mitems.get(position);
			holder.menuName.setText(item.MenuValue);
			convertView.setTag(item.MenuKey);
			return convertView;
		}
	}

	public static class ItemHolder {
		TextView menuName;
	}

	public class MenuItem implements Comparable {
		int MenuKey;

		String MenuValue;

		public MenuItem(int key, String value) {
			MenuKey = key;
			MenuValue = value;
		}

		@Override
		public int compareTo(Object o) {
			return this.MenuKey - ((MenuItem) o).MenuKey;
		}
	}
}
