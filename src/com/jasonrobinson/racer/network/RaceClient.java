package com.jasonrobinson.racer.network;

import java.net.SocketTimeoutException;
import java.util.List;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.converter.Converter;
import retrofit.converter.GsonConverter;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

import com.google.gson.GsonBuilder;
import com.jasonrobinson.racer.model.Ladder;
import com.jasonrobinson.racer.model.Race;

public class RaceClient {

	private static final String API_URL = "http://api.pathofexile.com";

	public List<Race> fetchRaces() throws SocketTimeoutException, RetrofitError {

		GsonBuilder builder = new GsonBuilder();
		builder.setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		RaceService raceService = buildRaceService(new GsonConverter(builder.create()));

		return raceService.races();
	}

	public Ladder fetchLadder(String id, int offset, int limit) throws SocketTimeoutException, RetrofitError {

		RaceService raceService = buildRaceService();

		return raceService.ladder(id, offset, limit);
	}

	private RaceService buildRaceService() {

		return buildRaceService(null);
	}

	private RaceService buildRaceService(Converter converter) {

		RestAdapter restAdapter = new RestAdapter.Builder().setServer(API_URL).setConverter(converter).build();
		return restAdapter.create(RaceService.class);
	}

	public interface RaceService {

		@GET("/leagues?type=event")
		List<Race> races();

		@GET("/ladders/{id}")
		Ladder ladder(@Path("id") String id, @Query("offset") int offset, @Query("limit") int limit);
	}
}
