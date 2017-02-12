package com.udacity.project.popularmovies.userinterface;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.udacity.project.popularmovies.R;
import com.udacity.project.popularmovies.adapter.MovieDataAdapter;
import com.udacity.project.popularmovies.model.Movie;
import com.udacity.project.popularmovies.utilities.DataProcessingUtils;
import com.udacity.project.popularmovies.utilities.MovieDataUtils;

public class MainActivity extends AppCompatActivity implements MovieDataAdapter.OnMovieCardClickListener,LoaderManager.LoaderCallbacks<List<Movie>>{

    /*Constant to be used for logging*/
    private static final String TAG = MainActivity.class.getSimpleName();

    /*Popular movie specific constant*/
    private static final String POPULAR = "popular";
    /*Popular movie specific constant*/
    private static final String TOP_RATED = "top_rated";

    /*Constant to be used to put the Parcelable Movie instance to the Intent as extra*/
    private static final String PARCELABLE_MOVIE = "parcelable_movie";

    /*Constant to be used as the ID of the Loader to bind the Loader to the LoaderManager*/
    private static final int MOVIE_LOADER_ID = 0;

    /*Constant to be used as the key to put the appropriate movie type to the Bundle*/
    private static final String MOVIE_TYPE_KEY = "movieType";

    /*Constant static but not final to capture the selected movie type at any point*/
    private static String SELECTED_MOVIE_TYPE = null;


    private ProgressBar progressBar_load;
    private RecyclerView recyclerView_moviedata;
    private TextView textView_error_message;
    //private Toolbar toolBar_activity_main;
    private List<Movie> movieList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar_load = (ProgressBar) findViewById(R.id.loading_activity_main);
        recyclerView_moviedata = (RecyclerView) findViewById(R.id.recyclerView_activity_main_movie_data);
        textView_error_message = (TextView) findViewById(R.id.textView_error_message);
        /*In an effort to make our app behave reasonably under the network connectivity disruption we are checking for the
        * network connectivity in background task and then loading the movie data*/
        try {
            if(new MainActivityConnectionStatusUtil().execute().get()){
                getSupportLoaderManager().initLoader(MOVIE_LOADER_ID,null,this);
                if(savedInstanceState!=null){
                    if(savedInstanceState.containsKey(MOVIE_TYPE_KEY)){
                        triggerBackgroundTask(savedInstanceState.getString(MOVIE_TYPE_KEY));
                    }
                }else{
                    triggerBackgroundTask(POPULAR);
                }
            }else{
                showNetworkError();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Implementation of the onSavedInstanceState to restore the instance state of the MainActivity in case the
     * the device is rotated.
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(MOVIE_TYPE_KEY,SELECTED_MOVIE_TYPE);
    }

    /**
     * Method showProgress adds the necessary polish for the ui in the MainActivity by introducing progress bar as appropriate.
     */
    public void showProgress(){
        textView_error_message.setVisibility(View.INVISIBLE);
        recyclerView_moviedata.setVisibility(View.INVISIBLE);
        progressBar_load.setVisibility(View.VISIBLE);
    }

    /**
     * Method showData adds the necessary polish for the ui in the MainActivity by hiding the progress bar when the data in available.
     */
    public void showData(){
        textView_error_message.setVisibility(View.INVISIBLE);
        progressBar_load.setVisibility(View.INVISIBLE);
        recyclerView_moviedata.setVisibility(View.VISIBLE);
    }

    /**
     * Method showNetworkError is triggered in case we have a disruption in network connectivity. If it happens an error message is
     * displayed in the MainActivity and users are advised to check the network connection and hit the refresh button in the AppBar.
     */
    public void showNetworkError(){
        textView_error_message.setVisibility(View.VISIBLE);
        progressBar_load.setVisibility(View.INVISIBLE);
        recyclerView_moviedata.setVisibility(View.INVISIBLE);
    }

    /**
     * Method triggerBackgroundTask runs the Background task in Loader pattern with movie category so that the appropriate movie data
     * could be fetched from the movie db api.
     * @param movieDataType // sort category of the movie data
     */
    public void triggerBackgroundTask(String movieDataType){
        Bundle movieTypeBundle = new Bundle();
        SELECTED_MOVIE_TYPE = movieDataType;
        movieTypeBundle.putString(MOVIE_TYPE_KEY,movieDataType);
        LoaderManager movieLoaderManager = getSupportLoaderManager();
        Loader<List<Movie>> movieDataLoader = movieLoaderManager.getLoader(MOVIE_LOADER_ID);
        if(movieDataLoader==null){
            movieLoaderManager.initLoader(MOVIE_LOADER_ID,movieTypeBundle,this);
        }else{
            movieLoaderManager.restartLoader(MOVIE_LOADER_ID,movieTypeBundle,this);
        }
    }

    /**
     * Overridden method onCreateOptionsMenu provides the implementation for inflating Overflow menu at AppBar
     * in MainActivity
     * @param menu // instance of the Menu.
     * @return boolean // in order to display the menu.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainactivitymenu,menu);
        return true;
    }

    /**
     * Overridden method onOptionsItemSelected provides the implementation for menu items included in the Overflow menu
     * at AppBar in MainActivity.
     * @param item //instance of the Menu Item
     * @return boolean // in order to activate the menu items.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedItemId = item.getItemId();
        switch (selectedItemId){
            case (R.id.mainActivityMenuItem_popular):
                /*In an effort to make our app behave reasonably under the network connectivity disruption we are checking for the
                * network connectivity in background task and then loading the movie data*/
                try {
                    if(new MainActivityConnectionStatusUtil().execute().get()){
                        triggerBackgroundTask(POPULAR);
                        Toast.makeText(MainActivity.this,"Popular Movies",Toast.LENGTH_SHORT).show();
                        Log.d(TAG,"Popular Movies Loaded");
                        return true;
                    }else{
                        showNetworkError();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

            case (R.id.mainActivityMenuItem_topRated):
                /*In an effort to make our app behave reasonably under the network connectivity disruption we are checking for the
                * network connectivity in background task and then loading the movie data*/
                try {
                    if(new MainActivityConnectionStatusUtil().execute().get()){
                        triggerBackgroundTask(TOP_RATED);
                        Toast.makeText(MainActivity.this,"Top Rated Movies",Toast.LENGTH_SHORT).show();
                        Log.d(TAG,"Top Rated Movies Loaded");
                        return true;
                    }else{
                        showNetworkError();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

            case (R.id.mainActivityMenuItem_refresh):
                /*In an effort to make our app behave reasonably under the network connectivity disruption we are checking for the
                * network connectivity in background task and then loading the movie data*/
                try {
                    if(new MainActivityConnectionStatusUtil().execute().get()){
                        if(SELECTED_MOVIE_TYPE!=null){
                            triggerBackgroundTask(SELECTED_MOVIE_TYPE);
                        }else{
                            triggerBackgroundTask(POPULAR);
                        }
                        Toast.makeText(MainActivity.this,"Movies Data Refreshed",Toast.LENGTH_SHORT).show();
                        Log.d(TAG,"Movies Data Refreshed");
                        return true;
                    }else{
                        showNetworkError();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Method calculateNoOfColumns dynamically calculate the number of columns and the layout would adapt
     * to the screen size and orientation.
     * @param context //Context of the current Activity
     * @return //Return the optimal no of columns
     */
    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 180);
        return noOfColumns;
    }

    /**
     * Method loadMovieData instantiated the adapter and connects the adapter to the RecyclerView. This method also determines
     * the kind of LayoutManager the RecyclerView is going to use to display the data.
     * @param movieList
     */
    public void loadMovieData(List<Movie> movieList){
        MovieDataAdapter movieDataAdapter=null;
        recyclerView_moviedata.setLayoutManager(new GridLayoutManager(this,calculateNoOfColumns(getBaseContext())));
        if(movieList.size() > 0){
           movieDataAdapter = new MovieDataAdapter(movieList,this);
        }
        recyclerView_moviedata.setAdapter(movieDataAdapter);
        movieDataAdapter.setOnMovieCardClickListener(this);
    }

    /**
     * Method onMovieCardClick is declared in the Callback interface OnMovieCardClickListener in the MovieDataAdapter class.
     * MainActivity needs to provide the implementation for the callback method onMovieCardClick to define what happens upon
     * clicking a particular card.
     * @param position
     */
    @Override
    public void onMovieCardClick(int position) {
        Movie movie = movieList.get(position);
        Intent intent = new Intent(MainActivity.this,DetailActivity.class);
        intent.putExtra(PARCELABLE_MOVIE,movie);
        startActivity(intent);
    }

    /**
     * Method onCreateLoader is part of the Loader pattern
     * @param id
     * @param args
     * @return
     */
    @Override
    public Loader<List<Movie>> onCreateLoader(int id, final Bundle args) {

        return new AsyncTaskLoader<List<Movie>>(this) {

            List<Movie> movieDataListCache = null;
            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if(args == null){
                    return;
                }
                if(movieDataListCache != null){
                    deliverResult(movieDataListCache);
                }else{
                    showProgress();
                    forceLoad();
                }
            }
            @Override
            public List<Movie> loadInBackground() {
                String movieType = null;
                if(args!=null){
                    if(args.containsKey(MOVIE_TYPE_KEY)){
                        movieType = args.getString(MOVIE_TYPE_KEY);
                    }
                }
                try {
                    String jsonData = MovieDataUtils.fetchMovieDataFromHttpUrl(MovieDataUtils.buildPopularMovieDataRequestURL(movieType));
                    movieList = DataProcessingUtils.processRawMovieData(jsonData);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return movieList;
            }
            @Override
            public void deliverResult(List<Movie> movieData) {
                if(movieData != null){
                    movieDataListCache = movieData;
                }
                super.deliverResult(movieData);
            }
        };
    }

    /**
     * Method onLoadFinished is part of the Loader pattern
     * @param loader
     * @param movieListdata
     */
    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> movieListdata) {
        for(Movie movie:movieListdata) {
            Log.d(TAG, movie.getMovieOriginalTitle());
        }
        loadMovieData(movieListdata);
        showData();
    }

    /**
     * Method onLoaderRest is part of the Loader pattern and useful when we wish to reset the Loader which is not the case in our
     * implementation. Hence the onLoaderReset will have an empty implementation
     * @param loader
     */
    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {}

    /**Inner class for the MainActivity to check the network connection in the device in background task*/
    public class MainActivityConnectionStatusUtil extends AsyncTask<Void,Void,Boolean>{
        @Override
        protected void onPreExecute() {
            progressBar_load.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return MovieDataUtils.checkConnectivityStatus(MainActivity.this);
        }
    }
}
