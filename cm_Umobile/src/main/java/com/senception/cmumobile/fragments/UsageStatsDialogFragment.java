package com.senception.cmumobile.fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

import com.senception.cmumobile.R;

/**
 * Copyright (C) 2016 Senception Lda
 * Author(s): Igor dos Santos - degomosIgor@sen-ception.com *
 * 			  José Soares - jose.soares@senception.com
 * Update to Contextual Manager 2017
 * @author Igor dos Santos
 * @author José Soares
 * @version 0.1
 *
 * @file Contains UsageStatsDialogFramgent. This class deals with the
 * dialog to show the user to ask it for permission to use the usage stats.
 */
public class UsageStatsDialogFragment  extends android.app.DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(getString(R.string.usage_stats));
        dialog.setMessage(getString(R.string.usage_stats_msg));
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        AlertDialog alertDialog = dialog.create();
        return alertDialog;
    }

    public static UsageStatsDialogFragment newInstance(String title, String msg){
        UsageStatsDialogFragment alertDialog = new UsageStatsDialogFragment();
        /*Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message", msg);
        alertDialog.setArguments(args);*/
        return alertDialog;
    }
}
