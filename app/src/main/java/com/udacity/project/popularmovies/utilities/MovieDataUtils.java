package com.udacity.project.popularmovies.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

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
    /*Query Parameter name for Api Key*/
    private static final String KEY_QUERY_PARAM = "api_key";


    /*Your API Key goes here*/
    private static final String API_KEY = "#########################";

    /**
     * Method buildMovieDataRequestURL takes a String parameter which determines which kind of data we would like to
     * fetch from the API. We have two choices for fetching data i.e. data for the popular movies and data from the top rated movies.
     * This method will build and return the URL accordingly.
     * @param typeOfData //The particular category of data that we want, either popular movies or top rated movies
     * @return URL for fetching the JSON data about given category
     */
    public static URL buildPopularMovieDataRequestURL(String typeOfData){
        URL movieDataUrl = null;
        if(typeOfData.equals("popular")){
            Uri popularMovieDataUri = Uri.parse(BASE_URL).buildUpon()
                    .appendPath(POPULAR_PATH_PARAM)
                    .appendQueryParameter(KEY_QUERY_PARAM,API_KEY)
                    .build();
            try {
                movieDataUrl = new URL(popularMovieDataUri.toString());
                Log.v(TAG,"Popular movies Uri"+movieDataUrl.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }else{
            Uri topRatedMovieDataUri = Uri.parse(BASE_URL).buildUpon()
                    .appendPath(TOP_RATED_PATH_PARAM)
                    .appendQueryParameter(KEY_QUERY_PARAM,API_KEY)
                    .build();
            try {
                movieDataUrl = new URL(topRatedMovieDataUri.toString());
                Log.v(TAG,"Top Rated movies Uri"+movieDataUrl.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return movieDataUrl;
    }

    /**
     * Method fetchMovieDataFromHttpUrl is responsible to send an HTTP URL request to the movie database api and fetch the
     * movie related data as JSON objects.
     * @param movieDataUrl // URL constructed by the method buildPopularMovieDataRequestURL based upon the chosen category
     * @return String // JSON data related to the chosen category of movies
     * @throws IOException
     */
    public static String fetchMovieDataFromHttpUrl(URL movieDataUrl) throws IOException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) movieDataUrl.openConnection();
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
     * Method checkConnectivityStatus checks the connectivity status of the device and helps the app to act reasonably.
     * We don' want our app to crash when the NetworkConnectivity is not available.
     * @param context //Context of the Activity class that checks the Connection status.
     * @return boolean // indicating the connectivity status of the device.
     */
    public static boolean checkConnectivityStatus(Context context){
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

}
