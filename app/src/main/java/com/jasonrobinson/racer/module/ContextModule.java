package com.jasonrobinson.racer.module;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

@Module(library = true)
public class ContextModule {

    private final Context mContext;

    public ContextModule(Context context) {
        mContext = context;
    }

    @Provides Context provideContext() {
        return mContext;
    }
}
