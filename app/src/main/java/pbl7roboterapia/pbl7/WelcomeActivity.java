package pbl7roboterapia.pbl7;

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

        /** Starting our internal service which will handle the notifications & signals from MQTT Service */
        Intent service = new Intent(this, AppService.class);
        startService(service);

        /** Checking if the user had logged in already in the past. If not, redirecting to login screen */
        if (!(sharedPref.getBoolean("LOGGED", false))){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        else{
            Intent intent = new Intent(this, IdleActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
