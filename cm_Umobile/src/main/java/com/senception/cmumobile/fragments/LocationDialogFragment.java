package com.senception.cmumobile.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

import com.senception.cmumobile.R;

/**
 * Created by Senception on 29/09/2017.
 */

public class LocationDialogFragment extends android.app.DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(getString(R.string.location));
        dialog.setMessage(getString(R.string.location_msg));
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        AlertDialog alertDialog = dialog.create();
        alertDialog.setCanceledOnTouchOutside(false);
        return alertDialog;
    }

    public static LocationDialogFragment newInstance(String title, String msg){
        LocationDialogFragment alertDialog = new LocationDialogFragment();
        /*Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message", msg);
        alertDialog.setArguments(args);*/
        return alertDialog;
    }
}
