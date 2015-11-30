package com.jasonrobinson.racer.network;

import com.jasonrobinson.racer.model.Race;

import java.util.List;

import retrofit.http.GET;
import rx.Observable;

public interface RestService {

    /**
     * Get a list of upcoming races.
     *
     * @return List of upcoming races.
     */
    @GET("/leagues?type=event")
    Observable<List<Race>> races();

    /**
     * Get a ladder by league id. There is a restriction in place on the last ladder entry
     * you are able to retrieve which is set to 15000.
     * @param id The id (name) of the league for the ladder you want to retrieve.
     * @param offset Specifies the offset to the first ladder entry to include. Default: 0.
     * @param limit Specifies the number of ladder entries to include. Default: 20, Max: 200.
     * @return Entries for the ladder within the limit and offset.
     */
//    @GET("/ladders/{id}")
//    Observable<Ladder> ladder(@Path("id") String id, @Query("offset") int offset, @Query("limit") int limit);
}
