package com.jasonrobinson.racer.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@DatabaseTable
public class Race implements Parcelable {

    public static final Parcelable.Creator<Race> CREATOR = new Parcelable.Creator<Race>() {

        public Race createFromParcel(Parcel in) {

            return new Race(in);
        }

        public Race[] newArray(int size) {

            return new Race[size];
        }
    };
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

    private Race(Parcel in) {

        raceId = in.readString();
        description = in.readString();
        url = in.readString();
        event = in.readInt() == 1 ? true : false;
        registerAt = new Date(in.readLong());
        startAt = new Date(in.readLong());
        endAt = new Date(in.readLong());

        Parcelable[] ruleParcels = in.readParcelableArray(Rule.class.getClassLoader());
        rules = new ArrayList<Rule>();
        for (Parcelable ruleParcel : ruleParcels) {
            rules.add((Rule) ruleParcel);
        }
    }

    @Override
    public int describeContents() {

        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(raceId);
        dest.writeString(description);
        dest.writeString(url);
        dest.writeInt(event ? 1 : 0);
        dest.writeLong(registerAt.getTime());
        dest.writeLong(startAt.getTime());
        dest.writeLong(endAt.getTime());
        dest.writeParcelableArray(rules.toArray(new Rule[0]), flags);
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

    @DatabaseTable
    public static class Rule implements Parcelable {

        public static final Parcelable.Creator<Rule> CREATOR = new Parcelable.Creator<Rule>() {

            public Rule createFromParcel(Parcel in) {

                return new Rule(in);
            }

            public Rule[] newArray(int size) {

                return new Rule[size];
            }
        };
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

        private Rule(Parcel in) {

            ruleId = in.readLong();
            name = in.readString();
            description = in.readString();
        }

        @Override
        public int describeContents() {

            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {

            dest.writeLong(ruleId);
            dest.writeString(name);
            dest.writeString(description);
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
}
