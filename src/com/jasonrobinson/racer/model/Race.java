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

	public String getRegisterAt() {

		return registerAt;
	}

	public Date getStartAt() throws ParseException {

		return DATE_FORMAT.parse(startAt);
	}

	public String getEndAt() {

		return endAt;
	}

	public Rule[] getRules() {

		return rules;
	}
}
