package com.senception.cmumobile.resource_usage;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

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
public class ResourceUsageHandler extends BroadcastReceiver{

    private static final String TAG = "RESOURCE USAGE HANDLER";

    @Override
    public void onReceive(Context context, Intent intent) {
        //Map<String, UsageStats> statsMap = usm.queryAndAggregateUsageStats(start, end);

        //Check battery usage
        checkEnergyUsage(context);
        //Check cpu usage
        //checkCPUUsage(context);
        //Check device storage
        //checkStorage(context);
        //Check device memory
        //checkMemory(context);
        //Check apps usage
        //checkAppsUsage(context);
    }

    private static void checkEnergyUsage(Context context) {

        //Log.d(TAG, "ENTROU NO CHECK ENERGY");

        PhysicalResourceUsage energy = new PhysicalResourceUsage(PhysicalResourceType.ENERGY);

        //1) Capture usage once
        energy.CaptureUsage(context);

        /** USANDO ARRAYLIST*/
        ArrayList<Integer> array = energy.getUsagePerHour();

        /** USANDO HASHMAP*/
        //HashMap<Integer, Integer> array = energy.getUsagePerHour();

        Log.d(TAG, "Array at" + new GregorianCalendar().getTime() + ":" + array.toString());

        //2) Save on the database
        energy.addToDataBase();
    }

    private static void checkCPUUsage(Context context) {
        PhysicalResourceUsage cpu = new PhysicalResourceUsage(PhysicalResourceType.CPU);

        //1) Capture usage once
        cpu.CaptureUsage(context);



        //2) Save on the database
        cpu.addToDataBase();
    }

    private static void checkStorage(Context context) {
        PhysicalResourceUsage storage = new PhysicalResourceUsage(PhysicalResourceType.STORAGE);

        //1) Capture usage
        storage.CaptureUsage(context);
        //2) Get the array
        storage.getUsagePerHour();
        //3) Put it on the database
        storage.addToDataBase();
    }

    private static void checkMemory(Context context) {
        PhysicalResourceUsage memory = new PhysicalResourceUsage(PhysicalResourceType.MEMORY);

        //1) Capture usage
        memory.CaptureUsage(context);
        //2) Get the array
        memory.getUsagePerHour();
        //3) Put it on the database
        memory.addToDataBase();

    }

    @SuppressLint("NewApi")
    private static void checkAppsUsage(Context context) {
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);

        //begin date
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.DAY_OF_MONTH, -8);
        printCalendar(startDate);
        long start = startDate.getTimeInMillis();

        //end date
        Calendar endDate = Calendar.getInstance();
        printCalendar(endDate);
        long end = endDate.getTimeInMillis();

        List<UsageStats> stats = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, start, end);


        //so far it only prints the apps that ran on beetween the given date
        for (UsageStats stat: stats) {
            if(!usm.isAppInactive(stat.getPackageName())){
                DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date netDate = (new Date(stat.getFirstTimeStamp()));
                //Log.d(TAG, "" + sdf.format(netDate));
            }
        }
    }


    public static void printCalendar(Calendar calendar){
        SimpleDateFormat format = new SimpleDateFormat("EEEE, d'/'MM'/'yyyy 'at' h:mm a");
        String currentDate = format.format(calendar.getTime());
        Log.d("CALENDAR", currentDate);
    }


}
