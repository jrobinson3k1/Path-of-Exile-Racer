package com.jasonrobinson.racer.dagger.module;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.jasonrobinson.racer.network.RestService;
import com.jasonrobinson.racer.util.gson.AnnotationExclusionStrategy;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

@Module
public class RestModule {

    private static final String API_URL = "http://api.pathofexile.com";

    @Provides
    @Singleton
    RestService providesRestService() {
        // https://github.com/Raizlabs/DBFlow/issues/121
        Gson gson = new GsonBuilder()
                .setExclusionStrategies(
                        new ExclusionStrategy() {
                            @Override
                            public boolean shouldSkipField(FieldAttributes f) {
                                return f.getDeclaredClass().equals(ModelAdapter.class);
                            }

                            @Override
                            public boolean shouldSkipClass(Class<?> clazz) {
                                return false;
                            }
                        },
                        new AnnotationExclusionStrategy()
                )
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return retrofit.create(RestService.class);
    }
}
