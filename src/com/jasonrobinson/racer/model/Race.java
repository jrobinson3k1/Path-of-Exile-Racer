package com.jasonrobinson.racer.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Race {

	private static final SimpleDateFormat DATE_FORMAT;

	static {
		DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
		DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	private String id;
	private String description;
	private String url;
	private boolean event;
	private String registerAt;
	private String startAt;
	private String endAt;
	private Rule[] rules;

	public static class Rule {

		private long id;
		private String name;
		private String description;

		public long getId() {

			return id;
		}

		public String getName() {

			return name;
		}

		public String getDescription() {

			return description;
		}
	}

	public String getId() {

		return id;
	}

	public String getDescription() {

		return description;
	}

	public String getUrl() {

		return url;
	}

	public boolean isEvent() {

		return event;
	}

	public Date getRegisterAt() throws ParseException {

		return DATE_FORMAT.parse(registerAt);
	}

	public Date getStartAt() throws ParseException {

		return DATE_FORMAT.parse(startAt);
	}

	public Date getEndAt() throws ParseException {

		return DATE_FORMAT.parse(endAt);
	}

	public Rule[] getRules() {

		return rules;
	}

	public boolean isInProgress() throws ParseException {

		Date now = new Date(System.currentTimeMillis());
		return now.after(getStartAt()) && now.before(getEndAt());
	}

	public boolean isFinished() throws ParseException {

		Date now = new Date(System.currentTimeMillis());
		return now.after(getEndAt());
	}

	public boolean isRegistrationOpen() throws ParseException {

		Date now = new Date(System.currentTimeMillis());
		return now.after(getRegisterAt()) && now.before(getEndAt());
	}
}
