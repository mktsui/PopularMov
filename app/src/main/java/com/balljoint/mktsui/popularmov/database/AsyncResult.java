package com.balljoin.mktsui.popularmov.database;

import java.util.List;

public interface AsyncResult {
    void asyncCompleted(List<MovieEntity> results);
}
