package com.udacity.project.popularmovies.adapter;


import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.udacity.project.popularmovies.R;
import com.udacity.project.popularmovies.userinterface.FavoriteActivity;
import com.udacity.project.popularmovies.utilities.MovieDataUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter class for the RecyclerView in the FavoriteActivity
 * Created by chandan on 02.04.17.
 */
public class FavoriteMovieDataAdapter extends RecyclerView.Adapter<FavoriteMovieDataAdapter.FavoriteMoviesViewHolder>{

    private Context context;
    private Cursor favoriteMovieCursor;
    private OnFavoriteMovieClickListener onFavoriteMovieClickListener;

    public FavoriteMovieDataAdapter(Context context, Cursor favoriteMovieCursor) {
        this.context = context;
        this.favoriteMovieCursor = favoriteMovieCursor;
    }

    public interface OnFavoriteMovieClickListener{
        public void onFavoriteMovieClick(long favoriteMovieId);
    }

    public void setOnFavoriteMovieClickListener(OnFavoriteMovieClickListener onFavoriteMovieClickListener) {
        this.onFavoriteMovieClickListener = onFavoriteMovieClickListener;
    }

    /**
     * Overridden method onCreateViewHolder inflates the layout for the items to be displayed by the RecyclerView.
     * We will create number of items once and recycle them using the onBindViewHolder method. This method returns
     * a number of ViewHolder instances.
     * @param parent // reference of the ViewGroup
     * @param viewType // Type of the View
     * @return ViewHolder instances.
     */
    @Override
    public FavoriteMoviesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        boolean inflatedLayoutToBeAttachedToParent = false;
        View view = layoutInflater.inflate(R.layout.favorite_movie_card,parent,inflatedLayoutToBeAttachedToParent);
        return new FavoriteMoviesViewHolder(view);
    }

    /**
     * Overridden method onBindViewHolder binds each of the items to be displayed using the RecyclerView with data and
     * upon recycling ViewHolder instances this method also binds them with new data.
     * @param holder // instance of the ViewHolder to be bound with data
     * @param position //position of the ViewHolder instance in the collection of data being displayed by the RecyclerView
     */
    @Override
    public void onBindViewHolder(FavoriteMoviesViewHolder holder, int position) {
        favoriteMovieCursor.moveToPosition(position);
        String favoriteMoviePosterURL = favoriteMovieCursor.getString(FavoriteActivity.INDEX_FAVORITE_MOVIE_POSTER_URL);
        long favoriteMovieRowId = favoriteMovieCursor.getLong(FavoriteActivity.INDEX_FAVORITE_MOVIE_ID);

        Picasso.with(context).setLoggingEnabled(true);
        Picasso.with(context).load(favoriteMoviePosterURL)
                .placeholder(R.drawable.placeholder_large_stacked_blue)
                .error(R.drawable.placeholder_large_stacked_blue)
                .into(holder.image_view_favorite_activity_movie_poster);

        holder.itemView.setTag(favoriteMovieRowId);
    }

    /**
     * Returns the displayable ItemCount of the data source
     * @return // Integer
     */
    @Override
    public int getItemCount() {
        return favoriteMovieCursor.getCount();
    }


    /*Inner class for hosting the ViewHolder which will be necessary for the Adapter to create and bind data to the
    * RecyclerView. ViewHolder class helps in creating the instance of the ViewItems once and recycle them.*/
    public class FavoriteMoviesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.card_view_favorite_activity_movie_poster)
        public CardView card_view_favorite_activity_movie_poster;
        @BindView(R.id.image_view_favorite_activity_movie_poster)
        public ImageView image_view_favorite_activity_movie_poster;

        public FavoriteMoviesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            favoriteMovieCursor.moveToPosition(getAdapterPosition());
            onFavoriteMovieClickListener.onFavoriteMovieClick(favoriteMovieCursor.getLong(FavoriteActivity.INDEX_FAVORITE_MOVIE_ID));
        }
    }
}
