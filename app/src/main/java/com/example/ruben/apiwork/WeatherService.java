package com.example.ruben.apiwork;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WeatherService {
    @GET("location/search/")
    Single<List<CitySearch>> searchCities(@Query("query")String cityName);

    @GET("location/{woid")
    Single<CityInfo>getCityInfo(@Path("woid") String id);
}
