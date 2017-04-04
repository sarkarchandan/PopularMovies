package com.udacity.project.popularmovies.adapter;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.udacity.project.popularmovies.R;
import com.udacity.project.popularmovies.userinterface.DetailActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter class for the RecyclerView in the DetailActivity dedicated for the MovieTrailer
 * Created by chandan on 03.04.17.
 */
public class MovieTrailerResourceAdapter extends RecyclerView.Adapter<MovieTrailerResourceAdapter.MovieTrailerItemViewHolder> {

    //Defining Uri component parameters for getting YouTube video thumbnail
    private static final String YOUTUBE_VIDEO_THUMBNAIL_BASE_URI = "http://img.youtube.com/vi/";
    private static final String YOUTUBE_VIDEO_THUMBNAIL_PARAMETER = "0.jpg";

    private Cursor movieTrailerCursor;
    private Context context;

    public MovieTrailerResourceAdapter(Cursor movieTrailerCursor, Context context) {
        this.movieTrailerCursor = movieTrailerCursor;
        this.context = context;
    }

    @Override
    public MovieTrailerItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.movie_trailer_item_card
                ,parent
                ,context.getResources().getBoolean(R.bool.inflatedLayoutToBeAttachedToParent));
        return new MovieTrailerItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieTrailerItemViewHolder holder, int position) {
        movieTrailerCursor.moveToPosition(position);

        String youTubeVideoId = movieTrailerCursor.getString(DetailActivity.INDEX_MOVIE_TRAILER_YOUTUBE_KEY);

        Uri movieTrailerThumbnailUri = Uri.parse(YOUTUBE_VIDEO_THUMBNAIL_BASE_URI).buildUpon()
                .appendPath(youTubeVideoId)
                .appendPath(YOUTUBE_VIDEO_THUMBNAIL_PARAMETER).build();

        Picasso.with(context)
                .setLoggingEnabled(true);
        Picasso.with(context)
                .load(movieTrailerThumbnailUri)
                .placeholder(R.drawable.placeholder_small_stacked_blue)
                .error(R.drawable.placeholder_small_stacked_blue)
                .into(holder.imageView_movie_trailer_thumbnail);
    }

    @Override
    public int getItemCount() {
        return movieTrailerCursor.getCount();
    }

    public class MovieTrailerItemViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.cardView_movie_trailer_item_container)
        public CardView cardView_movie_trailer_item_container;
        @BindView(R.id.imageView_movie_trailer_thumbnail)
        public ImageView imageView_movie_trailer_thumbnail;

        public MovieTrailerItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
