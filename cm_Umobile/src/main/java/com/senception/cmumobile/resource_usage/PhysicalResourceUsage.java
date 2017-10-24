package com.senception.cmumobile.resource_usage;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

/**
 * Copyright (C) 2016 Senception Lda
 * Author(s): Igor dos Santos - degomosIgor@sen-ception.com *
 * 			  José Soares - jose.soares@senception.com
 * Update to Contextual Manager 2017
 * @author Igor dos Santos
 * @author José Soares
 * @version 0.1
 *
 * @file Contains PhysicalResourceUsage. This class represents an object that will
 * save the type of a physical resource being used and its usage per hour.
 *
 */
public class PhysicalResourceUsage {
    private PhysicalResourceType resourceType;

    /** USANDO ARRAYLIST*/
    private ArrayList<Integer> usagePerHour;

    /** USANDO HASHMAP*/
    //private HashMap usagePerHour;

    private final String TAG = "PHYSICAL RESOURCE";

    public PhysicalResourceUsage(PhysicalResourceType resourceType) {
        this.resourceType = resourceType;

        /** USANDO ARRAYLIST*/
        this.usagePerHour = new ArrayList<>(47);
        for (int i = 0; i <= 47; i++){
            usagePerHour.add(i, null);
        }

        /** USANDO HASHMAP*/
        //this.usagePerHour = new HashMap <Integer, Integer>(23);
    }

    public PhysicalResourceType getResourceType() {
        return resourceType;
    }

    public ArrayList<Integer> getUsagePerHour() {
        return usagePerHour;
    }

    public void CaptureUsage(Context context) {
        switch(resourceType){
            case ENERGY:
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
                Intent batteryStatus = context.registerReceiver(null, intentFilter);

                int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                //Toast.makeText(context, "LEVEl " + level, Toast.LENGTH_SHORT).show();
                int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                //Toast.makeText(context, "SCALE " + scale, Toast.LENGTH_SHORT).show();

                int batteryPct = level / scale;
                //Toast.makeText(context, "NIVEL DA BATERIA " + batteryPct, Toast.LENGTH_SHORT).show();

                //get current time
                Calendar currentTime = Calendar.getInstance();
                //get current hour
                //int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
                int currentSecond = currentTime.get(Calendar.SECOND);

                /*USANDO ARRAYLIST*/
                usagePerHour.set(currentSecond, level);

                /** USANDO HASHMAP*/
                //usagePerHour.put(currentHour-1, level-33);
                //usagePerHour.put(currentHour, level);
                //for ( Object v : usagePerHour.values()){
                //    Log.d(TAG, "HASHMAP " + (int) v );
                //}
            case STORAGE:
                break;
            case CPU:
                break;
            case MEMORY:
                break;
            default:
                break;
        }
    }

    public void addToDataBase() {
        switch(resourceType){
            case ENERGY:
                break;
            case STORAGE:
                break;
            case CPU:
                break;
            case MEMORY:
                break;
            default:
                break;
        }
    }

}
