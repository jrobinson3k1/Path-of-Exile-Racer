package com.jasonrobinson.racer.model;

import java.util.Collection;
import java.util.Date;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Race {

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
	private Date registerAt;
	@DatabaseField
	private Date startAt;
	@DatabaseField
	private Date endAt;
	@ForeignCollectionField
	private Collection<Rule> rules;

	private Race() {

	}

	@DatabaseTable
	public static class Rule {

		@DatabaseField
		@SerializedName("id")
		private long ruleId;
		@DatabaseField
		private String name;
		@DatabaseField
		private String description;

		@SuppressWarnings("unused")
		@DatabaseField(generatedId = true)
		private transient long id;
		@SuppressWarnings("unused")
		@DatabaseField(foreign = true)
		private transient Race race;

		private Rule() {

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

	public Date getRegisterAt() {

		return registerAt;
	}

	public Date getStartAt() {

		return startAt;
	}

	public Date getEndAt() {

		return endAt;
	}

	public Collection<Rule> getRules() {

		return rules;
	}

	public boolean isInProgress() {

		Date now = new Date(System.currentTimeMillis());
		return now.after(getStartAt()) && now.before(getEndAt());
	}

	public boolean isFinished() {

		Date now = new Date(System.currentTimeMillis());
		return now.after(getEndAt());
	}

	public boolean isRegistrationOpen() {

		Date now = new Date(System.currentTimeMillis());
		return now.after(getRegisterAt()) && now.before(getEndAt());
	}
}
