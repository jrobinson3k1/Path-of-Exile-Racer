package com.jasonrobinson.racer.module;

import com.jasonrobinson.racer.ui.race.RaceListFragment;
import com.jasonrobinson.racer.ui.race.RaceManager;
import com.jasonrobinson.racer.ui.race.RacesActivity;

import dagger.Module;

@Module(
        includes = {
                AnalyticsModule.class,
                ContextModule.class,
                DatabaseModule.class,
                SettingsModule.class,
                NetworkModule.class
        },
        injects = {
                RacesActivity.class,
                RaceListFragment.class
        }
)
public class RacerModule {

}
