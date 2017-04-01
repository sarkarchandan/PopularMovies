package com.udacity.project.popularmovies.service;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.udacity.project.popularmovies.model.Movie;
import com.udacity.project.popularmovies.model.MovieReview;
import com.udacity.project.popularmovies.model.MovieTrailer;
import com.udacity.project.popularmovies.persistence.MovieContract;
import com.udacity.project.popularmovies.utilities.DataProcessingUtils;
import com.udacity.project.popularmovies.utilities.MovieDataUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Defines the tasks that must be performed by the Service in the background
 * Created by chandan on 01.04.17.
 */
public class DataPopulationTasks {

    //Constant for basic logging.
    private static final String TAG = DataPopulationTasks.class.getSimpleName();

    public static final String ACTION_POPULATE_MOVIE_DATA = "populate-movie-data";

    /*Popular movie specific constant*/
    public static final String POPULAR = "popular";
    /*Popular movie specific constant*/
    public static final String TOP_RATED = "top_rated";

    /**
     * Fetches Data from the TMDB V3 API and persists in the database after checking for pre-existing data
     * @param context // Application context
     * @param movieCategory //Category of the movie, typically popular and top_rated
     */
    public static void populateMovieData(Context context,String movieCategory){

        /*String constant array for checking whether a Movie is already in the favorites*/
        String[] EXISTING_MOVIE_CHECK_PROJECTION = new String[]{MovieContract.Movies.MOVIE_ORIGINAL_TITLE};

        //Getting the ContentResolver instance
        ContentResolver contentResolver = context.getContentResolver();

        //Getting the Required Uri(s)
        Uri moviesContentUri = MovieContract.Movies.MOVIES_CONTENT_URI;
        Uri trailersContentUri = MovieContract.Trailers.TRAILERS_CONTENT_URI;
        Uri reviewsContentUri = MovieContract.Reviews.REVIEWS_CONTENT_URI;

        //Populating data for the Movies after checking for the pre-existing entry.
        URL movieDataURL = MovieDataUtils.buildPopularMovieDataRequestURL(movieCategory);
        try {
            List<Movie> movieList = DataProcessingUtils.processRawMovieData(
                    MovieDataUtils.fetchMovieDataFromHttpUrl(movieDataURL));

            //Iterating through the collection of Movies and inserting data
            for(Movie movie: movieList){
                //Checking if the movie already exists in the database
                String movieTitle = movie.getMovieOriginalTitle();
                Uri existingMovieCheckUri = moviesContentUri.buildUpon().appendPath(movieTitle).build();
                Cursor existingMovieCheckCursor = contentResolver.query(existingMovieCheckUri
                ,EXISTING_MOVIE_CHECK_PROJECTION
                ,null
                ,null
                ,null);

                //Only insert the movie record to the database if the movie data is not pre-existing
                if(existingMovieCheckCursor.getCount() == 0){

                    //Initializing and wrapping the ContentValues for inserting to movies directory
                    ContentValues movieContentValues = new ContentValues();
                    movieContentValues.put(MovieContract.Movies.MOVIE_TMDB_ID,movie.getMovieTMDBId());
                    movieContentValues.put(MovieContract.Movies.MOVIE_ORIGINAL_TITLE,movie.getMovieOriginalTitle());
                    movieContentValues.put(MovieContract.Movies.MOVIE_TMDB_TYPE,movieCategory);
                    movieContentValues.put(MovieContract.Movies.MOVIE_POSTER_URL,movie.getMoviePosterUrl());
                    movieContentValues.put(MovieContract.Movies.MOVIE_BACKDROP_URL,movie.getMovieBackDropUrl());
                    movieContentValues.put(MovieContract.Movies.MOVIE_WEB_URL,movie.getMovieWebUrl());
                    movieContentValues.put(MovieContract.Movies.MOVIE_PLOT_SYNOPSIS,movie.getMoviePlotSynopsis());
                    movieContentValues.put(MovieContract.Movies.MOVIE_RATING,movie.getMovieRating());
                    movieContentValues.put(MovieContract.Movies.MOVIE_RELEASE_DATE,movie.getMovieReleaseDate());
                    //Persisting data to the movies directory
                    contentResolver.insert(moviesContentUri,movieContentValues);

                    //Iterating through the collection MovieTrailers and inserting data
                    for(MovieTrailer movieTrailer: movie.getMovieTrailers()){
                        //Initializing and wrapping ContentValues for inserting to trailers directory
                        ContentValues trailerContentValues = new ContentValues();
                        trailerContentValues.put(MovieContract.Trailers.TRAILER_TMDB_ID,movieTrailer.getMovieTrailerTMDBId());
                        trailerContentValues.put(MovieContract.Trailers.MOVIE_TMDB_ID,movie.getMovieTMDBId());
                        trailerContentValues.put(MovieContract.Trailers.TRAILER_URL,movieTrailer.getMovieTrailerURL());
                        //Persisting data to the trailers directory
                        contentResolver.insert(trailersContentUri,trailerContentValues);
                    }

                    //Iterating through the collection of MovieReviews and inserting data
                    for(MovieReview movieReview: movie.getMovieReviews()){
                        //Initializing and wrapping ContentValues for inserting to reviews directory
                        ContentValues reviewsContentValues = new ContentValues();
                        reviewsContentValues.put(MovieContract.Reviews.REVIEW_TMDB_ID,movieReview.getMovieReviewTMDBId());
                        reviewsContentValues.put(MovieContract.Reviews.MOVIE_TMDB_ID,movie.getMovieTMDBId());
                        reviewsContentValues.put(MovieContract.Reviews.REVIEW_AUTHOR,movieReview.getMovieReviewAuthor());
                        reviewsContentValues.put(MovieContract.Reviews.REVIEW_CONTENT,movieReview.getMovieReviewContent());
                        //Persisting data to the reviews directory
                        contentResolver.insert(reviewsContentUri,reviewsContentValues);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Executes the method populateMovieData() for both category of movies i.e. POPULAR and TOP_RATED if the passed in action
     * matches the defined action.
     * @param context // Passed in application context
     * @param action // Passed in action
     */
    public static void executeTask(final Context context, String action){
        boolean deviceNetworkStatus = false;

        //Defining an background task to check the network status.
        AsyncTask connectivityCheckAsyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                boolean networkStatus = MovieDataUtils.checkConnectivityStatus(context);
                return new Boolean(networkStatus);
            }
        };

        //Checking network status.
        try {
            deviceNetworkStatus = (boolean) connectivityCheckAsyncTask.execute().get();
            Log.d(TAG, "Current Network Status: "+deviceNetworkStatus);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        //Service will only be executed in case there is a connectivity. Otherwise it might result into stale data.
        if(action.equals(ACTION_POPULATE_MOVIE_DATA)){
            if(deviceNetworkStatus){
                populateMovieData(context,POPULAR);
                populateMovieData(context,TOP_RATED);
                Log.d(TAG,"DataPopulationService Executed");
            }else {
                Log.d(TAG, "Device is not connected to Internet. Might result to stale data.");
            }
        }
    }
}
