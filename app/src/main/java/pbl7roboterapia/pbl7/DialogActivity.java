package pbl7roboterapia.pbl7;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;

public class DialogActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);

        if(getIntent().getIntExtra(AppService.EXTRA_DIALOG_REASON, 0) == 1){
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.Dialogs);
            dialogBuilder.setCancelable(false);
            dialogBuilder.setTitle(getResources().getString(R.string.no_connection_title));
            dialogBuilder.setMessage(getResources().getString(R.string.no_connection_body));
            dialogBuilder.setPositiveButton(getResources().getString(R.string.no_connection_positive), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    startActivityForResult(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS), 0);
                    Intent service = new Intent(getApplicationContext(), AppService.class);
                    stopService(service);
                    finishAffinity();
                }
            });
            dialogBuilder.setNegativeButton(getResources().getString(R.string.no_connection_negative), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent service = new Intent(getApplicationContext(), AppService.class);
                    stopService(service);
                    finishAffinity();
                }
            });
            AlertDialog dialog = dialogBuilder.create();
            dialog.show();
        }else if (getIntent().getIntExtra(AppService.EXTRA_DIALOG_REASON, 0) == 2){
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.Dialogs);
            dialogBuilder.setCancelable(false);
            dialogBuilder.setTitle(getResources().getString(R.string.fail_title));
            dialogBuilder.setMessage(getResources().getString(R.string.fail_body));
            dialogBuilder.setPositiveButton(getResources().getString(R.string.fail_positive), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent service = new Intent(getApplicationContext(), AppService.class);
                    stopService(service);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            dialogBuilder.setNegativeButton(getResources().getString(R.string.fail_negative), new DialogInterface.OnClickListener() {
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
