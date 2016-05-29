package pbl7roboterapia.pbl7;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class AlarmActivity extends AppCompatActivity {

    private SharedPreferences sharedPref;
    private static final int PREFERENCE_MODE_PRIVATE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        TextView text = (TextView) findViewById(R.id.alarmText);
        text.setTextSize(20);
        sharedPref = getSharedPreferences("database",PREFERENCE_MODE_PRIVATE);
        text.setText("Oh no! "+sharedPref.getString("USERNAME", "ERROR")+", there is an alarm!");
    }

    public void Cycle (View view){
        Intent intent = new Intent(this, IdleActivity.class);
        startActivity(intent);
        finish();
    }
}
