package pbl7roboterapia.pbl7;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class DialogActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);

        Toast.makeText(getApplicationContext(), "TEST", Toast.LENGTH_SHORT).show();

        if(getIntent().getIntExtra(AppService.EXTRA_DIALOG_REASON, 0) == 1){
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder.setTitle(getResources().getString(R.string.no_connection_title))
                    .setMessage(getResources().getString(R.string.no_connection_body))
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK button
                        }
                    })
                    .setNegativeButton("NOT OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            AlertDialog dialog = dialogBuilder.create();
            dialog.show();
        }
    }
}
