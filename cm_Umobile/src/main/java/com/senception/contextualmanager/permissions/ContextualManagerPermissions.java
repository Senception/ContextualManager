package com.senception.contextualmanager.permissions;

import android.app.AppOpsManager;
import android.content.Context;
import android.os.Build;
import android.os.Process;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.Toast;

import static android.app.AppOpsManager.MODE_ALLOWED;
import static android.app.AppOpsManager.OPSTR_GET_USAGE_STATS;

/**
 * Copyright (C) 2016 Senception Lda
 * Author(s): Igor dos Santos - degomosIgor@sen-ception.com *
 * 			  José Soares - jose.soares@senception.com
 * Update to Contextual Manager 2017
 * @author Igor dos Santos
 * @author José Soares
 * @version 0.1
 *
 * @file Contains ContextualManagerPermissions. This class handles the necessary  requests
 * to get both of usage stats and location permissions.
 *
 */
public class ContextualManagerPermissions {

    /**
     * Function usageStatsPermission.
     * Checks if usage stats service is enabled.
     * @param context the context.
     * @return true if there is permission to use usage stats, false otherwise.
     */
    public static boolean usageStatsPermission(Context context){
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = 0;
        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.KITKAT) {
            mode = appOps.checkOpNoThrow(OPSTR_GET_USAGE_STATS, Process.myUid(), context.getPackageName());
        }
        else
            Toast.makeText(context, "This android system doesn't allow to get usage stats.", Toast.LENGTH_SHORT).show();
        return mode == MODE_ALLOWED;
    }

    /**
     * Function isLocationEnabled
     * Check if location service is Enabled
     * @param context application context
     * @return true if location service is enabled and false if location service is disabled
     */
    @SuppressWarnings("deprecation")
    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }
}
