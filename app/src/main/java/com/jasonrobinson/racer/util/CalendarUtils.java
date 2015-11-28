package com.jasonrobinson.racer.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CalendarUtils {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM d", Locale.getDefault());
    private static final SimpleDateFormat DATE_YEAR_FORMAT = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());

    private CalendarUtils() {
    }

    public static boolean isToday(Date date) {
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTime(date);

        Calendar today = Calendar.getInstance(Locale.getDefault());

        return cal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) && cal.get(Calendar.YEAR) == today.get(Calendar.YEAR);
    }

    public static boolean isTomorrow(Date date) {
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTime(date);

        Calendar tomorrow = Calendar.getInstance(Locale.getDefault());
        tomorrow.roll(Calendar.DAY_OF_YEAR, true);

        return cal.get(Calendar.DAY_OF_YEAR) == tomorrow.get(Calendar.DAY_OF_YEAR) && cal.get(Calendar.YEAR) == tomorrow.get(Calendar.YEAR);
    }

    public static boolean isYesterday(Date date) {
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTime(date);

        Calendar yesterday = Calendar.getInstance(Locale.getDefault());
        yesterday.roll(Calendar.DAY_OF_YEAR, false);

        return cal.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR) && cal.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR);
    }

    public static String getFormattedDate(Date date) {
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTime(date);

        Calendar now = Calendar.getInstance(Locale.getDefault());

        if (cal.get(Calendar.YEAR) != now.get(Calendar.YEAR)) {
            return DATE_YEAR_FORMAT.format(date);
        }

        return DATE_FORMAT.format(date);
    }
}
