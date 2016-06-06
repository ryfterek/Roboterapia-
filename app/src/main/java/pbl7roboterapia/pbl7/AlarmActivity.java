package pbl7roboterapia.pbl7;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class AlarmActivity extends AppCompatActivity {

    /**SharedPreference is the most compact way to save variables on device's memory */
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor sharedEdit;
    private static final int PREFERENCE_MODE_PRIVATE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        /** Opening SharedPreferences for future use */
        sharedPref = getSharedPreferences("database",PREFERENCE_MODE_PRIVATE);

        /** Updating the textView to contain the USERNAME string variable */
        TextView textView = (TextView) findViewById(R.id.alarmText);
        String login = sharedPref.getString("USERNAME", "ERROR");
        String text = (getResources().getString(R.string.prompt_calm)+login+"! "+getResources().getString(R.string.prompt_incoming));
        textView.setText(text);
        textView.setTextSize(getResources().getDimension(R.dimen.text_size));
    }

    /** Cycling to the next state, i.e. alarm */
    public void Cycle (View view){
        sharedEdit = sharedPref.edit();
        sharedEdit.putBoolean("SENDER", false);
        sharedEdit.putString("STATE", "NEEDHELP");
        sharedEdit.apply();

        Intent intent = new Intent(this, IdleActivity.class);
        startActivity(intent);
        finish();
    }
}
