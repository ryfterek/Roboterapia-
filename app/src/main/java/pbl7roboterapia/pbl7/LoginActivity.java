package pbl7roboterapia.pbl7;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;

public class LoginActivity extends AppCompatActivity {

    /**SharedPreference is the most compact way to save variables on device's memory */
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor sharedEdit;
    private static final int PREFERENCE_MODE_PRIVATE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sharedPref = getSharedPreferences("database",PREFERENCE_MODE_PRIVATE);
        sharedEdit = sharedPref.edit();
        sharedEdit.putString("broker", "test.mosquitto.org");
        sharedEdit.putString("topic", "/hello/hello");
        sharedEdit.commit();

        Intent svc = new Intent(this, MQTTService.class);
        startService(svc);
    }

    /** Called when the user clicks the Log in button */
    public void LogIn (View view) {
        Intent intent = new Intent(this, IdleActivity.class);
        AutoCompleteTextView usernameField = (AutoCompleteTextView) findViewById(R.id.username);
        String username = usernameField.getText().toString();
        sharedPref = getSharedPreferences("database",PREFERENCE_MODE_PRIVATE);
        sharedEdit = sharedPref.edit();
        sharedEdit.putString("USERNAME", username);
        sharedEdit.putBoolean("LOGGED", true);
        sharedEdit.commit();
        startActivity(intent);
        finish();
    }
}
