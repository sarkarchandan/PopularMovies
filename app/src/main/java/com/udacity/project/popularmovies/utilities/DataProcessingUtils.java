package com.udacity.project.popularmovies.utilities;


import android.net.Uri;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import com.udacity.project.popularmovies.model.Movie;

/**
 * Utility class that defines parameters and methods for processing the raw data received from the movie database api
 * and construct collection of object models which are necessary for displaying the data in the ui.
 * Created by chandan on 25/01/2017.
 */
public class DataProcessingUtils {

    /*Constant to be used for logging*/
    private static final String TAG = DataProcessingUtils.class.getSimpleName();

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
        JSONObject movieJSONObject = null;

        List<Movie> movieList = new ArrayList<>();
        if (!movieDataJSONString.equals("") && movieDataJSONString != null) {
            movieJSONObject = new JSONObject(movieDataJSONString);
        }

        JSONArray movieDataArray = movieJSONObject.getJSONArray(MOVIE_COLLECTION);

        for (int i = 0; i < movieDataArray.length(); i++) {
            JSONObject movieDataObject = movieDataArray.getJSONObject(i);
            Uri moviePosterUri = Uri.parse(MOVIE_POSTER_BASE_URL).buildUpon()
                    .appendPath(MOVIE_POSTER_SIZE_PARAM)
                    .appendEncodedPath(movieDataObject.getString(MOVIE_POSTER))
                    .build();
            Uri movieBackdropUri = Uri.parse(MOVIE_POSTER_BASE_URL).buildUpon()
                    .appendPath(MOVIE_BACKDROP_SIZE_PARAM)
                    .appendEncodedPath(movieDataObject.getString(MOVIE_BACKDROP))
                    .build();
            Uri movieWebUri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                    .appendEncodedPath(movieDataObject.getString(MOVIE_ID)+"-"+movieDataObject.getString(MOVIE_ORIGINAL_TITLE))
                    .build();
            movieList.add(new Movie(Integer.parseInt(movieDataObject.get(MOVIE_ID).toString())
                    , movieDataObject.getString(MOVIE_ORIGINAL_TITLE)
                    , moviePosterUri.toString()
                    , movieBackdropUri.toString()
                    , movieWebUri.toString()
                    , movieDataObject.getString(MOVIE_PLOT_SYNOPSIS)
                    , Float.parseFloat(movieDataObject.get(MOVIE_RATING).toString())
                    , movieDataObject.getString(MOVIE_RELEASE_DATE)));
        }
        if (!validateMovieData(movieList)) {
            Log.v(TAG, "Invalid collection of Movie instances obtained");
            return null;
        }
        return movieList;
    }
}