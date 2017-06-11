package com.udacity.project.popularmovies.userinterface;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.udacity.project.popularmovies.R;

import com.squareup.picasso.Picasso;
import com.udacity.project.popularmovies.adapter.MovieReviewResourceAdapter;
import com.udacity.project.popularmovies.adapter.MovieTrailerResourceAdapter;
import com.udacity.project.popularmovies.model.Movie;
import com.udacity.project.popularmovies.persistence.MovieContract;
import com.udacity.project.popularmovies.service.DataPopulationIntentService;
import com.udacity.project.popularmovies.service.DataPopulationTasks;
import com.udacity.project.popularmovies.utilities.MovieDataUtils;

import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>
        ,TabLayout.OnTabSelectedListener
        ,MovieTrailerResourceAdapter.OnMovieTrailerClikcListener {

    //Constant to be used for logging
    private static final String TAG = DetailActivity.class.getSimpleName();

    //Binding the Views of the DetailActivity Layout.
    @BindView(R.id.toolBar_activity_detail)
    public Toolbar toolBar_activity_detail;
    @BindView(R.id.imageView_detailActivity_backdrop)
    public ImageView imageView_detailActivity_backdrop;
    @BindView(R.id.imageView_detailActivity_poster)
    public ImageView imageView_detailActivity_poster;
    @BindView(R.id.textView_detailActivity_originalTitle)
    public TextView textView_detailActivity_originalTitle;
    @BindView(R.id.ratingBar_detailActivity_movieRating)
    public RatingBar ratingBar_detailActivity_movieRating;
    @BindView(R.id.textView_detailActivity_movie_releaseDate)
    public TextView textView_detailActivity_movie_releaseDate;
    @BindView(R.id.textView_detailActivity_plot_synopsis)
    public TextView textView_detailActivity_plot_synopsis;
    @BindView(R.id.progressBar_detailActivity_loading)
    public ProgressBar progressBar_detailActivity_loading;
    @BindView(R.id.cardView)
    public CardView cardView;
    @BindView(R.id.tabLayout_detail_Activity_resourceType_switcher)
    public TabLayout tabLayout_detail_Activity_resourceType_switcher;
    @BindView(R.id.recyclerView_activity_Detail_resource_switcher)
    public RecyclerView recyclerView_activity_Detail_resource_switcher;

    //Constant for putting the TMDB MovieId in to the Intent before launching the IntentService.
    public static final String SELECTED_MOVIE_RESOURCES_INTENT_KEY = "selected-movie-resource-intent-key";

    //This default constant for the TMDB MovieId will be used when we need to launch either the IntentService or JobService with
    //default value
    public static final int MOVIE_ID_DEFAULT_VALUE = 0;

    //Constant to be used as key to store the selected tab inside Bundle in order to support the rotation behavior.
    private static final String SELECTED_TAB_INDEX_KEY = "selected-tab-index";

    //Identifier for the CursorLoader for General Movie Data
    private static final int DISPLAY_SELECTED_MOVIE_DATA_LOADER_ID = 3018;


    //Defining the projection for fetching selected Movie data.
    private static final String[] MOVIE_DISPLAY_DATA_PROJECTION = new String[]{
            MovieContract.Movies.MOVIE_TMDB_ID,
            MovieContract.Movies.MOVIE_ORIGINAL_TITLE,
            MovieContract.Movies.MOVIE_BACKDROP_URL,
            MovieContract.Movies.MOVIE_POSTER_URL,
            MovieContract.Movies.MOVIE_PLOT_SYNOPSIS,
            MovieContract.Movies.MOVIE_RATING,
            MovieContract.Movies.MOVIE_RELEASE_DATE
    };

    //Defining the Cursor Index based on projection.
    private static final int INDEX_MOVIE_TMDB_ID = 0;
    private static final int INDEX_MOVIE_ORIGINAL_TITLE = 1;
    private static final int INDEX_MOVIE_BACKDROP_URL = 2;
    private static final int INDEX_MOVIE_POSTER_URL = 3;
    private static final int INDEX_MOVIE_PLOT_SYNOPSIS = 4;
    private static final int INDEX_MOVIE_RATING = 5;
    private static final int INDEX_MOVIE_RELEASE_DATE = 6;

    //Defining the projection for fetching the Trailer data for a selected Movie
    private static final String[] DISPLAY_MOVIE_TRAILER_PROJECTION = new String[]{
            MovieContract.Trailers.MOVIE_TRAILER_YOUTUBE_KEY,
    };

    //Defining the Cursor Index based on projection
    public static final int INDEX_MOVIE_TRAILER_YOUTUBE_KEY = 0;


    //Defining the projection for fetching the Review data for a selected Movie
    private static final String[] DISPLAY_MOVIE_REVIEW_PROJECTION = new String[]{
            MovieContract.Reviews.REVIEW_AUTHOR,
            MovieContract.Reviews.REVIEW_CONTENT
    };

    //Defining the Cursor Index based on projection
    public static final int INDEX_MOVIE_REVIEW_AUTHOR = 0;
    public static final int INDEX_MOVIE_REVIEW_CONTENT = 1;

    private Context context;
    private Intent selectedMovieIntent;
    private static Uri selectedMovieDataUri;
    private static int currentlyDisplayedMovieTMDBId;
    private Toast popupMessage;
    private MovieTrailerResourceAdapter movieTrailerResourceAdapter = null;
    private MovieReviewResourceAdapter movieReviewResourceAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        setSupportActionBar(toolBar_activity_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        showProgress();


        //Handling the Intent that has been passed on from the previous Activity to display the specific Movie data
        selectedMovieIntent = getIntent();
        if (selectedMovieIntent != null) {
            selectedMovieDataUri = selectedMovieIntent.getData();
            currentlyDisplayedMovieTMDBId = selectedMovieIntent.getIntExtra(MainActivity.MOVIE_TMDB_ID_INTENT_KEY,MOVIE_ID_DEFAULT_VALUE);
            getSupportLoaderManager().initLoader(DISPLAY_SELECTED_MOVIE_DATA_LOADER_ID,null,this);
        }

        //Starting the Service in background to update the database in case there is any new movie data available.
        Intent resourcePopulationServiceIntent = new Intent(this, DataPopulationIntentService.class);
        resourcePopulationServiceIntent.setAction(DataPopulationTasks.ACTION_POPULATE_MOVIE_RESOURCES);
        resourcePopulationServiceIntent.putExtra(SELECTED_MOVIE_RESOURCES_INTENT_KEY,currentlyDisplayedMovieTMDBId);
        startService(resourcePopulationServiceIntent);


        //Customizing the TabLayout.
        tabLayout_detail_Activity_resourceType_switcher.setSelectedTabIndicatorColor(ContextCompat.getColor(this,R.color.selectedTabIndicatorColor));
        tabLayout_detail_Activity_resourceType_switcher.setBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimary));
        tabLayout_detail_Activity_resourceType_switcher.setTabTextColors(ContextCompat.getColor(this,android.R.color.white)
                ,ContextCompat.getColor(this,R.color.selectedTabTextColor));

        //Adding new tabs in the TabLayout.
        tabLayout_detail_Activity_resourceType_switcher.addTab(
                tabLayout_detail_Activity_resourceType_switcher.newTab().setText(getString(R.string.option_trailers))
        );
        tabLayout_detail_Activity_resourceType_switcher.addTab(
                tabLayout_detail_Activity_resourceType_switcher.newTab().setText(getString(R.string.option_reviews))
        );

        //Adding OnTabSelectedListener to the TabLayout
        tabLayout_detail_Activity_resourceType_switcher.addOnTabSelectedListener(this);

        //Handling the app behavior on Create and on rotation.
        if(savedInstanceState !=null){
            if(savedInstanceState.containsKey(SELECTED_TAB_INDEX_KEY)){
                int position = savedInstanceState.getInt(SELECTED_TAB_INDEX_KEY);
                tabLayout_detail_Activity_resourceType_switcher.getTabAt(position).select();
            }
        }else {
            tabLayout_detail_Activity_resourceType_switcher.getTabAt(0).select();
        }
        fetchMovieReviewData();
        fetchMovieTrailersData();
    }


    /**
     * Provide implementation of what happens when a tab is selected.
     * @param tab
     */
    @Override
    public void onTabSelected(TabLayout.Tab tab) {

        Log.d(TAG,"Currently Selected Tab: "+tab.getPosition());

        if(tab.getPosition() == 0){
            fetchMovieTrailersData();
        }else{
            fetchMovieReviewData();
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
        outState.putInt(SELECTED_TAB_INDEX_KEY,tabLayout_detail_Activity_resourceType_switcher.getSelectedTabPosition());
    }

    /**
     * I have observed an issue with the loading of Trailer and Reviews on start up. Sometimes they load as they should and
     * sometimes they don't on launch of DetailActivity.
     * Hence, we have added this countermeasure. However I am trying to find out a better solution.
     *
     */
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG,"onStart called");
        if(tabLayout_detail_Activity_resourceType_switcher.getTabAt(0).isSelected()){
            fetchMovieTrailersData();
        }else {
            fetchMovieReviewData();
        }
    }


    /**
     * This method displays the data about a Movie in the ui and
     * @param selectedMovieDataCursor //Cursor with the Movie data
     */
    private void displayMovieDetails(Cursor selectedMovieDataCursor){

        fetchMovieReviewData();

        if(selectedMovieDataCursor.getCount() > 0){
            selectedMovieDataCursor.moveToFirst();
            String selectedMovieOriginalTitle = selectedMovieDataCursor.getString(INDEX_MOVIE_ORIGINAL_TITLE);
            String selectedMovieBackdropURL = selectedMovieDataCursor.getString(INDEX_MOVIE_BACKDROP_URL);
            String selectedMoviePosterURL = selectedMovieDataCursor.getString(INDEX_MOVIE_POSTER_URL);
            String selectedMoviePlotSynopsis = selectedMovieDataCursor.getString(INDEX_MOVIE_PLOT_SYNOPSIS);
            float selectedMovieRating = selectedMovieDataCursor.getFloat(INDEX_MOVIE_RATING);

            String selectedMovieReleaseDate;

            if(selectedMovieDataCursor.getString(INDEX_MOVIE_RELEASE_DATE).toString().equals("")) {
                selectedMovieReleaseDate = getString(R.string.not_released);
            }else {
                selectedMovieReleaseDate = selectedMovieDataCursor.getString(INDEX_MOVIE_RELEASE_DATE);
            }

            Picasso.with(context).setLoggingEnabled(true);
            Picasso.with(context)
                    .load(selectedMovieBackdropURL)
                    .placeholder(R.drawable.placeholder_small_stacked_blue)
                    .error(R.drawable.placeholder_small_stacked_blue)
                    .into(imageView_detailActivity_backdrop);

            Picasso.with(context)
                    .load(selectedMoviePosterURL)
                    .placeholder(R.drawable.placeholder_small_stacked_blue)
                    .error(R.drawable.placeholder_small_stacked_blue)
                    .into(imageView_detailActivity_poster);
            context = DetailActivity.this;

            textView_detailActivity_originalTitle.setText(selectedMovieOriginalTitle);
            textView_detailActivity_movie_releaseDate.setText(selectedMovieReleaseDate);

            ratingBar_detailActivity_movieRating.setNumStars(10);
            ratingBar_detailActivity_movieRating.setStepSize(1);
            ratingBar_detailActivity_movieRating.setRating(selectedMovieRating);

            textView_detailActivity_plot_synopsis.setText(selectedMoviePlotSynopsis);

            if(!MovieDataUtils.checkConnectionStatusInBackground(context)){
                Toast.makeText(context,getString(R.string.no_connectivity),Toast.LENGTH_SHORT).show();
            }
        }
        fetchMovieTrailersData();
    }

    /**
     * Fetches and loads the Movie Trailers from the database in background thread.
     */
    public void fetchMovieTrailersData(){

        Cursor trailerCursor=null;

        //Constructing the Uri for fetching the Trailers data for the currently displayed Movie.
        final Uri selectedMovieTrailerDataUri = MovieContract.Trailers.TRAILERS_CONTENT_URI.buildUpon()
                .appendPath(String.valueOf(currentlyDisplayedMovieTMDBId))
                .build();

        //Defining AsyncTask for performing database query in the background thread.
        AsyncTask fetchMovieTrailerAsyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {

                Cursor cursor = getContentResolver().query(selectedMovieTrailerDataUri
                        ,DISPLAY_MOVIE_TRAILER_PROJECTION
                        ,null
                        ,null
                        ,null
                        ,null);

                Log.d(TAG,"URI: "+selectedMovieTrailerDataUri);
                Log.d(TAG,"Count: "+cursor.getCount());
                return cursor;
            }
        };

        //Executing AsyncTask.
        try {
            trailerCursor = (Cursor) fetchMovieTrailerAsyncTask.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Log.d(TAG,"No of Trailers: "+trailerCursor.getCount());

        //Initializing MovieTrailerResourceAdapter
        movieTrailerResourceAdapter = new MovieTrailerResourceAdapter(trailerCursor,this);

        //Configuring the Resource Switcher RecyclerView.
        recyclerView_activity_Detail_resource_switcher.setLayoutManager(new LinearLayoutManager(this));
        movieTrailerResourceAdapter.setOnMovieTrailerClikcListener(this);
        recyclerView_activity_Detail_resource_switcher.setAdapter(movieTrailerResourceAdapter);

    }

    /**
     * Fetches and loads the Movie Trailers from the database in background thread.
     */
    public void fetchMovieReviewData(){

        Cursor reviewCursor = null;

        //Constructing the Uri for fetching the Reviews data for the currently displayed Movie.
        final Uri movieReviewDataUri = MovieContract.Reviews.REVIEWS_CONTENT_URI.buildUpon()
                .appendPath(String.valueOf(currentlyDisplayedMovieTMDBId))
                .build();

        //Defining AsyncTask for performing database query in the background thread.
        AsyncTask fetchMovieReviewsAsyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                Cursor cursor = getContentResolver().query(movieReviewDataUri
                        ,DISPLAY_MOVIE_REVIEW_PROJECTION
                        ,null
                        ,null
                        ,null);
                return cursor;
            }
        };

        //Executing AsyncTask
        try {
            reviewCursor = (Cursor) fetchMovieReviewsAsyncTask.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Log.d(TAG,"No of Reviews: "+reviewCursor.getCount());

        movieReviewResourceAdapter = new MovieReviewResourceAdapter(reviewCursor,this);
        //Configuring the Resource Switcher RecyclerView.
        recyclerView_activity_Detail_resource_switcher.setLayoutManager(new LinearLayoutManager(this));
        recyclerView_activity_Detail_resource_switcher.setAdapter(movieReviewResourceAdapter);
    }


    /**
     * Adds the necessary polish to the DetailActivity ui using a progress bar while the data is loading behind the scene.
     */
    private void showProgress(){
        progressBar_detailActivity_loading.setVisibility(View.VISIBLE);
        imageView_detailActivity_poster.setVisibility(View.INVISIBLE);
        imageView_detailActivity_backdrop.setVisibility(View.INVISIBLE);
        textView_detailActivity_originalTitle.setVisibility(View.INVISIBLE);
        ratingBar_detailActivity_movieRating.setVisibility(View.INVISIBLE);
        textView_detailActivity_movie_releaseDate.setVisibility(View.INVISIBLE);
        textView_detailActivity_plot_synopsis.setVisibility(View.INVISIBLE);
        tabLayout_detail_Activity_resourceType_switcher.setVisibility(View.INVISIBLE);
        recyclerView_activity_Detail_resource_switcher.setVisibility(View.INVISIBLE);
        cardView.setVisibility(View.INVISIBLE);
    }

    /**
     * On the data load makes the ProgressBar invisible and displays the data.
     */
    private void showData(){
        progressBar_detailActivity_loading.setVisibility(View.INVISIBLE);
        cardView.setVisibility(View.VISIBLE);
        imageView_detailActivity_poster.setVisibility(View.VISIBLE);
        imageView_detailActivity_backdrop.setVisibility(View.VISIBLE);
        textView_detailActivity_originalTitle.setVisibility(View.VISIBLE);
        ratingBar_detailActivity_movieRating.setVisibility(View.VISIBLE);
        textView_detailActivity_movie_releaseDate.setVisibility(View.VISIBLE);
        textView_detailActivity_plot_synopsis.setVisibility(View.VISIBLE);
        tabLayout_detail_Activity_resourceType_switcher.setVisibility(View.VISIBLE);
        recyclerView_activity_Detail_resource_switcher.setVisibility(View.VISIBLE);
    }

    /**
     * Overridden method onCreateOptionsMenu provides the implementation for inflating Overflow menu at AppBar
     * in DetailActivity
     * @param menu // instance of the Menu
     * @return boolean // in order to display the Menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detailactivitymenu,menu);
        return true;
    }

    /**
     * Overridden method onOptionsItemSelected provides the implementation for menu items included in the Overflow menu
     * at AppBar in DetailActivity.
     * @param item // instance of the MenuItem
     * @return boolean // in order to activate the MenuItem
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedItemId = item.getItemId();
        context = DetailActivity.this;
        switch (selectedItemId){
            case (android.R.id.home):
                onBackPressed();
                return true;
            case (R.id.detailActivityMenuItem_share):
                shareMovieInformation();
                return true;
            case (R.id.detailActivityMenuItem_findInWeb):
                findMovieInWeb();
                return true;
            case (R.id.detailActivityMenuItem_Favorite):
                if(popupMessage != null){
                    popupMessage.cancel();
                }
                if(markMovieAsFavorite()){
                    popupMessage = Toast.makeText(this,getString(R.string.marked_as_favorite),Toast.LENGTH_SHORT);
                }else {
                    popupMessage = Toast.makeText(this,getString(R.string.already_in_favorite),Toast.LENGTH_SHORT);
                }
                popupMessage.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundleArgument) {

        switch (loaderId){
            case DISPLAY_SELECTED_MOVIE_DATA_LOADER_ID:
                return new CursorLoader(this
                        ,selectedMovieDataUri
                        ,MOVIE_DISPLAY_DATA_PROJECTION
                        ,null
                        ,null
                        ,null);
            default:
                throw new RuntimeException("Loader not implemented: "+loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor selectedMovieDataCursor) {

        if(selectedMovieDataCursor.getCount() > 0){
            Log.d(TAG,"Cursor Size: "+selectedMovieDataCursor.getCount());
            selectedMovieDataCursor.moveToFirst();
            displayMovieDetails(selectedMovieDataCursor);
            //Setting the Adapter to display the Movie Trailers and Review in RecyclerView
            recyclerView_activity_Detail_resource_switcher.setAdapter(movieReviewResourceAdapter);
            recyclerView_activity_Detail_resource_switcher.setAdapter(movieTrailerResourceAdapter);
        }
        showData();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    /**
     * Method shareMovieInformation with the help of ShareCompat.IntentBuilder() method. This method would be called upon
     * entering the share icon in the DetailActivity. This method will fetch the required data from the database in the
     * background thread.
     */
    private void shareMovieInformation(){
        //Defining MIME type
        final String mimeType = "text/plain";
        //Defining the minimal projection for the require data for sharing
        final String[] MOVIE_SHARE_INFO_PROJECTION = new String[]{
                MovieContract.Movies.MOVIE_ORIGINAL_TITLE,
                MovieContract.Movies.MOVIE_WEB_URL
        };
        //Defining the cursor index based on minimal projection.
        final int INDEX_SHARE_INFO_TITLE_INDEX = 0;
        final int INDEX_SHARE_INFO_WEB_URL = 1;

        //Defining the Async Task to handle the database operation in background thread.
        AsyncTask fetchMovieShareInfo = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                Cursor movieShareInfoCursor = getContentResolver().query(selectedMovieDataUri
                        ,MOVIE_SHARE_INFO_PROJECTION
                        ,null,null,null,null);
                Log.d(TAG,"Retrieved Shared Info: "+movieShareInfoCursor.getCount());
                movieShareInfoCursor.moveToFirst();
                //Creating ShareCompat Intent for sharing data.
                ShareCompat.IntentBuilder.from(DetailActivity.this)
                        .setChooserTitle(movieShareInfoCursor.getString(INDEX_SHARE_INFO_TITLE_INDEX))
                        .setType(mimeType)
                        .setText(movieShareInfoCursor.getString(INDEX_SHARE_INFO_WEB_URL))
                        .startChooser();
                return null;
            }
        };
        fetchMovieShareInfo.execute();
    }

    /**
     * Method findMovieInWeb creates a Uri from the Movie Web URL and triggers the Intent.ACTION_VIEW to view the content
     * of the Uri. This method will fetch the required data from the database in the background thread.
     */
    private void findMovieInWeb(){
        //Defining the minimal projection for the require data
        final String[] MOVIE_WEB_INFO_PROJECTION = new String[]{
                MovieContract.Movies.MOVIE_WEB_URL
        };
        //Defining the cursor index based on minimal projection
        final int INDEX_MINIMAL_WEB_URL = 0;

        //Defining the Async Task to handle the database operation in background thread.
        AsyncTask webInfo = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                Cursor movieWebInfoCursor = getContentResolver().query(selectedMovieDataUri
                ,MOVIE_WEB_INFO_PROJECTION
                ,null,null,null,null);
                movieWebInfoCursor.moveToFirst();
                Uri movieWebUri = Uri.parse(movieWebInfoCursor.getString(INDEX_MINIMAL_WEB_URL));
                Intent intent = new Intent(Intent.ACTION_VIEW,movieWebUri);
                if(intent.resolveActivity(getPackageManager())!=null){
                    startActivity(intent);
                }
                return null;
            }
        };
        webInfo.execute();
    }

    /**
     * Mark a Movie as Favorite after checking that the movie is not marked as Favorite already.
     * @return //boolean true if the Movie is not already Favorite and false otherwise.
     */
    private boolean markMovieAsFavorite(){

        boolean markedAsFavorite = false;

        //Defining minimal projection to check if the Movie is already marked as Favorite.
        final String[] FAVORITE_MOVIE_CHECK_MINIMAL_PROJECTION = new String[]{
                MovieContract.Movies.IS_FAVORITE_MOVIE
        };

        //Defining Cursor index based on minimal projection.
        final int INDEX_IS_FAVORITE_MOVIE = 0;

        //Defining the Async Task to handle the database operation in background thread.
        AsyncTask markMovieAsFavorite = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                boolean updateFlag = false;
                //Fetching data to check if already marked as favorite
                Cursor checkFavoriteMovieCursor = getContentResolver().query(selectedMovieDataUri
                        ,FAVORITE_MOVIE_CHECK_MINIMAL_PROJECTION
                        ,null,null,null);
                checkFavoriteMovieCursor.moveToFirst();
                //Retrieving the value of the IS_FAVORITE_MOVIE field
                int isFavoriteMovie = checkFavoriteMovieCursor.getInt(INDEX_IS_FAVORITE_MOVIE);

                //Checking if already a favorite or not
                if(isFavoriteMovie == getResources().getInteger(R.integer.value_of_non_favorite_movie)){
                    ContentValues markFavoriteContentValues = new ContentValues();

                    //Marking as favorite if not
                    markFavoriteContentValues.put(MovieContract.Movies.IS_FAVORITE_MOVIE
                            ,getResources().getInteger(R.integer.value_of_favorite_movie));
                    int rowsUpdated = getContentResolver().update(selectedMovieDataUri
                            ,markFavoriteContentValues
                            ,null
                            ,null);
                    if(rowsUpdated == 1){
                        updateFlag = true;
                    }
                }
                return  updateFlag;
            }
        };

        //Getting the result from AsyncTask
        try {
            markedAsFavorite = (boolean) markMovieAsFavorite.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return markedAsFavorite;
    }

    /**
     * Launches the Movie Trailer in the Browser or native installed YouTube Application with an
     * implicit Intent
     * @param movieTrailerYoutubeKey
     */
    @Override
    public void onMovieTrailerClick(String movieTrailerYoutubeKey) {
        Intent youTubeTrailerIntent = new Intent(Intent.ACTION_VIEW
                , Uri.parse(getResources().getString(R.string.youTube_prefix_scheme) + movieTrailerYoutubeKey));
        if(youTubeTrailerIntent.resolveActivity(getPackageManager()) !=null){
            startActivity(youTubeTrailerIntent);
        }else {
            if(popupMessage !=null){
                popupMessage.cancel();
            }
            popupMessage = Toast.makeText(this,getString(R.string.no_package_manager_found),Toast.LENGTH_SHORT);
            popupMessage.show();
        }
    }
}
