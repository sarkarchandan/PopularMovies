package com.udacity.project.popularmovies.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.project.popularmovies.R;
import com.udacity.project.popularmovies.userinterface.DetailActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter class for the RecyclerView in the DetailActivity dedicated for the MovieReview
 * Created by chandan on 03.04.17.
 */
public class MovieReviewResourceAdapter extends RecyclerView.Adapter<MovieReviewResourceAdapter.MovieReviewItemViewHolder>{

    private Cursor movieReviewCursor;
    private Context context;

    public MovieReviewResourceAdapter(Cursor movieReviewCursor, Context context) {
        this.movieReviewCursor = movieReviewCursor;
        this.context = context;
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
    public MovieReviewItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.movie_review_item_card
                ,parent
                ,context.getResources().getBoolean(R.bool.inflatedLayoutToBeAttachedToParent));
        return new MovieReviewItemViewHolder(view);
    }

    /**
     * Overridden method onBindViewHolder binds each of the items to be displayed using the RecyclerView with data and
     * upon recycling ViewHolder instances this method also binds them with new data.
     * @param holder // instance of the ViewHolder to be bound with data
     * @param position //position of the ViewHolder instance in the collection of data being displayed by the RecyclerView
     */
    @Override
    public void onBindViewHolder(MovieReviewItemViewHolder holder, int position) {
        movieReviewCursor.moveToPosition(position);

        String reviewAuthor = movieReviewCursor.getString(DetailActivity.INDEX_MOVIE_REVIEW_AUTHOR);
        String reviewContent = movieReviewCursor.getString(DetailActivity.INDEX_MOVIE_REVIEW_CONTENT);
        if(movieReviewCursor == null){
            holder.textView_movie_review_item_review_author.setText(context.getString(R.string.say_sorry));
            holder.textView_movie_review_item_review_content.setText(context.getString(R.string.no_review_available));
        }else {
            holder.textView_movie_review_item_review_author.setText(reviewAuthor+" "+context.getString(R.string.author_says));
            holder.textView_movie_review_item_review_content.setText(reviewContent);
        }


    }

    /**
     * Returns the displayable ItemCount of the data source
     * @return // Integer
     */
    @Override
    public int getItemCount() {
        return movieReviewCursor.getCount();
    }

    public class MovieReviewItemViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.cardView_movie_review_item_container)
        public CardView cardView_movie_review_item_container;
        @BindView(R.id.textView_movie_review_item_review_author)
        public TextView textView_movie_review_item_review_author;
        @BindView(R.id.textView_movie_review_item_review_content)
        public TextView textView_movie_review_item_review_content;

        public MovieReviewItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
