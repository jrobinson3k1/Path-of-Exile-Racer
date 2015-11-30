package com.jasonrobinson.racer.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jasonrobinson.racer.R;
import com.jasonrobinson.racer.model.Race;
import com.jasonrobinson.racer.ui.view.DateDecoration;
import com.jasonrobinson.racer.util.RawTypeface;
import com.jasonrobinson.racer.util.TimeUtils;
import com.jasonrobinson.racer.util.ViewUtils;
import com.metova.slim.Slim;
import com.metova.slim.annotation.Layout;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

@Layout(R.layout.item_race)
public class RacesAdapter extends RecyclerView.Adapter<RacesAdapter.ViewHolder> implements DateDecoration.DateProvider {

    private static DateFormat sTimeFormat;

    private Context mContext;
    private int mDefaultTextColor;
    private List<Race> mRaces = new ArrayList<>();

    public RacesAdapter(Context context) {
        mContext = context;
        sTimeFormat = android.text.format.DateFormat.getTimeFormat(context);

        TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{android.R.attr.textColorSecondary});
        mDefaultTextColor = a.getColor(0, Color.BLACK);
        a.recycle();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = Slim.createLayout(parent.getContext(), this, parent);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Race race = mRaces.get(position);
        holder.mTitleTextView.setText(race.getId());
        holder.mDurationTextView.setText(TimeUtils.formatDuration(mContext, race.getStartAt(), race.getEndAt()));
        holder.mTimeTextView.setText(sTimeFormat.format(race.getStartAt()));

        updateColorForStatus(holder.mTimeTextView, race);

        holder.mAddAlarmImageButton.setSelected(false);
        holder.mToggleFavoriteImageButton.setSelected(false);

        holder.mAddAlarmImageButton.setOnClickListener(v -> v.setSelected(!v.isSelected()));
        holder.mToggleFavoriteImageButton.setOnClickListener(v -> v.setSelected(!v.isSelected()));
    }

    @Override
    public int getItemCount() {
        return mRaces.size();
    }

    @Override
    public Date getDateForPosition(int position) {
        return mRaces.get(position).getStartAt();
    }

    public void clearAll() {
        mRaces.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Race> races) {
        mRaces.addAll(races);
        notifyDataSetChanged();
    }

    private void updateColorForStatus(TextView textView, Race race) {
        if (race.isInProgress()) {
            textView.setTextColor(mContext.getResources().getColor(R.color.green));
        } else if (race.isFinished()) {
            textView.setTextColor(mContext.getResources().getColor(R.color.red));
        } else if (race.isRegistrationOpen()) {
            textView.setTextColor(mContext.getResources().getColor(R.color.orange));
        } else {
            textView.setTextColor(mDefaultTextColor);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.title)
        TextView mTitleTextView;
        @Bind(R.id.time)
        TextView mTimeTextView;
        @Bind(R.id.duration)
        TextView mDurationTextView;
        @Bind(R.id.add_alarm)
        ImageButton mAddAlarmImageButton;
        @Bind(R.id.toggle_favorite)
        ImageButton mToggleFavoriteImageButton;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            Context context = itemView.getContext();

            mTitleTextView.setTypeface(RawTypeface.obtain(context, R.raw.fontin_bold));
            mDurationTextView.setTypeface(RawTypeface.obtain(context, R.raw.fontin_regular));

            mAddAlarmImageButton.setImageDrawable(ViewUtils.colorizeDrawable(context, mAddAlarmImageButton.getDrawable()));
            mToggleFavoriteImageButton.setImageDrawable(ViewUtils.colorizeDrawable(context, mToggleFavoriteImageButton.getDrawable()));
        }
    }
}
