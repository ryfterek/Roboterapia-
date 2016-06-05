
package pbl7roboterapia.pbl7;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.NotificationCompat;

/**
 * Created by Szymon on 2016-06-05.
 */

public class WiFiReceiver extends BroadcastReceiver {

    private NotificationCompat.Builder pushNotification;
    private final static int pushNotiID = 1234;

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        /** Disconnection from WiFi is detected here */
        if (activeNetwork.getType() != ConnectivityManager.TYPE_WIFI) {

            pushNotification = new NotificationCompat.Builder(context);
            pushNotification.setSmallIcon(R.mipmap.ic_launcher);
            pushNotification.setWhen(System.currentTimeMillis());
            pushNotification.setContentTitle("Roboterapia");
            pushNotification.setContentText("Lost WiFi");
            pushNotification.setAutoCancel(true);

            NotificationManager nm = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
            nm.notify(pushNotiID, pushNotification.build());

        /** Reconnection to WiFi is detected here */
        }else{

        }

    }
}