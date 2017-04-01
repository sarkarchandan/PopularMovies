package com.udacity.project.popularmovies.userinterface;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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
import com.udacity.project.popularmovies.persistence.MovieContract;
import com.udacity.project.popularmovies.service.DataPopulationIntentService;
import com.udacity.project.popularmovies.service.DataPopulationTasks;
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
    private static int selectedTabIndex;

    //Constant that denotes the default selected tab when the app launches.
    private static final int DEFAULT_SELECTED_TAB = 0;

    //String array constant for the projection of data that we need from the database
    public static final String[] MOVIE_GRID_PROJECTION = new String[]{
            MovieContract.Movies.MOVIE_POSTER_URL
    };

    //Integer constants that are to be used for retrieving data from the Cursor.
    public static final int INDEX_MOVIE_POSTER_URL = 0;

    //String constants to be used as the Selection parameter for movie data
    public static final String MOVIE_SELECTION = MovieContract.Movies.MOVIE_TMDB_TYPE+" =?";

    //String constant to be used as the matching condition value for Popular movies.
    public static final String[] POPULAR_MOVIE_SELECTION_ARGS = new String[]{DataPopulationTasks.POPULAR};

    //String constant to be used as the matching condition value for Top Rated movies.
    public static final String[] TOP_RATED_MOVIE_SELECTION_ARGS = new String[]{DataPopulationTasks.TOP_RATED};

    //Binding the Views in the layout.
    @BindView(R.id.tabLayout_main_Activity_movieType_switcher)
    public TabLayout tabLayout_main_Activity_movieType_switcher;
    @BindView(R.id.loading_activity_main)
    public ProgressBar progressBar_load;
    @BindView(R.id.recyclerView_activity_main_movie_data)
    public RecyclerView recyclerView_moviedata;

    //Toast reference for making appropriate Toast
    private Toast message;
    //Bundle reference for selecting the appropriate movie type depending on the circumstances.
    private Bundle movieDataBundle;

    /**
     * Initializes the core app behavior and interaction.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //Starting the Service in background to update the database in case there is any new movie data available.
        Intent testServiceIntent = new Intent(this, DataPopulationIntentService.class);
        testServiceIntent.setAction(DataPopulationTasks.ACTION_POPULATE_MOVIE_DATA);
        startService(testServiceIntent);

        //Setting Elevation for the default Support Action Bar. Otherwise there will be elevation mismatch.
        getSupportActionBar().setElevation(0f);

        //Customizing the TabLayout.
        tabLayout_main_Activity_movieType_switcher.setSelectedTabIndicatorColor(ContextCompat.getColor(this,R.color.selectedTabIndicatorColor));
        tabLayout_main_Activity_movieType_switcher.setBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimary));
        tabLayout_main_Activity_movieType_switcher.setTabTextColors(ContextCompat.getColor(this,android.R.color.white)
        ,ContextCompat.getColor(this,R.color.selectedTabTextColor));
        tabLayout_main_Activity_movieType_switcher.addTab(
                tabLayout_main_Activity_movieType_switcher.newTab().setText(getString(R.string.popular_movies))
        );

        //Adding new tabs in the TabLayout.
        tabLayout_main_Activity_movieType_switcher.addTab(
                tabLayout_main_Activity_movieType_switcher.newTab().setText(getString(R.string.top_rated_movies))
        );
        tabLayout_main_Activity_movieType_switcher.addOnTabSelectedListener(this);

        //Initializing the Bundle
        movieDataBundle = new Bundle();

        //Handling the app behavior on Create and on rotation.
        if(savedInstanceState !=null && savedInstanceState.containsKey(SELECTED_TAB_INDEX_KEY)){
            Log.d(TAG,"Saved Instance state has stored tab: "+savedInstanceState.getInt(SELECTED_TAB_INDEX_KEY));
            tabLayout_main_Activity_movieType_switcher.getTabAt(savedInstanceState.getInt(SELECTED_TAB_INDEX_KEY)).select();
            movieDataBundle.putInt(SELECTED_TAB_INDEX_KEY,savedInstanceState.getInt(SELECTED_TAB_INDEX_KEY));
            getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID,movieDataBundle,this);
        }else {
            tabLayout_main_Activity_movieType_switcher.getTabAt(DEFAULT_SELECTED_TAB).select();
            movieDataBundle.putInt(SELECTED_TAB_INDEX_KEY,DEFAULT_SELECTED_TAB);
            getSupportLoaderManager().initLoader(MOVIE_LOADER_ID,movieDataBundle,this);
        }
    }

    /**
     * Handles the tab selection behavior for all possible selections.
     * @param tab
     */
    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        selectedTabIndex = tab.getPosition();
        Log.d(TAG,"Selected Tab: "+selectedTabIndex);
        if(message != null){
            message.cancel();
        }
        movieDataBundle = new Bundle();
        if(selectedTabIndex == 0){
            movieDataBundle.putInt(SELECTED_TAB_INDEX_KEY,selectedTabIndex);
            getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID,movieDataBundle,this);
            message = Toast.makeText(this,getString(R.string.popular_movies),Toast.LENGTH_SHORT);
            message.show();
        }else{
            movieDataBundle.putInt(SELECTED_TAB_INDEX_KEY,selectedTabIndex);
            getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID,movieDataBundle,this);
            message = Toast.makeText(this,getString(R.string.top_rated_movies),Toast.LENGTH_SHORT);
            message.show();
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

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
        outState.putInt(SELECTED_TAB_INDEX_KEY,selectedTabIndex);
    }

    /**
     * Method showProgress adds the necessary polish for the ui in the MainActivity by introducing progress bar as appropriate.
     */
    public void showProgress(){
        recyclerView_moviedata.setVisibility(View.INVISIBLE);
        progressBar_load.setVisibility(View.VISIBLE);
    }

    /**
     * Method showData adds the necessary polish for the ui in the MainActivity by hiding the progress bar when the data in available.
     */
    public void showData(){
        progressBar_load.setVisibility(View.INVISIBLE);
        recyclerView_moviedata.setVisibility(View.VISIBLE);
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
        MovieDataAdapter movieDataAdapter=null;
        recyclerView_moviedata.setLayoutManager(new GridLayoutManager(this,calculateNoOfColumns(getBaseContext())));
        if(movieCursor.getCount() > 0){
           movieDataAdapter = new MovieDataAdapter(movieCursor,this);
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
    public void onMovieCardClick(long position) {
        Uri clickedMovieUri = MovieContract.Movies.MOVIES_CONTENT_URI.buildUpon()
                .appendPath(String.valueOf(position)).build();
        Intent movieDetailIntent = new Intent(this,DetailActivity.class);
        movieDetailIntent.setData(clickedMovieUri);
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
        showProgress();
        switch (loaderId){
            case MOVIE_LOADER_ID:
                Uri movieDataUri = MovieContract.Movies.MOVIES_CONTENT_URI;
                if(selectedTabBundle.containsKey(SELECTED_TAB_INDEX_KEY)){
                    if(selectedTabBundle.getInt(SELECTED_TAB_INDEX_KEY) == 0){
                        //Loading Popular Movie Data
                        return new CursorLoader(this
                                ,movieDataUri
                                ,MOVIE_GRID_PROJECTION
                                ,MOVIE_SELECTION
                                ,POPULAR_MOVIE_SELECTION_ARGS
                                ,MovieContract.Movies.MOVIE_RATING);
                    }else {
                        //Loading Top-Rated Movie Data
                        return new CursorLoader(this
                                ,movieDataUri
                                ,MOVIE_GRID_PROJECTION
                                ,MOVIE_SELECTION
                                ,TOP_RATED_MOVIE_SELECTION_ARGS
                                ,MovieContract.Movies.MOVIE_RATING);
                    }
                }
            default:
                throw new RuntimeException("Loader not implemented: "+loaderId);
        }
    }

    /**
     * Upon finishing the load calling the method loadMovieData to feed the data to the Adapter.
     * @param loader
     * @param data //Data set that is generated by the CursorLoader.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        loadMovieData(data);
        showData();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
