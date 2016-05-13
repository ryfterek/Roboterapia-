package pbl7roboterapia.pbl7;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;

public class StartActivity extends AppCompatActivity {

    public final static String USERNAME = "pbl7roboterapia.pbl7.USERNAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

    /** Called when the user clicks the Log in button */
    public void LogIn (View view) {
        Intent intent = new Intent(this, IdleActivity.class);
        AutoCompleteTextView usernameField = (AutoCompleteTextView) findViewById(R.id.username);
        String username = usernameField.getText().toString();
        intent.putExtra(USERNAME, username);
        startActivity(intent);
    }
}
