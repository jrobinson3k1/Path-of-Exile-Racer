package com.jasonrobinson.racer.adapter;

import java.math.BigDecimal;
import java.math.MathContext;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jasonrobinson.racer.R;
import com.jasonrobinson.racer.model.Ladder.Entry;
import com.jasonrobinson.racer.model.Ladder.Entry.Character;

public class LadderAdapter extends BaseAdapter {

	private Entry[] mPreviousEntries;
	private Entry[] mEntries;

	private boolean mUseClassRank;
	private boolean mBorderFirstItem;

	public LadderAdapter(Entry[] entries, boolean useClassRank, boolean borderFirstItem) {

		mEntries = entries;
		mUseClassRank = useClassRank;
		mBorderFirstItem = borderFirstItem;
	}

	@Override
	public int getCount() {

		return mEntries.length;
	}

	@Override
	public Entry getItem(int position) {

		return mEntries[position];
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Context context = parent.getContext();
		View view = convertView;
		if (view == null) {
			view = LayoutInflater.from(context).inflate(R.layout.ladder_item, parent, false);
			view.setTag(new ViewHolder(view));
		}

		ViewHolder holder = (ViewHolder) view.getTag();
		holder.bottomBorderView.setVisibility(mBorderFirstItem && position == 0 ? View.VISIBLE : View.INVISIBLE);

		Entry entry = getItem(position);
		Character character = entry.getCharacter();

		holder.rankTextView.setText(Integer.toString(mUseClassRank ? entry.getClassRank() : entry.getRank()));
		holder.nameTextView.setText(character.getName());
		if (entry.isDead()) {
			holder.nameTextView.setPaintFlags(holder.nameTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
		}
		else {
			holder.nameTextView.setPaintFlags(holder.nameTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
		}

		holder.levelTextView.setText(Integer.toString(character.getLevel()));
		holder.classTextView.setText(character.getPoeClass());
		holder.experienceTextView.setText(formatExperience(character.getExperience()));

		applyRankChange(parent.getContext(), view, entry);

		return view;
	}

	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private void applyRankChange(Context context, View v, Entry entry) {

		RankChange rankChange = getRankChange(entry);
		Drawable drawable;
		switch (rankChange) {
			case UP:
				drawable = new ColorDrawable(context.getResources().getColor(R.color.rank_up));
				break;
			case DOWN:
				drawable = new ColorDrawable(context.getResources().getColor(R.color.rank_down));
				break;
			case NONE:
			default:
				drawable = new ColorDrawable(context.getResources().getColor(R.color.rank_none));
				break;
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			v.setBackground(drawable);
		}
		else {
			v.setBackgroundDrawable(drawable);
		}
	}

	private CharSequence formatExperience(long experience) {

		MathContext mc = new MathContext(4);
		if (experience >= 1000000000) { // billion
			return new BigDecimal((double) experience / 1000000000).round(mc).toString() + "B";
		}
		else if (experience >= 1000000) { // million
			return new BigDecimal((double) experience / 1000000).round(mc).toString() + "M";
		}
		else if (experience >= 1000) { // thousand
			return new BigDecimal((double) experience / 1000).round(mc).toString() + "K";
		}

		return Long.toString(experience);
	}

	private RankChange getRankChange(Entry entry) {

		if (mPreviousEntries == null) {
			return RankChange.NONE;
		}

		Entry prevEntry = findPreviousEntry(entry);
		if (prevEntry == null || entry.getRank() > prevEntry.getRank()) {
			return RankChange.UP;
		}
		else if (entry.getRank() < prevEntry.getRank()) {
			return RankChange.DOWN;
		}

		return RankChange.NONE;
	}

	private Entry findPreviousEntry(Entry entry) {

		if (mPreviousEntries == null) {
			return null;
		}

		String character = entry.getCharacter().getName();
		for (Entry prevEntry : mPreviousEntries) {
			String prevCharacter = prevEntry.getCharacter().getName();
			if (character.equals(prevCharacter)) {
				return prevEntry;
			}
		}

		return null;
	}

	public void setEntries(Entry[] entries, boolean useClassRank) {

		mPreviousEntries = mEntries;
		mEntries = entries;
		mUseClassRank = useClassRank;

		notifyDataSetChanged();
	}

	public void setBorderFirstItem(boolean enable) {

		mBorderFirstItem = enable;
	}

	private enum RankChange {
		UP,
		DOWN,
		NONE
	}

	private class ViewHolder {

		public TextView rankTextView;
		public TextView nameTextView;
		public TextView levelTextView;
		public TextView classTextView;
		public TextView experienceTextView;
		public View bottomBorderView;

		public ViewHolder(View v) {

			rankTextView = (TextView) v.findViewById(R.id.rank);
			nameTextView = (TextView) v.findViewById(R.id.name);
			levelTextView = (TextView) v.findViewById(R.id.level);
			classTextView = (TextView) v.findViewById(R.id.poeClass);
			experienceTextView = (TextView) v.findViewById(R.id.experience);
			bottomBorderView = v.findViewById(R.id.bottomBorder);
		}
	}
}
