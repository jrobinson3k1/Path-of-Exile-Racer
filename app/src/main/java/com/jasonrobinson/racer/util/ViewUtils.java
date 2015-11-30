package com.jasonrobinson.racer.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.TypedValue;

public class ViewUtils {

    private static ColorStateList sColorStateList;

    private ViewUtils() {
    }

    public static Drawable colorizeDrawable(Context context, Drawable drawable) {
        Drawable.ConstantState state = drawable.getConstantState();
        drawable = DrawableCompat.wrap(state == null ? drawable : state.newDrawable()).mutate();
        DrawableCompat.setTintList(drawable, getColorStateListForIcon(context));

        return drawable;
    }

    public static ColorStateList getColorStateListForIcon(Context context) {
        if (sColorStateList != null) {
            return sColorStateList;
        }

        TypedValue value = new TypedValue();
        if (!context.getTheme().resolveAttribute(android.R.attr.textColorSecondary, value, true)) {
            return null;
        }

        ColorStateList baseColor = context.getResources().getColorStateList(value.resourceId);
        if (!context.getTheme().resolveAttribute(android.support.design.R.attr.colorPrimary, value, true)) {
            return null;
        }

        int colorPrimary = value.data;
        int defaultColor = baseColor.getDefaultColor();

        sColorStateList = new ColorStateList(new int[][]{
                {-android.R.attr.state_enabled},
                {android.R.attr.state_checked},
                {android.R.attr.state_selected},
                {}
        }, new int[]{
                baseColor.getColorForState(new int[]{-android.R.attr.state_enabled}, defaultColor),
                colorPrimary,
                colorPrimary,
                defaultColor
        });

        return sColorStateList;
    }
}
