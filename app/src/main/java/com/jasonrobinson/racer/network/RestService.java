package com.jasonrobinson.racer.network;

import com.jasonrobinson.racer.model.Ladder;
import com.jasonrobinson.racer.model.Race;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public interface RestService {

    @GET("/leagues?type=event")
    Observable<List<Race>> races();

    @GET("/ladders/{id}")
    Observable<Ladder> ladder(@Path("id") String id, @Query("offset") int offset, @Query("limit") int limit);
}
