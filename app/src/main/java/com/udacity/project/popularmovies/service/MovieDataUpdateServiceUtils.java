package com.udacity.project.popularmovies.service;

import android.content.Context;
import android.support.annotation.NonNull;

import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

/**
 * Schedules the Job with FirebaseJobDispatcher
 * Created by chandan on 02.04.17.
 */
public class MovieDataUpdateServiceUtils {

    //Time interval the Job needs to wait before it starts (in hours)
    private static final int UPDATE_INTERVAL_HOURS = 12;
    //Converting the interval of Job in to seconds
    private static final int UPDATE_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(UPDATE_INTERVAL_HOURS);
    //Time that we want the Job to have as the scope of execution
    private static final int EXECUTION_SCOPE_SECONDS = UPDATE_INTERVAL_SECONDS;

    //Identity of our Job
    private static final String UPDATE_JOB_TAG = "movie-update-tag";

    //Determines whether the Job is initialized
    private static boolean isInitialized;

    /**
     * Schedules the Job to run in background in said interval and update the Movie data. This method must not be executed
     * more than once at a time.
     * @param context
     */
    public static synchronized void scheduleMovieDataUpdate(@NonNull Context context){
        //We return if the Job is already initialized.
        if(isInitialized){
            return;
        }

        //Initializing the FirebaseJobDispatcher with GooglePlayDriver instance
        Driver googlePlayDriver = new GooglePlayDriver(context);
        FirebaseJobDispatcher firebaseJobDispatcher = new FirebaseJobDispatcher(googlePlayDriver);

        //Creating a recurring Job using the FirebaseJobDispatcher.
        Job movieDataUpdateJob = firebaseJobDispatcher.newJobBuilder()
                .setService(MovieDataUpdateFirebaseJobService.class)
                .setTag(UPDATE_JOB_TAG)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(UPDATE_INTERVAL_HOURS
                        ,UPDATE_INTERVAL_HOURS+EXECUTION_SCOPE_SECONDS))
                .setReplaceCurrent(true)
                .build();
        //Scheduling the Job
        firebaseJobDispatcher.schedule(movieDataUpdateJob);
        //Setting the job as initialized.
        isInitialized = true;
    }
}
