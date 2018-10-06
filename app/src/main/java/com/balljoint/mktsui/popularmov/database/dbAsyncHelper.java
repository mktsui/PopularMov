package com.balljoin.mktsui.popularmov.database;

import android.os.AsyncTask;

import java.util.List;

public class dbAsyncHelper extends AsyncTask<String, Void, List<MovieEntity>>{
    private MovieDao asyncTaskDao;
    private MovieRepository delegate = null;

    public MovieRepository getDelegate() {
        return delegate;
    }

    public void setDelegate(MovieRepository delegate) {
        this.delegate = delegate;
    }

    dbAsyncHelper(MovieDao dao) {
        asyncTaskDao = dao;
    }

    @Override
    protected List<MovieEntity> doInBackground(final String... params) {
            return asyncTaskDao.getMovies(params[0]);
        }

    @Override
    protected void onPostExecute(List<MovieEntity> result) {
            delegate.asyncCompleted(result);
        }

    public static class insertAsyncTask extends AsyncTask<List<MovieEntity>, Void, Void> {

        private MovieDao asyncTaskDao;

        insertAsyncTask(MovieDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final List<MovieEntity>... params) {
            asyncTaskDao.insertAll(params[0]);
            return null;
        }
    }

    public static class deleteAsyncTask extends AsyncTask<Void, Void, Void> {

        private MovieDao asyncTaskDao;

        deleteAsyncTask(MovieDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Void... params) {
            asyncTaskDao.deleteAll();
            return null;
        }
    }
}