package com.jasonrobinson.racer.module;

import dagger.Module;

@Module(
        includes = {
                AnalyticsModule.class,
                ContextModule.class,
                DatabaseModule.class,
                SettingsModule.class
        }
)
public class RacerModule {
}
