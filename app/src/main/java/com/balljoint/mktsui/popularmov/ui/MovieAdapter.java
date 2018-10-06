package com.balljoin.mktsui.popularmov.ui;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import com.balljoin.mktsui.popularmov.R;
import com.balljoin.mktsui.popularmov.database.MovieEntity;
import com.balljoin.mktsui.popularmov.utilities.Constants;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class MovieAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int LIST_ITEM = 0;
    public static final int LOADING = 1;

    private List<MovieEntity> mMovies;
    private final Context mContext;
    private boolean showProgressbar;

    public MovieAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setMovies(List<MovieEntity> mMovies, boolean showProgressbar) {
        this.mMovies = mMovies;
        this.showProgressbar = showProgressbar;
        notifyDataSetChanged();
    }

    private static class ListItemView extends RecyclerView.ViewHolder {

        TextView movieTitle;
        ImageView movieRating;
        ImageView moviePoster;
        CardView cardView ;

        ListItemView(View itemView) {
            super(itemView);

            movieTitle = (TextView) itemView.findViewById(R.id.movie_title);
            movieRating = (ImageView) itemView.findViewById(R.id.movie_rating);
            moviePoster = (ImageView) itemView.findViewById(R.id.movie_poster);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
        }
    }

    private class LoadingView extends RecyclerView.ViewHolder {
        ProgressBar progressBar;
        LoadingView(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.list_progress);
        }
    }

    private int getRatingImg (int rating) {
        int ratingR;
        switch (rating) {
            case 1:
                ratingR = R.drawable.rating_1;
                break;
            case 2:
                ratingR = R.drawable.rating_1;
                break;
            case 3:
                ratingR = R.drawable.rating_2;
                break;
            case 4:
                ratingR = R.drawable.rating_2;
                break;
            case 5:
                ratingR = R.drawable.rating_3;
                break;
            case 6:
                ratingR = R.drawable.rating_3;
                break;
            case 7:
                ratingR = R.drawable.rating_4;
                break;
            case 8:
                ratingR = R.drawable.rating_4;
                break;
            case 9:
                ratingR = R.drawable.rating_5;
                break;
            case 10:
                ratingR = R.drawable.rating_5;
                break;
            default:
                ratingR = R.drawable.rating_5;
                break;
        }
        return ratingR;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder vh;

        if (viewType == LOADING) {
            View mView = LayoutInflater.from(mContext).
                    inflate(R.layout.list_progress, viewGroup, false);
            vh = new LoadingView(mView);
        } else {
            View mView = LayoutInflater.from(mContext).
                    inflate(R.layout.movie_card_item, viewGroup, false);
            vh = new ListItemView(mView);
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        final MovieEntity movie = mMovies.get(i);

        if (viewHolder instanceof LoadingView){
            LoadingView lVh = (LoadingView) viewHolder;

            if (showProgressbar) {
                lVh.progressBar.setVisibility(View.VISIBLE);
            } else {
                lVh.progressBar.setVisibility(View.GONE);
            }
        } else {
            try {
                String url = Constants.API_PSOTER_BASE_URL + movie.getPosterPath();
                Picasso.get()
                        .load(url)
                        .fit()
                        .centerCrop()
                        .transform(new RoundedCornersTransformation(30, 0))
                        .placeholder(R.drawable.ic_image)
                        .into(((ListItemView)viewHolder).moviePoster);
            } catch (Exception e) {
                e.printStackTrace();
            }

            ListItemView mVh = (ListItemView) viewHolder;

            String displayTitle;
            String originalTitle = movie.getTitle();
            if (originalTitle.length() > 30) {
                displayTitle = originalTitle.substring(0, 30) + "...";
            } else {
                displayTitle = originalTitle;
            }
            mVh.movieTitle.setText(displayTitle);
            Picasso.get()
                    .load(getRatingImg(movie.getRating()))
                    .into(((ListItemView)viewHolder).movieRating);
        }
    }

    @Override
    public int getItemCount() {
        return mMovies == null ? 0 : mMovies.size();
    }

    @Override
    public int getItemViewType(int position){
        return mMovies.size()-1 == position? LOADING : LIST_ITEM;
    }

}
