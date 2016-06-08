package pbl7roboterapia.pbl7;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class NeededActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_needed);

        /** Opening SharedPreferences for future use */
        sharedPref = getSharedPreferences("database",PREFERENCE_MODE_PRIVATE);

        /** Creating vibrator <GIGGLE> */
        vibe = (Vibrator) getSystemService(VIBRATOR_SERVICE) ;

        /** Updating the textView to contain the USERNAME string variable */
        TextView textView = (TextView) findViewById(R.id.neededText);
        String sigOth = sharedPref.getString("SIGNIFICANTOTHER", "ERROR");
        String text = getResources().getString(R.string.prompt_warning)+sigOth+getResources().getString(R.string.prompt_help);
        textView.setText(text);
        textView.setTextSize(getResources().getDimension(R.dimen.text_size));

        findViewById(R.id.neededButton).setOnClickListener(clickListener);
        findViewById(R.id.neededButton).setOnLongClickListener(longClickListener);
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

    View.OnClickListener clickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_hold), Toast.LENGTH_SHORT).show();
        }
    };

    View.OnLongClickListener longClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            vibe.vibrate(50);
            sharedEdit = sharedPref.edit();
            sharedEdit.putString("STATE", States.STATES.VOLUNTEER.name());
            sharedEdit.putBoolean("VOLUNTEER", true);
            sharedEdit.apply();

            mservice.publishMessage(2);
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);

            finish();
            return false;
        }
    };

/*    *//** Cycling to the <NEXT STATE> & sending a message to MQTT broker*//*
    public void Cycle (View view){

        sharedEdit = sharedPref.edit();
        sharedEdit.putString("STATE", States.STATES.VOLUNTEER.name());
        sharedEdit.putBoolean("VOLUNTEER", true);
        sharedEdit.apply();

        mservice.publishMessage(2);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

        finish();
    }*/
}
