package com.senception.cmumobile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Process;

import static android.app.AppOpsManager.MODE_ALLOWED;
import static android.app.AppOpsManager.OPSTR_GET_USAGE_STATS;

/**
 * Created by Senception on 21/09/2017.
 */

public class Permissions {

    //@TargetApi(19)
    @SuppressLint("NewApi")
    public static boolean usageStatsPermission(Context context){
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(OPSTR_GET_USAGE_STATS, Process.myUid(), context.getPackageName());
        return mode == MODE_ALLOWED;
    }
}
