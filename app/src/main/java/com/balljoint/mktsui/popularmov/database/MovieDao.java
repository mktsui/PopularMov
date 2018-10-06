package com.balljoin.mktsui.popularmov.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<MovieEntity> movies);

    @Query("SELECT * FROM movies WHERE title LIKE :movieTitle")
    List<MovieEntity> getMovies(String movieTitle);

    @Query("SELECT * FROM movies ORDER BY id")
    LiveData<List<MovieEntity>> getAllMovies();

    @Query("DELETE FROM movies")
    int deleteAll();

    @Query("SELECT COUNT(*) FROM movies")
    int getCount();
}
