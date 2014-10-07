package com.jasonrobinson.racer.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jasonrobinson.racer.R;
import com.jasonrobinson.racer.model.Race;
import com.jasonrobinson.racer.model.Race.Rule;
import com.jasonrobinson.racer.util.AlarmUtils;
import com.jasonrobinson.racer.util.CalendarUtils;
import com.jasonrobinson.racer.util.RacerTimeUtils;
import com.jasonrobinson.racer.util.RawTypeface;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class RaceAdapter extends BaseExpandableListAdapter {

    private final DateFormat mDayOfWeekFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
    private final DateFormat mDateFormat;
    private final DateFormat mTimeFormat;

    private List<List<Race>> mDateRaces = new ArrayList<List<Race>>();
    private List<Race> mRaces;

    public RaceAdapter(Context context, List<Race> races) {

        mRaces = races;
        mDateFormat = android.text.format.DateFormat.getDateFormat(context);
        mTimeFormat = android.text.format.DateFormat.getTimeFormat(context);

        init();
    }

    private void init() {

        Date lastDate = null;
        for (Race race : mRaces) {
            Date startAt = race.getStartAt();

            Calendar lastCal = Calendar.getInstance();
            if (lastDate != null) {
                lastCal.setTime(lastDate);
            }

            Calendar cal = Calendar.getInstance();
            cal.setTime(startAt);

            if (lastDate == null || cal.get(Calendar.YEAR) != lastCal.get(Calendar.YEAR) || cal.get(Calendar.DAY_OF_YEAR) != lastCal.get(Calendar.DAY_OF_YEAR)) {
                mDateRaces.add(new ArrayList<Race>());
                lastDate = startAt;
            }

            mDateRaces.get(mDateRaces.size() - 1).add(race);
        }
    }

    @Override
    public int getGroupCount() {

        return mDateRaces.size();
    }

    @Override
    public List<Race> getGroup(int groupPosition) {

        return mDateRaces.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {

        return groupPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        return mDateRaces.get(groupPosition).size();
    }

    @Override
    public Race getChild(int groupPosition, int childPosition) {

        return mDateRaces.get(groupPosition).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {

        return groupPosition * 1000 + childPosition;
    }

    @Override
    public boolean hasStableIds() {

        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {

        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        Context context = parent.getContext();
        View view = convertView;
        TextView textView1;
        TextView textView2;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(android.R.layout.simple_expandable_list_item_2, parent, false);

            textView1 = (TextView) view.findViewById(android.R.id.text1);
            textView1.setTypeface(RawTypeface.obtain(context, R.raw.fontin_regular));

            textView2 = (TextView) view.findViewById(android.R.id.text2);
            textView2.setTypeface(RawTypeface.obtain(context, R.raw.fontin_regular));
        } else {
            textView1 = (TextView) view.findViewById(android.R.id.text1);
            textView2 = (TextView) view.findViewById(android.R.id.text2);
        }

        List<Race> races = getGroup(groupPosition);
        Date date = races.get(0).getStartAt();

        textView1.setText(formatDayOfWeek(context, date) + " (" + races.size() + ")");
        textView2.setText(formatDate(date));

        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        Context context = parent.getContext();
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.race_item, parent, false);

            ViewHolder holder = new ViewHolder(view);
            holder.titleTextView.setTypeface(RawTypeface.obtain(context, R.raw.fontin_bold));
            view.setTag(holder);
        }

        Race race = getChild(groupPosition, childPosition);
        ViewHolder holder = (ViewHolder) view.getTag();

        if (holder.timer != null) {
            holder.timer.cancel();
            holder.timer = null;
        }

        holder.titleTextView.setText(race.getRaceId());

        Date startDate = race.getStartAt();
        Date endDate = race.getEndAt();

        if (startDate != null && endDate != null) {
            long millisNow = System.currentTimeMillis();
            long millisTotal = (startDate.getTime() - millisNow) + (endDate.getTime() - startDate.getTime());

            if (millisTotal > 0) {
                holder.timer = new RaceCountDownTimer(context, holder, race, millisTotal);
                holder.timer.start();
            } else {
                updateTimeViews(context, holder, race);
            }
        }

        holder.descriptionTextView.setText(formatRules(race.getRules()));
        holder.notificationImageView.setVisibility(AlarmUtils.isAlarmAdded(context, race) ? View.VISIBLE : View.GONE);
        return view;
    }

    private CharSequence formatRules(Collection<Rule> rules) {

        if (rules == null || rules.isEmpty()) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        Iterator<Rule> iterator = rules.iterator();
        for (int i = 0; i < rules.size(); i++) {
            Rule rule = iterator.next();

            if (i != 0) {
                sb.append(", ");
            }

            sb.append(rule.getName());
        }

        return sb;
    }

    private void updateTimeViews(Context context, ViewHolder holder, Race race) {

        CharSequence startAtTime;

        long millisNow = System.currentTimeMillis();
        long millisUntil = race.getStartAt().getTime() - millisNow;
        long millisRemaining = race.getEndAt().getTime() - millisNow;

        if (millisUntil <= 3600000 && !race.isFinished()) { // <60 minutes until
            // start
            long millis;
            if (millisUntil <= 0) { // race in progress
                millis = millisRemaining;
            } else {
                millis = millisUntil;
            }

            startAtTime = RacerTimeUtils.formatElapsedTime(millis / 1000);
        } else { // >60 minutes until start or race finished
            startAtTime = formatTime(race.getStartAt());
        }

        holder.timeTextView.setText(startAtTime);

        if (race.isInProgress()) {
            holder.registerTextView.setTextColor(Color.GREEN);
            holder.registerTextView.setText(R.string.started);
        } else if (race.isRegistrationOpen()) {
            holder.registerTextView.setTextColor(Color.GREEN);
            holder.registerTextView.setText(R.string.open);
        } else if (race.isFinished()) {
            holder.registerTextView.setTextColor(Color.BLACK);
            holder.registerTextView.setText(R.string.finished);
        } else {
            holder.registerTextView.setTextColor(Color.RED);
            holder.registerTextView.setText(R.string.closed);
        }

        holder.registerTextView.setText(holder.registerTextView.getText().toString().toUpperCase(Locale.getDefault()));
    }

    private CharSequence formatDate(Date date) {

        return mDateFormat.format(date);
    }

    private CharSequence formatDayOfWeek(Context context, Date date) {

        Calendar time = Calendar.getInstance(Locale.getDefault());
        time.setTime(date);

        if (CalendarUtils.isToday(time)) {
            return context.getString(R.string.today);
        } else if (CalendarUtils.isTomorrow(time)) {
            return context.getString(R.string.tomorrow);
        } else if (CalendarUtils.isYesterday(time)) {
            return context.getString(R.string.yesterday);
        }

        return mDayOfWeekFormat.format(date);
    }

    private CharSequence formatTime(Date date) {

        return mTimeFormat.format(date);
    }

    private class RaceCountDownTimer extends CountDownTimer {

        private Context mContext;
        private ViewHolder mViewHolder;
        private Race mRace;

        public RaceCountDownTimer(Context context, ViewHolder viewHolder, Race race, long millisInFuture) {

            super(millisInFuture, 1000);
            mContext = context;
            mViewHolder = viewHolder;
            mRace = race;
        }

        @Override
        public void onFinish() {

            updateTimeViews(mContext, mViewHolder, mRace);
        }

        @Override
        public void onTick(long millisUntilFinished) {

            updateTimeViews(mContext, mViewHolder, mRace);
        }
    }

    private class ViewHolder {

        public TextView titleTextView;
        public TextView timeTextView;
        public TextView registerTextView;
        public TextView descriptionTextView;
        public ImageView notificationImageView;

        public RaceCountDownTimer timer;

        public ViewHolder(View v) {

            titleTextView = (TextView) v.findViewById(R.id.title);
            timeTextView = (TextView) v.findViewById(R.id.startTime);
            registerTextView = (TextView) v.findViewById(R.id.register);
            descriptionTextView = (TextView) v.findViewById(R.id.description);
            notificationImageView = (ImageView) v.findViewById(R.id.notification);
        }
    }
}
