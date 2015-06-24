package com.jasonrobinson.racer.module;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import com.jasonrobinson.racer.BuildConfig;
import com.jasonrobinson.racer.enumeration.PoEClass;
import com.jasonrobinson.racer.network.RestService;

import java.lang.reflect.Type;

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
        GsonBuilder gsonBuilder = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .registerTypeAdapter(PoEClass.class, new PoEClassSerializer());

        RestAdapter restAdapter = restAdapterBuilder
                .setConverter(new GsonConverter(gsonBuilder.create()))
                .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
                .build();

        return restAdapter.create(RestService.class);
    }

    private class PoEClassSerializer implements JsonSerializer<PoEClass>, JsonDeserializer<PoEClass> {

        @Override
        public PoEClass deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            String value = json.getAsString();
            return PoEClass.getClassForName(value);
        }

        @Override
        public JsonElement serialize(PoEClass src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getName());
        }
    }
}
