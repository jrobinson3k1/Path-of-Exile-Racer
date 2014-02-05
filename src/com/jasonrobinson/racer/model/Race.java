package com.jasonrobinson.racer.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Race {

	private static final SimpleDateFormat DATE_FORMAT;

	static {
		DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
		DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	@DatabaseField(id = true)
	@SerializedName("id")
	private String raceId;
	@DatabaseField
	private String description;
	@DatabaseField
	private String url;
	@DatabaseField
	private boolean event;
	@DatabaseField
	private String registerAt;
	@DatabaseField
	private String startAt;
	@DatabaseField
	private String endAt;
	@ForeignCollectionField
	private Collection<Rule> rules;

	@DatabaseTable
	public static class Rule {

		@DatabaseField
		@SerializedName("id")
		private long ruleId;
		@DatabaseField
		private String name;
		@DatabaseField
		private String description;

		@DatabaseField(generatedId = true)
		private transient long id;
		@DatabaseField(foreign = true)
		private transient Race race;

		public long getRuleId() {

			return ruleId;
		}

		public String getName() {

			return name;
		}

		public String getDescription() {

			return description;
		}

		public void setRace(Race race) {

			this.race = race;
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

	public Collection<Rule> getRules() {

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
