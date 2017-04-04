package com.udacity.project.popularmovies.utilities;


import android.net.Uri;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import com.udacity.project.popularmovies.model.Movie;
import com.udacity.project.popularmovies.model.MovieReview;
import com.udacity.project.popularmovies.model.MovieTrailer;

/**
 * Utility class that defines parameters and methods for processing the raw data received from the movie database api
 * and construct collection of object models which are necessary for displaying the data in the ui.
 * Created by chandan on 25/01/2017.
 */
public class DataProcessingUtils {

    /*Constant to be used for logging*/
    private static final String TAG = DataProcessingUtils.class.getSimpleName();

    //Movie Review parameter constant
    private static final String MOVIE_SPECIFIC_REVIEW_PARAMETER = "reviews";
    //Movie Video parameter constant
    private static final String MOVIE_SPECIFIC_VIDEO_PARAMETER = "videos";

    /**
     * Method validateMovieData validates if the collection of Movie instance is valid
     * @param movieList //List of Novie instances obtained from the processRawMovieData method.
     * @return boolean // returns boolean expression based on the validity of the collection of movie data
     */
    public static boolean validateMovieData(List<Movie> movieList) {
        boolean validMovieInstance = false;
        if (movieList != null) {
            for (Movie movie : movieList) {
                if (movie instanceof Movie && movie.getMovieOriginalTitle() != null) {
                    validMovieInstance = true;
                } else {
                    validMovieInstance = false;
                }
            }
        } else {
            validMovieInstance = false;
        }
        return validMovieInstance;
    }

    /**
     * Method processRawMovieData parses the raw JSON data about the chosen category of movies and constructs the collection of Movie instances.
     * @param movieDataJSONString // raw JSON data about movies obtained from the movie database api.
     * @return List of Movie instances
     */
    public static List<Movie> processRawMovieData(String movieDataJSONString) throws JSONException {

        //Constants for parsing Movie related JSON Data
        final String MOVIE_COLLECTION = "results";
        final String MOVIE_ID = "id";
        final String MOVIE_POSTER = "poster_path";
        final String MOVIE_BACKDROP = "backdrop_path";
        final String MOVIE_ORIGINAL_TITLE = "original_title";
        final String MOVIE_PLOT_SYNOPSIS = "overview";
        final String MOVIE_RATING = "vote_average";
        final String MOVIE_RELEASE_DATE = "release_date";
        final String MOVIE_DB_BASE_URL = "https://www.themoviedb.org/movie";
        final String MOVIE_POSTER_BASE_URL = "http://image.tmdb.org/t/p";
        final String MOVIE_POSTER_SIZE_PARAM = "w342";
        final String MOVIE_BACKDROP_SIZE_PARAM = "w780";
        //Constants for parsing Review related JSON Data
        final String REVIEW_COLLECTION = "results";
        final String REVIEW_TMDB_ID = "id";
        final String REVIEW_AUTHOR = "author";
        final String REVIEW_CONTENT = "content";
        //Constants for parsing Video related JSON Data
        final String VIDEO_COLLECTION = "results";
        final String VIDEO_TMDB_ID = "id";
        final String VIDEO_TYPE = "type";
        final String VIDEO_KEY = "key";
        final String VIDEO_TRAILER_KEY = "Trailer";
        final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch";
        final String YOUTUBE_VIDEO_PARAM = "v";

        JSONObject movieJSONObject = null;
        List<Movie> movieList = new ArrayList<>();

        if (movieDataJSONString != null) {
            //Constructing JSONObject from raw JSON String of Movie related data
            movieJSONObject = new JSONObject(movieDataJSONString);
        }

        //Constructing the JSON Array of Movie related data
        JSONArray movieDataArray = movieJSONObject.getJSONArray(MOVIE_COLLECTION);

        //Iterating through the array of Movie Objects
        for (int i = 0; i < movieDataArray.length(); i++) {
            JSONObject movieDataObject = movieDataArray.getJSONObject(i);
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

            List<MovieReview> movieReviews = new ArrayList<>();
            List<MovieTrailer> movieTrailers = new ArrayList<>();

            /*Constructing the collection of the Movie Reviews*/
            try {
                //Fetching raw JSON Data for the Movie Review
                URL movieReviewDataURL = MovieDataUtils.buildMovieSpecificResourceRequestURL(Integer.parseInt(movieDataObject.get(MOVIE_ID).toString())
                        ,MOVIE_SPECIFIC_REVIEW_PARAMETER);
                String movieReviewJSONData = MovieDataUtils.fetchMovieDataFromHttpUrl(movieReviewDataURL);

                //Constructing the JSON Object for Movie Reviews
                JSONObject movieReviewJSONObject = new JSONObject(movieReviewJSONData);
                //Constructing the array of the Review related data
                JSONArray movieReviewJSONArray = movieReviewJSONObject.getJSONArray(REVIEW_COLLECTION);

                //Iterating through the array of Review Objects
                for(int j = 0; j < movieReviewJSONArray.length(); j++){
                    JSONObject movieReviewObject = movieReviewJSONArray.getJSONObject(j);

                    //Constructing the collection of MovieReview
                    movieReviews.add(
                            new MovieReview(movieReviewObject.getString(REVIEW_TMDB_ID)
                                    , movieReviewObject.getString(REVIEW_AUTHOR)
                                    , movieReviewObject.getString(REVIEW_CONTENT))
                    );
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            /*Constructing the collection of the Movie Videos*/
            try {
                //Fetching raw JSON data for the Movie Videos
                URL movieVideoDataURL = MovieDataUtils.buildMovieSpecificResourceRequestURL(Integer.parseInt(movieDataObject.get(MOVIE_ID).toString())
                        ,MOVIE_SPECIFIC_VIDEO_PARAMETER);
                String movieVideoJSONData = MovieDataUtils.fetchMovieDataFromHttpUrl(movieVideoDataURL);

                //Constructing JSON Object from the Review related raw JSON String
                JSONObject movieVideoJSONObject = new JSONObject(movieVideoJSONData);
                //Constructing the array of the Video related data
                JSONArray movieVideoJSONArray = movieVideoJSONObject.getJSONArray(VIDEO_COLLECTION);

                //Iterating through the array of the Video Objects
                for(int k = 0; k < movieVideoJSONArray.length(); k++){
                    JSONObject movieVideoObject = movieVideoJSONArray.getJSONObject(k);

                    //Constructing YouTube Video URI
                    String TRAILER_YOUTUBE_KEY = movieVideoObject.getString(VIDEO_KEY);
                    Uri youtubeVideoUri = Uri.parse(YOUTUBE_BASE_URL).buildUpon()
                            .appendQueryParameter(YOUTUBE_VIDEO_PARAM,TRAILER_YOUTUBE_KEY)
                            .build();

                    //Constructing the collection of the MovieTrailer
                    if(movieVideoObject.getString(VIDEO_TYPE).equals(VIDEO_TRAILER_KEY)){
                        movieTrailers.add(
                                new MovieTrailer(movieVideoObject.getString(VIDEO_TMDB_ID)
                                        ,TRAILER_YOUTUBE_KEY
                                        , youtubeVideoUri.toString())
                        );
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Constructing the List of Movie Object
            movieList.add(
                    //Constructing the Movie instance
                    new Movie(Integer.parseInt(movieDataObject.get(MOVIE_ID).toString())
                    , movieDataObject.getString(MOVIE_ORIGINAL_TITLE)
                    , moviePosterUri.toString()
                    , movieBackdropUri.toString()
                    , movieWebUri.toString()
                    , movieDataObject.getString(MOVIE_PLOT_SYNOPSIS)
                    , Float.parseFloat(movieDataObject.get(MOVIE_RATING).toString())
                    , movieDataObject.getString(MOVIE_RELEASE_DATE)
                    , movieTrailers
                    , movieReviews));
        }
        if (!validateMovieData(movieList)) {
            Log.v(TAG, "Invalid collection of Movie instances obtained");
            return null;
        }
        return movieList;
    }
}