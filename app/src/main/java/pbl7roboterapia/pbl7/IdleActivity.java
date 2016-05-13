package pbl7roboterapia.pbl7;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class IdleActivity extends AppCompatActivity {

    Intent startIntent = null;
    String username = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idle);

        startIntent = getIntent();
        if (startIntent != null) {
            username = startIntent.getStringExtra(StartActivity.USERNAME);
        }else{
            username = "ERROR";
        }

        TextView textView = new TextView(this);
        textView.setTextSize(40);
        String testText = R.string.test_prompt+username;
        textView.setText(testText);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.content);
        layout.addView(textView);
    }


}
