package com.jasonrobinson.racer.util;

import java.util.Formatter;
import java.util.Locale;

public class RacerTimeUtils {

    private static final String ELAPSED_TIME_HMMSS = "%1$d:%2$02d:%3$02d";
    private static final String ELAPSED_TIME_MMSS = "%1$02d:%2$02d";

    private RacerTimeUtils() {

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
}
