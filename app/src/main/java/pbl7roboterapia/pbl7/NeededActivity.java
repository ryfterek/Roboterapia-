package pbl7roboterapia.pbl7;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class NeededActivity extends AppCompatActivity {

    private SharedPreferences sharedPref;
    private static final int PREFERENCE_MODE_PRIVATE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_needed);

        /** Opening SharedPreferences for future use */
        sharedPref = getSharedPreferences("database",PREFERENCE_MODE_PRIVATE);

        /** Updating the textView to contain the USERNAME string variable */
        TextView textView = (TextView) findViewById(R.id.neededText);
        String sigOth = sharedPref.getString("SIGNIFICANTOTHER", "ERROR");
        String text = getResources().getString(R.string.prompt_warning)+sigOth+getResources().getString(R.string.prompt_help);
        textView.setText(text);
        textView.setTextSize(getResources().getDimension(R.dimen.text_size));
    }
}
