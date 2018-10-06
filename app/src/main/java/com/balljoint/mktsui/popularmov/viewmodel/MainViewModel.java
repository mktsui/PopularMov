package com.balljoin.mktsui.popularmov.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import java.util.List;

import com.balljoin.mktsui.popularmov.database.MovieEntity;
import com.balljoin.mktsui.popularmov.database.MovieRepository;
import com.balljoin.mktsui.popularmov.utilities.Constants;

public class MainViewModel extends AndroidViewModel {
    private MovieRepository mRepository;
    private LiveData<List<MovieEntity>> mMovies;
    private MutableLiveData<List<MovieEntity>> mResults;

    public MainViewModel(@NonNull Application application) {
        super(application);

        mRepository = MovieRepository.getInstance(application);
        mMovies = mRepository.getAllMovies();
        mResults = mRepository.getResults();
    }

    public LiveData<List<MovieEntity>> getAllMovies() {
        return mMovies;
    }

    public MutableLiveData<List<MovieEntity>> getResults() {
        return mResults;
    }

    public void findMovies(String title) {
        mRepository.findMovies(title);
    }

    public void delAllMovies() {
        mRepository.delAllMovies();
    }

    public void updateMovies(int page) {
        mRepository.updateMovies(Constants.TMDB_API_KEY, Constants.TMDB_LANG, page);
    }

    public boolean checkEndofList() {
        return mRepository.checkEndOfList();
    }
}
