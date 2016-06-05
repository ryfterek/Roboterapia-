package pbl7roboterapia.pbl7;

import android.app.ActivityManager;
import android.content.Context;

/**
 * Created by Szymon on 2016-06-05.
 */

/** Provides a method checking if the service is running already in the background, found at
 *  http://stackoverflow.com/questions/600207/how-to-check-if-a-service-is-running-on-android
 * */
public class ServiceCheck {
    Context context;

    public ServiceCheck (Context context){
        this.context = context;
    }
    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
