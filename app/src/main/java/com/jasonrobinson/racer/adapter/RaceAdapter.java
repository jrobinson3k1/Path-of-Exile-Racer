package com.jasonrobinson.racer.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.jasonrobinson.racer.R;
import com.jasonrobinson.racer.model.Race;
import com.jasonrobinson.racer.util.AlarmUtils;
import com.jasonrobinson.racer.util.CalendarUtils;
import com.jasonrobinson.racer.util.RacerTimeUtils;
import com.jasonrobinson.racer.util.RawTypeface;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class RaceAdapter extends BaseExpandableListAdapter {

    private final DateFormat mDayOfWeekFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
    private final DateFormat mDateFormat;
    private final DateFormat mTimeFormat;

    private List<List<Race>> mDateRaces = new ArrayList<>();
    private List<Race> mRaces;

    private OnRaceActionClickListener mOnActionClickListener;

    public RaceAdapter(Context context, List<Race> races, OnRaceActionClickListener onActionClickListener) {
        mRaces = races;
        mOnActionClickListener = onActionClickListener;

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
                mDateRaces.add(new ArrayList<>());
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

            final ViewHolder holder = new ViewHolder(view);
            holder.titleTextView.setTypeface(RawTypeface.obtain(context, R.raw.fontin_bold));
            holder.popupMenu = new PopupMenu(context, holder.actionImageView);

            holder.actionImageView.setOnClickListener(v -> holder.popupMenu.show());

            view.setTag(holder);
        }

        final Race race = getChild(groupPosition, childPosition);
        final ViewHolder holder = (ViewHolder) view.getTag();

        if (holder.timer != null) {
            holder.timer.cancel();
            holder.timer = null;
        }

        setupActionMenu(context, holder.popupMenu, race);

        holder.titleTextView.setText(race.getRaceId());

        Date startDate = race.getStartAt();
        Date endDate = race.getEndAt();

        if (startDate != null && endDate != null) {
            long millisNow = System.currentTimeMillis();
            long millisTotal = (startDate.getTime() - millisNow) + (endDate.getTime() - startDate.getTime());

            if (millisTotal > 0) {
                holder.timer = new RaceCountDownTimer(holder, race, millisTotal);
                holder.timer.start();
            } else {
                updateTimeViews(holder, race);
            }
        }

        int backgroundId;
        if (AlarmUtils.isAlarmAdded(context, race)) {
            backgroundId = R.color.pastel_yellow;
        } else {
            TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{android.R.attr.listChoiceBackgroundIndicator});
            backgroundId = a.getResourceId(0, android.R.drawable.list_selector_background);
            a.recycle();
        }
        holder.rootView.setBackgroundResource(backgroundId);

        return view;
    }

    private void setupActionMenu(Context context, PopupMenu popupMenu, final Race race) {
        Menu menu = popupMenu.getMenu();
        menu.clear();

        popupMenu.getMenuInflater().inflate(R.menu.races_context_menu, menu);
        if (TextUtils.isEmpty(race.getUrl())) {
            menu.removeItem(R.id.menu_forum_post);
        }

        boolean alarmAdded = AlarmUtils.isAlarmAdded(context, race);
        if (alarmAdded || race.isInProgress() || race.isFinished()) {
            menu.removeItem(R.id.menu_add_notification);
        }

        if (!alarmAdded || race.isInProgress() || race.isFinished()) {
            menu.removeItem(R.id.menu_remove_notification);
        }

        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_forum_post:
                    mOnActionClickListener.onForumPostClicked(race);
                    break;
                case R.id.menu_add_notification:
                    mOnActionClickListener.onAddNotificationClicked(race);
                    break;
                case R.id.menu_remove_notification:
                    mOnActionClickListener.onRemoveNotificationClicked(race);
                    break;
                default:
                    return false;
            }

            return true;
        });
    }

    private void updateTimeViews(ViewHolder holder, Race race) {
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

        int color;
        int textResId;
        if (race.isInProgress()) {
            color = Color.GREEN;
            textResId = R.string.started;
        } else if (race.isRegistrationOpen()) {
            color = Color.GREEN;
            textResId = R.string.open;
        } else if (race.isFinished()) {
            color = Color.BLACK;
            textResId = R.string.finished;
        } else {
            color = Color.RED;
            textResId = R.string.closed;
        }

        holder.registerTextView.setTextColor(color);
        holder.registerTextView.setText(textResId);

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

        private ViewHolder mViewHolder;
        private Race mRace;

        public RaceCountDownTimer(ViewHolder viewHolder, Race race, long millisInFuture) {
            super(millisInFuture, 1000);
            mViewHolder = viewHolder;
            mRace = race;
        }

        @Override
        public void onFinish() {
            updateTimeViews(mViewHolder, mRace);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            updateTimeViews(mViewHolder, mRace);
        }
    }

    public interface OnRaceActionClickListener {

        void onForumPostClicked(Race race);

        void onAddNotificationClicked(Race race);

        void onRemoveNotificationClicked(Race race);
    }

    class ViewHolder {

        View rootView;
        @InjectView(R.id.title)
        TextView titleTextView;
        @InjectView(R.id.startTime)
        TextView timeTextView;
        @InjectView(R.id.register)
        TextView registerTextView;
        @InjectView(R.id.action_ImageView)
        ImageView actionImageView;

        PopupMenu popupMenu;

        public RaceCountDownTimer timer;

        public ViewHolder(View v) {
            this.rootView = v;
            ButterKnife.inject(this, v);
        }
    }
}
