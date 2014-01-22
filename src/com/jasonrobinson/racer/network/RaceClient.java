package com.jasonrobinson.racer.network;

import java.util.List;

import retrofit.RestAdapter;
import retrofit.http.GET;

import com.jasonrobinson.racer.model.Race;

public class RaceClient {

	private static final String API_URL = "http://api.pathofexile.com";

	public List<Race> build() {

		RestAdapter restAdapter = new RestAdapter.Builder().setServer(API_URL).build();

		RaceService raceService = restAdapter.create(RaceService.class);
		return raceService.races();
	}

	public interface RaceService {

		@GET("/leagues?type=event")
		List<Race> races();
	}
}
