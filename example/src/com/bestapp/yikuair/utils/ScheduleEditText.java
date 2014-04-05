package com.bestapp.yikuair.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;
import com.bestapp.yikuair.R;

public class ScheduleEditText extends EditText {
	private String textContent;

	public ScheduleEditText(Context context) {
		super(context);
	}

	public ScheduleEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ScheduleEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Paint paint = new Paint();
		// String familiyName="宋体";
		// Typeface font = Typeface.create(familiyName, Typeface.BOLD);
		paint.setAntiAlias(true);
		paint.setTextSize(30);
		paint.setColor(getResources().getColor(R.color.schedule_font_color));
		paint.setTypeface(Typeface.DEFAULT);
		if (this.getTag().equals("task"))
			textContent = getResources().getString(R.string.schedule_title);
		else if (this.getTag().equals("meeting"))
			textContent = getResources().getString(
					R.string.schedule_meeting_title);
		else if (this.getTag().equals("other"))
			textContent = getResources().getString(
					R.string.schedule_other_title);
		else if (this.getTag().equals("begintime"))
			textContent = getResources().getString(R.string.schedule_begintime);
		else if (this.getTag().equals("endtime"))
			textContent = getResources().getString(R.string.schedule_endtime);
		else if (this.getTag().equals("meetingAddress"))
			textContent = getResources().getString(
					R.string.schedule_meeting_address);
		else if (this.getTag().equals("otherAddress"))
			textContent = getResources().getString(
					R.string.schedule_other_address);
		else if (this.getTag().equals("groupname"))
			textContent = getResources().getString(R.string.groupname);
		else if (this.getTag().equals("nickname"))
			textContent = getResources().getString(R.string.nickname);
		else if (this.getTag().equals("signature"))
			textContent = getResources().getString(R.string.personal_signature);
		canvas.drawText(textContent, 13, getHeight() / 2 + 11, paint);
		super.onDraw(canvas);
	}
}