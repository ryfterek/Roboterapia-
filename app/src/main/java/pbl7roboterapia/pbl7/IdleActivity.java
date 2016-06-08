package pbl7roboterapia.pbl7;

import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class IdleActivity extends AppCompatActivity {

    /**SharedPreference is the most compact way to save variables on device's memory */
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor sharedEdit;
    private static final int PREFERENCE_MODE_PRIVATE = 0;

    /** Global variables used along the Activity */
    boolean serviceBounded;
    AppService mservice;
    Vibrator vibe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idle);

        /** Opening SharedPreferences for future use */
        sharedPref = getSharedPreferences("database",PREFERENCE_MODE_PRIVATE);
        sharedEdit = sharedPref.edit();
        sharedEdit.putBoolean("SENDER", false);
        sharedEdit.putBoolean("VOLUNTEER", false);

        /** Creating vibrator <GIGGLE> */
        vibe = (Vibrator) getSystemService(VIBRATOR_SERVICE) ;

        /** Updating the textView to contain the USERNAME string variable */
        TextView textView = (TextView) findViewById(R.id.welcomeText);
        String login = sharedPref.getString("USERNAME", "ERROR");
        String text = getResources().getString(R.string.prompt_welcome)+login+"! "+getResources().getString(R.string.prompt_all_ok);
        textView.setText(text);
        textView.setTextSize(getResources().getDimension(R.dimen.text_size));

        /** Setting up button press handling */
        findViewById(R.id.idleButton).setOnClickListener(clickListener);
        findViewById(R.id.idleButton).setOnLongClickListener(longClickListener);
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

        Intent service = new Intent(this, AppService.class);
        ServiceCheck serviceCheck = new ServiceCheck(this);
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        switch (item.getItemId()){
            case R.id.settings:
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            break;
            case R.id.reconnect:
                if (serviceCheck.isMyServiceRunning(AppService.class)) {
                    unbindService(mConnection);
                    sharedEdit.putBoolean("TURNOFF", true);
                    sharedEdit.apply();
                    stopService(service);
                }
                nm.cancel(7002);
                startService(service);
                bindService(service, mConnection, BIND_AUTO_CREATE);
            break;
            case R.id.shutdown:
                if (serviceCheck.isMyServiceRunning(AppService.class)) {
                    nm.cancel(7002);
                    unbindService(mConnection);
                    serviceBounded = false;
                    sharedEdit.putBoolean("TURNOFF", true);
                    sharedEdit.apply();
                    stopService(service);
                }
                finish();
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    /** Binding with AppService */
    @Override
    protected void onStart() {
        super.onStart();
        Intent mIntent = new Intent(this, AppService.class);
        bindService(mIntent, mConnection, BIND_AUTO_CREATE);
    };

    /** Binding with AppService */
    ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceDisconnected(ComponentName name) {
            //Toast.makeText(IdleActivity.this, "Service is disconnected", Toast.LENGTH_SHORT).show();
            serviceBounded = false;
        }
        public void onServiceConnected(ComponentName name, IBinder service) {
            //Toast.makeText(IdleActivity.this, "Service is connected", Toast.LENGTH_SHORT).show();
            serviceBounded = true;
            AppService.LocalBinder mLocalBinder = (AppService.LocalBinder)service;
            mservice = mLocalBinder.getServerInstance();
        }
    };

    /** Unbinding from AppService */
    @Override
    protected void onStop() {
        super.onStop();
        if(serviceBounded) {
            unbindService(mConnection);
            serviceBounded = false;
        }
    }

    /** Handling button tap */
    View.OnClickListener clickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_hold), Toast.LENGTH_SHORT).show();
        }
    };

    /** Handling button press */
    View.OnLongClickListener longClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            vibe.vibrate(50);
            sharedEdit = sharedPref.edit();
            sharedEdit.putBoolean("SENDER", true);
            sharedEdit.putString("STATE", States.STATES.ALARM.name());
            sharedEdit.apply();

            mservice.publishMessage(0);
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);

            finish();
            return true;
        }
    };
}
