package com.balljoin.mktsui.popularmov;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.balljoin.mktsui.popularmov.database.MovieEntity;
import com.balljoin.mktsui.popularmov.ui.MovieAdapter;
import com.balljoin.mktsui.popularmov.utilities.Constants;
import com.balljoin.mktsui.popularmov.viewmodel.MainViewModel;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private MovieAdapter mAdapter;
    private MainViewModel mViewModel;
    private GridLayoutManager mLayoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<MovieEntity> mMovieList = new ArrayList<>();
    private TextView mEmptyView;
    private boolean mListPopulated = false;
    private boolean isLoading = false;
    private boolean isSearching = false;
    private boolean isLastPage = false;
    private int moviePageCount = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAdapter = new MovieAdapter(MainActivity.this);
        mEmptyView = (TextView) findViewById(R.id.empty_placeholder);

        initRecyclerView();
        initViewModel();

        if (savedInstanceState != null) {
            // prevent reload on orientation change
            mListPopulated = savedInstanceState.getBoolean(Constants.LIST_POPULATED_KEY);
        }

        if (!mListPopulated){
            // to make sure the list is fresh every time the app starts
            reLoadMovies();
        }

        // pull down refresh listener
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_view);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isSearching) {
                    reLoadMovies();
                }
            }
        });

        // scroll load more listener
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (!isSearching) {
                    super.onScrolled(recyclerView, dx, dy);

                    int totalItemCount = mLayoutManager.getItemCount();
                    int lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();

                    // last item of totalItemCount is progressbar placeholder
                    if (!isLoading && totalItemCount - 1 <= lastVisibleItem) {
                        requestData(++moviePageCount);
                    }
                }
            }
        });
    }

    private boolean showLoadMoreProgressbar() {
        return !(isSearching || !mListPopulated || isLastPage);
    }

    private void reLoadMovies() {
        mViewModel.delAllMovies();
        moviePageCount = 1;
        requestData(moviePageCount);
    }

    private void requestData(int page) {
        isLastPage = mViewModel.checkEndofList();
        mViewModel.updateMovies(page);
        isLoading = true;
    }

    private void findMovies(String title) {
        mViewModel.findMovies(title);
    }

    private void showAllMovies() {
        mAdapter.setMovies(mMovieList, showLoadMoreProgressbar());
        mEmptyView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void initViewModel() {
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        // observer for full movie list
        mViewModel.getAllMovies().observe(this, new Observer<List<MovieEntity>>() {
            @Override
            public void onChanged(@Nullable List<MovieEntity> movieEntities) {
                mMovieList.clear();
                mMovieList.addAll(movieEntities);

                // make a placeholder to put progressbar
                movieEntities.add(new MovieEntity());

                mAdapter.setMovies(movieEntities, showLoadMoreProgressbar());

                // update status after list is loaded
                if (mMovieList.size() != 0) {
                   findViewById(R.id.main_progress).setVisibility(View.GONE);
                   mListPopulated = true;
                   isLoading = false;
                }

                // Dismiss refresh icon
                if (swipeRefreshLayout != null) {
                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
            }
        });

        // observer for search results
        mViewModel.getResults().observe(this, new Observer<List<MovieEntity>>() {
            @Override
            public void onChanged(@Nullable List<MovieEntity> movieEntities) {
                if (movieEntities.size() > 0) {
                    // progress bar placeholder for result display
                    movieEntities.add(new MovieEntity());
                    mAdapter.setMovies(movieEntities, false);
                    mEmptyView.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                } else {
                    // no result
                    mRecyclerView.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void initRecyclerView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
        // span grid size for progress bar
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int i) {
                switch (mAdapter.getItemViewType(i)){
                    case MovieAdapter.LIST_ITEM:
                        return 1;
                    case MovieAdapter.LOADING:
                        return 2;
                    default:
                        return 0;
                }
            }
        });
        mLayoutManager = gridLayoutManager;
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                findMovies(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        MenuItem searchMenu = menu.findItem(R.id.search);
        searchMenu.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                isSearching = true;
                swipeRefreshLayout.setEnabled(false);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                isSearching = false;
                swipeRefreshLayout.setEnabled(true);
                showAllMovies();
                return true;
            }
        });

        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(Constants.LIST_POPULATED_KEY, mListPopulated);
        super.onSaveInstanceState(outState);
    }
}
