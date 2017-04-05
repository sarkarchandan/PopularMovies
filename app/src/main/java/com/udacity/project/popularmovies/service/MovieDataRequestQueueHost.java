package com.udacity.project.popularmovies.service;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * This class will host the RequestQueue for requesting MovieData from TMDB Api
 * Created by chandan on 05.04.17.
 */
public class MovieDataRequestQueueHost {

    private static MovieDataRequestQueueHost movieDataRequestQueueHost;
    private RequestQueue movieJSONDataRequestQueue;
    private static Context appContext;

    //Private Constructor to be used internally
    private MovieDataRequestQueueHost(Context context){
        this.appContext = context;
        this.movieJSONDataRequestQueue = getRequestQueue(context);
    }

    /**
     * Initializes the RequestQueue
     * @param context
     * @return
     */
    private RequestQueue getRequestQueue(Context context){
        if(movieJSONDataRequestQueue == null){
            movieJSONDataRequestQueue = Volley.newRequestQueue(context);
        }
        return movieJSONDataRequestQueue;
    }

    /**
     * Initializes the MovieDataRequestQueueHost
     * @param context
     * @return
     */
    public static synchronized MovieDataRequestQueueHost getMovieDataRequestQueueHost(Context context){
        if(movieDataRequestQueueHost == null){
            movieDataRequestQueueHost = new MovieDataRequestQueueHost(context);
        }
        return movieDataRequestQueueHost;
    }

    /**
     * Adds the passed in Request to the RequestQueue
     * @param request
     * @param <T>
     */
    public<T> void addToRequestQueue(Request<T> request){
        movieJSONDataRequestQueue.add(request);
    }
}
