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

public class AlarmActivity extends AppCompatActivity {

    /**SharedPreference is the most compact way to save variables on device's memory */
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor sharedEdit;
    private static final int PREFERENCE_MODE_PRIVATE = 0;

    boolean serviceBounded;
    AppService mservice;
    Vibrator vibe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        /** Opening SharedPreferences for future use */
        sharedPref = getSharedPreferences("database",PREFERENCE_MODE_PRIVATE);

        /** Creating vibrator <GIGGLE> */
        vibe = (Vibrator) getSystemService(VIBRATOR_SERVICE) ;

        /** Updating the textView to contain the USERNAME string variable */
        TextView mainTextView = (TextView) findViewById(R.id.mainAlarmText);
        String text = (getResources().getString(R.string.prompt_calm)+sharedPref.getString("USERNAME", "ERROR")+"! "+getResources().getString(R.string.prompt_incoming));
        mainTextView.setText(text);
        mainTextView.setTextSize(getResources().getDimension(R.dimen.text_size));


        /** Deciding how to fill the strings below the button*/
        TextView volunteerOneText = (TextView) findViewById(R.id.volunteerOneText);
        TextView volunteerTwoText = (TextView) findViewById(R.id.volunteerTwoText);
        String textOne, textTwo;

        if (sharedPref.getString("VOLUNTEER1", "NULL").equals("NULL")){
            textOne = getResources().getString(R.string.prompt_waiting);
        }else{
            textOne = (sharedPref.getString("VOLUNTEER1", "NULL")+getResources().getString(R.string.prompt_volunteer));
        }
        volunteerOneText.setText(textOne);
        volunteerOneText.setTextSize(getResources().getDimension(R.dimen.text_size));

        if (sharedPref.getString("VOLUNTEER2", "NULL").equals("NULL")){
            textTwo = "";
        }else{
            textTwo = (sharedPref.getString("VOLUNTEER2", "NULL")+getResources().getString(R.string.prompt_volunteer));
        }
        volunteerTwoText.setText(textTwo);
        volunteerTwoText.setTextSize(getResources().getDimension(R.dimen.text_size));

        findViewById(R.id.alarmButton).setOnLongClickListener(listener);

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
    }

    View.OnLongClickListener listener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            vibe.vibrate(50);
            sharedEdit = sharedPref.edit();
            sharedEdit.putBoolean("SENDER", false);
            sharedEdit.putString("STATE", States.STATES.IDLE.name());
            sharedEdit.putString("VOLUNTEER1", "NULL");
            if (!sharedPref.getString("VOLUNTEER2", "NULL").equals("NULL")){
                mservice.publishMessage(3);
            }
            sharedEdit.putString("VOLUNTEER2", "NULL");
            sharedEdit.apply();

            mservice.publishMessage(1);
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
            return false;
        }
    };

/*    *//** Cycling to the next state, i.e. alarm *//*
    public void Cycle (View view){
        sharedEdit = sharedPref.edit();
        sharedEdit.putBoolean("SENDER", false);
        sharedEdit.putString("STATE", States.STATES.IDLE.name());
        sharedEdit.putString("VOLUNTEER1", "NULL");
        sharedEdit.putString("VOLUNTEER2", "NULL");
        sharedEdit.apply();

        mservice.publishMessage(1);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }*/
}
