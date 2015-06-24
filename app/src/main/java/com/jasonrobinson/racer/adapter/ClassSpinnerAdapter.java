package com.jasonrobinson.racer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jasonrobinson.racer.R;
import com.jasonrobinson.racer.enumeration.PoEClass;
import com.jasonrobinson.racer.util.RawTypeface;

public class ClassSpinnerAdapter extends BaseAdapter {

    Context mContext;
    PoEClass[] mPoEClasses;
    boolean mShowAll;

    public ClassSpinnerAdapter(Context context, PoEClass[] poEClasses, boolean showAll) {
        mContext = context;
        mPoEClasses = poEClasses;
        mShowAll = showAll;
    }

    @Override
    public int getCount() {
        return mPoEClasses.length + (mShowAll ? 1 : 0);
    }

    @Override
    public PoEClass getItem(int position) {
        if (mShowAll && position == 0) {
            return null;
        }

        return mPoEClasses[mShowAll ? position - 1 : position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent, R.layout.support_simple_spinner_dropdown_item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent, android.R.layout.simple_spinner_item);
    }

    private View getView(int position, View convertView, ViewGroup parent, int layoutResId) {
        View v = convertView;
        TextView textView;
        if (v == null) {
            v = LayoutInflater.from(mContext).inflate(layoutResId, parent, false);
            textView = (TextView) v.findViewById(android.R.id.text1);
            textView.setTypeface(RawTypeface.obtain(mContext, R.raw.fontin_regular));
        } else {
            textView = (TextView) v.findViewById(android.R.id.text1);
        }

        PoEClass poEClass = getItem(position);
        textView.setText(poEClass == null ? mContext.getString(R.string.all) : poEClass.toString());

        return v;
    }
}
