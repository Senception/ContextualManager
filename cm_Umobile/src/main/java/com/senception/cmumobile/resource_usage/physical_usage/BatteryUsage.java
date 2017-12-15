package com.senception.cmumobile.resource_usage.physical_usage;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

/**
 * Copyright (C) 2016 Senception Lda
 * Author(s): Igor dos Santos - degomosIgor@sen-ception.com *
 * 			  José Soares - jose.soares@senception.com
 * Update to Contextual Manager 2017
 * @author Igor dos Santos
 * @author José Soares
 * @version 0.1
 *
 * @file Class to get the battery usage.
 *
 */
public class BatteryUsage {


    public static int getEnergyLevel(Context context) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, intentFilter);

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        //Toast.makeText(context, "LEVEl " + level, Toast.LENGTH_SHORT).show();
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        //Toast.makeText(context, "SCALE " + scale, Toast.LENGTH_SHORT).show();

        int batteryPct = level / scale;
        //Toast.makeText(context, "NIVEL DA BATERIA " + batteryPct, Toast.LENGTH_SHORT).show();

        return level;
    }
}
