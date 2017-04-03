package com.udacity.project.popularmovies.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * MovieDBHelper is the Helper class for the data persistence
 * Created by chandan on 31.03.17.
 */
public class MovieDBHelper extends SQLiteOpenHelper {

    //Database name
    private static final String MOVIE_DATABASE_NAME = "movies.db";
    //Database version
    private static final int MOVIE_DATABASE_VERSION = 5;

    public MovieDBHelper(Context context){
        super(context,MOVIE_DATABASE_NAME,null,MOVIE_DATABASE_VERSION);
    }

    /**
     * Creates the schemas for persisting data.
     * @param sqLiteDatabase
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //Declaring movies table schema
        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " +
                MovieContract.Movies.MOVIE_TABLE_NAME + "(" +
                MovieContract.Movies._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.Movies.MOVIE_TMDB_ID + " INTEGER, " +
                MovieContract.Movies.MOVIE_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                MovieContract.Movies.MOVIE_TMDB_TYPE + " TEXT NOT NULL, " +
                MovieContract.Movies.MOVIE_BACKDROP_URL + " TEXT NOT NULL, " +
                MovieContract.Movies.MOVIE_POSTER_URL + " TEXT NOT NULL, " +
                MovieContract.Movies.MOVIE_WEB_URL + " TEXT NOT NULL, " +
                MovieContract.Movies.MOVIE_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieContract.Movies.MOVIE_RATING + " REAL NOT NULL, " +
                MovieContract.Movies.MOVIE_PLOT_SYNOPSIS + " TEXT NOT NULL, " +
                MovieContract.Movies.IS_FAVORITE_MOVIE + " INTEGER DEFAULT 0 " +
                ");";

        //Declaring trailers table schema
        final String SQL_CREATE_TRAILERS_TABLE = "CREATE TABLE " +
                MovieContract.Trailers.TRAILER_TABLE_NAME + "(" +
                MovieContract.Trailers._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.Trailers.TRAILER_TMDB_ID + " TEXT NOT NULL, " +
                MovieContract.Trailers.MOVIE_TMDB_ID + " INTEGER, " +
                MovieContract.Trailers.TRAILER_URL + " TEXT NOT NULL " +
                ");";

        //Declaring reviews table schema
        final String SQL_CREATE_REVIEWS_TABLE = "CREATE TABLE " +
                MovieContract.Reviews.REVIEW_TABLE_NAME + "(" +
                MovieContract.Reviews._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.Reviews.REVIEW_TMDB_ID + " TEXT NOT NULL, " +
                MovieContract.Reviews.MOVIE_TMDB_ID + " INTEGER, " +
                MovieContract.Reviews.REVIEW_AUTHOR + " TEXT NOT NULL, " +
                MovieContract.Reviews.REVIEW_CONTENT + " TEXT NOT NULL " +
                ");";

        //Creating tables
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TRAILERS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEWS_TABLE);
    }

    /**
     * Modifies the database schemas
     * @param sqLiteDatabase
     * @param i
     * @param i1
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //Dropping and Creating the table schemas from the scratch
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+MovieContract.Movies.MOVIE_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+MovieContract.Trailers.TRAILER_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+MovieContract.Reviews.REVIEW_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
