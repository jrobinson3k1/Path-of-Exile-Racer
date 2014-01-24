package com.jasonrobinson.racer.network;

import java.util.List;

import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

import com.jasonrobinson.racer.model.Ladder;
import com.jasonrobinson.racer.model.Race;

public class RaceClient {

	private static final String API_URL = "http://api.pathofexile.com";

	public List<Race> fetchRaces() {

		RaceService raceService = buildRaceService();
		return raceService.races();
	}

	public Ladder fetchLadder(String id, int offset, int limit) {

		RaceService raceService = buildRaceService();
		return raceService.ladder(id, offset, limit);
	}

	private RaceService buildRaceService() {

		RestAdapter restAdapter = new RestAdapter.Builder().setServer(API_URL).build();
		return restAdapter.create(RaceService.class);
	}

	public interface RaceService {

		@GET("/leagues?type=event")
		List<Race> races();

		@GET("/ladders/{id}")
		Ladder ladder(@Path("id") String id, @Query("offset") int offset, @Query("limit") int limit);
	}
}
