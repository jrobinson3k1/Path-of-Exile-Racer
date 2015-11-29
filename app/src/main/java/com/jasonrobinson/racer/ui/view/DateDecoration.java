package com.jasonrobinson.racer.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.util.TypedValue;
import android.view.View;

import com.jasonrobinson.racer.R;
import com.jasonrobinson.racer.util.CalendarUtils;
import com.jasonrobinson.racer.util.RawTypeface;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DateDecoration extends RecyclerView.ItemDecoration {

    private Resources mRes;
    private Drawable mDrawable;

    private TextPaint mTextPaint;
    private float mTextOffsetY;
    private float mTextOffsetX;

    private List<String> mDisplayedDates = new ArrayList<>();

    public DateDecoration(Context context) {
        mRes = context.getResources();
        mDrawable = mRes.getDrawable(R.drawable.date_container);

        mTextPaint = new TextPaint();
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setColor(mRes.getColor(android.R.color.black));
        mTextPaint.setTypeface(RawTypeface.obtain(context, R.raw.fontin_bold));
        float size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20f, mRes.getDisplayMetrics());
        mTextPaint.setTextSize(size);

        Rect bounds = new Rect();
        mTextPaint.getTextBounds("A", 0, 1, bounds);

        mTextOffsetY = bounds.height() / 2;
        mTextOffsetX = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18f, mRes.getDisplayMetrics()) / 2;
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int childCount = parent.getChildCount();
        if (childCount == 0) {
            return;
        }

        mDisplayedDates.clear();

        int left = parent.getWidth() - mDrawable.getIntrinsicWidth();
        int right = parent.getWidth();

        View child = parent.getChildAt(0);
        int position = parent.getChildAdapterPosition(child);
        DateProvider dateProvider = (DateProvider) parent.getAdapter();

        Date date = dateProvider.getDateForPosition(position);
        String hoverText = getDateText(date);
        mDisplayedDates.add(hoverText);

        int nextTop = Integer.MAX_VALUE;
        boolean nextTopSet = false;
        for (int i = 1; i < childCount; i++) {
            child = parent.getChildAt(i);
            position = parent.getChildAdapterPosition(child);
            dateProvider = (DateProvider) parent.getAdapter();

            date = dateProvider.getDateForPosition(position);
            String text = getDateText(date);
            if (!mDisplayedDates.contains(text)) {
                mDisplayedDates.add(text);
                int top = child.getTop();
                if (top > 0) {
                    int bottom = top + mDrawable.getIntrinsicHeight();
                    mDrawable.setBounds(left, top, right, bottom);
                    mDrawable.draw(c);

                    c.drawText(text, (left + right) / 2 + mTextOffsetX, (top + bottom) / 2 + mTextOffsetY, mTextPaint);

                    if (!nextTopSet) {
                        nextTop = top;
                        nextTopSet = true;
                    }
                }
            }
        }

        int top = Math.min(0, nextTop - mDrawable.getIntrinsicHeight());
        int bottom = top + mDrawable.getIntrinsicHeight();

        mDrawable.setBounds(left, top, right, bottom);
        mDrawable.draw(c);

        c.drawText(hoverText, (left + right) / 2 + mTextOffsetX, (top + bottom) / 2 + mTextOffsetY, mTextPaint);
    }

    private String getDateText(Date date) {
        if (CalendarUtils.isToday(date)) {
            return mRes.getString(R.string.today);
        } else if (CalendarUtils.isTomorrow(date)) {
            return mRes.getString(R.string.tomorrow);
        } else if (CalendarUtils.isYesterday(date)) {
            return mRes.getString(R.string.yesterday);
        } else {
            return CalendarUtils.getFormattedDate(date);
        }
    }

    public interface DateProvider {
        Date getDateForPosition(int position);
    }
}
