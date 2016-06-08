package pbl7roboterapia.pbl7;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class VolunteerActivity extends AppCompatActivity {

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor sharedEdit;
    private static final int PREFERENCE_MODE_PRIVATE = 0;

    /** Global variables used along the Activity */
    boolean serviceBounded;
    AppService mservice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer);

        /** Opening SharedPreferences for future use */
        sharedPref = getSharedPreferences("database",PREFERENCE_MODE_PRIVATE);

        /** Updating the textView to contain the USERNAME string variable */
        TextView textView = (TextView) findViewById(R.id.volunteerText);
        String sigOth = sharedPref.getString("SIGNIFICANTOTHER", "ERROR");
        String text = getResources().getString(R.string.prompt_thank)+sigOth;
        textView.setText(text);
        textView.setTextSize(getResources().getDimension(R.dimen.text_size));
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
    };
}
