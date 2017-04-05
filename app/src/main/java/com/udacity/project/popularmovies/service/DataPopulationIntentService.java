package com.udacity.project.popularmovies.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.udacity.project.popularmovies.userinterface.DetailActivity;

/**
 * IntentService class that handles the movie data population in background.
 * Created by chandan on 01.04.17.
 */
public class DataPopulationIntentService extends IntentService {

    public DataPopulationIntentService() {
        super("DataPopulationIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        String action = intent.getAction();

        //This integer id will contain the TMDB MovieId when the IntentService will be launched by DetailActivity. Otherwise this
        //integer id will carry a default value of 0.
        int movieTMDBId = intent.getIntExtra(DetailActivity.SELECTED_MOVIE_RESOURCES_INTENT_KEY,DetailActivity.MOVIE_ID_DEFAULT_VALUE);

        DataPopulationTasks.executeTask(this,action,movieTMDBId);
    }
}
