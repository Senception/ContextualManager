package com.senception.cmumobile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.WindowManager;

import com.senception.cmumobile.R;

/**
 * Created by Senception on 20/09/2017.
 */

public class GPSDialog extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AlertDialog.Builder alertBox = new AlertDialog.Builder(getApplicationContext());

        alertBox.setTitle(getString(R.string.fused));
        alertBox.setMessage(getString(R.string.fusedd));
        alertBox.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        Dialog alertDialog = alertBox.create();
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }
}