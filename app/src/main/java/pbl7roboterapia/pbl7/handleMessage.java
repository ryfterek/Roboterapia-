package pbl7roboterapia.pbl7;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;

/**
 * Created by Szymon on 2016-06-06.
 */
public class HandleMessage {

    /**SharedPreference is the most compact way to save variables on device's memory */
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor sharedEdit;
    private static final int PREFERENCE_MODE_PRIVATE = 0;

    /** Context is needed to open SharedPreferences */
    Context context;

    /** Variables needed to handle notifications */
    private NotificationCompat.Builder pushNotification;
    private final static int pushNotiID = 7002;

    public HandleMessage(Context context){
        this.context = context;
    }

    public void handle(String message){

        /** Opening SharedPreferences for future use */
        sharedPref = context.getSharedPreferences("database",PREFERENCE_MODE_PRIVATE);

        String[] details = message.split(":");

        switch (details[0]){
            case "0":
                if (!sharedPref.getBoolean("SENDER", true)) {
                    long[] pattern = {0, 500, 500, 500};
                    pushNotification = new NotificationCompat.Builder(context);
                    pushNotification.setSmallIcon(R.mipmap.ic_launcher);
                    pushNotification.setWhen(System.currentTimeMillis());
                    pushNotification.setContentTitle(details[1] + context.getResources().getString(R.string.notification_title_alarm));
                    pushNotification.setContentText(context.getResources().getString(R.string.notification_body_alarm));
                    pushNotification.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                    pushNotification.setVibrate(pattern);
                    pushNotification.setOngoing(true);
                    pushNotification.setAutoCancel(true);

                    Intent intentNoti = new Intent(context, AlarmActivity.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentNoti, PendingIntent.FLAG_UPDATE_CURRENT);
                    pushNotification.setContentIntent(pendingIntent);

                    NotificationManager nm = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
                    nm.notify(pushNotiID, pushNotification.build());
                }
                break;

        }

    }
}
