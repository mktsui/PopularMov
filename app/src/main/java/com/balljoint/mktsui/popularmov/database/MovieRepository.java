package com.balljoin.mktsui.popularmov.database;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.balljoin.mktsui.popularmov.model.MovieModel;
import com.balljoin.mktsui.popularmov.model.TmdbReturnModel;
import com.balljoin.mktsui.popularmov.services.RetrofitClient;
import com.balljoin.mktsui.popularmov.services.TmdbWebService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieRepository implements AsyncResult{
    private static MovieRepository mInstance;
    private Context mContext;

    private MovieDao mDao;
    private TmdbWebService mService;
    private boolean isLastPage = false;
    private LiveData<List<MovieEntity>> mMovies;
    private MutableLiveData<List<MovieEntity>> mResults = new MutableLiveData<>();

    // Old sku method making this class singleton
    public static MovieRepository getInstance(Application application) {
        if (mInstance == null) {
            mInstance = new MovieRepository(application);

        }
        return mInstance;
    }

    private MovieRepository(Application application) {
        mContext = application;
        mDao = MovieDatabase.getInstance(application).movieDao();
        mMovies = mDao.getAllMovies();
        mService = RetrofitClient.getPopular().create(TmdbWebService.class);
    }

    private void addMovieData(List<MovieEntity> titles) {
        new dbAsyncHelper.insertAsyncTask(mDao).execute(titles);
    }

    public void delAllMovies() {
        new dbAsyncHelper.deleteAsyncTask(mDao).execute();
    }

    public void findMovies(String title) {
        dbAsyncHelper task = new dbAsyncHelper(mDao);
        task.setDelegate(this);
        task.execute("%"+title+"%");
    }

    public boolean checkEndOfList() {
        return isLastPage;
    }

    public LiveData<List<MovieEntity>> getAllMovies() {
        return mMovies;
    }

    public MutableLiveData<List<MovieEntity>> getResults() {
        return mResults;
    }

    public void updateMovies(String apiKey, String language, int page) {
        Call<TmdbReturnModel> call = mService.getMovies(apiKey, language, page);
        call.enqueue(new Callback<TmdbReturnModel>() {
            @Override
            public void onResponse(@NonNull Call<TmdbReturnModel> call,
                                   @NonNull Response<TmdbReturnModel> response) {
                if (response.body() != null) {
                    if (response.body().getResults().size() > 0) {
                        List<MovieEntity> titles = new ArrayList<>();

                        for (MovieModel mModel:
                                response.body().getResults()) {

                            titles.add(new MovieEntity(mModel.getTitle(),
                                    mModel.getPosterPath(),
                                    mModel.getOverview(),
                                    Math.round(mModel.getVoteAverage())));
                        }

                        addMovieData(titles);
                    } else {
                        isLastPage = true;
                        Toast.makeText(mContext, "-- End of List --",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<TmdbReturnModel> call, Throwable t) {
                Toast.makeText(mContext, "Unable to retrieve Movie List",
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void asyncCompleted(List<MovieEntity> results) {
        mResults.setValue(results);
    }

}
