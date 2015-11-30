package com.jasonrobinson.racer.util;

import android.content.Context;

import com.jasonrobinson.racer.R;

import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

public class TimeUtils {

    private static final String ELAPSED_TIME_HMMSS = "%1$d:%2$02d:%3$02d";
    private static final String ELAPSED_TIME_MMSS = "%1$02d:%2$02d";

    private static final long SEC_IN_WEEK = 604800;

    private TimeUtils() {

    }

    // I would use DateUtils.formatElapsedTime, but it's bugged for hours > 100
    public static CharSequence formatElapsedTime(long elapsedSeconds) {

        long hours = 0;
        long minutes = 0;
        long seconds = 0;
        if (elapsedSeconds >= 3600) {
            hours = elapsedSeconds / 3600;
            elapsedSeconds -= hours * 3600;
        }
        if (elapsedSeconds >= 60) {
            minutes = elapsedSeconds / 60;
            elapsedSeconds -= minutes * 60;
        }
        seconds = elapsedSeconds;

        Formatter f = new Formatter(new StringBuilder(10), Locale.getDefault());
        if (hours > 0) {
            return f.format(ELAPSED_TIME_HMMSS, hours, minutes, seconds).toString();
        } else {
            return f.format(ELAPSED_TIME_MMSS, minutes, seconds).toString();
        }
    }

    public static CharSequence formatDuration(Context context, Date start, Date end) {
        if (end.before(start) || end.equals(start)) {
            return "0 " + context.getString(R.string.seconds);
        }

        long duration = end.getTime() - start.getTime();
        duration /= 1000;

        long weeks = duration / 604800;
        long days = (duration % 604800) / 86400;
        long hours = (duration % 86400) / 3600;
        long minutes = (duration % 3600) / 60;
        long seconds = duration % 60;

        StringBuilder durationBuilder = new StringBuilder();
        if (weeks > 0) {
            durationBuilder.append(formatTimeUnit(weeks, context.getString(R.string.week), context.getString(R.string.weeks)));
            durationBuilder.append(" ");
        }

        if (days > 0) {
            durationBuilder.append(formatTimeUnit(days, context.getString(R.string.day), context.getString(R.string.days)));
            durationBuilder.append(" ");
        }

        if (hours > 0) {
            durationBuilder.append(formatTimeUnit(hours, context.getString(R.string.hour), context.getString(R.string.hours)));
            durationBuilder.append(" ");
        }

        if (minutes > 0) {
            durationBuilder.append(formatTimeUnit(minutes, context.getString(R.string.minute), context.getString(R.string.minutes)));
            durationBuilder.append(" ");
        }

        if (seconds > 0) {
            durationBuilder.append(formatTimeUnit(seconds, context.getString(R.string.second), context.getString(R.string.seconds)));
        }

        // Remove trailing whitespace
        if (durationBuilder.length() > 0 && durationBuilder.lastIndexOf(" ") == durationBuilder.length() - 1) {
            durationBuilder.deleteCharAt(durationBuilder.length() - 1);
        }

        return durationBuilder.toString();
    }

    private static CharSequence formatTimeUnit(long time, String singular, String plural) {
        if (time == 1) {
            return time + " " + singular;
        }

        return time + " " + plural;
    }
}
