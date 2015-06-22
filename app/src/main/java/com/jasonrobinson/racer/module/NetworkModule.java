package com.jasonrobinson.racer.module;

import com.google.gson.GsonBuilder;

import com.jasonrobinson.racer.network.RestService;
import com.jasonrobinson.racer.ui.race.RaceManager;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

@Module(
        library = true
)
public class NetworkModule {

    private static final String API_URL = "http://api.pathofexile.com";

    @Provides
    RestService providesRestService() {
        RestAdapter.Builder restAdapterBuilder = new RestAdapter.Builder().setEndpoint(API_URL);
        GsonBuilder gsonBuilder = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

        RestAdapter restAdapter = restAdapterBuilder.setConverter(new GsonConverter(gsonBuilder.create())).build();
        return restAdapter.create(RestService.class);
    }
}
