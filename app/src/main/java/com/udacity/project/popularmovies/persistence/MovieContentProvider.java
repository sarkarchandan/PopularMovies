package com.udacity.project.popularmovies.persistence;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.udacity.project.popularmovies.model.Movie;

/**
 * Defining the native ContentProvider for this app.
 * Created by chandan on 31.03.17.
 */
public class MovieContentProvider extends ContentProvider {

    private static final String TAG = MovieContentProvider.class.getSimpleName();

    private MovieDBHelper movieDBHelper;

    private static final int MOVIES = 100;
    private static final int MOVIE_WITH_ID = 101;
    private static final int MOVIE_WITH_TITLE = 102;

    private static final int TRAILERS = 200;
    private static final int TRAILER_WITH_MOVIE_ID = 201;
    private static final int TRAILERS_WITH_TMDB_ID = 202;

    private static final int REVIEWS = 300;
    private static final int REVIEW_WITH_MOVIE_ID = 301;
    private static final int REVIEW_WITH_TMDB_ID = 302;

    //TODO Add approrpiate options here 

    //Initializing the UriMatcher
    public static final UriMatcher sUriMatcher = buildUriMatcher();


    /**
     * Initializes and configures the UriMatcher to match Uri(s) for carrying out the supported operations.
     * @return UriMatcher
     */
    public static UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY,MovieContract.PATH_MOVIES,MOVIES);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY,MovieContract.PATH_MOVIES+"/#",MOVIE_WITH_ID);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY,MovieContract.PATH_MOVIES+"/*",MOVIE_WITH_TITLE);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY,MovieContract.PATH_REVIEWS,REVIEWS);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY,MovieContract.PATH_REVIEWS+"/#",REVIEW_WITH_MOVIE_ID);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY,MovieContract.PATH_REVIEWS+"/*",REVIEW_WITH_TMDB_ID);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY,MovieContract.PATH_TRAILERS,TRAILERS);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY,MovieContract.PATH_TRAILERS+"/#",TRAILER_WITH_MOVIE_ID);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY,MovieContract.PATH_TRAILERS+"/*",TRAILERS_WITH_TMDB_ID);
        return uriMatcher;
    }

    /**
     * Initializes the connection with the SQLite Database.
     * @return boolean
     */
    @Override
    public boolean onCreate() {
        Context context = getContext();
        movieDBHelper = new MovieDBHelper(context);
        return true;
    }

    /**
     * Queries the database for fetching the data using possible parameters.
     * @param uri //Uri for identifying the directory and data set in the database
     * @param projection //Set of columns that need to be fetched
     * @param selection //Condition equivalent to where clause
     * @param selectionArgs //Value that must be satisfied in the fetched data
     * @param sortOrder // Order in which the data set will be fetched into the Cursor.
     * @return Cursor with the data set
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase sqLiteDatabase = movieDBHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor returnCursor;
        switch (match){
            case MOVIES:
                returnCursor = sqLiteDatabase.query(MovieContract.Movies.MOVIE_TABLE_NAME
                ,projection
                ,selection
                ,selectionArgs
                ,null
                ,null
                ,MovieContract.Movies.MOVIE_RATING);
                break;
            case MOVIE_WITH_ID:
                String myMovieWithIdSelection = " _ID =?";
                String[] myMovieWithIdSelectionArgs = new String[]{uri.getPathSegments().get(1)};
                returnCursor = sqLiteDatabase.query(MovieContract.Movies.MOVIE_TABLE_NAME
                ,projection
                ,myMovieWithIdSelection
                ,myMovieWithIdSelectionArgs
                ,null
                ,null
                ,sortOrder);
                break;
            case MOVIE_WITH_TITLE:
                String myMovieWithTitleSelection = MovieContract.Movies.MOVIE_ORIGINAL_TITLE+" =?";
                String[] myMovieWithTitleSelectionArgs = new String[]{uri.getPathSegments().get(1)};
                returnCursor = sqLiteDatabase.query(MovieContract.Movies.MOVIE_TABLE_NAME
                ,projection
                ,myMovieWithTitleSelection
                ,myMovieWithTitleSelectionArgs
                ,null
                ,null
                ,sortOrder);
                break;
            case TRAILERS:
                returnCursor = sqLiteDatabase.query(MovieContract.Trailers.TRAILER_TABLE_NAME
                ,projection
                ,selection
                ,selectionArgs
                ,null
                ,null
                ,sortOrder);
                break;
            case TRAILER_WITH_MOVIE_ID:
                String myTrailerSelection = MovieContract.Trailers.MOVIE_TMDB_ID+" =?";
                String[] myTrailerSelectionArgs = new String[]{uri.getPathSegments().get(1)};
                returnCursor = sqLiteDatabase.query(MovieContract.Trailers.TRAILER_TABLE_NAME
                ,projection
                ,myTrailerSelection
                ,myTrailerSelectionArgs
                ,null
                ,null
                ,sortOrder);
                break;
            case TRAILERS_WITH_TMDB_ID:
                String myTrailerWithTMDBIdSelection = MovieContract.Trailers.TRAILER_TMDB_ID+" =?";
                String[] myTrailerWithTMDBIdSelectionArgs = new String[]{uri.getPathSegments().get(1)};
                returnCursor = sqLiteDatabase.query(MovieContract.Trailers.TRAILER_TABLE_NAME
                ,projection
                ,myTrailerWithTMDBIdSelection
                ,myTrailerWithTMDBIdSelectionArgs
                ,null
                ,null
                ,null);
                break;
            case REVIEWS:
                returnCursor = sqLiteDatabase.query(MovieContract.Reviews.REVIEW_TABLE_NAME
                ,projection
                ,selection
                ,selectionArgs
                ,null
                ,null
                ,sortOrder);
                break;
            case REVIEW_WITH_MOVIE_ID:
                String myReviewSelection = MovieContract.Reviews.MOVIE_TMDB_ID+" =?";
                String[] myReviewSelectionArgs = new String[]{uri.getPathSegments().get(1)};
                returnCursor = sqLiteDatabase.query(MovieContract.Reviews.REVIEW_TABLE_NAME
                ,projection
                ,myReviewSelection
                ,myReviewSelectionArgs
                ,null
                ,null
                ,sortOrder);
                break;
            case REVIEW_WITH_TMDB_ID:
                String myReviewWithTMDBIdSelection = MovieContract.Reviews.REVIEW_TMDB_ID+" =?";
                String[] myReviewWithTMDBIdSelectionArgs = new String[]{uri.getPathSegments().get(1)};
                returnCursor = sqLiteDatabase.query(MovieContract.Reviews.REVIEW_TABLE_NAME
                ,projection
                ,myReviewWithTMDBIdSelection
                ,myReviewWithTMDBIdSelectionArgs
                ,null
                ,null
                ,null);
                break;
            default:
                throw new UnsupportedOperationException("An Error Occurred while querying with the Uri: "+uri);
        }
        //sqLiteDatabase.close();
        returnCursor.setNotificationUri(getContext().getContentResolver(),uri);
        Log.d(TAG, "CP Count: "+ returnCursor.getCount());
        return returnCursor;
    }

    /**
     * Insert data into database tables.
     * @param uri // Uri for identifying the a directory in the database
     * @param contentValues //Contains the data to be inserted
     * @return Uri that points to the newly inserted data
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase sqLiteDatabase = movieDBHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match){
            case MOVIES:
                long newMovieInsertedRowId = sqLiteDatabase.insert(MovieContract.Movies.MOVIE_TABLE_NAME
                ,null
                ,contentValues);
                if(newMovieInsertedRowId > 0){
                    returnUri = ContentUris.withAppendedId(MovieContract.Movies.MOVIES_CONTENT_URI,newMovieInsertedRowId);
                }else {
                    throw new SQLiteException("Unable to insert the record for the Uri: "+uri);
                }
                break;
            case TRAILERS:
                long newTrailerInsertRowId = sqLiteDatabase.insert(MovieContract.Trailers.TRAILER_TABLE_NAME
                ,null
                ,contentValues);
                if(newTrailerInsertRowId > 0){
                    returnUri = ContentUris.withAppendedId(MovieContract.Trailers.TRAILERS_CONTENT_URI,newTrailerInsertRowId);
                }else {
                    throw new SQLiteException("Unable to insert the record for the Uri: "+uri);
                }
                break;
            case REVIEWS:
                long newReviewInsertRowId = sqLiteDatabase.insert(MovieContract.Reviews.REVIEW_TABLE_NAME
                ,null
                ,contentValues);
                if(newReviewInsertRowId > 0){
                    returnUri = ContentUris.withAppendedId(MovieContract.Reviews.REVIEWS_CONTENT_URI,newReviewInsertRowId);
                }else {
                    throw new SQLiteException("Unable to insert the record for the Uri: "+uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri "+uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return returnUri;
    }

    /**
     * Updates the value of a record in the database tables
     * @param uri // Uri that identifies a directory and data set in the database
     * @param contentValues //Contains the data to be updated
     * @param selection // Condition equivalen to the where clause
     * @param selectionArgs // Value that must be satisfied before making the update
     * @return Integer no of rows updated in the database.
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase sqLiteDatabase = movieDBHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int moviesUpdated;
        switch (match){
            case MOVIE_WITH_ID:
                String mySelection = " _ID =?";
                String[] mySelectionArgs = new String[]{uri.getPathSegments().get(1)};
                moviesUpdated = sqLiteDatabase.update(MovieContract.Movies.MOVIE_TABLE_NAME
                        ,contentValues
                        ,mySelection
                        ,mySelectionArgs);
                if(moviesUpdated != 0){
                    getContext().getContentResolver().notifyChange(uri,null);
                }else {
                    throw new SQLiteException("Unable to update the record for the Uri: "+uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri "+uri);
        }
        return moviesUpdated;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }
}
