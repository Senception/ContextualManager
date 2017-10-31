package com.senception.cmumobile.resource_usage.physical;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * Copyright (C) 2016 Senception Lda
 * Author(s): Igor dos Santos - degomosIgor@sen-ception.com *
 * 			  José Soares - jose.soares@senception.com
 * Update to Contextual Manager 2017
 * @author Igor dos Santos
 * @author José Soares
 * @version 0.1
 *
 * @file Class to get the memory usage.
 *
 */
public class MemoryUsage {

    public static int getCurrentRam(Context context){
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);

        //1024 bytes = 1Kbyte , 1024 Kbytes = 1 Mbyte -> 1024*1024 = 1048576 = 0x100000 Mbytes
        //double availableMegs = mi.availMem / 0x100000L;

        //16Log.d("RESOURCE", "PERCENTAGEM " + mi.availMem / (double)mi.totalMem * 100.0);

        Log.d("RESOURCE", String.valueOf(mi.availMem/ 0x100000L) + "mb");
        Log.d("RESOURCE", String.valueOf(mi.totalMem/ 0x100000L) + "mb");

        //Percentage can be calculated for API 16+
        int percentAvail = (int) ((mi.availMem / (double)mi.totalMem * 100.0) + 0.5);
        return  percentAvail;
    }
}
