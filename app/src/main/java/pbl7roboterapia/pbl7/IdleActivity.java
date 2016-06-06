package pbl7roboterapia.pbl7;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class IdleActivity extends AppCompatActivity {

    /**SharedPreference is the most compact way to save variables on device's memory */
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor sharedEdit;
    private static final int PREFERENCE_MODE_PRIVATE = 0;

    /** Global variables used along the Activity */
    boolean serviceBounded;
    AppService mservice;


    //comment
    int it = 0;

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

        Intent service = new Intent(this, AppService.class);
        ServiceCheck serviceCheck = new ServiceCheck(this);

        switch (item.getItemId()){
            case R.id.settings:
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            break;
            case R.id.reconnect:
                if (serviceCheck.isMyServiceRunning(AppService.class)) {
                    unbindService(mConnection);
                    stopService(service);
                }
                startService(service);
                bindService(service, mConnection, BIND_AUTO_CREATE);
            break;
            case R.id.shutdown:
                if (serviceCheck.isMyServiceRunning(AppService.class)) {
                    unbindService(mConnection);
                    serviceBounded = false;
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

    /** Unbidning from AppService */
    @Override
    protected void onStop() {
        super.onStop();
        if(serviceBounded) {
            unbindService(mConnection);
            serviceBounded = false;
        }
    };

    /** Cycling to the <NEXT STATE> & sending a message to MQTT broker*/
    public void Cycle (View view){

        sharedEdit = sharedPref.edit();
        sharedEdit.putBoolean("SENDER", true);
        sharedEdit.putString("STATE", "NEEDHELP");
        sharedEdit.apply();

        mservice.publishMessage(0);
        Intent intent = new Intent(this, AlarmActivity.class);
        startActivity(intent);
        finish();
    }
}
