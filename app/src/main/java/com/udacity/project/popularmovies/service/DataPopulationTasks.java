package com.udacity.project.popularmovies.service;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.udacity.project.popularmovies.model.Movie;
import com.udacity.project.popularmovies.model.MovieReview;
import com.udacity.project.popularmovies.model.MovieTrailer;
import com.udacity.project.popularmovies.persistence.MovieContract;
import com.udacity.project.popularmovies.userinterface.DetailActivity;
import com.udacity.project.popularmovies.utilities.MovieDataUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URL;

/**
 * Defines the tasks that must be performed by the Service in the background.
 * This tasks include contacting the API source and fetch data, process data and persist data conditionally.
 * Created by chandan on 01.04.17.
 */
public class DataPopulationTasks {

    //Constant for basic logging.
    private static final String TAG = DataPopulationTasks.class.getSimpleName();

    //Defining the ACTION constant for the task of populating the generic data for the Movies
    public static final String ACTION_POPULATE_MOVIE_DATA = "populate-movie-data";

    //Defining the ACTION constant for the task of populating the review and trailer data for the movies
    public static final String ACTION_POPULATE_MOVIE_RESOURCES = "populate-movie-resources";

    /*Popular movie specific constant*/
    public static final String POPULAR = "popular";
    /*Top Rated movie specific constant*/
    public static final String TOP_RATED = "top_rated";

    //Movie Review parameter constant
    private static final String MOVIE_SPECIFIC_REVIEW_PARAMETER = "reviews";
    //Movie Video parameter constant
    private static final String MOVIE_SPECIFIC_VIDEO_PARAMETER = "videos";

    //Constants for parsing Movie related JSON Data
    private static final String MOVIE_COLLECTION = "results";
    private static final String MOVIE_ID = "id";
    private static final String MOVIE_POSTER = "poster_path";
    private static final String MOVIE_BACKDROP = "backdrop_path";
    private static final String MOVIE_ORIGINAL_TITLE = "original_title";
    private static final String MOVIE_PLOT_SYNOPSIS = "overview";
    private static final String MOVIE_RATING = "vote_average";
    private static final String MOVIE_RELEASE_DATE = "release_date";
    private static final String MOVIE_DB_BASE_URL = "https://www.themoviedb.org/movie";
    private static final String MOVIE_POSTER_BASE_URL = "http://image.tmdb.org/t/p";
    private static final String MOVIE_POSTER_SIZE_PARAM = "w342";
    private static final String MOVIE_BACKDROP_SIZE_PARAM = "w780";
    //Constants for parsing Review related JSON Data
    private static final String REVIEW_COLLECTION = "results";
    private static final String REVIEW_TMDB_ID = "id";
    private static final String REVIEW_AUTHOR = "author";
    private static final String REVIEW_CONTENT = "content";
    //Constants for parsing Video related JSON Data
    private static final String VIDEO_COLLECTION = "results";
    private static final String VIDEO_TMDB_ID = "id";
    private static final String VIDEO_TYPE = "type";
    private static final String VIDEO_KEY = "key";
    private static final String VIDEO_TRAILER_KEY = "Trailer";

    //Declaring constants for the required Uris
    private static final Uri moviesContentUri = MovieContract.Movies.MOVIES_CONTENT_URI;
    private static final Uri trailersContentUri = MovieContract.Trailers.TRAILERS_CONTENT_URI;
    private static final Uri reviewsContentUri = MovieContract.Reviews.REVIEWS_CONTENT_URI;

    //Minimal projection for checking whether a Movie is already in the favorites
    private static final String[] EXISTING_MOVIE_CHECK_PROJECTION = new String[]{MovieContract.Movies.MOVIE_ORIGINAL_TITLE};

    //Minimal projection for retrieving TMDB Id for all the movies
    private static final String[] PERSISTED_MOVIE_TMDBID_PROJECTION = new String[]{MovieContract.Movies.MOVIE_TMDB_ID};

    //Defining the Index based on the minimal projection of TMDB Ids
    private static final int INDEX_MOVIE_TMDB_ID = 0;

    private static int persistedPopularMovieCount = 0;
    private static int persistedTopRatedMovieCount = 0;


    /**
     * Checks and returns boolean true or false depending on whether the Movie is pre-existing or not.
     * @param context
     * @param movieTMDBTitle
     * @return
     */
    private static boolean isExistingMovie(Context context,String movieTMDBTitle){
        //Defining the Uri for checking for existing Movie with the passed in Movie TMDB Title
        Uri checkExistingMovieUri = moviesContentUri.buildUpon()
                .appendPath(movieTMDBTitle)
                .build();

        //Getting the ContentResolver instance
        ContentResolver contentResolver = context.getContentResolver();

        Cursor existingMovieCheckCursor = contentResolver.query(checkExistingMovieUri
                ,EXISTING_MOVIE_CHECK_PROJECTION
                ,null
                ,null
                ,null);
        if(existingMovieCheckCursor.getCount() == 0){
            return false;
        }else {
            return true;
        }
    }

    /**
     * Checks and returns boolean true or false depending on whether the Trailer is pre-existing or not.
     * @param context //Application Context
     * @param movieTMDBTrailerID //String TMDB Trailer ID
     * @return //boolean
     */
    private static boolean isExistingTrailer(Context context, String movieTMDBTrailerID){
        //Getting the ContentResolver instance
        ContentResolver contentResolver = context.getContentResolver();

        //Defining Uri to check if a given trailer in existing
        Uri isExistingTrailerUri = MovieContract.Trailers.TRAILERS_CONTENT_URI.buildUpon()
                .appendPath(movieTMDBTrailerID)
                .build();

        //Defining minimal projection
        String[] EXISTING_TRAILER_MINIMAL_PROJECTION = new String[]{
                MovieContract.Trailers.MOVIE_TRAILER_YOUTUBE_KEY};

        Cursor existingMovieTrailerCursor = contentResolver.query(isExistingTrailerUri
                ,EXISTING_TRAILER_MINIMAL_PROJECTION
                ,null
                ,null
                ,null);
        if(existingMovieTrailerCursor.getCount() == 0){
            return false;
        }else {
            return true;
        }
    }

    /**
     * Checks and returns boolean true or false depending on whether the Review is pre-existing or not.
     * @param context //Application Context
     * @param movieTMDBReviewId //String TMDB Review ID
     * @return //boolean
     */
    private static boolean isExistingReview(Context context, String movieTMDBReviewId){
        //Getting the ContentResolver instance
        ContentResolver contentResolver = context.getContentResolver();

        //Defining Uri to check if a given Review is existing
        Uri isExistingReviewUri = MovieContract.Reviews.REVIEWS_CONTENT_URI.buildUpon()
                .appendPath(movieTMDBReviewId)
                .build();

        String[] EXISTING_REVIEW_MINIMAL_PROJECTION = new String[]{
                MovieContract.Reviews.REVIEW_AUTHOR};

        Cursor existingMovieReviewCursor = contentResolver.query(isExistingReviewUri
                ,EXISTING_REVIEW_MINIMAL_PROJECTION
                ,null
                ,null
                ,null);
        if(existingMovieReviewCursor.getCount() == 0){
            return false;
        }else {
            return true;
        }
    }

    /**
     * Persists a Movie in the movies table in the database
     * @param context
     * @param response //JSONObject
     */
    private static void persistMovie(Context context, JSONObject response, String movieCategory){

        //Getting the ContentResolver instance
        ContentResolver contentResolver = context.getContentResolver();
        int persistedMovieCount = 0;
        if(response !=null){
            try {
                //Constructing the JSON Array of Movie related data
                JSONArray movieDataArray = response.getJSONArray(MOVIE_COLLECTION);

                //Iterating through the array of Movie Objects
                for (int i = 0; i < movieDataArray.length(); i++) {
                    JSONObject movieDataObject = movieDataArray.getJSONObject(i);

                    String movieTitle = movieDataObject.getString(MOVIE_ORIGINAL_TITLE);

                    if(!isExistingMovie(context,movieTitle)) {
                        //Constructing the Uri to the Movie Poster
                        Uri moviePosterUri = Uri.parse(MOVIE_POSTER_BASE_URL).buildUpon()
                                .appendPath(MOVIE_POSTER_SIZE_PARAM)
                                .appendEncodedPath(movieDataObject.getString(MOVIE_POSTER))
                                .build();
                        //Constructing the Uri to the Movie Backdrop
                        Uri movieBackdropUri = Uri.parse(MOVIE_POSTER_BASE_URL).buildUpon()
                                .appendPath(MOVIE_BACKDROP_SIZE_PARAM)
                                .appendEncodedPath(movieDataObject.getString(MOVIE_BACKDROP))
                                .build();
                        //Constructing the Uri to the Movie web reference
                        Uri movieWebUri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                                .appendEncodedPath(movieDataObject.getString(MOVIE_ID)+"-"+movieDataObject.getString(MOVIE_ORIGINAL_TITLE))
                                .build();

                        int movieTMDBId = Integer.parseInt(movieDataObject.get(MOVIE_ID).toString());

                        String moviePlotSynopsis = movieDataObject.getString(MOVIE_PLOT_SYNOPSIS);
                        float movieRating = Float.parseFloat(movieDataObject.get(MOVIE_RATING).toString());
                        String movieReleaseDate = movieDataObject.getString(MOVIE_RELEASE_DATE);

                        if(new Movie(movieTMDBId
                                , movieTitle
                                , moviePosterUri.toString()
                                , movieBackdropUri.toString()
                                , movieWebUri.toString()
                                , moviePlotSynopsis
                                , movieRating
                                , movieReleaseDate) instanceof Movie){

                            //Initializing and wrapping the ContentValues for inserting to movies directory
                            ContentValues movieContentValues = new ContentValues();
                            movieContentValues.put(MovieContract.Movies.MOVIE_TMDB_ID,movieTMDBId);
                            movieContentValues.put(MovieContract.Movies.MOVIE_ORIGINAL_TITLE,movieTitle);
                            movieContentValues.put(MovieContract.Movies.MOVIE_TMDB_TYPE,movieCategory);
                            movieContentValues.put(MovieContract.Movies.MOVIE_POSTER_URL,moviePosterUri.toString());
                            movieContentValues.put(MovieContract.Movies.MOVIE_BACKDROP_URL,movieBackdropUri.toString());
                            movieContentValues.put(MovieContract.Movies.MOVIE_WEB_URL,movieWebUri.toString());
                            movieContentValues.put(MovieContract.Movies.MOVIE_PLOT_SYNOPSIS,moviePlotSynopsis);
                            movieContentValues.put(MovieContract.Movies.MOVIE_RATING,movieRating);
                            movieContentValues.put(MovieContract.Movies.MOVIE_RELEASE_DATE,movieReleaseDate);
                            //Persisting the Movie to the database
                            contentResolver.insert(moviesContentUri,movieContentValues);
                            if(movieCategory.equals(POPULAR)){
                                persistedPopularMovieCount++;
                            }else if(movieCategory.equals(TOP_RATED)){
                                persistedTopRatedMovieCount++;
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                Log.d(TAG,"We have error while constructing the JSON Array out of JSON Object");
                e.getMessage();
                e.getCause();
                e.printStackTrace();
            }
        }
        Log.d(TAG,"Persisted "+movieCategory+" movies Count By Volley: "+persistedMovieCount);
    }

    /**
     * Populate Movie Data to the database. This methods basically creates two consecutive JsonObjectRequests for Popular and Top-Rated
     * movies and enqueues them to the RequestQueue that we have configured.
     * @param context
     */
    public static synchronized void populateMoviesData(final Context context){
        //Defining the corresponding URL for fetching the Popular and Top Rated movies data
        final URL popularMovieDataRequestURL = MovieDataUtils.buildPopularMovieDataRequestURL(POPULAR);
        final URL topRatedMovieDataRequestURL = MovieDataUtils.buildPopularMovieDataRequestURL(TOP_RATED);

        //Defining the JsonObjectRequest for the Popular Movies
        JsonObjectRequest popularMovieJsonObjectRequest = new JsonObjectRequest(Request.Method.GET, popularMovieDataRequestURL.toString(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                persistMovie(context,response,POPULAR);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context,"Popular Error",Toast.LENGTH_SHORT).show();
                error.getCause();
                error.printStackTrace();
            }
        });

        //Defining the JsonObjectRequest for the Top-Rated Movies
        JsonObjectRequest topRatedMovieJsonObjectRequest = new JsonObjectRequest(Request.Method.GET, topRatedMovieDataRequestURL.toString(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                persistMovie(context,response,TOP_RATED);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context,"Popular Error",Toast.LENGTH_SHORT).show();
                error.getCause();
                error.printStackTrace();
            }
        });

        //Queueing the JsonObjectRequests for popular and top rated movies
        MovieDataRequestQueueHost.getMovieDataRequestQueueHost(context).addToRequestQueue(popularMovieJsonObjectRequest);
        MovieDataRequestQueueHost.getMovieDataRequestQueueHost(context).addToRequestQueue(topRatedMovieJsonObjectRequest);
    }

    /**
     * This method first fetches a collection of all movie ids persisted in the database and then for each movie tmdb id it
     * tries to persist particular resource type in the database i.e. either Review or Trailer depending on the resourceType
     * parameter.
     * @param context //App context
     * @param resourceType //String resourceType either Trailer or Review
     */
    public static synchronized void populateMovieResources(final Context context,final String resourceType,final int movieTMDBId){

        Log.d(TAG,"populateMovieResources() is called with movie Id: "+movieTMDBId+"and resource type: "+resourceType);

        URL movieSpecificResourceURL = null;

        //Getting the ContentResolver instance
        final ContentResolver contentResolver = context.getContentResolver();

        //Constructing the appropriate resource URL (either trailer or review) depending on the passed in resourceType for a
        //particular movie with passed in movieTMDBId;
        movieSpecificResourceURL = MovieDataUtils.buildMovieSpecificResourceRequestURL(movieTMDBId,resourceType);

        //Defining JsonObjectRequest to TMDB Api with the help of Volley
        JsonObjectRequest movieResourceJsonObjectRequest = new JsonObjectRequest(Request.Method.GET
                , movieSpecificResourceURL.toString()
                , null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if(resourceType.equals(MOVIE_SPECIFIC_VIDEO_PARAMETER)){
                    try {
                        JSONArray movieTrailersJSONArray = response.getJSONArray(VIDEO_COLLECTION);
                        for(int i=0;i<movieTrailersJSONArray.length();i++){
                            JSONObject movieVideoObject = movieTrailersJSONArray.getJSONObject(i);

                            if(movieVideoObject.getString(VIDEO_TYPE).equals(VIDEO_TRAILER_KEY)){

                                String trailerTMDBId = movieVideoObject.getString(VIDEO_TMDB_ID);
                                String TRAILER_YOUTUBE_KEY = movieVideoObject.getString(VIDEO_KEY);

                                //Only persist the trailer if it is not pre-existing
                                if(new MovieTrailer(trailerTMDBId,TRAILER_YOUTUBE_KEY) instanceof MovieTrailer && !isExistingTrailer(context,trailerTMDBId)){
                                    ContentValues trailerContentValues = new ContentValues();
                                    trailerContentValues.put(MovieContract.Trailers.TRAILER_TMDB_ID,trailerTMDBId);
                                    trailerContentValues.put(MovieContract.Trailers.MOVIE_TMDB_ID,movieTMDBId);
                                    trailerContentValues.put(MovieContract.Trailers.MOVIE_TRAILER_YOUTUBE_KEY,TRAILER_YOUTUBE_KEY);
                                    //Persisting data to the trailers directory
                                    contentResolver.insert(trailersContentUri,trailerContentValues);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else if (resourceType.equals(MOVIE_SPECIFIC_REVIEW_PARAMETER)){
                    try {
                        JSONArray movieReviewJSONArray = response.getJSONArray(REVIEW_COLLECTION);
                        for(int j=0;j<movieReviewJSONArray.length();j++){
                            JSONObject movieReviewObject = movieReviewJSONArray.getJSONObject(j);

                            String reviewTMDBId = movieReviewObject.getString(REVIEW_TMDB_ID);
                            String reviewAuthor = movieReviewObject.getString(REVIEW_AUTHOR);
                            String reviewContent = movieReviewObject.getString(REVIEW_CONTENT);

                            //Only persist the review is it is not pre-existing
                            if(new MovieReview(reviewTMDBId,reviewAuthor,reviewContent) instanceof MovieReview && !isExistingReview(context,reviewTMDBId)){
                                ContentValues reviewsContentValues = new ContentValues();
                                reviewsContentValues.put(MovieContract.Reviews.REVIEW_TMDB_ID,reviewTMDBId);
                                reviewsContentValues.put(MovieContract.Reviews.MOVIE_TMDB_ID,movieTMDBId);
                                reviewsContentValues.put(MovieContract.Reviews.REVIEW_AUTHOR,reviewAuthor);
                                reviewsContentValues.put(MovieContract.Reviews.REVIEW_CONTENT,reviewContent);
                                //Persisting data to the reviews directory
                                contentResolver.insert(reviewsContentUri,reviewsContentValues);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(resourceType.equals(MOVIE_SPECIFIC_VIDEO_PARAMETER)){
                    Toast.makeText(context,"Trailer Error",Toast.LENGTH_SHORT).show();
                }else if(resourceType.equals(MOVIE_SPECIFIC_REVIEW_PARAMETER)){
                    Toast.makeText(context,"Review Error",Toast.LENGTH_SHORT).show();
                }
                error.getCause();
                error.printStackTrace();
            }
        });

        //Queueing the JsonObjectRequests for the movie specific resources
        MovieDataRequestQueueHost.getMovieDataRequestQueueHost(context).addToRequestQueue(movieResourceJsonObjectRequest);
    }


    /**
     * Executes the method populateMovieData() for both category of movies i.e. POPULAR and TOP_RATED if the passed in action
     * matches the defined action.
     * @param context // Passed in application context
     * @param action // Passed in action
     */
    public static int executeTask(final Context context, String action,int movieTMDBId){

        //Service will only be executed in case there is a connectivity. Otherwise it might result into stale data.
        if(MovieDataUtils.checkConnectionStatusInBackground(context)){
            switch (action){
                //For the generic information of the Movie to be triggered by MainActivity as an IntentService and by FirebaseJobDispatcher as
                //a JobService
                case ACTION_POPULATE_MOVIE_DATA:
                    populateMoviesData(context);
                    Log.d(TAG,"DataPopulationService Executed for populating the Movie Generic Data");
                    break;
                //To be triggered by DetailActivity as an IntentService for each Movie
                case ACTION_POPULATE_MOVIE_RESOURCES:
                    Log.d(TAG,"The Movie Id Values received: "+movieTMDBId);
                    if(movieTMDBId != DetailActivity.MOVIE_ID_DEFAULT_VALUE){
                        populateMovieResources(context,MOVIE_SPECIFIC_VIDEO_PARAMETER,movieTMDBId);
                        populateMovieResources(context,MOVIE_SPECIFIC_REVIEW_PARAMETER,movieTMDBId);
                    }
                    break;
                default:
                    Log.d(TAG,"No specific action is defined to perform");
            }
        }else {
            Log.d(TAG,"Device is currently not connected, may result in stale data");
        }
        Log.d(TAG,"No of new Movie data updated: "+(persistedPopularMovieCount+persistedTopRatedMovieCount));
        return (persistedPopularMovieCount+persistedTopRatedMovieCount);
    }
}
