package com.jasonrobinson.racer.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jasonrobinson.racer.R;
import com.jasonrobinson.racer.model.Race;
import com.jasonrobinson.racer.ui.view.DateDecoration;
import com.jasonrobinson.racer.util.RawTypeface;
import com.metova.slim.Slim;
import com.metova.slim.annotation.Layout;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

@Layout(R.layout.item_race)
public class UpcomingAdapter extends RecyclerView.Adapter<UpcomingAdapter.ViewHolder> implements DateDecoration.DateProvider {

    private static DateFormat sTimeFormat;

    private Context mContext;
    private List<Race> mRaces = new ArrayList<>();

    public UpcomingAdapter(Context context) {
        mContext = context;
        sTimeFormat = android.text.format.DateFormat.getTimeFormat(context);
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
        holder.mStartTimeTextView.setText(sTimeFormat.format(race.getStartAt()));
        updateStatusText(holder.mStatusTextView, race);
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

    private void updateStatusText(TextView textView, Race race) {
        if (race.isInProgress()) {
            textView.setText(R.string.in_progress);
            textView.setTextColor(mContext.getResources().getColor(R.color.green));
        } else if (race.isFinished()) {
            textView.setText(R.string.finished);
            textView.setTextColor(mContext.getResources().getColor(R.color.red));
        } else if (race.isRegistrationOpen()) {
            textView.setText(R.string.registration_open);
            textView.setTextColor(mContext.getResources().getColor(R.color.orange));
        } else {
            textView.setText("");
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.title)
        TextView mTitleTextView;
        @Bind(R.id.start_time)
        TextView mStartTimeTextView;
        @Bind(R.id.status)
        TextView mStatusTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            Typeface typeface = RawTypeface.obtain(itemView.getContext(), R.raw.fontin_bold);
            mTitleTextView.setTypeface(typeface);
        }
    }
}
