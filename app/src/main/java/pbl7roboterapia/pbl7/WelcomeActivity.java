package pbl7roboterapia.pbl7;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class WelcomeActivity extends AppCompatActivity {

    /**SharedPreference is the most compact way to save variables on device's memory */
    private SharedPreferences sharedPref;
    private static final int PREFERENCE_MODE_PRIVATE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        /** Opening SharedPreferences for future use */
        sharedPref = getSharedPreferences("database",PREFERENCE_MODE_PRIVATE);

        /** Checking if the AppService already running in background */
        /** Starting our internal service which will handle the notifications & signals from MQTT Service */
        if(isMyServiceRunning(AppService.class) == false) {
            Intent service = new Intent(this, AppService.class);
            startService(service);
        }

        /** Checking if the user had logged in already in the past. If not, redirecting to login screen */
        if (!(sharedPref.getBoolean("LOGGED", false))){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        /** If yes, redirecting to idle state screen */
        else{
            Intent intent = new Intent(this, IdleActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /** Method checking if the service is running already in the background, found at http://stackoverflow.com/questions/600207/how-to-check-if-a-service-is-running-on-android */
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
