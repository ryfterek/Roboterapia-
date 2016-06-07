package pbl7roboterapia.pbl7;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

/** Default Activity being opened on icon tap. Check if a user has already provided login and if AppService is running*/

public class MainActivity extends AppCompatActivity {

    /**SharedPreference is the most compact way to save variables on device's memory */
    private SharedPreferences sharedPref;
    private static final int PREFERENCE_MODE_PRIVATE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        /** Opening SharedPreferences for future use */
        sharedPref = getSharedPreferences("database",PREFERENCE_MODE_PRIVATE);

        /** Checking if the user had logged in already in the past. If not, redirecting to login screen */
        if (!(sharedPref.getBoolean("LOGGED", false))){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        /** If yes, redirecting to idle state screen */
        else{
            ServiceCheck serviceCheck = new ServiceCheck(this);
            /** Checking if the AppService already running in background if not
            * Starting our internal service which will handle the notifications & signals from MQTT Service */
            if(!serviceCheck.isMyServiceRunning(AppService.class)) {
                Intent service = new Intent(this, AppService.class);
                startService(service);
            }

            /** Redirecting */
            switch (States.STATES.valueOf(sharedPref.getString("STATE","IDLE"))){
                case IDLE:
                    Intent idleIntent = new Intent(this, IdleActivity.class);
                    startActivity(idleIntent);
                    finish();
                    break;
                case ALARM:
                    Intent alarmIntent = new Intent(this, AlarmActivity.class);
                    startActivity(alarmIntent);
                    finish();
                    break;
                case NEEDED:
                    Intent neededIntent = new Intent(this, NeededActivity.class);
                    startActivity(neededIntent);
                    finish();
                    break;
                case VOLUNTEER:
                    Intent volunteerIntent = new Intent(this, VolunteerActivity.class);
                    startActivity(volunteerIntent);
                    finish();
                    break;
            }
        }
    }
}
