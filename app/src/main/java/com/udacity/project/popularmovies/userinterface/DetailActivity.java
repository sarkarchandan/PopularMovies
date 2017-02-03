package com.udacity.project.popularmovies.userinterface;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
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
import com.udacity.project.popularmovies.R;

import com.squareup.picasso.Picasso;
import com.udacity.project.popularmovies.model.Movie;
import com.udacity.project.popularmovies.utilities.MovieDataUtils;

import java.util.concurrent.ExecutionException;

public class DetailActivity extends AppCompatActivity {

    /*Constant to be used for logging*/
    private final String TAG = DetailActivity.class.getSimpleName();
    /*Constant to be used for getting the Parcelable object from the Intent*/
    private static final String PARCELABLE_MOVIE = "parcelable_movie";

    private Toolbar toolBar_activity_detail;
    private TextView textView_detailActivity_error_message;
    private ImageView imageView_detailActivity_backdrop;
    private ImageView imageView_detailActivity_poster;
    private TextView textView_detailActivity_originalTitle;
    private RatingBar ratingBar_detailActivity_movieRating;
    private TextView textView_detailActivity_movie_releaseDate;
    private TextView textView_detailActivity_plot_synopsis;
    private ProgressBar progressBar_detailActivity_loading;
    private CardView cardView;
    private Context context;
    private Intent movieIntent;
    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        toolBar_activity_detail = (Toolbar) findViewById(R.id.toolBar_activity_detail);
        textView_detailActivity_error_message = (TextView)findViewById(R.id.textView_detailActivity_error_message);
        setSupportActionBar(toolBar_activity_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //setStatusBarTranslucent(true);


        progressBar_detailActivity_loading = (ProgressBar)findViewById(R.id.progressBar_detailActivity_loading);
        imageView_detailActivity_backdrop = (ImageView) findViewById(R.id.imageView_detailActivity_backdrop);
        imageView_detailActivity_poster = (ImageView) findViewById(R.id.imageView_detailActivity_poster);
        textView_detailActivity_originalTitle = (TextView) findViewById(R.id.textView_detailActivity_originalTitle);
        ratingBar_detailActivity_movieRating = (RatingBar) findViewById(R.id.ratingBar_detailActivity_movieRating);
        textView_detailActivity_movie_releaseDate = (TextView) findViewById(R.id.textView_detailActivity_movie_releaseDate);
        textView_detailActivity_plot_synopsis = (TextView) findViewById(R.id.textView_detailActivity_plot_synopsis);
        cardView = (CardView) findViewById(R.id.cardView);
        showProgress();

        movieIntent = getIntent();
        if (movieIntent != null && movieIntent.hasExtra(PARCELABLE_MOVIE)) {
            movie = movieIntent.getExtras().getParcelable(PARCELABLE_MOVIE);
            Log.d(TAG, movie.getMovieOriginalTitle());
            loadMovieData(movie);
        }
    }

    /**
     * This method displays the data about a Movie in the ui and
     * @param movie
     */
    public void loadMovieData(Movie movie){
        context = DetailActivity.this;
        try {
            if(new DetailActivityConnectionStatusUtil().execute().get()){
                Picasso.with(context).setLoggingEnabled(true);
                Picasso.with(context)
                        .load(movie.getMovieBackDropUrl())
                        .placeholder(R.drawable.placeholder_small_stacked_blue)
                        .error(R.drawable.placeholder_small_stacked_blue)
                        .into(imageView_detailActivity_backdrop);
                Picasso.with(context)
                        .load(movie.getMoviePosterUrl())
                        .placeholder(R.drawable.placeholder_small_stacked_blue)
                        .error(R.drawable.placeholder_small_stacked_blue)
                        .into(imageView_detailActivity_poster);
                textView_detailActivity_originalTitle.setText(movie.getMovieOriginalTitle());
                textView_detailActivity_movie_releaseDate.setText(movie.getMovieReleaseDate());
                ratingBar_detailActivity_movieRating.setNumStars(10);
                ratingBar_detailActivity_movieRating.setStepSize(1);
                ratingBar_detailActivity_movieRating.setRating(movie.getMovieRating());
                textView_detailActivity_plot_synopsis.setText("Synopsis: ");
                textView_detailActivity_plot_synopsis.append("\n"+movie.getMoviePlotSynopsis());
                showData();
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
     * The method setStatusBarTranslucent makes the top status bar of the device window translucent when needed.
     * @param makeTranslucent // boolean flag to set the status bar translucent.
     */
    public void setStatusBarTranslucent(boolean makeTranslucent){
        if(makeTranslucent){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }else{
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * Adds the necessary polish to the DetailActivity ui using a progress bar while the data is loading behind the scene.
     */
    public void showProgress(){
        progressBar_detailActivity_loading.setVisibility(View.VISIBLE);
        textView_detailActivity_error_message.setVisibility(View.INVISIBLE);
        imageView_detailActivity_poster.setVisibility(View.INVISIBLE);
        imageView_detailActivity_backdrop.setVisibility(View.INVISIBLE);
        textView_detailActivity_originalTitle.setVisibility(View.INVISIBLE);
        ratingBar_detailActivity_movieRating.setVisibility(View.INVISIBLE);
        textView_detailActivity_movie_releaseDate.setVisibility(View.INVISIBLE);
        textView_detailActivity_plot_synopsis.setVisibility(View.INVISIBLE);
        cardView.setVisibility(View.INVISIBLE);
    }

    /**
     * On the data load makes the ProgressBar invisible and displays the data.
     */
    public void showData(){
        progressBar_detailActivity_loading.setVisibility(View.INVISIBLE);
        textView_detailActivity_error_message.setVisibility(View.INVISIBLE);
        cardView.setVisibility(View.VISIBLE);
        imageView_detailActivity_poster.setVisibility(View.VISIBLE);
        imageView_detailActivity_backdrop.setVisibility(View.VISIBLE);
        textView_detailActivity_originalTitle.setVisibility(View.VISIBLE);
        ratingBar_detailActivity_movieRating.setVisibility(View.VISIBLE);
        textView_detailActivity_movie_releaseDate.setVisibility(View.VISIBLE);
        textView_detailActivity_plot_synopsis.setVisibility(View.VISIBLE);
    }

    /**
     * Method showNetworkError is triggered in case we have a disruption in network connectivity.
     */
    public void showNetworkError(){
        progressBar_detailActivity_loading.setVisibility(View.INVISIBLE);
        cardView.setVisibility(View.INVISIBLE);
        textView_detailActivity_error_message.setVisibility(View.VISIBLE);
        imageView_detailActivity_poster.setVisibility(View.INVISIBLE);
        imageView_detailActivity_backdrop.setVisibility(View.INVISIBLE);
        textView_detailActivity_originalTitle.setVisibility(View.INVISIBLE);
        ratingBar_detailActivity_movieRating.setVisibility(View.INVISIBLE);
        textView_detailActivity_movie_releaseDate.setVisibility(View.INVISIBLE);
        textView_detailActivity_plot_synopsis.setVisibility(View.INVISIBLE);
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
            case (R.id.detailActivityMenuItem_share):
                shareMovieInformation();
                Log.i(TAG,movie.getMovieOriginalTitle()+" shared");
                return true;
            case (R.id.detailActivityMenuItem_findInWeb):
                findMovieInWeb();
                Log.i(TAG,movie.getMovieOriginalTitle()+" triggered to find in web");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Method shareMovieInformation with the help of ShareCompat.IntentBuilder() method. This method would be called upon
     * entering the share icon in the DetailActivity.
     */
    public void shareMovieInformation(){
        String mimeType = "text/plain";
        String title = movie.getMovieOriginalTitle();
        ShareCompat.IntentBuilder.from(DetailActivity.this)
                .setChooserTitle(title)
                .setType(mimeType)
                .setText(movie.getMovieWebUrl())
                .startChooser();
    }

    /**
     * Method findMovieInWeb creates a Uri from the Movie Web URL and triggers the Intent.ACTION_VIEW to view the content
     * of the Uri.
     */
    public void findMovieInWeb(){
        Uri movieWebUri = Uri.parse(movie.getMovieWebUrl());
        Intent intent = new Intent(Intent.ACTION_VIEW,movieWebUri);
        if(intent.resolveActivity(getPackageManager())!=null){
            startActivity(intent);
        }
    }

    /**Inner class for the DetailActivity to check the network connection in the device in background task*/
    public class DetailActivityConnectionStatusUtil extends AsyncTask<Void,Void,Boolean> {
        @Override
        protected void onPreExecute() {
            progressBar_detailActivity_loading.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return MovieDataUtils.checkConnectivityStatus(DetailActivity.this);
        }
    }
}
