package com.senception.contextualmanager.physical_usage;

import android.app.ActivityManager;
import android.content.Context;

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
public class ContextualManagerMemory {

    /**
     * Function that calculates the memory usage.
     * @param context the context
     * @return the memory usage in percentage.
     */
    public static int getCurrentRam(Context context){
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        int memPercentage= (int) ((mi.availMem / (double)mi.totalMem * 100.0) + 0.5);
        return memPercentage;
    }
}
