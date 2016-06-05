package pbl7roboterapia.pbl7;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannedString;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class IdleActivity extends AppCompatActivity {

    /**SharedPreference is the most compact way to save variables on device's memory */
    private SharedPreferences sharedPref;
    private static final int PREFERENCE_MODE_PRIVATE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idle);

        /** Opening SharedPreferences for future use */
        sharedPref = getSharedPreferences("database",PREFERENCE_MODE_PRIVATE);

        /** Updating the textView to contain the USERNAME string variable */
        TextView textView = (TextView) findViewById(R.id.welcomeText);
        String login = sharedPref.getString("USERNAME", "ERROR");
        String text = getResources().getString(R.string.prompt_welcome)+login+"! "+getResources().getString(R.string.prompt_all_ok);
        textView.setText(text);
        textView.setTextSize(getResources().getDimension(R.dimen.text_size));
    }

    /** Enabling an overlay menu */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /** Handling overlay menu item press */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.settings:
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            break;
            case R.id.shutdown:
                ServiceCheck serviceCheck = new ServiceCheck(this);

                if (serviceCheck.isMyServiceRunning(AppService.class) == true) {
                    Intent service = new Intent(this, AppService.class);
                    stopService(service);
                }else{
                    Intent service = new Intent(this, AppService.class);
                    startService(service);
                }

            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        MenuItem toggler = menu.findItem(R.id.shutdown);
        ServiceCheck serviceCheck = new ServiceCheck(this);
        if (serviceCheck.isMyServiceRunning(AppService.class) == true) {
            toggler.setTitle(getResources().getString(R.string.menu_disconnect_text));
        }else{
            toggler.setTitle(getResources().getString(R.string.menu_connect_text));
        }
        return super.onPrepareOptionsMenu(menu);
    }

    /** Cycling to the next state, i.e. alarm */
    public void Cycle (View view){
        Intent intent = new Intent(this, AlarmActivity.class);
        startActivity(intent);
        finish();
    }
}
