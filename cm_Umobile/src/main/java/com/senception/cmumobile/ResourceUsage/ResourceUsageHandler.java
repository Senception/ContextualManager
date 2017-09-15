package com.senception.cmumobile.ResourceUsage;

import android.app.AppOpsManager;
import android.content.Context;

import java.util.ArrayList;

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

    public static void start() {

        //Check battery usage
        checkEnergyUsage();

    }

    private static void checkEnergyUsage() {
        ArrayList usagePerHour = new ArrayList();

        PhysicalResourceUsage energy = new PhysicalResourceUsage(PhysicalResourceType.ENERGY, usagePerHour);

        //Next Step: Check every hour for the battery usage and save it in the array

    }

    private boolean checkForPermission(Context context){
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(OPSTR_GET_USAGE_STATS, Process.myUid(), context.getPackageName());
        return mode == MODE_ALLOWED;
    }
}
