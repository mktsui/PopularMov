package com.balljoin.mktsui.popularmov.services;

import com.balljoin.mktsui.popularmov.model.TmdbReturnModel;
import com.balljoin.mktsui.popularmov.utilities.Constants;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TmdbWebService {

    @GET(Constants.API_FFED)
    Call<TmdbReturnModel> getMovies(
            @Query("api_key") String api_key,
            @Query("language") String language,
            @Query("page") int page);
}
