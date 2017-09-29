package com.senception.cmumobile.ResourceUsage;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Process;
import android.provider.Settings;

import java.util.ArrayList;

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
 * @file Contains ResourceUsageHandler. This class handles the gotten
 * resources getting its usage.
 *
 */
public class ResourceUsageHandler {

    public static void start(Context context) {

        UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);



        //Check battery usage
        checkEnergyUsage();
    }

    private static void checkEnergyUsage() {
        ArrayList usagePerHour = new ArrayList();

        PhysicalResourceUsage energy = new PhysicalResourceUsage(PhysicalResourceType.ENERGY, usagePerHour);

        //Next Step: Check every hour for the battery usage and save it in the array

    }

}
