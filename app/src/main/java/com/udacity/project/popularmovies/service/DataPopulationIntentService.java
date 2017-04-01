package com.udacity.project.popularmovies.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

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
        DataPopulationTasks.executeTask(this,action);
    }
}
