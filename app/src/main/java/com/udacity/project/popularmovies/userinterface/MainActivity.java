package com.udacity.project.popularmovies.userinterface;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcelable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.udacity.project.popularmovies.R;
import com.udacity.project.popularmovies.adapter.MovieDataAdapter;
import com.udacity.project.popularmovies.model.Movie;
import com.udacity.project.popularmovies.persistence.MovieContract;
import com.udacity.project.popularmovies.service.DataPopulationIntentService;
import com.udacity.project.popularmovies.service.DataPopulationTasks;
import com.udacity.project.popularmovies.service.MovieDataUpdateServiceUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements MovieDataAdapter.OnMovieCardClickListener,LoaderManager.LoaderCallbacks<Cursor>
        ,TabLayout.OnTabSelectedListener{

    //Constant to be used for logging
    private static final String TAG = MainActivity.class.getSimpleName();

    //Constant to be used as the ID of the Loader to bind the Loader to the LoaderManager
    private static final int MOVIE_LOADER_ID = 0;

    //Constant to be used as key to store the selected tab inside Bundle in order to support the rotation behavior.
    private static final String SELECTED_TAB_INDEX_KEY = "selected-tab-index";

    //Constant to be used as a key to store the RecyclerView state in the Bundle
    private static final String RECYCLER_VIEW_STATE_KEY = "recycler-view-state-key";

    //String array constant for the projection of data that we need from the database
    private static final String[] MOVIE_GRID_PROJECTION = new String[]{
            MovieContract.Movies._ID,
            MovieContract.Movies.MOVIE_TMDB_ID,
            MovieContract.Movies.MOVIE_POSTER_URL
    };

    //Integer constants that are to be used for retrieving data from the Cursor.
    public static final int INDEX_MOVIE_DB_ID = 0;
    public static final int INDEX_MOVIE_TMDB_ID = 1;
    public static final int INDEX_MOVIE_POSTER_URL = 2;

    //String Constant to be used as key while putting the movieTMDBId to the explicit Intent
    public static final String MOVIE_TMDB_ID_INTENT_KEY = "movie-tmdb-intent-key";


    //String constants to be used as the Selection parameter for movie data
    public static final String MOVIE_SELECTION = MovieContract.Movies.MOVIE_TMDB_TYPE+" =?";

    //String constant to be used as the matching condition value for Popular movies.
    public static final String[] POPULAR_MOVIE_SELECTION_ARGS = new String[]{DataPopulationTasks.POPULAR};

    //String constant to be used as the matching condition value for Top Rated movies.
    public static final String[] TOP_RATED_MOVIE_SELECTION_ARGS = new String[]{DataPopulationTasks.TOP_RATED};

    //Binding the Views of the MainActivity Layout.
    @BindView(R.id.tabLayout_main_Activity_movieType_switcher)
    public TabLayout tabLayout_main_Activity_movieType_switcher;
    @BindView(R.id.loading_activity_main)
    public ProgressBar progressBar_load;
    @BindView(R.id.recyclerView_activity_main_movie_data)
    public RecyclerView recyclerView_movieData;

    private Parcelable recyclerViewState;

    //Toast reference for making appropriate Toast
    private Toast message;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //Starting the Service in background to update the database in case there is any new movie data available.
        Intent dataPopulationServiceIntent = new Intent(this, DataPopulationIntentService.class);
        dataPopulationServiceIntent.setAction(DataPopulationTasks.ACTION_POPULATE_MOVIE_DATA);
        startService(dataPopulationServiceIntent);

        //Scheduling the periodic Movie data update procedure using the FirebaseJobDispatcher
        MovieDataUpdateServiceUtils.scheduleMovieDataUpdate(this);

        //Setting Elevation for the default Support Action Bar. Otherwise there will be elevation mismatch.
        getSupportActionBar().setElevation(0f);

        //Customizing the TabLayout.
        tabLayout_main_Activity_movieType_switcher.setSelectedTabIndicatorColor(ContextCompat.getColor(this,R.color.selectedTabIndicatorColor));
        tabLayout_main_Activity_movieType_switcher.setBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimary));
        tabLayout_main_Activity_movieType_switcher.setTabTextColors(ContextCompat.getColor(this,android.R.color.white)
        ,ContextCompat.getColor(this,R.color.selectedTabTextColor));

        //Adding new tabs in the TabLayout.
        tabLayout_main_Activity_movieType_switcher.addTab(
                tabLayout_main_Activity_movieType_switcher.newTab().setText(getString(R.string.popular_movies))
        );
        tabLayout_main_Activity_movieType_switcher.addTab(
                tabLayout_main_Activity_movieType_switcher.newTab().setText(getString(R.string.top_rated_movies))
        );

        //Adding OnTabSelectedListener to the TabLayout
        tabLayout_main_Activity_movieType_switcher.addOnTabSelectedListener(this);

        if(savedInstanceState !=null){
            if(savedInstanceState.containsKey(SELECTED_TAB_INDEX_KEY)){
                int position = savedInstanceState.getInt(SELECTED_TAB_INDEX_KEY);
                tabLayout_main_Activity_movieType_switcher.getTabAt(position).select();
            }
        }else {
            tabLayout_main_Activity_movieType_switcher.getTabAt(0).select();
        }

        getSupportLoaderManager().initLoader(MOVIE_LOADER_ID,null,this);
    }

    /**
     * Handles the tab selection behavior for all possible selections.
     * @param tab
     */
    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        Log.d(TAG,"Selected Tab: "+tab.getPosition());
        getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID,null,this);
        if(message != null){
            message.cancel();
        }

        if(tab.getPosition() == 0){
            message = Toast.makeText(this,getString(R.string.popular_movies),Toast.LENGTH_SHORT);
            message.show();
        }else{
            message = Toast.makeText(this,getString(R.string.top_rated_movies),Toast.LENGTH_SHORT);
            message.show();
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        recyclerViewState = null;
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    /**
     * Implementation of the onSavedInstanceState to restore the instance state of the MainActivity in case the
     * the device is rotated.
     * @param outState// Bundle that stores temporary data in case the Activity is to be destroyed and recreated.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_TAB_INDEX_KEY,tabLayout_main_Activity_movieType_switcher.getSelectedTabPosition());
        //Storing the RecyclerView state in the Bundle
        outState.putParcelable(RECYCLER_VIEW_STATE_KEY,recyclerView_movieData.getLayoutManager().onSaveInstanceState());
    }

    /**
     * Implementation of the behavior for rotation support. We are putting the current state of the RecyclerView inside
     * a Parcelable instance when the device is about to be rotated.
     * @param savedInstanceState
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG,"onRestoreInstanceState called");
        if(savedInstanceState != null){
            Log.d(TAG,"savedInstanceState is not null");
            recyclerViewState = savedInstanceState.getParcelable(RECYCLER_VIEW_STATE_KEY);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID,null,this);
    }

    /**
     * Method showProgress adds the necessary polish for the ui in the MainActivity by introducing progress bar as appropriate.
     */
    public void showProgress(){
        recyclerView_movieData.setVisibility(View.INVISIBLE);
        progressBar_load.setVisibility(View.VISIBLE);
    }

    /**
     * Method showData adds the necessary polish for the ui in the MainActivity by hiding the progress bar when the data in available.
     */
    public void showData(){
        progressBar_load.setVisibility(View.INVISIBLE);
        recyclerView_movieData.setVisibility(View.VISIBLE);
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
            case (R.id.mainActivityMenuItem_favorites):
                Intent favoriteActivityIntent = new Intent(this,FavoriteActivity.class);
                startActivity(favoriteActivityIntent);
                return true;
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
     * @param movieCursor
     */
    public void loadMovieData(Cursor movieCursor){
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,calculateNoOfColumns(getBaseContext()));
        recyclerView_movieData.setLayoutManager(layoutManager);
        MovieDataAdapter movieDataAdapter = new MovieDataAdapter(movieCursor,this);
        recyclerView_movieData.setAdapter(movieDataAdapter);
        if(recyclerViewState != null){
            layoutManager.onRestoreInstanceState(recyclerViewState);
        }
        movieDataAdapter.setOnMovieCardClickListener(this);
    }

    /**
     * Method onMovieCardClick is declared in the Callback interface OnMovieCardClickListener in the MovieDataAdapter class.
     * MainActivity needs to provide the implementation for the callback method onMovieCardClick to define what happens upon
     * clicking a particular card.
     * @param position
     */
    @Override
    public void onMovieCardClick(long position,int movieTMDBId) {
        Uri clickedMovieUri = MovieContract.Movies.MOVIES_CONTENT_URI.buildUpon()
                .appendPath(String.valueOf(position)).build();
        Intent movieDetailIntent = new Intent(this,DetailActivity.class);
        movieDetailIntent.setData(clickedMovieUri);
        movieDetailIntent.putExtra(MOVIE_TMDB_ID_INTENT_KEY,movieTMDBId);
        startActivity(movieDetailIntent);
    }

    /**
     * Creates the CursorLoader, fetches the required data from the database.
     * @param loaderId //Pre-defined constant id for the Loader
     * @param selectedTabBundle //Bundle that encapsulates the parameters for fetching correct data.
     * @return CursorLoader
     */
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle selectedTabBundle) {
        Log.d(TAG,"onCreateLoaderCalled");
        showProgress();
        switch (loaderId){
            case MOVIE_LOADER_ID:
                Uri movieDataUri = MovieContract.Movies.MOVIES_CONTENT_URI;
                if(tabLayout_main_Activity_movieType_switcher.getSelectedTabPosition() == 0){
                    Log.d(TAG,"POPULAR CursorLoader Chosen");
                    //Loading Popular Movie Data
                    return new CursorLoader(this
                            ,movieDataUri
                            ,MOVIE_GRID_PROJECTION
                            ,MOVIE_SELECTION
                            ,POPULAR_MOVIE_SELECTION_ARGS
                            ,MovieContract.Movies.MOVIE_RATING);
                }else {
                    Log.d(TAG,"TOP_RATED CursorLoader Chosen");
                    //Loading Top-Rated Movie Data
                    return new CursorLoader(this
                            ,movieDataUri
                            ,MOVIE_GRID_PROJECTION
                            ,MOVIE_SELECTION
                            ,TOP_RATED_MOVIE_SELECTION_ARGS
                            ,MovieContract.Movies.MOVIE_RATING);
                }
            default:
                throw new RuntimeException("Loader not implemented: "+loaderId);
        }
    }

    /**
     * Upon finishing the load calling the method loadMovieData to feed the data to the Adapter.
     * @param loader
     * @param movieCursor //Data set that is generated by the CursorLoader.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor movieCursor) {
        Log.d(TAG,"onLoadFinishedCalled with Cursor Data Count: "+movieCursor.getCount());
        loadMovieData(movieCursor);
        if(movieCursor.getCount() > 0){
            showData();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
