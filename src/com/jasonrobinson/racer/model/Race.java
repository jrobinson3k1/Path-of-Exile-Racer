package com.jasonrobinson.racer.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.google.gson.annotations.SerializedName;

public class Race {

	private static final SimpleDateFormat DATE_FORMAT;

	static {
		DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
		DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	@SerializedName("id")
	private String raceId;
	private String description;
	private String url;
	private boolean event;
	private String registerAt;
	private String startAt;
	private String endAt;
	private Rule[] rules;

	@SuppressWarnings("unused")
	private Race() {

	}

	public Race(String raceId, String description, String url, boolean event, String registerAt, String startAt, String endAt, Rule[] rules) {

		this.raceId = raceId;
		this.description = description;
		this.url = url;
		this.event = event;
		this.registerAt = registerAt;
		this.startAt = startAt;
		this.endAt = endAt;
		this.rules = rules;
	}

	public static class Rule {

		@SerializedName("id")
		private long ruleId;
		private String name;
		private String description;

		@SuppressWarnings("unused")
		private Rule() {

		}

		public Rule(long ruleId, String name, String description) {

			this.ruleId = ruleId;
			this.name = name;
			this.description = description;
		}

		public long getRuleId() {

			return ruleId;
		}

		public String getName() {

			return name;
		}

		public String getDescription() {

			return description;
		}
	}

	public String getRaceId() {

		return raceId;
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

	public String getStartAt() {

		return startAt;
	}

	public String getEndAt() {

		return endAt;
	}

	public Date getRegisterAtDate() throws ParseException {

		return DATE_FORMAT.parse(registerAt);
	}

	public Date getStartAtDate() throws ParseException {

		return DATE_FORMAT.parse(startAt);
	}

	public Date getEndAtDate() throws ParseException {

		return DATE_FORMAT.parse(endAt);
	}

	public Rule[] getRules() {

		return rules;
	}

	public boolean isInProgress() throws ParseException {

		Date now = new Date(System.currentTimeMillis());
		return now.after(getStartAtDate()) && now.before(getEndAtDate());
	}

	public boolean isFinished() throws ParseException {

		Date now = new Date(System.currentTimeMillis());
		return now.after(getEndAtDate());
	}

	public boolean isRegistrationOpen() throws ParseException {

		Date now = new Date(System.currentTimeMillis());
		return now.after(getRegisterAtDate()) && now.before(getEndAtDate());
	}
}
