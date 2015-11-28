package com.jasonrobinson.racer.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jasonrobinson.racer.R;
import com.jasonrobinson.racer.model.Race;
import com.metova.slim.Slim;
import com.metova.slim.annotation.Layout;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

@Layout(R.layout.item_race)
public class UpcomingAdapter extends RecyclerView.Adapter<UpcomingAdapter.ViewHolder> {

    private List<Race> mRaces;

    public UpcomingAdapter(List<Race> races) {
        mRaces = races;
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
        holder.mStartTimeTextView.setText(race.getStartAt().toString());
        holder.mRegisterTextView.setText(race.getRegisterAt().toString());
    }

    @Override
    public int getItemCount() {
        return mRaces.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.title)
        TextView mTitleTextView;
        @Bind(R.id.start_time)
        TextView mStartTimeTextView;
        @Bind(R.id.register)
        TextView mRegisterTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
