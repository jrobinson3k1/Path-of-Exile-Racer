package com.jasonrobinson.racer.util;

import java.util.Calendar;
import java.util.Locale;

public class CalendarUtils {

	private CalendarUtils() {

	}

	public static boolean isToday(Calendar cal) {

		Calendar today = Calendar.getInstance(Locale.getDefault());

		return cal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) && cal.get(Calendar.YEAR) == today.get(Calendar.YEAR);
	}

	public static boolean isTomorrow(Calendar cal) {

		Calendar tomorrow = Calendar.getInstance(Locale.getDefault());
		tomorrow.roll(Calendar.DAY_OF_YEAR, true);

		return cal.get(Calendar.DAY_OF_YEAR) == tomorrow.get(Calendar.DAY_OF_YEAR) && cal.get(Calendar.YEAR) == tomorrow.get(Calendar.YEAR);
	}

	public static boolean isYesterday(Calendar cal) {

		Calendar yesterday = Calendar.getInstance(Locale.getDefault());
		yesterday.roll(Calendar.DAY_OF_YEAR, false);

		return cal.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR) && cal.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR);
	}
}
