package com.jasonrobinson.racer.model;

import com.google.gson.annotations.SerializedName;

import com.jasonrobinson.racer.database.RaceDatabase;
import com.jasonrobinson.racer.util.gson.Exclude;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ConflictAction;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.Date;

@Table(databaseName = RaceDatabase.NAME)
public class Race extends BaseModel {

    @Exclude
    @Column
    @PrimaryKey(autoincrement = true)
    long id;
    @SerializedName("id")
    @Column
    @Unique(onUniqueConflict = ConflictAction.IGNORE)
    String name;
    @Column
    String description;
    @Column
    String url;
    @Column
    boolean event;
    @Column
    Date registerAt;
    @Column
    Date startAt;
    @Column
    Date endAt;

    @Exclude
    @Column
    @ForeignKey(
            references = {@ForeignKeyReference(columnName = "interactions_id",
                    columnType = Long.class,
                    foreignColumnName = "id")},
            onDelete = ForeignKeyAction.CASCADE,
            saveForeignKeyModel = false)
    RaceInteractions interactions;

    private RaceInteractions getInteractions() {
        if (interactions == null) {
            interactions = new RaceInteractions();
            interactions.save();
            update();
        }

        return interactions;
    }

    public boolean isUpcoming() {
        Date now = new Date(System.currentTimeMillis());
        return now.before(getStartAt());
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

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
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

    public boolean isFavorite() {
        return getInteractions().isFavorite();
    }

    public void setFavorite(boolean favorite) {
        getInteractions().setFavorite(favorite);
    }

    public Date getAlarm() {
        return getInteractions().getAlarm();
    }

    public void setAlarm(Date alarm) {
        getInteractions().setAlarm(alarm);
    }
}
