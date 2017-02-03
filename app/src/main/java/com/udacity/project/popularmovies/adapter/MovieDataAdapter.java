package com.udacity.project.popularmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.udacity.project.popularmovies.R;
import com.udacity.project.popularmovies.model.Movie;
import java.util.List;

/**
 * Adapter class for the RecyclerView
 * Created by chandan on 23/01/2017.
 */

public class MovieDataAdapter extends RecyclerView.Adapter<MovieDataAdapter.MovieDataViewHolder>{

    private List<Movie> movieList;
    private Context context;
    private OnMovieCardClickListener onMovieCardClickListener;

    /**
     * Constructor for creating instances of MovieDataAdapter
     * @param movieList
     */
    public MovieDataAdapter(List<Movie> movieList,Context context) {
        this.movieList = movieList;
        this.context = context;
    }

    /**Defining Callback interface to implement the click event*/
    public interface OnMovieCardClickListener{
        public void onMovieCardClick(int position);
    }

    /**Setter method for OnMovieCardClickListener*/
    public void setOnMovieCardClickListener(OnMovieCardClickListener onMovieCardClickListener) {
        this.onMovieCardClickListener = onMovieCardClickListener;
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
    public MovieDataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        boolean inflatedLayoutToBeAttachedToParent = false;
        View view = layoutInflater.inflate(R.layout.movie_card,parent,inflatedLayoutToBeAttachedToParent);
        MovieDataViewHolder movieDataViewHolder = new MovieDataViewHolder(view);
        return movieDataViewHolder;
    }

    /**
     * Overridden method onBindViewHolder binds each of the items to be displayed using the RecyclerView with data and
     * upon recycling ViewHolder instances this method also binds them with new data.
     * @param holder // instance of the ViewHolder to be bound with data
     * @param position //position of the ViewHolder instance in the collection of data being displayed by the RecyclerView
     */
    @Override
    public void onBindViewHolder(MovieDataViewHolder holder, int position) {
        Movie movieItem = movieList.get(position);
        Picasso.with(context).setLoggingEnabled(true);
        Picasso.with(context)
                .load(movieItem.getMoviePosterUrl())
                .placeholder(R.drawable.placeholder_small_stacked_blue)
                .error(R.drawable.placeholder_small_stacked_blue)
                .into(holder.imageView_movie_card_moviePoster);
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }


    /*Inner class for hosting the ViewHolder which will be necessary for the Adapter to create and bind data to the
    * RecyclerView. ViewHolder class helps in creating the instance of the ViewItems once and recycle them.*/
    class MovieDataViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView imageView_movie_card_moviePoster;
        private View cardView_movie_card_movieItem;

        public MovieDataViewHolder(View itemView) {
            super(itemView);
            imageView_movie_card_moviePoster = (ImageView)itemView.findViewById(R.id.imageView_movie_card_moviePoster);
            cardView_movie_card_movieItem = itemView.findViewById(R.id.cardView_movie_card_movieItem);
            cardView_movie_card_movieItem.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            onMovieCardClickListener.onMovieCardClick(adapterPosition);
        }
    }
}
