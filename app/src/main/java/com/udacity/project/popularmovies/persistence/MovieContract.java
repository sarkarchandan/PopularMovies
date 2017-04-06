package com.udacity.project.popularmovies.persistence;

import android.net.Uri;
import android.net.wifi.ScanResult;
import android.provider.BaseColumns;

/**
 * Contract for the data persistence. This class will encapsulate the fundamental fields of our database tables
 * amd also contains the Uri we need to access the data from the database.
 * Created by chandan on 31.03.17.
 */
public final class MovieContract {

    //Empty Constructor
    private MovieContract(){}

    //Declaring prefix or scheme
    private static final String SCHEME = "content://";

    //Declaring Content Authority
    public static final String CONTENT_AUTHORITY = "com.udacity.project.popularmovies.persistence";

    //Defining Base Content Uri
    public static final Uri BASE_CONTENT_URI = Uri.parse(SCHEME+CONTENT_AUTHORITY);

    //Constant for movies Directory
    public static final String PATH_MOVIES = "movies";
    //Constant for the trailers Directory
    public static final String PATH_TRAILERS = "trailers";
    //Constants for the reviews Directory
    public static final String PATH_REVIEWS = "reviews";

    //Table 'movies' definition template
    public static final class Movies implements BaseColumns{

        public static final Uri MOVIES_CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIES).build();

        //Table definition constants
        public static final String MOVIE_TABLE_NAME = "movies";
        public static final String MOVIE_TMDB_ID = "movieTMDBId";
        public static final String MOVIE_ORIGINAL_TITLE = "movieOriginalTitle";
        public static final String MOVIE_TMDB_TYPE = "movieTMDBType";
        public static final String MOVIE_POSTER_URL = "moviePosterUrl";
        public static final String MOVIE_BACKDROP_URL = "movieBackDropUrl";
        public static final String MOVIE_WEB_URL = "movieWebUrl";
        public static final String MOVIE_PLOT_SYNOPSIS = "moviePlotSynopsis";
        public static final String MOVIE_RATING = "movieRating";
        public static final String MOVIE_RELEASE_DATE =  "movieReleaseDate";
        public static final String IS_FAVORITE_MOVIE = "isFavoriteMovie";
    }

    //Table 'trailers' definition template
    public static final class Trailers implements BaseColumns{

        public static final Uri TRAILERS_CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_TRAILERS).build();

        //Table definition constants
        public static final String TRAILER_TABLE_NAME = "trailers";
        public static final String TRAILER_TMDB_ID = "movieTrailerTMDBId";
        public static final String MOVIE_TMDB_ID = "movieTMDBId";
        public static final String MOVIE_TRAILER_YOUTUBE_KEY = "movieTrailerYouTubeKey";
    }

    //Table 'reviews' definition template
    public static final class Reviews implements BaseColumns{

        public static final Uri REVIEWS_CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_REVIEWS).build();

        //Table definition constants
        public static final String REVIEW_TABLE_NAME = "reviews";
        public static final String REVIEW_TMDB_ID = "movieReviewTMDBId";
        public static final String MOVIE_TMDB_ID = "movieTMDBId";
        public static final String REVIEW_AUTHOR = "movieReviewAuthor";
        public static final String REVIEW_CONTENT = "movieReviewContent";
    }
}
