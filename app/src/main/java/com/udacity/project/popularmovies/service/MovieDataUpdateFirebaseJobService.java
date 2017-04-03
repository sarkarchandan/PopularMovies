package com.udacity.project.popularmovies.service;

import android.content.Context;
import android.os.AsyncTask;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.udacity.project.popularmovies.utilities.NotificationUtils;

/**
 * Handles the execution of the scheduled JobService in the background.
 * Created by chandan on 02.04.17.
 */
public class MovieDataUpdateFirebaseJobService extends JobService{

    private AsyncTask movieDataUpdateAsyncTask;

    /**
     * Executes the job in the background when triggered.
     * @param jobParameters
     * @return //boolean
     */
    @Override
    public boolean onStartJob(final JobParameters jobParameters) {

        movieDataUpdateAsyncTask = new AsyncTask(){
            @Override
            protected Object doInBackground(Object[] objects) {
                Context context = MovieDataUpdateFirebaseJobService.this;
                int noOfMoviesUpdated;
                /*
                 * We want the same task to execute periodically which our IntentService executes on start up, in order to keep
                 * the database updated whenever the user runs the app.
                 */
                noOfMoviesUpdated = DataPopulationTasks.executeTask(context,DataPopulationTasks.ACTION_POPULATE_MOVIE_DATA);
                if(noOfMoviesUpdated != 0){
                    NotificationUtils.createMovieUpdateNotification(context);
                }
                return null;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                jobFinished(jobParameters,false);
            }
        };
        movieDataUpdateAsyncTask.execute();
        return true;
    }

    /**
     * Cancels the job and determines if the job needs to reschedule.
     * @param job
     * @return //boolean
     */
    @Override
    public boolean onStopJob(JobParameters job) {
        if(movieDataUpdateAsyncTask !=null){
            movieDataUpdateAsyncTask.cancel(true);
        }
        return true;
    }
}
