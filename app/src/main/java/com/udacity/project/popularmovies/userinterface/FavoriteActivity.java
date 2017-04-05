package com.udacity.project.popularmovies.userinterface;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;

import com.udacity.project.popularmovies.R;
import com.udacity.project.popularmovies.adapter.FavoriteMovieDataAdapter;
import com.udacity.project.popularmovies.persistence.MovieContract;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoriteActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>
        ,FavoriteMovieDataAdapter.OnFavoriteMovieClickListener{

    //Constant defined for logging
    private static final String TAG = FavoriteActivity.class.getSimpleName();

    //Defining minimal projection for the Favorite movie poster URL
    public static final String[] FAVORITE_MOVIE_MINIMAL_PROJECTION = new String[]{
            MovieContract.Movies._ID,
            MovieContract.Movies.MOVIE_POSTER_URL
    };

    //Defining the Cursor index based on minimal projection
    public static final int INDEX_FAVORITE_MOVIE_ID = 0;
    public static final int INDEX_FAVORITE_MOVIE_POSTER_URL = 1;

    //Defining Favorite Movies Content Uri
    private static final Uri FAVORITE_MOVIE_CONTENT_URI = MovieContract.Movies.MOVIES_CONTENT_URI;

    //Defining Favorite Movies Selection Parameter
    private static final String FAVORITE_MOVIE_MINIMAL_SELECTION = MovieContract.Movies.IS_FAVORITE_MOVIE+" =?";

    //Identifier for the Cursor Loader
    private static final int FAVORITE_MOVIE_LOADER_ID = 6874;

    //Binding the View of FavoriteActivity
    @BindView(R.id.recyclerView_activity_favorite_movies)
    public RecyclerView recyclerView_activity_favorite_movies;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Initializing the CursorLoader
        getSupportLoaderManager().initLoader(FAVORITE_MOVIE_LOADER_ID,null,this);

        //Providing the Implementation for deleting a Movie from the Favorites
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                long swipedMovieId = (long) viewHolder.itemView.getTag();
                removeMovieFromFavorites(swipedMovieId);
                getSupportLoaderManager().restartLoader(FAVORITE_MOVIE_LOADER_ID,null,FavoriteActivity.this);
            }
        }).attachToRecyclerView(recyclerView_activity_favorite_movies);
    }



    /**
     * Updates appropriate field of the Movie to
     * @param movieRowId
     */
    public void removeMovieFromFavorites(long movieRowId){
        final Uri REMOVE_FAVORITE_CONTENT_URI = MovieContract.Movies.MOVIES_CONTENT_URI.buildUpon()
                .appendPath(String.valueOf(movieRowId)).build();

        //Defining Async Task to handle the database operations in background thread.
        AsyncTask removeFromFavoriteAsyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                ContentValues removeFromFavoriteContentValues = new ContentValues();
                removeFromFavoriteContentValues.put(MovieContract.Movies.IS_FAVORITE_MOVIE
                        ,getResources().getInteger(R.integer.value_of_non_favorite_movie));
                //Updating the table to remove Movie from favorites.
                getContentResolver().update(REMOVE_FAVORITE_CONTENT_URI
                ,removeFromFavoriteContentValues,null,null);
                return null;
            }
        };
        removeFromFavoriteAsyncTask.execute();
    }

    /**
     * Initializes the Adapter in order to display the Favorite Movie posters in the RecyclerView.
     */
    public void loadFavoriteMovieData(Cursor favoriteMovieDataCursor){
        recyclerView_activity_favorite_movies.setLayoutManager(new GridLayoutManager(FavoriteActivity.this,
                getResources().getInteger(R.integer.movie_poster_number_in_a_row)));
        FavoriteMovieDataAdapter favoriteMoviesAdapter = new FavoriteMovieDataAdapter(this,favoriteMovieDataCursor);
        recyclerView_activity_favorite_movies.setAdapter(favoriteMoviesAdapter);
        favoriteMoviesAdapter.setOnFavoriteMovieClickListener(this);
    }

    /**
     * Providing the implementation for menu items
     * @param item
     * @return /boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedItemId = item.getItemId();
        switch (selectedItemId){
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Creates and returns a CursorLoader with the requested data.
     * @param favoriteMovieLoaderId
     * @param args
     * @return //CursorLoader
     */
    @Override
    public Loader<Cursor> onCreateLoader(int favoriteMovieLoaderId, Bundle args) {
        final String[] FAVORITE_MOVIE_MINIMAL_SELECTION_ARGS = new String[]
                {String.valueOf(getResources().getInteger(R.integer.value_of_favorite_movie))};
        switch (favoriteMovieLoaderId){
            case FAVORITE_MOVIE_LOADER_ID:
                return new CursorLoader(this
                        ,FAVORITE_MOVIE_CONTENT_URI
                        ,FAVORITE_MOVIE_MINIMAL_PROJECTION
                        ,FAVORITE_MOVIE_MINIMAL_SELECTION
                        ,FAVORITE_MOVIE_MINIMAL_SELECTION_ARGS,null);
            default:
                throw new RuntimeException("Loader not implemented: "+favoriteMovieLoaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        loadFavoriteMovieData(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    /**
     * Method onFavoriteMovieClick is declared in the Callback interface OnFavoriteMovieClickListener in the FavoriteMovieDataAdapter class.
     * FavoriteActivity needs to provide the implementation for the callback method onFavoriteMovieClick to define what happens upon
     * clicking a particular card on the FavoriteActivity.
     * @param favoriteMovieId
     */
    @Override
    public void onFavoriteMovieClick(long favoriteMovieId) {
        Uri favoriteMovieDetailUri = MovieContract.Movies.MOVIES_CONTENT_URI.buildUpon()
                .appendPath(String.valueOf(favoriteMovieId)).build();
        Intent favoriteMovieDetailIntent = new Intent(this,DetailActivity.class);
        favoriteMovieDetailIntent.setData(favoriteMovieDetailUri);
        startActivity(favoriteMovieDetailIntent);
    }
}
