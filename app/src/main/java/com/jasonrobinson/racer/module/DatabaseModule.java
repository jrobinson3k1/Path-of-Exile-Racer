package com.jasonrobinson.racer.module;

import android.content.Context;

import com.jasonrobinson.racer.db.DatabaseHelper;
import com.jasonrobinson.racer.db.DatabaseManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = DatabaseManager.class,
        includes = ContextModule.class
)
public class DatabaseModule {

    @Provides
    @Singleton
    DatabaseHelper provideDatabaseHelper(Context context) {
        return new DatabaseHelper(context);
    }
}
