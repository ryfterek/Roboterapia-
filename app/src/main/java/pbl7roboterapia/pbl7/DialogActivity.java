package pbl7roboterapia.pbl7;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class DialogActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);

        if(getIntent().getIntExtra(AppService.EXTRA_DIALOG_REASON, 0) == 1){
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder.setTitle(getResources().getString(R.string.no_connection_title))
                    .setMessage(getResources().getString(R.string.no_connection_body))
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent service = new Intent(getApplicationContext(), AppService.class);
                            stopService(service);
                            finishAffinity();
                        }
                    })
                    .setNegativeButton("NOT OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent service = new Intent(getApplicationContext(), AppService.class);
                            stopService(service);
                            finishAffinity();
                        }
                    });
            AlertDialog dialog = dialogBuilder.create();
            dialog.show();
        }
    }
}
