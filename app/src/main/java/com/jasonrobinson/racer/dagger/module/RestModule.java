package com.jasonrobinson.racer.dagger.module;

import com.jasonrobinson.racer.network.RestService;

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
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(RestService.class);
    }
}
