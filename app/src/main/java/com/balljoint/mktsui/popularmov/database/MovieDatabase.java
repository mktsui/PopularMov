package com.balljoin.mktsui.popularmov.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {MovieEntity.class}, version = 1, exportSchema = false)
public abstract class MovieDatabase extends RoomDatabase {
    private static volatile MovieDatabase instance;
    public abstract MovieDao movieDao();

    // Declaring DB
    private static final String DATABASE_NAME = "MovieDatabase.db";
    private static final Object LOCK = new Object();

    public static MovieDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                            MovieDatabase.class,
                            DATABASE_NAME).
                            build();
                }
            }
        }
        return instance;
    }
}
