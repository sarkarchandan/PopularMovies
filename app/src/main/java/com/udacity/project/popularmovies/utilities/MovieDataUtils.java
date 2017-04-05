package com.udacity.project.popularmovies.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.udacity.project.popularmovies.BuildConfig;
import com.udacity.project.popularmovies.model.Movie;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

/**
 * Utility class that defines parameters and methods for performing the network operations. This class also defines the method
 * to check the connection status of the device.
 * This class will construct the Uri and fetch data from the movie database api over internet.
 * Created by chandan on 23/01/2017.
 */

public class MovieDataUtils {

    private static final String TAG = MovieDataUtils.class.getSimpleName();

    /* Base url for requesting data from Movie DB */
    private static final String BASE_URL = "http://api.themoviedb.org/3/movie";
    /*Popular movie specific path parameter*/
    private static final String POPULAR_PATH_PARAM = "popular";
    /*Popular movie specific path parameter*/
    private static final String TOP_RATED_PATH_PARAM = "top_rated";
    //Movie trailer specific path parameter
    public static final String MOVIE_VIDEOS_PATH_PARAM = "videos";
    //Movie review specific path parameter
    public static final String MOVIE_REVIEWS_PATH_PARAM = "reviews";
    /*Query Parameter name for Api Key*/
    private static final String KEY_QUERY_PARAM = "api_key";

    /**
     * Takes a String parameter which determines which kind of data we would like to
     * fetch from the API. We have two choices for fetching data i.e. data for the popular movies and data from the top rated movies.
     * This method will build and return the URL accordingly.
     * @param typeOfData //The particular category of data that we want, either popular movies or top rated movies
     * @return URL for fetching the JSON data about given category
     */
    public static URL buildPopularMovieDataRequestURL(String typeOfData){
        URL movieDataUrl = null;
        if(typeOfData != null){
            switch (typeOfData){
                case POPULAR_PATH_PARAM:
                    Uri popularMovieDataUri = Uri.parse(BASE_URL).buildUpon()
                            .appendPath(POPULAR_PATH_PARAM)
                            .appendQueryParameter(KEY_QUERY_PARAM,BuildConfig.TMDB_API_KEY)
                            .build();
                    try {
                        movieDataUrl = new URL(popularMovieDataUri.toString());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    break;
                case TOP_RATED_PATH_PARAM:
                    Uri topRatedMovieDataUri = Uri.parse(BASE_URL).buildUpon()
                            .appendPath(TOP_RATED_PATH_PARAM)
                            .appendQueryParameter(KEY_QUERY_PARAM,BuildConfig.TMDB_API_KEY)
                            .build();
                    try {
                        movieDataUrl = new URL(topRatedMovieDataUri.toString());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    Log.d(TAG,"Invalid Movie Type parameter provided");
                    break;
            }
        }
        return movieDataUrl;
    }

    /**
     * Takes the id for a specific movie and the resource type that is being requested for and returns and appropriate URL for
     * that specific movie. We have two optional Resource types which are reviews and videos.
     * @param movieTMDBId //unique identifier for a given movie
     * @param resourceType //videos or review resource types.
     * @return
     */
    public static URL buildMovieSpecificResourceRequestURL(int movieTMDBId,String resourceType){
        URL dataRequestURL = null;
        if(resourceType != null){
            switch (resourceType){
                case MOVIE_REVIEWS_PATH_PARAM:
                    Uri movieReviewsUri = Uri.parse(BASE_URL).buildUpon()
                            .appendPath(String.valueOf(movieTMDBId))
                            .appendPath(MOVIE_REVIEWS_PATH_PARAM)
                            .appendQueryParameter(KEY_QUERY_PARAM,BuildConfig.TMDB_API_KEY)
                            .build();
                    try {
                        dataRequestURL = new URL(movieReviewsUri.toString());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    break;
                case MOVIE_VIDEOS_PATH_PARAM:
                    Uri movieVideosUri = Uri.parse(BASE_URL).buildUpon()
                            .appendPath(String.valueOf(movieTMDBId))
                            .appendPath(MOVIE_VIDEOS_PATH_PARAM)
                            .appendQueryParameter(KEY_QUERY_PARAM,BuildConfig.TMDB_API_KEY)
                            .build();
                    try {
                        dataRequestURL = new URL(movieVideosUri.toString());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    Log.d(TAG,"Invalid Data Type parameter provided");
                    break;
            }
        }
        return dataRequestURL;
    }

    /**
     * Method fetchMovieDataFromHttpUrl is responsible to send an HTTP URL request to the movie database api and fetch the
     * movie related data as JSON objects.
     * @param movieDataUrl // URL constructed by the method buildPopularMovieDataRequestURL based upon the chosen category
     * @return String // JSON data related to the chosen category of movies
     * @throws IOException
     */
    public static synchronized String fetchMovieDataFromHttpUrl(URL movieDataUrl) throws IOException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) movieDataUrl.openConnection();
        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.connect();
        try{
            InputStream inputStream = httpURLConnection.getInputStream();
            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\A");
            boolean hasInput = scanner.hasNext();
            if(hasInput){
                return scanner.next();
            }else{
                return null;
            }
        }finally {
            httpURLConnection.disconnect();
        }
    }

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
     * Method checkConnectivityStatus checks the connectivity status of the device and helps the app to act reasonably.
     * We don' want our app to crash when the NetworkConnectivity is not available.
     * @param context //Context of the Activity class that checks the Connection status.
     * @return boolean // indicating the connectivity status of the device.
     */
    private static boolean checkConnectivityStatus(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo!=null && networkInfo.isConnected()){
            try {
                URL url = new URL("http://www.google.com");
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setConnectTimeout(3000);
                httpURLConnection.connect();
                if(httpURLConnection.getResponseCode()==HttpURLConnection.HTTP_OK){
                    return new Boolean(true);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Checks the network Status in Background threads and returns boolean. This is a convenient method to be used all over
     * the app.
     * @param context
     * @return
     */
    public static boolean checkConnectionStatusInBackground(final Context context){

        boolean deviceNetworkStatus = false;

        //Defining an background task to check the network status.
        AsyncTask connectivityCheckAsyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                boolean networkStatus = checkConnectivityStatus(context);
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
        return deviceNetworkStatus;
    }
}
