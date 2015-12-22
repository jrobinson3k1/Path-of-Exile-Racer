package com.jasonrobinson.racer.model;

import com.jasonrobinson.racer.database.RaceDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ConflictAction;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.container.ForeignKeyContainer;

import java.util.Date;

@Table(databaseName = RaceDatabase.NAME)
public class RaceInteractions extends BaseModel {

    @Column
    @PrimaryKey(autoincrement = true)
    long id;
    @Column
    @Unique(onUniqueConflict = ConflictAction.IGNORE)
    @ForeignKey(
            references = {@ForeignKeyReference(columnName = "race_id",
                    columnType = Long.class,
                    foreignColumnName = "id")},
            saveForeignKeyModel = false)
    ForeignKeyContainer<Race> race;
    @Column
    boolean favorite;
    @Column
    Date alarm;

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
        update();
    }

    public Date getAlarm() {
        return alarm;
    }

    public void setAlarm(Date alarm) {
        this.alarm = alarm;
        update();
    }
}