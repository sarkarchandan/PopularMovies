package com.udacity.project.popularmovies.utilities;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import com.udacity.project.popularmovies.R;
import com.udacity.project.popularmovies.userinterface.MainActivity;

/**
 * Handles the Notification when a new MovieData is available and recorded in the data store.
 * Created by chandan on 02.04.17.
 */
public class NotificationUtils {

    //Identifier for the Pending Intent we are going to create
    private static final int MOVIE_UPDATE_PENDING_INTENT_ID = 2345;
    //Identifier for the Notification
    private static final int MOVIE_UPDATE_NOTIFICATION_ID = 6789;

    /**
     * Cretaes and returns a PendingIntent out of an explicit Intent to open the MainActivity upon tapping on the Notification.
     * @param context //App context
     * @return //PendingIntent
     */
    private static PendingIntent contentIntent(Context context){
        //Creating explicit Intent to open MainActivity of the PopularMovies app when requested
        Intent startPopularMoviesMainActivityIntent = new Intent(context, MainActivity.class);
        //Wrapping the explicit Intent inside the PendingIntent and return the PendingIntent
        return PendingIntent.getActivity(context
        ,MOVIE_UPDATE_PENDING_INTENT_ID
        ,startPopularMoviesMainActivityIntent
        ,PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Cretaes a Bitmap resource for the Notification from a provided Drawable resource.
     * @param context
     * @return
     */
    private static Bitmap createlargeIconBitmap(Context context){
        Resources resources = context.getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.tmdb_notification_logo_green);
        return bitmap;
    }

    /**
     * Defines and configures the Notification for the PopularMovies app.
     * @param context //Application Context reference
     */
    public static void createMovieUpdateNotification(Context context){
        //Creating and customizing the Notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setColor(ContextCompat.getColor(context,R.color.colorPrimary))
                .setSmallIcon(R.drawable.placeholder_small_stacked_blue)
                .setLargeIcon(createlargeIconBitmap(context))
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(context.getString(R.string.notification_body))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(context.getString(R.string.notification_body)))
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentIntent(contentIntent(context))
                .setAutoCancel(true);

        //Setting the Notification Priority to High to enable heads up style which is only available in JellyBean and above.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
        }

        //Initializing an instance of the NotificationManager
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //Display the Notification.
        notificationManager.notify(MOVIE_UPDATE_NOTIFICATION_ID,notificationBuilder.build());
    }
}
